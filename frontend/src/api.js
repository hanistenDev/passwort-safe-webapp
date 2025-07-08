import axios from 'axios';

const API_URL = 'http://localhost:8080/api';


const instance = axios.create({
    baseURL: API_URL,
});

// Function to set auth credentials
export const setAuthCredentials = (username, password) => {
    instance.defaults.auth = {
        username: username,
        password: password,
    };
};

// User registration function
export const registerUser = async (username, password) => {
    try {
        const response = await axios.post(`${API_URL}/users/register`, {
            username: username,
            password: password  // Changed from passwordHash to password to match backend
        });
        // After successful registration, set the auth credentials
        setAuthCredentials(username, password);
        return response.data;
    } catch (error) {
        if (error.response) {
            throw new Error(error.response.data);
        }
        throw error;
    }
};

// Login function
export const login = async (username, password) => {
    try {
        // Test the credentials with a GET request to a protected endpoint
        const response = await axios.get(`${API_URL}/passwords`, {
            auth: {
                username: username,
                password: password,
            },
        });
        // If successful, set the auth credentials
        setAuthCredentials(username, password);
        return response.data;
    } catch (error) {
        if (error.response) {
            throw new Error('Ungültige Anmeldedaten');
        }
        throw error;
    }
};

// Logout function
export const logout = () => {
    instance.defaults.auth = null;
};

export default instance;
