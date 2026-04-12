<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="权益名称" prop="rightsName">
        <el-input v-model="queryParams.rightsName" placeholder="请输入权益名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="rightsType">
        <el-select v-model="queryParams.rightsType" placeholder="全部" clearable>
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
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['jst:marketing:rights_template:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.rightsTemplateId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.rightsName }}</span>
            <el-tag size="mini" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>{{ typeLabel(row.rightsType) }}</span>
            <span>{{ quotaModeLabel(row.quotaMode) }}：{{ row.quotaValue }}</span>
            <span>{{ writeoffLabel(row.writeoffMode) }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:marketing:rights_template:edit']">编辑</el-button>
            <el-button type="text" size="mini" @click="handleToggle(row)">{{ row.status === 1 ? '停用' : '启用' }}</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无权益模板" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="rightsTemplateId" width="70" />
      <el-table-column label="权益名称" prop="rightsName" min-width="160" show-overflow-tooltip />
      <el-table-column label="类型" width="120">
        <template slot-scope="{ row }">{{ typeLabel(row.rightsType) }}</template>
      </el-table-column>
      <el-table-column label="口径" width="80">
        <template slot-scope="{ row }">{{ quotaModeLabel(row.quotaMode) }}</template>
      </el-table-column>
      <el-table-column label="数值" prop="quotaValue" width="80" />
      <el-table-column label="核销方式" width="120">
        <template slot-scope="{ row }">{{ writeoffLabel(row.writeoffMode) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="150">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:marketing:rights_template:edit']">编辑</el-button>
          <el-button type="text" size="mini" @click="handleToggle(row)">{{ row.status === 1 ? '停用' : '启用' }}</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" :width="isMobile ? '100%' : '580px'" :fullscreen="isMobile" append-to-body>
      <el-form ref="form" :model="form" :rules="formRules" :label-width="isMobile ? '80px' : '100px'">
        <el-form-item label="权益名称" prop="rightsName">
          <el-input v-model="form.rightsName" placeholder="请输入权益名称" />
        </el-form-item>
        <el-form-item label="类型" prop="rightsType">
          <el-select v-model="form.rightsType" placeholder="请选择">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="口径" prop="quotaMode">
              <el-select v-model="form.quotaMode" placeholder="请选择">
                <el-option label="次数" value="times" />
                <el-option label="金额" value="amount" />
                <el-option label="周期" value="period" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="数值" prop="quotaValue">
              <el-input-number v-model="form.quotaValue" :min="0" :precision="2" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="有效天数">
          <el-input-number v-model="form.validDays" :min="0" controls-position="right" style="width:100%" />
        </el-form-item>
        <el-form-item label="核销方式" prop="writeoffMode">
          <el-select v-model="form.writeoffMode" placeholder="请选择">
            <el-option label="线上自动" value="online_auto" />
            <el-option label="人工审核" value="manual_review" />
            <el-option label="线下确认" value="offline_confirm" />
          </el-select>
        </el-form-item>
        <el-form-item label="适用角色">
          <el-select v-model="form.applicableRole" placeholder="请选择">
            <el-option label="学生" value="student" />
            <el-option label="渠道" value="channel" />
            <el-option label="全部" value="both" />
          </el-select>
        </el-form-item>
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
import { listRightsTemplate, getRightsTemplate, addRightsTemplate, updateRightsTemplate } from '@/api/jst/rights'

const TYPE_OPTIONS = [
  { label: '报名抵扣', value: 'enroll_deduct' },
  { label: '场馆优惠', value: 'venue_reduce' },
  { label: '专属课程', value: 'exclusive_course' },
  { label: '客服优先', value: 'cs_priority' },
  { label: '自定义', value: 'custom' }
]

const QUOTA_MODE_MAP = { times: '次数', amount: '金额', period: '周期' }
const WRITEOFF_MAP = { online_auto: '线上自动', manual_review: '人工审核', offline_confirm: '线下确认' }

export default {
  name: 'JstRightsTemplate',
  data() {
    return {
      loading: false,
      submitLoading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, rightsName: null, rightsType: null, status: null },
      typeOptions: TYPE_OPTIONS,
      dialogVisible: false,
      dialogTitle: '',
      form: {},
      formRules: {
        rightsName: [{ required: true, message: '权益名称不能为空', trigger: 'blur' }],
        rightsType: [{ required: true, message: '请选择类型', trigger: 'change' }],
        quotaMode: [{ required: true, message: '请选择口径', trigger: 'change' }],
        quotaValue: [{ required: true, message: '数值不能为空', trigger: 'blur' }],
        writeoffMode: [{ required: true, message: '请选择核销方式', trigger: 'change' }]
      }
    }
  },
  computed: {
    isMobile() { return this.$store.state.app.device === 'mobile' }
  },
  created() { this.getList() },
  methods: {
    typeLabel(t) { const o = TYPE_OPTIONS.find(x => x.value === t); return o ? o.label : t || '--' },
    quotaModeLabel(m) { return QUOTA_MODE_MAP[m] || m || '--' },
    writeoffLabel(w) { return WRITEOFF_MAP[w] || w || '--' },
    getList() {
      this.loading = true
      listRightsTemplate(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.$refs.queryForm && this.$refs.queryForm.resetFields(); this.handleQuery() },
    initForm() {
      return { rightsName: '', rightsType: 'enroll_deduct', quotaMode: 'times', quotaValue: 1, validDays: 365, writeoffMode: 'online_auto', applicableRole: 'student', status: 1 }
    },
    handleAdd() {
      this.form = this.initForm()
      this.dialogTitle = '新增权益模板'
      this.dialogVisible = true
      this.$nextTick(() => { this.$refs.form && this.$refs.form.clearValidate() })
    },
    handleEdit(row) {
      getRightsTemplate(row.rightsTemplateId).then(res => {
        this.form = { ...(res.data || res) }
        this.dialogTitle = '编辑权益模板'
        this.dialogVisible = true
      })
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const api = this.form.rightsTemplateId ? updateRightsTemplate : addRightsTemplate
        api(this.form).then(() => {
          this.$modal.msgSuccess(this.form.rightsTemplateId ? '修改成功' : '新增成功')
          this.dialogVisible = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    handleToggle(row) {
      const newStatus = row.status === 1 ? 0 : 1
      updateRightsTemplate({ ...row, status: newStatus }).then(() => {
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
