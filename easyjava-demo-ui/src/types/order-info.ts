import type { PageQuery, PaginationResultVO } from './common'

/**
 * 订单信息表
 */
export interface OrderInfo {
  /** 主键 */
  id?: number
  /** 订单号 */
  orderNo?: string
  /** 用户ID */
  userId?: number
  /** 商品ID */
  productId?: number
  /** 数量 */
  quantity?: number
  /** 订单金额 */
  amount?: number
  /** 订单状态 */
  orderStatus?: number
  /** 备注 */
  remark?: string
  /** 创建时间 */
  createTime?: string
}

export interface OrderInfoQuery extends PageQuery {
  id: string
  orderNoFuzzy: string
  userId: string
  productId: string
  quantity: string
  amount: string
  orderStatus: string
  remarkFuzzy: string
  createTimeStart: string
  createTimeEnd: string
}

export type OrderInfoPage = PaginationResultVO<OrderInfo>
