import { request } from '../utils/request'
import type { UserInfo, UserInfoQuery, UserInfoPage } from '../types/user-info'

const BASE = '/userInfo'

export function loadUserInfoList(params: UserInfoQuery) {
  return request<UserInfoPage>({ url: BASE + '/loadDataList', method: 'get', params })
}

export function addUserInfo(data: UserInfo) {
  return request<null>({ url: BASE + '/add', method: 'post', data })
}

export function getUserInfoById(id: number) {
  return request<UserInfo | null>({
    url: BASE + '/getUserInfoById',
    method: 'get',
    params: { id }
  })
}

export function updateUserInfoById(data: UserInfo, id: number) {
  return request<null>({
    url: BASE + '/updateUserInfoById',
    method: 'post',
    data,
    params: { id }
  })
}

export function deleteUserInfoById(id: number) {
  return request<null>({
    url: BASE + '/deleteUserInfoById',
    method: 'post',
    params: { id }
  })
}
