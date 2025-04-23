<template>
    <v-dialog v-model="internalUserDialog" max-width="600px">
      <v-card :class="{ 'card-dark': darkMode, 'card-light': !darkMode }" class="elevation-1" outlined>
        <v-toolbar color="primary" dark flat>
          <v-toolbar-title>Add New User</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn icon @click="closeDialog">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-toolbar>
        <v-card-text>
          <v-form ref="form" v-model="formValid" @submit.prevent="submitForm">
            <v-text-field
              v-model="userData.name"
              label="Name"
              :rules="[rules.required]"
              required
            ></v-text-field>
            <v-text-field
              v-model="userData.email"
              label="Email"
              :rules="[rules.required, rules.validEmail]"
              required
            ></v-text-field>
            <v-text-field
              v-model="userData.bio"
              label="Bio"
              :rules="[rules.required]"
              required
            ></v-text-field>
            <v-text-field
              v-model="userData.profilePicture"
              label="Profile Picture URL"
              :rules="[rules.required, rules.validURL]"
              required
            ></v-text-field>
            <v-select
              v-model="userData.roles"
              :items="availableRoles"
              label="Roles"
              item-title="name"
              item-value="role"
              multiple
              clearable
              :rules="[rules.required]"
            ></v-select>
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
  import { createUser } from '@/api/user-client.ts';
  
  export default {
    name: 'NewUserView',
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
        internalUserDialog: this.dialog,
        formValid: false,
        userData: {
          name: '',
          email: '',
          bio: '',
          profilePicture: '',
          roles: []
        },
        availableRoles: [
          { name: 'Admin', role: 'ADMIN' },
          { name: 'Author', role: 'AUTHOR' },
          { name: 'User', role: 'USER' }
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
          validEmail: value => /.+@.+\..+/.test(value) || 'E-mail must be valid.'
        }
      };
    },
    watch: {
      dialog(val) {
        this.internalUserDialog = val;
      },
      internalUserDialog(val) {
        this.$emit('update:dialog', val);
      }
    },
    methods: {
      submitForm() {
        createUser(this.userData)
          .then(response => {
            this.success = 'User created successfully!';
            this.error = null;
            this.successVisible = true;
            this.errorVisible = false;
            setTimeout(() => {
              this.successVisible = false;
              this.$emit('user-created', response.data); // Emit event with new user data
              this.$emit('update:dialog', false); // Close the dialog
              this.$emit('users-updated'); // Emit users-updated event
            }, 1000);
          })
          .catch(error => {
            this.error = 'Error creating user.';
            this.success = null;
            this.successVisible = false;
            this.errorVisible = true;
            setTimeout(() => this.errorVisible = false, 1000);
          });
      },
      closeDialog() {
        this.internalUserDialog = false;
      }
    }
  };
  </script>
  
  <style scoped>
  .card-dark {
    background-color: #424242;
    color: white;
  }
  .card-light {
    background-color: white;
    color: #333;
  }
  .fade-out {
    transition: opacity 1s ease-out;
    opacity: 1;
  }
  .fade-out[data-fade="false"] {
    opacity: 0;
  }
  </style>
  