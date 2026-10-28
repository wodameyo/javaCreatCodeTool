import type { PageQuery, PaginationResultVO } from './common'

/**
 * 这是一个用户表
 */
export interface UserInfo {
  /** 这是id */
  id?: number
  /** 用户昵称 */
  userName?: string
  /** 商品名称 */
  productName?: string
  /** 创建时间 */
  createTime?: string
  /** 硬币数量 */
  taotle?: number
  /** 身份状态 */
  status?: number
  /** 创建日期 */
  createDate?: string
  /** 用户是否存在，0：删除 1：正常 */
  isDel?: number
}

export interface UserInfoQuery extends PageQuery {
  id: string
  userNameFuzzy: string
  productNameFuzzy: string
  createTimeStart: string
  createTimeEnd: string
  createDateStart: string
  createDateEnd: string
  isDel: string
}

export type UserInfoPage = PaginationResultVO<UserInfo>
