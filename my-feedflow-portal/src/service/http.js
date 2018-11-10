import CONFIG from '../config'
import axios from 'axios'
import { Feedback } from '@icedesign/base';

axios.interceptors.response.use(response =>response , (err) => {
    if (err && err.response) {
        switch (err.response.status) {
            case 400: err.message = 'Bad Request (400)' ; break;
            case 401: err.message = 'Unauthorized (401)'; break;
            case 403: err.message = 'Forbidden (403)'; break;
            case 404: err.message = 'Not Found (404)'; break;
            case 408: err.message = 'Request Timeout (408)'; break;
            case 500: err.message = 'Internal Server Error (500)'; break;
            case 501: err.message = 'Not Implemented (501)'; break;
            case 502: err.message = 'Bad Gateway (502)'; break;
            case 503: err.message = 'Service Unavailable (503)'; break;
            case 504: err.message = 'Gateway Timeout (504)'; break;
            case 505: err.message = 'HTTP Version Not Supported (505)'; break;
            default: err.message = `Connection error (${err.response.status})!`;
        }
    } if(err.code === 'ECONNABORTED') {
        err.message = 'Request Timeout';
    } else{
        err.message = 'Connection to server failed!'
    }
    Feedback.toast.error(err.message);
    return Promise.reject(err);
});

axios.defaults.timeout = 10000
axios.defaults.headers.post['Content-Type'] = 'application/json'

export default {
  async fetchGet (url) {
    let data = await axios.get(CONFIG.SERVICE_HOST + url, { headers : fetchHeader() })
    return after(data)
  },
  async fetchQuickSearch (url) {
    let data = await axios.get(CONFIG.SERVICE_HOST + url , { headers : fetchHeader() })
    return after(data)
  },
  async fetchPost (url, params = {}) {
    let data = await axios.post(CONFIG.SERVICE_HOST + url, params , { headers : fetchHeader() })
    return after(data)
  }
}

export const fetchHeader = () => {
  return {  }
}

export const after = (data) => {
  if(data.state < 200 || data.state >= 400){ throw { message : data.statusText } }
  if(data.data.status){
    return data.data.data
  }else if(data.data.message){
    throw { message : data.data.message }
  }else{
    throw data.data
  }
}

