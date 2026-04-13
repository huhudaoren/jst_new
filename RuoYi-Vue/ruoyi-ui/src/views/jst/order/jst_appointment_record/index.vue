<template>
  <div class="app-container appointment-record-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">预约中心</p>
        <h2>个人预约记录管理</h2>
        <p class="hero-desc">支持按预约人/赛事智能检索，展示预约状态与核销状态，支持详情抽屉查看。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="88px" class="query-panel">
      <el-form-item label="预约单号">
        <el-input
          v-model="queryParams.appointmentNo"
          placeholder="请输入预约单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预约人">
        <el-input
          v-model="queryParams.participantKeyword"
          placeholder="姓名/编号/ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛事">
        <el-input
          v-model="queryParams.contestKeyword"
          placeholder="赛事名/赛事ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预约状态">
        <el-select v-model="queryParams.mainStatus" placeholder="全部" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="预约日期">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始"
          end-placeholder="结束"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.appointmentId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.appointmentNo || '--' }}</div>
              <div class="mobile-sub">{{ contestText(row) }}</div>
            </div>
            <el-tag size="small" :type="statusType(row.mainStatus)">{{ statusLabel(row.mainStatus) }}</el-tag>
          </div>
          <div class="mobile-info-row">
            <span>预约人 {{ participantText(row) }}</span>
            <span>{{ row.appointmentDate || '--' }}</span>
          </div>
          <div class="mobile-info-row">
            <span>核销状态</span>
            <el-tag size="mini" :type="writeoffMeta(row).type">{{ writeoffMeta(row).label }}</el-tag>
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">查看详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无预约记录" :image-size="96" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="预约单号" prop="appointmentNo" min-width="170" show-overflow-tooltip />
      <el-table-column label="赛事" min-width="180" show-overflow-tooltip>
        <template slot-scope="scope">{{ contestText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="预约人" min-width="140" show-overflow-tooltip>
        <template slot-scope="scope">{{ participantText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="预约类型" min-width="110">
        <template slot-scope="scope">
          <el-tag size="small" type="info">{{ appointmentTypeLabel(scope.row.appointmentType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="预约日期" prop="appointmentDate" min-width="120" />
      <el-table-column label="场次/时段" prop="sessionCode" min-width="120" show-overflow-tooltip />
      <el-table-column label="预约状态" min-width="120">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.mainStatus)">{{ statusLabel(scope.row.mainStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="核销状态" min-width="120">
        <template slot-scope="scope">
          <el-tag size="small" :type="writeoffMeta(scope.row).type">{{ writeoffMeta(scope.row).label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="关联订单号" min-width="150" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link v-if="scope.row.orderNo && scope.row.orderId" type="primary" :underline="false" @click="goOrder(scope.row)">
            {{ scope.row.orderNo }}
          </el-link>
          <span v-else>{{ scope.row.orderNo || scope.row.orderId || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
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

    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '700px'" title="预约详情" append-to-body>
      <div v-loading="detailLoading" class="drawer-body">
        <el-descriptions v-if="detail" :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="预约ID">{{ detail.appointmentId }}</el-descriptions-item>
          <el-descriptions-item label="预约单号">{{ detail.appointmentNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="赛事">{{ contestText(detail) }}</el-descriptions-item>
          <el-descriptions-item label="预约人">{{ participantText(detail) }}</el-descriptions-item>
          <el-descriptions-item label="预约类型">{{ appointmentTypeLabel(detail.appointmentType) }}</el-descriptions-item>
          <el-descriptions-item label="预约日期">{{ detail.appointmentDate || '--' }}</el-descriptions-item>
          <el-descriptions-item label="场次/时段">{{ detail.sessionCode || '--' }}</el-descriptions-item>
          <el-descriptions-item label="关联团队ID">{{ detail.teamAppointmentId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="关联订单号">
            <el-link v-if="detail.orderNo && detail.orderId" type="primary" :underline="false" @click="goOrder(detail)">{{ detail.orderNo }}</el-link>
            <span v-else>{{ detail.orderNo || detail.orderId || '--' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="渠道ID">{{ detail.channelId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="预约状态">
            <el-tag size="small" :type="statusType(detail.mainStatus)">{{ statusLabel(detail.mainStatus) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="核销状态">
            <el-tag size="small" :type="writeoffMeta(detail).type">{{ writeoffMeta(detail).label }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="二维码URL" :span="isMobile ? 1 : 2">{{ detail.qrCode || '--' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ parseTime(detail.createTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ parseTime(detail.updateTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="isMobile ? 1 : 2">{{ detail.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
        <el-empty v-else description="暂无详情数据" :image-size="96" />
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_appointment_record, getJst_appointment_record } from '@/api/jst/order/jst_appointment_record'

const APPOINTMENT_STATUS_MAP = {
  unbooked: { label: '待预约', type: 'info' },
  booked: { label: '已预约', type: 'primary' },
  partial_writeoff: { label: '部分核销中', type: 'warning' },
  fully_writeoff: { label: '已全部核销', type: 'success' },
  partial_writeoff_ended: { label: '部分核销结束', type: 'warning' },
  cancelled: { label: '已取消', type: 'info' },
  expired: { label: '已过期', type: 'info' },
  no_show: { label: '未到场', type: 'danger' }
}

const APPOINTMENT_TYPE_MAP = {
  individual: '个人预约',
  team: '团队预约'
}

export default {
  name: 'JstAppointmentRecordManage',
  data() {
    return {
      loading: false,
      detailLoading: false,
      isMobile: false,
      list: [],
      total: 0,
      dateRange: [],
      detailVisible: false,
      detail: null,
      lastAutoOpenKey: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        appointmentNo: '',
        participantKeyword: '',
        contestKeyword: '',
        mainStatus: undefined
      },
      statusOptions: Object.keys(APPOINTMENT_STATUS_MAP).map(key => ({
        value: key,
        label: APPOINTMENT_STATUS_MAP[key].label
      }))
    }
  },
  watch: {
    '$route.query': {
      handler() {
        this.tryAutoOpenFromRoute()
      },
      deep: true
    }
  },
  created() {
    this.updateViewport()
    window.addEventListener('resize', this.updateViewport)
    this.getList()
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.updateViewport)
  },
  methods: {
    updateViewport() {
      this.isMobile = window.innerWidth <= 768
    },
    isNumericKeyword(value) {
      return /^\d+$/.test(String(value || '').trim())
    },
    async getList() {
      this.loading = true
      try {
        const participantKeyword = String(this.queryParams.participantKeyword || '').trim()
        const contestKeyword = String(this.queryParams.contestKeyword || '').trim()
        const params = {
          pageNum: this.queryParams.pageNum,
          pageSize: this.queryParams.pageSize,
          appointmentNo: this.queryParams.appointmentNo || undefined,
          mainStatus: this.queryParams.mainStatus || undefined
        }
        if (this.isNumericKeyword(participantKeyword)) {
          params.participantId = Number(participantKeyword)
        }
        if (this.isNumericKeyword(contestKeyword)) {
          params.contestId = Number(contestKeyword)
        }
        if (this.dateRange && this.dateRange.length === 2) {
          params.beginAppointmentDate = this.dateRange[0]
          params.endAppointmentDate = this.dateRange[1]
        }
        const res = await listJst_appointment_record(params)
        const rows = res.rows || []
        const localParticipant = participantKeyword && !this.isNumericKeyword(participantKeyword)
        const localContest = contestKeyword && !this.isNumericKeyword(contestKeyword)
        const localDate = this.dateRange && this.dateRange.length === 2
        if (localParticipant || localContest || localDate) {
          const participantLower = participantKeyword.toLowerCase()
          const contestLower = contestKeyword.toLowerCase()
          this.list = rows.filter(row => {
            const participantMatch = !localParticipant || this.participantText(row).toLowerCase().indexOf(participantLower) > -1
            const contestMatch = !localContest || this.contestText(row).toLowerCase().indexOf(contestLower) > -1
            const dateMatch = this.matchDateRange(row.appointmentDate, this.dateRange)
            return participantMatch && contestMatch && dateMatch
          })
          this.total = this.list.length
        } else {
          this.list = rows
          this.total = res.total || 0
        }
      } finally {
        this.loading = false
        this.tryAutoOpenFromRoute()
      }
    },
    matchDateRange(value, range) {
      if (!range || range.length !== 2) return true
      if (!value) return false
      const begin = new Date(range[0] + ' 00:00:00').getTime()
      const end = new Date(range[1] + ' 23:59:59').getTime()
      const current = new Date(value + ' 12:00:00').getTime()
      return current >= begin && current <= end
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        appointmentNo: '',
        participantKeyword: '',
        contestKeyword: '',
        mainStatus: undefined
      }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detailLoading = true
      try {
        const res = await getJst_appointment_record(row.appointmentId)
        this.detail = res.data || null
      } finally {
        this.detailLoading = false
      }
    },
    tryAutoOpenFromRoute() {
      const query = this.$route.query || {}
      const autoOpen = query.autoOpen
      const appointmentId = query.appointmentId
      if (autoOpen !== '1' || !appointmentId) return
      const key = autoOpen + '-' + appointmentId
      if (this.lastAutoOpenKey === key) return
      this.lastAutoOpenKey = key
      const id = Number(appointmentId)
      if (!id) return
      this.openDetail({ appointmentId: id })
    },
    goOrder(row) {
      const orderId = row && row.orderId
      if (!orderId) return
      this.$router.push({
        path: '/jst/order/admin-order',
        query: { orderId: String(orderId), autoOpen: '1' }
      }).catch(() => {})
    },
    contestText(row) {
      return row.contestName || row.contestTitle || (row.contestId ? '赛事 #' + row.contestId : '--')
    },
    participantText(row) {
      return row.participantName || row.nameSnapshot || (row.participantId ? 'ID ' + row.participantId : '--')
    },
    statusLabel(value) {
      return (APPOINTMENT_STATUS_MAP[value] && APPOINTMENT_STATUS_MAP[value].label) || value || '--'
    },
    statusType(value) {
      return (APPOINTMENT_STATUS_MAP[value] && APPOINTMENT_STATUS_MAP[value].type) || 'info'
    },
    appointmentTypeLabel(value) {
      return APPOINTMENT_TYPE_MAP[value] || value || '--'
    },
    writeoffMeta(row) {
      const status = row.mainStatus
      if (status === 'fully_writeoff') {
        return { label: '已全部核销', type: 'success' }
      }
      if (status === 'partial_writeoff' || status === 'partial_writeoff_ended') {
        return { label: '部分核销', type: 'warning' }
      }
      if (status === 'no_show' || status === 'expired' || status === 'cancelled') {
        return { label: '未核销', type: 'info' }
      }
      return { label: '待核销', type: 'info' }
    }
  }
}
</script>

<style scoped>
.appointment-record-page {
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

.drawer-body {
  padding: 0 24px 24px;
}

.mobile-list {
  min-height: 180px;
}

.mobile-card {
  padding: 16px;
  margin-bottom: 12px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.mobile-card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.mobile-title {
  font-weight: 700;
  color: #172033;
}

.mobile-sub {
  margin-top: 4px;
  font-size: 12px;
  color: #7a8495;
}

.mobile-info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
  font-size: 13px;
  color: #7a8495;
}

.mobile-actions {
  display: flex;
  gap: 14px;
  margin-top: 12px;
}

@media (max-width: 768px) {
  .appointment-record-page {
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
  .query-panel ::v-deep .el-input,
  .query-panel ::v-deep .el-date-editor {
    width: 100%;
  }

  .mobile-actions .el-button {
    min-height: 44px;
  }
}
</style>
