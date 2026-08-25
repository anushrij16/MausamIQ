import { useState } from 'react';
import { MapPin, Search, LocateFixed, Loader2, Save } from 'lucide-react';
import type { Preferences, ProfileType, LangCode, LocationResult } from '@/lib/types';
import { LANGUAGES, PROFILES, t } from '@/lib/translations';
import { searchLocations } from '@/lib/weather';

interface Props {
  prefs: Preferences;
  lang: LangCode;
  onSave: (prefs: Preferences) => Promise<void>;
  onClose: () => void;
}

export default function SettingsPanel({ prefs, lang, onSave, onClose }: Props) {
  const [profile, setProfile] = useState<ProfileType>(prefs.profile_type);
  const [language, setLanguage] = useState<LangCode>(prefs.language);
  const [query, setQuery] = useState(prefs.city_name ?? '');
  const [results, setResults] = useState<LocationResult[]>([]);
  const [selected, setSelected] = useState<LocationResult | null>(
    prefs.lat != null && prefs.lng != null
      ? {
          name: prefs.city_name ?? '',
          latitude: prefs.lat,
          longitude: prefs.lng,
        }
      : null
  );
  const [searching, setSearching] = useState(false);
  const [locating, setLocating] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSearch = async (q: string) => {
    setQuery(q);
    if (q.trim().length < 2) {
      setResults([]);
      return;
    }
    setSearching(true);
    try {
      const r = await searchLocations(q);
      setResults(r);
    } finally {
      setSearching(false);
    }
  };

  const handleUseLocation = () => {
    if (!navigator.geolocation) {
      setError('Geolocation not available');
      return;
    }
    setLocating(true);
    setError(null);
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const loc: LocationResult = {
          name: 'My current location',
          latitude: pos.coords.latitude,
          longitude: pos.coords.longitude,
        };
        setSelected(loc);
        setQuery(loc.name);
        setResults([]);
        setLocating(false);
      },
      () => {
        setError('Could not get your location');
        setLocating(false);
      }
    );
  };

  const handleSave = async () => {
    setSaving(true);
    setError(null);
    try {
      await onSave({
        profile_type: profile,
        language,
        lat: selected?.latitude ?? null,
        lng: selected?.longitude ?? null,
        city_name: selected?.name ?? null,
      });
      onClose();
    } catch {
      setError('Could not save settings');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end justify-center bg-black/60 backdrop-blur-sm sm:items-center">
      <div className="w-full max-w-lg rounded-t-3xl border border-white/10 bg-slate-900 p-6 text-slate-100 shadow-2xl sm:rounded-3xl">
        <div className="mb-5 flex items-center justify-between">
          <h2 className="text-xl font-semibold">{t(lang, 'settings')}</h2>
          <button
            onClick={onClose}
            className="rounded-full p-1 text-slate-400 transition hover:bg-white/10 hover:text-white"
            aria-label="Close"
          >
            ✕
          </button>
        </div>

        {/* Profile */}
        <section className="mb-6">
          <label className="mb-2 block text-sm font-medium text-slate-300">
            {t(lang, 'profile')}
          </label>
          <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
            {PROFILES.map((p) => (
              <button
                key={p.type}
                onClick={() => setProfile(p.type)}
                className={`flex flex-col items-center gap-1 rounded-2xl border px-3 py-3 text-sm transition ${
                  profile === p.type
                    ? 'border-teal-400 bg-teal-400/15 text-white'
                    : 'border-white/10 bg-white/5 text-slate-300 hover:border-white/20'
                }`}
              >
                <span className="text-2xl">{p.icon}</span>
                {t(lang, p.labelKey)}
              </button>
            ))}
          </div>
        </section>

        {/* Language */}
        <section className="mb-6">
          <label className="mb-2 block text-sm font-medium text-slate-300">
            {t(lang, 'language')}
          </label>
          <div className="flex flex-wrap gap-2">
            {LANGUAGES.map((l) => (
              <button
                key={l.code}
                onClick={() => setLanguage(l.code)}
                className={`rounded-full border px-4 py-1.5 text-sm transition ${
                  language === l.code
                    ? 'border-teal-400 bg-teal-400/15 text-white'
                    : 'border-white/10 bg-white/5 text-slate-300 hover:border-white/20'
                }`}
              >
                {l.native}
              </button>
            ))}
          </div>
        </section>

        {/* Location */}
        <section className="mb-6">
          <label className="mb-2 block text-sm font-medium text-slate-300">
            {t(lang, 'location')}
          </label>
          <div className="relative">
            <Search className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
            <input
              value={query}
              onChange={(e) => handleSearch(e.target.value)}
              placeholder={t(lang, 'searchLocation')}
              className="w-full rounded-2xl border border-white/10 bg-white/5 py-2.5 pl-9 pr-3 text-sm text-white placeholder-slate-400 outline-none focus:border-teal-400"
            />
            {searching && (
              <Loader2 className="absolute right-3 top-3 h-4 w-4 animate-spin text-slate-400" />
            )}
          </div>

          {results.length > 0 && (
            <ul className="mt-2 max-h-40 overflow-auto rounded-2xl border border-white/10 bg-slate-800/80 text-sm">
              {results.map((r) => (
                <li key={`${r.latitude}-${r.longitude}`}>
                  <button
                    onClick={() => {
                      setSelected(r);
                      setQuery(`${r.name}${r.admin1 ? ', ' + r.admin1 : ''}`);
                      setResults([]);
                    }}
                    className="flex w-full items-center gap-2 px-3 py-2 text-left text-slate-200 hover:bg-white/10"
                  >
                    <MapPin className="h-4 w-4 text-teal-300" />
                    {r.name}
                    {r.admin1 ? `, ${r.admin1}` : ''}
                    {r.country ? `, ${r.country}` : ''}
                  </button>
                </li>
              ))}
            </ul>
          )}

          <button
            onClick={handleUseLocation}
            disabled={locating}
            className="mt-3 inline-flex items-center gap-2 rounded-2xl border border-white/10 bg-white/5 px-3 py-2 text-sm text-slate-200 transition hover:border-white/20 disabled:opacity-50"
          >
            {locating ? (
              <Loader2 className="h-4 w-4 animate-spin" />
            ) : (
              <LocateFixed className="h-4 w-4 text-teal-300" />
            )}
            {t(lang, 'useMyLocation')}
          </button>

          {selected && (
            <p className="mt-2 text-xs text-slate-400">
              {selected.name} · {selected.latitude.toFixed(2)}, {selected.longitude.toFixed(2)}
            </p>
          )}
        </section>

        {error && <p className="mb-3 text-sm text-red-300">{error}</p>}

        <button
          onClick={handleSave}
          disabled={saving || !selected}
          className="flex w-full items-center justify-center gap-2 rounded-2xl bg-gradient-to-r from-teal-500 to-sky-500 py-3 font-semibold text-white transition hover:opacity-90 disabled:opacity-50"
        >
          {saving ? (
            <Loader2 className="h-4 w-4 animate-spin" />
          ) : (
            <Save className="h-4 w-4" />
          )}
          {saving ? t(lang, 'saving') : t(lang, 'save')}
        </button>
      </div>
    </div>
  );
}
