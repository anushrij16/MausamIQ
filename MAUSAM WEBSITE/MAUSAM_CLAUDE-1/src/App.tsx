import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { Settings, Loader2, AlertTriangle, CloudSun, History } from 'lucide-react';
import type { Preferences, WeatherData, Alert, LangCode, CopilotAdvice } from '@/lib/types';
import { t, LANGUAGES } from '@/lib/translations';
import { fetchWeather, weatherCodeLabel, weatherIcon } from '@/lib/weather';
import { evaluateRisk } from '@/lib/riskEngine';
import { buildCopilotAdvice } from '@/lib/copilot';
import {
  loadPreferences,
  savePreferences,
  logAlert,
  loadAlertHistory,
  type AlertLogRow,
} from '@/lib/storage';

import WeatherHero from '@/components/WeatherHero';
import ForecastStrip from '@/components/ForecastStrip';
import DailyForecast from '@/components/DailyForecast';
import ActionCopilot from '@/components/ActionCopilot';
import RiskAlerts from '@/components/RiskAlerts';
import VoiceAssistant from '@/components/VoiceAssistant';
import SettingsPanel from '@/components/SettingsPanel';
import LoginPage from '@/components/LoginPage';
import OnboardingPage from '@/components/OnboardingPage';

const DEFAULT_PREFS: Preferences = {
  profile_type: 'general',
  language: 'en-IN',
  lat: 11.0168, // Coimbatore
  lng: 76.9558,
  city_name: 'Coimbatore',
};

export default function App() {
  const [prefs, setPrefs] = useState<Preferences | null>(null);
  const [loggedIn, setLoggedIn] = useState(() => sessionStorage.getItem('mausam_auth_v2') === '1');
  const [userEmail, setUserEmail] = useState(() => sessionStorage.getItem('mausam_user_identifier') ?? '');
  const [onboardingDone, setOnboardingDone] = useState(() => sessionStorage.getItem('mausam_onboarding_v2_done') === '1');
  const [prefsLoading, setPrefsLoading] = useState(true);
  const [weather, setWeather] = useState<WeatherData | null>(null);
  const [weatherError, setWeatherError] = useState<string | null>(null);
  const [showSettings, setShowSettings] = useState(false);
  const [history, setHistory] = useState<AlertLogRow[]>([]);
  const [showHistory, setShowHistory] = useState(false);
  const loggedAlertsRef = useRef<Set<string>>(new Set());

  const lang: LangCode = prefs?.language ?? DEFAULT_PREFS.language;

  // Load preferences once
  useEffect(() => {
    void loadPreferences().then((p) => {
      setPrefs(p);
      if (!p) setShowSettings(true);
      setPrefsLoading(false);
    });
  }, []);

  // Fetch weather when prefs change
  useEffect(() => {
    if (!prefs || prefs.lat == null || prefs.lng == null) return;
    setWeatherError(null);
    setWeather(null);
    fetchWeather(prefs.lat, prefs.lng)
      .then((w) => setWeather(w))
      .catch(() => setWeatherError(t(prefs.language, 'loadError')));
  }, [prefs]);

  const alerts: Alert[] = useMemo(() => {
    if (!weather || !prefs) return [];
    return evaluateRisk(weather, prefs.profile_type, prefs.language);
  }, [weather, prefs]);

  const copilot: CopilotAdvice | null = useMemo(() => {
    if (!weather || !prefs) return null;
    return buildCopilotAdvice(weather, prefs.profile_type, prefs.language);
  }, [weather, prefs]);

  // Log new alerts to Supabase (dedupe within session)
  useEffect(() => {
    if (!alerts.length) return;
    for (const a of alerts) {
      const key = `${a.weatherEvent}-${a.severity}-${a.title}`;
      if (!loggedAlertsRef.current.has(key)) {
        loggedAlertsRef.current.add(key);
        void logAlert(a).catch(() => {});
      }
    }
  }, [alerts]);

  const handleSavePrefs = useCallback(async (next: Preferences) => {
    await savePreferences(next);
    setPrefs(next);
  }, []);

  // Load alert history when panel opens
  useEffect(() => {
    if (showHistory) void loadAlertHistory().then(setHistory);
  }, [showHistory]);

  // ---- Spoken summaries ----
  const weatherSpoken = useCallback((): string => {
    if (!weather) return '';
    const c = weather.current;
    const desc = weatherCodeLabel(c.weatherCode);
    const temp = Math.round(c.temperature);
    const wind = Math.round(c.windSpeed);
    if (lang.startsWith('ta')) return `இப்போது ${desc} நிலவுகிறது. வெப்பநிலை ${temp} டிகிரி. ஈரப்பதம் ${c.humidity} சதவீதம். காற்றின் வேகம் மணிக்கு ${wind} கிலோமீட்டர்.`;
    if (lang.startsWith('hi')) return `अभी ${desc} है, तापमान ${temp} डिग्री है। नमी ${c.humidity} प्रतिशत, हवा ${wind} किलोमीटर प्रति घंटा।`;
    if (lang.startsWith('bn')) return `এখন ${desc}। তাপমাত্রা ${temp} ডিগ্রি। আর্দ্রতা ${c.humidity} শতাংশ এবং বাতাসের গতি ${wind} কিলোমিটার প্রতি ঘণ্টা।`;
    if (lang.startsWith('te')) return `ప్రస్తుతం ${desc}. ఉష్ణోగ్రత ${temp} డిగ్రీలు. తేమ ${c.humidity} శాతం, గాలి వేగం గంటకు ${wind} కిలోమీటర్లు.`;
    if (lang.startsWith('mr')) return `आत्ता ${desc} आहे. तापमान ${temp} अंश आहे. आर्द्रता ${c.humidity} टक्के आणि वाऱ्याचा वेग ${wind} किलोमीटर प्रति तास आहे.`;
    return `Right now: ${desc}, ${temp} degrees. Humidity ${c.humidity} percent, wind ${wind} km/h.`;
  }, [weather, lang]);

  const alertsSpoken = useCallback((): string => {
    if (!alerts.length) {
      if (lang.startsWith('ta')) return 'தற்போது செயலில் உள்ள வானிலை எச்சரிக்கைகள் எதுவும் இல்லை.';
      if (lang.startsWith('hi')) return 'कोई चेतावनी नहीं है।';
      if (lang.startsWith('bn')) return 'এখন কোনো সক্রিয় সতর্কতা নেই।';
      if (lang.startsWith('te')) return 'ప్రస్తుతం ఎలాంటి వాతావరణ హెచ్చరికలు లేవు.';
      if (lang.startsWith('mr')) return 'सध्या कोणत्याही सक्रिय हवामान सूचना नाहीत.';
      return 'No active warnings.';
    }
    const eventText: Record<string, Record<string, string>> = {
      ta: { rain: 'மழை எச்சரிக்கை உள்ளது.', heat: 'வெப்ப எச்சரிக்கை உள்ளது.', storm: 'புயல் எச்சரிக்கை உள்ளது.', wind: 'பலத்த காற்று எச்சரிக்கை உள்ளது.', cold: 'குளிர் எச்சரிக்கை உள்ளது.', fog: 'மூடுபனி எச்சரிக்கை உள்ளது.' },
      hi: { rain: 'बारिश की चेतावनी है।', heat: 'गर्मी की चेतावनी है।', storm: 'तूफान की चेतावनी है।', wind: 'तेज़ हवा की चेतावनी है।', cold: 'ठंड की चेतावनी है।', fog: 'कोहरे की चेतावनी है।' },
      bn: { rain: 'বৃষ্টির সতর্কতা রয়েছে।', heat: 'তাপের সতর্কতা রয়েছে।', storm: 'ঝড়ের সতর্কতা রয়েছে।', wind: 'প্রবল বাতাসের সতর্কতা রয়েছে।', cold: 'ঠান্ডার সতর্কতা রয়েছে।', fog: 'কুয়াশার সতর্কতা রয়েছে।' },
      te: { rain: 'వర్షం హెచ్చరిక ఉంది.', heat: 'వేడి హెచ్చరిక ఉంది.', storm: 'తుఫాను హెచ్చరిక ఉంది.', wind: 'బలమైన గాలి హెచ్చరిక ఉంది.', cold: 'చలి హెచ్చరిక ఉంది.', fog: 'పొగమంచు హెచ్చరిక ఉంది.' },
      mr: { rain: 'पावसाची सूचना आहे.', heat: 'उष्णतेची सूचना आहे.', storm: 'वादळाची सूचना आहे.', wind: 'जोरदार वाऱ्याची सूचना आहे.', cold: 'थंडीची सूचना आहे.', fog: 'धुक्याची सूचना आहे.' },
      en: { rain: 'There is a rain warning.', heat: 'There is a heat warning.', storm: 'There is a storm warning.', wind: 'There is a strong wind warning.', cold: 'There is a cold warning.', fog: 'There is a fog warning.' },
    };
    const key = lang.slice(0, 2);
    return alerts.map((a) => eventText[key]?.[a.weatherEvent] ?? eventText.en[a.weatherEvent]).join(' ');
  }, [alerts, lang]);

  const adviceSpoken = useCallback((): string => {
    if (!copilot) return '';
    return copilot.voiceText;
  }, [copilot]);

  const fullSpoken = useCallback((): string => {
    const greeting = lang.startsWith('ta') ? 'வணக்கம், மௌசம் சகி இங்கே இருக்கிறேன்.' : lang.startsWith('hi') ? 'नमस्ते, मौसम सखी यहाँ है।' : lang.startsWith('bn') ? 'নমস্কার, মৌসম সখী এখানে আছি।' : lang.startsWith('te') ? 'నమస్కారం, మౌసమ్ సఖి ఇక్కడ ఉన్నాను.' : lang.startsWith('mr') ? 'नमस्कार, मौसम सखी इथे आहे.' : 'Hello, Mausam Sakhi here.';
    return [greeting, weatherSpoken(), alertsSpoken(), adviceSpoken()]
      .filter(Boolean)
      .join(' ');
  }, [lang, weatherSpoken, alertsSpoken, adviceSpoken]);

  // ---- Render ----
  if (!loggedIn) {
    return <LoginPage onLogin={(identifier) => { setUserEmail(identifier); setLoggedIn(true); setOnboardingDone(false); sessionStorage.setItem('mausam_auth_v2', '1'); }} />;
  }

  if (prefsLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-950 text-slate-300">
        <Loader2 className="h-6 w-6 animate-spin" />
      </div>
    );
  }

  if (!prefs || !onboardingDone) {
    return (
      <OnboardingPage
        initialPrefs={prefs ?? DEFAULT_PREFS}
        onComplete={async (next) => {
          await handleSavePrefs(next);
          sessionStorage.setItem('mausam_onboarding_v2_done', '1');
          setOnboardingDone(true);
        }}
      />
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      {/* Header */}
      <header className="sticky top-0 z-30 border-b border-white/5 bg-slate-950/80 backdrop-blur">
        <div className="mx-auto flex max-w-5xl items-center gap-3 px-4 py-3">
          <CloudSun className="h-7 w-7 text-teal-400" />
          <div>
            <h1 className="text-lg font-semibold leading-tight">{t(lang, 'appName')}</h1>
            <p className="text-[11px] text-slate-400">{t(lang, 'appTagline')}</p>
          </div>
          <div className="ml-auto flex items-center gap-2">
            <button
              onClick={() => setShowHistory(true)}
              className="inline-flex items-center gap-1.5 rounded-full border border-white/10 bg-white/5 px-3 py-1.5 text-xs text-slate-200 transition hover:border-white/20"
            >
              <History className="h-3.5 w-3.5" />
              <span className="hidden sm:inline">{t(lang, 'alertHistory')}</span>
            </button>
            <button
              onClick={() => setShowSettings(true)}
              className="inline-flex items-center gap-1.5 rounded-full border border-white/10 bg-white/5 px-3 py-1.5 text-xs text-slate-200 transition hover:border-white/20"
            >
              <Settings className="h-3.5 w-3.5" />
              <span className="hidden sm:inline">{t(lang, 'settings')}</span>
            </button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-5xl px-4 py-6">
        {weatherError && (
          <div className="mb-5 flex items-center gap-2 rounded-2xl border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
            <AlertTriangle className="h-4 w-4" />
            {weatherError}
          </div>
        )}

        {!weather && !weatherError && (
          <div className="flex items-center justify-center py-20 text-slate-400">
            <Loader2 className="mr-2 h-5 w-5 animate-spin" />
            {t(lang, 'loading')}
          </div>
        )}

        {weather && (
          <div className="grid gap-5 lg:grid-cols-2">
            <div className="space-y-5">
              <WeatherHero weather={weather} cityName={prefs?.city_name ?? null} />
              <ForecastStrip weather={weather} />
              <DailyForecast weather={weather} />
            </div>

            <div className="space-y-5">
              <VoiceAssistant
                lang={lang}
                fullSpokenSummary={fullSpoken}
                alertsSpokenSummary={alertsSpoken}
                adviceSpokenSummary={adviceSpoken}
                weatherSpokenSummary={weatherSpoken}
                weather={weather}
                profile={prefs.profile_type}
              />
              <RiskAlerts alerts={alerts} />
              <ActionCopilot advice={copilot} />
            </div>
          </div>
        )}

        <footer className="mt-10 text-center text-xs text-slate-500">
          {t(lang, 'footer')}
        </footer>
      </main>

      {showSettings && prefs && (
        <SettingsPanel
          prefs={prefs}
          lang={lang}
          onSave={handleSavePrefs}
          onClose={() => setShowSettings(false)}
        />
      )}

      {showHistory && (
        <HistoryDrawer
          lang={lang}
          history={history}
          onClose={() => setShowHistory(false)}
        />
      )}
    </div>
  );
}

function HistoryDrawer({
  lang,
  history,
  onClose,
}: {
  lang: LangCode;
  history: AlertLogRow[];
  onClose: () => void;
}) {
  const sevColor: Record<string, string> = {
    extreme: 'bg-red-500/15 text-red-300 border-red-500/40',
    severe: 'bg-orange-500/15 text-orange-300 border-orange-500/40',
    moderate: 'bg-amber-500/15 text-amber-300 border-amber-500/40',
    info: 'bg-sky-500/15 text-sky-300 border-sky-500/40',
  };
  return (
    <div
      className="fixed inset-0 z-50 flex justify-end bg-black/60 backdrop-blur-sm"
      onClick={onClose}
    >
      <div
        className="h-full w-full max-w-md overflow-y-auto border-l border-white/10 bg-slate-900 p-5"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="mb-5 flex items-center justify-between">
          <h2 className="text-lg font-semibold">{t(lang, 'alertHistory')}</h2>
          <button
            onClick={onClose}
            className="rounded-full p-1 text-slate-400 transition hover:bg-white/10 hover:text-white"
          >
            ✕
          </button>
        </div>
        {history.length === 0 ? (
          <p className="text-sm text-slate-400">{t(lang, 'noHistory')}</p>
        ) : (
          <ul className="space-y-3">
            {history.map((h) => (
              <li key={h.id} className={`rounded-2xl border p-3 ${sevColor[h.severity] ?? sevColor.info}`}>
                <div className="mb-1 flex items-center gap-2">
                  <span className="text-sm font-semibold">{h.title}</span>
                  <span className="ml-auto text-[10px] text-slate-400">
                    {new Date(h.created_at).toLocaleString()}
                  </span>
                </div>
                <p className="text-xs leading-relaxed opacity-90">{h.message}</p>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
