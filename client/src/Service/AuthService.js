import axios from "axios";

export const login = async (data) => {
    return await axios.post(
        `${import.meta.env.VITE_API_BASE_URL}/api/v1.0/login`,
        data
    );
}

export const guestLogin = async () => {
    console.log("API URL:", import.meta.env.VITE_API_BASE_URL);

    return await axios.post(
        `${import.meta.env.VITE_API_BASE_URL}/api/v1.0/guest-login`
    );
};