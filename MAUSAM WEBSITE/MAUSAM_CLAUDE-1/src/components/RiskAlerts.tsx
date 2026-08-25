import { Droplets, Wind, Thermometer, CloudRain, Eye } from 'lucide-react';
import type { Alert, WeatherEvent } from '@/lib/types';
import { severityColor, eventIcon } from '@/lib/riskEngine';

interface Props {
  alerts: Alert[];
}

const EVENT_META: Record<WeatherEvent, { label: string }> = {
  rain: { label: 'Rain' },
  heat: { label: 'Heat' },
  storm: { label: 'Storm' },
  wind: { label: 'Wind' },
  cold: { label: 'Cold' },
  fog: { label: 'Fog' },
};

const SEV_LABEL: Record<string, string> = {
  extreme: 'EXTREME',
  severe: 'SEVERE',
  moderate: 'MODERATE',
  info: 'INFO',
};

export default function RiskAlerts({ alerts }: Props) {
  if (alerts.length === 0) {
    return (
      <div className="rounded-3xl border border-white/10 bg-slate-900 p-5">
        <div className="mb-3 flex items-center gap-2">
          <span className="text-xl">🚨</span>
          <h3 className="text-lg font-semibold text-white">Risk Alerts</h3>
        </div>
        <div className="flex items-center gap-3 rounded-2xl bg-emerald-500/10 px-4 py-4 text-emerald-300">
          <Eye className="h-5 w-5" />
          <span className="text-sm">No active warnings for your area right now.</span>
        </div>
      </div>
    );
  }

  return (
    <div className="rounded-3xl border border-white/10 bg-slate-900 p-5">
      <div className="mb-4 flex items-center gap-2">
        <span className="text-xl">🚨</span>
        <h3 className="text-lg font-semibold text-white">Risk Alerts</h3>
        <span className="ml-auto rounded-full bg-white/5 px-2 py-0.5 text-xs text-slate-400">
          {alerts.length}
        </span>
      </div>

      <div className="space-y-3">
        {alerts.map((a) => (
          <div
            key={a.id}
            className={`rounded-2xl border p-4 ${severityColor(a.severity)}`}
          >
            <div className="mb-1 flex items-center gap-2">
              <span className="text-lg">{eventIcon(a.weatherEvent)}</span>
              <span className="font-semibold">{a.title}</span>
              <span className="ml-auto rounded-full border border-current px-2 py-0.5 text-[10px] font-bold tracking-wider">
                {SEV_LABEL[a.severity]}
              </span>
            </div>
            <p className="text-sm leading-relaxed opacity-90">{a.message}</p>
            <div className="mt-2 flex items-center gap-2 text-[11px] opacity-60">
              {a.weatherEvent === 'rain' && <CloudRain className="h-3 w-3" />}
              {a.weatherEvent === 'heat' && <Thermometer className="h-3 w-3" />}
              {a.weatherEvent === 'storm' && <CloudRain className="h-3 w-3" />}
              {a.weatherEvent === 'wind' && <Wind className="h-3 w-3" />}
              {a.weatherEvent === 'cold' && <Thermometer className="h-3 w-3" />}
              {a.weatherEvent === 'fog' && <Droplets className="h-3 w-3" />}
              {EVENT_META[a.weatherEvent].label}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
