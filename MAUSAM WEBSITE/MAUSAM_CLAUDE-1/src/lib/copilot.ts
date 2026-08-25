import type { WeatherData, ProfileType, LangCode, CopilotAdvice } from './types';
import { weatherCodeLabel, weatherIcon } from './weather';

export function buildCopilotAdvice(
  weather: WeatherData,
  profile: ProfileType,
  lang: LangCode
): CopilotAdvice {
  const today = weather.daily[0];
  const nextRain = weather.hourly.find((h) => h.precipitationProbability > 55);
  const rainInHours = nextRain
    ? Math.round(
        (new Date(nextRain.time).getTime() - new Date(weather.current.time).getTime()) / 3_600_000
      )
    : null;

  const hi = lang.startsWith('hi');

  const banks: Record<string, { hi: string[]; en: string[] }> = {
    health_conscious: {
      hi: ['धूप और गर्मी के स्तर पर ध्यान दें, बाहर निकलते समय पानी साथ रखें।', 'UV और मौसम जोखिम अधिक हो तो दोपहर की सीधी धूप से बचें।', `अभी ${weatherCodeLabel(weather.current.weatherCode)} और तापमान ${Math.round(weather.current.temperature)}° है।`],
      en: ['Watch heat and sun exposure; carry water when heading out.', 'If UV and weather risk is high, avoid direct afternoon sun.', `Currently ${weatherCodeLabel(weather.current.weatherCode)} at ${Math.round(weather.current.temperature)}°. `],
    },
    fitness_enthusiast: {
      hi: ['व्यायाम के लिए सुबह या शाम का समय चुनें और पानी साथ रखें।', 'तेज़ गर्मी या हवा में वर्कआउट की तीव्रता कम करें।', `अभी तापमान ${Math.round(weather.current.temperature)}° और हवा ${Math.round(weather.current.windSpeed)} किमी/घंटा है।`],
      en: ['Prefer morning or evening for exercise and carry water.', 'Reduce workout intensity during high heat or strong winds.', `Now ${Math.round(weather.current.temperature)}° with winds at ${Math.round(weather.current.windSpeed)} km/h.`],
    },
    beachgoer: {
      hi: ['समुद्र तट पर जाने से पहले बारिश और तेज़ हवा की चेतावनी देखें।', 'तेज़ हवा या तूफान में पानी के पास न जाएँ।', 'समुद्र तट की गतिविधियों के लिए मौसम स्थिर होने पर ही निकलें।'],
      en: ['Check rain and strong-wind warnings before heading to the beach.', 'Stay away from the water during storms or strong winds.', 'Plan beach activities when conditions are stable.'],
    },
    traveler: {
      hi: ['यात्रा से पहले अगले कुछ घंटों की बारिश और तूफान की स्थिति देखें।', 'छाता या हल्का रेनकोट साथ रखें अगर बारिश की संभावना है।', 'यात्रा के लिए सुरक्षित और कम जोखिम वाला समय चुनें।'],
      en: ['Check rain and storm conditions for the next few hours before travel.', 'Carry an umbrella or light raincoat when rain is possible.', 'Choose a safer, lower-risk travel window.'],
    },
    parent: {
      hi: ['बच्चों के बाहर जाने से पहले बारिश, गर्मी और तूफान की चेतावनी देखें।', 'भारी बारिश में जलभराव वाले रास्तों से बचें।', 'गर्मी में बच्चों को पानी पिलाते रहें और दोपहर की धूप से बचाएँ।'],
      en: ['Check rain, heat and storm warnings before children head outside.', 'Avoid waterlogged routes during heavy rain.', 'Keep children hydrated and out of strong afternoon heat.'],
    },
    commuter: {
      hi: ['आवागमन से पहले बारिश, कोहरा और तेज़ हवा की चेतावनी देखें।', 'दृश्यता कम होने पर धीरे चलें और अतिरिक्त समय रखें।', 'भारी बारिश में जलभराव वाले रास्तों से बचें।'],
      en: ['Check rain, fog and strong-wind warnings before commuting.', 'Allow extra travel time when visibility is low.', 'Avoid waterlogged routes during heavy rain.'],
    },
    event_planner: {
      hi: ['कार्यक्रम के लिए अगले 24 घंटे की बारिश और हवा की संभावना देखें।', 'बारिश या तूफान की स्थिति में इनडोर बैकअप तैयार रखें।', 'मेहमानों के आराम के लिए तापमान और हवा को ध्यान में रखें।'],
      en: ['Check rain and wind probability for the next 24 hours before the event.', 'Keep an indoor backup ready for rain or storms.', 'Plan guest comfort around temperature and wind.'],
    },
    farmer: {
      hi: [
        today.precipitationSum > 15
          ? 'कल भारी बारिश होगी — खाद और कीटनाशक छिड़काव टालें।'
          : 'बारिश कम है — फसल की निगरानी करें और आवश्यकतानुसार सिंचाई करें।',
        today.tempMax >= 38
          ? 'दोपहर 12-4 बजे खेत में काम टालें; सुबह 6-8 बजे पानी दें।'
          : 'तापमान सामान्य है; नियमित काम जारी रखें।',
        `आज का तापमान ${Math.round(today.tempMax)}° / ${Math.round(today.tempMin)}°।`,
      ],
      en: [
        today.precipitationSum > 15
          ? 'Heavy rain expected tomorrow — postpone fertilizer and pesticide spraying.'
          : 'Rain is low — monitor crops and irrigate as needed.',
        today.tempMax >= 38
          ? 'Avoid field work between 12–4 PM; irrigate early morning (6–8 AM).'
          : 'Temperatures are normal; continue regular tasks.',
        `Today’s temperature: ${Math.round(today.tempMax)}° / ${Math.round(today.tempMin)}°.`,
      ],
    },
    student: {
      hi: [
        today.precipitationSum > 15
          ? 'कल बारिश होगी — छाता/रेनकोट साथ रखें, निचले रास्तों से बचें।'
          : 'मौसम साफ है — बाहर घूम सकते हैं, पानी साथ रखें।',
        today.tempMax >= 38
          ? 'दोपहर बाहर न घूमें; हल्के कपड़े पहनें और पानी पिएँ।'
          : 'तापमान ठीक है — सामान्य दिनचर्या रखें।',
        rainInHours !== null && rainInHours <= 6
          ? `अगली ${rainInHours} घंटे में बारिश संभव है — समय पर निकलें।`
          : 'अगले कुछ घंटों में बारिश की संभावना कम है।',
      ],
      en: [
        today.precipitationSum > 15
          ? 'Rain expected tomorrow — carry an umbrella/raincoat and avoid low-lying routes.'
          : 'Weather is clear — you can head out; carry water.',
        today.tempMax >= 38
          ? 'Avoid outdoor activity in the afternoon; wear light clothes and hydrate.'
          : 'Temperatures are fine — keep your normal routine.',
        rainInHours !== null && rainInHours <= 6
          ? `Rain likely in the next ${rainInHours} hours — leave on time.`
          : 'Low chance of rain in the next few hours.',
      ],
    },
    outdoor_worker: {
      hi: [
        today.precipitationSum > 15
          ? 'बारिश के कारण खुले में काम टालें; बारिश रुकने पर शुरू करें।'
          : 'मौसम साफ है — सुरक्षा उपकरण के साथ काम करें।',
        today.tempMax >= 38
          ? 'गर्मी अधिक है — 12-4 बजे बाहर न काम करें, छाया में आराम करें।'
          : 'तापमान सामान्य — नियमित काम जारी रखें।',
        today.windSpeedMax >= 45
          ? `तेज़ हवा (${Math.round(today.windSpeedMax)} किमी/घंटा) — ऊँचाई पर काम टालें।`
          : 'हवा सामान्य है।',
      ],
      en: [
        today.precipitationSum > 15
          ? 'Rain ahead — postpone outdoor work; resume once it clears.'
          : 'Weather is clear — work with your safety gear on.',
        today.tempMax >= 38
          ? 'High heat — do not work outdoors 12–4 PM, rest in shade.'
          : 'Temperatures normal — continue regular work.',
        today.windSpeedMax >= 45
          ? `Strong winds (${Math.round(today.windSpeedMax)} km/h) — postpone work at height.`
          : 'Winds are normal.',
      ],
    },
    general: {
      hi: [
        today.precipitationSum > 15
          ? 'कल बारिश होगी — छाता साथ रखें।'
          : 'मौसम साफ रहेगा — बाहर निकल सकते हैं।',
        today.tempMax >= 38
          ? 'गर्मी रहेगी — पानी पिएँ और दोपहर की धूप से बचें।'
          : 'तापमान सामान्य है।',
        `अभी ${weatherCodeLabel(weather.current.weatherCode)} (${Math.round(weather.current.temperature)}°)।`,
      ],
      en: [
        today.precipitationSum > 15
          ? 'Rain expected tomorrow — carry an umbrella.'
          : 'Weather will stay clear — you can head out.',
        today.tempMax >= 38
          ? 'It will be hot — hydrate and avoid the afternoon sun.'
          : 'Temperatures are normal.',
        `Right now: ${weatherCodeLabel(weather.current.weatherCode)} (${Math.round(weather.current.temperature)}°).`,
      ],
    },
  };

  const actions = banks[profile][hi ? 'hi' : 'en'];
  const profileLabel = profile.replace('_', ' ');
  const headline = lang.startsWith('ta')
    ? `${profileLabel} சுயவிவரத்திற்கான வானிலை ஆலோசனை`
    : lang.startsWith('bn')
      ? `${profileLabel} প্রোফাইলের জন্য আবহাওয়া পরামর্শ`
      : lang.startsWith('te')
        ? `${profileLabel} ప్రొఫైల్‌కు వాతావరణ సూచనలు`
        : lang.startsWith('mr')
          ? `${profileLabel} प्रोफाइलसाठी हवामान सूचना`
          : hi
            ? `${profileLabel} प्रोफ़ाइल के अनुसार सुझाव`
            : `Advice for ${profileLabel} profile`;

  const localVoice = lang.startsWith('ta')
    ? `உங்கள் வானிலை ஆலோசனை: வெப்பநிலை ${Math.round(today.tempMax)} முதல் ${Math.round(today.tempMin)} டிகிரி வரை இருக்கும். வெளியே செல்லும் முன் மழை மற்றும் காற்று எச்சரிக்கைகளைப் பார்க்கவும். தண்ணீர் எடுத்துச் செல்லுங்கள்.`
    : lang.startsWith('bn')
      ? `আপনার আবহাওয়া পরামর্শ: তাপমাত্রা ${Math.round(today.tempMax)} থেকে ${Math.round(today.tempMin)} ডিগ্রির মধ্যে থাকবে। বাইরে যাওয়ার আগে বৃষ্টি ও বাতাসের সতর্কতা দেখুন এবং জল সঙ্গে রাখুন।`
      : lang.startsWith('te')
        ? `మీ వాతావరణ సూచన: ఉష్ణోగ్రత ${Math.round(today.tempMin)} నుండి ${Math.round(today.tempMax)} డిగ్రీల వరకు ఉంటుంది. బయటకు వెళ్లే ముందు వర్షం మరియు గాలి హెచ్చరికలను చూడండి. నీరు వెంట తీసుకెళ్లండి.`
        : lang.startsWith('mr')
          ? `तुमच्यासाठी हवामान सूचना: तापमान ${Math.round(today.tempMin)} ते ${Math.round(today.tempMax)} अंश राहील. बाहेर जाण्यापूर्वी पाऊस आणि वाऱ्याच्या सूचना तपासा आणि पाणी सोबत ठेवा.`
          : `${headline}. ${actions.join(' ')}`;

  const voiceText = localVoice;

  return { headline, actions, voiceText };
}

export { weatherIcon };
