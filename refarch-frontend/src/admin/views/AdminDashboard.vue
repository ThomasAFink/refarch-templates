<template>
    <v-container :class="[darkMode ? 'container-dark' : 'container-light', hideBackground ? 'hide-background' : '']">
        <v-row>
            <v-col cols="12">
                <v-card :class="{ 'card-dark': darkMode, 'card-light': !darkMode }">
                    <v-card-title>
                        Admin Dashboard <v-icon large>mdi-view-dashboard</v-icon>
                    </v-card-title>
                </v-card>
            </v-col>
        </v-row>
        <v-row>
            <!-- Each admin section as a separate card -->
            <v-col cols="12" sm="12" md="6" v-for="(item, index) in reorderedAdminSections" :key="index"
                :lg="index == 1 ? 8 : 4" :xl="index == 1 ? 8 : 4">
                <v-card v-if="isLargeScreen ? index === 1 : index === 0" class="mx-auto"
                    :class="{ 'card-dark': darkMode, 'card-light': !darkMode }" :height="cardHeight">
                    <template v-slot:prepend>
                        <h1 style="font-size: 64px;" class="mr-5"><i class="fa-solid fa-chart-line" icon="mdi-chart-line" @click="takePulse"></i></h1>
                        
                    </template>
                    <template v-slot:title>
                        <div class="text-caption text-grey text-uppercase">
                            {{ item.title }}
                        </div>
                        <span class="text-h3 font-weight-black" v-text="avg || '—'"></span>
                        <strong v-if="avg">Visitors</strong>
                    </template>
                    <template v-slot:append>
                        <v-btn class="align-self-start" icon="mdi-arrow-right-thick" size="34" variant="text"></v-btn>
                    </template>
                    <v-sheet color="transparent">
                        <v-sparkline :key="String(avg)" :gradient="['#f72047', '#ffd200', '#1feaea']" :line-width="3"
                            :model-value="visitors" :smooth="16" stroke-linecap="round" auto-draw></v-sparkline>
                    </v-sheet>
                    <router-link :to="{ name: item.managementRoute }" class="text-decoration-none">
                    </router-link>
                </v-card>


                <v-card v-else-if="isLargeScreen ? index === 0 : index === 1" class="mx-auto"
                    :class="{ 'card-dark': darkMode, 'card-light': !darkMode }" :height="cardHeight">
                    <v-card-title>
                        <h5 class="d-flex align-center">
                            <v-icon>{{ item.icon }}</v-icon>
                            {{ item.title }}
                            <v-spacer v-if="item.btn"></v-spacer>
                            <v-btn v-if="item.btn" color="secondary" text v-html="item.btn"
                                @click.stop="openDialog(item.dialogComponent)"></v-btn>
                        </h5>
                    </v-card-title>
                    <v-card-text>
                        <v-row>
                            <v-col class="mb-5" cols="12">
                                <v-row>
                                    <v-col cols="12" sm="12" md="12" v-for="(post, index) in paginatedPosts"
                                        :key="index">
                                        <router-link :to="post.link" class="text-decoration-none">
                                            <v-card
                                                :class="['post-card', { 'card-dark': darkMode, 'card-light': !darkMode }]">
                                                <v-card-title class="card-title">{{ post.title }}</v-card-title>
                                                <div class="blog-thumbnail-container">
                                                    <v-img :src="post.thumbnail" cover />
                                                </div>
                                                <v-card-text>
                                                    <p>{{ post.shortDescription }}</p>
                                                    <p><b>Updated: {{ new Date(post.updatedAt).toLocaleDateString()
                                                            }}</b>
                                                    </p>
                                                    <p><b><a :href="post.userLink" target="_blank">{{ post.userName
                                                                }}</a></b></p>
                                                </v-card-text>
                                                <v-card-actions>
                                                    <v-btn color="secondary" text>Edit Post</v-btn>
                                                </v-card-actions>
                                            </v-card>
                                        </router-link>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col>
                                        <v-pagination v-model="currentPage" :length="pageCount" circle
                                            color="secondary"></v-pagination>
                                    </v-col>
                                </v-row>
                            </v-col>
                        </v-row>
                        <router-link :to="{ name: item.managementRoute }" class="text-decoration-none">
                        </router-link>
                    </v-card-text>
                </v-card>

                <v-card v-else class="mx-auto" :class="{ 'card-dark': darkMode, 'card-light': !darkMode }"
                    :height="index === 0 ? cardHeight : '140px'">
                    <v-card-title>
                        <h5 class="d-flex align-center">
                            <span class="mr-2" v-html="item.icon"></span>
                            {{ item.title }}
                            <v-spacer v-if="item.btn"></v-spacer>
                            <v-btn v-if="item.btn" color="secondary" text v-html="item.btn"
                                @click.stop="openDialog(item.dialogComponent)"></v-btn>
                        </h5>
                    </v-card-title>
                    <v-card-text>
                        <v-row class="d-flex flex-column justify-space-between">
                            <v-col>
                                {{ item.subtitle }}
                            </v-col>
                            <v-col class="d-flex justify-end">
                                <router-link :to="{ name: item.managementRoute }" class="text-decoration-none">
                                    <v-btn icon="mdi-arrow-right-thick" size="34" variant="text"></v-btn>
                                </router-link>
                            </v-col>
                        </v-row>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>

        <!-- Dynamic Dialog -->
        <component :is="activeDialogComponent" :dark-mode="darkMode" v-model:dialog="dialog"></component>
    </v-container>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue';
import { useDisplay } from 'vuetify';
import { getPostsByLanguageAbbreviation } from '@/api/post-client.ts';
import NewLanguageView from '@/admin/components/NewLanguageView.vue';
import NewLinkView from '@/admin/components/NewLinkView.vue';
import NewUserView from '@/admin/components/NewUserView.vue';
import NewPostView from '@/admin/components/NewPostView.vue';

export default {
    name: 'AdminDashboard',
    components: {
        NewLanguageView,
        NewLinkView,
        NewUserView,
        NewPostView
    },
    props: {
        darkMode: {
            type: Boolean,
            required: true
        },
        adminSections: {
            type: Array,
            required: true
        }
    },
    data() {
        return {
            visitors: [75, 43, 40, 65, 45, 95, 85, 120, 89, 40, 75, 40, 95, 85],
            hideBackground: false,
            dialog: false,
            activeDialogComponent: null,
            currentPage: 1,
            pageSize: 1, // Number of posts per page
            pageCount: 0, // Total number of pages
            teamPostLinks: [],
        };
    },
    watch: {
        darkMode(newValue) {
            this.hideBackground = true;
            setTimeout(() => {
                this.hideBackground = false;
            }, 500);
        }
    },
    computed: {
        avg() {
            const sum = this.visitors.reduce((acc, cur) => acc + cur, 0);
            const length = this.visitors.length;
            return length ? Math.ceil(sum / length) : 0;
        },
        reorderedAdminSections() {
            const sections = [...this.adminSections];
            if (!this.isLargeScreen) {
                const first = sections.shift();
                const second = sections.shift();
                sections.unshift(first);
                sections.unshift(second);
            }
            return sections;
        },
        currentLocale() {
            return this.$i18n.locale;
        },
        paginatedPosts() {
            const start = (this.currentPage - 1) * this.pageSize;
            const end = start + this.pageSize;
            return this.teamPostLinks.slice(start, end);
        }
    },
    methods: {
        openDialog(dialogComponent) {
            this.activeDialogComponent = dialogComponent;
            this.dialog = true;
        },
        async fetchAndSetPosts() {
            const postsResponse = await getPostsByLanguageAbbreviation(this.currentLocale);
            if (postsResponse.data.length > 0) {
                this.teamPostLinks = postsResponse.data
                    .map(post => ({
                        ...post,
                        link: `/${this.currentLocale}/admin/edit${post.link}`,
                        userLink: `/${this.currentLocale}${post.userLink}`
                    }))
                    .sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));
                this.pageCount = Math.ceil(this.teamPostLinks.length / this.pageSize);
            } else {
                this.teamPostLinks = [];
                this.pageCount = 0;
            }
        }
    },
    mounted() {
        this.fetchAndSetPosts();
    },
    setup() {
        const { lgAndUp } = useDisplay();
        const isLargeScreen = computed(() => lgAndUp.value);
        const cardHeight = ref('550px');

        onMounted(() => {
            cardHeight.value = '550px';
        });

        return { cardHeight, isLargeScreen };
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
</style>
