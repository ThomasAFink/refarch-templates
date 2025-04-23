import {
  defaultCatchHandler,
  defaultResponseHandler,
  getConfig,
} from "@/api/fetch-utils";
import Homepage from "@/types/Homepage";

/**
 * Fetches all homepages from the backend service.
 */
export function getHomepages(): Promise<Homepage[]> {
  return fetch("api/backend-service/homepages", getConfig())
    .catch(defaultCatchHandler)
    .then((response) => {
      defaultResponseHandler(
        response,
        "Beim Laden der Homepages ist ein Fehler aufgetreten."
      );
      return response.json();
    });
} 