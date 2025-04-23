<template>
  <v-dialog v-model="internalLanguageDialog" max-width="600px">
    <v-card :class="{ 'card-dark': darkMode, 'card-light': !darkMode }" class="elevation-1" outlined>
      <v-toolbar color="primary" dark flat>
        <v-toolbar-title>Create New Language</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn icon @click="closeDialog">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-toolbar>
      <v-card-text>
        <v-form ref="form" v-model="formValid" @submit.prevent="submitForm">
          <v-select
            v-model="languageData.name"
            :items="filteredLanguagesOptions"
            label="Name"
            item-title="name"
            item-value="name"
            clearable
            :rules="[rules.required]"
          ></v-select>
          <v-select
            v-model="languageData.abbreviation"
            :items="abbreviationOptions"
            label="Abbreviation"
            item-title="abbreviation"
            item-value="abbreviation"
            clearable
            :rules="[rules.required, rules.notEmpty]"
            disabled
          ></v-select>
          <v-select
            v-model="languageData.fontAwesomeIcon"
            :items="fontAwesomeOptions"
            label="Font Awesome Icon"
            item-title="fontAwesomeIcon"
            item-value="fontAwesomeIcon"
            clearable
            :rules="[rules.required]"
            disabled
          ></v-select>
          <v-select
            v-model="languageData.mdiIcon"
            :items="mdiOptions"
            label="MDI Icon"
            item-title="mdiIcon"
            item-value="mdiIcon"
            clearable
            :rules="[rules.required]"
            disabled
          ></v-select>
          <v-btn :disabled="!formValid" type="submit" color="secondary" class="mr-4">Submit</v-btn>
          <v-btn @click="closeDialog" color="red" class="mr-4">Cancel</v-btn>
          <v-alert v-if="submitting" type="info" color="yellow" class="mt-3"><p>Translating... <i class="fa-solid fa-spinner fa-spin"></i></p></v-alert>
          <v-alert v-if="errorVisible" type="error" class="mt-3 fade-out" dismissible>{{ error }}</v-alert>
          <v-alert v-if="successVisible" type="success" class="mt-3 fade-out" dismissible>{{ success }}</v-alert>
        </v-form>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import { createLanguage, fetchLanguages } from '@/api/language-client.ts';

export default {
  name: 'NewLanguageView',
  props: {
    darkMode: {
      type: Boolean,
      required: true
    },
    dialog: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      internalLanguageDialog: this.dialog,
      formValid: false,
      languageData: {
        name: '',
        abbreviation: '',
        fontAwesomeIcon: '',
        mdiIcon: '',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      },
      languages: [],
      languagesOptions: [
        { name: 'English', icon: 'fa-solid fa-language', abbreviation: 'en', mdiIcon: 'mdi mdi-translate' },
        { name: 'Spanish', icon: 'fa-solid fa-language', abbreviation: 'es', mdiIcon: 'mdi mdi-translate' },
        { name: 'German', icon: 'fa-solid fa-language', abbreviation: 'de', mdiIcon: 'mdi mdi-translate' },
        { name: 'French', icon: 'fa-solid fa-language', abbreviation: 'fr', mdiIcon: 'mdi mdi-translate' },
        { name: 'Chinese', icon: 'fa-solid fa-language', abbreviation: 'zh', mdiIcon: 'mdi mdi-translate' },
        { name: 'Japanese', icon: 'fa-solid fa-language', abbreviation: 'ja', mdiIcon: 'mdi mdi-translate' },
        { name: 'Korean', icon: 'fa-solid fa-language', abbreviation: 'ko', mdiIcon: 'mdi mdi-translate' },
        { name: 'Russian', icon: 'fa-solid fa-language', abbreviation: 'ru', mdiIcon: 'mdi mdi-translate' },
        { name: 'Italian', icon: 'fa-solid fa-language', abbreviation: 'it', mdiIcon: 'mdi mdi-translate' },
        { name: 'Portuguese', icon: 'fa-solid fa-language', abbreviation: 'pt', mdiIcon: 'mdi mdi-translate' },
        { name: 'Dutch', icon: 'fa-solid fa-language', abbreviation: 'nl', mdiIcon: 'mdi mdi-translate' },
        { name: 'Greek', icon: 'fa-solid fa-language', abbreviation: 'el', mdiIcon: 'mdi mdi-translate' },
        { name: 'Turkish', icon: 'fa-solid fa-language', abbreviation: 'tr', mdiIcon: 'mdi mdi-translate' },
        { name: 'Arabic', icon: 'fa-solid fa-language', abbreviation: 'ar', mdiIcon: 'mdi mdi-translate' },
        { name: 'Hebrew', icon: 'fa-solid fa-language', abbreviation: 'he', mdiIcon: 'mdi mdi-translate' },
        { name: 'Swedish', icon: 'fa-solid fa-language', abbreviation: 'sv', mdiIcon: 'mdi mdi-translate' },
        { name: 'Norwegian', icon: 'fa-solid fa-language', abbreviation: 'no', mdiIcon: 'mdi mdi-translate' },
        { name: 'Danish', icon: 'fa-solid fa-language', abbreviation: 'da', mdiIcon: 'mdi mdi-translate' },
        { name: 'Finnish', icon: 'fa-solid fa-language', abbreviation: 'fi', mdiIcon: 'mdi mdi-translate' },
        { name: 'Polish', icon: 'fa-solid fa-language', abbreviation: 'pl', mdiIcon: 'mdi mdi-translate' },
        { name: 'Czech', icon: 'fa-solid fa-language', abbreviation: 'cs', mdiIcon: 'mdi mdi-translate' },
        { name: 'Hungarian', icon: 'fa-solid fa-language', abbreviation: 'hu', mdiIcon: 'mdi mdi-translate' },
        { name: 'Romanian', icon: 'fa-solid fa-language', abbreviation: 'ro', mdiIcon: 'mdi mdi-translate' },
        { name: 'Slovak', icon: 'fa-solid fa-language', abbreviation: 'sk', mdiIcon: 'mdi mdi-translate' },
        { name: 'Bulgarian', icon: 'fa-solid fa-language', abbreviation: 'bg', mdiIcon: 'mdi mdi-translate' },
        { name: 'Serbian', icon: 'fa-solid fa-language', abbreviation: 'sr', mdiIcon: 'mdi mdi-translate' },
        { name: 'Croatian', icon: 'fa-solid fa-language', abbreviation: 'hr', mdiIcon: 'mdi mdi-translate' },
        { name: 'Slovenian', icon: 'fa-solid fa-language', abbreviation: 'sl', mdiIcon: 'mdi mdi-translate' },
        { name: 'Lithuanian', icon: 'fa-solid fa-language', abbreviation: 'lt', mdiIcon: 'mdi mdi-translate' },
        { name: 'Latvian', icon: 'fa-solid fa-language', abbreviation: 'lv', mdiIcon: 'mdi mdi-translate' },
        { name: 'Estonian', icon: 'fa-solid fa-language', abbreviation: 'et', mdiIcon: 'mdi mdi-translate' },
        { name: 'Malay', icon: 'fa-solid fa-language', abbreviation: 'ms', mdiIcon: 'mdi mdi-translate' },
        { name: 'Indonesian', icon: 'fa-solid fa-language', abbreviation: 'id', mdiIcon: 'mdi mdi-translate' },
        { name: 'Thai', icon: 'fa-solid fa-language', abbreviation: 'th', mdiIcon: 'mdi mdi-translate' },
        { name: 'Vietnamese', icon: 'fa-solid fa-language', abbreviation: 'vi', mdiIcon: 'mdi mdi-translate' },
        { name: 'Filipino', icon: 'fa-solid fa-language', abbreviation: 'tl', mdiIcon: 'mdi mdi-translate' },
        { name: 'Hindi', icon: 'fa-solid fa-language', abbreviation: 'hi', mdiIcon: 'mdi mdi-translate' },
        { name: 'Bengali', icon: 'fa-solid fa-language', abbreviation: 'bn', mdiIcon: 'mdi mdi-translate' },
        { name: 'Punjabi', icon: 'fa-solid fa-language', abbreviation: 'pa', mdiIcon: 'mdi mdi-translate' },
        { name: 'Gujarati', icon: 'fa-solid fa-language', abbreviation: 'gu', mdiIcon: 'mdi mdi-translate' },
        { name: 'Tamil', icon: 'fa-solid fa-language', abbreviation: 'ta', mdiIcon: 'mdi mdi-translate' },
        { name: 'Telugu', icon: 'fa-solid fa-language', abbreviation: 'te', mdiIcon: 'mdi mdi-translate' },
        { name: 'Kannada', icon: 'fa-solid fa-language', abbreviation: 'kn', mdiIcon: 'mdi mdi-translate' },
        { name: 'Malayalam', icon: 'fa-solid fa-language', abbreviation: 'ml', mdiIcon: 'mdi mdi-translate' },
        { name: 'Sinhalese', icon: 'fa-solid fa-language', abbreviation: 'si', mdiIcon: 'mdi mdi-translate' },
        { name: 'Marathi', icon: 'fa-solid fa-language', abbreviation: 'mr', mdiIcon: 'mdi mdi-translate' },
        { name: 'Nepali', icon: 'fa-solid fa-language', abbreviation: 'ne', mdiIcon: 'mdi mdi-translate' },
        { name: 'Burmese', icon: 'fa-solid fa-language', abbreviation: 'my', mdiIcon: 'mdi mdi-translate' },
      ],
      error: null,
      success: null,
      errorVisible: false,
      successVisible: false,
      submitting: false,
      rules: {
        required: value => !!value || 'Required.',
        notEmpty: value => (value && value.trim() !== '') || 'Cannot be empty.'
      }
    };
  },
  watch: {
    dialog(val) {
      this.internalLanguageDialog = val;
    },
    internalLanguageDialog(val) {
      this.$emit('update:dialog', val);
    },
    'languageData.name'(newName) {
      const selectedLanguage = this.languagesOptions.find(option => option.name === newName);
      if (selectedLanguage) {
        this.languageData.abbreviation = selectedLanguage.abbreviation;
        this.languageData.fontAwesomeIcon = selectedLanguage.icon;
        this.languageData.mdiIcon = selectedLanguage.mdiIcon;
      }
    }
  },
  methods: {
    async submitForm() {
      this.submitting = true;
      try {
        const response = await createLanguage(this.languageData);
        this.success = 'Language created successfully!';
        this.error = null;
        this.successVisible = true;
        this.errorVisible = false;
        this.submitting = false;
        setTimeout(async () => {
          this.successVisible = false;
          this.$emit('language-created', response.data);
          this.$emit('update:dialog', false);
          this.$router.reload();
        }, 1000);
      } catch (error) {
        this.error = 'Error creating language.';
        this.success = null;
        this.successVisible = false;
        this.errorVisible = true;
        this.submitting = false;
        setTimeout(() => this.errorVisible = false, 1000);
      }
    },
    closeDialog() {
      this.internalLanguageDialog = false;
    },
    fetchLanguages() {
      fetchLanguages()
        .then(response => {
          this.languages = response.data._embedded.languages.map(language => ({
            id: language._links.self.href.split('/').pop(), // Extract ID from the URL
            name: language.name,
            abbreviation: language.abbreviation,
            fontAwesomeIcon: language.fontAwesomeIcon,
            mdiIcon: language.mdiIcon,
            createdAt: language.createdAt,
            updatedAt: language.updatedAt
          }));
        })
        .catch(error => {
          console.error('Error fetching languages:', error);
        });
    }
  },
  computed: {
    sortedLanguagesOptions() {
      return this.languagesOptions.sort((a, b) => a.name.localeCompare(b.name));
    },
    filteredLanguagesOptions() {
      const existingLanguages = this.languages.map(lang => lang.name);
      return this.sortedLanguagesOptions.filter(option => !existingLanguages.includes(option.name));
    },
    abbreviationOptions() {
      return this.languagesOptions.map(option => ({
        abbreviation: option.abbreviation,
        name: option.name
      }));
    },
    fontAwesomeOptions() {
      return this.languagesOptions.map(option => ({
        fontAwesomeIcon: option.icon,
        name: option.name
      }));
    },
    mdiOptions() {
      return this.languagesOptions.map(option => ({
        mdiIcon: option.mdiIcon,
        name: option.name
      }));
    }
  },
  mounted() {
    this.fetchLanguages(); // Fetch existing languages on mount
  }
};
</script>

<style scoped>
.container-light {
  width: 100% !important;
  max-width: 100%;
  position: relative;
  background-image: url('@/assets/neural_net_light.svg');
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  background-blend-mode: color-burn;
  background-blend-mode: lighten;
  transition: background-image 1s ease-in-out;
}

.container-dark {
  width: 100% !important;
  max-width: 100%;
  position: relative;
  background-image: url('@/assets/neural_net_dark.svg');
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  background-blend-mode: color-burn;
  background-blend-mode: lighten;
  transition: background-image 1s ease-in-out;
}

.hide-background {
  background-image: none !important;
}

.card-dark {
  background-color: #424242;
  color: white;
}

.card-light {
  background-color: white;
  color: #333;
}

.blog-thumbnail-container {
  height: 200px;
  width: 100%;
  overflow: hidden;
  display: flex; 
  justify-content: center;
  align-items: center;
}

.fade-out {
  transition: opacity 1s ease-out;
  opacity: 1;
}

.fade-out[data-fade="false"] {
  opacity: 0;
}

/* Add animation for spinner */
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.fa-spin {
  float: right;
  line-height: 1.5;
  animation: spin 1s linear infinite;
}
</style>
