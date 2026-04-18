<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="待审核" value="pending_review" />
          <el-option label="已审核" value="approved" />
          <el-option label="已打款" value="paid" />
          <el-option label="已驳回" value="rejected" />
        </el-select>
      </el-form-item>
      <el-form-item label="结算月份" prop="period">
        <el-date-picker v-model="queryParams.period" type="month" value-format="yyyy-MM" placeholder="选择月份" clearable />
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
      <el-table-column label="结算单ID" prop="settlementId" width="100" />
      <el-table-column label="销售" min-width="130">
        <template slot-scope="scope">
          <entity-link entity="sales" :id="scope.row.salesId" :name="scope.row.salesName" />
        </template>
      </el-table-column>
      <el-table-column label="结算月份" prop="period" width="110" align="center" />
      <el-table-column label="成交笔数" prop="orderCount" width="90" align="center" />
      <el-table-column label="提成金额" prop="totalAmount" width="120" align="right">
        <template slot-scope="scope">¥{{ formatAmount(scope.row.totalAmount) }}</template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="100" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="statusTagType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="打款流水" prop="payVoucher" min-width="130" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.payVoucher || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="90" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-document" @click="goDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listSettlement } from '@/api/admin/sales/settlement'

export default {
  name: 'SalesSettlementList',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        status: undefined,
        period: undefined
      }
    }
  },
  created() {
    // 支持从销售列表跳转带 salesId 过滤
    if (this.$route.query.salesId) {
      this.queryParams.salesId = this.$route.query.salesId
    }
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listSettlement(this.queryParams).then(res => {
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
    statusTagType(s) {
      const m = { pending_review: 'warning', approved: 'primary', paid: 'success', rejected: 'danger' }
      return m[s] || 'info'
    },
    statusLabel(s) {
      const m = { pending_review: '待审核', approved: '已审核', paid: '已打款', rejected: '已驳回' }
      return m[s] || s || '-'
    },
    formatAmount(v) {
      if (v == null) return '0.00'
      return Number(v).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    },
    goDetail(row) {
      this.$router.push({ path: '/jst/sales/settlement/detail', query: { settlementId: row.settlementId } })
    }
  }
}
</script>

<style scoped>
@media (max-width: 768px) {
  .el-form--inline .el-form-item { display: block; margin-right: 0; }
}
</style>
