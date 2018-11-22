import http from './http'

export const list = async() =>{
    return http.fetchGet(`/api/jobStep/list`)
}