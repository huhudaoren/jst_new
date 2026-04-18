import request from '@/utils/request'

/**
 * 我的任务列表。
 * @param {{ status?: string, pageNum?: number, pageSize?: number }} query
 *   status: pending | in_progress | completed | overdue（不传查全部）
 */
export function listMyTasks(query) {
  return request({ url: '/sales/me/tasks/list', method: 'get', params: query })
}

/**
 * 完成任务。
 * @param {number} taskId
 * @param {number|null} linkedRecordId 关联跟进记录 ID（可为 null）
 */
export function completeTask(taskId, linkedRecordId) {
  return request({
    url: `/sales/me/tasks/${taskId}/complete`,
    method: 'post',
    params: linkedRecordId != null ? { linkedRecordId } : {}
  })
}
