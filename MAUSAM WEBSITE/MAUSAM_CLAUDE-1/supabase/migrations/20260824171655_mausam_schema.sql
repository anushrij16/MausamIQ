/*
# MAUSAM — Weather app schema (single-tenant, no auth)

This app does NOT have a sign-in screen. Users configure their profile
(farmer / student / outdoor worker / general), preferred language, and
location once, and the app persists those preferences so they survive
reload. Alert history (risk warnings the app has shown) is also logged
so the user can review past warnings.

1. New Tables

  - `user_preferences`
    - `id` (uuid, primary key)
    - `profile_type` (text): farmer | student | outdoor_worker | general
    - `language` (text): BCP-47 voice locale, e.g. "hi-IN", "en-IN"
    - `lat` (double precision)
    - `lng` (double precision)
    - `city_name` (text)
    - `updated_at` (timestamptz)

  - `alert_log`
    - `id` (uuid, primary key)
    - `severity` (text): info | moderate | severe | extreme
    - `title` (text)
    - `message` (text)
    - `weather_event` (text): rain | heat | storm | wind | cold | fog
    - `created_at` (timestamptz)

2. Security
  - RLS enabled on both tables.
  - Single-tenant no-auth app → policies use TO anon, authenticated with
    USING (true) / WITH CHECK (true) because the data is intentionally
    shared within this app instance.
*/

CREATE TABLE IF NOT EXISTS user_preferences (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  profile_type text NOT NULL DEFAULT 'general',
  language text NOT NULL DEFAULT 'hi-IN',
  lat double precision,
  lng double precision,
  city_name text,
  updated_at timestamptz DEFAULT now()
);

ALTER TABLE user_preferences ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "anon_select_prefs" ON user_preferences;
CREATE POLICY "anon_select_prefs" ON user_preferences FOR SELECT
  TO anon, authenticated USING (true);

DROP POLICY IF EXISTS "anon_insert_prefs" ON user_preferences;
CREATE POLICY "anon_insert_prefs" ON user_preferences FOR INSERT
  TO anon, authenticated WITH CHECK (true);

DROP POLICY IF EXISTS "anon_update_prefs" ON user_preferences;
CREATE POLICY "anon_update_prefs" ON user_preferences FOR UPDATE
  TO anon, authenticated USING (true) WITH CHECK (true);

DROP POLICY IF EXISTS "anon_delete_prefs" ON user_preferences;
CREATE POLICY "anon_delete_prefs" ON user_preferences FOR DELETE
  TO anon, authenticated USING (true);

CREATE TABLE IF NOT EXISTS alert_log (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  severity text NOT NULL DEFAULT 'info',
  title text NOT NULL,
  message text NOT NULL,
  weather_event text NOT NULL DEFAULT 'rain',
  created_at timestamptz DEFAULT now()
);

ALTER TABLE alert_log ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "anon_select_alerts" ON alert_log;
CREATE POLICY "anon_select_alerts" ON alert_log FOR SELECT
  TO anon, authenticated USING (true);

DROP POLICY IF EXISTS "anon_insert_alerts" ON alert_log;
CREATE POLICY "anon_insert_alerts" ON alert_log FOR INSERT
  TO anon, authenticated WITH CHECK (true);

DROP POLICY IF EXISTS "anon_delete_alerts" ON alert_log;
CREATE POLICY "anon_delete_alerts" ON alert_log FOR DELETE
  TO anon, authenticated USING (true);