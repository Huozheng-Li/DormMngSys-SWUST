import api from './index'

export function loginApi(username, password) {
    return api.post('/auth/login', {username, password})
}
