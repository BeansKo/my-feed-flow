import http from './http'

export const list = async() =>{
    return http.fetchGet('/api/database/list')
}

export const get = async(id) => {
    return http.fetchGet(`/api/database/get/${id}`)
}

export const del = async(id) =>{
    return http.fetchPost(`/api/database/delete/${id}`, {})
}

export const update = async(data) =>{
    return http.fetchPost(`/api/database/update`, data)
}

export const insert = async(data) =>{
    return http.fetchPost(`/api/database/insert`, data)
}