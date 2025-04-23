import PostContent from "./PostContent";

export default class Post {
  id?: string;
  linkId?: string;
  thumbnail?: string;
  commentsEnabled?: boolean;
  published?: boolean;
  contents?: PostContent[];
  createdAt?: string;
  updatedAt?: string;
} 