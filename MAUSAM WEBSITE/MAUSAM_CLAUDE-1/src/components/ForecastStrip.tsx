import type { WeatherData } from '@/lib/types';
import { weatherIcon } from '@/lib/weather';

interface Props {
  weather: WeatherData;
}

export default function ForecastStrip({ weather }: Props) {
  const hours = weather.hourly.slice(0, 12);
  return (
    <div className="rounded-3xl border border-white/10 bg-slate-900 p-5">
      <h3 className="mb-3 text-sm font-medium text-slate-300">Next 24 hours</h3>
      <div className="flex gap-2 overflow-x-auto pb-1">
        {hours.map((h) => {
          const time = new Date(h.time);
          const label = `${time.getHours() % 12 || 12}${time.getHours() < 12 ? 'a' : 'p'}`;
          return (
            <div
              key={h.time}
              className="flex min-w-[58px] flex-col items-center gap-1 rounded-2xl bg-white/5 px-2 py-2"
            >
              <span className="text-xs text-slate-400">{label}</span>
              <span className="text-xl">{weatherIcon(h.weatherCode, true)}</span>
              <span className="text-sm font-medium text-white">{Math.round(h.temperature)}°</span>
              <span className="text-[10px] text-sky-300">{h.precipitationProbability}%</span>
            </div>
          );
        })}
      </div>
    </div>
  );
}
