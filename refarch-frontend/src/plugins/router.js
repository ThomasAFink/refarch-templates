import { createRouter, createWebHistory } from 'vue-router';
import NotFoundView from '@/views/NotFoundView.vue';
import HomeView from '@/components/HomeView.vue';
import LoginView from '@/admin/views/LoginView.vue';
import AdminDashboard from '@/admin/views/AdminDashboard.vue';
import LinkManagementView from '@/admin/views/LinkManagementView.vue';
import LanguageManagementView from '@/admin/views/LanguageManagementView.vue';
import UserManagementView from '@/admin/views/UserManagementView.vue';
import { loadHomepageContents } from '@/i18n.js';
import store from '@/store';  // Import the store
import i18n from '@/i18n';
import { getCurrentUserRole } from '@/services/authService';

async function createAppRouter() {
  await store.dispatch('fetchLanguages');
  await store.dispatch('fetchRoutes');

  const staticRoutes = [
    {
      path: '/',
      name: 'Home',
      component: HomeView,
      beforeEnter: async (to, from, next) => {
        // Ensures home page contents are loaded when navigating to the home page
        if (!store.state.homeContentLoaded) {
          await loadHomepageContents();
          store.commit('SET_HOME_CONTENT_LOADED', true);  // Assuming mutation is set up to track this state
        }
        next();
      }
    },
    {
      path: '/login',
      name: 'Login',
      component: LoginView,
      beforeEnter: async (to, from, next) => {
        try {
          const roles = await getCurrentUserRole();
          if (roles.admin || roles.author) {
            next({ name: 'AdminDashboard' });
          } else if(roles.user) {
            next({ name: 'Home' });
          } else {
            next();
          }
        } catch (error) {
          next({ name: 'AdminDashboard' });
        }
      }
    },
    {
      path: '/admin',
      name: 'AdminDashboard',
      component: AdminDashboard,
      beforeEnter: async (to, from, next) => {
        try {
          const roles = await getCurrentUserRole();
          if (roles.admin || roles.author) {
            next();
          } else if(roles.user) {
            next({ name: 'Home' });
          }
          else {
            next({ name: 'Login' });
          }
        } catch (error) {
          next({ name: 'Login' });
        }
      }
    },
    {
      path: '/admin/new/blog/post/',
      name: 'AdminNewBlogPost',
      component: AdminDashboard,
      beforeEnter: async (to, from, next) => {
        try {
          const roles = await getCurrentUserRole();
          if (roles.admin || roles.author) {
            next();
          } else if(roles.user) {
            next({ name: 'Home' });
          }
          else {
            next({ name: 'Login' });
          }
        } catch (error) {
          next({ name: 'Login' });
        }
      }
    },
    {
      path: '/admin/page-management/',
      name: 'AdminPageManagement',
      component: LinkManagementView,
      beforeEnter: async (to, from, next) => {
        try {
          const roles = await getCurrentUserRole();
          if (roles.admin || roles.author) {
            next();
          } else if(roles.user) {
            next({ name: 'Home' });
          }
          else {
            next({ name: 'Login' });
          }
        } catch (error) {
          next({ name: 'Login' });
        }
      }
    },
    {
      path: '/admin/link-management/',
      name: 'AdminLinkManagement',
      component: UserManagementView,
      beforeEnter: async (to, from, next) => {
        try {
          const roles = await getCurrentUserRole();
          if (roles.admin || roles.author) {
            next();
          } else if(roles.user) {
            next({ name: 'Home' });
          }
          else {
            next({ name: 'Login' });
          }
        } catch (error) {
          next({ name: 'Login' });
        }
      }
    },
    {
      path: '/admin/language-management/',
      name: 'AdminLanguageManagement',
      component: LanguageManagementView,
      beforeEnter: async (to, from, next) => {
        try {
          const roles = await getCurrentUserRole();
          if (roles.admin || roles.author) {
            next();
          } else if(roles.user) {
            next({ name: 'Home' });
          }
          else {
            next({ name: 'Login' });
          }
        } catch (error) {
          next({ name: 'Login' });
        }
      }
    },
    {
      path: '/admin/user-management/',
      name: 'AdminUserManagement',
      component: UserManagementView,
      beforeEnter: async (to, from, next) => {
        try {
          const roles = await getCurrentUserRole();
          if (roles.admin || roles.author) {
            next();
          } else if(roles.user) {
            next({ name: 'Home' });
          }
          else {
            next({ name: 'Login' });
          }
        } catch (error) {
          next({ name: 'Login' });
        }
      }
    },
    { 
      path: '/:pathMatch(.*)*', 
      name: 'NotFoundView', 
      component: NotFoundView 
    }
  ];

  const routes = [...staticRoutes, ...store.state.routes];

  console.log(routes);

  const router = createRouter({
    history: createWebHistory(),
    routes
  });

  router.beforeEach((to, from, next) => {
    const lang = to.path.split('/')[1];
    if (lang && store.state.languages.find(l => l.abbreviation === lang)) {
      i18n.global.locale = lang;
    }
    next();
  });

  store.watch(
    (state) => state.routes,
    (newRoutes) => {
      newRoutes.forEach(route => {
        router.addRoute(route);
      });
    }
  );

  router.reload = async () => {
    await store.dispatch('fetchLanguages');
    await store.dispatch('fetchRoutes');
    router.matcher = createRouter({
      history: createWebHistory(),
      routes: [...staticRoutes, ...store.state.routes]
    }).matcher;
  };

  return router;
}

export default createAppRouter;
