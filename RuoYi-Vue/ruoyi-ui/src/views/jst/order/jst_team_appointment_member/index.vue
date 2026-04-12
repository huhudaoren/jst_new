<template>
  <div class="app-container team-member-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">预约中心</p>
        <h2>团队预约成员管理</h2>
        <p class="hero-desc">支持团队/成员名智能检索，展示签到状态，支持详情抽屉查看成员信息。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="88px" class="query-panel">
      <el-form-item label="团队关键词">
        <el-input
          v-model="queryParams.teamKeyword"
          placeholder="团队名/团队ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="成员关键词">
        <el-input
          v-model="queryParams.memberKeyword"
          placeholder="成员名/成员编号/ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="签到状态">
        <el-select v-model="queryParams.writeoffStatus" placeholder="全部" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.memberId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ memberNameText(row) }}</div>
              <div class="mobile-sub">{{ teamText(row) }}</div>
            </div>
            <el-tag size="small" :type="statusType(row.writeoffStatus)">{{ statusLabel(row.writeoffStatus) }}</el-tag>
          </div>
          <div class="mobile-info-row">
            <span>成员编号 {{ row.memberNo || '--' }}</span>
            <span>{{ row.mobileSnapshot || '--' }}</span>
          </div>
          <div class="mobile-info-row">
            <span>签到时间 {{ signTimeText(row) }}</span>
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">查看详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无成员记录" :image-size="96" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="成员编号" prop="memberNo" min-width="130" show-overflow-tooltip />
      <el-table-column label="成员姓名" min-width="120" show-overflow-tooltip>
        <template slot-scope="scope">{{ memberNameText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="手机号" prop="mobileSnapshot" min-width="130" show-overflow-tooltip />
      <el-table-column label="团队" min-width="170" show-overflow-tooltip>
        <template slot-scope="scope">{{ teamText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="参赛人ID" prop="participantId" min-width="110" />
      <el-table-column label="子订单ID" prop="subOrderId" min-width="110" />
      <el-table-column label="签到状态" min-width="120">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.writeoffStatus)">{{ statusLabel(scope.row.writeoffStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="签到时间" min-width="160">
        <template slot-scope="scope">{{ signTimeText(scope.row) }}</template>
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

    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '680px'" title="成员详情" append-to-body>
      <div v-loading="detailLoading" class="drawer-body">
        <el-descriptions v-if="detail" :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="成员ID">{{ detail.memberId }}</el-descriptions-item>
          <el-descriptions-item label="成员编号">{{ detail.memberNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="成员姓名">{{ memberNameText(detail) }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detail.mobileSnapshot || '--' }}</el-descriptions-item>
          <el-descriptions-item label="团队">{{ teamText(detail) }}</el-descriptions-item>
          <el-descriptions-item label="团队ID">{{ detail.teamAppointmentId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="正式用户ID">{{ detail.userId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="参赛人ID">{{ detail.participantId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="子订单ID">{{ detail.subOrderId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="签到状态">
            <el-tag size="small" :type="statusType(detail.writeoffStatus)">{{ statusLabel(detail.writeoffStatus) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="签到时间">{{ signTimeText(detail) }}</el-descriptions-item>
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
import { listJst_team_appointment_member, getJst_team_appointment_member } from '@/api/jst/order/jst_team_appointment_member'

const MEMBER_STATUS_MAP = {
  unused: { label: '未签到', type: 'info' },
  used: { label: '已签到', type: 'success' },
  expired: { label: '已过期', type: 'warning' },
  voided: { label: '已作废', type: 'danger' }
}

export default {
  name: 'JstTeamAppointmentMemberManage',
  data() {
    return {
      loading: false,
      detailLoading: false,
      isMobile: false,
      list: [],
      total: 0,
      detailVisible: false,
      detail: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        teamKeyword: '',
        memberKeyword: '',
        writeoffStatus: undefined
      },
      statusOptions: Object.keys(MEMBER_STATUS_MAP).map(key => ({
        value: key,
        label: MEMBER_STATUS_MAP[key].label
      }))
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
        const teamKeyword = String(this.queryParams.teamKeyword || '').trim()
        const memberKeyword = String(this.queryParams.memberKeyword || '').trim()
        const params = {
          pageNum: this.queryParams.pageNum,
          pageSize: this.queryParams.pageSize,
          writeoffStatus: this.queryParams.writeoffStatus || undefined
        }

        if (teamKeyword && this.isNumericKeyword(teamKeyword)) {
          params.teamAppointmentId = Number(teamKeyword)
        }

        if (memberKeyword) {
          params.nameSnapshot = memberKeyword
          if (this.isNumericKeyword(memberKeyword)) {
            params.participantId = Number(memberKeyword)
          }
        }

        const res = await listJst_team_appointment_member(params)
        const rows = res.rows || []
        const needLocalTeam = !!teamKeyword
        const needLocalMember = !!memberKeyword

        if (needLocalTeam || needLocalMember) {
          const teamLower = teamKeyword.toLowerCase()
          const memberLower = memberKeyword.toLowerCase()
          this.list = rows.filter(row => {
            const teamMatch = !needLocalTeam || this.teamText(row).toLowerCase().indexOf(teamLower) > -1
            const memberMatch = !needLocalMember || this.memberSearchText(row).toLowerCase().indexOf(memberLower) > -1
            return teamMatch && memberMatch
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
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        teamKeyword: '',
        memberKeyword: '',
        writeoffStatus: undefined
      }
      this.getList()
    },
    async openDetail(row) {
      const id = row && row.memberId ? row.memberId : null
      if (!id) return
      this.detailVisible = true
      this.detailLoading = true
      try {
        const res = await getJst_team_appointment_member(id)
        this.detail = res.data || null
      } finally {
        this.detailLoading = false
      }
    },
    memberNameText(row) {
      return row.nameSnapshot || (row.participantId ? '成员 #' + row.participantId : '--')
    },
    teamText(row) {
      return row.teamName || (row.teamAppointmentId ? '团队 #' + row.teamAppointmentId : '--')
    },
    memberSearchText(row) {
      return [
        row.nameSnapshot,
        row.memberNo,
        row.mobileSnapshot,
        row.participantId,
        row.userId,
        row.subOrderId
      ]
        .filter(Boolean)
        .join(' ')
    },
    signTimeText(row) {
      return this.parseTime(row.writeoffTime || row.signInTime || row.updateTime) || '--'
    },
    statusLabel(value) {
      return (MEMBER_STATUS_MAP[value] && MEMBER_STATUS_MAP[value].label) || value || '--'
    },
    statusType(value) {
      return (MEMBER_STATUS_MAP[value] && MEMBER_STATUS_MAP[value].type) || 'info'
    }
  }
}
</script>

<style scoped>
.team-member-page {
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
  .team-member-page {
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
