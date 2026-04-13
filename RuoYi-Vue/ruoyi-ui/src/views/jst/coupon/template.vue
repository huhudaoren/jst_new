<template>
  <div class="app-container jst-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">Coupon Templates</p>
        <h2>优惠券模板</h2>
        <p class="hero-desc">配置面额、门槛和有效期，统一管理模板启停状态。</p>
      </div>
      <div class="hero-actions">
        <el-popover placement="bottom" width="280" trigger="hover">
          <div class="help-lines">
            <p>1. 优先维护模板参数，再批量发券。</p>
            <p>2. 折扣券支持单独设置折扣率。</p>
            <p>3. 抽屉内保存后自动刷新列表。</p>
          </div>
          <el-button slot="reference" icon="el-icon-question" plain>使用说明</el-button>
        </el-popover>
        <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
      </div>
    </div>

    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="券名称" prop="couponName">
        <el-input v-model="queryParams.couponName" placeholder="请输入券名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="券类型" prop="couponType">
        <el-select v-model="queryParams.couponType" placeholder="全部" clearable>
          <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8 toolbar-row">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['jst:marketing:coupon_template:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.couponTemplateId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.couponName }}</span>
            <el-tag size="mini" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>{{ typeLabel(row.couponType) }}</span>
            <span>面额 ¥{{ row.faceValue || 0 }}</span>
            <span>门槛 ¥{{ row.thresholdAmount || 0 }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:marketing:coupon_template:edit']">编辑</el-button>
            <el-button type="text" size="mini" @click="handleToggle(row)">{{ row.status === 1 ? '停用' : '启用' }}</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无券模板" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="couponTemplateId" width="70" />
      <el-table-column label="券名称" prop="couponName" min-width="160" show-overflow-tooltip />
      <el-table-column label="类型" width="110">
        <template slot-scope="{ row }">{{ typeLabel(row.couponType) }}</template>
      </el-table-column>
      <el-table-column label="面额(元)" prop="faceValue" width="90" />
      <el-table-column label="门槛(元)" prop="thresholdAmount" width="90" />
      <el-table-column label="有效天数" prop="validDays" width="90" />
      <el-table-column label="状态" width="80">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="160">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:marketing:coupon_template:edit']">编辑</el-button>
          <el-button type="text" size="mini" @click="handleToggle(row)">{{ row.status === 1 ? '停用' : '启用' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-drawer
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :size="isMobile ? '100%' : '55%'"
      append-to-body
      :with-header="true"
    >
      <div class="drawer-body">
        <el-form ref="form" :model="form" :rules="formRules" :label-width="isMobile ? '88px' : '100px'">
          <el-form-item label="券名称" prop="couponName">
            <el-input v-model="form.couponName" placeholder="请输入券名称" />
          </el-form-item>
          <el-form-item label="券类型" prop="couponType">
            <el-select v-model="form.couponType" placeholder="请选择" class="full-width">
              <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-row :gutter="12">
            <el-col :xs="24" :sm="12">
              <el-form-item label="面额" prop="faceValue">
                <el-input-number v-model="form.faceValue" :min="0" :precision="2" controls-position="right" class="full-width" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" v-if="form.couponType === 'discount'">
              <el-form-item label="折扣率">
                <el-input-number v-model="form.discountRate" :min="0" :max="1" :precision="2" :step="0.05" controls-position="right" class="full-width" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="门槛金额">
            <el-input-number v-model="form.thresholdAmount" :min="0" :precision="2" controls-position="right" class="full-width" />
          </el-form-item>
          <el-form-item label="适用角色">
            <el-select v-model="form.applicableRole" placeholder="请选择" class="full-width">
              <el-option label="全部" value="all" />
              <el-option label="学生" value="student" />
              <el-option label="渠道" value="channel" />
            </el-select>
          </el-form-item>
          <el-form-item label="有效天数">
            <el-input-number v-model="form.validDays" :min="0" controls-position="right" class="full-width" />
          </el-form-item>
          <el-row :gutter="12">
            <el-col :xs="24" :sm="12">
              <el-form-item label="有效开始">
                <el-date-picker v-model="form.validStart" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="可选" class="full-width" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="有效结束">
                <el-date-picker v-model="form.validEnd" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="可选" class="full-width" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="状态">
            <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </el-form>
      </div>
      <div class="drawer-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确认</el-button>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listCouponTemplate, getCouponTemplate, addCouponTemplate, updateCouponTemplate } from '@/api/jst/coupon'

const TYPE_OPTIONS = [
  { label: '满减券', value: 'full_reduce' },
  { label: '直减券', value: 'direct_reduce' },
  { label: '折扣券', value: 'discount' },
  { label: '赛事专属券', value: 'contest_specific' }
]

export default {
  name: 'JstCouponTemplate',
  data() {
    return {
      loading: false,
      submitLoading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, couponName: null, couponType: null, status: null },
      typeOptions: TYPE_OPTIONS,
      dialogVisible: false,
      dialogTitle: '',
      form: {},
      formRules: {
        couponName: [{ required: true, message: '券名称不能为空', trigger: 'blur' }],
        couponType: [{ required: true, message: '请选择券类型', trigger: 'change' }],
        faceValue: [{ required: true, message: '面额不能为空', trigger: 'blur' }]
      }
    }
  },
  computed: {
    isMobile() { return this.$store.state.app.device === 'mobile' }
  },
  created() { this.getList() },
  methods: {
    typeLabel(t) { const o = TYPE_OPTIONS.find(x => x.value === t); return o ? o.label : t || '--' },
    getList() {
      this.loading = true
      listCouponTemplate(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.$refs.queryForm && this.$refs.queryForm.resetFields(); this.handleQuery() },
    initForm() {
      return { couponName: '', couponType: 'full_reduce', faceValue: 0, discountRate: null, thresholdAmount: 0, applicableRole: 'all', validDays: 30, validStart: null, validEnd: null, status: 1 }
    },
    handleAdd() {
      this.form = this.initForm()
      this.dialogTitle = '新增券模板'
      this.dialogVisible = true
      this.$nextTick(() => { this.$refs.form && this.$refs.form.clearValidate() })
    },
    handleEdit(row) {
      getCouponTemplate(row.couponTemplateId).then(res => {
        const d = res.data || res
        this.form = { ...d }
        this.dialogTitle = '编辑券模板'
        this.dialogVisible = true
      })
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const api = this.form.couponTemplateId ? updateCouponTemplate : addCouponTemplate
        api(this.form).then(() => {
          this.$modal.msgSuccess(this.form.couponTemplateId ? '修改成功' : '新增成功')
          this.dialogVisible = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    handleToggle(row) {
      const newStatus = row.status === 1 ? 0 : 1
      updateCouponTemplate({ ...row, status: newStatus }).then(() => {
        this.$modal.msgSuccess(newStatus === 1 ? '已启用' : '已停用')
        this.getList()
      })
    }
  }
}
</script>

<style scoped>
.jst-page {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.page-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 22px 24px;
  margin-bottom: 16px;
  color: #fff;
  border-radius: 12px;
  background: linear-gradient(130deg, #0f766e 0%, #2563eb 58%, #1e293b 100%);
}

.hero-eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: .08em;
  text-transform: uppercase;
  opacity: .82;
}

.page-hero h2 {
  margin: 0;
  font-size: 24px;
}

.hero-desc {
  margin: 8px 0 0;
  color: rgba(255, 255, 255, 0.82);
}

.hero-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.help-lines p {
  margin: 0 0 6px;
  line-height: 1.6;
}

.help-lines p:last-child {
  margin-bottom: 0;
}

.query-panel {
  padding: 16px 16px 0;
  margin-bottom: 16px;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 10px;
}

.toolbar-row {
  margin-bottom: 12px;
}

.mobile-card-list {
  padding: 0 4px;
}

.mobile-card {
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 10px;
  padding: 14px;
  margin-bottom: 10px;
}

.mobile-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.mobile-card__title {
  font-weight: 600;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 68%;
}

.mobile-card__meta {
  font-size: 12px;
  color: #6b7280;
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.mobile-card__actions {
  display: flex;
  gap: 8px;
}

.drawer-body {
  padding: 8px 20px 90px;
}

.drawer-footer {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 20px;
  border-top: 1px solid #e5eaf2;
  background: #fff;
}

.full-width {
  width: 100%;
}

@media (max-width: 768px) {
  .jst-page {
    padding: 12px;
  }

  .page-hero {
    flex-direction: column;
    align-items: flex-start;
    padding: 18px;
  }

  .page-hero h2 {
    font-size: 20px;
  }

  .hero-actions {
    width: 100%;
  }

  .hero-actions .el-button {
    flex: 1;
    min-height: 44px;
  }

  .query-panel {
    padding-bottom: 10px;
  }

  .query-panel ::v-deep .el-form-item {
    display: block;
    margin-right: 0;
  }

  .query-panel ::v-deep .el-form-item__content,
  .query-panel ::v-deep .el-input,
  .query-panel ::v-deep .el-select,
  .query-panel ::v-deep .el-date-editor {
    width: 100%;
  }

  .query-panel .el-button,
  .mobile-card__actions .el-button {
    min-height: 44px;
  }

  .drawer-body {
    padding: 8px 14px 96px;
  }

  .drawer-footer {
    padding: 10px 14px;
  }

  .drawer-footer .el-button {
    min-height: 44px;
  }
}
</style>
