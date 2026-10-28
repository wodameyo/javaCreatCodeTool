import type { PageQuery, PaginationResultVO } from './common'

/**
 * 商品信息表
 */
export interface ProductInfo {
  /** 主键 */
  id?: number
  /** 商品名称 */
  productName?: string
  /** 商品编码 */
  productCode?: string
  /** 商品分类 */
  category?: string
  /** 售价 */
  price?: number
  /** 库存 */
  stock?: number
  /** 是否上架 */
  onSale?: boolean
  /** 商品描述 */
  description?: string
  /** 创建时间 */
  createTime?: string
}

export interface ProductInfoQuery extends PageQuery {
  id: string
  productNameFuzzy: string
  productCodeFuzzy: string
  categoryFuzzy: string
  price: string
  stock: string
  onSale: string
  descriptionFuzzy: string
  createTimeStart: string
  createTimeEnd: string
}

export type ProductInfoPage = PaginationResultVO<ProductInfo>
