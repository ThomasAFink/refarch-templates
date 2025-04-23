import PageContent from "./PageContent";

export default class Page {
  id?: string;
  linkId?: string;
  thumbnail?: string;
  commentsEnabled?: boolean;
  published?: boolean;
  contents?: PageContent[];
  createdAt?: string;
  updatedAt?: string;
} 