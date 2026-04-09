/**
 * 用户收货地址接口
 * 对应后端: jst-user/controller/wx/WxUserAddressController
 */
import request from './request'

export function getAddressList(options = {}) {
  return request({
    url: '/jst/wx/user/address/list',
    method: 'GET',
    ...options
  })
}

export function getAddressDetail(addressId, options = {}) {
  return request({
    url: `/jst/wx/user/address/${addressId}`,
    method: 'GET',
    ...options
  })
}

export function createAddress(data, options = {}) {
  return request({
    url: '/jst/wx/user/address',
    method: 'POST',
    data,
    ...options
  })
}

export function updateAddress(addressId, data, options = {}) {
  return request({
    url: `/jst/wx/user/address/${addressId}`,
    method: 'PUT',
    data,
    ...options
  })
}

export function deleteAddress(addressId, options = {}) {
  return request({
    url: `/jst/wx/user/address/${addressId}`,
    method: 'DELETE',
    ...options
  })
}

export function setDefaultAddress(addressId, options = {}) {
  return request({
    url: `/jst/wx/user/address/${addressId}/default`,
    method: 'POST',
    ...options
  })
}
