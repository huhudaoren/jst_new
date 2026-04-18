<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="销售姓名" prop="salesName">
        <el-input v-model="queryParams.salesName" placeholder="请输入销售姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="在职" value="active" />
          <el-option label="离职申请中" value="resign_pending" />
          <el-option label="过渡期" value="transitioning" />
          <el-option label="已离职" value="resigned" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" v-hasPermi="['jst:sales:add']" @click="handleCreate">新建</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-s-data" size="mini" @click="$router.push('/jst/sales/dashboard')">业绩看板</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-warning-outline" size="mini" @click="$router.push('/jst/sales/conflict')">归属冲突</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="销售编号" prop="salesNo" width="130" />
      <el-table-column label="姓名" prop="salesName" min-width="100" show-overflow-tooltip />
      <el-table-column label="手机号" prop="phone" width="130" align="center" />
      <el-table-column label="直属主管" prop="managerName" width="110" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.managerName || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="110" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="statusTagType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="默认费率" prop="commissionRateDefault" width="100" align="right">
        <template slot-scope="scope">
          {{ scope.row.commissionRateDefault != null ? (scope.row.commissionRateDefault * 100).toFixed(2) + '%' : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="是否主管" prop="isManager" width="90" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.isManager ? 'success' : 'info'">{{ scope.row.isManager ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" v-hasPermi="['jst:sales:edit:rate']" @click="handleUpdateRate(scope.row)">改费率</el-button>
          <el-button size="mini" type="text" icon="el-icon-user" v-hasPermi="['jst:sales:edit:manager']" @click="handleUpdateManager(scope.row)">设主管</el-button>
          <el-button size="mini" type="text" icon="el-icon-document" @click="toSettlement(scope.row)">月结单</el-button>
          <el-dropdown size="mini" trigger="click" @command="cmd => handleCommand(cmd, scope.row)" style="margin-left:8px;">
            <el-button size="mini" type="text" icon="el-icon-more">更多</el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="resign-apply" v-if="scope.row.status === 'active'" icon="el-icon-remove-outline">申请离职</el-dropdown-item>
              <el-dropdown-item command="resign-execute" v-if="scope.row.status === 'resign_pending'" icon="el-icon-circle-close">立即执行离职</el-dropdown-item>
              <el-dropdown-item command="transition-end" v-if="scope.row.status === 'transitioning'" icon="el-icon-switch-button">终结过渡期</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新建销售 dialog -->
    <el-dialog title="新建销售档案" :visible.sync="createOpen" width="600px" append-to-body>
      <el-form :model="createForm" ref="createForm" :rules="createRules" label-width="110px">
        <div class="form-section">
          <div class="form-section__title">基本信息</div>
          <el-form-item label="关联系统用户" prop="sysUserId">
            <user-picker v-model="createForm.sysUserId" placeholder="搜索用户名或昵称" style="width:100%" />
          </el-form-item>
          <el-form-item label="销售姓名" prop="salesName">
            <el-input v-model="createForm.salesName" placeholder="请输入销售姓名" maxlength="64" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="createForm.phone" placeholder="请输入 11 位手机号" maxlength="11" />
          </el-form-item>
        </div>
        <el-divider />
        <div class="form-section">
          <div class="form-section__title">归属与费率</div>
          <el-form-item label="直属主管">
            <el-select v-model="createForm.managerId" placeholder="请选择直属主管（可选）" clearable style="width:100%">
              <el-option v-for="m in managerOptions" :key="m.salesId" :label="m.salesName" :value="m.salesId" />
            </el-select>
          </el-form-item>
          <el-form-item label="默认提成费率" prop="commissionRateDefault">
            <el-input-number v-model="createForm.commissionRateDefault" :min="0" :max="1" :step="0.001" :precision="4" style="width:100%" placeholder="如 0.05 表示 5%" />
          </el-form-item>
          <el-form-item label="是否主管">
            <el-checkbox v-model="createForm.asManager">设为主管</el-checkbox>
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer">
        <el-button @click="createOpen = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitCreate">确认创建</el-button>
      </div>
    </el-dialog>

    <!-- 修改费率 dialog -->
    <el-dialog title="修改默认费率" :visible.sync="rateOpen" width="400px" append-to-body>
      <el-form :model="rateForm" ref="rateForm" label-width="100px">
        <el-form-item label="新费率">
          <el-input-number v-model="rateForm.rate" :min="0" :max="1" :step="0.001" :precision="4" style="width:100%" />
          <div class="el-form-item__tip">输入小数，如 0.05 代表 5%</div>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="rateOpen = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitRate">确认</el-button>
      </div>
    </el-dialog>

    <!-- 设置主管 dialog -->
    <el-dialog title="设置/更换直属主管" :visible.sync="managerOpen" width="400px" append-to-body>
      <el-form :model="managerForm" ref="managerForm" label-width="100px">
        <el-form-item label="直属主管">
          <el-select v-model="managerForm.managerId" placeholder="请选择主管（清空则取消）" clearable style="width:100%">
            <el-option v-for="m in managerOptions" :key="m.salesId" :label="m.salesName" :value="m.salesId" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="managerOpen = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitManager">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSales, createSales, updateRate, updateManager, resignApply, resignExecute, transitionEnd } from '@/api/admin/sales/index'

export default {
  name: 'SalesList',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        salesName: undefined,
        phone: undefined,
        status: undefined
      },
      // 新建
      createOpen: false,
      createForm: {
        sysUserId: undefined,
        salesName: '',
        phone: '',
        managerId: undefined,
        commissionRateDefault: 0.05,
        asManager: false
      },
      createRules: {
        sysUserId: [{ required: true, message: '请选择关联系统用户', trigger: 'change' }],
        salesName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        commissionRateDefault: [{ required: true, message: '请输入费率', trigger: 'blur' }]
      },
      // 改费率
      rateOpen: false,
      rateForm: { salesId: null, rate: 0.05 },
      // 设主管
      managerOpen: false,
      managerForm: { salesId: null, managerId: undefined },
      // 主管选项（是主管的销售）
      managerOptions: [],
      submitting: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listSales(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
        this.managerOptions = (res.rows || []).filter(s => s.isManager)
      }).catch(() => {}).finally(() => { this.loading = false })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    statusTagType(s) {
      const m = { active: 'success', resign_pending: 'warning', transitioning: 'info', resigned: 'danger' }
      return m[s] || 'info'
    },
    statusLabel(s) {
      const m = { active: '在职', resign_pending: '离职申请中', transitioning: '过渡期', resigned: '已离职' }
      return m[s] || s || '-'
    },
    handleCreate() {
      this.createForm = {
        sysUserId: undefined, salesName: '', phone: '',
        managerId: undefined, commissionRateDefault: 0.05, asManager: false
      }
      this.createOpen = true
      this.$nextTick(() => this.$refs.createForm && this.$refs.createForm.clearValidate())
    },
    submitCreate() {
      this.$refs.createForm.validate(valid => {
        if (!valid) return
        this.submitting = true
        createSales(this.createForm).then(() => {
          this.$modal.msgSuccess('创建成功')
          this.createOpen = false
          this.getList()
        }).catch(() => {}).finally(() => { this.submitting = false })
      })
    },
    handleUpdateRate(row) {
      this.rateForm = { salesId: row.salesId, rate: row.commissionRateDefault || 0.05 }
      this.rateOpen = true
    },
    submitRate() {
      this.submitting = true
      updateRate(this.rateForm.salesId, this.rateForm.rate).then(() => {
        this.$modal.msgSuccess('费率更新成功')
        this.rateOpen = false
        this.getList()
      }).catch(() => {}).finally(() => { this.submitting = false })
    },
    handleUpdateManager(row) {
      this.managerForm = { salesId: row.salesId, managerId: row.managerId || undefined }
      this.managerOpen = true
    },
    submitManager() {
      this.submitting = true
      updateManager(this.managerForm.salesId, this.managerForm.managerId || null).then(() => {
        this.$modal.msgSuccess('主管设置成功')
        this.managerOpen = false
        this.getList()
      }).catch(() => {}).finally(() => { this.submitting = false })
    },
    handleCommand(cmd, row) {
      if (cmd === 'resign-apply') {
        this.$prompt('请输入预期离职日期（如 2026-05-31）', '申请离职', {
          inputPlaceholder: 'YYYY-MM-DD',
          confirmButtonText: '提交',
          cancelButtonText: '取消'
        }).then(({ value }) => {
          if (!value) return
          resignApply(row.salesId, value).then(() => {
            this.$modal.msgSuccess('离职申请已提交')
            this.getList()
          }).catch(() => {})
        }).catch(() => {})
      } else if (cmd === 'resign-execute') {
        this.$confirm('确认立即执行「' + row.salesName + '」的离职？操作不可撤销。', '确认离职', { type: 'warning' }).then(() => {
          return resignExecute(row.salesId)
        }).then(() => {
          this.$modal.msgSuccess('已执行离职')
          this.getList()
        }).catch(() => {})
      } else if (cmd === 'transition-end') {
        this.$confirm('确认终结「' + row.salesName + '」的过渡期？', '终结过渡期', { type: 'warning' }).then(() => {
          return transitionEnd(row.salesId)
        }).then(() => {
          this.$modal.msgSuccess('过渡期已终结')
          this.getList()
        }).catch(() => {})
      }
    },
    toSettlement(row) {
      this.$router.push({ path: '/jst/sales/settlement', query: { salesId: row.salesId, salesName: row.salesName } })
    }
  }
}
</script>

<style scoped>
.form-section { margin-bottom: 8px; }
.form-section__title {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  padding: 4px 0 12px;
  border-left: 3px solid #409EFF;
  padding-left: 8px;
  margin-bottom: 12px;
}
.el-form-item__tip { font-size: 12px; color: #909399; margin-top: 4px; }
@media (max-width: 768px) {
  .el-form--inline .el-form-item { display: block; margin-right: 0; }
}
</style>
