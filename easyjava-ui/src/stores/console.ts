import { defineStore } from 'pinia'
import type {
  ConnectionTestResponse,
  DefaultsResponse,
  GenerateResponse,
  GeneratorConfigModel,
  PreviewResponse,
  TableInfo
} from '../types'

const SESSION_KEY = 'easyjava.sessionId'
const CONNECTION_KEY = 'easyjava.connection'

interface ConnectionForm {
  host: string
  port: number
  database: string
  username: string
}

function defaultConfig(): GeneratorConfigModel {
  return {
    packageBase: 'com.easyjava',
    packages: {
      po: 'entity.po',
      query: 'entity.query',
      vo: 'entity.vo',
      utils: 'utils',
      enums: 'enums',
      exception: 'exception',
      mappers: 'mappers',
      service: 'service',
      serviceImpl: 'service.impl',
      controller: 'controller'
    },
    author: 'easyjava',
    output: {
      backendDir: 'easyjava-demo/src/main/java',
      resourcesDir: 'easyjava-demo/src/main/resources',
      frontendDir: 'easyjava-demo-ui',
      overwritePolicy: 'BACKUP'
    },
    backend: {
      lombok: false,
      dateType: 'LEGACY',
      transaction: true,
      ignoreJsonFields: ['status', 'taotle']
    },
    table: {
      include: [],
      exclude: [],
      ignorePrefix: ['ts_']
    }
  }
}

function loadConnection(): ConnectionForm {
  try {
    const raw = sessionStorage.getItem(CONNECTION_KEY)
    if (raw) {
      const parsed = JSON.parse(raw) as ConnectionForm
      return {
        host: parsed.host || 'localhost',
        port: parsed.port || 3306,
        database: parsed.database || 'test',
        username: parsed.username || 'root'
      }
    }
  } catch {
    // 忽略损坏的本地缓存
  }
  return { host: 'localhost', port: 3306, database: 'test', username: 'root' }
}

export const useConsoleStore = defineStore('console', {
  state: () => ({
    sessionId: sessionStorage.getItem(SESSION_KEY) || '',
    connection: loadConnection(),
    productName: '',
    productVersion: '',
    jdbcUrl: '',
    tables: [] as TableInfo[],
    selectedTables: [] as string[],
    config: defaultConfig(),
    configLoaded: false,
    allowedOutputRoots: [] as string[],
    sessionTtlMinutes: 30,
    preview: null as PreviewResponse | null,
    result: null as GenerateResponse | null
  }),

  actions: {
    saveConnection() {
      sessionStorage.setItem(CONNECTION_KEY, JSON.stringify(this.connection))
    },

    setSession(response: ConnectionTestResponse) {
      this.sessionId = response.sessionId
      sessionStorage.setItem(SESSION_KEY, response.sessionId)
      this.jdbcUrl = response.jdbcUrl
      this.productName = response.productName
      this.productVersion = response.productVersion
      this.tables = []
      this.selectedTables = []
      this.preview = null
      this.result = null
    },

    clearSession() {
      this.sessionId = ''
      this.productName = ''
      this.productVersion = ''
      this.jdbcUrl = ''
      this.tables = []
      this.selectedTables = []
      this.preview = null
      this.result = null
      sessionStorage.removeItem(SESSION_KEY)
    },

    applyDefaults(defaults: DefaultsResponse) {
      this.allowedOutputRoots = defaults.allowedOutputRoots
      this.sessionTtlMinutes = defaults.sessionTtlMinutes
      if (!this.configLoaded) {
        this.config = defaults.config
        this.configLoaded = true
      }
    },

    reset() {
      this.clearSession()
      this.config = defaultConfig()
      this.configLoaded = false
    }
  }
})
