import http from './http'

export const list = async() =>{
    return http.fetchGet(`/api/email/list`)
}

export const get = async(id) =>{
    return http.fetchGet(`/api/email/get/${id}`)
}

export const insert = async(data) =>{
    return http.fetchPost(`/api/email/insert`, data)
}

export const update = async(data) =>{
    return http.fetchPost(`/api/email/update`, data)
}

export const del = async(id) =>{
    return http.fetchPost(`/api/email/delete/${id}`, {})
}