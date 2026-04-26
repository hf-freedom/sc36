import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

export const memberApi = {
  create: (data) => api.post('/members', data),
  getById: (id) => api.get(`/members/${id}`),
  getByPhone: (phone) => api.get(`/members/phone/${phone}`),
  getAll: () => api.get('/members'),
  updateLevel: (id, level) => api.put(`/members/${id}/level/${level}`)
}

export const storedValueCardApi = {
  create: (data) => api.post('/stored-value-cards', data),
  getById: (id) => api.get(`/stored-value-cards/${id}`),
  getByMember: (memberId) => api.get(`/stored-value-cards/member/${memberId}`),
  getActiveByMember: (memberId) => api.get(`/stored-value-cards/member/${memberId}/active`),
  refund: (id, allowPrincipalRefund) => api.put(`/stored-value-cards/${id}/refund?allowPrincipalRefund=${allowPrincipalRefund}`)
}

export const packageCardApi = {
  create: (data) => api.post('/package-cards', data),
  getById: (id) => api.get(`/package-cards/${id}`),
  getByMember: (memberId) => api.get(`/package-cards/member/${memberId}`),
  getActiveByMember: (memberId) => api.get(`/package-cards/member/${memberId}/active`),
  getByMemberAndService: (memberId, serviceId) => api.get(`/package-cards/member/${memberId}/service/${serviceId}`),
  refund: (id) => api.put(`/package-cards/${id}/refund`)
}

export const paymentApi = {
  create: (data) => api.post('/payments', data),
  getById: (id) => api.get(`/payments/${id}`),
  pay: (id) => api.put(`/payments/${id}/pay`),
  cancel: (id) => api.put(`/payments/${id}/cancel`)
}

export const consumptionApi = {
  consumeStoredValue: (data) => api.post('/consumptions/stored-value', data),
  consumePackage: (data) => api.post('/consumptions/package', data),
  getById: (id) => api.get(`/consumptions/${id}`),
  getByIdempotentKey: (key) => api.get(`/consumptions/idempotent/${key}`),
  getAll: (params) => api.get('/consumptions', { params })
}

export const refundApi = {
  create: (data) => api.post('/refunds', data),
  getById: (id) => api.get(`/refunds/${id}`),
  getByConsumption: (consumptionId) => api.get(`/refunds/consumption/${consumptionId}`)
}

export const reportApi = {
  generateDaily: (date) => api.post(`/reports/daily/${date}`),
  getDaily: (date) => api.get(`/reports/daily/${date}`),
  getRange: (startDate, endDate) => api.get(`/reports/range?startDate=${startDate}&endDate=${endDate}`)
}

export default api
