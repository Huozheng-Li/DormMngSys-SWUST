import api from './index'

export const repairApi = {
    list(params) {
        return api.get('/repairs', {params})
    },
    get(id) {
        return api.get(`/repairs/${id}`)
    },
    create(data) {
        return api.post('/repairs', data)
    },
    assign(id, assigneeId) {
        return api.put(`/repairs/${id}/assign`, {assigneeId})
    },
    complete(id, feedback) {
        return api.put(`/repairs/${id}/complete`, {feedback})
    },
    verify(id, passed) {
        return api.put(`/repairs/${id}/verify`, {passed})
    }
}
