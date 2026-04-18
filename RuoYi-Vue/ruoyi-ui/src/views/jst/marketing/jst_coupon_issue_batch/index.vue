<template>
  <div class="app-container coupon-batch-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">营销管理</p>
        <h2>优惠券批次</h2>
        <p class="hero-desc">管理优惠券发放批次，查看发放进度与目标详情。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="批次号" prop="batchNo">
        <el-input v-model="queryParams.batchNo" placeholder="请输入批次号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="模板ID" prop="couponTemplateId">
        <coupon-template-picker v-model="queryParams.couponTemplateId" clearable />
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
          <el-descriptions-item label="目标用户">
            <JsonDisplay :value="detailData.targetJson" :label-map="{ userId: '用户ID', userName: '用户名', mobile: '手机号', role: '角色', tag: '标签' }" />
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
import CouponTemplatePicker from '@/components/RelationPicker/CouponTemplatePicker.vue'

const STATUS_META = {
  pending: { label: '待执行', type: 'warning' },
  running: { label: '执行中', type: 'warning' },
  completed: { label: '已完成', type: 'success' },
  failed: { label: '失败', type: 'danger' }
}

export default {
  name: 'JstCouponIssueBatch',
  components: {
    CouponTemplatePicker
  },
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
.coupon-batch-page {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.page-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
  margin-bottom: 18px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.hero-eyebrow {
  margin: 0 0 8px;
  color: #2f6fec;
  font-size: 13px;
  font-weight: 600;
}

.page-hero h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #172033;
}

.hero-desc {
  margin: 8px 0 0;
  color: #6f7b8f;
}

.query-panel {
  padding: 16px 16px 0;
  margin-bottom: 16px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

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

@media (max-width: 768px) {
  .coupon-batch-page {
    padding: 12px;
  }

  .page-hero {
    display: block;
    padding: 18px;
  }

  .page-hero .el-button {
    width: 100%;
    min-height: 44px;
    margin-top: 16px;
  }

  .page-hero h2 {
    font-size: 20px;
  }

  .query-panel {
    padding-bottom: 8px;
  }

  .query-panel ::v-deep .el-form-item {
    display: block;
    margin-right: 0;
  }

  .query-panel ::v-deep .el-form-item__content,
  .query-panel ::v-deep .el-select,
  .query-panel ::v-deep .el-input {
    width: 100%;
  }
}
</style>
