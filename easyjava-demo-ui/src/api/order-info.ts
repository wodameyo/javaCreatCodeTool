import { request } from '../utils/request'
import type { OrderInfo, OrderInfoQuery, OrderInfoPage } from '../types/order-info'

const BASE = '/orderInfo'

export function loadOrderInfoList(params: OrderInfoQuery) {
  return request<OrderInfoPage>({ url: BASE + '/loadDataList', method: 'get', params })
}

export function addOrderInfo(data: OrderInfo) {
  return request<null>({ url: BASE + '/add', method: 'post', data })
}

export function getOrderInfoById(id: number) {
  return request<OrderInfo | null>({
    url: BASE + '/getOrderInfoById',
    method: 'get',
    params: { id }
  })
}

export function updateOrderInfoById(data: OrderInfo, id: number) {
  return request<null>({
    url: BASE + '/updateOrderInfoById',
    method: 'post',
    data,
    params: { id }
  })
}

export function deleteOrderInfoById(id: number) {
  return request<null>({
    url: BASE + '/deleteOrderInfoById',
    method: 'post',
    params: { id }
  })
}
