import http from './http'

export const list = async() =>{
    return http.fetchGet(`/api/jobType/list`)
}

export const get = async(id) =>{
    return http.fetchGet(`/api/jobType/get/${id}`)
}

export const getConfig = async(id) =>{
    return http.fetchGet(`/api/jobType/get/${id}/config`)
}

export const insert = async(data) =>{
    return http.fetchPost(`/api/jobType/insert`, data)
}

export const update = async(data) =>{
    return http.fetchPost(`/api/jobType/update`, data)
}

export const del = async(id) =>{
    return http.fetchPost(`/api/jobType/delete/${id}`, {})
}