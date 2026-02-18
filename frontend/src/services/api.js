import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8081/api',
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const login = async (username, password) => {
  const response = await api.post('/auth/login', { username, password })
  const { token, role } = response.data
  localStorage.setItem('token', token)
  localStorage.setItem('role', role)
  return response.data
}

export const register = async (username, fullName, password) => {
  const response = await api.post('/auth/register', { username, fullName, password })
  return response.data
}

export const getDoctors = async () => {
  const response = await api.get('/doctors')
  return response.data
}

export const createDoctor = async (name, specialization) => {
  const response = await api.post('/doctors', { name, specialization })
  return response.data
}

export const bookAppointment = async (doctorId, appointmentTime) => {
  const response = await api.post('/appointments', { doctorId, appointmentTime })
  return response.data
}

export const getMyAppointments = async () => {
  const response = await api.get('/appointments/me')
  return response.data
}

export const getAdminStats = async () => {
  const response = await api.get('/admin/stats')
  return response.data
}

export const getAdminAppointments = async () => {
  const response = await api.get('/admin/appointments')
  return response.data
}

export const updateAppointmentStatus = async (id, status) => {
  const response = await api.patch(`/admin/appointments/${id}/status`, { status })
  return response.data
}

export default api

