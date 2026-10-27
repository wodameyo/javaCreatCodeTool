export interface ConnectionRequest {
  host: string
  port: number
  database: string
  username: string
  password: string
  driverClassName?: string
  url?: string
}

export interface ConnectionTestResponse {
  sessionId: string
  jdbcUrl: string
  productName: string
  productVersion: string
  expiresInSeconds: number
}

export interface TableInfo {
  name: string
  comment: string
  rowCount: number
  columnCount: number
}

export interface TableListResponse {
  sessionId: string
  tables: TableInfo[]
}

export interface PackageConfig {
  po: string
  query: string
  vo: string
  utils: string
  enums: string
  exception: string
  mappers: string
  service: string
  serviceImpl: string
  controller: string
}

export interface OutputConfig {
  backendDir: string
  resourcesDir: string
  frontendDir: string
  overwritePolicy: string
}

export interface BackendConfig {
  lombok: boolean
  dateType: string
  transaction: boolean
  ignoreJsonFields: string[]
}

export interface TableConfig {
  include: string[]
  exclude: string[]
  ignorePrefix: string[]
}

export interface GeneratorConfigModel {
  packageBase: string
  packages: PackageConfig
  author: string
  output: OutputConfig
  backend: BackendConfig
  table: TableConfig
}

export interface FilePreview {
  logicalPath: string
  kind: string
  targetPath: string
  exists: boolean
  changed: boolean
  content: string
  existingContent?: string | null
}

export interface PreviewResponse {
  files: FilePreview[]
  total: number
  existingCount: number
  overwritePolicy: string
}

export interface PreviewRequest {
  sessionId: string
  tables: string[]
  config: GeneratorConfigModel
}

export interface GenerationSummary {
  dryRun: boolean
  created: string[]
  overwritten: string[]
  backedUp: string[]
  skipped: string[]
  total: number
}

export interface GenerateRequest extends PreviewRequest {
  writeToDisk: boolean
}

export interface GenerateResponse {
  token: string
  zipFileName: string
  summary: GenerationSummary
  fileCount: number
  elapsedMs: number
}

export interface DefaultsResponse {
  config: GeneratorConfigModel
  allowedOutputRoots: string[]
  sessionTtlMinutes: number
  version: string
  phase: string
}

export interface HealthResponse {
  status: string
  version: string
  phase: string
  time: string
}
