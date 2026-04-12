<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="批次号" prop="batchNo">
        <el-input v-model="queryParams.batchNo" placeholder="请输入批次号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="模板ID" prop="couponTemplateId">
        <el-input v-model="queryParams.couponTemplateId" placeholder="请输入模板ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
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
        <div v-for="row in list" :key="row.batchId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.batchNo }}</span>
            <el-tag size="mini" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>模板ID：{{ row.couponTemplateId }}</span>
            <span>发放/领取：{{ row.totalCount || 0 }}/{{ row.successCount || 0 }}</span>
            <span>失败：{{ row.failCount || 0 }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无发券批次" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="批次ID" prop="batchId" width="90" />
      <el-table-column label="批次号" prop="batchNo" min-width="160" show-overflow-tooltip />
      <el-table-column label="模板ID" prop="couponTemplateId" min-width="100" />
      <el-table-column label="目标类型" prop="targetType" min-width="110" />
      <el-table-column label="发放数量" prop="totalCount" min-width="90" align="right" />
      <el-table-column label="领取数量" prop="successCount" min-width="90" align="right" />
      <el-table-column label="失败数量" prop="failCount" min-width="90" align="right" />
      <el-table-column label="状态" width="100">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" min-width="160">
        <template slot-scope="{ row }">{{ parseTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="90">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-drawer :title="'发券批次详情 #' + (detailData.batchId || '')" :visible.sync="detailVisible" :size="isMobile ? '100%' : '520px'" append-to-body>
      <div class="detail-body">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="批次号">{{ detailData.batchNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="模板ID">{{ detailData.couponTemplateId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="目标类型">{{ detailData.targetType || '--' }}</el-descriptions-item>
          <el-descriptions-item label="发放/领取">{{ detailData.totalCount || 0 }} / {{ detailData.successCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="失败数量">{{ detailData.failCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag size="small" :type="statusType(detailData.status)">{{ statusLabel(detailData.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="目标JSON">
            <pre class="json-block">{{ formatJson(detailData.targetJson) }}</pre>
          </el-descriptions-item>
          <el-descriptions-item label="备注">{{ detailData.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'
import { listJst_coupon_issue_batch, getJst_coupon_issue_batch } from '@/api/jst/marketing/jst_coupon_issue_batch'

const STATUS_META = {
  pending: { label: '待执行', type: 'warning' },
  running: { label: '执行中', type: 'warning' },
  completed: { label: '已完成', type: 'success' },
  failed: { label: '失败', type: 'danger' }
}

export default {
  name: 'JstCouponIssueBatch',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        batchNo: null,
        couponTemplateId: null,
        status: null
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
    formatJson(val) {
      if (!val) {
        return '--'
      }
      if (typeof val === 'string') {
        try {
          return JSON.stringify(JSON.parse(val), null, 2)
        } catch (e) {
          return val
        }
      }
      return JSON.stringify(val, null, 2)
    },
    getList() {
      this.loading = true
      listJst_coupon_issue_batch(this.queryParams).then(res => {
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
      getJst_coupon_issue_batch(row.batchId).then(res => {
        this.detailData = res.data || res || row
        this.detailVisible = true
      })
    }
  }
}
</script>

<style scoped>
.detail-body {
  padding: 0 16px 16px;
}

.json-block {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 12px;
  line-height: 1.5;
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
