<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="持有者ID" prop="ownerId">
        <el-input v-model="queryParams.ownerId" placeholder="请输入持有者ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="变更类型" prop="changeType">
        <el-select v-model="queryParams.changeType" placeholder="全部" clearable>
          <el-option v-for="item in changeTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="业务来源" prop="sourceType">
        <el-select v-model="queryParams.sourceType" placeholder="全部" clearable>
          <el-option v-for="item in sourceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="来源单据" prop="sourceRefId">
        <el-input v-model="queryParams.sourceRefId" placeholder="请输入来源单据ID" clearable @keyup.enter.native="handleQuery" />
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
        <div v-for="row in list" :key="row.ledgerId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">流水 #{{ row.ledgerId }}</span>
            <el-tag size="mini" :type="directionType(row.pointsChange)">
              {{ Number(row.pointsChange) >= 0 ? '收入' : '支出' }}
            </el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>用户：{{ row.ownerId || '--' }}</span>
            <span>{{ changeTypeLabel(row.changeType) }}</span>
            <span :class="Number(row.pointsChange) >= 0 ? 'points-positive' : 'points-negative'">
              {{ Number(row.pointsChange) >= 0 ? '+' : '' }}{{ row.pointsChange || 0 }}
            </span>
            <span>余额：{{ row.balanceAfter || 0 }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
            <el-button type="text" size="mini" @click="handleBusinessJump(row)">关联业务</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无积分流水" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="流水ID" prop="ledgerId" width="90" />
      <el-table-column label="持有者ID" prop="ownerId" min-width="110" />
      <el-table-column label="变更类型" min-width="110">
        <template slot-scope="{ row }">{{ changeTypeLabel(row.changeType) }}</template>
      </el-table-column>
      <el-table-column label="业务来源" min-width="120">
        <template slot-scope="{ row }">{{ sourceTypeLabel(row.sourceType) }}</template>
      </el-table-column>
      <el-table-column label="来源单据ID" prop="sourceRefId" min-width="130" />
      <el-table-column label="积分变化" min-width="100" align="right">
        <template slot-scope="{ row }">
          <span :class="Number(row.pointsChange) >= 0 ? 'points-positive' : 'points-negative'">
            {{ Number(row.pointsChange) >= 0 ? '+' : '' }}{{ row.pointsChange || 0 }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="变更后余额" prop="balanceAfter" min-width="100" align="right" />
      <el-table-column label="操作人" prop="operatorId" min-width="100" />
      <el-table-column label="创建时间" min-width="160">
        <template slot-scope="{ row }">{{ parseTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="140">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
          <el-button type="text" size="mini" @click="handleBusinessJump(row)">关联业务</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-drawer :title="'积分流水详情 #' + (detailData.ledgerId || '')" :visible.sync="detailVisible" :size="isMobile ? '100%' : '500px'" append-to-body>
      <div class="detail-body">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="流水ID">{{ detailData.ledgerId }}</el-descriptions-item>
          <el-descriptions-item label="账户ID">{{ detailData.accountId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="持有者">{{ detailData.ownerType || '--' }} / {{ detailData.ownerId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="变更类型">{{ changeTypeLabel(detailData.changeType) }}</el-descriptions-item>
          <el-descriptions-item label="业务来源">{{ sourceTypeLabel(detailData.sourceType) }}</el-descriptions-item>
          <el-descriptions-item label="来源单据ID">{{ detailData.sourceRefId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="积分变化">
            <span :class="Number(detailData.pointsChange) >= 0 ? 'points-positive' : 'points-negative'">
              {{ Number(detailData.pointsChange) >= 0 ? '+' : '' }}{{ detailData.pointsChange || 0 }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="变更后余额">{{ detailData.balanceAfter || 0 }}</el-descriptions-item>
          <el-descriptions-item label="操作人">{{ detailData.operatorId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ parseTime(detailData.createTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detailData.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'
import { listJst_points_ledger, getJst_points_ledger } from '@/api/jst/points/jst_points_ledger'

const CHANGE_TYPE_OPTIONS = [
  { label: '获取', value: 'earn' },
  { label: '消耗', value: 'spend' },
  { label: '冻结', value: 'freeze' },
  { label: '解冻', value: 'unfreeze' },
  { label: '调整', value: 'adjust' },
  { label: '回滚', value: 'rollback' },
  { label: '售后返还', value: 'aftersale_refund' }
]

const SOURCE_TYPE_OPTIONS = [
  { label: '报名', value: 'enroll' },
  { label: '课程', value: 'course' },
  { label: '签到', value: 'sign' },
  { label: '邀请', value: 'invite' },
  { label: '学习', value: 'learn' },
  { label: '获奖', value: 'award' },
  { label: '兑换', value: 'exchange' },
  { label: '手工调整', value: 'manual' },
  { label: '退款', value: 'refund' },
  { label: '售后', value: 'mall_aftersale' }
]

const BIZ_ROUTE_MAP = {
  enroll: '/jst/enroll',
  course: '/jst/course',
  exchange: '/jst/mall/exchange',
  refund: '/jst/refund'
}

export default {
  name: 'JstPointsLedger',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ownerId: null,
        changeType: null,
        sourceType: null,
        sourceRefId: null
      },
      detailVisible: false,
      detailData: {},
      changeTypeOptions: CHANGE_TYPE_OPTIONS,
      sourceTypeOptions: SOURCE_TYPE_OPTIONS
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
    directionType(pointsChange) {
      return Number(pointsChange) >= 0 ? 'success' : 'danger'
    },
    changeTypeLabel(changeType) {
      const match = CHANGE_TYPE_OPTIONS.find(item => item.value === changeType)
      return match ? match.label : changeType || '--'
    },
    sourceTypeLabel(sourceType) {
      const match = SOURCE_TYPE_OPTIONS.find(item => item.value === sourceType)
      return match ? match.label : sourceType || '--'
    },
    getList() {
      this.loading = true
      listJst_points_ledger(this.queryParams).then(res => {
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
      getJst_points_ledger(row.ledgerId).then(res => {
        this.detailData = res.data || res || row
        this.detailVisible = true
      })
    },
    handleBusinessJump(row) {
      if (!row.sourceRefId) {
        this.$modal.msgWarning('该流水未关联业务单据')
        return
      }
      const routePath = BIZ_ROUTE_MAP[row.sourceType]
      if (!routePath) {
        this.$modal.msgWarning('当前来源类型暂未配置跳转')
        return
      }
      this.$router.push({
        path: routePath,
        query: { refId: row.sourceRefId }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.points-positive {
  color: #67c23a;
  font-weight: 600;
}

.points-negative {
  color: #f56c6c;
  font-weight: 600;
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
