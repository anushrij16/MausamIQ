import type { LangCode } from './types';

// Web Speech API helpers — no external dependency.

export function isSpeechSupported(): boolean {
  return typeof window !== 'undefined' && 'speechSynthesis' in window;
}

export function isRecognitionSupported(): boolean {
  return (
    typeof window !== 'undefined' &&
    ('SpeechRecognition' in window || 'webkitSpeechRecognition' in window)
  );
}

interface SpeechRecognitionLike {
  lang: string;
  continuous: boolean;
  interimResults: boolean;
  start: () => void;
  stop: () => void;
  abort: () => void;
  onresult: ((event: SpeechRecognitionEventLike) => void) | null;
  onend: (() => void) | null;
  onerror: ((event: { error: string }) => void) | null;
}

interface SpeechRecognitionEventLike {
  results: { 0: { transcript: string }; length: number }[];
  resultIndex: number;
}

const RecognitionCtor = (): (new () => SpeechRecognitionLike) | null => {
  if (typeof window === 'undefined') return null;
  const w = window as unknown as Record<string, unknown>;
  return (w.SpeechRecognition || w.webkitSpeechRecognition) as (new () => SpeechRecognitionLike) | null;
};

export function createRecognition(lang: LangCode): SpeechRecognitionLike | null {
  const Ctor = RecognitionCtor();
  if (!Ctor) return null;
  const rec = new Ctor();
  rec.lang = lang;
  rec.continuous = false;
  rec.interimResults = false;
  return rec;
}

// Speak a string in the given language; resolves when speech ends or is cancelled.
export function speak(text: string, lang: LangCode): Promise<void> {
  if (!isSpeechSupported()) return Promise.resolve();
  return new Promise((resolve) => {
    window.speechSynthesis.cancel();
    const utter = new SpeechSynthesisUtterance(text);
    utter.lang = lang;
    utter.rate = 0.95;
    utter.pitch = 1;

    // Prefer a matching-language voice for clearer pronunciation.
    const chooseVoice = () => {
      const voices = window.speechSynthesis.getVoices();
      const base = lang.split('-')[0].toLowerCase();
      const match =
        voices.find((v) => v.lang.toLowerCase() === lang.toLowerCase()) ||
        voices.find((v) => v.lang.toLowerCase().startsWith(base));
      if (match) utter.voice = match;
      window.speechSynthesis.speak(utter);
    };

    utter.onend = () => resolve();
    utter.onerror = () => resolve();
    const voices = window.speechSynthesis.getVoices();
    if (voices.length) chooseVoice();
    else {
      const onVoices = () => {
        window.speechSynthesis.removeEventListener('voiceschanged', onVoices);
        chooseVoice();
      };
      window.speechSynthesis.addEventListener('voiceschanged', onVoices);
      window.setTimeout(() => {
        window.speechSynthesis.removeEventListener('voiceschanged', onVoices);
        if (!window.speechSynthesis.speaking) chooseVoice();
      }, 800);
    }
  });
}

export function stopSpeaking(): void {
  if (isSpeechSupported()) window.speechSynthesis.cancel();
}

// Heuristic command interpretation — recognizes spoken weather queries
// in Hindi + English and returns a normalized intent.
export type VoiceIntent = 'weather' | 'rain' | 'temperature' | 'alert' | 'advice' | 'unknown';

export function interpretCommand(transcript: string): VoiceIntent {
  const s = transcript.toLowerCase();
  if (/(बारिश|rain|バーりシ|मौसम|mausam|weather|आबहवा|आबहवान)/.test(s)) {
    if (/(बारिश|rain|पावस)/.test(s)) return 'rain';
    return 'weather';
  }
  if (/(तापमान|temperature|ताप|गरमी|heat|गर्मी)/.test(s)) return 'temperature';
  if (/(चेतावनी|alert|warning|warning|सतर्क|जोखिम|risk)/.test(s)) return 'alert';
  if (/(क्या कर|what should|advice|सलाह|करना|karoon|करूँ)/.test(s)) return 'advice';
  return 'unknown';
}
