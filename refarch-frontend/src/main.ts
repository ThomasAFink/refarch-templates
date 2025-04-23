import { createApp } from "vue";
import App from "@/App.vue";

import i18n from "@/i18n.ts";
import vuetify from "@/plugins/vuetify.ts";
import { loadFonts } from "@/plugins/webfontloader.ts";
import router from "@/plugins/router.ts";
import store from "@/store";
import { createHead } from "@vueuse/head";

import { registerPlugins } from "@/plugins"; // ← your plugin registration helper

import "@/assets/css/styles.css";
import "unfonts.css";

loadFonts();

async function bootstrapApp() {
  try {
    const app = createApp(App);

    registerPlugins(app); // ← your custom plugin registrations

    app
      .use(vuetify)
      .use(store)
      .use(createHead())
      .use(router)
      .use(i18n);

    app.mount("#app");
  } catch (error) {
    console.error("An error occurred while bootstrapping the app:", error);
  }
}

bootstrapApp();
