import { supabase } from './supabase';
import type { Preferences, Alert, Severity, WeatherEvent } from './types';

const PREF_ID_KEY = 'mausam_pref_id';

export async function loadPreferences(): Promise<Preferences | null> {
  const id = localStorage.getItem(PREF_ID_KEY);
  if (id) {
    const { data } = await supabase
      .from('user_preferences')
      .select('*')
      .eq('id', id)
      .maybeSingle();
    if (data) {
      return {
        profile_type: data.profile_type,
        language: data.language,
        lat: data.lat,
        lng: data.lng,
        city_name: data.city_name,
      };
    }
  }
  // fallback: get the most recently updated row
  const { data } = await supabase
    .from('user_preferences')
    .select('*')
    .order('updated_at', { ascending: false })
    .limit(1)
    .maybeSingle();
  if (data) {
    localStorage.setItem(PREF_ID_KEY, data.id);
    return {
      profile_type: data.profile_type,
      language: data.language,
      lat: data.lat,
      lng: data.lng,
      city_name: data.city_name,
    };
  }
  return null;
}

export async function savePreferences(prefs: Preferences): Promise<void> {
  const id = localStorage.getItem(PREF_ID_KEY);
  const row = {
    profile_type: prefs.profile_type,
    language: prefs.language,
    lat: prefs.lat,
    lng: prefs.lng,
    city_name: prefs.city_name,
    updated_at: new Date().toISOString(),
  };
  if (id) {
    const { data, error } = await supabase
      .from('user_preferences')
      .update(row)
      .eq('id', id)
      .select('id')
      .maybeSingle();
    if (data && !error) return;
  }
  const { data, error } = await supabase
    .from('user_preferences')
    .insert(row)
    .select('id')
    .maybeSingle();
  if (data) localStorage.setItem(PREF_ID_KEY, data.id);
  if (error) throw error;
}

export async function logAlert(alert: Alert): Promise<void> {
  await supabase.from('alert_log').insert({
    severity: alert.severity,
    title: alert.title,
    message: alert.message,
    weather_event: alert.weatherEvent,
  });
}

export interface AlertLogRow {
  id: string;
  severity: Severity;
  title: string;
  message: string;
  weather_event: WeatherEvent;
  created_at: string;
}

export async function loadAlertHistory(limit = 20): Promise<AlertLogRow[]> {
  const { data } = await supabase
    .from('alert_log')
    .select('id, severity, title, message, weather_event, created_at')
    .order('created_at', { ascending: false })
    .limit(limit);
  return (data as AlertLogRow[] | null) ?? [];
}
