export enum LinkScope {
  INTERNAL = "INTERNAL",
  EXTERNAL = "EXTERNAL"
}

export default class Link {
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