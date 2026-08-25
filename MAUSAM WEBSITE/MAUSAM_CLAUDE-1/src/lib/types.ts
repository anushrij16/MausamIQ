export type ProfileType =
  | 'health_conscious'
  | 'fitness_enthusiast'
  | 'beachgoer'
  | 'traveler'
  | 'parent'
  | 'farmer'
  | 'commuter'
  | 'event_planner'
  | 'student'
  | 'outdoor_worker'
  | 'general';

export type LangCode = 'hi-IN' | 'en-IN' | 'ta-IN';

export interface Preferences {
  profile_type: ProfileType;
  language: LangCode;
  lat: number | null;
  lng: number | null;
  city_name: string | null;
}

export interface CurrentWeather {
  temperature: number;
  apparentTemperature: number;
  humidity: number;
  precipitation: number;
  weatherCode: number;
  windSpeed: number;
  isDay: boolean;
  time: string;
}

export interface HourlyEntry {
  time: string;
  temperature: number;
  precipitationProbability: number;
  precipitation: number;
  weatherCode: number;
  windSpeed: number;
  /** UV index for this hour, when available from the provider. */
  uvIndex?: number;
  /** Volumetric soil moisture (m3/m3) in the top 0-1cm layer, when available. */
  soilMoisture?: number;
}

export interface DailyEntry {
  date: string;
  weatherCode: number;
  tempMax: number;
  tempMin: number;
  precipitationSum: number;
  precipitationProbabilityMax: number;
  windSpeedMax: number;
  /** Peak UV index for the day, when available from the provider. */
  uvIndexMax?: number;
}

export interface WeatherData {
  current: CurrentWeather;
  hourly: HourlyEntry[];
  daily: DailyEntry[];
  timezone: string;
}

export type Severity = 'info' | 'moderate' | 'severe' | 'extreme';
export type WeatherEvent = 'rain' | 'heat' | 'storm' | 'wind' | 'cold' | 'fog';

export interface Alert {
  id: string;
  severity: Severity;
  title: string;
  message: string;
  weatherEvent: WeatherEvent;
  voiceText?: string;
}

export interface CopilotAdvice {
  headline: string;
  actions: string[];
  voiceText: string;
}

export interface LocationResult {
  name: string;
  latitude: number;
  longitude: number;
  country?: string;
  admin1?: string;
}
