<template>
  <v-container :class="[darkMode ? 'container-dark' : 'container-light', hideBackground ? 'hide-background' : '']" class="fill-height" fluid>
    <v-row class="text-justified">
      <v-col class="mb-4" cols="12">
        <v-card :class="{ 'card-dark': darkMode, 'card-light': !darkMode }" class="elevation-1" outlined>
          <v-card-title>
            <v-row class="d-flex align-center flex-wrap">
              <v-col class="d-flex align-center">
                <i class="fa-solid fa-link mr-2"></i>
                <span>Edit/Delete Social Media Links</span>
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
                  <span class="d-none d-md-inline">Add Link</span>
                  <span class="d-md-none">Add</span>
                  <i class="fa-solid fa-link ml-2"></i>
                </v-btn>
              </v-col>
              <v-col class="d-flex d-md-none justify-center">
                <router-link :to="{ name: 'AdminDashboard' }" class="text-decoration-none">
                  <v-btn color="secondary" text>
                    Admin <i class="fa-solid fa-arrow-left ml-2"></i>
                  </v-btn>
                </router-link>
                <v-btn class="ml-2" color="secondary" text @click="dialog = true">
                  Add <i class="fa-solid fa-link ml-2"></i>
                </v-btn>
              </v-col>
            </v-row>
          </v-card-title>
          <v-card-text>
            <v-expansion-panels accordion>
              <v-expansion-panel v-for="(link, index) in sortedSocialMediaLinks" :key="link.id" :class="[darkMode ? 'container-dark' : 'container-light', hideBackground ? 'hide-background' : 'hide-background']">
                <v-expansion-panel-title>
                  <i v-if="link.fontAwesomeIcon" :class="link.fontAwesomeIcon" class="mr-4"></i>
                  <v-icon v-else class="mr-4">{{ link.mdiIcon }}</v-icon>
                  {{ link.name }}
                </v-expansion-panel-title>
                <v-expansion-panel-text>
                  <v-form :ref="'form-' + index" v-model="forms[index]" @submit.prevent="validateAndUpdateLink(index)">
                    <v-text-field
                      v-model="link.link"
                      label="Link"
                      :rules="[rules.required, rules.validURL]"
                      required
                    ></v-text-field>
                    <v-text-field
                      v-model="link.name"
                      label="Name"
                      :rules="[rules.required]"
                      required
                    ></v-text-field>
                    <v-select
                      v-model="link.fontAwesomeIcon"
                      :items="sortedFontAwesomeIcons"
                      label="Font Awesome Icon"
                      item-title="name"
                      item-value="icon"
                      clearable
                      :rules="[rules.required]"
                      @change="syncIcons(link, 'fontAwesome')"
                    ></v-select>
                    <v-select
                      v-model="link.mdiIcon"
                      :items="sortedMdiIcons"
                      label="MDI Icon"
                      item-title="name"
                      item-value="icon"
                      clearable
                      :rules="[rules.required]"
                      @change="syncIcons(link, 'mdi')"
                    ></v-select>
                    <v-text-field v-model="link.type" label="Type" disabled></v-text-field>
                    <v-text-field v-model="link.scope" label="Scope" disabled></v-text-field>
                    <v-btn :disabled="!forms[index]" type="submit" color="secondary" class="mr-4">Update</v-btn>
                    <v-btn @click="deleteLink(index)" :class="darkMode ? 'btn-light' : 'btn-dark'" class="mr-4" color="red">Delete</v-btn>
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
    <!-- New Link Dialog -->
    <new-link-view :dark-mode="darkMode" v-model:dialog="dialog" @link-created="addNewLink"></new-link-view>
  </v-container>
</template>

<script>
import { createLink, getLinksByType, updateLink, deleteLink } from '@/api/link-client.ts';
import NewLinkView from '@/admin/components/NewLinkView.vue'; // Adjust the path as needed

export default {
  name: 'LinkManagementView',
  components: {
    NewLinkView
  },
  props: {
    darkMode: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      socialMediaLinks: [],
      forms: [],
      hideBackground: false,
      dialog: false,
      linkData: {
        link: '',
        name: '',
        fontAwesomeIcon: '',
        mdiIcon: '',
        type: 'social', // Preselected value
        scope: 'external', // Preselected value
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      },
      fontAwesomeIcons: [
        { name: 'Facebook', icon: 'fa-brands fa-facebook' },
        { name: 'Twitter', icon: 'fa-brands fa-twitter' },
        { name: 'Instagram', icon: 'fa-brands fa-instagram' },
        { name: 'LinkedIn', icon: 'fa-brands fa-linkedin' },
        { name: 'Pinterest', icon: 'fa-brands fa-pinterest' },
        { name: 'Snapchat', icon: 'fa-brands fa-snapchat' },
        { name: 'TikTok', icon: 'fa-brands fa-tiktok' },
        { name: 'YouTube', icon: 'fa-brands fa-youtube' },
        { name: 'WhatsApp', icon: 'fa-brands fa-whatsapp' },
        { name: 'Reddit', icon: 'fa-brands fa-reddit' },
        { name: 'Tumblr', icon: 'fa-brands fa-tumblr' },
        { name: 'Flickr', icon: 'fa-brands fa-flickr' },
        { name: 'Vimeo', icon: 'fa-brands fa-vimeo' },
        { name: 'Dribbble', icon: 'fa-brands fa-dribbble' },
        { name: 'Behance', icon: 'fa-brands fa-behance' },
        { name: 'Medium', icon: 'fa-brands fa-medium' },
        { name: 'Discord', icon: 'fa-brands fa-discord' },
        { name: 'Twitch', icon: 'fa-brands fa-twitch' },
        { name: 'Slack', icon: 'fa-brands fa-slack' },
        { name: 'Telegram', icon: 'fa-brands fa-telegram' },
        { name: 'Stackoverflow', icon: 'fa-brands fa-stackoverflow' },
        { name: 'Gitlab', icon: 'fa-brands fa-gitlab' },
        { name: 'Github', icon: 'fa-brands fa-github' }
        // Add more Font Awesome social icons here
      ],
      mdiIcons: [
        { name: 'Facebook', icon: 'mdi mdi-facebook' },
        { name: 'Twitter', icon: 'mdi mdi-twitter' },
        { name: 'Instagram', icon: 'mdi mdi-instagram' },
        { name: 'LinkedIn', icon: 'mdi mdi-linkedin' },
        { name: 'Pinterest', icon: 'mdi mdi-pinterest' },
        { name: 'Snapchat', icon: 'mdi mdi-snapchat' },
        { name: 'TikTok', icon: 'mdi mdi-tiktok' },
        { name: 'YouTube', icon: 'mdi mdi-youtube' },
        { name: 'WhatsApp', icon: 'mdi mdi-whatsapp' },
        { name: 'Reddit', icon: 'mdi mdi-reddit' },
        { name: 'Tumblr', icon: 'mdi mdi-tumblr' },
        { name: 'Flickr', icon: 'mdi mdi-flickr' },
        { name: 'Vimeo', icon: 'mdi mdi-vimeo' },
        { name: 'Dribbble', icon: 'mdi mdi-dribbble' },
        { name: 'Behance', icon: 'mdi mdi-behance' },
        { name: 'Medium', icon: 'mdi mdi-medium' },
        { name: 'Discord', icon: 'mdi mdi-discord' },
        { name: 'Twitch', icon: 'mdi mdi-twitch' },
        { name: 'Slack', icon: 'mdi mdi-slack' },
        { name: 'Telegram', icon: 'mdi mdi-telegram' },
        { name: 'Stackoverflow', icon: 'mdi mdi-stackoverflow' },
        { name: 'Gitlab', icon: 'mdi mdi-gitlab' },
        { name: 'Github', icon: 'mdi mdi-github' }
        // Add more MDI social icons here
      ],
      error: null,
      success: null,
      errorIndex: null,
      successIndex: null,
      errorVisible: false,
      successVisible: false,
      rules: {
        required: value => !!value || 'Required.',
        validURL: value => {
          try {
            new URL(value);
            return true;
          } catch (e) {
            return 'Must be a valid URL.';
          }
        },
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
    socialMediaLinks: {
      handler(newLinks) {
        newLinks.forEach((link, index) => {
          this.$watch(() => link.fontAwesomeIcon, (newIcon) => {
            if (newIcon) {
              const iconName = this.fontAwesomeIcons.find(icon => icon.icon === newIcon).name;
              link.mdiIcon = this.mdiIcons.find(icon => icon.name === iconName).icon;
            }
          });

          this.$watch(() => link.mdiIcon, (newIcon) => {
            if (newIcon) {
              const iconName = this.mdiIcons.find(icon => icon.icon === newIcon).name;
              link.fontAwesomeIcon = this.fontAwesomeIcons.find(icon => icon.name === iconName).icon;
            }
          });
        });
      },
      deep: true,
      immediate: true,
    }
  },
  methods: {
    submitForm() {
      createLink(this.linkData)
        .then(response => {
          this.success = 'Link created successfully!';
          this.error = null;
          this.successIndex = null;
          this.errorIndex = null;
          this.successVisible = true;
          this.errorVisible = false;
          setTimeout(() => this.successVisible = false, 1000);
          this.fetchSocialMediaLinks();
          this.$emit('links-updated'); // Emit event
        })
        .catch(error => {
          this.error = 'Error creating link.';
          this.success = null;
          this.successIndex = null;
          this.successIndex = null;
          this.errorVisible = true;
          setTimeout(() => this.errorVisible = false, 1000);
        });
    },
    validateAndUpdateLink(index) {
      const formRef = this.$refs[`form-${index}`][0]; // Correctly access the form reference
      formRef.validate().then(success => {
        if (success) {
          this.updateLink(index);
        }
      });
    },
    updateLink(index) {
      const link = this.socialMediaLinks[index];
      updateLink(link.id, link)
        .then(response => {
          this.success = 'Link updated successfully!';
          this.error = null;
          this.successIndex = index;
          this.errorIndex = null;
          this.successVisible = true;
          this.errorVisible = false;
          setTimeout(() => this.successVisible = false, 1000);
          this.$emit('links-updated'); // Emit event
        })
        .catch(error => {
          this.error = 'Error updating link.';
          this.success = null;
          this.successIndex = null;
          this.errorIndex = index;
          this.successVisible = false;
          this.errorVisible = true;
          setTimeout(() => this.errorVisible = false, 1000);
        });
    },
    deleteLink(index) {
      const linkId = this.socialMediaLinks[index].id;
      deleteLink(linkId)
        .then(response => {
          this.success = 'Link deleted successfully!';
          this.error = null;
          this.successIndex = index;
          this.errorIndex = null;
          this.successVisible = true;
          this.errorVisible = false;
          setTimeout(() => this.successVisible = false, 500);
          this.socialMediaLinks.splice(index, 1);
          this.$emit('links-updated'); // Emit event
        })
        .catch(error => {
          this.error = 'Error deleting link.';
          this.success = null;
          this.successIndex = null;
          this.errorIndex = index;
          this.successVisible = false;
          this.errorVisible = true;
          setTimeout(() => this.errorVisible = false, 500);
        });
    },
    fetchSocialMediaLinks() {
      getLinksByType('social')
        .then(response => {
          this.socialMediaLinks = response.data.map(link => ({
            ...link
          }));
          this.successIndex = null;
          this.errorIndex = null;
          this.successVisible = false;
          this.errorVisible = false;
        })
        .catch(error => {
          this.error = 'Error fetching links.';
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
    addNewLink(newLink) {
      this.socialMediaLinks.push(newLink); // Add the new link to the list
      this.$emit('link-created', newLink); // Emit event for parent component
      this.$emit('links-updated'); // Emit event
    },
    syncIcons(link, type) {
      if (type === 'fontAwesome') {
        const iconName = this.fontAwesomeIcons.find(icon => icon.icon === link.fontAwesomeIcon).name;
        link.mdiIcon = this.mdiIcons.find(icon => icon.name === iconName).icon;
      } else if (type === 'mdi') {
        const iconName = this.mdiIcons.find(icon => icon.icon === link.mdiIcon).name;
        link.fontAwesomeIcon = this.fontAwesomeIcons.find(icon => icon.name === iconName).icon;
      }
    }
  },
  mounted() {
    this.setupPageHead(); // Initial setup of the head
    this.fetchSocialMediaLinks(); // Fetch social media links on mount
  },
  computed: {
    sortedFontAwesomeIcons() {
      return this.fontAwesomeIcons.sort((a, b) => a.name.localeCompare(b.name));
    },
    sortedMdiIcons() {
      return this.mdiIcons.sort((a, b) => a.name.localeCompare(b.name));
    },
    sortedSocialMediaLinks() {
      return this.socialMediaLinks.sort((a, b) => a.name.localeCompare(b.name));
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
