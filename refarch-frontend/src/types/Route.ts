import type { RouteRecordRaw } from 'vue-router';

export interface Route {
  id: string;
  name: string;
  path: string;
  component: any;
  beforeEnter?: (to: any, from: any, next: any) => void;
  children?: Route[];
  meta?: Record<string, any>;
  props?: boolean | Record<string, any> | ((to: any) => Record<string, any>);
  alias?: string | string[];
  redirect?: string | ((to: any) => string);
} 