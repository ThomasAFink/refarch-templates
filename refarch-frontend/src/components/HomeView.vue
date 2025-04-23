<template>
  <v-container :class="[darkMode ? 'container-dark' : 'container-light', hideBackground ? 'hide-background' : '']">
    <v-row class="text-center">
      <v-col cols="12">
        <v-img
          :src="logoSrc"
          class="my-3" contain height="100" />
      </v-col>

      <v-col class="mb-4" cols="12">
        <h1 class="display-2 font-weight-bold mb-3" v-html="$t('welcomeMessage')"></h1>
        <p class="subheading font-weight-regular" v-html="$t('welcomeMessageExtended')"></p>
      </v-col>

      <v-col class="mb-5" cols="12" sm="6">
        <h2 class="headline font-weight-bold mb-5" v-html="$t('exploreOurWork')"></h2>
        <router-link v-for="(link, i) in importantLinksComputed" :key="i" :to="link.href" class="subheading mx-3">
          {{ link.text }}
        </router-link>
      </v-col>

      <v-col class="mb-5" cols="12" sm="6">
        <h2 class="headline font-weight-bold mb-5" v-html="$t('getInvolved')"></h2>
        <v-row justify="center">
          <router-link v-for="(link, i) in ecosystemLinksComputed" :key="i" :to="link.href" class="subheading mx-3">
            {{ link.text }}
          </router-link>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
  <v-container class="video-container" fluid>
    <video autoplay muted loop class="background-video">
      <source :src="videoSrc1" type="video/mp4">
      Your browser does not support the video tag.
    </video>
    <div :class="darkMode ? 'frosted-overlay-dark' : 'frosted-overlay-light'"></div>
  </v-container>
  <v-container :class="[darkMode ? 'container-dark' : 'container-light', hideBackground ? 'hide-background' : '']">
    <v-row>
      <v-col class="mb-5" cols="12">
        <v-row>
          <v-col cols="12" sm="6" md="4" v-for="(post, index) in paginatedPosts" :key="index">
            <router-link :to="post.link" class="text-decoration-none">
              <v-card :class="['post-card', { 'card-dark': darkMode, 'card-light': !darkMode }]">
                <v-card-title class="card-title">{{ post.title }}</v-card-title>
                <div class="blog-thumbnail-container">
                  <v-img :src="post.thumbnail" cover/>
                </div>
                <v-card-text>
                  <p>{{ post.shortDescription }}</p>
                  <p><b>Updated: {{ new Date(post.updatedAt).toLocaleDateString() }}</b></p>
                  <p><b><a :href="post.userLink" target="_blank">{{ post.userName }}</a></b></p>
                </v-card-text>
                <v-card-actions>
                  <v-btn color="primary" text>Read More</v-btn>
                </v-card-actions>
              </v-card>
            </router-link>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-pagination
              v-model="currentPage"
              :length="pageCount"
              circle
              color="primary"
            ></v-pagination>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
  <v-container class="video-container" fluid>
    <video autoplay muted loop class="background-video">
      <source :src="videoSrc2" type="video/mp4">
      Your browser does not support the video tag.
    </video>
    <div :class="darkMode ? 'frosted-overlay-dark' : 'frosted-overlay-light'"></div>
  </v-container>
</template>

<script>
import darkLogo from '@/assets/logo_icon_dark_trans.png';
import lightLogo from '@/assets/logo_icon_light_trans.png';
import videoSrc1 from '@/assets/P8250293_8x.mp4';
import videoSrc2 from '@/assets/P8260251_8x.mp4';
import { useHead } from '@vueuse/head';
import { getPostsByLanguageAbbreviation } from '@/api/post-client.ts';

export default {
  name: 'HomeView',
  props: {
    darkMode: {
      type: Boolean,
      required: true
    }
  },
  
  data() {
    return {
      logoSrc: '',
      videoSrc1: '',
      videoSrc2: '',
      hideBackground: false,
      teamPostLinks: [],
      // Pagination properties
      currentPage: 1,
      pageSize: 9, // Number of posts per page
      pageCount: 0, // Total number of pages
    };
  },
  methods: {
    setupPageHead() {
      useHead({
        title: "refarch", // Dynamic localized name from i18n
        meta: [
          // Twitter Card data
          { name: "twitter:card", content: "summary" },
          { name: "twitter:site", content: "@refarch" },
          { name: "twitter:creator", content: "refarch" },
          
          // Open Graph data for Facebook and other platforms
          { property: "og:url", content: `https://refarch.com${this.$router.currentRoute.fullPath}` },
          { property: "og:title", content: "refarch" },
          { property: "og:description", content: this.$t('welcomeMessageExtended') },
          { property: "og:image", content: this.$t('thumbnail') },
          { property: "og:updated_time", content: this.$t("updatedAt") },
        ],
      });
    },
    async fetchAndSetPosts() {
      const postsResponse = await getPostsByLanguageAbbreviation(this.currentLocale);
      if (postsResponse.data.length > 0) {
        this.teamPostLinks = postsResponse.data
          .map(post => ({
            ...post,
            link: `/${this.currentLocale}${post.link}`,
            userLink: `/${this.currentLocale}${post.userLink}` // Prepend the locale to userLink
          }))
          .sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));
        this.pageCount = Math.ceil(this.teamPostLinks.length / this.pageSize); // Calculate total pages
      } else {
        this.teamPostLinks = [];
        this.pageCount = 0;
      }
    }
  },
  mounted() {
    this.setupPageHead();
    this.fetchAndSetPosts();
  },
  computed: {
    currentLocale() {
      return this.$i18n.locale;
    },
    logoSrc() {
      return this.darkMode ? darkLogo : lightLogo;
    },
    videoSrc1() {
      return videoSrc1;
    },
    videoSrc2() {
      return videoSrc2;
    },
    importantLinksComputed() {
      return this.$tm('importantLinks').map(link => ({
        text: link.text,
        href: `/${this.currentLocale}${link.href}`
      }));
    },
    ecosystemLinksComputed() {
      return this.$tm('ecosystemLinks').map(link => ({
        text: link.text,
        href: `/${this.currentLocale}${link.href}`
      }));
    },
    blogComputed() {
      return this.$tm('blog');
    },
    papersComputed() {
      return this.$tm('papers');
    },
    paginatedPosts() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.teamPostLinks.slice(start, end);
    }
  },
  watch: {
    '$route'(to, from) {
      this.fetchAndSetPosts();
      this.setupPageHead();
    },
    darkMode() {
      // Always trigger the hide/show behavior regardless of darkMode value
      this.hideBackground = true;
      setTimeout(() => {
        this.hideBackground = false;
      }, 500); // Adjusted to 500ms for demonstration purposes
    }
  },
}
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

.card-title {
  font-size: 1.1rem; /* Adjust the font size as needed */
  line-height: 1.25 !important; /* Decrease line spacing */
  margin-bottom: 0.5rem; /* Adjust spacing below the title */
  white-space: normal; /* Ensure text can wrap if needed */
}

.blog-thumbnail-container {
  height: 200px; /* Set height for cropping */
  width: 100%; /* Full width to ensure it fills the card */
  overflow: hidden; /* Crop anything outside this container */
  display: flex; /* Ensures the child image can be centered if needed */
  justify-content: center;
  align-items: center;
}
</style>
