import { createI18n } from 'vue-i18n'

// Import your translation files
import en from '@/locales/en.json'
import de from '@/locales/de.json'

const i18n = createI18n({
  legacy: false, // Set to false to use Composition API
  locale: 'en', // Set default locale
  fallbackLocale: 'en', // Set fallback locale
  messages: {
    en,
    de
  }
})

export default i18n 