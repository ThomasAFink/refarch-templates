import {
  defaultCatchHandler,
  defaultResponseHandler,
  getConfig,
  postConfig,
  putConfig,
  deleteConfig,
} from "@/api/fetch-utils";
import Link, { LinkScope } from "@/types/Link";
import type { Route } from "@/types/Route";

/**
 * Fetches all links from the backend service.
 */
export function getLinks(): Promise<Link[]> {
  return fetch("api/backend-service/links", getConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Laden der Links ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Fetches a link by its ID.
 */
export function getLinkById(id: string): Promise<Link> {
  return fetch(`api/backend-service/links/${id}`, getConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Laden des Links ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Fetches links by their type.
 */
export function getLinksByType(type: string): Promise<Link[]> {
  return fetch(`api/backend-service/link-management/link-type/${type}`, getConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Laden der Links ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Fetches links by their scope.
 */
export function getLinksByScope(scope: LinkScope): Promise<Link[]> {
  return fetch(`api/backend-service/link-management/link-scope/${scope}`, getConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Laden der Links ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Creates a new link.
 */
export function createLink(link: Link): Promise<Link> {
  return fetch("api/backend-service/link-management/link", postConfig(link))
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Erstellen des Links ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Updates an existing link.
 */
export function updateLink(id: string, link: Link): Promise<Link> {
  return fetch(`api/backend-service/link-management/link/${id}`, putConfig(link))
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Aktualisieren des Links ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Deletes a link.
 */
export function deleteLink(id: string): Promise<void> {
  return fetch(`api/backend-service/link-management/link/${id}`, deleteConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Löschen des Links ist ein Fehler aufgetreten."
      );
    });
}

/**
 * Fetches internal links and converts them to routes.
 */
export async function fetchRoutes(): Promise<Route[]> {
  const links = await getLinksByScope(LinkScope.INTERNAL);
  return links.map(link => ({
    id: link.id || crypto.randomUUID(),
    path: link.link || '/',
    name: link.name || 'unnamed',
    component: () => import('@/views/DefaultView.vue'),
    meta: {
      title: link.name,
      icon: link.fontAwesomeIcon || link.mdiIcon
    }
  }));
} 