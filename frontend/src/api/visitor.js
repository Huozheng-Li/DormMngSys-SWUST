import api from './index'

export const visitorApi = {
    list(params) {
        return api.get('/visitors', {params})
    },
    create(data) {
        return api.post('/visitors', data)
    },
    confirm(id) {
        return api.put(`/visitors/${id}/confirm`)
    },
    approve(id) {
        return api.put(`/visitors/${id}/approve`)
    },
    reject(id) {
        return api.put(`/visitors/${id}/reject`)
    },
    recordEntry(id) {
        return api.put(`/visitors/${id}/entry`)
    },
    recordExit(id) {
        return api.put(`/visitors/${id}/exit`)
    }
}
