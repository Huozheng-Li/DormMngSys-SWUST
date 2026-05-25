import api from './index'

export const buildingApi = {
    list() {
        return api.get('/buildings')
    },
    get(id) {
        return api.get(`/buildings/${id}`)
    },
    create(data) {
        return api.post('/buildings', data)
    },
    delete(id) {
        return api.delete(`/buildings/${id}`)
    },
    getFloors(buildingId) {
        return api.get(`/buildings/${buildingId}/floors`)
    },
    addFloor(buildingId, data) {
        return api.post(`/buildings/${buildingId}/floors`, data)
    },
    getRooms(floorId) {
        return api.get(`/buildings/floors/${floorId}/rooms`)
    },
    addRoom(floorId, data) {
        return api.post(`/buildings/floors/${floorId}/rooms`, data)
    },
    getBeds(roomId) {
        return api.get(`/buildings/rooms/${roomId}/beds`)
    }
}
