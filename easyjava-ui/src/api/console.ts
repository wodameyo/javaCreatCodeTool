import http from './http'
import type {
  ConnectionRequest,
  ConnectionTestResponse,
  DefaultsResponse,
  GenerateRequest,
  GenerateResponse,
  HealthResponse,
  PreviewRequest,
  PreviewResponse,
  TableListResponse
} from '../types'

export const consoleApi = {
  health() {
    return http.get<HealthResponse>('/api/console/health').then((response) => response.data)
  },

  defaults() {
    return http.get<DefaultsResponse>('/api/console/config/defaults').then((response) => response.data)
  },

  testConnection(payload: ConnectionRequest) {
    return http
      .post<ConnectionTestResponse>('/api/console/datasource/test', payload, { timeout: 20000 })
      .then((response) => response.data)
  },

  listTables(sessionId: string) {
    return http
      .post<TableListResponse>('/api/console/datasource/tables', { sessionId })
      .then((response) => response.data)
  },

  preview(payload: PreviewRequest) {
    return http
      .post<PreviewResponse>('/api/console/generate/preview', payload)
      .then((response) => response.data)
  },

  generate(payload: GenerateRequest) {
    return http
      .post<GenerateResponse>('/api/console/generate', payload)
      .then((response) => response.data)
  },

  downloadUrl(token: string) {
    return `/api/console/generate/download/${encodeURIComponent(token)}`
  }
}
