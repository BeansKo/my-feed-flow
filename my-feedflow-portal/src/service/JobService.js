import http from './http'

export const list = async() =>{
    return http.fetchGet(`/api/job/list`)
}

export const get = async(id) =>{
    return http.fetchGet(`/api/job/get/${id}`)
}

export const insert = async(data) =>{
    return http.fetchPost(`/api/job/insert`, data)
}

export const update = async(data) =>{
    return http.fetchPost(`/api/job/update`, data)
}

export const del = async(id) =>{
    return http.fetchPost(`/api/job/delete/${id}`, {})
}

export const run = async(id) =>{
    return http.fetchPost(`/api/job/run/${id}`, {})
}

export const checkScheduled = async(value) =>{
    return http.fetchGet(`/api/job/checkScheduled?scheduled=${value}`)
}