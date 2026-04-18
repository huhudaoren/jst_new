<template>
  <div class="app-container">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">销售主管</p>
        <h2>团队管理</h2>
        <p class="hero-desc">查看团队成员本月业绩，派发跟进任务。</p>
      </div>
      <el-button type="primary" icon="el-icon-plus" @click="openAssignTask">派发任务</el-button>
    </div>

    <!-- 月份选择 -->
    <el-row :gutter="12" style="margin-bottom:16px">
      <el-col :span="6" :xs="12">
        <el-date-picker
          v-model="selectedMonth"
          type="month"
          value-format="yyyy-MM"
          placeholder="选择月份"
          style="width:100%"
          @change="loadTeam"
        />
      </el-col>
    </el-row>

    <!-- 团队成员表 -->
    <el-card shadow="never" style="margin-bottom:16px">
      <div slot="header">团队成员业绩</div>
      <div v-loading="teamLoading">
        <el-table :data="teamList" border stripe size="small">
          <el-table-column label="姓名" prop="salesName" min-width="90" show-overflow-tooltip />
          <el-table-column label="销售编号" prop="salesNo" width="110" align="center" />
          <el-table-column label="状态" prop="status" width="90" align="center">
            <template slot-scope="scope">
              <el-tag size="mini" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="本月订单" prop="monthOrderCount" width="90" align="center" />
          <el-table-column label="本月 GMV" min-width="110" align="right">
            <template slot-scope="scope">¥{{ formatAmount(scope.row.monthGmv) }}</template>
          </el-table-column>
          <el-table-column label="本月提成" min-width="110" align="right">
            <template slot-scope="scope">¥{{ formatAmount(scope.row.monthCommission) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" type="text" @click="assignToSales(scope.row)">派任务</el-button>
            </template>
          </el-table-column>
        </el-table>
        <empty-state-cta v-if="!teamLoading && teamList.length === 0" title="还没有下属销售" description="联系管理员为你分配下属" :image-size="60" />
      </div>
    </el-card>

    <!-- 已派任务 -->
    <el-card shadow="never">
      <div slot="header">已派任务</div>
      <div v-loading="tasksLoading">
        <el-table :data="assignedTasks" border stripe size="small">
          <el-table-column label="任务标题" prop="title" min-width="160" show-overflow-tooltip />
          <el-table-column label="被分配销售" width="160">
            <template slot-scope="scope">
              <entity-link entity="sales" :id="scope.row.assigneeSalesId" :name="scope.row.assigneeSalesName" />
            </template>
          </el-table-column>
          <el-table-column label="截止日期" prop="dueDate" width="120" align="center">
            <template slot-scope="scope">{{ formatDate(scope.row.dueDate) }}</template>
          </el-table-column>
          <el-table-column label="状态" prop="status" width="100" align="center">
            <template slot-scope="scope">
              <el-tag size="mini" :type="taskStatusType(scope.row.status)">{{ taskStatusLabel(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
        <empty-state-cta v-if="!tasksLoading && assignedTasks.length === 0" title="无待办任务" description="放松心情，业绩稳如泰山" :image-size="60" />
      </div>
    </el-card>

    <!-- 派发任务弹窗 -->
    <el-dialog title="派发跟进任务" :visible.sync="assignVisible" width="480px" @closed="resetAssignForm">
      <el-form ref="assignForm" :model="assignForm" :rules="assignRules" label-width="100px" size="small">
        <el-form-item label="销售" prop="assigneeSalesId">
          <sales-picker v-model="assignForm.assigneeSalesId" placeholder="搜索销售姓名" style="width:100%" />
        </el-form-item>
        <el-form-item label="关联渠道">
          <channel-picker v-model="assignForm.channelId" :clearable="true" placeholder="选填" style="width:100%" />
        </el-form-item>
        <el-form-item label="任务标题" prop="title">
          <el-input v-model="assignForm.title" placeholder="简短描述任务目标" maxlength="100" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input v-model="assignForm.description" type="textarea" :rows="3" maxlength="500" placeholder="详细任务说明（选填）" />
        </el-form-item>
        <el-form-item label="截止日期" prop="dueDate">
          <el-date-picker
            v-model="assignForm.dueDate"
            type="date"
            placeholder="选择截止日期"
            value-format="yyyy-MM-dd"
            style="width:100%"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button size="small" @click="assignVisible = false">取消</el-button>
        <el-button type="primary" size="small" :loading="saving" @click="handleAssign">派发</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { assignTask, listAssignedByMe } from '@/api/sales/manager/task'
import { listTeam } from '@/api/sales/manager/team'

export default {
  name: 'SalesManagerTeam',
  data() {
    return {
      selectedMonth: this.currentMonth(),
      teamLoading: false,
      teamList: [],
      tasksLoading: false,
      saving: false,
      assignedTasks: [],
      assignVisible: false,
      assignForm: {
        assigneeSalesId: null,
        channelId: null,
        title: '',
        description: '',
        dueDate: ''
      },
      assignRules: {
        assigneeSalesId: [{ required: true, message: '请选择销售', trigger: 'change' }],
        title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }],
        dueDate: [{ required: true, message: '请选择截止日期', trigger: 'change' }]
      }
    }
  },
  created() {
    this.loadTeam()
    this.loadAssignedTasks()
  },
  methods: {
    currentMonth() {
      const d = new Date()
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
    },
    loadTeam() {
      this.teamLoading = true
      listTeam(this.selectedMonth).then(res => {
        this.teamList = res.data || []
      }).catch(() => {
        this.$message.error('加载团队数据失败')
      }).finally(() => { this.teamLoading = false })
    },
    loadAssignedTasks() {
      this.tasksLoading = true
      listAssignedByMe().then(res => {
        this.assignedTasks = res.rows || []
      }).catch(() => {}).finally(() => { this.tasksLoading = false })
    },
    assignToSales(row) {
      this.assignForm.assigneeSalesId = row.salesId
      this.assignVisible = true
    },
    openAssignTask() {
      this.assignVisible = true
    },
    handleAssign() {
      this.$refs.assignForm.validate(valid => {
        if (!valid) return
        this.saving = true
        assignTask(this.assignForm).then(() => {
          this.$message.success('任务已派发')
          this.assignVisible = false
          this.loadAssignedTasks()
        }).catch(e => this.$message.error(e.msg || '派发失败')).finally(() => { this.saving = false })
      })
    },
    resetAssignForm() {
      this.assignForm = { assigneeSalesId: null, channelId: null, title: '', description: '', dueDate: '' }
      this.$refs.assignForm && this.$refs.assignForm.resetFields()
    },
    statusType(s) {
      const map = { active: 'success', resign_apply: 'warning', resigned_pending_settle: 'info', resigned_final: 'danger' }
      return map[s] || 'info'
    },
    statusLabel(s) {
      const map = { active: '在职', resign_apply: '申请离职', resigned_pending_settle: '过渡期', resigned_final: '已离职' }
      return map[s] || s
    },
    taskStatusType(s) {
      const map = { pending: 'warning', in_progress: 'primary', completed: 'success', overdue: 'danger' }
      return map[s] || 'info'
    },
    taskStatusLabel(s) {
      const map = { pending: '待办', in_progress: '进行中', completed: '已完成', overdue: '已超时' }
      return map[s] || s
    },
    formatDate(val) {
      if (!val) return '--'
      return String(val).substring(0, 10)
    },
    formatAmount(v) {
      if (v == null) return '0.00'
      return Number(v).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    }
  }
}
</script>

<style scoped>
.page-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px 24px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
  margin-bottom: 16px;
}
.hero-eyebrow { margin: 0; font-size: 12px; color: #409EFF; text-transform: uppercase; }
.page-hero h2 { margin: 4px 0; font-size: 22px; font-weight: 700; color: #172033; }
.hero-desc { margin: 4px 0 0; color: #6f7b8f; }
</style>
