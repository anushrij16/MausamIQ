import type {
  WeatherData,
  Alert,
  Severity,
  WeatherEvent,
  ProfileType,
  LangCode,
} from './types';

interface Condition {
  event: WeatherEvent;
  severity: Severity;
  weatherCodes: number[];
  messageBuilder: (ctx: AlertContext) => { title: string; message: string };
}

interface AlertContext {
  profile: ProfileType;
  lang: LangCode;
  // temporal info for messages
  rainHours: number; // hours with >55% precip prob in next 24h
  rainStartHour: number | null; // hour index (0-23) when rain likely starts
  maxTemp: number;
  minTemp: number;
  maxWind: number;
  todayRainSum: number;
}

// ---- Message banks per language & profile ----

type MsgTuple = { title: string; message: string };

function rainMsg(ctx: AlertContext, severity: Severity): MsgTuple {
  const startHint =
    ctx.rainStartHour !== null
      ? timeWindow(ctx.rainStartHour, ctx.lang)
      : '';
  const L = ctx.lang;
  if (L.startsWith('hi')) {
    const farmer: MsgTuple = {
      title: 'भारी बारिश की चेतावनी',
      message: `${startHint ? startHint + ' ' : ''}कल भारी बारिश होने की संभावना है। आज फसल की कटाई सुरक्षित जगह रखें और कीटनाशक छिड़काव टालें। खाद तब तक न डालें जब तक मिट्टी सूखी न हो।`,
    };
    const student: MsgTuple = {
      title: 'भारी बारिश की चेतावनी',
      message: `${startHint ? startHint + ' ' : ''}भारी बारिश की संभावना है। निचले रास्तों से यात्रा टालें और छाता/रेनकोट साथ रखें।`,
    };
    const outdoor: MsgTuple = {
      title: 'भारी बारिश की चेतावनी',
      message: `${startHint ? startHint + ' ' : ''}खुले में काम टालें। बारिश रुकने पर ही बाहरी काम शुरू करें। पानी भरी जगह से दूर रहें।`,
    };
    const general: MsgTuple = {
      title: 'भारी बारिश की चेतावनी',
      message: `${startHint ? startHint + ' ' : ''}भारी बारिश की संभावना है। बाहर निकलते समय छाता साथ रखें और जलजमाव वाले रास्तों से बचें।`,
    };
    return pick(ctx.profile, { farmer, student, outdoor_worker: outdoor, general });
  }
  // English default
  const farmer: MsgTuple = {
    title: 'Heavy rainfall warning',
    message: `${startHint ? startHint + '. ' : ''}Heavy rain expected tomorrow. Move harvested crops to a sheltered spot and postpone pesticide spraying. Do not apply fertilizer until the soil dries.`,
  };
  const student: MsgTuple = {
    title: 'Heavy rainfall warning',
    message: `${startHint ? startHint + '. ' : ''}Heavy rain likely. Avoid travel through low-lying roads and carry an umbrella or raincoat.`,
  };
  const outdoor: MsgTuple = {
    title: 'Heavy rainfall warning',
    message: `${startHint ? startHint + '. ' : ''}Postpone outdoor work. Resume only after rain stops and stay away from waterlogged areas.`,
  };
  const general: MsgTuple = {
    title: 'Heavy rainfall warning',
    message: `${startHint ? startHint + '. ' : ''}Heavy rain expected. Carry an umbrella and avoid waterlogged routes.`,
  };
  return pick(ctx.profile, { farmer, student, outdoor_worker: outdoor, general });
}

function heatMsg(ctx: AlertContext): MsgTuple {
  const L = ctx.lang;
  if (L.startsWith('hi')) {
    const farmer: MsgTuple = {
      title: 'गर्मी की चेतावनी',
      message: `कल तापमान ${Math.round(ctx.maxTemp)}°C तक जा सकता है। दोपहर 12 से 4 बजे खेत में काम टालें। फसलों को पानी देने का समय सुबह 6-8 बजे रखें। बहुत ज़्यादा तापमान से पौधों को नुकसान हो सकता है।`,
    };
    const student: MsgTuple = {
      title: 'गर्मी की चेतावनी',
      message: `कल बहुत गर्मी रहेगी (${Math.round(ctx.maxTemp)}°C)। दोपहर में बाहर घूमने से बचें, पानी पीते रहें और हल्के कपड़े पहनें।`,
    };
    const outdoor: MsgTuple = {
      title: 'गर्मी की चेतावनी',
      message: `तापमान ${Math.round(ctx.maxTemp)}°C तक पहुँच सकता है। दोपहर 12-4 बजे खुले में काम टालें, ढक्कन वाली जगह पर आराम करें और बार-बार पानी पिएँ।`,
    };
    const general: MsgTuple = {
      title: 'गर्मी की चेतावनी',
      message: `कल गर्मी बढ़ेगी (${Math.round(ctx.maxTemp)}°C)। पानी पीते रहें, हल्के कपड़े पहनें और दोपहर की धूप से बचें।`,
    };
    return pick(ctx.profile, { farmer, student, outdoor_worker: outdoor, general });
  }
  const farmer: MsgTuple = {
    title: 'Heat warning',
    message: `Temperatures may reach ${Math.round(ctx.maxTemp)}°C tomorrow. Avoid field work between 12 PM and 4 PM. Schedule irrigation for early morning (6–8 AM). High heat can stress crops.`,
  };
  const student: MsgTuple = {
    title: 'Heat warning',
    message: `It will be very hot tomorrow (${Math.round(ctx.maxTemp)}°C). Avoid outdoor play in the afternoon, stay hydrated and wear light clothing.`,
  };
  const outdoor: MsgTuple = {
    title: 'Heat warning',
    message: `Temperatures may reach ${Math.round(ctx.maxTemp)}°C. Postpone outdoor work from 12–4 PM, rest in shaded areas and drink water frequently.`,
  };
  const general: MsgTuple = {
    title: 'Heat warning',
    message: `Heat will rise tomorrow (${Math.round(ctx.maxTemp)}°C). Stay hydrated, wear light clothing and avoid the afternoon sun.`,
  };
  return pick(ctx.profile, { farmer, student, outdoor_worker: outdoor, general });
}

function stormMsg(ctx: AlertContext): MsgTuple {
  const L = ctx.lang;
  const hi = L.startsWith('hi');
  const farmer: MsgTuple = hi
    ? { title: 'आंधी/तूफान चेतावनी', message: 'आने वाले घंटों में आंधी-तूफान संभव है। खुले में रखे सामान सुरक्षित करें, बिजली के उपकरण बंद रखें और खेत में न जाएँ।' }
    : { title: 'Storm warning', message: 'A storm is likely in the coming hours. Secure loose outdoor items, unplug electrical equipment and avoid going to the field.' };
  const student: MsgTuple = hi
    ? { title: 'आंधी/तूफान चेतावनी', message: 'आंधी-तूफान संभव है। खिड़कियाँ बंद रखें, पेड़ों से दूर रहें और घर के अंदर रहें।' }
    : { title: 'Storm warning', message: 'A storm is likely. Keep windows closed, stay away from trees and remain indoors.' };
  const outdoor: MsgTuple = hi
    ? { title: 'आंधी/तूफान चेतावनी', message: 'आंधी-तूफान की संभावना है। ऊँचाई पर या खुले में काम तुरंत बंद करें और सुरक्षित आश्रय में जाएँ।' }
    : { title: 'Storm warning', message: 'A storm is approaching. Stop all work at height or outdoors immediately and move to a safe shelter.' };
  const general: MsgTuple = hi
    ? { title: 'आंधी/तूफान चेतावनी', message: 'आंधी-तूफान संभव है। खुले स्थानों से बचें और घर के अंदर रहें।' }
    : { title: 'Storm warning', message: 'A storm is likely. Avoid open areas and stay indoors.' };
  return pick(ctx.profile, { farmer, student, outdoor_worker: outdoor, general });
}

function windMsg(ctx: AlertContext): MsgTuple {
  const L = ctx.lang;
  const hi = L.startsWith('hi');
  const farmer: MsgTuple = hi
    ? { title: 'तेज़ हवा चेतावनी', message: `हवा ${Math.round(ctx.maxWind)} किमी/घंटा तक चल सकती है। बड़े पेड़ों या अस्थिर संरचनाओं के पास न रहें। फसल सहारा जाँचें।` }
    : { title: 'Strong wind warning', message: `Winds may reach ${Math.round(ctx.maxWind)} km/h. Avoid large trees or unstable structures. Check crop supports and staking.` };
  const student: MsgTuple = hi
    ? { title: 'तेज़ हवा चेतावनी', message: `हवा ${Math.round(ctx.maxWind)} किमी/घंटा तक चल सकती है। खुले क्षेत्र में सावधानी रखें।` }
    : { title: 'Strong wind warning', message: `Winds may reach ${Math.round(ctx.maxWind)} km/h. Be careful in open areas.` };
  const outdoor: MsgTuple = hi
    ? { title: 'तेज़ हवा चेतावनी', message: `हवा ${Math.round(ctx.maxWind)} किमी/घंटा तक चल सकती है। ऊँचाई पर काम टालें और ढीली चीज़ें सुरक्षित करें।` }
    : { title: 'Strong wind warning', message: `Winds may reach ${Math.round(ctx.maxWind)} km/h. Postpone work at height and secure loose materials.` };
  const general: MsgTuple = hi
    ? { title: 'तेज़ हवा चेतावनी', message: `तेज़ हवा (${Math.round(ctx.maxWind)} किमी/घंटा) संभव है। सावधानी बरतें।` }
    : { title: 'Strong wind warning', message: `Strong winds (${Math.round(ctx.maxWind)} km/h) are likely. Take care.` };
  return pick(ctx.profile, { farmer, student, outdoor_worker: outdoor, general });
}

function coldMsg(ctx: AlertContext): MsgTuple {
  const L = ctx.lang;
  const hi = L.startsWith('hi');
  const farmer: MsgTuple = hi
    ? { title: 'ठंड की चेतावनी', message: `न्यूनतम तापमान ${Math.round(ctx.minTemp)}°C तक जा सकता है। नादी फसलों को ढकें और सिंचाई करें ताकि ठंढ से बचाव हो। पौधशाला में गर्मी बनाए रखें।` }
    : { title: 'Cold warning', message: `Minimum temperature may drop to ${Math.round(ctx.minTemp)}°C. Cover tender crops and irrigate to protect against frost. Keep nurseries warm.` };
  const student: MsgTuple = hi
    ? { title: 'ठंड की चेतावनी', message: `तापमान ${Math.round(ctx.minTemp)}°C तक गिर सकता है। गर्म कपड़े पहनें और सुबह जल्दी बाहर न निकलें।` }
    : { title: 'Cold warning', message: `Temperature may drop to ${Math.round(ctx.minTemp)}°C. Wear warm clothes and avoid going out early morning.` };
  const outdoor: MsgTuple = hi
    ? { title: 'ठंड की चेतावनी', message: `तापमान ${Math.round(ctx.minTemp)}°C तक जा सकता है। गर्म कपड़े पहनें, लंबे समय तक ठंड में न रहें।` }
    : { title: 'Cold warning', message: `Temperature may fall to ${Math.round(ctx.minTemp)}°C. Wear layers and avoid prolonged exposure to cold.` };
  const general: MsgTuple = hi
    ? { title: 'ठंड की चेतावनी', message: `ठंड बढ़ेगी (${Math.round(ctx.minTemp)}°C)। गर्म कपड़े पहनें।` }
    : { title: 'Cold warning', message: `It will be cold (${Math.round(ctx.minTemp)}°C). Wear warm clothing.` };
  return pick(ctx.profile, { farmer, student, outdoor_worker: outdoor, general });
}

function fogMsg(ctx: AlertContext): MsgTuple {
  const L = ctx.lang;
  const hi = L.startsWith('hi');
  const farmer: MsgTuple = hi
    ? { title: 'कोहरा चेतावनी', message: 'सुबह घना कोहरा रहेगा। खेत तक जाते समय सावधानी रखें और छिड़काव देर से करें।' }
    : { title: 'Fog warning', message: 'Dense fog expected in the morning. Take care while heading to the field and delay spraying until visibility improves.' };
  const student: MsgTuple = hi
    ? { title: 'कोहरा चेतावनी', message: 'सुबह कोहरा रहेगा। स्कूल जाते समय सावधानी रखें।' }
    : { title: 'Fog warning', message: 'Morning fog likely. Be careful while commuting to school.' };
  const outdoor: MsgTuple = hi
    ? { title: 'कोहरा चेतावनी', message: 'सुबह कोहरे के कारण दृश्यता कम रहेगी। वाहन धीमे चलाएँ और सावधानी बरतें।' }
    : { title: 'Fog warning', message: 'Visibility will be low due to morning fog. Drive slowly and exercise caution.' };
  const general: MsgTuple = hi
    ? { title: 'कोहरा चेतावनी', message: 'सुबह कोहरा संभव है। सावधानी से चलें।' }
    : { title: 'Fog warning', message: 'Morning fog is likely. Travel carefully.' };
  return pick(ctx.profile, { farmer, student, outdoor_worker: outdoor, general });
}

function pick(profile: ProfileType, m: Partial<Record<ProfileType, MsgTuple>>): MsgTuple {
  return m[profile] ?? m.general ?? m.parent ?? m.commuter ?? m.farmer ?? Object.values(m)[0]!;
}

function timeWindow(startHour: number, lang: LangCode): string {
  const h = (startHour % 24);
  const endH = (h + 3) % 24;
  const hi = lang.startsWith('hi');
  const fmt = (x: number) => {
    const ap = x < 12 ? (hi ? 'पूर्वाह्न' : 'AM') : (hi ? 'अपराह्न' : 'PM');
    const hr = x % 12 === 0 ? 12 : x % 12;
    return `${hr} ${ap}`;
  };
  return `${fmt(h)}–${fmt(endH)}`;
}

// ---- Conditions table ----

const RAIN_CODES = [61, 63, 65, 66, 67, 80, 81, 82];
const STORM_CODES = [95, 96, 99];
const FOG_CODES = [45, 48];

const CONDITIONS: Condition[] = [
  {
    event: 'storm',
    severity: 'extreme',
    weatherCodes: STORM_CODES,
    messageBuilder: (ctx) => stormMsg(ctx),
  },
  {
    event: 'rain',
    severity: 'severe',
    weatherCodes: [65, 67, 82],
    messageBuilder: (ctx) => rainMsg(ctx, 'severe'),
  },
  {
    event: 'rain',
    severity: 'moderate',
    weatherCodes: [63, 81],
    messageBuilder: (ctx) => rainMsg(ctx, 'moderate'),
  },
  {
    event: 'wind',
    severity: 'moderate',
    weatherCodes: [],
    messageBuilder: (ctx) => windMsg(ctx),
  },
  {
    event: 'heat',
    severity: 'severe',
    weatherCodes: [],
    messageBuilder: (ctx) => heatMsg(ctx),
  },
  {
    event: 'cold',
    severity: 'moderate',
    weatherCodes: [],
    messageBuilder: (ctx) => coldMsg(ctx),
  },
  {
    event: 'fog',
    severity: 'info',
    weatherCodes: FOG_CODES,
    messageBuilder: (ctx) => fogMsg(ctx),
  },
];

function buildContext(weather: WeatherData, profile: ProfileType, lang: LangCode): AlertContext {
  let rainHours = 0;
  let rainStartHour: number | null = null;
  for (let i = 0; i < weather.hourly.length; i++) {
    const h = weather.hourly[i];
    if (h.precipitationProbability > 55 || h.precipitation > 1) {
      rainHours++;
      if (rainStartHour === null) rainStartHour = i;
    }
  }
  const today = weather.daily[0];
  return {
    profile,
    lang,
    rainHours,
    rainStartHour,
    maxTemp: today.tempMax,
    minTemp: today.tempMin,
    maxWind: Math.max(today.windSpeedMax, ...weather.hourly.slice(0, 24).map((h) => h.windSpeed)),
    todayRainSum: today.precipitationSum,
  };
}

export function evaluateRisk(
  weather: WeatherData,
  profile: ProfileType,
  lang: LangCode
): Alert[] {
  const ctx = buildContext(weather, profile, lang);
  const alerts: Alert[] = [];
  const seen = new Set<WeatherEvent>();

  const allCodes = new Set<number>([
    ...weather.hourly.slice(0, 24).map((h) => h.weatherCode),
    ...weather.daily.slice(0, 2).map((d) => d.weatherCode),
  ]);

  for (const cond of CONDITIONS) {
    if (seen.has(cond.event)) continue;
    let triggered = cond.weatherCodes.length === 0;
    if (cond.weatherCodes.length > 0) {
      triggered = cond.weatherCodes.some((c) => allCodes.has(c));
    }
    // custom numeric thresholds for non-code conditions
    if (cond.event === 'heat' && !triggered) triggered = ctx.maxTemp >= 38;
    if (cond.event === 'cold' && !triggered) triggered = ctx.minTemp <= 8;
    if (cond.event === 'wind' && !triggered) triggered = ctx.maxWind >= 45;
    if (cond.event === 'rain' && !triggered) triggered = ctx.rainHours >= 4 || ctx.todayRainSum >= 15;

    if (triggered) {
      const { title, message } = cond.messageBuilder(ctx);
      alerts.push({
        id: `${cond.event}-${Date.now()}`,
        severity: cond.severity,
        title,
        message,
        weatherEvent: cond.event,
        voiceText: `${title}. ${message}`,
      });
      seen.add(cond.event);
    }
  }

  // severity ordering: extreme > severe > moderate > info
  const order: Record<Severity, number> = { extreme: 0, severe: 1, moderate: 2, info: 3 };
  alerts.sort((a, b) => order[a.severity] - order[b.severity]);
  return alerts;
}

export function severityColor(sev: Severity): string {
  switch (sev) {
    case 'extreme':
      return 'bg-red-500/15 text-red-300 border-red-500/40';
    case 'severe':
      return 'bg-orange-500/15 text-orange-300 border-orange-500/40';
    case 'moderate':
      return 'bg-amber-500/15 text-amber-300 border-amber-500/40';
    default:
      return 'bg-sky-500/15 text-sky-300 border-sky-500/40';
  }
}

export function eventIcon(event: WeatherEvent): string {
  switch (event) {
    case 'rain':
      return '🌧️';
    case 'heat':
      return '🔥';
    case 'storm':
      return '⛈️';
    case 'wind':
      return '💨';
    case 'cold':
      return '❄️';
    case 'fog':
      return '🌫️';
  }
}
