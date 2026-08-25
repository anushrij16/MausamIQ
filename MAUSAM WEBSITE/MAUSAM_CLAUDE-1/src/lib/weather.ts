import type { WeatherData, LocationResult } from './types';

// Open-Meteo — free, no API key required.
const FORECAST_URL = 'https://api.open-meteo.com/v1/forecast';
const GEOCODE_URL = 'https://geocoding-api.open-meteo.com/v1/search';
const REVERSE_URL = 'https://geocoding-api.open-meteo.com/v1/reverse';

export async function fetchWeather(lat: number, lng: number): Promise<WeatherData> {
  const params = new URLSearchParams({
    latitude: String(lat),
    longitude: String(lng),
    current:
      'temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m,is_day',
    hourly:
      'temperature_2m,precipitation_probability,precipitation,weather_code,wind_speed_10m,uv_index,soil_moisture_0_to_1cm',
    daily:
      'weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,precipitation_probability_max,wind_speed_10m_max,uv_index_max',
    timezone: 'auto',
    forecast_days: '7',
    wind_speed_unit: 'kmh',
  });

  const res = await fetch(`${FORECAST_URL}?${params.toString()}`);
  if (!res.ok) throw new Error(`Weather request failed (${res.status})`);
  const d = await res.json();

  const nowIdx = Math.max(
    0,
    d.hourly.time.findIndex((t: string) => new Date(t).getTime() >= new Date(d.current.time).getTime() - 3600_000)
  );

  return {
    timezone: d.timezone,
    current: {
      temperature: d.current.temperature_2m,
      apparentTemperature: d.current.apparent_temperature,
      humidity: d.current.relative_humidity_2m,
      precipitation: d.current.precipitation,
      weatherCode: d.current.weather_code,
      windSpeed: d.current.wind_speed_10m,
      isDay: d.current.is_day === 1,
      time: d.current.time,
    },
    hourly: d.hourly.time.slice(nowIdx, nowIdx + 24).map((t: string, i: number) => ({
      time: t,
      temperature: d.hourly.temperature_2m[nowIdx + i],
      precipitationProbability: d.hourly.precipitation_probability[nowIdx + i],
      precipitation: d.hourly.precipitation[nowIdx + i],
      weatherCode: d.hourly.weather_code[nowIdx + i],
      windSpeed: d.hourly.wind_speed_10m[nowIdx + i],
      uvIndex: d.hourly.uv_index?.[nowIdx + i],
      soilMoisture: d.hourly.soil_moisture_0_to_1cm?.[nowIdx + i],
    })),
    daily: d.daily.time.map((t: string, i: number) => ({
      date: t,
      weatherCode: d.daily.weather_code[i],
      tempMax: d.daily.temperature_2m_max[i],
      tempMin: d.daily.temperature_2m_min[i],
      precipitationSum: d.daily.precipitation_sum[i],
      precipitationProbabilityMax: d.daily.precipitation_probability_max[i],
      windSpeedMax: d.daily.wind_speed_10m_max[i],
      uvIndexMax: d.daily.uv_index_max?.[i],
    })),
  };
}

export async function searchLocations(query: string): Promise<LocationResult[]> {
  if (!query.trim()) return [];
  const params = new URLSearchParams({
    name: query,
    count: '6',
    language: 'en',
    format: 'json',
  });
  const res = await fetch(`${GEOCODE_URL}?${params.toString()}`);
  if (!res.ok) return [];
  const d = await res.json();
  if (!d.results) return [];
  return d.results.map((r: Record<string, unknown>) => ({
    name: r.name as string,
    latitude: r.latitude as number,
    longitude: r.longitude as number,
    country: r.country as string | undefined,
    admin1: r.admin1 as string | undefined,
  }));
}

export async function reverseGeocode(lat: number, lng: number): Promise<LocationResult | null> {
  const params = new URLSearchParams({
    latitude: String(lat),
    longitude: String(lng),
    language: 'en',
    format: 'json',
  });
  try {
    const res = await fetch(`${REVERSE_URL}?${params.toString()}`);
    if (!res.ok) return null;
    const d = await res.json();
    if (!d.results || d.results.length === 0) return null;
    const r = d.results[0];
    return {
      name: r.name,
      latitude: r.latitude,
      longitude: r.longitude,
      country: r.country,
      admin1: r.admin1,
    };
  } catch {
    return null;
  }
}

// Human-readable description of WMO weather codes.
export function weatherCodeLabel(code: number): string {
  const map: Record<number, string> = {
    0: 'Clear sky',
    1: 'Mainly clear',
    2: 'Partly cloudy',
    3: 'Overcast',
    45: 'Fog',
    48: 'Depositing rime fog',
    51: 'Light drizzle',
    53: 'Moderate drizzle',
    55: 'Dense drizzle',
    56: 'Light freezing drizzle',
    57: 'Dense freezing drizzle',
    61: 'Slight rain',
    63: 'Moderate rain',
    65: 'Heavy rain',
    66: 'Light freezing rain',
    67: 'Heavy freezing rain',
    71: 'Slight snow',
    73: 'Moderate snow',
    75: 'Heavy snow',
    77: 'Snow grains',
    80: 'Slight rain showers',
    81: 'Moderate rain showers',
    82: 'Violent rain showers',
    85: 'Slight snow showers',
    86: 'Heavy snow showers',
    95: 'Thunderstorm',
    96: 'Thunderstorm with slight hail',
    99: 'Thunderstorm with heavy hail',
  };
  return map[code] ?? 'Unknown';
}

export function weatherIcon(code: number, isDay = true): string {
  // emoji glyph
  if (code === 0) return isDay ? '☀️' : '🌙';
  if ([1, 2].includes(code)) return isDay ? '🌤️' : '☁️';
  if (code === 3) return '☁️';
  if ([45, 48].includes(code)) return '🌫️';
  if ([51, 53, 55, 56, 57].includes(code)) return '🌦️';
  if ([61, 63, 65, 66, 67, 80, 81, 82].includes(code)) return '🌧️';
  if ([71, 73, 75, 77, 85, 86].includes(code)) return '🌨️';
  if ([95, 96, 99].includes(code)) return '⛈️';
  return '🌡️';
}
