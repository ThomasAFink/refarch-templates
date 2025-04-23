import { getConfig, postConfig, putConfig, patchConfig, deleteConfig } from "./fetch-utils";
import Post from "../types/Post";
import PostContent from "../types/PostContent";

const BASE_URL = "/api/posts";

export async function getPosts(): Promise<Post[]> {
  try {
    const response = await fetch(BASE_URL, getConfig());
    if (!response.ok) {
      throw new Error(`Failed to fetch posts: ${response.statusText}`);
    }
    return await response.json();
  } catch (error) {
    console.error("Error fetching posts:", error);
    throw error;
  }
}

export async function getPostById(id: string): Promise<Post> {
  try {
    const response = await fetch(`${BASE_URL}/${id}`, getConfig());
    if (!response.ok) {
      throw new Error(`Failed to fetch post: ${response.statusText}`);
    }
    return await response.json();
  } catch (error) {
    console.error(`Error fetching post ${id}:`, error);
    throw error;
  }
}

export async function createPost(post: Post): Promise<Post> {
  try {
    const response = await fetch(BASE_URL, postConfig(post));
    if (!response.ok) {
      throw new Error(`Failed to create post: ${response.statusText}`);
    }
    return await response.json();
  } catch (error) {
    console.error("Error creating post:", error);
    throw error;
  }
}

export async function updatePost(id: string, post: Post): Promise<Post> {
  try {
    const response = await fetch(`${BASE_URL}/${id}`, putConfig(post));
    if (!response.ok) {
      throw new Error(`Failed to update post: ${response.statusText}`);
    }
    return await response.json();
  } catch (error) {
    console.error(`Error updating post ${id}:`, error);
    throw error;
  }
}

export async function deletePost(id: string): Promise<void> {
  try {
    const response = await fetch(`${BASE_URL}/${id}`, deleteConfig());
    if (!response.ok) {
      throw new Error(`Failed to delete post: ${response.statusText}`);
    }
  } catch (error) {
    console.error(`Error deleting post ${id}:`, error);
    throw error;
  }
}

export async function addPostContent(postId: string, content: PostContent): Promise<PostContent> {
  try {
    const response = await fetch(`${BASE_URL}/${postId}/contents`, postConfig(content));
    if (!response.ok) {
      throw new Error(`Failed to add post content: ${response.statusText}`);
    }
    return await response.json();
  } catch (error) {
    console.error(`Error adding content to post ${postId}:`, error);
    throw error;
  }
}

export async function updatePostContent(postId: string, contentId: string, content: PostContent): Promise<PostContent> {
  try {
    const response = await fetch(`${BASE_URL}/${postId}/contents/${contentId}`, putConfig(content));
    if (!response.ok) {
      throw new Error(`Failed to update post content: ${response.statusText}`);
    }
    return await response.json();
  } catch (error) {
    console.error(`Error updating content ${contentId} for post ${postId}:`, error);
    throw error;
  }
}

export async function deletePostContent(postId: string, contentId: string): Promise<void> {
  try {
    const response = await fetch(`${BASE_URL}/${postId}/contents/${contentId}`, deleteConfig());
    if (!response.ok) {
      throw new Error(`Failed to delete post content: ${response.statusText}`);
    }
  } catch (error) {
    console.error(`Error deleting content ${contentId} from post ${postId}:`, error);
    throw error;
  }
}

export async function getPostByPostLinkId(linkId: string): Promise<Post> {
  try {
    const response = await fetch(`${BASE_URL}/link-id/${linkId}`, getConfig());
    if (!response.ok) {
      throw new Error(`Failed to fetch post by link ID: ${response.statusText}`);
    }
    return await response.json();
  } catch (error) {
    console.error(`Error fetching post by link ID ${linkId}:`, error);
    throw error;
  }
}

export async function getPostsByLanguageAbbreviation(abbreviation: string): Promise<Post[]> {
  try {
    const response = await fetch(`${BASE_URL}/language-abbreviation/${abbreviation}`, getConfig());
    if (!response.ok) {
      throw new Error(`Failed to fetch posts by language abbreviation: ${response.statusText}`);
    }
    return await response.json();
  } catch (error) {
    console.error(`Error fetching posts for language ${abbreviation}:`, error);
    throw error;
  }
} 