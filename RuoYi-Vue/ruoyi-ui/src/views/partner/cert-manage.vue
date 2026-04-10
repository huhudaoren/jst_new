<template>
  <div class="app-container partner-cert-page">
    <div class="page-banner">
      <div>
        <div class="page-eyebrow">Certificate Workspace</div>
        <div class="page-title">证书管理</div>
        <div class="page-desc">维护证书模板，基于已发布成绩生成证书草稿并提交平台审核。</div>
      </div>
      <div class="banner-actions">
        <el-button icon="el-icon-picture-outline" @click="templateDialogOpen = true">新增模板</el-button>
        <el-button type="primary" icon="el-icon-medal" :disabled="!canBatchGrant" @click="batchGrant">批量生成证书</el-button>
      </div>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="76px">
      <el-form-item label="赛事" prop="contestId">
        <el-select v-model="queryParams.contestId" filterable clearable placeholder="请选择赛事" @change="handleQuery">
          <el-option v-for="item in contests" :key="item.contestId" :label="item.contestName" :value="item.contestId" />
        </el-select>
      </el-form-item>
      <el-form-item label="模板">
        <el-select v-model="selectedTemplateId" clearable placeholder="选择生成模板">
          <el-option v-for="item in templates" :key="item.templateId" :label="templateLabel(item)" :value="item.templateId" />
        </el-select>
      </el-form-item>
      <el-form-item label="姓名" prop="participantName">
        <el-input v-model="queryParams.participantName" clearable placeholder="参赛人姓名" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="displayStatus">
        <el-select v-model="queryParams.displayStatus" clearable placeholder="全部状态">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="toolbar-row">
      <el-button icon="el-icon-s-promotion" size="mini" :disabled="!selectedRows.length" @click="submitSelected">提交审核</el-button>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </div>

    <el-table v-loading="loading" :data="certList" @selection-change="selectedRows = $event">
      <el-table-column type="selection" width="46" />
      <el-table-column label="证书编号" prop="certNo" min-width="170" />
      <el-table-column label="赛事" prop="contestName" min-width="180" show-overflow-tooltip />
      <el-table-column label="参赛人" prop="participantName" min-width="120" />
      <el-table-column label="奖项" prop="awardLevel" min-width="120" />
      <el-table-column label="模板" prop="templateName" min-width="150" show-overflow-tooltip />
      <el-table-column label="状态" min-width="110">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.displayStatus)">{{ statusLabel(scope.row.displayStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="180">
        <template slot-scope="scope">
          <el-button v-if="scope.row.displayStatus === 'draft'" type="text" size="mini" @click="submitRows([scope.row])">提审</el-button>
          <el-button type="text" size="mini" @click="openPreview(scope.row)">预览</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="新增证书模板" :visible.sync="templateDialogOpen" width="560px" append-to-body>
      <el-form ref="templateForm" :model="templateForm" :rules="templateRules" label-width="96px">
        <el-form-item label="模板名称" prop="templateName"><el-input v-model="templateForm.templateName" maxlength="64" /></el-form-item>
        <el-form-item label="底图地址" prop="bgImage"><el-input v-model="templateForm.bgImage" placeholder="可填 OSS key，也可上传图片" /></el-form-item>
        <el-form-item label="上传底图">
          <el-upload action="" :auto-upload="false" :limit="1" :on-change="onTemplateFileChange" :on-remove="onTemplateFileRemove" accept=".jpg,.jpeg,.png,.gif,.webp">
            <el-button icon="el-icon-upload">选择图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="布局JSON"><el-input v-model="templateForm.layoutJson" type="textarea" :rows="4" /></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="templateDialogOpen = false">取消</el-button>
        <el-button type="primary" @click="saveTemplate">保存并提审</el-button>
      </div>
    </el-dialog>

    <el-dialog title="证书预览" :visible.sync="previewDialogOpen" width="760px" append-to-body>
      <div class="preview-box">
        <img v-if="previewImage" :src="previewImage" alt="证书预览" />
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPartnerContests } from '@/api/partner/contest'
import {
  batchGrantCerts,
  listCertTemplates,
  listPartnerCerts,
  previewCert,
  saveCertTemplate,
  submitCertReview,
  uploadCertTemplate
} from '@/api/partner/cert'

const STATUS_META = {
  draft: { label: '草稿', type: 'info' },
  pending: { label: '审核中', type: 'warning' },
  granted: { label: '已发放', type: 'success' },
  voided: { label: '已作废', type: 'danger' }
}

export default {
  name: 'PartnerCertManage',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      contests: [],
      templates: [],
      certList: [],
      selectedRows: [],
      selectedTemplateId: null,
      templateDialogOpen: false,
      previewDialogOpen: false,
      previewImage: null,
      templateFile: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestId: this.$route.query.contestId ? Number(this.$route.query.contestId) : null,
        participantName: null,
        displayStatus: null
      },
      templateForm: {
        templateName: null,
        bgImage: null,
        layoutJson: '{}'
      },
      statusOptions: Object.keys(STATUS_META).map(key => ({ value: key, label: STATUS_META[key].label })),
      templateRules: {
        templateName: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }]
      }
    }
  },
  computed: {
    canBatchGrant() {
      return !!this.queryParams.contestId && !!this.selectedTemplateId
    }
  },
  created() {
    this.loadContests()
    this.loadTemplates()
    this.getList()
  },
  methods: {
    loadContests() {
      listPartnerContests({ pageNum: 1, pageSize: 100 }).then(response => {
        this.contests = Array.isArray(response.rows) ? response.rows : []
      })
    },
    loadTemplates() {
      listCertTemplates().then(response => {
        this.templates = Array.isArray(response.data) ? response.data : []
      })
    },
    getList() {
      this.loading = true
      listPartnerCerts(this.queryParams).then(response => {
        this.certList = Array.isArray(response.rows) ? response.rows : []
        this.total = Number(response.total || 0)
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.pageNum = 1
      this.getList()
    },
    onTemplateFileChange(file) {
      this.templateFile = file.raw
    },
    onTemplateFileRemove() {
      this.templateFile = null
    },
    saveTemplate() {
      this.$refs.templateForm.validate(valid => {
        if (!valid) return
        const promise = this.templateFile ? this.uploadTemplate() : saveCertTemplate(this.templateForm)
        promise.then(() => {
          this.$modal.msgSuccess('模板已保存，等待平台审核')
          this.templateDialogOpen = false
          this.templateFile = null
          this.templateForm = { templateName: null, bgImage: null, layoutJson: '{}' }
          this.loadTemplates()
        })
      })
    },
    uploadTemplate() {
      const formData = new FormData()
      formData.append('templateName', this.templateForm.templateName)
      if (this.templateForm.bgImage) formData.append('bgImage', this.templateForm.bgImage)
      if (this.templateForm.layoutJson) formData.append('layoutJson', this.templateForm.layoutJson)
      formData.append('file', this.templateFile)
      return uploadCertTemplate(formData)
    },
    batchGrant() {
      batchGrantCerts({
        contestId: this.queryParams.contestId,
        templateId: this.selectedTemplateId
      }).then(response => {
        const data = response.data || {}
        this.$modal.msgSuccess(`已生成 ${data.createdCount || 0} 本证书，跳过 ${data.skippedCount || 0} 条`)
        this.getList()
      })
    },
    submitSelected() {
      this.submitRows(this.selectedRows)
    },
    submitRows(rows) {
      const certIds = rows.map(item => item.certId)
      submitCertReview({ certIds }).then(() => {
        this.$modal.msgSuccess('已提交审核')
        this.getList()
      })
    },
    openPreview(row) {
      previewCert(row.certId).then(response => {
        this.previewImage = response.data && response.data.previewImage
        this.previewDialogOpen = true
      })
    },
    templateLabel(item) {
      const status = item.auditStatus === 'approved' ? '已审' : '待审'
      return `${item.templateName}（${status}）`
    },
    statusLabel(status) {
      return STATUS_META[status] ? STATUS_META[status].label : (status || '--')
    },
    statusType(status) {
      return STATUS_META[status] ? STATUS_META[status].type : 'info'
    }
  }
}
</script>

<style scoped>
.partner-cert-page .page-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 20px;
  margin-bottom: 16px;
  background: #f6f8fb;
  border: 1px solid #e7ebf2;
  border-radius: 8px;
}
.page-eyebrow {
  color: #6b7280;
  font-size: 12px;
  letter-spacing: 0;
}
.page-title {
  margin-top: 4px;
  color: #111827;
  font-size: 22px;
  font-weight: 700;
}
.page-desc {
  margin-top: 6px;
  color: #4b5563;
}
.banner-actions,
.toolbar-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.toolbar-row {
  margin: 12px 0;
}
.preview-box {
  display: flex;
  justify-content: center;
  min-height: 420px;
  background: #f9fafb;
  border: 1px solid #e7ebf2;
  border-radius: 8px;
}
.preview-box img {
  width: 100%;
  max-width: 700px;
  object-fit: contain;
}
@media (max-width: 768px) {
  .partner-cert-page .page-banner,
  .banner-actions,
  .toolbar-row {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
