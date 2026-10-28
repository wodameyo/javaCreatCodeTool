import axios, { type AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'

export interface ResponseVO<T> {
  code: number
  info: string
  status: string
  data: T
}

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '${frontend.apiBasePath}',
  timeout: 15000
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error?.response?.data?.info || error?.message || '请求失败'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export async function request<T>(config: AxiosRequestConfig): Promise<T> {
  const response = await http.request<ResponseVO<T>>(config)
  const body = response.data
  if (!body || body.code !== 200) {
    const message = body?.info || '请求失败'
    ElMessage.error(message)
    throw new Error(message)
  }
  return body.data
}

export default http
