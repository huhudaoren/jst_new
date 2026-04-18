<template>
  <div class="app-container">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">销售主管</p>
        <h2>团队管理</h2>
        <p class="hero-desc">查看下属销售业绩，派发跟进任务。完整团队看板将在 plan-04 补充。</p>
      </div>
      <el-button type="primary" icon="el-icon-plus" @click="openAssignTask">派发任务</el-button>
    </div>

    <el-alert
      type="info"
      title="团队业绩汇总表和下属管理功能将在 plan-04 完善（Admin 销售管理 API 补齐后接入）。"
      :closable="false"
      style="margin-bottom:16px"
    />

    <!-- 派发任务快捷入口 -->
    <el-card shadow="never">
      <div slot="header">已派任务</div>
      <div v-loading="tasksLoading">
        <el-table :data="assignedTasks" border stripe size="small">
          <el-table-column label="任务标题" prop="title" min-width="160" show-overflow-tooltip />
          <el-table-column label="被分配销售ID" prop="assigneeSalesId" width="130" align="center" />
          <el-table-column label="截止日期" prop="dueDate" width="120" align="center">
            <template slot-scope="scope">{{ formatDate(scope.row.dueDate) }}</template>
          </el-table-column>
          <el-table-column label="状态" prop="status" width="100" align="center">
            <template slot-scope="scope">
              <el-tag size="mini" :type="taskStatusType(scope.row.status)">{{ taskStatusLabel(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!tasksLoading && assignedTasks.length === 0" description="暂无派发任务" :image-size="60" />
      </div>
    </el-card>

    <!-- 派发任务弹窗 -->
    <el-dialog title="派发跟进任务" :visible.sync="assignVisible" width="480px" @closed="resetAssignForm">
      <el-form ref="assignForm" :model="assignForm" :rules="assignRules" label-width="100px" size="small">
        <el-form-item label="销售ID" prop="assigneeSalesId">
          <el-input-number v-model="assignForm.assigneeSalesId" :min="1" placeholder="输入销售 sales_id" style="width:100%" />
        </el-form-item>
        <el-form-item label="关联渠道ID">
          <el-input-number v-model="assignForm.channelId" :min="1" placeholder="选填" style="width:100%" />
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

export default {
  name: 'SalesManagerTeam',
  data() {
    return {
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
        assigneeSalesId: [{ required: true, message: '请输入销售ID', trigger: 'blur' }],
        title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }],
        dueDate: [{ required: true, message: '请选择截止日期', trigger: 'change' }]
      }
    }
  },
  created() {
    this.loadAssignedTasks()
  },
  methods: {
    loadAssignedTasks() {
      this.tasksLoading = true
      listAssignedByMe().then(res => {
        this.assignedTasks = res.rows || []
      }).catch(() => {}).finally(() => { this.tasksLoading = false })
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
