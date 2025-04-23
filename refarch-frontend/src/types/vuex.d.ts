declare module 'vuex' {
  import { Component } from 'vue';
  import { Store } from 'vuex/types/index';

  export function createStore<S>(options: StoreOptions<S>): Store<S>;

  export interface StoreOptions<S> {
    state?: S | (() => S);
    getters?: GetterTree<S, S>;
    actions?: ActionTree<S, S>;
    mutations?: MutationTree<S>;
    modules?: ModuleTree<S>;
    plugins?: Plugin<S>[];
    strict?: boolean;
    devtools?: boolean;
  }

  export interface GetterTree<S, R> {
    [key: string]: Getter<S, R>;
  }

  export interface ActionTree<S, R> {
    [key: string]: Action<S, R>;
  }

  export interface MutationTree<S> {
    [key: string]: Mutation<S>;
  }

  export interface ModuleTree<S> {
    [key: string]: Module<any, S>;
  }

  export type Getter<S, R> = (state: S, getters: any, rootState: R, rootGetters: any) => any;
  export type Action<S, R> = (context: ActionContext<S, R>, payload?: any) => any;
  export type Mutation<S> = (state: S, payload?: any) => void;
  export type Plugin<S> = (store: Store<S>) => void;

  export interface ActionContext<S, R> {
    dispatch: Dispatch;
    commit: Commit;
    state: S;
    getters: any;
    rootState: R;
    rootGetters: any;
  }

  export interface Dispatch {
    (type: string, payload?: any, options?: DispatchOptions): Promise<any>;
    <P extends Payload>(payloadWithType: P, options?: DispatchOptions): Promise<any>;
  }

  export interface Commit {
    (type: string, payload?: any, options?: CommitOptions): void;
    <P extends Payload>(payloadWithType: P, options?: CommitOptions): void;
  }

  export interface Payload {
    type: string;
  }

  export interface DispatchOptions {
    root?: boolean;
  }

  export interface CommitOptions {
    root?: boolean;
  }

  export interface Module<S, R> {
    namespaced?: boolean;
    state?: S | (() => S);
    getters?: GetterTree<S, R>;
    actions?: ActionTree<S, R>;
    mutations?: MutationTree<S>;
    modules?: ModuleTree<R>;
  }
} 