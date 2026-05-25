import axios from 'axios'
import auth, {logout} from '../stores/auth'

const api = axios.create({
    baseURL: '/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
})

api.interceptors.request.use(config => {
    if (auth.token) {
        config.headers.Authorization = `Bearer ${auth.token}`
    }
    return config
})

api.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401) {
            logout()
            window.location.href = '/login'
        }
        return Promise.reject(error)
    }
)

export default api
