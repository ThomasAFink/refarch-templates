export interface User {
  roles: {
    userNoAuth: boolean;
    author: boolean;
    admin: boolean;
    user: boolean;
  };
  [key: string]: any;
}

export interface Language {
  id: string;
  abbreviation: string;
  name: string;
}

export interface Link {
  id?: string;
  link?: string;
  name?: string;
  fontAwesomeIcon?: string;
  mdiIcon?: string;
  type?: string;
  scope?: LinkScope;
  createdAt?: string;
  updatedAt?: string;
}

export enum LinkScope {
  INTERNAL = "INTERNAL",
  EXTERNAL = "EXTERNAL"
}

export interface Route {
  path: string;
  name: string;
  component: any;
  beforeEnter?: (to: any, from: any, next: any) => Promise<void>;
  meta?: { requiresDarkMode: boolean };
}

export interface State {
  isAuthenticated: boolean;
  user: User | null;
  homeContentLoaded: boolean;
  languages: Language[];
  routes: Route[];
  routesLoaded: boolean;
  darkMode: boolean;
}

export interface ActionContext {
  commit: (mutation: string, payload?: any) => void;
  dispatch: (action: string, payload?: any) => Promise<any>;
  state: State;
  getters: any;
  rootState: any;
  rootGetters: any;
} 