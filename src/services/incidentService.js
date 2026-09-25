import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1/mining';

export const getAllIncidents = async () => {
    const response = await axios.get(API_URL);
    return response.data;
};

export const createIncident = async (incident) => {
    const response = await axios.post(API_URL, incident);
    return response.data;
};