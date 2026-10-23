import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const apiService = {
  // Authentication
  register: async (organizerData) => {
    const res = await apiClient.post('/auth/register', organizerData);
    return res.data;
  },

  login: async (credentials) => {
    const res = await apiClient.post('/auth/login', credentials);
    return res.data;
  },

  // Artist Events
  submitEvent: async (eventData) => {
    const res = await apiClient.post('/events', eventData);
    return res.data;
  },

  getArtistEvents: async (organizerId) => {
    const res = await apiClient.get(`/organizers/${organizerId}/events`);
    return res.data;
  },

  getOrganizerProfile: async (organizerId) => {
    const res = await apiClient.get(`/organizers/${organizerId}`);
    return res.data;
  },

  // Public Hosted Events
  getHostedEvents: async () => {
    const res = await apiClient.get('/events/hosted');
    return res.data;
  },

  getEventById: async (eventId) => {
    const res = await apiClient.get(`/events/${eventId}`);
    return res.data;
  },

  // Admin Moderation
  getAdminEvents: async (statusFilter = null) => {
    const params = statusFilter && statusFilter !== 'ALL' ? { status: statusFilter } : {};
    const res = await apiClient.get('/admin/events', { params });
    return res.data;
  },

  processApproval: async (eventId, status, rejectionReason = null) => {
    const res = await apiClient.put(`/admin/events/${eventId}/approval`, {
      status,
      rejectionReason,
    });
    return res.data;
  },

  getAdminStats: async () => {
    const res = await apiClient.get('/admin/stats');
    return res.data;
  },
};

export default apiClient;
