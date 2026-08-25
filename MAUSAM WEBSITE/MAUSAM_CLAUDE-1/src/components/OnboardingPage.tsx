import { useEffect, useState } from 'react';
import { Check, Loader2, MapPin, Search } from 'lucide-react';
import type { LangCode, Preferences, ProfileType, LocationResult } from '@/lib/types';
import { LANGUAGES, PROFILES, t } from '@/lib/translations';
import { searchLocations, reverseGeocode } from '@/lib/weather';

interface Props {
  initialPrefs: Preferences;
  onComplete: (prefs: Preferences) => Promise<void>;
}

export default function OnboardingPage({ initialPrefs, onComplete }: Props) {
  const [step, setStep] = useState<'language' | 'profile' | 'location'>('language');
  const [language, setLanguage] = useState<LangCode>(
    ['en-IN', 'ta-IN', 'hi-IN'].includes(initialPrefs.language) ? initialPrefs.language : 'en-IN'
  );
  const [profile, setProfile] = useState<ProfileType>(initialPrefs.profile_type);
  const [query, setQuery] = useState(initialPrefs.city_name ?? '');
  const [selected, setSelected] = useState<LocationResult | null>(
    initialPrefs.lat != null && initialPrefs.lng != null
      ? { name: initialPrefs.city_name ?? 'New Delhi', latitude: initialPrefs.lat, longitude: initialPrefs.lng }
      : null
  );
  const [results, setResults] = useState<LocationResult[]>([]);
  const [searching, setSearching] = useState(false);
  const [saving, setSaving] = useState(false);

  const lang = language;

  // Prefer the user's real location for the first weather screen. The user can
  // still search for any city manually.
  useEffect(() => {
    if (!navigator.geolocation || selected) return;
    navigator.geolocation.getCurrentPosition(
      async (position) => {
        const { latitude, longitude } = position.coords;
        const location = await reverseGeocode(latitude, longitude);
        if (location) {
          setSelected(location);
          setQuery(location.name);
        } else {
          setSelected({ name: 'My current location', latitude, longitude });
          setQuery('My current location');
        }
      },
      () => { /* manual city search remains available */ },
      { enableHighAccuracy: false, timeout: 8000, maximumAge: 300000 }
    );
  }, [selected]);

  const search = async (value: string) => {
    setQuery(value);
    if (value.trim().length < 2) return setResults([]);
    setSearching(true);
    try { setResults(await searchLocations(value)); } finally { setSearching(false); }
  };

  const finish = async () => {
    setSaving(true);
    try {
      await onComplete({
        profile_type: profile,
        language,
        lat: selected?.latitude ?? initialPrefs.lat,
        lng: selected?.longitude ?? initialPrefs.lng,
        city_name: selected?.name ?? initialPrefs.city_name,
      });
    } finally { setSaving(false); }
  };

  return (
    <div className="min-h-screen bg-slate-950 px-4 py-8 text-slate-100 sm:flex sm:items-center sm:justify-center">
      <div className="w-full max-w-2xl rounded-3xl border border-white/10 bg-slate-900 p-6 shadow-2xl sm:p-8">
        <div className="mb-7">
          <div className="mb-2 text-xs font-semibold uppercase tracking-[0.2em] text-teal-400">MAUSAM</div>
          <h1 className="text-2xl font-bold">{step === 'language' ? 'Choose your language' : step === 'profile' ? t(lang, 'profile') : t(lang, 'location')}</h1>
          <p className="mt-2 text-sm text-slate-400">
            {step === 'language' ? 'Choose the language Mausam Sakhi should speak and reply in.' : step === 'profile' ? 'Personalize your weather experience.' : 'Choose where you want MAUSAM to check the weather.'}
          </p>
          <div className="mt-5 flex gap-2">
            {(['language','profile','location'] as const).map((s, i) => { const active = ['language','profile','location'].indexOf(step) >= i; return <div key={s} className={`h-1.5 flex-1 rounded-full ${active ? 'bg-teal-400' : 'bg-white/10'}`} />; })}
          </div>
        </div>

        {step === 'language' ? (
          <div className="grid gap-3 sm:grid-cols-3">
            {LANGUAGES.map((item) => (
              <button key={item.code} onClick={() => setLanguage(item.code)} className={`rounded-2xl border p-5 text-left transition ${language === item.code ? 'border-teal-400 bg-teal-400/15' : 'border-white/10 bg-white/5 hover:border-white/20'}`}>
                <div className="text-lg font-semibold">{item.native}</div>
                <div className="mt-1 text-xs text-slate-400">{item.label}</div>
                {language === item.code && <Check className="mt-4 h-5 w-5 text-teal-300" />}
              </button>
            ))}
          </div>
        ) : step === 'profile' ? (
          <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
            {PROFILES.map((p) => (
              <button key={p.type} onClick={() => setProfile(p.type)} className={`relative flex min-h-28 flex-col items-center justify-center rounded-2xl border p-3 text-center transition ${profile === p.type ? 'border-teal-400 bg-teal-400/15' : 'border-white/10 bg-white/5 hover:border-white/20'}`}>
                <span className="text-2xl">{p.icon}</span>
                <span className="mt-2 text-sm font-semibold">{t(lang, p.labelKey)}</span>
                {profile === p.type && <Check className="absolute right-2 top-2 h-4 w-4 text-teal-300" />}
              </button>
            ))}
          </div>
        ) : (
          <div>
            <div className="rounded-2xl border border-teal-400/20 bg-teal-400/10 p-4">
              <div className="flex items-center gap-3">
                <MapPin className="h-6 w-6 text-teal-300" />
                <div><div className="text-sm font-semibold">{selected?.name || 'Choose a location'}</div><div className="text-xs text-slate-400">Your weather and travel answers will use this location.</div></div>
              </div>
            </div>
            <div className="mt-5">
              <label className="mb-2 block text-sm font-medium text-slate-300">{t(lang, 'location')}</label>
              <div className="relative">
                <Search className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
                <input value={query} onChange={(e) => void search(e.target.value)} placeholder={t(lang, 'searchLocation')} className="w-full rounded-2xl border border-white/10 bg-white/5 py-3 pl-10 pr-10 text-sm outline-none focus:border-teal-400" />
                {searching && <Loader2 className="absolute right-3 top-3 h-4 w-4 animate-spin text-slate-400" />}
              </div>
              {results.length > 0 && <div className="mt-2 max-h-48 overflow-auto rounded-2xl border border-white/10 bg-slate-800">{results.map((r) => <button key={`${r.latitude}-${r.longitude}`} onClick={() => { setSelected(r); setQuery(r.name); setResults([]); }} className="flex w-full items-center gap-2 px-3 py-3 text-left text-sm hover:bg-white/10"><MapPin className="h-4 w-4 text-teal-300" />{r.name}{r.admin1 ? `, ${r.admin1}` : ''}</button>)}</div>}
            </div>
          </div>
        )}

        <div className="mt-8 flex justify-end gap-3">
          {step !== 'language' && <button onClick={() => setStep(step === 'location' ? 'profile' : 'language')} className="rounded-2xl border border-white/10 px-5 py-3 text-sm text-slate-300">Back</button>}
          {step === 'language' ? (
            <button onClick={() => setStep('profile')} className="rounded-2xl bg-gradient-to-r from-teal-500 to-sky-500 px-6 py-3 text-sm font-semibold">Continue</button>
          ) : step === 'profile' ? (
            <button onClick={() => setStep('location')} className="rounded-2xl bg-gradient-to-r from-teal-500 to-sky-500 px-6 py-3 text-sm font-semibold">Continue</button>
          ) : (
            <button onClick={() => void finish()} disabled={saving || !selected} className="flex items-center gap-2 rounded-2xl bg-gradient-to-r from-teal-500 to-sky-500 px-6 py-3 text-sm font-semibold disabled:opacity-60">{saving && <Loader2 className="h-4 w-4 animate-spin" />}{t(lang, 'continue')}</button>
          )}
        </div>
      </div>
    </div>
  );
}
