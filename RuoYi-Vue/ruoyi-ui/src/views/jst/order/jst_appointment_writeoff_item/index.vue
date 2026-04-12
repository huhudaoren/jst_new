<template>
  <div class="app-container writeoff-item-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">预约中心</p>
        <h2>核销明细管理</h2>
        <p class="hero-desc">支持核销单号/预约单号检索，查看核销人和核销时间，并可跳转到预约详情。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="88px" class="query-panel">
      <el-form-item label="核销单号">
        <el-input
          v-model="queryParams.writeoffKeyword"
          placeholder="请输入核销单号/ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预约单号">
        <el-input
          v-model="queryParams.appointmentKeyword"
          placeholder="请输入预约单号/ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="核销状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="核销人">
        <el-input
          v-model="queryParams.writeoffUserKeyword"
          placeholder="请输入核销人姓名或ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="核销时间">
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
        <div v-for="row in list" :key="row.writeoffItemId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ writeoffNoText(row) }}</div>
              <div class="mobile-sub">
                <el-link type="primary" :underline="false" @click="goAppointment(row)">
                  预约 {{ appointmentNoText(row) }}
                </el-link>
              </div>
            </div>
            <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div class="mobile-info-row">
            <span>核销人 {{ writeoffUserText(row) }}</span>
            <span>{{ parseTime(row.writeoffTime) || '--' }}</span>
          </div>
          <div class="mobile-info-row">
            <span>核销人数 {{ row.writeoffQty || 0 }}/{{ row.totalQty || 0 }}</span>
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">查看详情</el-button>
            <el-button type="text" @click="goAppointment(row)">关联预约</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无核销明细" :image-size="96" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="核销单号" min-width="160" show-overflow-tooltip>
        <template slot-scope="scope">{{ writeoffNoText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="预约单号" min-width="170" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="goAppointment(scope.row)">
            {{ appointmentNoText(scope.row) }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="子项类型" min-width="120">
        <template slot-scope="scope">{{ itemTypeLabel(scope.row.itemType) }}</template>
      </el-table-column>
      <el-table-column label="子项名称" prop="itemName" min-width="150" show-overflow-tooltip />
      <el-table-column label="核销状态" min-width="110">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="核销人" min-width="130" show-overflow-tooltip>
        <template slot-scope="scope">{{ writeoffUserText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="核销时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.writeoffTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="核销人数" min-width="100" align="center">
        <template slot-scope="scope">{{ scope.row.writeoffQty || 0 }}/{{ scope.row.totalQty || 0 }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
          <el-button type="text" @click="goAppointment(scope.row)">预约</el-button>
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

    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '700px'" title="核销明细详情" append-to-body>
      <div v-loading="detailLoading" class="drawer-body">
        <el-descriptions v-if="detail" :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="核销ID">{{ detail.writeoffItemId }}</el-descriptions-item>
          <el-descriptions-item label="核销单号">{{ writeoffNoText(detail) }}</el-descriptions-item>
          <el-descriptions-item label="预约单号">{{ appointmentNoText(detail) }}</el-descriptions-item>
          <el-descriptions-item label="团队预约ID">{{ detail.teamAppointmentId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="子项类型">{{ itemTypeLabel(detail.itemType) }}</el-descriptions-item>
          <el-descriptions-item label="子项名称">{{ detail.itemName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="核销状态">
            <el-tag size="small" :type="statusType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="核销人">{{ writeoffUserText(detail) }}</el-descriptions-item>
          <el-descriptions-item label="核销时间">{{ parseTime(detail.writeoffTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="核销终端">{{ detail.writeoffTerminal || '--' }}</el-descriptions-item>
          <el-descriptions-item label="核销人数">{{ detail.writeoffQty || 0 }}</el-descriptions-item>
          <el-descriptions-item label="总人数">{{ detail.totalQty || 0 }}</el-descriptions-item>
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
import { listJst_appointment_writeoff_item, getJst_appointment_writeoff_item } from '@/api/jst/order/jst_appointment_writeoff_item'

const STATUS_MAP = {
  unused: { label: '未核销', type: 'info' },
  used: { label: '已核销', type: 'success' },
  expired: { label: '已过期', type: 'warning' },
  voided: { label: '已作废', type: 'danger' }
}

const ITEM_TYPE_MAP = {
  arrival: '到场',
  gift: '礼品',
  ceremony: '仪式',
  custom: '自定义'
}

export default {
  name: 'JstAppointmentWriteoffItemManage',
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
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        writeoffKeyword: '',
        appointmentKeyword: '',
        status: undefined,
        writeoffUserKeyword: ''
      },
      statusOptions: Object.keys(STATUS_MAP).map(key => ({ value: key, label: STATUS_MAP[key].label }))
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
        const writeoffKeyword = String(this.queryParams.writeoffKeyword || '').trim()
        const appointmentKeyword = String(this.queryParams.appointmentKeyword || '').trim()
        const writeoffUserKeyword = String(this.queryParams.writeoffUserKeyword || '').trim()
        const params = {
          pageNum: this.queryParams.pageNum,
          pageSize: this.queryParams.pageSize,
          status: this.queryParams.status || undefined
        }
        if (this.isNumericKeyword(appointmentKeyword)) {
          params.appointmentId = Number(appointmentKeyword)
        }
        if (this.isNumericKeyword(writeoffUserKeyword)) {
          params.writeoffUserId = Number(writeoffUserKeyword)
        }
        if (this.dateRange && this.dateRange.length === 2) {
          params.beginWriteoffTime = this.dateRange[0]
          params.endWriteoffTime = this.dateRange[1]
        }
        const res = await listJst_appointment_writeoff_item(params)
        const rows = res.rows || []
        const needLocalWriteoff = !!writeoffKeyword
        const needLocalAppointment = appointmentKeyword && !this.isNumericKeyword(appointmentKeyword)
        const needLocalUser = writeoffUserKeyword && !this.isNumericKeyword(writeoffUserKeyword)
        const needLocalDate = this.dateRange && this.dateRange.length === 2
        if (needLocalWriteoff || needLocalAppointment || needLocalUser || needLocalDate) {
          const writeoffLower = writeoffKeyword.toLowerCase()
          const appointmentLower = appointmentKeyword.toLowerCase()
          const userLower = writeoffUserKeyword.toLowerCase()
          this.list = rows.filter(row => {
            const writeoffMatch = !needLocalWriteoff || this.writeoffNoText(row).toLowerCase().indexOf(writeoffLower) > -1
            const appointmentMatch = !needLocalAppointment || this.appointmentNoText(row).toLowerCase().indexOf(appointmentLower) > -1
            const userMatch = !needLocalUser || this.writeoffUserText(row).toLowerCase().indexOf(userLower) > -1
            const dateMatch = this.matchDateRange(row.writeoffTime, this.dateRange)
            return writeoffMatch && appointmentMatch && userMatch && dateMatch
          })
          this.total = this.list.length
        } else {
          this.list = rows
          this.total = res.total || 0
        }
      } finally {
        this.loading = false
      }
    },
    matchDateRange(value, range) {
      if (!range || range.length !== 2) return true
      if (!value) return false
      const begin = new Date(range[0] + ' 00:00:00').getTime()
      const end = new Date(range[1] + ' 23:59:59').getTime()
      const current = new Date(value).getTime()
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
        writeoffKeyword: '',
        appointmentKeyword: '',
        status: undefined,
        writeoffUserKeyword: ''
      }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detailLoading = true
      try {
        const res = await getJst_appointment_writeoff_item(row.writeoffItemId)
        this.detail = res.data || null
      } finally {
        this.detailLoading = false
      }
    },
    goAppointment(row) {
      const appointmentId = row && row.appointmentId ? row.appointmentId : null
      if (!appointmentId) return
      this.$router.push({
        path: '/jst/order/jst_appointment_record',
        query: { appointmentId: String(appointmentId), autoOpen: '1' }
      })
    },
    writeoffNoText(row) {
      return row.writeoffNo || (row.writeoffItemId ? 'WO-' + row.writeoffItemId : '--')
    },
    appointmentNoText(row) {
      return row.appointmentNo || (row.appointmentId ? 'AP-' + row.appointmentId : '--')
    },
    writeoffUserText(row) {
      return row.writeoffUserName || (row.writeoffUserId ? 'ID ' + row.writeoffUserId : '--')
    },
    itemTypeLabel(value) {
      return ITEM_TYPE_MAP[value] || value || '--'
    },
    statusLabel(value) {
      return (STATUS_MAP[value] && STATUS_MAP[value].label) || value || '--'
    },
    statusType(value) {
      return (STATUS_MAP[value] && STATUS_MAP[value].type) || 'info'
    }
  }
}
</script>

<style scoped>
.writeoff-item-page {
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
  .writeoff-item-page {
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
