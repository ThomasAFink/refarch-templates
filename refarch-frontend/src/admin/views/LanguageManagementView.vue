<template>
  <v-container :class="[darkMode ? 'container-dark' : 'container-light', hideBackground ? 'hide-background' : '']" class="fill-height" fluid>
    <v-row class="text-justified">
      <v-col class="mb-4" cols="12">
        <v-card :class="{ 'card-dark': darkMode, 'card-light': !darkMode }" class="elevation-1" outlined>
          <v-card-title>
            <v-row class="d-flex align-center flex-wrap">
              <v-col class="d-flex align-center">
                <i class="fa-solid fa-language mr-2"></i>
                <span>Edit/Delete Languages</span>
                <v-spacer></v-spacer>
              </v-col>
              <v-col class="d-none d-md-flex justify-end">
                <router-link :to="{ name: 'AdminDashboard' }" class="text-decoration-none">
                  <v-btn color="secondary" text>
                    <span class="d-none d-md-inline">Admin Dashboard</span>
                    <span class="d-md-none">Admin</span>
                    <i class="fa-solid fa-arrow-left ml-2"></i>
                  </v-btn>
                </router-link>
                <v-btn class="ml-2" color="secondary" text @click="dialog = true">
                  <span class="d-none d-md-inline">Add Language</span>
                  <span class="d-md-none">Add</span>
                  <i class="fa-solid fa-language ml-2"></i>
                </v-btn>
              </v-col>
              <v-col class="d-flex d-md-none justify-center">
                <router-link :to="{ name: 'AdminDashboard' }" class="text-decoration-none">
                  <v-btn color="secondary" text>
                    Admin <i class="fa-solid fa-arrow-left ml-2"></i>
                  </v-btn>
                </router-link>
                <v-btn class="ml-2" color="secondary" text @click="dialog = true">
                  Add <i class="fa-solid fa-language ml-2"></i>
                </v-btn>
              </v-col>
            </v-row>
          </v-card-title>
          <v-card-text>
            <v-expansion-panels accordion>
              <v-expansion-panel v-for="(language, index) in sortedLanguages" :key="language.id" :class="[darkMode ? 'container-dark' : 'container-light', hideBackground ? 'hide-background' : 'hide-background']">
                <v-expansion-panel-title>
                  <i v-if="language.fontAwesomeIcon" :class="language.fontAwesomeIcon" class="mr-4"></i>
                  <v-icon v-else class="mr-4">{{ language.mdiIcon }}</v-icon>
                  {{ language.name }}
                </v-expansion-panel-title>
                <v-expansion-panel-text>
                  <v-form :ref="'form-' + index" v-model="forms[index]" @submit.prevent="validateAndUpdateLanguage(index)">
                    <v-select
                      v-model="language.name"
                      :items="filteredLanguagesOptions"
                      label="Name"
                      item-title="name"
                      item-value="name"
                      clearable
                      :rules="[rules.required]"
                      @change="syncLanguageFields(language)"
                    ></v-select>
                    <v-select
                      v-model="language.abbreviation"
                      :items="filteredLanguagesOptions"
                      label="Abbreviation"
                      item-title="abbreviation"
                      item-value="abbreviation"
                      clearable
                      :rules="[rules.required, rules.notEmpty]"
                      disabled
                    ></v-select>
                    <v-select
                      v-model="language.fontAwesomeIcon"
                      :items="filteredLanguagesOptions"
                      label="Font Awesome Icon"
                      item-title="fontAwesomeIcon"
                      item-value="fontAwesomeIcon"
                      clearable
                      :rules="[rules.required]"
                      disabled
                    ></v-select>
                    <v-select
                      v-model="language.mdiIcon"
                      :items="filteredLanguagesOptions"
                      label="MDI Icon"
                      item-title="mdiIcon"
                      item-value="mdiIcon"
                      clearable
                      :rules="[rules.required]"
                      disabled
                    ></v-select>
                    <v-btn :disabled="!forms[index]" type="submit" color="secondary" class="mr-4">Update</v-btn>
                    <v-btn @click="deleteLanguage(index)" :class="darkMode ? 'btn-light' : 'btn-dark'" class="mr-4" color="red">Delete</v-btn>
                  </v-form>
                  <v-alert v-if="errorIndex === index && errorVisible" type="error" class="mt-3 fade-out" dismissible>{{ error }}</v-alert>
                  <v-alert v-if="successIndex === index && successVisible" type="success" class="mt-3 fade-out" dismissible>{{ success }}</v-alert>
                </v-expansion-panel-text>
              </v-expansion-panel>
            </v-expansion-panels>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    <!-- New Language Dialog -->
    <new-language-view :dark-mode="darkMode" v-model:dialog="dialog" @language-created="addNewLanguage"></new-language-view>
  </v-container>
</template>

<script>
import { createLanguage, fetchLanguages, updateLanguage, deleteLanguage } from '@/api/language-client.ts';
import NewLanguageView from '@/admin/components/NewLanguageView.vue'; // Adjust the path as needed

export default {
  name: 'LanguageManagementView',
  components: {
    NewLanguageView
  },
  props: {
    darkMode: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      languages: [],
      forms: [],
      hideBackground: false,
      dialog: false,
      languageData: {
        name: '',
        abbreviation: '',
        fontAwesomeIcon: '',
        mdiIcon: '',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      },
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
      errorIndex: null,
      successIndex: null,
      errorVisible: false,
      successVisible: false,
      rules: {
        required: value => !!value || 'Required.',
        notEmpty: value => (value && value.trim() !== '') || 'Cannot be empty.'
      }
    };
  },
  watch: {
    darkMode(newValue) {
      // Always trigger the hide/show behavior regardless of darkMode value
      this.hideBackground = true;
      setTimeout(() => {
        this.hideBackground = false;
      }, 500); // Adjusted to 500ms for demonstration purposes
    },
    '$route'() {
      this.setupPageHead(); // Update the head when the route changes
    },
    languages: {
      handler(newLanguages) {
        newLanguages.forEach((language, index) => {
          this.$watch(() => language.name, (newName) => {
            if (newName) {
              const selectedLanguage = this.languagesOptions.find(option => option.name === newName);
              if (selectedLanguage) {
                language.abbreviation = selectedLanguage.abbreviation;
                language.fontAwesomeIcon = selectedLanguage.icon;
                language.mdiIcon = selectedLanguage.mdiIcon;
              }
            }
          });
        });
      },
      deep: true,
      immediate: true,
    }
  },
  methods: {
    validateAndUpdateLanguage(index) {
      const formRef = this.$refs[`form-${index}`][0]; // Correctly access the form reference
      formRef.validate().then(success => {
        if (success) {
          this.updateLanguage(index);
        }
      });
    },
    updateLanguage(index) {
      const language = this.languages[index];
      updateLanguage(language.id, language)
        .then(response => {
          this.success = 'Language updated successfully!';
          this.error = null;
          this.successIndex = index;
          this.errorIndex = null;
          this.successVisible = true;
          this.errorVisible = false;
          setTimeout(() => this.successVisible = false, 1000);
          this.$emit('languages-updated'); // Emit event
        })
        .catch(error => {
          this.error = 'Error updating language.';
          this.success = null;
          this.successIndex = null;
          this.errorIndex = index;
          this.successVisible = false;
          this.errorVisible = true;
          setTimeout(() => this.errorVisible = false, 1000);
        });
    },
    deleteLanguage(index) {
      const languageId = this.languages[index].id;
      deleteLanguage(languageId)
        .then(response => {
          this.success = 'Language deleted successfully!';
          this.error = null;
          this.successIndex = index;
          this.errorIndex = null;
          this.successVisible = true;
          this.errorVisible = false;
          setTimeout(() => this.successVisible = false, 500);
          this.languages.splice(index, 1);
          this.$router.reload();
        })
        .catch(error => {
          this.error = 'Error deleting language.';
          this.success = null;
          this.successIndex = null;
          this.errorIndex = index;
          this.successVisible = false;
          this.errorVisible = true;
          setTimeout(() => this.errorVisible = false, 500);
        });
    },
    fetchLanguages() {
      fetchLanguages()
        .then(response => {
          // Assuming response.data._embedded.languages is the array of language objects
          this.languages = response.data._embedded.languages.map(language => ({
            id: language._links.self.href.split('/').pop(), // Extract ID from the URL
            linkId: language.linkId,
            name: language.name,
            abbreviation: language.abbreviation,
            fontAwesomeIcon: language.fontAwesomeIcon,
            mdiIcon: language.mdiIcon,
            createdAt: language.createdAt,
            updatedAt: language.updatedAt
          }));
          this.successIndex = null;
          this.errorIndex = null;
          this.successVisible = false;
          this.errorVisible = false;
        })
        .catch(error => {
          this.error = 'Error fetching languages.';
          this.success = null;
          this.successIndex = null;
          this.errorIndex = null;
          this.successVisible = false;
          this.errorVisible = true;
          setTimeout(() => this.errorVisible = false, 500);
        });
    },
    setupPageHead() {
      // Your method to update the head
    },
    addNewLanguage(newLanguage) {
      this.languages.push(newLanguage); // Add the new language to the list
      this.$emit('language-created', newLanguage); // Emit event for parent component
      this.$emit('languages-updated'); // Emit event
    },
    syncLanguageFields(language) {
      const selectedLanguage = this.languagesOptions.find(option => option.name === language.name);
      if (selectedLanguage) {
        language.abbreviation = selectedLanguage.abbreviation;
        language.fontAwesomeIcon = selectedLanguage.icon;
        language.mdiIcon = selectedLanguage.mdiIcon;
      }
    }
  },
  mounted() {
    this.setupPageHead(); // Initial setup of the head
    this.fetchLanguages(); // Fetch languages on mount
  },
  computed: {
    sortedLanguagesOptions() {
      return this.languagesOptions.sort((a, b) => a.name.localeCompare(b.name));
    },
    sortedLanguages() {
      return this.languages.sort((a, b) => a.name.localeCompare(b.name));
    },
    filteredLanguagesOptions() {
      const existingLanguages = this.languages.map(lang => lang.name);
      return this.sortedLanguagesOptions.filter(option => !existingLanguages.includes(option.name));
    }
  },
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
  background-color: #fff;
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
  background-color: #333;
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
</style>
