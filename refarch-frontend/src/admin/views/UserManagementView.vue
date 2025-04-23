<template>
  <v-container :class="[darkMode ? 'container-dark' : 'container-light', hideBackground ? 'hide-background' : '']" class="fill-height" fluid>
    <v-row class="text-justified">
      <v-col class="mb-4" cols="12">
        <v-card :class="{ 'card-dark': darkMode, 'card-light': !darkMode }" class="elevation-1" outlined>
          <v-card-title>
            <v-row class="d-flex align-center flex-wrap">
              <v-col class="d-flex align-center">
                <i class="fa-solid fa-user mr-2"></i>
                <span>Edit/Delete Users</span>
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
                  <span class="d-none d-md-inline">Add User</span>
                  <span class="d-md-none">Add</span>
                  <i class="fa-solid fa-user ml-2"></i>
                </v-btn>
              </v-col>
              <v-col class="d-flex d-md-none justify-center">
                <router-link :to="{ name: 'AdminDashboard' }" class="text-decoration-none">
                  <v-btn color="secondary" text>
                    Admin <i class="fa-solid fa-arrow-left ml-2"></i>
                  </v-btn>
                </router-link>
                <v-btn class="ml-2" color="secondary" text @click="dialog = true">
                  Add <i class="fa-solid fa-user ml-2"></i>
                </v-btn>
              </v-col>
            </v-row>
          </v-card-title>
          <v-card-text>
            <v-expansion-panels accordion>
              <v-expansion-panel v-for="(user, index) in sortedUsers" :key="user.id" :class="[darkMode ? 'container-dark' : 'container-light', hideBackground ? 'hide-background' : 'hide-background']">
                <v-expansion-panel-title>
                  <img :src="user.profilePicture" alt="profile picture" class="mr-4" style="width: 40px; height: 40px; border-radius: 50%;">
                  {{ user.name }}
                </v-expansion-panel-title>
                <v-expansion-panel-text>
                  <v-form :ref="'form-' + index" v-model="forms[index]" @submit.prevent="validateAndUpdateUser(index)">
                    <v-text-field
                      v-model="user.name"
                      label="Name"
                      :rules="[rules.required]"
                      required
                    ></v-text-field>
                    <v-text-field
                      v-model="user.email"
                      label="Email"
                      :rules="[rules.required, rules.validEmail]"
                      required
                    ></v-text-field>
                    <v-text-field
                      v-model="user.profilePicture"
                      label="Profile Picture URL"
                      :rules="[rules.required, rules.validURL]"
                      required
                    ></v-text-field>
                    <v-select
                      v-model="user.roles"
                      :items="availableRoles"
                      label="Roles"
                      item-title="name"
                      item-value="role"
                      multiple
                      clearable
                      :rules="[rules.required]"
                    ></v-select>
                    <div v-for="(bio, bioIndex) in user.bio" :key="bioIndex">
                    <v-textarea
                      v-model="bio.bio"
                      :label="`Bio (${getLanguageAbbreviation(bio.languageId)})`"
                      rows="5"
                      :rules="[rules.required]"
                      required
                    ></v-textarea>
                  </div>
                    <v-btn :disabled="!forms[index]" type="submit" color="secondary" class="mr-4">Update</v-btn>
                    <v-btn @click="deleteUser(index)" :class="darkMode ? 'btn-light' : 'btn-dark'" class="mr-4" color="red">Delete</v-btn>
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
    <!-- New User Dialog -->
    <new-user-view :dark-mode="darkMode" v-model:dialog="dialog" @user-created="addNewUser"></new-user-view>
  </v-container>
</template>

<script>
import { createUser, fetchUsersWithAllTranslatedBios, updateUser, deleteUser } from '@/api/user-client.ts';
import { fetchLanguageById } from '@/api/language-client.ts';
import NewUserView from '@/admin/components/NewUserView.vue'; // Adjust the path as needed

export default {
  name: 'UserManagementView',
  components: {
    NewUserView
  },
  props: {
    darkMode: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      users: [],
      forms: [],
      hideBackground: false,
      dialog: false,
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
      errorIndex: null,
      successIndex: null,
      errorVisible: false,
      successVisible: false,
      languageAbbreviations: {},
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
    darkMode(newValue) {
      // Always trigger the hide/show behavior regardless of darkMode value
      this.hideBackground = true;
      setTimeout(() => {
        this.hideBackground = false;
      }, 500); // Adjusted to 500ms for demonstration purposes
    },
    '$route'() {
      this.setupPageHead(); // Update the head when the route changes
    }
  },
  methods: {
    submitForm() {
      createUser(this.userData)
        .then(response => {
          this.success = 'User created successfully!';
          this.error = null;
          this.successIndex = null;
          this.errorIndex = null;
          this.successVisible = true;
          this.errorVisible = false;
          setTimeout(() => this.successVisible = false, 1000);
          this.fetchUsersWithAllTranslatedBios();
          this.$emit('users-updated'); // Emit event
        })
        .catch(error => {
          this.error = 'Error creating user.';
          this.success = null;
          this.successIndex = null;
          this.errorIndex = null;
          this.errorVisible = true;
          setTimeout(() => this.errorVisible = false, 1000);
        });
    },
    validateAndUpdateUser(index) {
      const formRef = this.$refs[`form-${index}`][0]; // Correctly access the form reference
      formRef.validate().then(success => {
        if (success) {
          this.updateUser(index);
        }
      });
    },
    updateUser(index) {
      const user = this.users[index];
      updateUser(user.id, user)
        .then(response => {
          this.success = 'User updated successfully!';
          this.error = null;
          this.successIndex = index;
          this.errorIndex = null;
          this.successVisible = true;
          this.errorVisible = false;
          setTimeout(() => this.successVisible = false, 1000);
          this.$emit('users-updated'); // Emit event
        })
        .catch(error => {
          this.error = 'Error updating user.';
          this.success = null;
          this.successIndex = null;
          this.errorIndex = index;
          this.successVisible = false;
          this.errorVisible = true;
          setTimeout(() => this.errorVisible = false, 1000);
        });
    },
    deleteUser(index) {
      const userId = this.users[index].id;
      deleteUser(userId)
        .then(response => {
          this.success = 'User deleted successfully!';
          this.error = null;
          this.successIndex = index;
          this.errorIndex = null;
          this.successVisible = true;
          this.errorVisible = false;
          setTimeout(() => this.successVisible = false, 500);
          this.users.splice(index, 1);
          this.$emit('users-updated'); // Emit event
        })
        .catch(error => {
          this.error = 'Error deleting user.';
          this.success = null;
          this.successIndex = null;
          this.errorIndex = index;
          this.successVisible = false;
          this.errorVisible = true;
          setTimeout(() => this.errorVisible = false, 500);
        });
    },
    fetchUsersWithAllTranslatedBios() {
      fetchUsersWithAllTranslatedBios()
        .then(response => {
          this.users = response.data.map(user => ({
            ...user
          }));
          this.successIndex = null;
          this.errorIndex = null;
          this.successVisible = false;
          this.errorVisible = false;
        })
        .catch(error => {
          this.error = 'Error fetching users.';
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
    addNewUser(newUser) {
      this.users.push(newUser); // Add the new user to the list
      this.$emit('user-created', newUser); // Emit event for parent component
      this.$emit('users-updated'); // Emit event
    },
    getLanguageAbbreviation(languageId) {
    if (!this.languageAbbreviations[languageId]) {
      fetchLanguageById(languageId)
        .then(response => {
          this.languageAbbreviations = { ...this.languageAbbreviations, [languageId]: response.data.abbreviation };
        })
        .catch(error => {
          console.error('Error fetching language abbreviation:', error);
        });
    }
    return this.languageAbbreviations[languageId] || '';
  }
  },
  mounted() {
    this.setupPageHead(); // Initial setup of the head
    this.fetchUsersWithAllTranslatedBios(); // Fetch users on mount
  },
  computed: {
    sortedUsers() {
      return this.users.sort((a, b) => a.name.localeCompare(b.name));
    }
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
