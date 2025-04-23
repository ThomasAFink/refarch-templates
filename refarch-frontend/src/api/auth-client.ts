import { postConfig } from "./fetch-utils";

const BASE_URL = "/api/backend-service/auth";

export interface User {
  roles: {
    userNoAuth: boolean;
    author: boolean;
    admin: boolean;
    user: boolean;
  };
  [key: string]: any;
}

export async function login(email: string, password: string): Promise<User> {
  try {
    const response = await fetch(`${BASE_URL}/login`, postConfig({ email, password }));
    if (!response.ok) {
      throw new Error(`Login failed: ${response.statusText}`);
    }
    const user = await response.json();
    localStorage.setItem('user', JSON.stringify(user));
    localStorage.setItem('isAuthenticated', 'true');
    return user;
  } catch (error) {
    console.error("Error during login:", error);
    throw error;
  }
}

export async function logout(): Promise<void> {
  try {
    const response = await fetch(`${BASE_URL}/logout`, postConfig({}));
    if (!response.ok) {
      throw new Error(`Logout failed: ${response.statusText}`);
    }
    localStorage.setItem('user', JSON.stringify({
      roles: { userNoAuth: true, author: false, admin: false, user: false }
    }));
    localStorage.setItem('isAuthenticated', 'false');
  } catch (error) {
    console.error("Error during logout:", error);
    throw error;
  }
}

export function getCurrentUser(): User | null {
  const userStr = localStorage.getItem('user');
  return userStr ? JSON.parse(userStr) : null;
}

export function getCurrentUserRole(): User['roles'] {
  const user = getCurrentUser();
  return user ? user.roles : { admin: false, author: false, user: false, userNoAuth: true };
}

export function isAuthenticated(): boolean {
  return localStorage.getItem('isAuthenticated') === 'true';
} 