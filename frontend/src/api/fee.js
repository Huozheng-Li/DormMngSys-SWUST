import api from './index'

export const feeApi = {
    list(params) {
        return api.get('/fees', {params})
    },
    get(id) {
        return api.get(`/fees/${id}`)
    },
    create(data) {
        return api.post('/fees', data)
    },
    pay(id, method, transactionId) {
        return api.put(`/fees/${id}/pay`, {method, transactionId})
    },
    delete(id) {
        return api.delete(`/fees/${id}`)
    }
}
