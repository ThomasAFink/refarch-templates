import { createStore } from 'vuex';
import { getLinksByScope } from '@/api/link-client';
import { getLanguages } from '@/api/language-client';
import NotFoundView from '@/views/NotFoundView.vue';
import HomeView from '@/components/HomeView.vue';
/*import PageView from '@/views/PageView.vue';
import PostView from '@/views/PostView.vue';
import UserView from '@/views/UserView.vue';*/
/*import EditPageView from '@/admin/views/EditPageView.vue';
import EditPostView from '@/admin/views/EditPostView.vue';
import EditUserView from '@/admin/views/EditUserView.vue';*/
//import { loadHomepageContents, loadPageContents, loadPostContents, loadUserContents } from '@/i18n';
import { loadHomepageContents} from '@/i18n';
import type { State, User, Language, Link, Route, ActionContext } from './types';
import { LinkScope } from './types';

// Helper function to determine the Vue component based on the type of link
function determineComponent(type: string, isAdmin = false): any {
  if (isAdmin) {
    switch (type) {
      /*case 'page':
        return EditPageView;
      case 'post':
        return EditPostView;
      case 'user':
        return EditUserView;*/
      default:
        return NotFoundView;
    }
  } else {
    switch (type) {
      case 'home':
        return HomeView;
      /*case 'user':
        return UserView;
      case 'page':
        return PageView;
      case 'post':
        return PostView;*/
      default:
        return NotFoundView;
    }
  }
}

// Helper function to determine which content loading function to use based on the link type
function determineLoadFunction(link: Link): () => Promise<void> {
  if (!link.type) return () => Promise.resolve();
  
  switch (link.type) {
    case 'home':
      return () => loadHomepageContents();
    /*case 'page':
      return () => loadPageContents(link.id || '');
    case 'user':
      return () => loadUserContents(link.id || '');
    case 'post':
      return () => loadPostContents(link.id || '');*/
    default:
      return () => Promise.resolve();
  }
}

const store = createStore<State>({
  state: {
    isAuthenticated: localStorage.getItem('isAuthenticated') === 'true',
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    homeContentLoaded: false,
    languages: [],
    routes: [],
    routesLoaded: false,
    darkMode: localStorage.getItem('darkMode') === 'true',
  },
  mutations: {
    SET_AUTH(state: State, status: boolean) {
      state.isAuthenticated = status;
      localStorage.setItem('isAuthenticated', String(status));
    },
    SET_USER(state: State, user: User) {
      state.user = user;
      localStorage.setItem('user', JSON.stringify(user));
    },
    LOGOUT(state: State) {
      state.isAuthenticated = false;
      state.user = null;
      localStorage.removeItem('isAuthenticated');
      localStorage.removeItem('user');
    },
    TOGGLE_DARK_MODE(state: State) {
      state.darkMode = !state.darkMode;
      localStorage.setItem('darkMode', String(state.darkMode));
    },
    SET_HOME_CONTENT_LOADED(state: State, loaded: boolean) {
      state.homeContentLoaded = loaded;
    },
    SET_LANGUAGES(state: State, languages: Language[]) {
      state.languages = languages;
    },
    SET_ROUTES(state: State, routes: Route[]) {
      state.routes = routes;
    },
    SET_ROUTES_LOADED(state: State, loaded: boolean) {
      state.routesLoaded = loaded;
    },
  },
  actions: {
    async login({ commit }: ActionContext, { username, password }: { username: string; password: string }) {
      const user: User = { 
        username, 
        roles: { 
          userNoAuth: false, 
          author: false, 
          admin: false, 
          user: true 
        } 
      };
      commit('SET_USER', user);
      commit('SET_AUTH', true);
    },
    logout({ commit }: ActionContext) {
      commit('LOGOUT');
    },
    toggleDarkMode({ commit }: ActionContext) {
      commit('TOGGLE_DARK_MODE');
    },
    async fetchLanguages({ commit }: ActionContext) {
      const languages = await getLanguages();
      commit('SET_LANGUAGES', languages);
    },
    async fetchRoutes({ commit, state, dispatch }: ActionContext) {
      await dispatch('fetchLanguages');
      const links = await getLinksByScope(LinkScope.INTERNAL);
      const routes: Route[] = [];
      
      state.languages.forEach((lang: Language) => {
        links.forEach((link: Link) => {
          if (!link.id || !link.link || !link.type) return;
          
          const route: Route = {
            path: `/${lang.abbreviation}${link.link}`,
            name: `${lang.abbreviation}-${link.id}`,
            component: determineComponent(link.type),
            beforeEnter: async (to, from, next) => {
              await determineLoadFunction(link)();
              next();
            },
            meta: { requiresDarkMode: state.darkMode }
          };
          routes.push(route);

          if (['page', 'post', 'user'].includes(link.type)) {
            const adminRoute: Route = {
              ...route,
              path: `/${lang.abbreviation}/admin/edit/${link.link}`,
              name: `admin-${lang.abbreviation}-${link.id}`,
              component: determineComponent(link.type, true),
            };
            routes.push(adminRoute);
          }
        });
      });
      
      commit('SET_ROUTES', routes);
      commit('SET_ROUTES_LOADED', true);
    },
  },
});

export default store; 