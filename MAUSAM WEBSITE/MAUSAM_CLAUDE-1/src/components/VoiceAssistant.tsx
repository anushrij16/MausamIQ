import { Mic, Square, Volume2, Loader2 } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';
import type { LangCode } from '@/lib/types';
import {
  isRecognitionSupported,
  isSpeechSupported,
  createRecognition,
  speak,
  stopSpeaking,
  type VoiceIntent,
} from '@/lib/voice';
import { t } from '@/lib/translations';
import type { WeatherData, ProfileType } from '@/lib/types';
import { answerQuestion } from '@/lib/chat';

interface Props {
  lang: LangCode;
  /** Speak a spoken summary of the current weather + alerts + advice */
  fullSpokenSummary: () => string;
  /** Speak only the active alerts */
  alertsSpokenSummary: () => string;
  /** Speak only the copilot advice */
  adviceSpokenSummary: () => string;
  /** Speak only current weather */
  weatherSpokenSummary: () => string;
  weather: WeatherData | null;
  profile: ProfileType;
}

export default function VoiceAssistant({
  lang,
  fullSpokenSummary,
  alertsSpokenSummary,
  adviceSpokenSummary,
  weatherSpokenSummary,
  weather,
  profile,
}: Props) {
  const [listening, setListening] = useState(false);
  const [speaking, setSpeaking] = useState(false);
  const [transcript, setTranscript] = useState('');
  const [intent, setIntent] = useState<VoiceIntent | null>(null);
  const [question, setQuestion] = useState('');
  const [answer, setAnswer] = useState('');
  const [answering, setAnswering] = useState(false);
  const recRef = useRef<ReturnType<typeof createRecognition>>(null);

  const recognitionOk = isRecognitionSupported();
  const speechOk = isSpeechSupported();

  // Warm up the voices list (Chrome loads voices asynchronously).
  useEffect(() => {
    if (speechOk) window.speechSynthesis.getVoices();
  }, [speechOk]);


  const askQuestion = async (text: string) => {
    const value = text.trim();
    if (!value) return;
    setQuestion(value);
    setAnswering(true);
    try {
      const result = await answerQuestion(value, lang, weather, profile);
      setAnswer(result);
      setSpeaking(true);
      await speak(result, lang);
      setSpeaking(false);
    } finally {
      setAnswering(false);
    }
  };

  const handleIntent = (i: VoiceIntent) => {
    setIntent(i);
    let text = '';
    switch (i) {
      case 'weather':
      case 'temperature':
        text = weatherSpokenSummary();
        break;
      case 'rain':
        text = weatherSpokenSummary();
        break;
      case 'alert':
        text = alertsSpokenSummary();
        break;
      case 'advice':
        text = adviceSpokenSummary();
        break;
      default:
        text = fullSpokenSummary();
    }
    void speak(text, lang).then(() => setSpeaking(false));
    setSpeaking(true);
  };

  const startListening = () => {
    if (!recognitionOk) return;
    const rec = createRecognition(lang);
    if (!rec) return;
    recRef.current = rec;
    setTranscript('');
    setIntent(null);
    setListening(true);
    rec.onresult = (event) => {
      const text = event.results[event.resultIndex][0].transcript;
      setTranscript(text);
      // Every spoken sentence goes through Sakhi's conversational answer engine.
      // This avoids limiting voice input to a small set of weather commands.
      void askQuestion(text);
    };
    rec.onend = () => setListening(false);
    rec.onerror = () => setListening(false);
    rec.start();
  };

  const stopListening = () => {
    recRef.current?.stop();
    setListening(false);
  };

  const handleSpeakAll = () => {
    if (speaking) {
      stopSpeaking();
      setSpeaking(false);
      return;
    }
    setSpeaking(true);
    void speak(fullSpokenSummary(), lang).then(() => setSpeaking(false));
  };

  return (
    <div className="rounded-3xl border border-white/10 bg-gradient-to-br from-violet-500/10 via-slate-900 to-slate-900 p-5">
      <div className="mb-4 flex items-center gap-2">
        <span className="text-xl">🎙️</span>
        <h3 className="text-lg font-semibold text-white">{t(lang, 'voiceAssistant')}</h3>
        <span className="ml-auto rounded-full bg-white/5 px-2 py-0.5 text-xs text-slate-400">
          {lang}
        </span>
      </div>

      {!recognitionOk && (
        <p className="mb-3 rounded-xl bg-amber-500/10 px-3 py-2 text-xs text-amber-300">
          {t(lang, 'voiceUnsupported')}
        </p>
      )}

      <div className="flex flex-wrap items-center gap-3">
        {/* Mic */}
        <button
          onClick={listening ? stopListening : startListening}
          disabled={!recognitionOk}
          className={`flex h-16 w-16 items-center justify-center rounded-full transition disabled:opacity-40 ${
            listening
              ? 'bg-red-500 text-white shadow-lg shadow-red-500/30 animate-pulse'
              : 'bg-gradient-to-br from-teal-400 to-sky-500 text-white hover:scale-105'
          }`}
          aria-label={t(lang, 'voiceListen')}
        >
          {listening ? <Square className="h-6 w-6" /> : <Mic className="h-7 w-7" />}
        </button>

        {/* Read aloud */}
        <button
          onClick={handleSpeakAll}
          disabled={!speechOk}
          className={`flex items-center gap-2 rounded-2xl border px-4 py-3 text-sm font-medium transition disabled:opacity-40 ${
            speaking
              ? 'border-teal-400 bg-teal-400/15 text-white'
              : 'border-white/10 bg-white/5 text-slate-200 hover:border-white/20'
          }`}
        >
          {speaking ? <Loader2 className="h-4 w-4 animate-spin" /> : <Volume2 className="h-4 w-4" />}
          {speaking ? t(lang, 'voiceStop') : t(lang, 'voiceSpeak')}
        </button>

        <div className="min-w-0 flex-1 text-sm text-slate-300">
          {listening ? (
            <span className="text-teal-300">{t(lang, 'voiceListening')}</span>
          ) : transcript ? (
            <span className="text-slate-200">
              &ldquo;{transcript}&rdquo;
              {intent && intent !== 'unknown' && (
                <span className="ml-2 rounded-full bg-white/5 px-2 py-0.5 text-xs text-slate-400">
                  {intent}
                </span>
              )}
            </span>
          ) : (
            <span className="text-slate-400">{t(lang, 'voiceListen')}</span>
          )}
        </div>
      </div>

      <div className="mt-4 border-t border-white/10 pt-4">
        <div className="mb-2 text-xs font-semibold uppercase tracking-wide text-slate-400">Chat with Sakhi</div>
        <div className="flex gap-2">
          <input
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
            onKeyDown={(e) => { if (e.key === 'Enter') void askQuestion(question); }}
            placeholder={lang.startsWith('ta') ? 'எதையும் கேளுங்கள்…' : lang.startsWith('hi') ? 'कुछ भी पूछें…' : 'Ask anything…'}
            className="min-w-0 flex-1 rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-white outline-none focus:border-teal-400"
          />
          <button onClick={() => void askQuestion(question)} disabled={answering || !question.trim()} className="rounded-2xl bg-teal-500 px-4 py-3 text-sm font-semibold disabled:opacity-40">
            {answering ? '…' : 'Ask'}
          </button>
        </div>
        {answer && <div className="mt-3 rounded-2xl bg-white/5 p-4 text-sm leading-6 text-slate-200">{answer}</div>}
      </div>
    </div>
  );
}
