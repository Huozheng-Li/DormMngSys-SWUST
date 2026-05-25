import api from './index'

export const hygieneApi = {
    list(params) {
        return api.get('/hygiene', {params})
    },
    create(data) {
        return api.post('/hygiene', data)
    },
    delete(id) {
        return api.delete(`/hygiene/${id}`)
    }
}
