<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="持有者类型" prop="ownerType">
        <el-select v-model="queryParams.ownerType" placeholder="全部" clearable>
          <el-option label="学生" value="student" />
          <el-option label="渠道" value="channel" />
          <el-option label="用户" value="user" />
        </el-select>
      </el-form-item>
      <el-form-item label="持有者ID" prop="ownerId">
        <el-input v-model="queryParams.ownerId" placeholder="请输入持有者ID" clearable @keyup.enter.native="handleQuery" />
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
        <div v-for="row in list" :key="row.accountId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">账户 #{{ row.accountId }}</span>
            <el-tag size="mini" :type="ownerTypeTagType(row.ownerType)">{{ ownerTypeLabel(row.ownerType) }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>
              持有者：
              <el-link v-if="row.ownerName && row.ownerId" type="primary" :underline="false" @click="goUser(row)">{{ row.ownerName }}</el-link>
              <span v-else>{{ row.ownerName || row.ownerId || '--' }}</span>
            </span>
            <span class="points-positive">可用 {{ row.availablePoints || 0 }}</span>
            <span class="points-negative">冻结 {{ row.frozenPoints || 0 }}</span>
            <span>等级：{{ row.levelName || row.currentLevelId || '--' }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无积分账户" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="账户ID" prop="accountId" width="90" />
      <el-table-column label="持有者类型" width="110">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="ownerTypeTagType(row.ownerType)">{{ ownerTypeLabel(row.ownerType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="持有者" min-width="150" show-overflow-tooltip>
        <template slot-scope="{ row }">
          <el-link v-if="row.ownerName && row.ownerId" type="primary" :underline="false" @click="goUser(row)">
            {{ row.ownerName }}
          </el-link>
          <span v-else>{{ row.ownerName || row.ownerId || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="可用积分" min-width="100" align="right">
        <template slot-scope="{ row }">
          <span class="points-positive">{{ row.availablePoints || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="冻结积分" min-width="100" align="right">
        <template slot-scope="{ row }">
          <span class="points-negative">{{ row.frozenPoints || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="累计获取" prop="totalEarn" min-width="100" align="right" />
      <el-table-column label="累计消耗" prop="totalSpend" min-width="100" align="right" />
      <el-table-column label="成长值" prop="growthValue" min-width="100" align="right" />
      <el-table-column label="当前等级" min-width="120" show-overflow-tooltip>
        <template slot-scope="{ row }">{{ row.levelName || row.currentLevelId || '--' }}</template>
      </el-table-column>
      <el-table-column label="更新时间" min-width="160">
        <template slot-scope="{ row }">{{ parseTime(row.updateTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="90">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-drawer :title="'积分账户详情 #' + (detailData.accountId || '')" :visible.sync="detailVisible" :size="isMobile ? '100%' : '520px'" append-to-body>
      <div class="detail-body">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="持有者">
            {{ ownerTypeLabel(detailData.ownerType) }} /
            <el-link v-if="detailData.ownerName && detailData.ownerId" type="primary" :underline="false" @click="goUser(detailData)">{{ detailData.ownerName }}</el-link>
            <span v-else>{{ detailData.ownerName || detailData.ownerId || '--' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="可用积分"><span class="points-positive">{{ detailData.availablePoints || 0 }}</span></el-descriptions-item>
          <el-descriptions-item label="冻结积分"><span class="points-negative">{{ detailData.frozenPoints || 0 }}</span></el-descriptions-item>
          <el-descriptions-item label="累计获取">{{ detailData.totalEarn || 0 }}</el-descriptions-item>
          <el-descriptions-item label="累计消耗">{{ detailData.totalSpend || 0 }}</el-descriptions-item>
          <el-descriptions-item label="成长值">{{ detailData.growthValue || 0 }}</el-descriptions-item>
          <el-descriptions-item label="当前等级">{{ detailData.levelName || detailData.currentLevelId || '--' }}</el-descriptions-item>
        </el-descriptions>

        <div class="drawer-block-title">最近流水</div>
        <el-table v-loading="ledgerLoading" :data="ledgerList" size="mini" max-height="260">
          <el-table-column label="时间" min-width="140">
            <template slot-scope="{ row }">{{ parseTime(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="类型" min-width="110">
            <template slot-scope="{ row }">{{ changeTypeLabel(row.changeType) }}</template>
          </el-table-column>
          <el-table-column label="变化" min-width="90" align="right">
            <template slot-scope="{ row }">
              <span :class="Number(row.pointsChange) >= 0 ? 'points-positive' : 'points-negative'">
                {{ Number(row.pointsChange) >= 0 ? '+' : '' }}{{ row.pointsChange || 0 }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="余额" prop="balanceAfter" min-width="80" align="right" />
        </el-table>
        <el-empty v-if="!ledgerLoading && !ledgerList.length" :image-size="80" description="暂无流水" />
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'
import { listJst_points_account, getJst_points_account } from '@/api/jst/points/jst_points_account'
import { listJst_points_ledger } from '@/api/jst/points/jst_points_ledger'

const OWNER_TYPE_MAP = {
  student: { label: '学生', tag: 'success' },
  channel: { label: '渠道', tag: 'warning' },
  user: { label: '用户', tag: 'info' }
}

const CHANGE_TYPE_MAP = {
  earn: '获取',
  spend: '消耗',
  freeze: '冻结',
  unfreeze: '解冻',
  adjust: '调整',
  rollback: '回滚',
  aftersale_refund: '售后返还'
}

export default {
  name: 'JstPointsAccount',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ownerType: null,
        ownerId: null
      },
      detailVisible: false,
      detailData: {},
      ledgerLoading: false,
      ledgerList: []
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
    ownerTypeLabel(ownerType) {
      return (OWNER_TYPE_MAP[ownerType] || {}).label || ownerType || '--'
    },
    ownerTypeTagType(ownerType) {
      return (OWNER_TYPE_MAP[ownerType] || {}).tag || 'info'
    },
    changeTypeLabel(changeType) {
      return CHANGE_TYPE_MAP[changeType] || changeType || '--'
    },
    getList() {
      this.loading = true
      listJst_points_account(this.queryParams).then(res => {
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
      getJst_points_account(row.accountId).then(res => {
        this.detailData = res.data || res || row
        this.detailVisible = true
        this.getLedgerList(this.detailData.accountId)
      })
    },
    getLedgerList(accountId) {
      this.ledgerLoading = true
      listJst_points_ledger({
        pageNum: 1,
        pageSize: 20,
        accountId: accountId
      }).then(res => {
        this.ledgerList = res.rows || []
      }).finally(() => {
        this.ledgerLoading = false
      })
    },
    goUser(row) {
      const userId = row && (row.ownerId || row.userId)
      if (!userId) return
      this.$router.push({
        path: '/jst/user',
        query: { userId: String(userId), autoOpen: '1' }
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

.drawer-block-title {
  margin: 14px 0 8px;
  font-weight: 600;
  color: #303133;
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
