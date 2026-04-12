<template>
  <div class="app-container team-appointment-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">预约中心</p>
        <h2>团队预约管理</h2>
        <p class="hero-desc">支持团队名/赛事智能检索，展示成员数与预约状态，可在详情中查看团队成员签到信息。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="88px" class="query-panel">
      <el-form-item label="团队关键词">
        <el-input
          v-model="queryParams.teamKeyword"
          placeholder="团队名/团队号/团队ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛事关键词">
        <el-input
          v-model="queryParams.contestKeyword"
          placeholder="赛事名/赛事ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预约状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
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
        <div v-for="row in list" :key="row.teamAppointmentId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ teamNameText(row) }}</div>
              <div class="mobile-sub">{{ teamNoText(row) }}</div>
            </div>
            <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div class="mobile-info-row">
            <span>{{ contestText(row) }}</span>
            <span>{{ row.appointmentDate || '--' }}</span>
          </div>
          <div class="mobile-info-row">
            <span>成员 {{ row.memberPersons || 0 }}/{{ row.totalPersons || 0 }}</span>
            <span>已核销 {{ row.writeoffPersons || 0 }}</span>
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">查看详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无团队预约" :image-size="96" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="团队预约号" min-width="170" show-overflow-tooltip>
        <template slot-scope="scope">{{ teamNoText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="团队名称" min-width="180" show-overflow-tooltip>
        <template slot-scope="scope">{{ teamNameText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="赛事" min-width="180" show-overflow-tooltip>
        <template slot-scope="scope">{{ contestText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="预约日期" prop="appointmentDate" min-width="120" />
      <el-table-column label="场次/时段" prop="sessionCode" min-width="130" show-overflow-tooltip />
      <el-table-column label="成员数" min-width="110" align="center">
        <template slot-scope="scope">
          <span class="member-count">{{ scope.row.memberPersons || 0 }}/{{ scope.row.totalPersons || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="已核销" prop="writeoffPersons" min-width="90" align="center" />
      <el-table-column label="状态" min-width="140">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
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

    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '760px'" title="团队预约详情" append-to-body>
      <div v-loading="detailLoading" class="drawer-body">
        <el-descriptions v-if="detail" :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="团队预约ID">{{ detail.teamAppointmentId }}</el-descriptions-item>
          <el-descriptions-item label="团队预约号">{{ teamNoText(detail) }}</el-descriptions-item>
          <el-descriptions-item label="团队名称">{{ teamNameText(detail) }}</el-descriptions-item>
          <el-descriptions-item label="关联赛事">{{ contestText(detail) }}</el-descriptions-item>
          <el-descriptions-item label="预约日期">{{ detail.appointmentDate || '--' }}</el-descriptions-item>
          <el-descriptions-item label="场次/时段">{{ detail.sessionCode || '--' }}</el-descriptions-item>
          <el-descriptions-item label="渠道ID">{{ detail.channelId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag size="small" :type="statusType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="成员数">{{ detail.memberPersons || 0 }}/{{ detail.totalPersons || 0 }}</el-descriptions-item>
          <el-descriptions-item label="额外人数">{{ detail.extraPersons || 0 }}</el-descriptions-item>
          <el-descriptions-item label="已核销人数">{{ detail.writeoffPersons || 0 }}</el-descriptions-item>
          <el-descriptions-item label="额外名单">{{ extraListText(detail) }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ parseTime(detail.createTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ parseTime(detail.updateTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="isMobile ? 1 : 2">{{ detail.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
        <el-empty v-else description="暂无详情数据" :image-size="96" />

        <div class="section-title">团队成员</div>
        <el-table v-loading="memberLoading" :data="memberList" size="small" max-height="320">
          <el-table-column label="姓名" prop="nameSnapshot" min-width="120" show-overflow-tooltip />
          <el-table-column label="手机号" prop="mobileSnapshot" min-width="130" show-overflow-tooltip />
          <el-table-column label="签到状态" min-width="120">
            <template slot-scope="scope">
              <el-tag size="small" :type="memberStatusType(scope.row.writeoffStatus)">{{ memberStatusLabel(scope.row.writeoffStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="签到时间" min-width="160">
            <template slot-scope="scope">{{ memberSignTime(scope.row) }}</template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!memberLoading && !memberList.length" description="暂无团队成员" :image-size="80" />
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_team_appointment, getJst_team_appointment } from '@/api/jst/order/jst_team_appointment'
import { listJst_team_appointment_member } from '@/api/jst/order/jst_team_appointment_member'

const TEAM_STATUS_MAP = {
  booked: { label: '已预约', type: 'primary' },
  partial_writeoff: { label: '部分核销中', type: 'warning' },
  fully_writeoff: { label: '已全部核销', type: 'success' },
  partial_writeoff_ended: { label: '部分核销结束', type: 'warning' },
  no_show: { label: '未到场', type: 'danger' },
  cancelled: { label: '已取消', type: 'info' },
  expired: { label: '已过期', type: 'info' }
}

const MEMBER_STATUS_MAP = {
  unused: { label: '未签到', type: 'info' },
  used: { label: '已签到', type: 'success' },
  expired: { label: '已过期', type: 'warning' },
  voided: { label: '已作废', type: 'danger' }
}

export default {
  name: 'JstTeamAppointmentManage',
  data() {
    return {
      loading: false,
      detailLoading: false,
      memberLoading: false,
      isMobile: false,
      list: [],
      total: 0,
      dateRange: [],
      detailVisible: false,
      detail: null,
      memberList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        teamKeyword: '',
        contestKeyword: '',
        status: undefined
      },
      statusOptions: Object.keys(TEAM_STATUS_MAP).map(key => ({
        value: key,
        label: TEAM_STATUS_MAP[key].label
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
        const contestKeyword = String(this.queryParams.contestKeyword || '').trim()
        const params = {
          pageNum: this.queryParams.pageNum,
          pageSize: this.queryParams.pageSize,
          status: this.queryParams.status || undefined
        }

        if (teamKeyword) {
          params.teamName = teamKeyword
          if (this.isNumericKeyword(teamKeyword)) {
            params.teamAppointmentId = Number(teamKeyword)
          }
        }

        if (this.isNumericKeyword(contestKeyword)) {
          params.contestId = Number(contestKeyword)
        }

        if (this.dateRange && this.dateRange.length === 2) {
          params.beginAppointmentDate = this.dateRange[0]
          params.endAppointmentDate = this.dateRange[1]
        }

        const res = await listJst_team_appointment(params)
        const rows = res.rows || []
        const needLocalTeam = !!teamKeyword
        const needLocalContest = contestKeyword && !this.isNumericKeyword(contestKeyword)
        const needLocalDate = this.dateRange && this.dateRange.length === 2

        if (needLocalTeam || needLocalContest || needLocalDate) {
          const teamLower = teamKeyword.toLowerCase()
          const contestLower = contestKeyword.toLowerCase()
          this.list = rows.filter(row => {
            const teamMatch = !needLocalTeam || this.teamSearchText(row).toLowerCase().indexOf(teamLower) > -1
            const contestMatch = !needLocalContest || this.contestText(row).toLowerCase().indexOf(contestLower) > -1
            const dateMatch = this.matchDateRange(row.appointmentDate, this.dateRange)
            return teamMatch && contestMatch && dateMatch
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
      const current = new Date(value + ' 12:00:00').getTime()
      if (Number.isNaN(current)) return false
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
        teamKeyword: '',
        contestKeyword: '',
        status: undefined
      }
      this.getList()
    },
    async openDetail(row) {
      const id = row && row.teamAppointmentId ? row.teamAppointmentId : null
      if (!id) return
      this.detailVisible = true
      this.detailLoading = true
      this.memberLoading = true
      try {
        const [detailRes, memberRes] = await Promise.all([
          getJst_team_appointment(id),
          listJst_team_appointment_member({ pageNum: 1, pageSize: 200, teamAppointmentId: id })
        ])
        this.detail = detailRes.data || null
        this.memberList = memberRes.rows || []
      } finally {
        this.detailLoading = false
        this.memberLoading = false
      }
    },
    teamNoText(row) {
      return row.teamNo || (row.teamAppointmentId ? 'TEAM-' + row.teamAppointmentId : '--')
    },
    teamNameText(row) {
      return row.teamName || row.batchName || (row.teamAppointmentId ? '团队 #' + row.teamAppointmentId : '--')
    },
    teamSearchText(row) {
      return [
        this.teamNoText(row),
        this.teamNameText(row),
        row.teamAppointmentId,
        row.channelId
      ]
        .filter(Boolean)
        .join(' ')
    },
    contestText(row) {
      return row.contestName || row.contestTitle || (row.contestId ? '赛事 #' + row.contestId : '--')
    },
    extraListText(row) {
      const raw = row && row.extraListJson
      if (!raw) return '--'
      if (Array.isArray(raw)) return raw.join('、')
      if (typeof raw === 'string') {
        try {
          const parsed = JSON.parse(raw)
          if (Array.isArray(parsed)) return parsed.join('、')
        } catch (e) {
          // keep original string when parse failed
        }
        return raw
      }
      return '--'
    },
    statusLabel(value) {
      return (TEAM_STATUS_MAP[value] && TEAM_STATUS_MAP[value].label) || value || '--'
    },
    statusType(value) {
      return (TEAM_STATUS_MAP[value] && TEAM_STATUS_MAP[value].type) || 'info'
    },
    memberStatusLabel(value) {
      return (MEMBER_STATUS_MAP[value] && MEMBER_STATUS_MAP[value].label) || value || '--'
    },
    memberStatusType(value) {
      return (MEMBER_STATUS_MAP[value] && MEMBER_STATUS_MAP[value].type) || 'info'
    },
    memberSignTime(row) {
      return this.parseTime(row.writeoffTime || row.signInTime || row.updateTime) || '--'
    }
  }
}
</script>

<style scoped>
.team-appointment-page {
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

.member-count {
  font-weight: 700;
}

.drawer-body {
  padding: 0 24px 24px;
}

.section-title {
  margin: 18px 0 10px;
  font-weight: 700;
  color: #172033;
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
  .team-appointment-page {
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
