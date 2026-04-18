<template>
  <div class="app-container partner-cert-page">
    <div class="page-banner">
      <div>
        <div class="page-eyebrow">Certificate Workspace</div>
        <div class="page-title">证书管理</div>
        <div class="page-desc">维护证书模板，基于已发布成绩生成证书草稿并提交平台审核。</div>
      </div>
      <div class="banner-actions">
        <el-button icon="el-icon-picture-outline" @click="openDesigner(null)">新增模板</el-button>
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
      <el-table-column label="操作" fixed="right" width="220">
        <template slot-scope="scope">
          <el-button v-if="scope.row.displayStatus === 'draft'" type="text" size="mini" @click="submitRows([scope.row])">提审</el-button>
          <el-button type="text" size="mini" @click="openPreview(scope.row)">预览</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 模板列表管理区域 -->
    <el-card shadow="never" class="template-card">
      <div slot="header" class="template-header">
        <span class="section-title">证书模板库</span>
        <el-button type="primary" plain size="small" icon="el-icon-plus" @click="openDesigner(null)">新增模板</el-button>
      </div>
      <el-table v-loading="templateLoading" :data="templates" size="small">
        <el-table-column label="模板名称" prop="templateName" min-width="180" show-overflow-tooltip />
        <el-table-column label="审核状态" min-width="100">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.auditStatus === 'approved' ? 'success' : 'warning'">
              {{ scope.row.auditStatus === 'approved' ? '已审核' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template slot-scope="scope">
            <el-button type="text" size="mini" icon="el-icon-edit" @click="openDesigner(scope.row)">编辑设计</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 设计器 Drawer -->
    <el-drawer
      :title="designerTitle"
      :visible.sync="designerVisible"
      size="95%"
      direction="rtl"
      append-to-body
      :before-close="onDesignerClose"
      class="designer-drawer"
    >
      <div class="designer-drawer-body">
        <el-form v-if="!editingTemplateId" ref="tplNameForm" :model="newTemplateForm" :rules="tplNameRules" :inline="true" size="small" class="tpl-name-bar">
          <el-form-item label="模板名称" prop="templateName">
            <el-input v-model="newTemplateForm.templateName" maxlength="64" placeholder="请输入模板名称" style="width: 260px" />
          </el-form-item>
        </el-form>
        <CertDesigner
          v-if="designerVisible"
          ref="certDesigner"
          :layout-json="designerLayoutJson"
          :bg-image="designerBgImage"
          :owner-type="designerOwnerType"
          :title="designerTitle"
          @save="onDesignerSave"
        />
      </div>
    </el-drawer>

    <!-- 证书预览 -->
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
  getCertTemplate,
  listCertTemplates,
  listPartnerCerts,
  previewCert,
  saveCertTemplate,
  submitCertReview,
  updateCertTemplate
} from '@/api/partner/cert'
import CertDesigner from './components/CertDesigner/index.vue'

const STATUS_META = {
  draft: { label: '草稿', type: 'info' },
  pending: { label: '审核中', type: 'warning' },
  granted: { label: '已发放', type: 'success' },
  voided: { label: '已作废', type: 'danger' }
}

export default {
  name: 'PartnerCertManage',
  components: { CertDesigner },
  data() {
    return {
      loading: false,
      templateLoading: false,
      showSearch: true,
      total: 0,
      contests: [],
      templates: [],
      certList: [],
      selectedRows: [],
      selectedTemplateId: null,
      previewDialogOpen: false,
      previewImage: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestId: this.$route.query.contestId ? Number(this.$route.query.contestId) : null,
        participantName: null,
        displayStatus: null
      },
      statusOptions: Object.keys(STATUS_META).map(key => ({ value: key, label: STATUS_META[key].label })),
      // Designer state
      designerVisible: false,
      designerTitle: '新增证书模板',
      designerLayoutJson: null,
      designerBgImage: '',
      designerOwnerType: 'contest',
      editingTemplateId: null,
      newTemplateForm: { templateName: '' },
      tplNameRules: {
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
  mounted() {
    // PATCH-2: Picker「查看详情」自动打开对应证书模板到设计器
    const autoId = this.$route.query.templateId || this.$route.query.autoOpenId
    if (autoId) {
      this.$nextTick(() => this.handleAutoOpen(Number(autoId)))
    }
  },
  methods: {
    loadContests() {
      listPartnerContests({ pageNum: 1, pageSize: 100 }).then(response => {
        this.contests = Array.isArray(response.rows) ? response.rows : []
      })
    },
    loadTemplates() {
      this.templateLoading = true
      return listCertTemplates().then(response => {
        this.templates = Array.isArray(response.data) ? response.data : []
      }).finally(() => {
        this.templateLoading = false
      })
    },
    async handleAutoOpen(id) {
      if (!id || Number.isNaN(id)) return
      // 证书设计器：先确保 templates 拉好，再按 id 找到记录 → openDesigner
      try {
        // 等 loadTemplates 若仍在跑，再并等一次（created 已经触发）
        if (this.templateLoading) {
          await new Promise(r => setTimeout(r, 300))
        }
        let template = (this.templates || []).find(t => Number(t.templateId) === Number(id))
        if (!template) {
          // 可能分页未取全，兜底再拉一次
          await this.loadTemplates()
          template = (this.templates || []).find(t => Number(t.templateId) === Number(id))
        }
        if (template) {
          this.openDesigner(template)
        } else {
          this.$modal.msgWarning('未找到指定证书模板，可能已删除')
        }
      } catch (e) {
        this.$modal.msgWarning('未找到指定证书模板，可能已删除')
      }
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
    },

    // ==================== Designer ====================
    openDesigner(template) {
      if (template && template.templateId) {
        this.editingTemplateId = template.templateId
        this.designerTitle = '编辑模板 - ' + (template.templateName || '')
        getCertTemplate(template.templateId).then(response => {
          const data = response.data || template
          this.designerLayoutJson = data.layoutJson || null
          this.designerBgImage = data.bgImage || data.backgroundImage || ''
          this.designerVisible = true
        }).catch(() => {
          this.designerLayoutJson = template.layoutJson || null
          this.designerBgImage = template.bgImage || template.backgroundImage || ''
          this.designerVisible = true
        })
      } else {
        this.editingTemplateId = null
        this.designerTitle = '新增证书模板'
        this.designerLayoutJson = null
        this.designerBgImage = ''
        this.newTemplateForm = { templateName: '' }
        this.designerVisible = true
      }
    },
    onDesignerSave({ layoutJson, thumbnail }) {
      if (this.editingTemplateId) {
        updateCertTemplate(this.editingTemplateId, {
          layoutJson,
          thumbnail
        }).then(() => {
          this.$modal.msgSuccess('模板已保存')
          this.designerVisible = false
          this.loadTemplates()
        }).catch(() => {
          this.$modal.msgError('保存失败，请重试')
        })
      } else {
        if (this.$refs.tplNameForm) {
          this.$refs.tplNameForm.validate(valid => {
            if (!valid) return
            saveCertTemplate({
              templateName: this.newTemplateForm.templateName,
              layoutJson,
              thumbnail
            }).then(() => {
              this.$modal.msgSuccess('模板已创建')
              this.designerVisible = false
              this.loadTemplates()
            }).catch(() => {
              this.$modal.msgError('创建失败，请重试')
            })
          })
        }
      }
    },
    onDesignerClose(done) {
      this.$confirm('关闭将丢失未保存的设计，确认关闭？', '提示', {
        type: 'warning'
      }).then(() => done()).catch(() => {})
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
.template-card {
  margin-top: 20px;
}
.template-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.designer-drawer-body {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
  padding: 0 12px 12px;
}
.tpl-name-bar {
  padding: 8px 0;
  flex-shrink: 0;
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

<style>
/* Unscoped: drawer body padding override */
.designer-drawer .el-drawer__body {
  padding: 0;
  overflow: hidden;
}
</style>
