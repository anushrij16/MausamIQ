import { useState } from 'react';
import { CloudSun, LockKeyhole, Mail, Phone, Loader2 } from 'lucide-react';

interface Props { onLogin: (identifier: string) => void; }

function validIdentifier(value: string) {
  const v = value.trim();
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) || /^\+?[0-9][0-9\s-]{7,14}$/.test(v);
}

export default function LoginPage({ onLogin }: Props) {
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validIdentifier(identifier) || password.trim().length < 4) {
      setError('Enter a valid phone number or email and a password (minimum 4 characters).');
      return;
    }
    setLoading(true);
    window.setTimeout(() => {
      const value = identifier.trim();
      sessionStorage.setItem('mausam_logged_in', '1');
      sessionStorage.setItem('mausam_user_identifier', value);
      onLogin(value);
      setLoading(false);
    }, 250);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center px-4">
      <div className="w-full max-w-md rounded-3xl border border-white/10 bg-slate-900 p-7 shadow-2xl">
        <div className="mb-7 flex items-center gap-3">
          <div className="rounded-2xl bg-teal-400/15 p-3"><CloudSun className="h-8 w-8 text-teal-400" /></div>
          <div><h1 className="text-xl font-semibold">MAUSAM</h1><p className="text-xs text-slate-400">Sign in to continue</p></div>
        </div>
        <form onSubmit={submit} className="space-y-4">
          <div>
            <label className="mb-2 block text-sm text-slate-300">Phone number or email</label>
            <div className="relative"><Mail className="absolute left-3 top-3 h-4 w-4 text-slate-400" /><input type="text" value={identifier} onChange={e=>setIdentifier(e.target.value)} placeholder="Phone number or email" className="w-full rounded-2xl border border-white/10 bg-white/5 py-3 pl-10 pr-3 outline-none focus:border-teal-400" /></div>
            <p className="mt-1 text-[11px] text-slate-500"><Phone className="mr-1 inline h-3 w-3" />Use either one.</p>
          </div>
          <div>
            <label className="mb-2 block text-sm text-slate-300">Password</label>
            <div className="relative"><LockKeyhole className="absolute left-3 top-3 h-4 w-4 text-slate-400" /><input type="password" value={password} onChange={e=>setPassword(e.target.value)} placeholder="Password" className="w-full rounded-2xl border border-white/10 bg-white/5 py-3 pl-10 pr-3 outline-none focus:border-teal-400" /></div>
          </div>
          {error && <p className="text-sm text-red-300">{error}</p>}
          <button disabled={loading} className="flex w-full items-center justify-center gap-2 rounded-2xl bg-gradient-to-r from-teal-500 to-sky-500 py-3 font-semibold disabled:opacity-60">
            {loading && <Loader2 className="h-4 w-4 animate-spin" />}Continue
          </button>
        </form>
      </div>
    </div>
  );
}
