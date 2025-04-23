import { createI18n } from 'vue-i18n';
import { getHomepages } from '@/api/homepage-client.ts';
import { getPageByLinkId } from '@/api/page-client.ts';
//import { fetchUsersContentByUserLinkId } from '@/api/user-client.ts';
import { getPostByPostLinkId } from '@/api/post-client.ts';
import type { Homepage } from '@/types/Homepage';

interface UserContent {
  id: string;
  linkId: string;
  languageId: string;
  name: string;
  bio: string;
  profilePicture: string;
  createdAt: string;
  updatedAt: string;
}

interface PageContent {
  id: string;
  linkId: string;
  languageId: string;
  title: string;
  content: string;
  pageName: string;
  shortDescription: string;
  keywords: string;
  thumbnail: string;
  createdAt: string;
  updatedAt: string;
}

interface PostContent {
  id: string;
  linkId: string;
  languageId: string;
  title: string;
  content: string;
  shortDescription: string;
  keywords: string;
  thumbnail: string;
  createdAt: string;
  updatedAt: string;
}

const i18n = createI18n({
  locale: 'en',
  fallbackLocale: 'en',
  messages: {}
});

async function loadHomepageContents(): Promise<void> {
  try {
    const homepages = await getHomepages();
    console.log('Homepages:', homepages);
    for (const page of homepages) {
      if (page.languageAbbreviation) {

        const content: Homepage = {
          id: String(page.id),
          linkId: page.linkId,
          languageId: page.languageId,
          welcomeMessage: page.contents.welcomeMessage,
          welcomeMessageExtended: page.contents.welcomeMessageExtended,
          exploreOurWork: page.contents.exploreOurWork,
          getInvolved: page.contents.getInvolved,
          importantLinks: JSON.parse(page.importantLinks),
          ecosystemLinks: JSON.parse(page.ecosystemLinks),
          blog: JSON.parse(page.blog),
        }




        
        const content: HomepageContent = {
          id: String(page.id),
          linkId: page.linkId,
          languageId: page.languageId,
          welcomeMessage: page.welcomeMessage,
          welcomeMessageExtended: page.welcomeMessageExtended,
          exploreOurWork: page.exploreOurWork,
          getInvolved: page.getInvolved,
          importantLinks: JSON.parse(page.importantLinks),
          ecosystemLinks: JSON.parse(page.ecosystemLinks),
          blog: JSON.parse(page.blog),
          papers: JSON.parse(page.papers),
          readMore: page.readMore,
          thumbnail: page.thumbnail,
          createdAt: page.createdAt,
          updatedAt: page.updatedAt
        };
        i18n.global.setLocaleMessage(page.languageAbbreviation, {
          ...i18n.global.messages[page.languageAbbreviation],
          ...content
        });
      }
    }
  } catch (error) {
    console.error('Error loading homepage contents:', error);
  }
}

/*async function loadUserContents(linkId: string): Promise<void> {
  try {
    const users = await fetchUsersContentByUserLinkId(linkId);
    for (const user of users) {
      if (user.languageAbbreviation) {
        const content: UserContent = {
          id: String(user.id),
          linkId: user.linkId,
          languageId: user.languageId,
          name: user.name,
          bio: user.bio,
          profilePicture: user.profilePicture,
          createdAt: user.createdAt,
          updatedAt: user.updatedAt
        };
        i18n.global.setLocaleMessage(user.languageAbbreviation, {
          ...i18n.global.messages[user.languageAbbreviation],
          ...content
        });
      }
    }
  } catch (error) {
    console.error('Error loading user contents:', error);
  }
}*/

/*async function loadPageContents(linkId: string): Promise<void> {
  try {
    const pages = await getPageByLinkId(linkId);
    for (const page of pages) {
      if (page.languageAbbreviation) {
        const content: PageContent = {
          id: String(page.id),
          linkId: page.linkId,
          languageId: page.languageId,
          title: page.title,
          content: page.content,
          pageName: page.pageName,
          shortDescription: page.shortDescription,
          keywords: page.keywords,
          thumbnail: page.thumbnail,
          createdAt: page.createdAt,
          updatedAt: page.updatedAt
        };
        i18n.global.setLocaleMessage(page.languageAbbreviation, {
          ...i18n.global.messages[page.languageAbbreviation],
          ...content
        });
      }
    }
  } catch (error) {
    console.error(`Error loading contents for the page "${linkId}":`, error);
  }
}*/

/*async function loadPostContents(linkId: string): Promise<void> {
  try {
    const posts = await getPostByPostLinkId(linkId);
    for (const post of posts) {
      if (post.languageAbbreviation) {
        const content: PostContent = {
          id: String(post.id),
          linkId: post.linkId,
          languageId: post.languageId,
          title: post.title,
          content: post.content,
          shortDescription: post.shortDescription,
          keywords: post.keywords,
          thumbnail: post.thumbnail,
          createdAt: post.createdAt,
          updatedAt: post.updatedAt
        };
        i18n.global.setLocaleMessage(post.languageAbbreviation, {
          ...i18n.global.messages[post.languageAbbreviation],
          ...content
        });
      }
    }
  } catch (error) {
    console.error(`Error loading contents for the post "${linkId}":`, error);
  }
}*/

//export { loadHomepageContents, loadPageContents, loadUserContents, loadPostContents };
export { loadHomepageContents };
export default i18n; 