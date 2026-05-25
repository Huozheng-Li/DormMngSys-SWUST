import api from './index'

export const userApi = {
    list(role) {
        return api.get('/users', {params: {role}})
    },
    get(id) {
        return api.get(`/users/${id}`)
    },
    create(data) {
        return api.post('/users', data)
    },
    update(id, data) {
        return api.put(`/users/${id}`, data)
    },
    delete(id) {
        return api.delete(`/users/${id}`)
    },
    search(keyword) {
        return api.get('/users/search', {params: {keyword}})
    }
}
