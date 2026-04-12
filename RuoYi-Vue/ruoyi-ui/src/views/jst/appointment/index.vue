<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
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
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" v-hasPermi="['jst:order:appointment_record:export']" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="ID" prop="appointmentId" width="70" />
      <el-table-column label="预约号" prop="appointmentNo" width="140" show-overflow-tooltip />
      <el-table-column label="赛事名称" prop="contestName" min-width="150" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.contestName || scope.row.contestId }}</template>
      </el-table-column>
      <el-table-column label="参赛人" prop="participantName" min-width="100" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.participantName || scope.row.participantId }}</template>
      </el-table-column>
      <el-table-column label="预约日期" prop="appointmentDate" width="110" align="center" />
      <el-table-column label="时段" prop="sessionCode" width="100" align="center">
        <template slot-scope="scope">{{ scope.row.sessionCode || '-' }}</template>
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

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer title="预约详情" :visible.sync="detailOpen" :size="drawerSize" direction="rtl">
      <div style="padding: 20px;" v-if="detailData">
        <el-descriptions :column="1" border size="medium">
          <el-descriptions-item label="预约ID">{{ detailData.appointmentId }}</el-descriptions-item>
          <el-descriptions-item label="预约号">{{ detailData.appointmentNo }}</el-descriptions-item>
          <el-descriptions-item label="赛事名称">{{ detailData.contestName || detailData.contestId }}</el-descriptions-item>
          <el-descriptions-item label="参赛人">{{ detailData.participantName || detailData.participantId }}</el-descriptions-item>
          <el-descriptions-item label="预约日期">{{ detailData.appointmentDate }}</el-descriptions-item>
          <el-descriptions-item label="时段">{{ detailData.sessionCode || '-' }}</el-descriptions-item>
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
  computed: {
    drawerSize() {
      return window.innerWidth <= 768 ? '100%' : '500px'
    }
  },
  created() {
    this.getList()
  },
  methods: {
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
      return map[status] || status || '-'
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
      this.$confirm('确认取消预约「' + (row.appointmentNo || row.appointmentId) + '」？取消后不可恢复。', '取消确认', { type: 'warning' }).then(() => {
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
