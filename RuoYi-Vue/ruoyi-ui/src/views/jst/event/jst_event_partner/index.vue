<template>
  <div class="app-container jst-event-partner-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">赛事方档案</p>
        <h2>赛事方关联</h2>
        <p class="hero-desc">支持赛事名与赛事方检索，统一查看入驻审核、合作与账号状态。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="88px"
      class="query-panel"
    >
      <el-form-item label="赛事" prop="partnerName">
        <el-input
          v-model.trim="contestKeyword"
          placeholder="赛事名称（模糊）"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛事方名称" prop="partnerName">
        <el-input
          v-model.trim="queryParams.partnerName"
          placeholder="请输入赛事方名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系人" prop="contactName">
        <el-input
          v-model.trim="queryParams.contactName"
          placeholder="请输入联系人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="全部" clearable>
          <el-option v-for="item in auditStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="账号状态" prop="accountStatus">
        <el-select v-model="queryParams.accountStatus" placeholder="全部" clearable>
          <el-option v-for="item in accountStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="合作状态" prop="coopStatus">
        <el-select v-model="queryParams.coopStatus" placeholder="全部" clearable>
          <el-option v-for="item in coopStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8 action-row">
      <el-col :xs="12" :sm="8" :md="4">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd" v-hasPermi="['jst:event:event_partner:add']">新增</el-button>
      </el-col>
      <el-col :xs="12" :sm="8" :md="4">
        <el-button icon="el-icon-download" @click="handleExport" v-hasPermi="['jst:event:event_partner:export']">导出</el-button>
      </el-col>
    </el-row>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="partnerList.length">
        <div v-for="row in partnerList" :key="row.partnerId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.partnerName || '--' }}</div>
              <div class="mobile-sub">赛事方ID：{{ row.partnerId }}</div>
            </div>
            <JstStatusBadge :status="String(row.auditStatus || '')" :status-map="auditStatusMap" />
          </div>
          <div class="mobile-info-row">联系人：{{ row.contactName || '--' }} / {{ row.contactMobile || '--' }}</div>
          <div class="mobile-info-row">营业执照：{{ row.businessLicenseNo || '--' }}</div>
          <div class="mobile-info-row">
            账号状态：
            <JstStatusBadge :status="String(row.accountStatus)" :status-map="accountStatusMap" />
          </div>
          <div class="mobile-info-row">
            合作状态：
            <JstStatusBadge :status="String(row.coopStatus || '')" :status-map="coopStatusMap" />
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
            <el-button type="text" v-hasPermi="['jst:event:event_partner:edit']" @click="handleUpdate(row)">编辑</el-button>
            <el-button type="text" class="danger-text" v-hasPermi="['jst:event:event_partner:remove']" @click="handleDelete(row)">删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无赛事方数据" :image-size="96" />
    </div>

    <el-table v-else v-loading="loading" :data="partnerList">
      <template slot="empty">
        <el-empty description="暂无赛事方数据" :image-size="96" />
      </template>
      <el-table-column label="赛事方ID" prop="partnerId" min-width="90" />
      <el-table-column label="赛事方名称" prop="partnerName" min-width="180" show-overflow-tooltip />
      <el-table-column label="联系人" min-width="160">
        <template slot-scope="scope">
          {{ scope.row.contactName || '--' }} / {{ scope.row.contactMobile || '--' }}
        </template>
      </el-table-column>
      <el-table-column label="审核状态" min-width="120">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.auditStatus || '')" :status-map="auditStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="账号状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.accountStatus)" :status-map="accountStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="合作状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.coopStatus || '')" :status-map="coopStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="审核时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.auditTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="210" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
          <el-button type="text" v-hasPermi="['jst:event:event_partner:edit']" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button type="text" class="danger-text" v-hasPermi="['jst:event:event_partner:remove']" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-drawer
      title="赛事方详情"
      :visible.sync="detailVisible"
      :size="isMobile ? '100%' : '52%'"
      append-to-body
    >
      <div v-loading="detailLoading" class="drawer-body">
        <el-descriptions v-if="detailData" :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="赛事方ID">{{ detailData.partnerId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="关联用户ID">{{ detailData.userId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="赛事方名称">{{ detailData.partnerName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ detailData.contactName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detailData.contactMobile || '--' }}</el-descriptions-item>
          <el-descriptions-item label="营业执照号">{{ detailData.businessLicenseNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <JstStatusBadge :status="String(detailData.auditStatus || '')" :status-map="auditStatusMap" />
          </el-descriptions-item>
          <el-descriptions-item label="审核通过时间">{{ parseTime(detailData.auditTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="账号状态">
            <JstStatusBadge :status="String(detailData.accountStatus)" :status-map="accountStatusMap" />
          </el-descriptions-item>
          <el-descriptions-item label="合作状态">
            <JstStatusBadge :status="String(detailData.coopStatus || '')" :status-map="coopStatusMap" />
          </el-descriptions-item>
          <el-descriptions-item label="审核备注" :span="isMobile ? 1 : 2">{{ detailData.auditRemark || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="isMobile ? 1 : 2">{{ detailData.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>

    <el-dialog :title="title" :visible.sync="open" :width="isMobile ? '100%' : '640px'" :fullscreen="isMobile" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" :label-width="isMobile ? '92px' : '104px'">
        <div class="form-section">
          <div class="form-section__title">机构信息</div>
          <el-form-item label="赛事方名称" prop="partnerName">
            <el-input v-model="form.partnerName" placeholder="请输入赛事方名称" />
          </el-form-item>
          <el-form-item label="营业执照号" prop="businessLicenseNo">
            <el-input v-model="form.businessLicenseNo" placeholder="请输入营业执照号" />
          </el-form-item>
        </div>

        <el-divider />

        <div class="form-section">
          <div class="form-section__title">联系方式</div>
          <el-row :gutter="12">
            <el-col :xs="24" :sm="12">
              <el-form-item label="联系人姓名" prop="contactName">
                <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="联系电话" prop="contactMobile">
                <el-input v-model="form.contactMobile" placeholder="请输入联系电话" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <el-divider />

        <div class="form-section">
          <div class="form-section__title">审核信息</div>
          <el-row :gutter="12">
            <el-col :xs="24" :sm="12">
              <el-form-item label="审核状态" prop="auditStatus">
                <el-select v-model="form.auditStatus" placeholder="请选择" class="full-width">
                  <el-option v-for="item in auditStatusOptions" :key="'f-a-' + item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="审核时间" prop="auditTime">
                <el-date-picker
                  v-model="form.auditTime"
                  type="date"
                  value-format="yyyy-MM-dd"
                  placeholder="请选择审核通过时间"
                  class="full-width"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="审核备注" prop="auditRemark">
            <el-input v-model="form.auditRemark" type="textarea" :rows="3" maxlength="255" show-word-limit placeholder="请输入审核备注" />
          </el-form-item>
        </div>

        <el-divider />

        <div class="form-section">
          <div class="form-section__title">运营状态</div>
          <el-row :gutter="12">
            <el-col :xs="24" :sm="12">
              <el-form-item label="账号状态" prop="accountStatus">
                <el-select v-model="form.accountStatus" placeholder="请选择" class="full-width">
                  <el-option v-for="item in accountStatusOptions" :key="'f-as-' + item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="合作状态" prop="coopStatus">
                <el-select v-model="form.coopStatus" placeholder="请选择" class="full-width">
                  <el-option v-for="item in coopStatusOptions" :key="'f-cs-' + item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" :rows="3" maxlength="255" show-word-limit placeholder="请输入备注" />
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addJst_event_partner,
  delJst_event_partner,
  getJst_event_partner,
  listJst_event_partner,
  updateJst_event_partner
} from '@/api/jst/event/jst_event_partner'

export default {
  name: 'JstEventPartner',
  data() {
    return {
      loading: false,
      total: 0,
      rawList: [],
      contestKeyword: '',
      detailVisible: false,
      detailLoading: false,
      detailData: null,
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        partnerName: undefined,
        contactName: undefined,
        auditStatus: undefined,
        accountStatus: undefined,
        coopStatus: undefined
      },
      form: {},
      rules: {
        partnerName: [{ required: true, message: '赛事方名称不能为空', trigger: 'blur' }],
        contactName: [{ required: true, message: '联系人姓名不能为空', trigger: 'blur' }],
        contactMobile: [{ required: true, message: '联系电话不能为空', trigger: 'blur' }],
        auditStatus: [{ required: true, message: '审核状态不能为空', trigger: 'change' }],
        accountStatus: [{ required: true, message: '账号状态不能为空', trigger: 'change' }],
        coopStatus: [{ required: true, message: '合作状态不能为空', trigger: 'change' }]
      },
      auditStatusOptions: [
        { label: '草稿', value: 'draft' },
        { label: '待审核', value: 'pending' },
        { label: '审核通过', value: 'approved' },
        { label: '已驳回', value: 'rejected' },
        { label: '补充材料', value: 'supplement' }
      ],
      accountStatusOptions: [
        { label: '停用', value: 0 },
        { label: '启用', value: 1 }
      ],
      coopStatusOptions: [
        { label: '合作中', value: 'active' },
        { label: '已到期', value: 'expired' },
        { label: '已终止', value: 'terminated' }
      ],
      auditStatusMap: {
        draft: { label: '草稿', type: 'info' },
        pending: { label: '待审核', type: 'warning' },
        approved: { label: '审核通过', type: 'success' },
        rejected: { label: '已驳回', type: 'danger' },
        supplement: { label: '补充材料', type: 'warning' }
      },
      accountStatusMap: {
        0: { label: '停用', type: 'info' },
        1: { label: '启用', type: 'success' }
      },
      coopStatusMap: {
        active: { label: '合作中', type: 'success' },
        expired: { label: '已到期', type: 'warning' },
        terminated: { label: '已终止', type: 'danger' }
      }
    }
  },
  computed: {
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    },
    partnerList() {
      const keyword = String(this.contestKeyword || '').toLowerCase()
      if (!keyword) return this.rawList
      return (Array.isArray(this.rawList) ? this.rawList : []).filter(row => this.containsText(row.contestName, keyword))
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.loading = true
      try {
        const res = await listJst_event_partner({ ...this.queryParams })
        this.rawList = Array.isArray(res.rows) ? res.rows : []
        this.total = Number(res.total || 0)
      } finally {
        this.loading = false
      }
    },
    reset() {
      this.form = {
        partnerId: undefined,
        partnerName: undefined,
        contactName: undefined,
        contactMobile: undefined,
        businessLicenseNo: undefined,
        auditStatus: undefined,
        auditTime: undefined,
        auditRemark: undefined,
        accountStatus: undefined,
        coopStatus: undefined,
        remark: undefined
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.contestKeyword = ''
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加赛事方档案'
    },
    handleUpdate(row) {
      const partnerId = row && row.partnerId
      if (!partnerId) return
      this.reset()
      getJst_event_partner(partnerId).then(res => {
        this.form = { ...this.form, ...(res.data || {}) }
        this.open = true
        this.title = '编辑赛事方档案'
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const api = this.form.partnerId ? updateJst_event_partner : addJst_event_partner
        api(this.form).then(() => {
          this.$modal.msgSuccess(this.form.partnerId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const partnerId = row && row.partnerId
      if (!partnerId) return
      this.$modal.confirm('确认删除赛事方档案「' + partnerId + '」吗？').then(() => {
        return delJst_event_partner(partnerId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleExport() {
      this.download('jst/event/jst_event_partner/export', { ...this.queryParams }, `jst_event_partner_${Date.now()}.xlsx`)
    },
    openDetail(row) {
      const partnerId = row && row.partnerId
      this.detailData = row || null
      this.detailVisible = true
      if (!partnerId) return
      this.detailLoading = true
      getJst_event_partner(partnerId).then(res => {
        this.detailData = res.data || row
      }).finally(() => {
        this.detailLoading = false
      })
    },
    containsText(value, keyword) {
      return String(value === undefined || value === null ? '' : value).toLowerCase().indexOf(keyword) > -1
    }
  }
}
</script>

<style scoped lang="scss">
.jst-event-partner-page {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.page-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
  margin-bottom: 18px;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.hero-eyebrow {
  margin: 0 0 8px;
  color: #2f6fec;
  font-size: 13px;
  font-weight: 600;
}

.page-hero h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #172033;
}

.hero-desc {
  margin: 8px 0 0;
  color: #6f7b8f;
}

.query-panel {
  padding: 16px 16px 0;
  margin-bottom: 16px;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.action-row .el-button {
  width: 100%;
}

.mobile-list {
  min-height: 180px;
}

.mobile-card {
  padding: 16px;
  margin-bottom: 12px;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.mobile-card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.mobile-title {
  font-weight: 700;
  color: #172033;
}

.mobile-sub {
  margin-top: 4px;
  font-size: 12px;
  color: #7a8495;
}

.mobile-info-row {
  margin-top: 8px;
  color: #6f7b8f;
  font-size: 13px;
}

.mobile-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 12px;
}

.danger-text {
  color: #f56c6c;
}

.drawer-body {
  padding: 8px 16px 16px;
}

.form-section {
  padding: 0 4px;
}

.form-section + .form-section {
  margin-top: 4px;
}

.form-section__title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 4px 0 14px;
  padding-left: 10px;
  border-left: 3px solid #409eff;
  line-height: 1.2;
}

.full-width {
  width: 100%;
}

@media (max-width: 768px) {
  .jst-event-partner-page {
    padding: 12px;
  }

  .page-hero {
    display: block;
    padding: 18px;
  }

  .page-hero .el-button {
    width: 100%;
    min-height: 44px;
    margin-top: 16px;
  }

  .page-hero h2 {
    font-size: 20px;
  }

  .query-panel {
    padding-bottom: 8px;
  }

  .query-panel ::v-deep .el-form-item {
    display: block;
    margin-right: 0;
  }

  .query-panel ::v-deep .el-form-item__content,
  .query-panel ::v-deep .el-select,
  .query-panel ::v-deep .el-input {
    width: 100%;
  }

  .mobile-actions .el-button {
    min-height: 44px;
  }

  .form-section__title {
    font-size: 13px;
    margin: 2px 0 10px;
  }
}
</style>
