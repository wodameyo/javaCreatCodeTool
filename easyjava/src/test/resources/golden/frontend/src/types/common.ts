export interface PageQuery {
  pageNo: number
  pageSize: number
  orderBy?: string
}

export interface PaginationResultVO<T> {
  totalCount: number
  pageSize: number
  pageNo: number
  pageTotal: number
  list: T[]
}
