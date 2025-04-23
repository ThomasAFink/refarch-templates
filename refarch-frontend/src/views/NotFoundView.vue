<template>
  <v-container :class="[darkMode ? 'container-dark' : 'container-light', hideBackground ? 'hide-background' : '']" class="fill-height" fluid>
    <v-row justify="center" align="center">
      <v-col cols="12" sm="8" md="6">
        <div class="text-center">
          <h1 class="display-2 font-weight-bold mb-2">404</h1>
          <h2 class="subtitle-1 mb-4">Page Not Found</h2>
          <p>The page you are looking for doesn't exist or has been moved.</p>
          <v-btn color="primary" to="/">Go Back Home</v-btn>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>
  
<script>
export default {
  name: 'NotFoundView',
  props: {
    darkMode: {
      type: Boolean,
      required: true
    }
  },
  watch: {
    darkMode() {
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
  background-image: none !important; /* Example way to hide the background */
}

</style>
  