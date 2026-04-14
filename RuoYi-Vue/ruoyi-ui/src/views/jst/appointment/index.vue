<template>
  <div class="app-container appointment-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">预约中心</p>
        <h2>预约管理</h2>
        <p class="hero-desc">管理赛事预约时间段，查看预约记录与核销状态，支持取消操作。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px" class="query-panel">
      <el-form-item label="赛事名称" prop="contestName">
        <el-input v-model="queryParams.contestName" placeholder="请输入赛事名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="参赛人" prop="participantName">
        <el-input v-model="queryParams.participantName" placeholder="请输入参赛人姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="mainStatus">
        <el-select v-model="queryParams.mainStatus" placeholder="全部" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="预约日期" prop="appointmentDate">
        <el-date-picker v-model="queryParams.appointmentDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" v-hasPermi="['jst:order:appointment_record:export']" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 移动端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.appointmentId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.contestName || row.contestId }}</div>
              <div class="mobile-sub">预约号 {{ row.appointmentNo || '--' }}</div>
            </div>
            <el-tag size="mini" :type="apptStatusType(row.mainStatus)">{{ apptStatusLabel(row.mainStatus) }}</el-tag>
          </div>
          <div class="mobile-info-row">
            <span>参赛人：{{ row.participantName || row.participantId || '--' }}</span>
            <span>日期：{{ row.appointmentDate || '--' }}</span>
            <span>时段：{{ row.sessionCode || '--' }}</span>
          </div>
          <div class="mobile-actions">
            <el-button type="text" size="mini" icon="el-icon-view" @click="handleDetail(row)">详情</el-button>
            <el-button type="text" size="mini" icon="el-icon-circle-close" v-if="canCancel(row.mainStatus)" v-hasPermi="['jst:order:appointment_record:remove']" @click="handleCancel(row)">取消</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无预约记录" />
    </div>

    <!-- PC 表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="预约号" prop="appointmentNo" min-width="140" show-overflow-tooltip />
      <el-table-column label="赛事名称" prop="contestName" min-width="150" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.contestName || scope.row.contestId }}</template>
      </el-table-column>
      <el-table-column label="参赛人" prop="participantName" min-width="100" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.participantName || scope.row.participantId }}</template>
      </el-table-column>
      <el-table-column label="预约日期" prop="appointmentDate" width="110" align="center" />
      <el-table-column label="时段" prop="sessionCode" width="100" align="center">
        <template slot-scope="scope">{{ scope.row.sessionCode || '--' }}</template>
      </el-table-column>
      <el-table-column label="状态" prop="mainStatus" width="100" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="apptStatusType(scope.row.mainStatus)">{{ apptStatusLabel(scope.row.mainStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-circle-close" v-if="canCancel(scope.row.mainStatus)" v-hasPermi="['jst:order:appointment_record:remove']" @click="handleCancel(scope.row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!isMobile && !loading && list.length === 0" description="暂无预约记录" />

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer title="预约详情" :visible.sync="detailOpen" :size="isMobile ? '100%' : '500px'" direction="rtl">
      <div class="drawer-body" v-if="detailData">
        <el-descriptions :column="1" border size="medium">
          <el-descriptions-item label="预约号">{{ detailData.appointmentNo }}</el-descriptions-item>
          <el-descriptions-item label="赛事名称">{{ detailData.contestName || detailData.contestId }}</el-descriptions-item>
          <el-descriptions-item label="参赛人">{{ detailData.participantName || detailData.participantId }}</el-descriptions-item>
          <el-descriptions-item label="预约日期">{{ detailData.appointmentDate }}</el-descriptions-item>
          <el-descriptions-item label="时段">{{ detailData.sessionCode || '--' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag size="mini" :type="apptStatusType(detailData.mainStatus)">{{ apptStatusLabel(detailData.mainStatus) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="关联订单" v-if="detailData.orderId">{{ detailData.orderId }}</el-descriptions-item>
          <el-descriptions-item label="渠道方" v-if="detailData.channelId">{{ detailData.channelName || detailData.channelId }}</el-descriptions-item>
          <el-descriptions-item label="核销状态" v-if="detailData.writeoffStatus">{{ detailData.writeoffStatus }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ parseTime(detailData.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="备注" v-if="detailData.remark">{{ detailData.remark }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listAppointment, getAppointment, delAppointment } from '@/api/admin/appointment'

export default {
  name: 'AppointmentManage',
  data() {
    return {
      loading: false,
      showSearch: true,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestName: undefined,
        participantName: undefined,
        mainStatus: undefined,
        appointmentDate: undefined
      },
      statusOptions: [
        { label: '待确认', value: 'pending' },
        { label: '已确认', value: 'confirmed' },
        { label: '已完成', value: 'completed' },
        { label: '已取消', value: 'cancelled' },
        { label: '已过期', value: 'expired' }
      ],
      detailOpen: false,
      detailData: null
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
    getList() {
      this.loading = true
      listAppointment(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    apptStatusType(status) {
      const map = { pending: 'warning', confirmed: '', completed: 'success', cancelled: 'info', expired: 'info' }
      return map[status] || 'info'
    },
    apptStatusLabel(status) {
      const map = { pending: '待确认', confirmed: '已确认', completed: '已完成', cancelled: '已取消', expired: '已过期' }
      return map[status] || status || '--'
    },
    canCancel(status) {
      return status === 'pending' || status === 'confirmed'
    },
    handleDetail(row) {
      this.detailData = row
      this.detailOpen = true
      getAppointment(row.appointmentId).then(res => {
        this.detailData = res.data || row
      }).catch(() => {})
    },
    handleCancel(row) {
      this.$modal.confirm('确认取消预约「' + (row.appointmentNo || row.appointmentId) + '」？取消后不可恢复。').then(() => {
        return delAppointment(row.appointmentId)
      }).then(() => {
        this.$modal.msgSuccess('已取消')
        this.getList()
      }).catch(() => {})
    },
    handleExport() {
      this.download('/jst/order/jst_appointment_record/export', this.queryParams, '预约记录.xlsx')
    }
  }
}
</script>

<style scoped>
.appointment-page {
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
  .appointment-page {
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
