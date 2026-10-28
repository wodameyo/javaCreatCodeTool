import { request } from '../utils/request'
import type { ProductInfo, ProductInfoQuery, ProductInfoPage } from '../types/product-info'

const BASE = '/productInfo'

export function loadProductInfoList(params: ProductInfoQuery) {
  return request<ProductInfoPage>({ url: BASE + '/loadDataList', method: 'get', params })
}

export function addProductInfo(data: ProductInfo) {
  return request<null>({ url: BASE + '/add', method: 'post', data })
}

export function getProductInfoById(id: number) {
  return request<ProductInfo | null>({
    url: BASE + '/getProductInfoById',
    method: 'get',
    params: { id }
  })
}

export function updateProductInfoById(data: ProductInfo, id: number) {
  return request<null>({
    url: BASE + '/updateProductInfoById',
    method: 'post',
    data,
    params: { id }
  })
}

export function deleteProductInfoById(id: number) {
  return request<null>({
    url: BASE + '/deleteProductInfoById',
    method: 'post',
    params: { id }
  })
}
