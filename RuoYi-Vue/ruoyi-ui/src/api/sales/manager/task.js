import request from '@/utils/request'

/**
 * 派发跟进任务给指定销售（仅 jst_sales_manager / admin 可调）。
 * @param {{ assigneeSalesId, channelId, title, description, dueDate }} data
 */
export function assignTask(data) {
  return request({ url: '/sales/manager/tasks', method: 'post', data })
}

/**
 * 查我派出的任务。
 * @param {string} [status] pending | in_progress | completed | overdue（不传查全部）
 */
export function listAssignedByMe(status) {
  return request({ url: '/sales/manager/tasks/assigned-by-me', method: 'get', params: { status } })
}
