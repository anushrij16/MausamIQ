import type { HourlyEntry, LangCode, WeatherData } from './types';

// ---------- small time helpers (string-based, avoids timezone bugs) ----------

export function hourOf(iso: string): number {
  const t = iso.split('T')[1] ?? '00:00';
  return parseInt(t.slice(0, 2), 10);
}

export function formatHourWord(hour: number, lang: LangCode): string {
  const h = ((hour % 24) + 24) % 24;
  const h12 = h % 12 === 0 ? 12 : h % 12;
  const isTa = lang.startsWith('ta');
  const isHi = lang.startsWith('hi');
  if (isHi) {
    const period = h < 5 ? 'रात' : h < 12 ? 'सुबह' : h < 17 ? 'दोपहर' : h < 20 ? 'शाम' : 'रात';
    return `${period} ${h12} बजे`;
  }
  if (isTa) {
    const period = h < 5 ? 'இரவு' : h < 12 ? 'காலை' : h < 17 ? 'மதியம்' : h < 19 ? 'மாலை' : 'இரவு';
    return `${period} ${h12} மணி`;
  }
  const ampm = h < 12 ? 'AM' : 'PM';
  return `${h12} ${ampm}`;
}

export function formatWindowWord(startHour: number, endHour: number, lang: LangCode): string {
  return `${formatHourWord(startHour, lang)} – ${formatHourWord(endHour, lang)}`;
}

// ---------- scoring ----------

export interface ActivityCriteria {
  idealTempMin: number;
  idealTempMax: number;
  hourRange?: [number, number];
  maxWind?: number;
  preferWind?: boolean;
}

function scoreHour(entry: HourlyEntry, c: ActivityCriteria): number {
  let score = 100;
  score -= entry.precipitationProbability * 1.5;
  if ([95, 96, 99].includes(entry.weatherCode)) score -= 60;
  else if ([61, 63, 65, 66, 67, 80, 81, 82].includes(entry.weatherCode)) score -= 20;
  if (entry.temperature < c.idealTempMin) score -= (c.idealTempMin - entry.temperature) * 3;
  if (entry.temperature > c.idealTempMax) score -= (entry.temperature - c.idealTempMax) * 4;
  if (c.maxWind && entry.windSpeed > c.maxWind) score -= (entry.windSpeed - c.maxWind) * 2;
  if (c.preferWind) score += Math.min(entry.windSpeed, 25) * 0.4;
  return score;
}

export interface WindowResult {
  startHour: number;
  endHour: number;
  avgRain: number;
  avgTemp: number;
  avgWind: number;
  hasStorm: boolean;
  avgScore: number;
}

/** Finds the best contiguous window (default 2h) in the hourly forecast for a given activity. */
export function bestWindow(hourly: HourlyEntry[], c: ActivityCriteria, windowSize = 2): WindowResult | null {
  const pool = c.hourRange
    ? hourly.filter((h) => {
        const hr = hourOf(h.time);
        return hr >= c.hourRange![0] && hr <= c.hourRange![1];
      })
    : hourly;
  if (pool.length < windowSize) return null;
  let best: { entries: HourlyEntry[]; avgScore: number } | null = null;
  for (let i = 0; i <= pool.length - windowSize; i++) {
    const slice = pool.slice(i, i + windowSize);
    const avg = slice.reduce((s, e) => s + scoreHour(e, c), 0) / slice.length;
    if (!best || avg > best.avgScore) best = { entries: slice, avgScore: avg };
  }
  if (!best) return null;
  const entries = best.entries;
  return {
    startHour: hourOf(entries[0].time),
    endHour: hourOf(entries[entries.length - 1].time) + 1,
    avgRain: Math.round(entries.reduce((s, e) => s + e.precipitationProbability, 0) / entries.length),
    avgTemp: Math.round(entries.reduce((s, e) => s + e.temperature, 0) / entries.length),
    avgWind: Math.round(entries.reduce((s, e) => s + e.windSpeed, 0) / entries.length),
    hasStorm: entries.some((e) => [95, 96, 99].includes(e.weatherCode)),
    avgScore: best.avgScore,
  };
}

export function soilMoistureBucket(value: number, lang: LangCode): string {
  const isTa = lang.startsWith('ta');
  const isHi = lang.startsWith('hi');
  if (value < 0.15) return isTa ? 'வறண்டது' : isHi ? 'सूखी' : 'dry';
  if (value < 0.30) return isTa ? 'போதுமானது' : isHi ? 'पर्याप्त' : 'adequate';
  return isTa ? 'ஈரமானது' : isHi ? 'गीली' : 'wet';
}

/** Finds the closest hourly entry to a given target hour within the forecast (today, or tomorrow if past). */
export function closestHour(hourly: HourlyEntry[], targetHour: number): HourlyEntry | null {
  if (!hourly.length) return null;
  let best = hourly[0];
  let bestDiff = Infinity;
  for (const e of hourly) {
    const diff = Math.abs(hourOf(e.time) - targetHour);
    if (diff < bestDiff) {
      bestDiff = diff;
      best = e;
    }
  }
  return best;
}

/** Parses a spoken/typed clock time like "5pm", "at 17:00", "9 am" into a 24h hour, or null. */
export function parseClockTime(question: string): number | null {
  const q = question.toLowerCase();
  const m = q.match(/\b(?:at\s+)?(\d{1,2})(?::(\d{2}))?\s*(am|pm)?\b/);
  if (!m) return null;
  let h = parseInt(m[1], 10);
  if (h > 23) return null;
  const meridiem = m[3];
  if (meridiem === 'pm' && h < 12) h += 12;
  if (meridiem === 'am' && h === 12) h = 0;
  if (!meridiem && h > 12) return null;
  return h;
}

export function isWarmClear(weather: WeatherData): boolean {
  const c = weather.current;
  return c.temperature >= 22 && c.temperature <= 34 && ![95, 96, 99, 61, 63, 65, 80, 81, 82].includes(c.weatherCode);
}

export function isRainingNow(weather: WeatherData): boolean {
  return weather.current.precipitation > 0.2 || [61, 63, 65, 66, 67, 80, 81, 82, 95, 96, 99].includes(weather.current.weatherCode);
}
