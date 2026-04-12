<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="权益名称" prop="rightsName">
        <el-input v-model="queryParams.rightsName" placeholder="请输入权益名称" clearable @keyup.enter.native="handleQuery" />
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
        <div v-for="row in list" :key="row.userRightsId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.rightsName || '--' }}</span>
            <el-tag size="mini" :type="rightsStatusType(row.status)">{{ row.status || '--' }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>用户：{{ row.userName || row.userId || '--' }}</span>
            <span>剩余：{{ row.remainQuota != null ? row.remainQuota : '--' }}</span>
            <span>有效至：{{ parseTime(row.validEnd, '{y}-{m}-{d}') || '--' }}</span>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无已发权益" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="userRightsId" width="70" />
      <el-table-column label="用户" min-width="120">
        <template slot-scope="{ row }">{{ row.userName || row.userId || '--' }}</template>
      </el-table-column>
      <el-table-column label="权益名称" prop="rightsName" min-width="160" show-overflow-tooltip />
      <el-table-column label="剩余配额" prop="remainQuota" width="90" />
      <el-table-column label="状态" width="100">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="rightsStatusType(row.status)">{{ row.status || '--' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="有效期至" width="160">
        <template slot-scope="{ row }">{{ parseTime(row.validEnd) }}</template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listUserRights } from '@/api/jst/rights'
import { parseTime } from '@/utils/ruoyi'

export default {
  name: 'JstRightsIssued',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, rightsName: null, status: null },
      statusOptions: [
        { label: '有效', value: 'active' },
        { label: '已用完', value: 'exhausted' },
        { label: '已过期', value: 'expired' }
      ]
    }
  },
  computed: {
    isMobile() { return this.$store.state.app.device === 'mobile' }
  },
  created() { this.getList() },
  methods: {
    parseTime,
    rightsStatusType(s) {
      const map = { active: 'success', exhausted: 'info', expired: 'info' }
      return map[s] || 'info'
    },
    getList() {
      this.loading = true
      listUserRights(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.$refs.queryForm && this.$refs.queryForm.resetFields(); this.handleQuery() }
  }
}
</script>

<style scoped>
.mobile-card-list { padding: 0 4px; }
.mobile-card { background: #fff; border-radius: 8px; padding: 12px 14px; margin-bottom: 10px; box-shadow: 0 1px 4px rgba(0,0,0,.06); }
.mobile-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.mobile-card__title { font-weight: 600; font-size: 14px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 65%; }
.mobile-card__meta { font-size: 12px; color: #909399; display: flex; gap: 12px; margin-bottom: 8px; flex-wrap: wrap; }
</style>
