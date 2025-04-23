<template>
  <v-app
    :dark="darkMode"
    :class="`theme--${darkMode ? 'dark' : 'light'}`"
  >
    <v-app-bar
      :class="[bgColorClass, isAdminRoute ? 'admin-header' : '']"
      class="app-bar"
      flat
      app
    >
      <router-link
        :to="`/${currentLocale}`"
        class="d-flex align-center text-decoration-none"
      >
        <v-row
          no-gutters
          align="center"
          class="fill-height"
        >
          <v-col
            cols="auto"
            class="d-flex align-center pl-4"
          >
            <v-img
              :src="logoSrc"
              class="logo-image"
            ></v-img>
          </v-col>
          <v-col>
            <v-toolbar-title class="text-no-wrap pl-2"
              >TrafficGCN <span v-if="isAdminRoute">| CMS</span></v-toolbar-title
            >
          </v-col>
        </v-row>
      </router-link>
      <v-spacer></v-spacer>
      <v-btn
        icon
        v-if="$vuetify.display.smAndDown"
        @click="drawer = !drawer"
      >
        <v-icon>mdi-menu</v-icon>
      </v-btn>
      <template v-if="$vuetify.display.mdAndUp">
        <v-btn
          v-if="isAdminRoute"
          v-for="section in adminSections"
          :key="section.managementRoute"
          icon
          :to="{ name: section.managementRoute }"
          
        >
        <v-icon style="font-size: 1rem" v-html="section.icon"></v-icon>
        </v-btn>
        <v-btn
          v-if="!isAdminRoute"
          v-for="link in socialMediaLinks"
          :key="link.href"
          icon
          :href="link.href"
          :class="bgColorClass"
          target="_blank"
        >
          <i
            v-if="link.fontAwesomeIcon"
            :class="link.fontAwesomeIcon"
          ></i>
          <v-icon v-else-if="link.mdiIcon">{{ link.mdiIcon }}</v-icon>
          <span v-else>{{ link.name }}</span>
        </v-btn>
        <v-select
          v-if="!isAdminRoute && availableLanguages.length > 1"
          :items="availableLanguages"
          v-model="currentLocale"
          @update:modelValue="changeLanguage"
          hide-details
          dense
          outlined
          small
          class="language-dropdown"
        >
        </v-select>
        <v-btn
          icon
          @click="toggleDarkMode"
        >
          <v-icon>{{
            darkMode ? "mdi-weather-night" : "mdi-weather-sunny"
          }}</v-icon>
        </v-btn>
        <v-btn
          v-if="isAuthenticated && user.roles.admin"
          icon
          :to="'/admin'"
          large
        >
          <v-icon>mdi-view-dashboard</v-icon>
        </v-btn>
        <v-btn
          v-if="isAuthenticated"
          icon
          :to="'/profile'"
        >
          <v-avatar size="25">
            <v-img
              :src="user.profilePicture"
              alt="Profile"
              class="grey lighten-4"
              aspect-ratio="1"
              contain
            ></v-img>
          </v-avatar>
        </v-btn>
        <v-btn
          v-if="isAuthenticated"
          icon
          @click="logout"
        >
          <v-icon>mdi-logout</v-icon>
        </v-btn>
      </template>
    </v-app-bar>
    <v-navigation-drawer
      v-model="drawer"
      app
      temporary
      location="right"
      :class="bgColorClass"
    >
      <v-row>
        <v-col
          cols="12"
          sm="12"
          md="12"
          lg="12"
          xl="12"
        >
          <v-select
            v-if="!isAdminRoute && availableLanguages.length > 1"
            :items="availableLanguages"
            v-model="currentLocale"
            @update:modelValue="changeLanguage"
            hide-details
            dense
            outlined
            small
            class="language-dropdown"
          >
          </v-select>
        </v-col>
      </v-row>
      <v-row>
        <v-col
          cols="12"
          sm="12"
          md="12"
          lg="12"
          xl="12"
        >
          <v-list :class="bgColorClass">
            <v-list-item
              v-for="route in isAdminRoute ? adminSections : pageRoutes"
              :key="route.pageName || route.managementRoute"
            >
              <v-list-item-content>
                <router-link :to="route.fullPath || { name: route.managementRoute }">
                  <v-list-item-title><span class="mr-2" v-html="route.icon"></span>{{ route.title }}</v-list-item-title>
                </router-link>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-col>
      </v-row>
      <v-row>
        <v-col
          cols="12"
          sm="12"
          md="12"
          lg="12"
          xl="12"
        >
          <v-btn
            v-if="!isAdminRoute"
            v-for="link in socialMediaLinks"
            :key="link.href"
            icon
            :href="link.href"
            :class="bgColorClass"
            target="_blank"
          >
            <i
              v-if="link.fontAwesomeIcon"
              :class="link.fontAwesomeIcon"
            ></i>
            <v-icon v-else-if="link.mdiIcon">{{ link.mdiIcon }}</v-icon>
            <span v-else>{{ link.name }}</span>
          </v-btn>
          <v-btn
            icon
            @click="toggleDarkMode"
            :class="bgColorClass"
          >
            <v-icon>{{
              darkMode ? "mdi-weather-night" : "mdi-weather-sunny"
            }}</v-icon>
          </v-btn>
          <v-btn
            v-if="isAuthenticated && user.roles.admin"
            icon
            :to="'/admin'"
            :class="bgColorClass"
            large
          >
            <v-icon>mdi-view-dashboard</v-icon>
          </v-btn>
          <v-btn
            v-if="isAuthenticated"
            icon
            :to="'/profile'"
            :class="bgColorClass"
          >
            <v-avatar size="25">
              <v-img
                :src="user.profilePicture"
                alt="Profile"
                class="grey lighten-4"
                aspect-ratio="1"
                contain
              ></v-img>
            </v-avatar>
          </v-btn>
          <v-btn
            v-if="isAuthenticated"
            icon
            @click="logout"
            :class="bgColorClass"
          >
            <v-icon>mdi-logout</v-icon>
          </v-btn>
        </v-col>
      </v-row>
    </v-navigation-drawer>
    <v-main :class="bgColorClass">
      <router-view
        :dark-mode="darkMode"
        :admin-sections="adminSections"
        @links-updated="loadSocialMediaLinks"
      ></router-view>
    </v-main>
    <v-footer :class="bgColorClass">
      <v-container class="pt-0 pb-0 mt-0 mb-0">
        <v-row class="pt-0 pb-0 mt-0 mb-0">
          <v-col
            v-for="n in 4"
            :key="n"
            cols="12"
            sm="6"
            md="6"
            lg="3"
            xl="3"
            class="pt-0 pb-0 mt-0 mb-0"
          >
            <v-list :class="[bgColorClass, 'pt-0', 'pb-0', 'mt-0', 'mb-0']">
              <v-list-item
                v-for="route in (isAdminRoute ? adminSections : pageRoutes).slice(
                  (n - 1) * Math.ceil((isAdminRoute ? adminSections : pageRoutes).length / 4),
                  n * Math.ceil((isAdminRoute ? adminSections : pageRoutes).length / 4)
                )"
                :key="route.managementRoute || route.pageName"
                class="pt-0 pb-0 mt-0 mb-0"
              >
                <v-list-item-content class="pt-0 pb-0 mt-0 mb-0">
                  <router-link :to="route.managementRoute ? { name: route.managementRoute } : route.fullPath">
                    <v-list-item-title class="text-wrap" ><span class="mr-2" v-html="route.icon"></span>{{
                      route.title
                    }}</v-list-item-title>
                  </router-link>
                </v-list-item-content>
              </v-list-item>
            </v-list>
          </v-col>
          <v-col cols="12">
            <div class="text-center pt-0 pb-0 mt-0 mb-0">
              <v-btn
                v-if="!isAdminRoute"
                v-for="link in socialMediaLinks"
                :key="link.href"
                icon
                :href="link.href"
                :class="[bgColorClass, 'pt-0', 'pb-0', 'mt-0', 'mb-0']"
                target="_blank"
              >
                <i
                  v-if="link.fontAwesomeIcon"
                  :class="link.fontAwesomeIcon"
                ></i>
                <v-icon v-else-if="link.mdiIcon">{{ link.mdiIcon }}</v-icon>
                <span v-else>{{ link.name }}</span>
              </v-btn>
            </div>
          </v-col>
        </v-row>
      </v-container>
    </v-footer>
  </v-app>
</template>

<script>
import { computed, onMounted, ref, watchEffect, watch } from "vue";
import { useStore } from 'vuex';
import { useRoute, useRouter } from "vue-router";
import darkLogo from "@/assets/logo_icon_dark_trans.png";
import lightLogo from "@/assets/logo_icon_light_trans.png";
import { getLinksByType } from "@/api/link-client.ts"; // Adjust the path as necessary
import i18n from "@/i18n.ts";
import { getPagesByLanguageAbbreviation } from "@/api/page-client.ts";
import { logout as logoutService, getCurrentUser } from '@/api/auth-client.ts';

export default {
  name: "App",
  setup() {
    const store = useStore();
    const router = useRouter();
    const route = useRoute();
    const drawer = ref(false);
    const socialMediaLinks = ref([]);
    const pageRoutes = ref([]);
    const isAdminRoute = ref(false);

    const darkMode = computed({
      get: () => store.state.darkMode,
      set: (value) => store.commit('TOGGLE_DARK_MODE', value)
    });

    const logoSrc = computed(() => {
      return darkMode.value ? darkLogo : lightLogo;
    });

    const currentLocale = computed(() => i18n.global.locale);
    const availableLanguages = computed(() => {
      let languages = Object.keys(i18n.global.messages).sort();
      const prioritized = ["en"];
      languages = languages.filter((lang) => !prioritized.includes(lang));
      prioritized.forEach((lang) => {
        if (i18n.global.messages.hasOwnProperty(lang)) {
          languages.unshift(lang);
        }
      });
      return languages;
    });

    const bgColorClass = computed(() => {
      return `background-${darkMode.value ? "dark" : "light"}`;
    });

    const toggleDarkMode = () => {
      darkMode.value = !darkMode.value;
    };

    const changeLanguage = async (newLocale) => {
      const currentPath = router.currentRoute.value.fullPath;
      const newPath = currentPath.replace(/^\/[^\/]+/, `/${newLocale}`);
      i18n.global.locale = newLocale;
      await router.push(newPath);
    };

    const loadSocialMediaLinks = async () => {
      try {
        const response = await getLinksByType("social");
        socialMediaLinks.value = response.data.map((link) => ({
          ...link, href: link.link
        }));
      } catch (error) {
        console.error("Failed to fetch social media links:", error);
      }
    };

    const logout = () => {
      logoutService();
      store.commit('SET_USER', null);
      store.commit('SET_AUTH', false);
      router.push({ name: 'Login' });
    };

    const isAuthenticated = computed(() => store.state.isAuthenticated);
    const user = computed(() => store.state.user);
    const userRoles = computed(() => {
      return store.state.user?.roles ?? { admin: false, author: false, user: false };
    });

    const adminSections = ref([
      { title: 'Blog Management', icon: '<i class="fa-solid fa-pencil"></i>', subtitle: 'Create engaging content to attract more readers.', btn: 'New Post <i class="fa-solid fa-file-circle-plus ml-2"></i>', creationRoute: 'AdminNewBlogPost', managementRoute: 'AdminPageManagement', dialogComponent: 'NewPostView' },
      { title: 'Insights', icon: '<i class="fa-solid fa-chart-line"></i>', subtitle: 'Visitor analytics and post performance.', btn: null, creationRoute: 'AdminNewLink', managementRoute: 'AdminPageManagement' },
      { title: 'Page management', icon: '<i class="fa-solid fa-file"></i>', subtitle: 'Edit and update page contents and layout.', btn: 'New Page <i class="fa-solid fa-file-circle-plus ml-2"></i>', creationRoute: 'AdminNewLink', managementRoute: 'AdminPageManagement', dialogComponent: 'NewPostView' },
      { title: 'File management', icon: '<i class="fa-solid fa-folder"></i>', subtitle: 'Organize, edit, or delete files.', btn: 'Upload File <i class="fa-solid fa-folder-plus ml-2"></i>', creationRoute: 'AdminNewLink', managementRoute: 'AdminPageManagement', dialogComponent: 'NewPostView' },
      { title: 'User management', icon: '<i class="fa-solid fa-user"></i>', subtitle: 'Manage user roles and access rights.', btn: 'Create User <i class="fa-solid fa-user-plus ml-2"></i>', creationRoute: 'UserNewLink', managementRoute: 'AdminUserManagement', dialogComponent: 'NewUserView' },
      { title: 'Admin config', icon: '<i class="fa-solid fa-gear"></i>', subtitle: 'Adjust site-wide settings and configurations.', btn: null, creationRoute: 'AdminNewLink', managementRoute: 'AdminPageManagement' },
      { title: 'Link management', icon: '<i class="fa-solid fa-link"></i>', subtitle: 'Manage hyperlinks and SEO links across the site.', btn: 'Add Link <i class="fa-solid fa-link ml-2"></i>', creationRoute: 'AdminNewLink', managementRoute: 'AdminLinkManagement', dialogComponent: 'NewLinkView' },
      { title: 'Language management', icon: '<i class="fa-solid fa-language"></i>', subtitle: 'Configure available languages and translations.', btn: 'Add Language <i class="fa-solid fa-language ml-2"></i>', creationRoute: 'AdminNewLanguage', managementRoute: 'AdminLanguageManagement', dialogComponent: 'NewLanguageView' }
    ]);

    watch(
      () => route.path,
      (newPath) => {
        isAdminRoute.value = newPath.startsWith("/admin");
      },
      { immediate: true }
    );

    watchEffect(async () => {
      if (currentLocale.value) {
        const pagesResponse = await getPagesByLanguageAbbreviation(currentLocale.value);
        if (pagesResponse.data.length > 0) {
          pageRoutes.value = pagesResponse.data.map(page => ({
            ...page,
            fullPath: `/${currentLocale.value}${page.link}`
          }));
        } else {
          pageRoutes.value = [];
        }
      }
    });

    onMounted(() => {
      loadSocialMediaLinks();
    });

    return {
      darkMode,
      drawer,
      toggleDarkMode,
      bgColorClass,
      logoSrc,
      pageRoutes,
      currentLocale,
      availableLanguages,
      changeLanguage,
      socialMediaLinks,
      isAuthenticated,
      user,
      logout,
      isAdminRoute,
      adminSections,
      loadSocialMediaLinks, // Add this to the return object
    };
  }
};
</script>

<style scoped>
.container-light {
  width: 100% !important;
  max-width: 100%;
  position: relative;
  background-image: url("@/assets/neural_net_light.svg");
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
  background-image: url("@/assets/neural_net_dark.svg");
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  background-blend-mode: color-burn;
  background-blend-mode: lighten;
  transition: background-image 1s ease-in-out;
}

.card-dark {
  background-color: #424242; /* Dark grey background for dark mode */
  color: white; /* Light text for dark mode */
}

.card-light {
  background-color: white; /* White background for light mode */
  color: #333; /* Dark text for light mode */
}

.hide-background {
  background-image: none !important;
  /* Example way to hide the background */
}

/* Button styling for light and dark modes */
.btn-dark {
  color: #333 !important;
  background-color: white;
}

.btn-light {
  color: white;
  background-color: #333;
}

/* Admin header style */
.admin-header {
  background-color: black !important;
}
</style>
