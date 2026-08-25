import type { LangCode, WeatherData, ProfileType } from './types';
import { weatherCodeLabel, searchLocations, fetchWeather } from './weather';
import { bestWindow, closestHour, formatHourWord, formatWindowWord, isRainingNow, parseClockTime, soilMoistureBucket } from './advisor';

const AI_URL = (import.meta.env.VITE_AI_API_URL as string | undefined) || 'https://text.pollinations.ai/openai';
const AI_KEY = import.meta.env.VITE_AI_API_KEY as string | undefined;

function languageName(lang: LangCode) {
  if (lang.startsWith('ta')) return 'Tamil';
  if (lang.startsWith('hi')) return 'Hindi';
  return 'English';
}

function context(weather: WeatherData | null, profile: ProfileType, destinationWeather?: WeatherData | null, destinationName?: string) {
  if (!weather) return `Profile: ${profile}. Weather data is currently unavailable.`;
  const c = weather.current;
  const d = weather.daily[0];
  const tomorrow = weather.daily[1];
  const parts = [
    `Profile: ${profile}.`,
    `Current location weather: ${weatherCodeLabel(c.weatherCode)}, ${Math.round(c.temperature)}°C, feels ${Math.round(c.apparentTemperature)}°C, humidity ${c.humidity}%, wind ${Math.round(c.windSpeed)} km/h.`,
    `Today: high ${Math.round(d.tempMax)}°C, low ${Math.round(d.tempMin)}°C, rain ${Math.round(d.precipitationProbabilityMax)}%, rainfall ${d.precipitationSum} mm, max wind ${Math.round(d.windSpeedMax)} km/h.`,
    tomorrow ? `Tomorrow at current location: high ${Math.round(tomorrow.tempMax)}°C, low ${Math.round(tomorrow.tempMin)}°C, rain ${Math.round(tomorrow.precipitationProbabilityMax)}%, rainfall ${tomorrow.precipitationSum} mm, max wind ${Math.round(tomorrow.windSpeedMax)} km/h.` : '',
  ];
  if (destinationWeather && destinationName) {
    const dc = destinationWeather.current;
    const dd = destinationWeather.daily[1] ?? destinationWeather.daily[0];
    parts.push(`Destination ${destinationName}: tomorrow high ${Math.round(dd.tempMax)}°C, low ${Math.round(dd.tempMin)}°C, rain ${Math.round(dd.precipitationProbabilityMax)}%, rainfall ${dd.precipitationSum} mm, max wind ${Math.round(dd.windSpeedMax)} km/h, current ${weatherCodeLabel(dc.weatherCode)}.`);
  }
  return parts.filter(Boolean).join(' ');
}

function findDestination(question: string): string | null {
  const q = question.toLowerCase();
  if (/வால்பாறை|valparai|वालपराई|वलपारै/.test(q)) return 'Valparai';
  const patterns = [
    /\b(?:go|going|travel|travelling|visit|visiting|to)\s+(?:to\s+)?([a-z][a-z .'-]{2,30})/i,
    /(?:போக|செல்ல|பயணம்|போகலாம்)\s+([\u0B80-\u0BFF][\u0B80-\u0BFF .'-]{1,25})/u,
    /(?:जाना|जाऊ|यात्रा|घूमने)\s+(?:के लिए\s+)?([\u0900-\u097F][\u0900-\u097F .'-]{1,25})/u,
  ];
  for (const pattern of patterns) {
    const match = q.match(pattern);
    if (match?.[1]) {
      const candidate = match[1].replace(/[?.!,]+$/, '').trim();
      if (candidate && candidate.length <= 30) return candidate;
    }
  }
  return null;
}

function localFallback(question: string, lang: LangCode, weather: WeatherData | null, destinationWeather: WeatherData | null, destinationName: string | null) {
  const q = question.toLowerCase().trim();
  const isTa = lang.startsWith('ta');
  const isHi = lang.startsWith('hi');
  const tomorrow = weather?.daily[1] ?? weather?.daily[0];
  const destinationTomorrow = destinationWeather?.daily[1] ?? destinationWeather?.daily[0];

  if (/^(hi|hello|hey|வணக்கம்|ஹாய்|नमस्ते|हाय)\b/.test(q)) {
    return isTa ? 'வணக்கம்! நான் மௌசம் சகி. என்ன கேட்க விரும்புகிறீர்கள்?' : isHi ? 'नमस्ते! मैं मौसम सखी हूँ। आप क्या पूछना चाहते हैं?' : 'Hello! I am Mausam Sakhi. What would you like to know?';
  }
  if (/thank|thanks|நன்றி|धन्यवाद/.test(q)) {
    return isTa ? 'மகிழ்ச்சி! இன்னும் ஏதாவது உதவி வேண்டுமென்றால் கேளுங்கள்.' : isHi ? 'खुशी हुई! अगर और मदद चाहिए तो पूछिए।' : 'You’re welcome! Ask me anything else whenever you need help.';
  }
  if (/who are you|உன்னைப் பற்றி|நீ யார்|तुम कौन|आप कौन/.test(q)) {
    return isTa ? 'நான் மௌசம் சகி. வானிலை, பயணம், தினசரி திட்டம் மற்றும் பொதுவான கேள்விகளில் உதவும் உங்கள் குரல் உதவியாளர்.' : isHi ? 'मैं मौसम सखी हूँ। मैं मौसम, यात्रा, रोज़मर्रा की योजना और सामान्य सवालों में आपकी मदद करती हूँ।' : 'I am Mausam Sakhi, your conversational assistant for weather, travel, daily planning and general questions.';
  }
  if (/what can you do|என்ன செய்ய முடியும்|என்னென்ன செய்ய|क्या कर सकती|क्या कर सकते/.test(q)) {
    return isTa ? 'நீங்கள் என்ன வேண்டுமானாலும் கேட்கலாம். வானிலை மற்றும் பயண முடிவுகளில் தற்போதைய தரவைப் பயன்படுத்தி உதவுவேன்; மற்ற கேள்விகளுக்கும் உரையாடல் முறையில் பதில் அளிப்பேன்.' : isHi ? 'आप मुझसे कुछ भी पूछ सकते हैं। मौसम और यात्रा के लिए मैं उपलब्ध मौसम डेटा का उपयोग करके मदद करूँगी और बाकी सवालों का भी बातचीत के रूप में जवाब दूँगी।' : 'You can ask me anything. For weather and travel I use the available forecast data, and for other topics I will answer conversationally.';
  }

  // Simple arithmetic without eval.
  const math = q.match(/^\s*(\d+(?:\.\d+)?)\s*([+\-*/])\s*(\d+(?:\.\d+)?)\s*\??\s*$/);
  if (math) {
    const a = Number(math[1]), b = Number(math[3]);
    const value = math[2] === '+' ? a + b : math[2] === '-' ? a - b : math[2] === '*' ? a * b : b === 0 ? null : a / b;
    if (value !== null) return isTa ? `பதில் ${value}.` : isHi ? `उत्तर ${value} है।` : `The answer is ${value}.`;
  }

  if (!weather) {
    return isTa ? 'நிச்சயமாக உதவுகிறேன். தற்போதைய வானிலை தரவு மட்டும் இப்போது கிடைக்கவில்லை; உங்கள் கேள்வியை மீண்டும் கேளுங்கள்.' : isHi ? 'ज़रूर, मैं मदद करूँगी। अभी मौसम का डेटा उपलब्ध नहीं है, लेकिन आप अपना सवाल पूछ सकते हैं।' : 'Absolutely, I can help. Current weather data is unavailable right now, but you can still ask your question.';
  }

  // Farmer: soil moisture.
  if (/soil moisture|soil condition|மண் ஈரப்பதம்|மண்ணின் ஈரப்பதம்|मिट्टी की नमी|मिट्टी में नमी/.test(q)) {
    const sm = weather.hourly[0]?.soilMoisture;
    if (sm === undefined) {
      return isTa
        ? 'மண் ஈரப்பதத் தரவு தற்போது இந்த இடத்திற்கு கிடைக்கவில்லை. சமீபத்திய மழையைப் பொறுத்து மதிப்பிடலாம்.'
        : isHi
          ? 'इस स्थान के लिए मिट्टी की नमी का डेटा अभी उपलब्ध नहीं है। हाल की बारिश के आधार पर अंदाज़ा लगाया जा सकता है।'
          : 'Soil moisture data is not available for this location right now. It can be estimated from recent rainfall instead.';
    }
    const bucket = soilMoistureBucket(sm, lang);
    const pct = Math.round(sm * 100);
    if (isTa) return `மேல் மண் அடுக்கில் தோராயமான ஈரப்பதம் ${pct}% (${bucket}). ${bucket === 'வறண்டது' ? 'நீர்ப்பாசனம் செய்யலாம்.' : bucket === 'ஈரமானது' ? 'இப்போது நீர்ப்பாசனம் தேவையில்லை.' : 'நிலைமையை கண்காணித்து தேவைப்படின் நீர்ப்பாசனம் செய்யலாம்.'}`;
    if (isHi) return `ऊपरी मिट्टी में अनुमानित नमी ${pct}% है (${bucket})। ${bucket === 'सूखी' ? 'सिंचाई करना उचित रहेगा।' : bucket === 'गीली' ? 'अभी सिंचाई की आवश्यकता नहीं है।' : 'स्थिति पर नज़र रखें और ज़रूरत पड़ने पर सिंचाई करें।'}`;
    return `Estimated topsoil moisture is about ${pct}% (${bucket}). ${bucket === 'dry' ? 'Irrigation would help.' : bucket === 'wet' ? 'No irrigation needed right now.' : 'Keep monitoring and irrigate if it stays dry.'}`;
  }

  // Farmer: best time for crop / field work.
  if (/\bcrop|field work|sowing|harvest|spray|pesticide|farming|பயிர்|விதைப்பு|அறுவடை|விவசாயம்|வயல்|फसल|खेत|बुवाई|कटाई|छिड़काव|खेती/.test(q)) {
    const win = bestWindow(weather.hourly, { idealTempMin: 18, idealTempMax: 32, hourRange: [5, 18], maxWind: 25 });
    if (win) {
      const w = formatWindowWord(win.startHour, win.endHour, lang);
      if (isTa) return `பயிர் வேலைகளுக்கு ${w} நல்ல நேரமாக இருக்கும். மழை வாய்ப்பு ${win.avgRain}%, வெப்பநிலை சுமார் ${win.avgTemp}°C. ${win.hasStorm ? 'இடி மின்னலுக்கு கவனமாக இருங்கள்.' : 'மதிய வெப்பத்தை தவிர்க்கவும்.'}`;
      if (isHi) return `खेत के काम के लिए ${w} अच्छा समय रहेगा। बारिश की संभावना ${win.avgRain}% है और तापमान लगभग ${win.avgTemp}°C रहेगा। ${win.hasStorm ? 'गरज-चमक से सावधान रहें।' : 'दोपहर की गर्मी से बचें।'}`;
      return `${w} would be a good window for field work. Rain probability is ${win.avgRain}% and temperature around ${win.avgTemp}°C. ${win.hasStorm ? 'Watch out for possible thunderstorms.' : 'Avoid the hottest midday hours.'}`;
    }
  }

  // Parent: laundry / drying clothes.
  if (/wash clothes|washing clothes|\blaundry\b|dry clothes|clothes dry|shall i wash|துணி.*(துவை|காய)|காய வை|कपड़े धोना|कपड़े सुखाना|कपड़े धोने/.test(q)) {
    const next6 = weather.hourly.slice(0, 6);
    const avgRain = Math.round(next6.reduce((s, e) => s + e.precipitationProbability, 0) / next6.length);
    const anyStorm = next6.some((e) => [95, 96, 99].includes(e.weatherCode));
    if (avgRain < 35 && !anyStorm) {
      if (isTa) return `ஆம், இப்போது துணி துவைக்கலாம். அடுத்த சில மணி நேரங்களில் மழை வாய்ப்பு சராசரியாக ${avgRain}% மட்டுமே, எனவே காயவைக்க ஏற்றது.`;
      if (isHi) return `हाँ, अभी कपड़े धो सकते हैं। अगले कुछ घंटों में बारिश की औसत संभावना केवल ${avgRain}% है, इसलिए सूखने के लिए अच्छा समय है।`;
      return `Yes, it's a good time to wash clothes now. Rain probability over the next few hours averages only ${avgRain}%, so they should dry well.`;
    }
    const win = bestWindow(weather.hourly, { idealTempMin: 22, idealTempMax: 38, hourRange: [7, 17], maxWind: 30, preferWind: true });
    if (win && win.avgRain < 35) {
      const w = formatWindowWord(win.startHour, win.endHour, lang);
      if (isTa) return `இப்போது துவைப்பதைத் தவிர்க்கவும் - அடுத்த சில மணி நேரங்களில் மழை வாய்ப்பு ${avgRain}%. ${w} நேரத்தில் முயற்சி செய்யலாம், அப்போது வானிலை உலர்ந்திருக்கும்.`;
      if (isHi) return `अभी धोने से बचें - अगले कुछ घंटों में बारिश की संभावना ${avgRain}% है। ${w} के आसपास प्रयास करें, तब मौसम सूखा रहेगा।`;
      return `I'd hold off for now - rain probability over the next few hours is ${avgRain}%. Try around ${w} instead, when it should be drier.`;
    }
    if (isTa) return `அடுத்த சில மணி நேரங்களில் மழை வாய்ப்பு ${avgRain}% உள்ளது, எனவே துணி காயவைக்க கடினமாக இருக்கலாம். வெளியே காயவைப்பதை தள்ளி வைப்பது நல்லது.`;
    if (isHi) return `अगले कुछ घंटों में बारिश की संभावना ${avgRain}% है, इसलिए कपड़े सुखाना मुश्किल हो सकता है। बाहर सुखाना टालना बेहतर होगा।`;
    return `Rain probability over the next few hours is ${avgRain}%, so drying outdoors will be difficult. It's best to hold off or dry indoors today.`;
  }

  // Parent / fitness: gym or workout right now — uses current conditions, not tomorrow.
  if (/gym.{0,10}(out|now)|work.?out now|exercise now|இப்போது.*(ஜிம்|பயிற்சி)|अभी.*(जिम|व्यायाम|एक्सरसाइज़)/.test(q)) {
    const c = weather.current;
    if (isRainingNow(weather)) {
      if (isTa) return 'இப்போது வெளியில் மழை/ஈரமான வானிலை உள்ளது, எனவே வெளியே ஜிம்/பயிற்சி தவிர்க்கவும். உள்ளே பயிற்சி செய்வது நல்லது.';
      if (isHi) return 'अभी बाहर बारिश/नमी वाला मौसम है, इसलिए बाहर जिम/व्यायाम टालें। घर के अंदर व्यायाम करना बेहतर रहेगा।';
      return "It's currently rainy or wet outside, so I'd skip the outdoor gym for now. An indoor workout would be the safer choice.";
    }
    if (c.temperature >= 36) {
      if (isTa) return `இப்போது வெப்பநிலை ${Math.round(c.temperature)}°C - வெளியே பயிற்சி செய்ய அதிக வெப்பமாக உள்ளது. குளிர்ந்த நேரம் அல்லது உள்ளே பயிற்சி செய்வது நல்லது.`;
      if (isHi) return `अभी तापमान ${Math.round(c.temperature)}°C है - बाहर व्यायाम के लिए काफी गर्मी है। ठंडे समय पर या घर के अंदर व्यायाम करें।`;
      return `It's ${Math.round(c.temperature)}°C right now - a bit too hot for an outdoor session. Best to wait for a cooler time or work out indoors.`;
    }
    if (isTa) return `ஆம், இப்போது வெளியே ஜிம்/பயிற்சிக்கு ஏற்ற வானிலை உள்ளது. வெப்பநிலை ${Math.round(c.temperature)}°C. நீரூட்டத்துடன் இருங்கள்.`;
    if (isHi) return `हाँ, अभी बाहर जिम/व्यायाम के लिए मौसम ठीक है। तापमान ${Math.round(c.temperature)}°C है। पानी पीते रहें।`;
    return `Yes, conditions look fine for an outdoor gym session right now. Temperature is ${Math.round(c.temperature)}°C. Stay hydrated.`;
  }

  // Surfer: best time to surf (wind/storm based — no swell data available).
  if (/\bsurf|surfing|கடல் சறுக்கு|சர்ஃபிங்|सर्फिंग|सर्फ करना/.test(q)) {
    const win = bestWindow(weather.hourly, { idealTempMin: 20, idealTempMax: 34, hourRange: [6, 18], maxWind: 35, preferWind: true });
    const note = isTa
      ? ' (குறிப்பு: இந்த பரிந்துரை காற்று மற்றும் புயல் நிலையை மட்டும் அடிப்படையாகக் கொண்டது; அலை/சுவெல் தகவலுக்கு உள்ளூர் சர்ஃப் அறிக்கையையும் பார்க்கவும்.)'
      : isHi
        ? ' (नोट: यह सुझाव केवल हवा और तूफान की स्थिति पर आधारित है; लहर/स्वेल जानकारी के लिए स्थानीय सर्फ रिपोर्ट भी देखें।)'
        : ' (Note: this is based on wind and storm conditions only — check a local surf/swell report for wave height too.)';
    if (win) {
      const w = formatWindowWord(win.startHour, win.endHour, lang);
      if (isTa) return `இன்று ${w} நேரம் சர்ஃபிங்கிற்கு ஏற்றதாக இருக்கும். காற்று வேகம் சுமார் ${win.avgWind} கி.மீ/மணி, மழை வாய்ப்பு ${win.avgRain}%.${note}`;
      if (isHi) return `आज ${w} सर्फिंग के लिए अच्छा समय रहेगा। हवा की गति लगभग ${win.avgWind} किमी/घंटा है और बारिश की संभावना ${win.avgRain}%।${note}`;
      return `${w} today looks like a reasonable window for surfing. Wind is around ${win.avgWind} km/h and rain probability is ${win.avgRain}%.${note}`;
    }
    if (isTa) return `இன்று வானிலை நிலைமைகள் நிச்சயமற்றவை.${note}`;
    if (isHi) return `आज मौसम की स्थिति अनिश्चित है।${note}`;
    return `Conditions look uncertain today.${note}`;
  }

  // Traveler: a specific clock time was mentioned — check that exact time, and offer a better one.
  const requestedHour = parseClockTime(q);
  if (requestedHour !== null && /travel|trip|journey|drive|driving|பயணம்|போக|செல்ல|यात्रा|जाना|सफर/.test(q)) {
    const hourly = destinationWeather?.hourly ?? weather.hourly;
    const place = destinationName ?? (isTa ? 'உங்கள் இடம்' : isHi ? 'आपके स्थान' : 'your location');
    const at = closestHour(hourly, requestedHour);
    if (at) {
      const win = bestWindow(hourly, { idealTempMin: 15, idealTempMax: 35, maxWind: 40 });
      const risky = at.precipitationProbability >= 55 || [95, 96, 99].includes(at.weatherCode);
      const atLabel = formatHourWord(requestedHour, lang);
      const betterLabel = win ? formatWindowWord(win.startHour, win.endHour, lang) : null;
      if (isTa) {
        if (risky) return `${atLabel} அளவில் ${place} பயணத்தில் கவனம் தேவை - மழை வாய்ப்பு ${Math.round(at.precipitationProbability)}%.${betterLabel ? ` பதிலாக ${betterLabel} நேரத்தை பரிசீலிக்கவும், அப்போது வானிலை சிறப்பாக இருக்கும் (மழை வாய்ப்பு ${win!.avgRain}%).` : ''}`;
        return `ஆம், ${atLabel} அளவில் பயணம் செய்யலாம். மழை வாய்ப்பு ${Math.round(at.precipitationProbability)}% மட்டுமே, வெப்பநிலை சுமார் ${Math.round(at.temperature)}°C.`;
      }
      if (isHi) {
        if (risky) return `${atLabel} पर ${place} की यात्रा में सावधानी बरतें - बारिश की संभावना ${Math.round(at.precipitationProbability)}% है।${betterLabel ? ` इसके बजाय ${betterLabel} का समय बेहतर रहेगा, जब मौसम अच्छा होगा (बारिश की संभावना ${win!.avgRain}%)।` : ''}`;
        return `हाँ, ${atLabel} पर यात्रा की जा सकती है। बारिश की संभावना केवल ${Math.round(at.precipitationProbability)}% है और तापमान लगभग ${Math.round(at.temperature)}°C रहेगा।`;
      }
      if (risky) return `I'd be cautious about travelling to ${place} around ${atLabel} - rain probability is ${Math.round(at.precipitationProbability)}%.${betterLabel ? ` Consider ${betterLabel} instead, when conditions look better (rain probability ${win!.avgRain}%).` : ''}`;
      return `Yes, travelling around ${atLabel} should be fine. Rain probability is only ${Math.round(at.precipitationProbability)}% and temperature is about ${Math.round(at.temperature)}°C.`;
    }
  }

  if (destinationWeather && destinationTomorrow && destinationName && /travel|trip|go|going|visit|walk|walking|outing|outside|morning|பயணம்|போக|செல்ல|நடக்க|நடப்பு|வெளியே|சுற்றுலா|யாத்திரை|यात्रा|जाना|चलना|बाहर|घूम/.test(q)) {
    const rain = Math.round(destinationTomorrow.precipitationProbabilityMax);
    const hot = destinationTomorrow.tempMax >= 36;
    const storm = [95, 96, 99].includes(destinationTomorrow.weatherCode);
    if (isTa) {
      if (storm || rain >= 70) return `நாளை ${destinationName} செல்லும் திட்டத்தில் கவனம் தேவை. மழை வாய்ப்பு ${rain}%. ${storm ? 'இடி மின்னல் வாய்ப்பும் உள்ளது. ' : ''}பயணத்தை தாமதிப்பது அல்லது வானிலை மேம்பட்ட பிறகு செல்லுவது பாதுகாப்பானது.`;
      return `${destinationName}க்கு நாளை செல்லலாம். மழை வாய்ப்பு ${rain}%, வெப்பநிலை ${Math.round(destinationTomorrow.tempMin)}°C முதல் ${Math.round(destinationTomorrow.tempMax)}°C வரை.${hot ? ' வெப்பம் அதிகமாக இருக்கலாம், தண்ணீர் அதிகம் எடுத்துச் செல்லுங்கள்.' : ''} குடை/மழைக்கோட்டை எடுத்துச் செல்வது நல்லது.`;
    }
    if (isHi) {
      if (storm || rain >= 70) return `कल ${destinationName} जाने की योजना में सावधानी रखें। बारिश की संभावना ${rain}% है। ${storm ? 'गरज-चमक की संभावना भी है। ' : ''}यात्रा टालना या मौसम सुधरने के बाद जाना बेहतर होगा।`;
      return `${destinationName} की कल यात्रा की जा सकती है। बारिश की संभावना ${rain}% है और तापमान ${Math.round(destinationTomorrow.tempMin)}°C से ${Math.round(destinationTomorrow.tempMax)}°C रहेगा।${hot ? ' गर्मी अधिक हो सकती है, पानी साथ रखें।' : ''} छाता या रेनकोट साथ रखें।`;
    }
    if (storm || rain >= 70) return `I would be cautious about travelling to ${destinationName} tomorrow. Rain probability is ${rain}%. ${storm ? 'Thunderstorms are also possible. ' : ''}Consider delaying the trip if conditions worsen.`;
    return `You can plan a trip to ${destinationName} tomorrow. Rain probability is ${rain}% and temperatures should be ${Math.round(destinationTomorrow.tempMin)}–${Math.round(destinationTomorrow.tempMax)}°C.${hot ? ' It may be quite hot, so carry extra water.' : ''} Carry an umbrella or raincoat.`;
  }

  const rain = Math.round(tomorrow?.precipitationProbabilityMax ?? 0);
  const hot = (tomorrow?.tempMax ?? 0) >= 38;
  const storm = tomorrow ? [95, 96, 99].includes(tomorrow.weatherCode) : false;

  // General travel question where no specific destination could be extracted
  // (e.g. "shall I travel from here to here", "is it a good day to travel").
  if (!destinationWeather && /travel|trip|journey|road trip|பயணம்|சுற்றுலா|யாத்திரை|यात्रा|सफर/.test(q)) {
    const risky = rain >= 55 || hot || storm;
    if (isTa) return risky ? `நாளை பயணத்திற்கு கொஞ்சம் கவனம் தேவை. மழை வாய்ப்பு ${rain}%${hot ? ', வெப்பம் அதிகமாக இருக்கலாம்' : ''}${storm ? ', புயல்/இடி மின்னல் வாய்ப்பும் உள்ளது' : ''}. முடிந்தால் காலை நேரத்தைத் தேர்வு செய்யுங்கள்.` : `நாளை பயணம் செய்யலாம். மழை வாய்ப்பு ${rain}% மட்டுமே, வானிலை பொதுவாக நல்லதாக இருக்கும். குறிப்பிட்ட இடத்திற்குச் சொன்னால் இன்னும் துல்லியமாகச் சொல்ல முடியும்.`;
    if (isHi) return risky ? `कल यात्रा में थोड़ी सावधानी रखें। बारिश की संभावना ${rain}%${hot ? ' है और गर्मी अधिक हो सकती है' : ''}${storm ? ', तूफानी मौसम भी संभव है' : ''}। सुबह जल्दी निकलना बेहतर रहेगा।` : `कल यात्रा की जा सकती है। बारिश की संभावना केवल ${rain}% है और मौसम सामान्य रहने की उम्मीद है। अगर आप गंतव्य का नाम बताएं तो मैं और सटीक जानकारी दे सकती हूँ।`;
    return risky ? `I would be a little cautious about travelling tomorrow. Rain probability is ${rain}%${hot ? ' and it may be quite hot' : ''}${storm ? ', with possible stormy weather' : ''}. An early morning start would be safer.` : `Yes, tomorrow looks like a reasonable day to travel. Rain probability is only ${rain}% and conditions look generally fine. Tell me the destination and I can give you a more precise answer.`;
  }

  if (/walk|walking|jog|jogging|run\b|running|exercise|exercising|workout|work out|gym|cycle|cycling|sport|sports|நடக்க|நடப்ப|வாக்கிங்|ஓட்டம்|ஓடு|உடற்பயிற்சி|सुबह टहल|चलने|morning walk|காலை நடை|दौड़|व्यायाम|कसरत|एक्सरसाइज़/.test(q)) {
    // If it's raining right now, an indoor session is the clear recommendation.
    if (isRainingNow(weather)) {
      if (isTa) return 'இப்போது மழை/ஈரமான வானிலை உள்ளது, எனவே உள்ளே பயிற்சி செய்வது நல்லது - stretching, yoga அல்லது home workout முயற்சிக்கலாம்.';
      if (isHi) return 'अभी बारिश/नमी वाला मौसम है, इसलिए घर के अंदर व्यायाम करना बेहतर रहेगा - स्ट्रेचिंग, योग या होम वर्कआउट कर सकते हैं।';
      return "It's rainy or damp right now, so an indoor workout would be the better choice today — stretching, yoga, or a home routine work well.";
    }
    const win = bestWindow(weather.hourly, { idealTempMin: 16, idealTempMax: 28, hourRange: [5, 19], maxWind: 25 });
    if (win) {
      const w = formatWindowWord(win.startHour, win.endHour, lang);
      if (win.hasStorm || win.avgRain >= 55) {
        if (isTa) return `அடுத்த சில மணி நேரங்களில் மழை/புயல் வாய்ப்பு அதிகமாக உள்ளது (${win.avgRain}%). இன்று உள்ளே பயிற்சி செய்வதே பாதுகாப்பானது.`;
        if (isHi) return `अगले कुछ घंटों में बारिश/तूफान की संभावना अधिक है (${win.avgRain}%)। आज घर के अंदर व्यायाम करना ही सुरक्षित रहेगा।`;
        return `Rain or storm risk is high over the next few hours (${win.avgRain}%). An indoor workout is the safer option today.`;
      }
      const warmSunny = win.avgTemp >= 20 && win.startHour <= 11;
      if (isTa) return `${w} உடற்பயிற்சிக்கு நல்ல நேரமாக இருக்கும். மழை வாய்ப்பு ${win.avgRain}%, வெப்பநிலை சுமார் ${win.avgTemp}°C.${warmSunny ? ' வானிலை வெதுவெதுப்பாக இருப்பதால், இந்த நேரத்தில் வெளியே செல்வது சூரிய ஒளியையும் பெற உதவும்.' : ''}`;
      if (isHi) return `${w} व्यायाम के लिए अच्छा समय रहेगा। बारिश की संभावना ${win.avgRain}% है और तापमान लगभग ${win.avgTemp}°C रहेगा।${warmSunny ? ' मौसम गर्म और साफ रहेगा, तो इस समय बाहर जाने से अच्छी धूप भी मिलेगी।' : ''}`;
      return `${w} looks like a good window to exercise. Rain probability is ${win.avgRain}% and temperature around ${win.avgTemp}°C.${warmSunny ? ' Since it will be warm and reasonably clear, stepping outside then also gives you some healthy morning sunlight.' : ''}`;
    }
  }
  if (/வெளியே|போகலாமா|வெளியில்|out|outside|go out|बाहर|बाहर जा/.test(q)) {
    if (isTa) return rain >= 55 || hot ? `நாளை வெளியே செல்வதில் கவனம் தேவை. மழை வாய்ப்பு ${rain}%${hot ? ' மற்றும் வெப்பம் அதிகமாக இருக்கலாம்' : ''}.` : `நாளை வெளியே செல்லலாம். மழை வாய்ப்பு ${rain}% மற்றும் வானிலை ஏற்றதாக இருக்கிறது.`;
    if (isHi) return rain >= 55 || hot ? `कल बाहर जाने में सावधानी रखें। बारिश की संभावना ${rain}%${hot ? ' है और गर्मी भी अधिक हो सकती है' : ''}।` : `कल बाहर जा सकते हैं। बारिश की संभावना ${rain}% है और मौसम ठीक रहने की उम्मीद है।`;
    return rain >= 55 || hot ? `I would be cautious about going outside tomorrow. Rain probability is ${rain}%${hot ? ' and it may be quite hot' : ''}.` : `Yes, you can go outside tomorrow. Rain probability is ${rain}% and conditions look reasonable.`;
  }
  if (/rain|மழை|மழை வர|बारिश|वर्षा/.test(q)) {
    return isTa ? `நாளைய மழை வாய்ப்பு சுமார் ${rain}%.` : isHi ? `कल बारिश की संभावना लगभग ${rain}% है।` : `Tomorrow's rain probability is about ${rain}%.`;
  }
  if (/temperature|hot|cold|வெப்ப|சூடு|குளிர்|तापमान|गर्मी|ठंड/.test(q)) {
    return isTa ? `நாளை வெப்பநிலை ${Math.round(tomorrow?.tempMin ?? weather.current.temperature)}°C முதல் ${Math.round(tomorrow?.tempMax ?? weather.current.temperature)}°C வரை இருக்கும்.` : isHi ? `कल तापमान ${Math.round(tomorrow?.tempMin ?? weather.current.temperature)}°C से ${Math.round(tomorrow?.tempMax ?? weather.current.temperature)}°C के बीच रहेगा।` : `Tomorrow's temperature should be around ${Math.round(tomorrow?.tempMin ?? weather.current.temperature)}°C to ${Math.round(tomorrow?.tempMax ?? weather.current.temperature)}°C.`;
  }
  if (/good morning|good night|காலை வணக்கம்|இரவு வணக்கம்|सुप्रभात|शुभ रात्रि/.test(q)) {
    return isTa ? 'உங்களுக்கும் இனிய நாள்! இன்று என்ன திட்டம்?' : isHi ? 'आपका दिन शुभ हो! आज क्या योजना है?' : 'Have a great day! What are you planning to do today?';
  }
  if (/study|exam|படிப்பு|தேர்வு|படிக்க|पढ़ाई|परीक्षा|पढ़ना/.test(q)) {
    return isTa ? 'படிப்பை சிறிய பகுதிகளாகப் பிரித்து, 25–30 நிமிட கவன நேரத்துடன் இடைவெளி எடுத்துக்கொள்ளுங்கள். நீங்கள் எந்த பாடம் படிக்கிறீர்கள் என்று சொன்னால் திட்டம் அமைக்க உதவுகிறேன்.' : isHi ? 'पढ़ाई को छोटे हिस्सों में बाँटें और 25–30 मिनट के फोकस सेशन रखें। विषय बताएं तो मैं पढ़ाई की योजना बनाने में मदद कर सकती हूँ।' : 'Break your study into small sessions with 25–30 minutes of focused work and short breaks. Tell me the subject and I can help you plan it.';
  }
  if (/code|coding|python|java|program|கோடிங்|பைதான்|ஜாவா|कोड|पाइथन|जावा/.test(q)) {
    return isTa ? 'கண்டிப்பாக. Coding, Python, Java அல்லது project debugging பற்றி கேட்கலாம். Error message அல்லது code-ஐ அனுப்பினால் step-by-step உதவுகிறேன்.' : isHi ? 'बिल्कुल। Coding, Python, Java या project debugging के बारे में पूछें। Error या code भेजें, मैं step-by-step मदद करूँगी।' : 'Absolutely. Ask me about coding, Python, Java or project debugging. Send the error or code and I can help step by step.';
  }
  if (/health|fever|headache|சுகாதாரம்|காய்ச்சல்|தலைவலி|स्वास्थ्य|बुखार|सिरदर्द/.test(q)) {
    return isTa ? 'உடல்நலம் தொடர்பான கேள்வியில் பொதுவான தகவலை வழங்க முடியும். கடுமையான அல்லது நீடித்த அறிகுறிகள் இருந்தால் மருத்துவரை அணுகுவது பாதுகாப்பானது.' : isHi ? 'स्वास्थ्य से जुड़ी सामान्य जानकारी में मैं मदद कर सकती हूँ। गंभीर या लगातार लक्षण हों तो डॉक्टर से सलाह लेना बेहतर है।' : 'I can provide general health information. For severe or persistent symptoms, it is safest to consult a qualified clinician.';
  }
  if (/(what is|who is|meaning of|define)|என்ன அர்த்தம்|என்ன என்பது|என்றால் என்ன|क्या है|कौन है/.test(q)) {
    return isTa ? `“${question.trim()}” என்பதற்கு எளிய விளக்கத்தை வழங்குகிறேன். கேள்வியில் உள்ள முக்கிய சொல்லை மட்டும் தனியாக அனுப்பினால் இன்னும் தெளிவாகச் சொல்ல முடியும்.` : isHi ? `मैं “${question.trim()}” को सरल भाषा में समझा सकती हूँ। अगर आप मुख्य शब्द अलग से भेजें तो मैं और स्पष्ट जवाब दूँगी।` : `I can explain “${question.trim()}” in simple language. If you send the main term by itself, I can give a clearer explanation.`;
  }
  if (/how do i|how to|எப்படி|என்ன செய்வது|எப்படி செய்வது|कैसे|कैसे करें/.test(q)) {
    return isTa ? `“${question.trim()}” செய்ய பொதுவாக படிப்படியாக வழிகாட்ட முடியும். நீங்கள் எந்த சாதனம் அல்லது சூழலில் செய்யப் போகிறீர்கள் என்று சொன்னால், அதற்கேற்ற வழிமுறையைத் தருகிறேன்.` : isHi ? `मैं “${question.trim()}” के लिए आसान step-by-step तरीका बता सकती हूँ। आप किस डिवाइस या परिस्थिति में करना चाहते हैं, यह बताएं तो जवाब और सटीक होगा।` : `I can give you a simple step-by-step way to do “${question.trim()}”. Tell me the device or situation if you want the steps tailored to you.`;
  }

  // A useful conversational fallback instead of claiming that only weather questions are supported.
  return isTa
    ? `நிச்சயமாக. “${question.trim()}” பற்றி உதவுகிறேன். இதை எப்படி செய்யலாம், என்ன தேர்வு நல்லது, அல்லது step-by-step திட்டம் வேண்டுமா என்று சொன்னால் அதற்கேற்ப பதில் தருகிறேன்.`
    : isHi
      ? `ज़रूर। मैं “${question.trim()}” के बारे में मदद कर सकती हूँ। अगर आपको तरीका, विकल्पों की तुलना या step-by-step योजना चाहिए, बताइए—मैं उसी हिसाब से जवाब दूँगी।`
      : `Sure. I can help with “${question.trim()}”. If you want a method, options, comparison, or a step-by-step plan, tell me and I’ll tailor the answer.`;
}

export async function answerQuestion(question: string, lang: LangCode, weather: WeatherData | null, profile: ProfileType): Promise<string> {
  const q = question.trim();
  if (!q) return '';

  // If an AI endpoint is configured, use it for genuinely open-ended conversation.
  if (AI_URL) {
    try {
      const controller = new AbortController();
      const timeout = window.setTimeout(() => controller.abort(), 9000);
      const response = await fetch(AI_URL, {
        method: 'POST',
        signal: controller.signal,
        headers: { 'Content-Type': 'application/json', ...(AI_KEY ? { Authorization: `Bearer ${AI_KEY}` } : {}) },
        body: JSON.stringify({
          model: 'openai',
          private: true,
          messages: [
            {
              role: 'system',
              content: `You are Mausam Sakhi, a helpful voice assistant. Answer questions clearly, accurately, and conversationally, as if speaking aloud. Keep responses concise, usually 2-4 sentences unless the user asks for more detail. Answer any question the user asks: factual, how-to, opinion-based, casual, study, coding, travel, or weather. Always reply in ${languageName(lang)} because the user selected that language. Avoid markdown, bullets, or symbols that do not translate naturally to speech. Spell out numbers, dates, and units naturally when speaking. Do not repeatedly say you are an AI. If you do not know something or it requires current information not in the supplied context, say so honestly rather than guessing. For weather or travel, use only the supplied weather context and destination context. Ask a clarifying question only when genuinely unclear. Weather context: ${context(weather, profile)}`,
            },
            { role: 'user', content: q },
          ],
        }),
      });
      window.clearTimeout(timeout);
      if (response.ok) {
        const data = await response.json() as { answer?: string; text?: string; choices?: Array<{ message?: { content?: string } }> };
        const answer = data.answer ?? data.text ?? data.choices?.[0]?.message?.content;
        if (answer) return answer.trim();
      }
    } catch { /* reliable local fallback below */ }
  }

  let destinationWeather: WeatherData | null = null;
  let destinationName: string | null = null;
  const destination = findDestination(q);
  if (destination) {
    try {
      const matches = await searchLocations(destination);
      if (matches[0]) {
        destinationName = matches[0].name;
        destinationWeather = await fetchWeather(matches[0].latitude, matches[0].longitude);
      }
    } catch { /* keep current-location answer */ }
  }

  return localFallback(q, lang, weather, destinationWeather, destinationName);
}
