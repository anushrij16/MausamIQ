import { Sparkles, CheckCircle2 } from 'lucide-react';
import type { CopilotAdvice } from '@/lib/types';

interface Props {
  advice: CopilotAdvice | null;
}

export default function ActionCopilot({ advice }: Props) {
  return (
    <div className="rounded-3xl border border-white/10 bg-gradient-to-br from-teal-500/10 via-slate-900 to-slate-900 p-5">
      <div className="mb-4 flex items-center gap-2">
        <span className="text-xl">🤖</span>
        <h3 className="text-lg font-semibold text-white">Action Copilot</h3>
        <span className="ml-auto inline-flex items-center gap-1 rounded-full bg-teal-400/15 px-2 py-0.5 text-xs text-teal-300">
          <Sparkles className="h-3 w-3" />
          What should I do?
        </span>
      </div>

      {!advice ? (
        <p className="text-sm text-slate-400">Set your profile to get personalized actions.</p>
      ) : (
        <div>
          <p className="mb-3 text-sm font-medium text-slate-200">{advice.headline}</p>
          <ul className="space-y-2">
            {advice.actions.map((a, i) => (
              <li key={i} className="flex items-start gap-2 text-sm text-slate-200">
                <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0 text-teal-400" />
                <span>{a}</span>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}
