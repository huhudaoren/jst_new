<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="核销单号" prop="writeoffNo">
        <el-input v-model="queryParams.writeoffNo" placeholder="请输入核销单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="权益ID" prop="userRightsId">
        <el-input v-model="queryParams.userRightsId" placeholder="请输入用户权益ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="核销时间" prop="writeoffTime">
        <el-date-picker v-model="queryParams.writeoffTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="精确时间" clearable class="date-picker-width" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.recordId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.writeoffNo }}</span>
            <el-tag size="mini" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>权益ID：{{ row.userRightsId }}</span>
            <span class="amount-cell">使用：¥ {{ formatAmount(row.useAmount) }}</span>
            <span>核销时间：{{ parseTime(row.writeoffTime) || '--' }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无核销记录" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="记录ID" prop="recordId" width="90" />
      <el-table-column label="核销单号" prop="writeoffNo" min-width="150" show-overflow-tooltip />
      <el-table-column label="权益ID" prop="userRightsId" min-width="100" />
      <el-table-column label="使用额度" min-width="100" align="right">
        <template slot-scope="{ row }">
          <span class="amount-cell">¥ {{ formatAmount(row.useAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核员" prop="auditUserId" min-width="90" />
      <el-table-column label="核销时间" min-width="160">
        <template slot-scope="{ row }">{{ parseTime(row.writeoffTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="90">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-drawer :title="'核销详情 #' + (detailData.recordId || '')" :visible.sync="detailVisible" :size="isMobile ? '100%' : '520px'" append-to-body>
      <div class="detail-body">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="核销单号">{{ detailData.writeoffNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="权益ID">{{ detailData.userRightsId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="使用额度">¥ {{ formatAmount(detailData.useAmount) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag size="small" :type="statusType(detailData.status)">{{ statusLabel(detailData.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请说明">{{ detailData.applyRemark || '--' }}</el-descriptions-item>
          <el-descriptions-item label="审核员">{{ detailData.auditUserId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="审核备注">{{ detailData.auditRemark || '--' }}</el-descriptions-item>
          <el-descriptions-item label="核销时间">{{ parseTime(detailData.writeoffTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detailData.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'
import { listJst_rights_writeoff_record, getJst_rights_writeoff_record } from '@/api/jst/marketing/jst_rights_writeoff_record'

const STATUS_META = {
  pending: { label: '待审核', type: 'warning' },
  approved: { label: '已通过', type: 'success' },
  rejected: { label: '已驳回', type: 'danger' },
  confirmed: { label: '已确认', type: 'success' },
  rolled_back: { label: '已回滚', type: 'info' }
}

export default {
  name: 'JstRightsWriteoffRecord',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        writeoffNo: null,
        userRightsId: null,
        status: null,
        writeoffTime: null
      },
      detailVisible: false,
      detailData: {},
      statusOptions: Object.keys(STATUS_META).map(key => ({ value: key, label: STATUS_META[key].label }))
    }
  },
  computed: {
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    parseTime,
    statusType(status) {
      return (STATUS_META[status] || {}).type || 'info'
    },
    statusLabel(status) {
      return (STATUS_META[status] || {}).label || status || '--'
    },
    formatAmount(value) {
      if (value === null || value === undefined || value === '') {
        return '0.00'
      }
      const num = Number(value)
      if (Number.isNaN(num)) {
        return value
      }
      const displayNum = Number.isInteger(num) ? num / 100 : num
      return displayNum.toFixed(2)
    },
    getList() {
      this.loading = true
      listJst_rights_writeoff_record(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    handleDetail(row) {
      getJst_rights_writeoff_record(row.recordId).then(res => {
        this.detailData = res.data || res || row
        this.detailVisible = true
      })
    }
  }
}
</script>

<style scoped>
.amount-cell {
  text-align: right;
  display: inline-block;
  min-width: 68px;
}

.detail-body {
  padding: 0 16px 16px;
}

.mobile-card-list {
  padding: 0 4px;
}

.mobile-card {
  background: #fff;
  border-radius: 8px;
  padding: 12px 14px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.mobile-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.mobile-card__title {
  font-weight: 600;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 65%;
}

.mobile-card__meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.mobile-card__actions {
  display: flex;
  gap: 6px;
}
</style>
