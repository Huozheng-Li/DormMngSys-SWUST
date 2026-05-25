import api from './index'

export const checkinoutApi = {
    list(params) {
        return api.get('/checkinout', {params})
    },
    get(id) {
        return api.get(`/checkinout/${id}`)
    },
    create(data) {
        return api.post('/checkinout', data)
    },
    approve(id, reviewerId, bedId) {
        return api.put(`/checkinout/${id}/approve`, {reviewerId, bedId})
    },
    reject(id, reviewerId, comment) {
        return api.put(`/checkinout/${id}/reject`, {reviewerId, comment})
    }
}
