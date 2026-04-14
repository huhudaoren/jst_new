/**
 * 通用文件上传接口
 * 对应后端: CommonController POST /common/upload
 */
import { BASE_URL, getToken } from './request'

/**
 * 上传单个文件到后端
 * @param {String} filePath 本地临时文件路径
 * @param {Object} options
 * @param {Function} options.onProgress 上传进度回调 (percent: Number 0-100)
 * @returns {Promise<{url: String, fileName: String}>}
 */
export function uploadFile(filePath, options = {}) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    const task = uni.uploadFile({
      url: BASE_URL + '/common/upload',
      filePath,
      name: 'file',
      header: token ? { Authorization: 'Bearer ' + token } : {},
      success: (res) => {
        try {
          const body = JSON.parse(res.data)
          if (body.code === 200) {
            resolve({
              url: body.url || '',
              fileName: body.fileName || '',
              originalFilename: body.originalFilename || ''
            })
          } else {
            uni.showToast({ title: body.msg || '上传失败', icon: 'none' })
            reject(body)
          }
        } catch (e) {
          uni.showToast({ title: '上传响应解析失败', icon: 'none' })
          reject(e)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '上传失败，请检查网络', icon: 'none' })
        reject(err)
      }
    })

    if (task && options.onProgress) {
      task.onProgressUpdate((res) => {
        options.onProgress(res.progress || 0)
      })
    }
  })
}
