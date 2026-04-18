<template>
  <div class="app-container">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">销售工作台</p>
        <h2>我的预录入</h2>
        <p class="hero-desc">预录入潜在渠道手机号，渠道注册后自动归属本人。</p>
      </div>
      <el-button type="primary" icon="el-icon-plus" @click="openCreate">新建预录入</el-button>
    </div>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="手机号" prop="phone" width="130" align="center" />
      <el-table-column label="目标名称" prop="targetName" min-width="120" show-overflow-tooltip />
      <el-table-column label="状态" prop="status" width="100" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="到期时间" prop="expireAt" width="130" align="center">
        <template slot-scope="scope">
          <span :class="{ 'text-danger': isExpired(scope.row.expireAt) }">
            {{ formatDate(scope.row.expireAt) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="已续期" prop="renewCount" width="80" align="center">
        <template slot-scope="scope">{{ scope.row.renewCount || 0 }} / 2</template>
      </el-table-column>
      <el-table-column label="已匹配渠道" prop="matchedChannelId" width="120" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.matchedChannelId" size="mini" type="success">已匹配 #{{ scope.row.matchedChannelId }}</el-tag>
          <span v-else style="color:#909399">--</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.status === 'pending' && (scope.row.renewCount || 0) < 2"
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleRenew(scope.row)"
          >续期</el-button>
          <el-button
            v-if="scope.row.status === 'pending'"
            size="mini"
            type="text"
            icon="el-icon-delete"
            style="color:#F56C6C"
            @click="handleDelete(scope.row)"
          >删除</el-button>
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

    <!-- 新建弹窗 -->
    <el-dialog title="新建预录入" :visible.sync="createVisible" width="400px" @closed="resetCreateForm">
      <el-form ref="createForm" :model="createForm" :rules="createRules" label-width="80px" size="small">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="createForm.phone" placeholder="输入目标渠道手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="目标名称" prop="targetName">
          <el-input v-model="createForm.targetName" placeholder="渠道姓名或机构名（备注用）" maxlength="50" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button size="small" @click="createVisible = false">取消</el-button>
        <el-button type="primary" size="small" :loading="saving" @click="handleCreate">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMyPreReg, createPreReg, renewPreReg, removePreReg } from '@/api/sales/me/preregister'

export default {
  name: 'SalesPreRegister',
  data() {
    return {
      loading: false,
      saving: false,
      list: [],
      total: 0,
      createVisible: false,
      queryParams: { pageNum: 1, pageSize: 20 },
      createForm: { phone: '', targetName: '' },
      createRules: {
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        targetName: [{ required: true, message: '请输入目标名称', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listMyPreReg(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).catch(() => {}).finally(() => { this.loading = false })
    },
    openCreate() {
      this.createVisible = true
    },
    handleCreate() {
      this.$refs.createForm.validate(valid => {
        if (!valid) return
        this.saving = true
        createPreReg(this.createForm).then(() => {
          this.$message.success('创建成功')
          this.createVisible = false
          this.getList()
        }).catch(e => this.$message.error(e.msg || '创建失败')).finally(() => { this.saving = false })
      })
    },
    handleRenew(row) {
      this.$confirm(`确定续期"${row.phone}"？可延长 30 天有效期（每条最多续期 2 次）。`, '提示', { type: 'info' }).then(() => {
        renewPreReg(row.preId).then(() => {
          this.$message.success('续期成功')
          this.getList()
        }).catch(e => this.$message.error(e.msg || '续期失败'))
      }).catch(() => {})
    },
    handleDelete(row) {
      this.$confirm(`确定删除手机号"${row.phone}"的预录入记录？`, '提示', { type: 'warning' }).then(() => {
        removePreReg(row.preId).then(() => {
          this.$message.success('已删除')
          this.getList()
        }).catch(e => this.$message.error(e.msg || '删除失败'))
      }).catch(() => {})
    },
    resetCreateForm() {
      this.createForm = { phone: '', targetName: '' }
      this.$refs.createForm && this.$refs.createForm.resetFields()
    },
    statusType(s) {
      const map = { pending: 'warning', matched: 'success', expired: 'info' }
      return map[s] || 'info'
    },
    statusLabel(s) {
      const map = { pending: '待匹配', matched: '已匹配', expired: '已过期' }
      return map[s] || s
    },
    isExpired(d) {
      return d && new Date(d) < new Date()
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
.text-danger { color: #F56C6C; }
</style>
