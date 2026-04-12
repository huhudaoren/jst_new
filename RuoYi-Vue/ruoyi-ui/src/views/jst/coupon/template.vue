<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
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
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
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

    <!-- 编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" :width="isMobile ? '100%' : '620px'" :fullscreen="isMobile" append-to-body>
      <el-form ref="form" :model="form" :rules="formRules" :label-width="isMobile ? '80px' : '100px'">
        <el-form-item label="券名称" prop="couponName">
          <el-input v-model="form.couponName" placeholder="请输入券名称" />
        </el-form-item>
        <el-form-item label="券类型" prop="couponType">
          <el-select v-model="form.couponType" placeholder="请选择">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="面额" prop="faceValue">
              <el-input-number v-model="form.faceValue" :min="0" :precision="2" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" v-if="form.couponType === 'discount'">
            <el-form-item label="折扣率">
              <el-input-number v-model="form.discountRate" :min="0" :max="1" :precision="2" :step="0.05" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="门槛金额">
          <el-input-number v-model="form.thresholdAmount" :min="0" :precision="2" controls-position="right" style="width:100%" />
        </el-form-item>
        <el-form-item label="适用角色">
          <el-select v-model="form.applicableRole" placeholder="请选择">
            <el-option label="全部" value="all" />
            <el-option label="学生" value="student" />
            <el-option label="渠道" value="channel" />
          </el-select>
        </el-form-item>
        <el-form-item label="有效天数">
          <el-input-number v-model="form.validDays" :min="0" controls-position="right" style="width:100%" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="有效开始">
              <el-date-picker v-model="form.validStart" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="可选" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="有效结束">
              <el-date-picker v-model="form.validEnd" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="可选" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>
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
.mobile-card-list { padding: 0 4px; }
.mobile-card { background: #fff; border-radius: 8px; padding: 12px 14px; margin-bottom: 10px; box-shadow: 0 1px 4px rgba(0,0,0,.06); }
.mobile-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.mobile-card__title { font-weight: 600; font-size: 14px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 65%; }
.mobile-card__meta { font-size: 12px; color: #909399; display: flex; gap: 12px; margin-bottom: 8px; flex-wrap: wrap; }
.mobile-card__actions { display: flex; gap: 6px; }
</style>
