import {reactive} from 'vue'

const auth = reactive({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null')
})

export function login(token, user) {
    auth.token = token
    auth.user = user
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(user))
}

export function logout() {
    auth.token = ''
    auth.user = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
}

export function isLoggedIn() {
    return !!auth.token
}

export default auth
