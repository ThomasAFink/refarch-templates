<template>
    <v-dialog v-model="internalLinkDialog" max-width="600px">
      <v-card :class="{ 'card-dark': darkMode, 'card-light': !darkMode }" class="elevation-1" outlined>
        <v-toolbar color="primary" dark flat>
          <v-toolbar-title>Add New Link</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="closeDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-toolbar>
        <v-card-text>
          <v-form ref="form" v-model="formValid" @submit.prevent="submitForm">
            <v-text-field
              v-model="linkData.link"
              label="Link"
              :rules="[rules.required, rules.validURL]"
              required
            ></v-text-field>
            <v-text-field
              v-model="linkData.name"
              label="Name"
              :rules="[rules.required]"
              required
            ></v-text-field>
  
            <v-select
              v-model="linkData.fontAwesomeIcon"
              :items="sortedFontAwesomeIcons"
              label="Font Awesome Icon"
              item-title="name"
              item-value="icon"
              clearable
              :rules="[rules.required]"
              @change="syncIcons('fontAwesome')"
            ></v-select>
  
            <v-select
              v-model="linkData.mdiIcon"
              :items="sortedMdiIcons"
              label="MDI Icon"
              item-title="name"
              item-value="icon"
              clearable
              :rules="[rules.required]"
              @change="syncIcons('mdi')"
            ></v-select>
  
            <v-text-field v-model="linkData.type" label="Type" disabled></v-text-field>
            <v-text-field v-model="linkData.scope" label="Scope" disabled></v-text-field>
            <v-btn :disabled="!formValid" type="submit" color="secondary" class="mr-4">Submit</v-btn>
            <v-btn @click="closeDialog" color="red" class="mr-4">Cancel</v-btn>
            <v-alert v-if="errorVisible" type="error" class="mt-3 fade-out" dismissible>{{ error }}</v-alert>
            <v-alert v-if="successVisible" type="success" class="mt-3 fade-out" dismissible>{{ success }}</v-alert>
          </v-form>
        </v-card-text>
      </v-card>
    </v-dialog>
  </template>
  
  <script>
  import { createLink } from '@/api/link-client.ts';
  
  export default {
    name: 'NewLinkView',
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
        internalLinkDialog: this.dialog,
        formValid: false,
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
        },
      };
    },
    watch: {
      dialog(val) {
        this.internalLinkDialog = val;
      },
      internalLinkDialog(val) {
        this.$emit('update:dialog', val);
      },
      'linkData.fontAwesomeIcon'(newIcon) {
        if (newIcon) {
          const iconName = this.fontAwesomeIcons.find(icon => icon.icon === newIcon).name;
          this.linkData.mdiIcon = this.mdiIcons.find(icon => icon.name === iconName).icon;
        }
      },
      'linkData.mdiIcon'(newIcon) {
        if (newIcon) {
          const iconName = this.mdiIcons.find(icon => icon.icon === newIcon).name;
          this.linkData.fontAwesomeIcon = this.fontAwesomeIcons.find(icon => icon.name === iconName).icon;
        }
      }
    },
    methods: {
      submitForm() {
        createLink(this.linkData)
          .then(response => {
            this.success = 'Link created successfully!';
            this.error = null;
            this.successVisible = true;
            this.errorVisible = false;
            setTimeout(() => {
              this.successVisible = false;
              this.$emit('link-created', response.data); // Emit event with new link data
              this.$emit('update:dialog', false); // Close the dialog
              this.$emit('links-updated'); // Emit links-updated event
            }, 1000);
          })
          .catch(error => {
            this.error = 'Error creating link.';
            this.success = null;
            this.successVisible = false;
            this.errorVisible = true;
            setTimeout(() => this.errorVisible = false, 1000);
          });
      },
      closeDialog() {
        this.internalLinkDialog = false;
      },
      syncIcons(type) {
        if (type === 'fontAwesome') {
          const iconName = this.fontAwesomeIcons.find(icon => icon.icon === this.linkData.fontAwesomeIcon).name;
          this.linkData.mdiIcon = this.mdiIcons.find(icon => icon.name === iconName).icon;
        } else if (type === 'mdi') {
          const iconName = this.mdiIcons.find(icon => icon.icon === this.linkData.mdiIcon).name;
          this.linkData.fontAwesomeIcon = this.fontAwesomeIcons.find(icon => icon.name === iconName).icon;
        }
      }
    },
    computed: {
      sortedFontAwesomeIcons() {
        return this.fontAwesomeIcons.sort((a, b) => a.name.localeCompare(b.name));
      },
      sortedMdiIcons() {
        return this.mdiIcons.sort((a, b) => a.name.localeCompare(b.name));
      },
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
  </style>
  