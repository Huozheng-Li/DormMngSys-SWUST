import api from './index'

export const announcementApi = {
    list() {
        return api.get('/announcements')
    },
    get(id) {
        return api.get(`/announcements/${id}`)
    },
    create(data) {
        return api.post('/announcements', data)
    },
    delete(id) {
        return api.delete(`/announcements/${id}`)
    },
    markRead(id, userId) {
        return api.post(`/announcements/${id}/read`, {userId})
    },
    getReadStatus(id) {
        return api.get(`/announcements/${id}/read-status`)
    }
}
