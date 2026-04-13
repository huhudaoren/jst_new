<template>
  <div class="app-container jst-admin-participant-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">档案中心</p>
        <h2>参赛档案管理</h2>
        <p class="hero-desc">管理临时档案认领关系，支持人工认领、撤销与详情追溯。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="76px"
      class="query-panel"
    >
      <el-form-item label="姓名" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="queryParams.mobile" placeholder="请输入监护人手机号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="认领状态" prop="claimStatus">
        <el-select v-model="queryParams.claimStatus" placeholder="全部" clearable>
          <el-option v-for="item in claimStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="participantList.length">
        <div v-for="row in participantList" :key="row.participantId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.name || '--' }}</div>
              <div class="mobile-sub">PID: {{ row.participantId }}</div>
            </div>
            <JstStatusBadge :status="String(row.claimStatus || '')" :status-map="claimStatusMap" />
          </div>
          <div class="mobile-info-row">年龄：{{ row.age || '--' }}</div>
          <div class="mobile-info-row">学校：{{ row.school || '--' }}</div>
          <div class="mobile-info-row">
            认领用户：
            <el-link v-if="canGoClaimUser(row)" type="primary" :underline="false" @click="goClaimUser(row)">
              {{ getClaimUser(row) }}
            </el-link>
            <span v-else>{{ getClaimUser(row) }}</span>
          </div>
          <div class="mobile-info-row">渠道：{{ getChannelName(row) }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
            <el-button
              v-if="canManualClaim(row)"
              type="text"
              v-hasPermi="['jst:user:participant:claim']"
              @click="openClaimDialog(row)"
            >
              手动认领
            </el-button>
            <el-button
              v-if="canRevoke(row)"
              type="text"
              class="danger-text"
              v-hasPermi="['jst:user:participant:claim']"
              @click="handleRevoke(row)"
            >
              撤销认领
            </el-button>
          </div>
        </div>
      </div>
      <JstEmptyState v-else description="暂无档案数据" />
    </div>

    <el-table v-else v-loading="loading" :data="participantList">
      <el-table-column label="档案ID" prop="participantId" min-width="88" />
      <el-table-column label="姓名" prop="name" min-width="120" show-overflow-tooltip />
      <el-table-column label="年龄" prop="age" min-width="72" />
      <el-table-column label="学校" prop="school" min-width="180" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.school || '--' }}</template>
      </el-table-column>
      <el-table-column label="认领状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.claimStatus || '')" :status-map="claimStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="认领用户" min-width="110">
        <template slot-scope="scope">
          <el-link v-if="canGoClaimUser(scope.row)" type="primary" :underline="false" @click="goClaimUser(scope.row)">
            {{ getClaimUser(scope.row) }}
          </el-link>
          <span v-else>{{ getClaimUser(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="渠道" min-width="130">
        <template slot-scope="scope">{{ getChannelName(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="操作" min-width="210" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
          <el-button
            v-if="canManualClaim(scope.row)"
            type="text"
            v-hasPermi="['jst:user:participant:claim']"
            @click="openClaimDialog(scope.row)"
          >
            手动认领
          </el-button>
          <el-button
            v-if="canRevoke(scope.row)"
            type="text"
            class="danger-text"
            v-hasPermi="['jst:user:participant:claim']"
            @click="handleRevoke(scope.row)"
          >
            撤销认领
          </el-button>
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

    <el-dialog title="手动认领档案" :visible.sync="claimVisible" width="420px" append-to-body>
      <el-form ref="claimFormRef" :model="claimForm" :rules="claimRules" label-width="88px">
        <el-form-item label="档案ID">
          <span>{{ claimForm.participantId || '--' }}</span>
        </el-form-item>
        <el-form-item label="认领用户ID" prop="userId">
          <el-input-number v-model="claimForm.userId" :min="1" controls-position="right" class="full-width" />
        </el-form-item>
        <el-form-item label="认领原因" prop="reason">
          <el-input
            v-model="claimForm.reason"
            type="textarea"
            :rows="4"
            maxlength="120"
            show-word-limit
            placeholder="请输入手动认领原因"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="claimVisible = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="submitClaim">确定</el-button>
      </div>
    </el-dialog>

    <el-drawer
      title="档案详情"
      :visible.sync="detailVisible"
      :size="isMobile ? '100%' : '760px'"
      append-to-body
    >
      <div v-loading="detailLoading" class="detail-wrap">
        <template v-if="detailData">
          <el-descriptions :column="isMobile ? 1 : 2" border>
            <el-descriptions-item label="档案ID">{{ detailData.participantId }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ detailData.name || '--' }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ detailData.age || '--' }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ genderLabel(detailData.gender) }}</el-descriptions-item>
            <el-descriptions-item label="监护人">{{ detailData.guardianName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="监护人手机号">{{ detailData.guardianMobile || '--' }}</el-descriptions-item>
            <el-descriptions-item label="学校">{{ detailData.school || '--' }}</el-descriptions-item>
            <el-descriptions-item label="机构">{{ detailData.organization || '--' }}</el-descriptions-item>
            <el-descriptions-item label="班级">{{ detailData.className || '--' }}</el-descriptions-item>
            <el-descriptions-item label="认领状态">
              <JstStatusBadge :status="String(detailData.claimStatus || '')" :status-map="claimStatusMap" />
            </el-descriptions-item>
            <el-descriptions-item label="认领用户">
              <el-link v-if="canGoClaimUser(detailData)" type="primary" :underline="false" @click="goClaimUser(detailData)">
                {{ getClaimUser(detailData) }}
              </el-link>
              <span v-else>{{ getClaimUser(detailData) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="认领时间">{{ parseTime(detailData.claimedTime) || '--' }}</el-descriptions-item>
          </el-descriptions>

          <div class="section-title">认领映射记录</div>
          <el-table v-if="detailMaps.length" :data="detailMaps" size="small" max-height="260">
            <el-table-column label="映射ID" prop="mapId" width="90" />
            <el-table-column label="认领用户" width="140" show-overflow-tooltip>
              <template slot-scope="scope">
                <el-link v-if="canGoClaimUser(scope.row)" type="primary" :underline="false" @click="goClaimUser(scope.row)">
                  {{ getClaimUser(scope.row) }}
                </el-link>
                <span v-else>{{ getClaimUser(scope.row) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="认领方式" prop="claimMethod" min-width="120" />
            <el-table-column label="状态" min-width="100">
              <template slot-scope="scope">
                <JstStatusBadge :status="scope.row.status" :status-map="mapStatusMap" />
              </template>
            </el-table-column>
            <el-table-column label="认领时间" min-width="150">
              <template slot-scope="scope">{{ parseTime(scope.row.claimTime) || '--' }}</template>
            </el-table-column>
          </el-table>
          <JstEmptyState v-else description="暂无认领映射" />
        </template>
        <JstEmptyState v-else description="暂无档案详情" />
      </div>
    </el-drawer>
  </div>
</template>

<script>
import {
  claimParticipantByAdmin,
  getAdminParticipant,
  listAdminParticipants,
  listParticipantUserMaps,
  revokeParticipantClaim
} from '@/api/jst/user/admin-participant'

export default {
  name: 'JstAdminParticipantIndex',
  data() {
    return {
      loading: false,
      actionLoading: false,
      detailLoading: false,
      total: 0,
      participantList: [],
      participantMapById: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        mobile: undefined,
        claimStatus: undefined
      },
      claimVisible: false,
      claimForm: {
        participantId: null,
        userId: null,
        reason: ''
      },
      claimRules: {
        userId: [{ required: true, message: '请输入认领用户ID', trigger: 'change' }],
        reason: [{ required: true, message: '请输入认领原因', trigger: 'blur' }]
      },
      detailVisible: false,
      detailData: null,
      detailMaps: [],
      claimStatusMap: {
        unclaimed: { label: '未认领', type: 'info' },
        auto_claimed: { label: '自动认领', type: 'success' },
        manual_claimed: { label: '人工认领', type: 'primary' },
        pending_manual: { label: '待人工认领', type: 'warning' }
      },
      mapStatusMap: {
        active: { label: '生效', type: 'success' },
        revoked: { label: '已撤销', type: 'danger' }
      },
      claimStatusOptions: [
        { label: '未认领', value: 'unclaimed' },
        { label: '自动认领', value: 'auto_claimed' },
        { label: '人工认领', value: 'manual_claimed' },
        { label: '待人工认领', value: 'pending_manual' }
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
    async getList() {
      this.loading = true
      try {
        const params = {
          ...this.queryParams,
          guardianMobile: this.queryParams.mobile
        }
        delete params.mobile
        const [listRes, mapRes] = await Promise.all([
          listAdminParticipants(params),
          listParticipantUserMaps({ pageNum: 1, pageSize: 500 })
        ])
        this.participantList = Array.isArray(listRes.rows) ? listRes.rows : []
        this.total = Number(listRes.total || 0)
        this.participantMapById = this.buildParticipantMap(Array.isArray(mapRes.rows) ? mapRes.rows : [])
      } catch (e) {
        this.participantList = []
        this.total = 0
        this.participantMapById = {}
      } finally {
        this.loading = false
      }
    },
    buildParticipantMap(rows) {
      return rows.reduce((acc, item) => {
        if (!item || !item.participantId) return acc
        if (!acc[item.participantId] || item.status === 'active') {
          acc[item.participantId] = item
        }
        return acc
      }, {})
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.pageNum = 1
      this.getList()
    },
    canManualClaim(row) {
      return ['unclaimed', 'pending_manual'].includes(row.claimStatus)
    },
    canRevoke(row) {
      return ['auto_claimed', 'manual_claimed'].includes(row.claimStatus)
    },
    getClaimUserName(row) {
      if (!row) return ''
      return row.claimUserName || row.claimedUserName || row.userName || ''
    },
    getClaimUserId(row) {
      if (!row) return null
      const map = row.participantId ? (this.participantMapById[row.participantId] || {}) : {}
      return row.claimedUserId || row.userId || map.userId || null
    },
    getClaimUser(row) {
      const name = this.getClaimUserName(row)
      const userId = this.getClaimUserId(row)
      return name || userId || '--'
    },
    canGoClaimUser(row) {
      return Boolean(this.getClaimUserId(row) && this.getClaimUserName(row))
    },
    goClaimUser(row) {
      const userId = this.getClaimUserId(row)
      if (!userId) return
      this.$router.push({
        path: '/jst/user',
        query: { userId: String(userId), autoOpen: '1' }
      }).catch(() => {})
    },
    getChannelName(row) {
      if (row.channelName) return row.channelName
      if (row.organization) return row.organization
      if (row.createdByChannelName) return row.createdByChannelName
      if (row.createdByChannelId) return '渠道ID:' + row.createdByChannelId
      return '--'
    },
    openClaimDialog(row) {
      this.claimForm = {
        participantId: row.participantId,
        userId: row.claimedUserId || row.userId || null,
        reason: '后台人工认领'
      }
      this.claimVisible = true
      this.$nextTick(() => {
        if (this.$refs.claimFormRef) this.$refs.claimFormRef.clearValidate()
      })
    },
    submitClaim() {
      this.$refs.claimFormRef.validate(async valid => {
        if (!valid) return
        this.actionLoading = true
        try {
          await claimParticipantByAdmin({ ...this.claimForm })
          this.$modal.msgSuccess('认领成功')
          this.claimVisible = false
          this.getList()
        } finally {
          this.actionLoading = false
        }
      })
    },
    handleRevoke(row) {
      this.$prompt('请输入撤销原因', '撤销认领', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '撤销原因不能为空'
      }).then(async({ value }) => {
        await revokeParticipantClaim(row.participantId, value)
        this.$modal.msgSuccess('撤销成功')
        this.getList()
      }).catch(() => {})
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detailLoading = true
      try {
        const [detailRes, mapsRes] = await Promise.all([
          getAdminParticipant(row.participantId),
          listParticipantUserMaps({ pageNum: 1, pageSize: 50, participantId: row.participantId })
        ])
        this.detailData = detailRes.data || null
        this.detailMaps = Array.isArray(mapsRes.rows) ? mapsRes.rows : []
      } catch (e) {
        this.detailData = null
        this.detailMaps = []
      } finally {
        this.detailLoading = false
      }
    },
    genderLabel(gender) {
      if (Number(gender) === 1) return '男'
      if (Number(gender) === 2) return '女'
      return '--'
    }
  }
}
</script>

<style scoped>
.jst-admin-participant-page {
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

.full-width {
  width: 100%;
}

.danger-text {
  color: #f56c6c;
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
  margin-top: 8px;
  color: #7a8495;
  font-size: 13px;
}

.mobile-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: 12px;
}

.detail-wrap {
  padding: 0 16px 16px;
}

.section-title {
  margin: 20px 0 10px;
  font-size: 15px;
  font-weight: 700;
  color: #172033;
}

@media (max-width: 768px) {
  .jst-admin-participant-page {
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

  .mobile-actions .el-button {
    min-height: 44px;
  }
}
</style>
