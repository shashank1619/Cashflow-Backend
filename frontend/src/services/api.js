import axios from 'axios'

const API_BASE_URL = '/api'

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
})

// ==================== USER APIs ====================

export const userAPI = {
    register: (userData) => api.post('/users/register', userData),
    login: (credentials) => api.post('/users/login', credentials),
    getById: (id) => api.get(`/users/${id}`),
    getByUsername: (username) => api.get(`/users/username/${username}`),
    update: (id, userData) => api.put(`/users/${id}`, userData),
    delete: (id) => api.delete(`/users/${id}`)
}

// ==================== EXPENSE APIs ====================

export const expenseAPI = {
    add: (expenseData) => api.post('/expenses', expenseData),
    getById: (id) => api.get(`/expenses/${id}`),
    getByUser: (userId) => api.get(`/expenses/user/${userId}`),
    getSummary: (userId) => api.get(`/expenses/user/${userId}/summary`),
    getByCategory: (userId, categoryId) =>
        api.get(`/expenses/user/${userId}/category/${categoryId}`),
    getSummaryByDateRange: (userId, startDate, endDate) =>
        api.get(`/expenses/user/${userId}/summary/range`, {
            params: { startDate, endDate }
        }),
    update: (id, expenseData) => api.put(`/expenses/${id}`, expenseData),
    delete: (id) => api.delete(`/expenses/${id}`)
}

// ==================== CATEGORY APIs ====================

export const categoryAPI = {
    create: (categoryData) => api.post('/categories', categoryData),
    getById: (id) => api.get(`/categories/${id}`),
    getByUser: (userId) => api.get(`/categories/user/${userId}`),
    update: (id, categoryData) => api.put(`/categories/${id}`, categoryData),
    delete: (id) => api.delete(`/categories/${id}`),
    createDefaults: (userId) => api.post(`/categories/user/${userId}/defaults`)
}

// ==================== CREDIT APIs ====================

export const creditAPI = {
    add: (creditData) => api.post('/credits', creditData),
    getById: (id) => api.get(`/credits/${id}`),
    getByUser: (userId) => api.get(`/credits/user/${userId}`),
    getTotal: (userId) => api.get(`/credits/user/${userId}/total`),
    getByDateRange: (userId, startDate, endDate) =>
        api.get(`/credits/user/${userId}/range`, {
            params: { startDate, endDate }
        }),
    getBySource: (userId, source) => api.get(`/credits/user/${userId}/source/${source}`),
    update: (id, creditData) => api.put(`/credits/${id}`, creditData),
    delete: (id) => api.delete(`/credits/${id}`)
}

// ==================== THRESHOLD APIs ====================

export const thresholdAPI = {
    set: (thresholdData) => api.post('/thresholds', thresholdData),
    getById: (id) => api.get(`/thresholds/${id}`),
    getByUser: (userId) => api.get(`/thresholds/user/${userId}`),
    getActive: (userId) => api.get(`/thresholds/user/${userId}/active`),
    getAlerts: (userId) => api.get(`/thresholds/alerts/${userId}`),
    checkThresholds: (userId) => api.get(`/thresholds/check/${userId}`),
    update: (id, thresholdData) => api.put(`/thresholds/${id}`, thresholdData),
    delete: (id) => api.delete(`/thresholds/${id}`),
    toggle: (id) => api.patch(`/thresholds/${id}/toggle`)
}

export default api
