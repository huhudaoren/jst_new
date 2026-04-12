<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="持有者ID" prop="ownerId">
        <el-input v-model="queryParams.ownerId" placeholder="请输入持有者ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="权益模板ID" prop="rightsTemplateId">
        <el-input v-model="queryParams.rightsTemplateId" placeholder="请输入权益模板ID" clearable @keyup.enter.native="handleQuery" />
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
            <span class="mobile-card__title">权益 #{{ row.userRightsId }}</span>
            <el-tag size="mini" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>用户：{{ row.ownerId || '--' }}</span>
            <span>模板：{{ row.rightsTemplateId || '--' }}</span>
            <span>剩余：{{ row.remainQuota || 0 }}</span>
            <span>有效至：{{ parseTime(row.validEnd, '{y}-{m}-{d}') || '--' }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无用户权益" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="用户权益ID" prop="userRightsId" width="110" />
      <el-table-column label="权益模板ID" prop="rightsTemplateId" min-width="110" />
      <el-table-column label="持有者" min-width="130">
        <template slot-scope="{ row }">{{ row.ownerType || '--' }} / {{ row.ownerId || '--' }}</template>
      </el-table-column>
      <el-table-column label="来源" min-width="110">
        <template slot-scope="{ row }">{{ row.sourceType || '--' }}</template>
      </el-table-column>
      <el-table-column label="剩余额度" prop="remainQuota" min-width="100" align="right" />
      <el-table-column label="状态" width="100">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="生效时间" min-width="160">
        <template slot-scope="{ row }">{{ parseTime(row.validStart) || '--' }}</template>
      </el-table-column>
      <el-table-column label="失效时间" min-width="160">
        <template slot-scope="{ row }">{{ parseTime(row.validEnd) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="90">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-drawer :title="'权益详情 #' + (detailData.userRightsId || '')" :visible.sync="detailVisible" :size="isMobile ? '100%' : '560px'" append-to-body>
      <div class="detail-body">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="权益ID">{{ detailData.userRightsId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="模板ID">{{ detailData.rightsTemplateId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="持有者">{{ detailData.ownerType || '--' }} / {{ detailData.ownerId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="来源">{{ detailData.sourceType || '--' }} / {{ detailData.sourceRefId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="剩余额度">{{ detailData.remainQuota || 0 }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag size="small" :type="statusType(detailData.status)">{{ statusLabel(detailData.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="有效期">{{ parseTime(detailData.validStart) || '--' }} ~ {{ parseTime(detailData.validEnd) || '--' }}</el-descriptions-item>
        </el-descriptions>

        <div class="drawer-block-title">核销记录</div>
        <el-table v-loading="writeoffLoading" :data="writeoffList" size="mini" max-height="260">
          <el-table-column label="核销单号" prop="writeoffNo" min-width="120" show-overflow-tooltip />
          <el-table-column label="状态" min-width="90">
            <template slot-scope="{ row }">{{ row.status || '--' }}</template>
          </el-table-column>
          <el-table-column label="使用额度" prop="useAmount" min-width="90" align="right" />
          <el-table-column label="核销时间" min-width="130">
            <template slot-scope="{ row }">{{ parseTime(row.writeoffTime) || '--' }}</template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!writeoffLoading && !writeoffList.length" :image-size="80" description="暂无核销记录" />
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'
import { listJst_user_rights, getJst_user_rights } from '@/api/jst/marketing/jst_user_rights'
import { listJst_rights_writeoff_record } from '@/api/jst/marketing/jst_rights_writeoff_record'

const STATUS_META = {
  active: { label: '生效', type: 'success' },
  available: { label: '生效', type: 'success' },
  expired: { label: '已过期', type: 'info' },
  exhausted: { label: '已耗尽', type: 'danger' },
  used: { label: '已耗尽', type: 'danger' },
  revoked: { label: '已失效', type: 'danger' },
  locked: { label: '锁定中', type: 'warning' }
}

export default {
  name: 'JstUserRights',
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
        rightsTemplateId: null,
        status: null
      },
      detailVisible: false,
      detailData: {},
      writeoffLoading: false,
      writeoffList: [],
      statusOptions: [
        { label: '生效', value: 'available' },
        { label: '已过期', value: 'expired' },
        { label: '已耗尽', value: 'used' },
        { label: '锁定中', value: 'locked' },
        { label: '已失效', value: 'revoked' }
      ]
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
    getList() {
      this.loading = true
      listJst_user_rights(this.queryParams).then(res => {
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
      getJst_user_rights(row.userRightsId).then(res => {
        this.detailData = res.data || res || row
        this.detailVisible = true
        this.getWriteoffList(row.userRightsId)
      })
    },
    getWriteoffList(userRightsId) {
      this.writeoffLoading = true
      listJst_rights_writeoff_record({
        pageNum: 1,
        pageSize: 20,
        userRightsId: userRightsId
      }).then(res => {
        this.writeoffList = res.rows || []
      }).finally(() => {
        this.writeoffLoading = false
      })
    }
  }
}
</script>

<style scoped>
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
