import { Droplets, Wind, Thermometer } from 'lucide-react';
import type { WeatherData } from '@/lib/types';
import { weatherCodeLabel, weatherIcon } from '@/lib/weather';

interface Props {
  weather: WeatherData;
  cityName: string | null;
}

export default function WeatherHero({ weather, cityName }: Props) {
  const c = weather.current;
  const today = weather.daily[0];

  return (
    <div className="relative overflow-hidden rounded-3xl border border-white/10 bg-gradient-to-br from-sky-500/20 via-slate-900 to-slate-900 p-6">
      {/* decorative glow */}
      <div className="pointer-events-none absolute -right-16 -top-16 h-48 w-48 rounded-full bg-sky-500/20 blur-3xl" />
      <div className="pointer-events-none absolute -bottom-20 -left-10 h-40 w-40 rounded-full bg-teal-500/15 blur-3xl" />

      <div className="relative flex items-start justify-between">
        <div>
          <p className="text-sm text-slate-300">{cityName ?? weather.timezone}</p>
          <div className="mt-1 flex items-end gap-3">
            <span className="text-6xl leading-none">{weatherIcon(c.weatherCode, c.isDay)}</span>
            <span className="text-5xl font-light text-white">{Math.round(c.temperature)}°</span>
          </div>
          <p className="mt-2 text-lg text-slate-200">{weatherCodeLabel(c.weatherCode)}</p>
        </div>
        <div className="text-right text-sm text-slate-300">
          <p>H: {Math.round(today.tempMax)}°</p>
          <p>L: {Math.round(today.tempMin)}°</p>
        </div>
      </div>

      <div className="relative mt-6 grid grid-cols-3 gap-3">
        <Stat icon={<Thermometer className="h-4 w-4" />} label="Feels like" value={`${Math.round(c.apparentTemperature)}°`} />
        <Stat icon={<Droplets className="h-4 w-4" />} label="Humidity" value={`${c.humidity}%`} />
        <Stat icon={<Wind className="h-4 w-4" />} label="Wind" value={`${Math.round(c.windSpeed)} km/h`} />
      </div>
    </div>
  );
}

function Stat({ icon, label, value }: { icon: React.ReactNode; label: string; value: string }) {
  return (
    <div className="rounded-2xl border border-white/10 bg-white/5 px-3 py-2.5">
      <div className="flex items-center gap-1.5 text-slate-400">
        {icon}
        <span className="text-xs">{label}</span>
      </div>
      <p className="mt-1 text-base font-medium text-white">{value}</p>
    </div>
  );
}
