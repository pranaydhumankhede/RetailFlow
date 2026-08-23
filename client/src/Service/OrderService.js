import axios from "axios";

export const latestOrders = async () => {
    return await axios.get(
        `${import.meta.env.VITE_API_BASE_URL}/api/v1.0/orders/latest`,
        {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        }
    );
};

export const createOrder = async (order) => {
    return await axios.post(
        `${import.meta.env.VITE_API_BASE_URL}/api/v1.0/orders`,
        order,
        {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        }
    );
};

export const deleteOrder = async (id) => {
    return await axios.delete(
        `${import.meta.env.VITE_API_BASE_URL}/api/v1.0/orders/${id}`,
        {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        }
    );
};