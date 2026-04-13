<template>
  <el-drawer
    :visible.sync="localVisible"
    :size="drawerSize"
    direction="rtl"
    append-to-body
    custom-class="enroll-detail-drawer"
    :with-header="false"
  >
    <div class="drawer-shell">
      <div class="drawer-header">
        <div>
          <div class="drawer-eyebrow">报名审核详情</div>
          <div class="drawer-title">
            <span>{{ headerTitle }}</span>
            <el-tag size="small" :type="statusMeta.type">{{ statusMeta.label }}</el-tag>
            <el-tag v-if="sourceLabel !== '待后端补充'" size="small" type="info">{{ sourceLabel }}</el-tag>
            <el-tag v-if="isTemporaryArchive" size="small" type="warning">临时档案</el-tag>
          </div>
          <div class="drawer-subtitle">
            <span>报名编号：{{ detail.enrollNo || '--' }}</span>
            <span>提交时间：{{ formatTime(detail.submitTime) }}</span>
          </div>
        </div>
        <el-button icon="el-icon-close" circle plain @click="closeDrawer" />
      </div>

      <div v-loading="loading" class="drawer-body">
        <template v-if="!loading">
          <div class="section-card">
            <div class="section-head">学生基础信息</div>
            <div class="info-grid">
              <div v-for="item in studentInfo" :key="item.label" class="info-item">
                <div class="info-label">{{ item.label }}</div>
                <div class="info-value">{{ item.value || '--' }}</div>
              </div>
            </div>
          </div>

          <div class="section-card">
            <div class="section-head">赛事信息</div>
            <div class="info-grid">
              <div v-for="item in contestInfo" :key="item.label" class="info-item">
                <div class="info-label">{{ item.label }}</div>
                <div class="info-value">{{ item.value || '--' }}</div>
              </div>
            </div>
          </div>

          <div class="section-card">
            <div class="section-head">动态表单快照</div>
            <div v-if="formEntries.length" class="form-grid">
              <div v-for="entry in formEntries" :key="entry.key" class="form-item">
                <div class="info-label">{{ entry.label }}</div>
                <div class="info-value">
                  <pre v-if="entry.multiline" class="json-block">{{ entry.value }}</pre>
                  <span v-else>{{ entry.value || '--' }}</span>
                </div>
              </div>
            </div>
            <div v-else class="empty-block">当前接口未返回可渲染的表单字段，已保留原始快照容错能力。</div>
          </div>

          <div class="section-card">
            <div class="section-head">作品材料</div>
            <div v-if="attachments.length" class="attachment-grid">
              <div v-for="item in attachments" :key="item.key" class="attachment-card">
                <el-image
                  v-if="item.kind === 'image'"
                  :src="item.url"
                  :preview-src-list="imagePreviewList"
                  fit="cover"
                  class="attachment-image"
                />
                <div v-else class="attachment-file">
                  <i :class="item.kind === 'pdf' ? 'el-icon-document' : 'el-icon-video-camera-solid'"></i>
                  <span>{{ item.name }}</span>
                </div>
                <div class="attachment-actions">
                  <el-button type="text" size="mini" @click="openAttachment(item)">
                    {{ item.kind === 'image' ? '预览' : '打开' }}
                  </el-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-block">暂无附件</div>
          </div>

          <div class="section-card">
            <div class="section-head">审核记录</div>
            <div v-if="auditRecords.length" class="audit-timeline">
              <div v-for="(record, index) in auditRecords" :key="index" class="audit-record">
                <div class="audit-status">
                  <el-tag size="small" :type="statusType(record.result)">{{ statusLabel(record.result) }}</el-tag>
                </div>
                <div class="audit-content">
                  <div class="audit-meta">
                    <span>{{ record.operator || '系统留痕' }}</span>
                    <span>{{ formatTime(record.time || record.updateTime) }}</span>
                  </div>
                  <div class="audit-remark">{{ record.remark || '无备注' }}</div>
                </div>
              </div>
            </div>
            <div v-else class="empty-block">当前接口未返回审核历史，仅可展示当前审核结果。</div>
          </div>
        </template>
      </div>

      <div class="drawer-footer">
        <div class="footer-form">
          <div class="footer-title">审核操作</div>
          <el-input
            v-model="auditForm.auditRemark"
            type="textarea"
            :rows="3"
            maxlength="255"
            show-word-limit
            placeholder="通过可留空；驳回时请填写原因"
            :disabled="!canAudit || submitting"
          />
        </div>
        <div class="footer-actions">
          <el-button @click="closeDrawer">关闭</el-button>
          <el-button
            type="danger"
            :disabled="!canAudit"
            :loading="submitting"
            v-hasRole="['jst_partner']"
            @click="handleAudit('rejected')"
          >
            驳回
          </el-button>
          <el-button
            type="primary"
            :disabled="!canAudit"
            :loading="submitting"
            v-hasRole="['jst_partner']"
            @click="handleAudit('approved')"
          >
            通过
          </el-button>
        </div>
      </div>
    </div>
  </el-drawer>
</template>

<script>
import { auditPartnerEnroll, getPartnerEnrollDetail } from '@/api/partner/enroll'
import { parseTime } from '@/utils/ruoyi'

const STATUS_META = {
  pending: { label: '待审核', type: 'warning' },
  approved: { label: '已通过', type: 'success' },
  rejected: { label: '已驳回', type: 'danger' },
  supplement: { label: '补件中', type: 'info' }
}

const SOURCE_META = {
  self: '个人报名',
  channel_single: '渠道代报名',
  channel_batch: '渠道代报名'
}

export default {
  name: 'EnrollDetailDrawer',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    enrollId: {
      type: [Number, String],
      default: null
    },
    mobile: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      localVisible: false,
      loading: false,
      submitting: false,
      detail: {},
      auditForm: {
        auditRemark: ''
      }
    }
  },
  computed: {
    drawerSize() {
      return this.mobile ? '100%' : '760px'
    },
    headerTitle() {
      return this.detail.participantName || '报名详情'
    },
    statusMeta() {
      return STATUS_META[this.detail.auditStatus] || { label: this.detail.auditStatus || '未知状态', type: 'info' }
    },
    sourceLabel() {
      const source = this.detail.enrollSource || this.detail.source || this.detail.sourceType
      return SOURCE_META[source] || (source ? source : '待后端补充')
    },
    isTemporaryArchive() {
      if (this.detail.isTemporaryArchive === true || this.detail.tempArchive === true) {
        return true
      }
      if (this.detail.participantType === 'temporary_participant') {
        return true
      }
      return this.detail.userId === null && Object.prototype.hasOwnProperty.call(this.detail, 'userId')
    },
    canAudit() {
      return ['pending', 'supplement'].includes(this.detail.auditStatus)
    },
    studentInfo() {
      return [
        { label: '参赛人', value: this.detail.participantName },
        { label: '监护人', value: this.detail.guardianName },
        { label: '联系电话', value: this.detail.guardianMobileMasked },
        { label: '学校', value: this.detail.school }
      ]
    },
    contestInfo() {
      return [
        { label: '赛事名称', value: this.detail.contestName },
        { label: '赛事 ID', value: this.detail.contestId },
        { label: '表单模板', value: this.detail.templateId ? `${this.detail.templateId} / v${this.detail.templateVersion || '--'}` : '--' },
        { label: '资料状态', value: this.materialStatusLabel(this.detail.materialStatus) },
        { label: '审核备注', value: this.detail.auditRemark || '--' },
        { label: '补件说明', value: this.detail.supplementRemark || '--' }
      ]
    },
    normalizedSnapshot() {
      const raw = this.detail.formSnapshotJson
      if (!raw) {
        return {}
      }
      if (typeof raw === 'string') {
        try {
          return JSON.parse(raw)
        } catch (error) {
          return { raw }
        }
      }
      return raw
    },
    formEntries() {
      return this.buildFormEntries(this.normalizedSnapshot)
    },
    attachments() {
      return this.buildAttachments(this.normalizedSnapshot)
    },
    imagePreviewList() {
      return this.attachments.filter(item => item.kind === 'image').map(item => item.url)
    },
    auditRecords() {
      return this.buildAuditRecords()
    }
  },
  watch: {
    visible: {
      immediate: true,
      handler(val) {
        this.localVisible = val
        if (val && this.enrollId) {
          this.loadDetail()
        }
      }
    },
    localVisible(val) {
      this.$emit('update:visible', val)
      if (!val) {
        this.resetState()
      }
    },
    enrollId(val, oldVal) {
      if (this.localVisible && val && val !== oldVal) {
        this.loadDetail()
      }
    }
  },
  methods: {
    resetState() {
      this.loading = false
      this.submitting = false
      this.detail = {}
      this.auditForm = { auditRemark: '' }
    },
    loadDetail() {
      if (!this.enrollId) {
        return
      }
      this.loading = true
      getPartnerEnrollDetail(this.enrollId).then(response => {
        const payload = response && response.data ? response.data : {}
        this.detail = payload
        this.auditForm.auditRemark = payload.auditRemark || payload.supplementRemark || ''
      }).finally(() => {
        this.loading = false
      })
    },
    closeDrawer() {
      this.localVisible = false
    },
    handleAudit(result) {
      const remark = (this.auditForm.auditRemark || '').trim()
      if (result === 'rejected' && !remark) {
        this.$modal.msgError('驳回时请填写原因')
        return
      }
      if (!this.enrollId || this.submitting) {
        return
      }
      this.submitting = true
      auditPartnerEnroll(this.enrollId, {
        result,
        auditRemark: remark
      }).then(() => {
        this.$modal.msgSuccess(result === 'approved' ? '审核通过成功' : '驳回成功')
        this.$emit('audited')
        this.loadDetail()
      }).finally(() => {
        this.submitting = false
      })
    },
    formatTime(value) {
      return value ? parseTime(value, '{y}-{m}-{d} {h}:{i}') : '--'
    },
    statusLabel(status) {
      return (STATUS_META[status] && STATUS_META[status].label) || status || '未知状态'
    },
    statusType(status) {
      return (STATUS_META[status] && STATUS_META[status].type) || 'info'
    },
    materialStatusLabel(status) {
      const meta = {
        draft: '草稿',
        submitted: '已提交',
        supplement: '待补件'
      }
      return meta[status] || status || '--'
    },
    buildFormEntries(snapshot) {
      const entries = []
      const formData = snapshot && snapshot.formData
      if (Array.isArray(formData)) {
        formData.forEach((item, index) => {
          const key = item && (item.key || item.field || item.label || item.name || `field_${index + 1}`)
          const rawValue = item && Object.prototype.hasOwnProperty.call(item, 'value') ? item.value : item
          entries.push(this.makeEntry(key, rawValue))
        })
      } else if (this.isPlainObject(formData)) {
        Object.keys(formData).forEach(key => {
          entries.push(this.makeEntry(key, formData[key]))
        })
      } else if (formData !== undefined && formData !== null) {
        entries.push(this.makeEntry('formData', formData))
      }

      if (entries.length) {
        return entries
      }

      if (this.isPlainObject(snapshot)) {
        Object.keys(snapshot)
          .filter(key => !['template_id', 'template_version', 'attachments', 'formData'].includes(key))
          .forEach(key => {
            entries.push(this.makeEntry(key, snapshot[key]))
          })
      }
      return entries
    },
    buildAttachments(snapshot) {
      const rawList = snapshot && snapshot.attachments
      if (!Array.isArray(rawList)) {
        return []
      }
      return rawList
        .map((item, index) => this.normalizeAttachment(item, index))
        .filter(Boolean)
    },
    buildAuditRecords() {
      const rawHistory = this.detail.auditHistory || this.detail.auditRecords || this.detail.auditLogs
      if (Array.isArray(rawHistory) && rawHistory.length) {
        return rawHistory.map(item => ({
          result: item.result || item.auditStatus || item.status,
          remark: item.remark || item.auditRemark || item.reason,
          time: item.time || item.auditTime || item.updateTime || item.createTime,
          operator: item.operator || item.auditBy || item.createBy
        }))
      }

      const fallback = []
      if (this.detail.supplementRemark) {
        fallback.push({
          result: 'supplement',
          remark: this.detail.supplementRemark,
          time: this.detail.updateTime
        })
      }
      if (this.detail.auditStatus || this.detail.auditRemark) {
        fallback.push({
          result: this.detail.auditStatus,
          remark: this.detail.auditRemark,
          time: this.detail.updateTime
        })
      }
      return fallback
    },
    makeEntry(key, rawValue) {
      const value = this.formatFieldValue(rawValue)
      return {
        key,
        label: this.prettyFieldLabel(key),
        value,
        multiline: typeof value === 'string' && value.includes('\n')
      }
    },
    prettyFieldLabel(key) {
      const map = {
        name: '姓名',
        mobile: '手机号',
        guardian_name: '监护人',
        guardian_mobile: '监护人手机号',
        school: '学校',
        grade: '年级',
        work_name: '作品名称',
        work_type: '作品类型',
        introduction: '作品介绍',
        description: '描述'
      }
      if (map[key]) {
        return map[key]
      }
      return String(key)
        .replace(/_/g, ' ')
        .replace(/([a-z])([A-Z])/g, '$1 $2')
    },
    formatFieldValue(value) {
      if (value === null || value === undefined || value === '') {
        return '--'
      }
      if (Array.isArray(value)) {
        return value.map(item => this.scalarValue(item)).join('\n')
      }
      if (this.isPlainObject(value)) {
        return JSON.stringify(value, null, 2)
      }
      return String(value)
    },
    scalarValue(value) {
      if (value === null || value === undefined) {
        return '--'
      }
      if (this.isPlainObject(value) || Array.isArray(value)) {
        return JSON.stringify(value)
      }
      return String(value)
    },
    normalizeAttachment(item, index) {
      const url = typeof item === 'string'
        ? item
        : item && (item.url || item.fileUrl || item.path || item.value)
      if (!url) {
        return null
      }
      return {
        key: `${index}-${url}`,
        url,
        name: (item && (item.name || item.fileName)) || this.extractFileName(url),
        kind: this.attachmentKind(url)
      }
    },
    attachmentKind(url) {
      const lower = String(url).toLowerCase()
      if (/\.(png|jpe?g|gif|bmp|webp|svg)(\?.*)?$/.test(lower)) {
        return 'image'
      }
      if (/\.pdf(\?.*)?$/.test(lower)) {
        return 'pdf'
      }
      if (/\.(mp4|mov|m4v|avi|webm)(\?.*)?$/.test(lower)) {
        return 'video'
      }
      return 'file'
    },
    extractFileName(url) {
      const cleanUrl = String(url).split('?')[0]
      const segments = cleanUrl.split('/')
      return segments[segments.length - 1] || '附件'
    },
    openAttachment(item) {
      window.open(item.url, '_blank')
    },
    isPlainObject(value) {
      return Object.prototype.toString.call(value) === '[object Object]'
    }
  }
}
</script>

<style scoped>
.drawer-shell {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.drawer-header {
  padding: 20px 24px 16px;
  background: #ffffff;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.drawer-eyebrow {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}

.drawer-title {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.drawer-subtitle {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
  color: #606266;
  font-size: 13px;
}

.drawer-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px 24px;
}

.section-card {
  background: #ffffff;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 14px;
}

.section-head {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 14px;
}

.info-grid,
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.info-item,
.form-item {
  background: #fafafa;
  border-radius: 10px;
  padding: 12px;
}

.info-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.info-value {
  color: #303133;
  line-height: 1.6;
  word-break: break-word;
}

.json-block {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: Consolas, Monaco, monospace;
  font-size: 12px;
  color: #303133;
}

.attachment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
}

.attachment-card {
  border: 1px solid #ebeef5;
  border-radius: 12px;
  overflow: hidden;
  background: #ffffff;
}

.attachment-image {
  display: block;
  width: 100%;
  height: 130px;
  background: #f4f6f8;
}

.attachment-file {
  height: 130px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background: #f8fafc;
  color: #606266;
  padding: 12px;
  text-align: center;
}

.attachment-file i {
  font-size: 28px;
}

.attachment-actions {
  padding: 8px 12px;
  border-top: 1px solid #f0f2f5;
}

.audit-timeline {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.audit-record {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 12px;
  border-radius: 10px;
  background: #fafafa;
}

.audit-content {
  flex: 1;
}

.audit-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  color: #909399;
  font-size: 12px;
  margin-bottom: 6px;
}

.audit-remark {
  color: #303133;
  line-height: 1.6;
}

.empty-block {
  padding: 18px 14px;
  border-radius: 10px;
  background: #fafafa;
  color: #909399;
  font-size: 13px;
}

.drawer-footer {
  border-top: 1px solid #ebeef5;
  background: #ffffff;
  padding: 16px 24px 20px;
}

.footer-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}

.footer-actions {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 992px) {
  .drawer-header,
  .drawer-body,
  .drawer-footer {
    padding-left: 16px;
    padding-right: 16px;
  }

  .info-grid,
  .form-grid {
    grid-template-columns: 1fr;
  }

  .attachment-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 640px) {
  .drawer-title {
    font-size: 18px;
  }

  .attachment-grid {
    grid-template-columns: 1fr;
  }

  .footer-actions {
    flex-wrap: wrap;
  }

  .footer-actions .el-button {
    width: 100%;
    margin-left: 0;
  }
}
</style>
