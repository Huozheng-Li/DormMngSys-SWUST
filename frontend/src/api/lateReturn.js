import api from './index'

export const lateReturnApi = {
    list(params) {
        return api.get('/late-returns', {params})
    },
    create(data) {
        return api.post('/late-returns', data)
    },
    delete(id) {
        return api.delete(`/late-returns/${id}`)
    }
}
