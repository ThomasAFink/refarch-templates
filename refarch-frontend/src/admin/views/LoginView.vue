<template>
  <v-container :class="[darkMode ? 'container-dark' : 'container-light', hideBackground ? 'hide-background' : '']"
    class="fill-height" fluid>
    <v-row justify="center" align="center">
      <v-col cols="12" sm="8" md="4">
        <v-card :class="{ 'card-dark': darkMode, 'card-light': !darkMode }" class="elevation-1" outlined>
          <v-toolbar color="primary" dark flat>
            <v-toolbar-title>Login</v-toolbar-title>
          </v-toolbar>
          <v-card-text>
            <v-form @submit.prevent="handleLogin">
              <v-text-field v-model="email" label="Email" prepend-icon="mdi-account" type="email" required></v-text-field>
              <v-text-field v-model="password" label="Password" prepend-icon="mdi-lock"
                :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                :type="showPassword ? 'text' : 'password'"
                @click:append="showPassword = !showPassword" required></v-text-field>
              <v-alert v-if="error" type="error" class="mb-3" dismissible>{{ error }}</v-alert>
              <v-alert v-if="success" type="success" class="mb-3" dismissible>{{ success }}</v-alert>
              <v-btn type="submit" color="success">Login</v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { ref } from 'vue';
import { useStore } from 'vuex';
import { login } from '@/api/auth-client.ts';
import { getCurrentUser, getCurrentUserRole } from '@/api/auth-client.ts';
import { useRouter } from 'vue-router';

export default {
  name: 'LoginView',
  props: {
    darkMode: {
      type: Boolean,
      required: true
    }
  },
  setup(props) {
    const store = useStore();
    const email = ref('');
    const password = ref('');
    const error = ref(null);
    const success = ref(null);
    const showPassword = ref(false);
    const router = useRouter();

    const handleLogin = async () => {
      try {
        const response = await login(email.value, password.value);
        success.value = "Login successful!";
        error.value = null;

        const user = await getCurrentUser();
        const roles = await getCurrentUserRole();
        
        if (roles.admin || roles.author && !roles.userNoAuth) {
          store.commit('SET_USER', user);
          store.commit('SET_AUTH', true);
          router.push({ name: 'AdminDashboard' });
        } else if (roles.user && !roles.userNoAuth) {
          router.push({ name: 'Home' });
          store.commit('SET_USER', user);
          store.commit('SET_AUTH', true);
        } else {
          router.push({ name: 'Login' });
        }
      } catch (err) {
        error.value = 'Failed to login.';
        success.value = null;
      }
    };

    return {
      email,
      password,
      error,
      success,
      showPassword,
      handleLogin
    };
  },
  watch: {
    darkMode(newValue) {
      // Always trigger the hide/show behavior regardless of darkMode value
      this.hideBackground = true;
      setTimeout(() => {
        this.hideBackground = false;
      }, 500); // Adjusted to 500ms for demonstration purposes
    }
  },
  data() {
    return {
      hideBackground: false, // Add this line
    };
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
  /* Example way to hide the background */
}


.card-dark {
  background-color: #424242; /* Dark grey background for dark mode */
  color: white; /* Light text for dark mode */
}

.card-light {
  background-color: white; /* White background for light mode */
  color: #333; /* Dark text for light mode */
}
</style>
