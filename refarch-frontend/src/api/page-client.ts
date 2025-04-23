import {
  defaultCatchHandler,
  defaultResponseHandler,
  getConfig,
  postConfig,
  putConfig,
  deleteConfig,
} from "@/api/fetch-utils";
import Page from "@/types/Page";
import PageContent from "@/types/PageContent";

/**
 * Fetches all pages from the backend service.
 */
export function getPages(): Promise<Page[]> {
  return fetch("api/backend-service/pages", getConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Laden der Seiten ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Fetches a page by its ID.
 */
export function getPageById(id: string): Promise<Page> {
  return fetch(`api/backend-service/pages/${id}`, getConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Laden der Seite ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Fetches a page by its link ID.
 */
export function getPageByLinkId(linkId: string): Promise<Page> {
  return fetch(`api/backend-service/page-management/page/link-id/${linkId}`, getConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Laden der Seite ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Creates a new page.
 */
export function createPage(page: Page): Promise<Page> {
  return fetch("api/backend-service/pages", postConfig(page))
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Erstellen der Seite ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Updates an existing page.
 */
export function updatePage(id: string, page: Page): Promise<Page> {
  return fetch(`api/backend-service/pages/${id}`, putConfig(page))
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Aktualisieren der Seite ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Deletes a page.
 */
export function deletePage(id: string): Promise<void> {
  return fetch(`api/backend-service/pages/${id}`, deleteConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Löschen der Seite ist ein Fehler aufgetreten."
      );
    });
}

/**
 * Fetches all content for a page.
 */
export function getPageContents(pageId: string): Promise<PageContent[]> {
  return fetch(`api/backend-service/pages/${pageId}/content`, getConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Laden der Seiteninhalte ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Fetches content for a page in a specific language.
 */
export function getPageContentByLanguage(pageId: string, languageId: string): Promise<PageContent> {
  return fetch(`api/backend-service/pages/${pageId}/content/${languageId}`, getConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Laden des Seiteninhalts ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Creates content for a page.
 */
export function createPageContent(pageId: string, content: PageContent): Promise<PageContent> {
  return fetch(`api/backend-service/pages/${pageId}/content`, postConfig(content))
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Erstellen des Seiteninhalts ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Updates content for a page.
 */
export function updatePageContent(pageId: string, languageId: string, content: PageContent): Promise<PageContent> {
  return fetch(`api/backend-service/pages/${pageId}/content/${languageId}`, putConfig(content))
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Aktualisieren des Seiteninhalts ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Deletes content for a page.
 */
export function deletePageContent(pageId: string, languageId: string): Promise<void> {
  return fetch(`api/backend-service/pages/${pageId}/content/${languageId}`, deleteConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Löschen des Seiteninhalts ist ein Fehler aufgetreten."
      );
    });
}

/**
 * Publishes a page.
 */
export function publishPage(id: string): Promise<void> {
  return fetch(`api/backend-service/pages/${id}/publish`, putConfig({}))
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Veröffentlichen der Seite ist ein Fehler aufgetreten."
      );
    });
}

/**
 * Unpublishes a page.
 */
export function unpublishPage(id: string): Promise<void> {
  return fetch(`api/backend-service/pages/${id}/unpublish`, putConfig({}))
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Zurückziehen der Seite ist ein Fehler aufgetreten."
      );
    });
}

/**
 * Fetches pages by language abbreviation.
 */
export function getPagesByLanguageAbbreviation(abbreviation: string): Promise<Page[]> {
  return fetch(`api/backend-service/page-management/pages/language-abbreviation/${abbreviation}`, getConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Laden der Seiten ist ein Fehler aufgetreten."
      );
      return response.json();
    });
} 