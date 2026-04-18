<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="待裁决" value="pending" />
          <el-option label="已解决" value="resolved" />
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
        <el-button icon="el-icon-back" size="mini" @click="$router.go(-1)">返回</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="冲突ID" prop="conflictId" width="90" />
      <el-table-column label="渠道" width="160">
        <template slot-scope="scope">
          <entity-link entity="channel" :id="scope.row.channelId" :name="scope.row.channelName" />
        </template>
      </el-table-column>
      <el-table-column label="候选销售" prop="candidateSalesIds" min-width="160" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ formatCandidates(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="冲突原因" prop="reason" min-width="160" show-overflow-tooltip />
      <el-table-column label="状态" prop="status" width="100" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.status === 'resolved' ? 'success' : 'warning'">
            {{ scope.row.status === 'resolved' ? '已解决' : '待裁决' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="解决备注" prop="resolutionRemark" min-width="140" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.resolutionRemark || '-' }}</template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" align="center">
        <template slot-scope="scope">{{ parseTime(scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini" type="text" icon="el-icon-s-check"
            v-if="scope.row.status === 'pending'"
            @click="handleResolve(scope.row)"
          >裁决</el-button>
          <span v-else class="text-muted">已处理</span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 裁决 dialog -->
    <el-dialog title="归属冲突裁决" :visible.sync="resolveOpen" width="480px" append-to-body>
      <el-form :model="resolveForm" ref="resolveForm" label-width="110px">
        <div class="form-section">
          <div class="form-section__title">冲突信息</div>
          <el-form-item label="渠道ID">
            <span>{{ currentRow.channelId }}</span>
          </el-form-item>
          <el-form-item label="候选销售">
            <span>{{ formatCandidates(currentRow) }}</span>
          </el-form-item>
        </div>
        <el-divider />
        <div class="form-section">
          <div class="form-section__title">裁决结果</div>
          <el-form-item label="最终归属销售" prop="finalSalesId">
            <el-select v-model="resolveForm.finalSalesId" placeholder="请选择最终归属" style="width:100%">
              <el-option v-for="s in salesOptions" :key="s.salesId" :label="s.salesName + ' (' + s.salesId + ')'" :value="s.salesId" />
            </el-select>
          </el-form-item>
          <el-form-item label="备注">
            <el-input type="textarea" v-model="resolveForm.resolutionRemark" placeholder="请输入裁决备注" :rows="3" />
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer">
        <el-button @click="resolveOpen = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitResolve">确认裁决</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listConflict, resolveConflict } from '@/api/admin/sales/conflict'
import { listSales } from '@/api/admin/sales/index'

export default {
  name: 'SalesConflict',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        status: undefined
      },
      resolveOpen: false,
      currentRow: {},
      resolveForm: { finalSalesId: null, resolutionRemark: '' },
      salesOptions: [],
      submitting: false
    }
  },
  created() {
    this.getList()
    this.loadSalesOptions()
  },
  methods: {
    getList() {
      this.loading = true
      listConflict(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
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
    loadSalesOptions() {
      listSales({ pageNum: 1, pageSize: 200, status: 'active' }).then(res => {
        this.salesOptions = res.rows || []
      }).catch(() => {})
    },
    formatCandidates(row) {
      if (!row) return '-'
      if (row.candidateSalesIds) {
        try {
          const ids = JSON.parse(row.candidateSalesIds)
          return Array.isArray(ids) ? ids.join(', ') : String(row.candidateSalesIds)
        } catch (e) { return String(row.candidateSalesIds) }
      }
      return '-'
    },
    handleResolve(row) {
      this.currentRow = row
      this.resolveForm = { finalSalesId: null, resolutionRemark: '' }
      this.resolveOpen = true
    },
    submitResolve() {
      if (!this.resolveForm.finalSalesId) {
        this.$message.warning('请选择最终归属销售')
        return
      }
      this.submitting = true
      resolveConflict(this.currentRow.conflictId, {
        finalSalesId: this.resolveForm.finalSalesId,
        resolutionRemark: this.resolveForm.resolutionRemark
      }).then(() => {
        this.$modal.msgSuccess('裁决成功')
        this.resolveOpen = false
        this.getList()
      }).catch(() => {}).finally(() => { this.submitting = false })
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
  border-left: 3px solid #409EFF;
  padding-left: 8px;
  margin-bottom: 12px;
}
.text-muted { color: #909399; font-size: 12px; }
@media (max-width: 768px) {
  .el-form--inline .el-form-item { display: block; margin-right: 0; }
}
</style>
