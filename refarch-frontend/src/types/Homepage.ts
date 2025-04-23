export default class Homepage {
  id?: string;
  linkId?: string;
  thumbnail?: string;
  commentsEnabled?: boolean;
  published?: boolean;
  contents: HomepageContent[] = [];
  createdAt?: string;
  updatedAt?: string;
}

export interface HomepageContent {
  id?: string;
  homepageId?: string;
  languageId?: string;
  welcomeMessage?: string;
  welcomeMessageExtended?: string;
  exploreOurWork?: string;
  getInvolved?: string;
  importantLinks?: string;
  ecosystemLinks?: string;
  blog?: string;
  papers?: string;
  readMore?: string;
  createdAt?: string;
  updatedAt?: string;
} 