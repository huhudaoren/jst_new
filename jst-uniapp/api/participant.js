/**
 * 临时档案接口
 * 对应后端: jst-user/controller/wx/WxParticipantController
 */
import request from './request'

/** 我的所有参赛档案 */
export function myParticipants() {
  return request({ url: '/jst/wx/participant/my', method: 'GET' })
}

/** 主动认领临时档案 */
export function claimParticipant(participantId) {
  return request({
    url: `/jst/wx/participant/claim?participantId=${participantId}`,
    method: 'POST'
  })
}

/** 档案详情 */
export function getParticipantDetail(participantId) {
  return request({ url: `/jst/wx/participant/${participantId}`, method: 'GET' })
}
