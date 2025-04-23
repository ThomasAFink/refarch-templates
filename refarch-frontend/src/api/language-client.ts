import {
  defaultCatchHandler,
  defaultResponseHandler,
  getConfig,
  postConfig,
  putConfig,
  deleteConfig,
} from "@/api/fetch-utils";
import Language from "@/types/Language";

/**
 * Fetches all languages from the backend service.
 */
export function getLanguages(): Promise<Language[]> {
  return fetch("api/backend-service/languages", getConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Laden der Sprachen ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Fetches a language by its ID.
 */
export function getLanguageById(id: string): Promise<Language> {
  return fetch(`api/backend-service/languages/${id}`, getConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Laden der Sprache ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Creates a new language.
 */
export function createLanguage(language: Language): Promise<Language> {
  return fetch("api/backend-service/language-management/language", postConfig(language))
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Erstellen der Sprache ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Updates an existing language.
 */
export function updateLanguage(id: string, language: Language): Promise<Language> {
  return fetch(`api/backend-service/language-management/language/${id}`, putConfig(language))
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Aktualisieren der Sprache ist ein Fehler aufgetreten."
      );
      return response.json();
    });
}

/**
 * Deletes a language.
 */
export function deleteLanguage(id: string): Promise<void> {
  return fetch(`api/backend-service/language-management/language/${id}`, deleteConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Löschen der Sprache ist ein Fehler aufgetreten."
      );
    });
}

/**
 * Listens to translation progress events.
 */
export function listenToTranslationProgress(callback: (data: string) => void): EventSource {
  const eventSource = new EventSource("api/backend-service/language-management/language/translation-events");
  eventSource.onmessage = (event) => {
    callback(event.data);
  };
  eventSource.onerror = () => {
    eventSource.close();
  };
  return eventSource;
}

/**
 * Builds the headers for the request.
 * @returns {Headers}
 */
function getHeaders(): Headers {
  const headers = new Headers({
    "Content-Type": "application/json",
  });
  const csrfCookie = getXSRFToken();
  if (csrfCookie !== "") {
    headers.append("X-XSRF-TOKEN", csrfCookie);
  }
  return headers;
}

/**
 * Returns the XSRF-TOKEN.
 * @returns {string|string}
 */
function getXSRFToken(): string {
  const help = document.cookie.match(
    "(^|;)\\s*" + "XSRF-TOKEN" + "\\s*=\\s*([^;]+)"
  );
  return (help ? help.pop() : "") as string;
} 