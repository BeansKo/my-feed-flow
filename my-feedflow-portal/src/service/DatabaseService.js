import http from './http'

export const list = async() =>{
    return http.fetchGet('/api/database/list')
}