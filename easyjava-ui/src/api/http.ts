import axios from 'axios'

const http = axios.create({
  baseURL: '',
  timeout: 180000
})

export default http
