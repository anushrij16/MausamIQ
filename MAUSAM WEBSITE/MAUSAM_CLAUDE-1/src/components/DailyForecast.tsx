import type { WeatherData } from '@/lib/types';
import { weatherIcon, weatherCodeLabel } from '@/lib/weather';

interface Props {
  weather: WeatherData;
}

const DOW = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

export default function DailyForecast({ weather }: Props) {
  return (
    <div className="rounded-3xl border border-white/10 bg-slate-900 p-5">
      <h3 className="mb-3 text-sm font-medium text-slate-300">Next 7 days</h3>
      <div className="space-y-2">
        {weather.daily.map((d, i) => {
          const date = new Date(d.date);
          const dow = i === 0 ? 'Today' : DOW[date.getDay()];
          return (
            <div
              key={d.date}
              className="flex items-center gap-3 rounded-2xl bg-white/5 px-3 py-2"
            >
              <span className="w-12 text-sm text-slate-300">{dow}</span>
              <span className="text-xl">{weatherIcon(d.weatherCode, true)}</span>
              <span className="flex-1 truncate text-xs text-slate-400">
                {weatherCodeLabel(d.weatherCode)}
              </span>
              <span className="text-[11px] text-sky-300">{d.precipitationProbabilityMax}%</span>
              <span className="text-sm text-slate-300">
                <span className="text-white">{Math.round(d.tempMax)}°</span>
                <span className="mx-1 text-slate-500">/</span>
                {Math.round(d.tempMin)}°
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
}
