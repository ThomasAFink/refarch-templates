import axios from 'axios';

const API_URL = import.meta.env.VITE_VUE_APP_API_URL || '/api/backend-service/';

export const fetchUsersByLanguageAbbreviation = (abbreviation) => {
    return axios.get(`${API_URL}user-management/users/language-abbreviation/${abbreviation}`);
};

export const fetchUsersByPageId = (pageId) => {
    return axios.get(`${API_URL}user-management/users/page/${pageId}`);
};

export const fetchUsersByPostId = (postId) => {
    return axios.get(`${API_URL}user-management/users/post/${postId}`);
};

export const fetchUsersContentByUserLinkId = (linkId) => {
    return axios.get(`${API_URL}user-management/user/link-id/${linkId}`);
};

export const fetchUsersContentById = (id) => {
    return axios.get(`${API_URL}user-management/user/${id}`);
};

export const createUser = (userData) => {
    return axios.post(`${API_URL}user-management/user`, userData);
};

export const updateUser = (id, userData) => {
    return axios.put(`${API_URL}user-management/user/${id}`, userData);
};

export const deleteUser = (id) => {
    return axios.delete(`${API_URL}user-management/user/${id}`);
};

export const fetchUsersWithAllTranslatedBios = () => {
    return axios.get(`${API_URL}user-management/users/all-bio-translations`);
};

export const fetchUsers = () => {
    return axios.get(`${API_URL}users`);
};
