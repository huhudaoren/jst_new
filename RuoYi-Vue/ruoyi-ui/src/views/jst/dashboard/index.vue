<template>
  <div class="app-container admin-dashboard">
    <!-- Hero Banner -->
    <div class="hero-banner">
      <div>
        <p class="hero-eyebrow">平台管理中心</p>
        <h2>运营数据总览</h2>
        <p class="hero-desc">聚合平台核心指标与待办事项，快速掌握运营全局。</p>
      </div>
      <el-button type="primary" plain icon="el-icon-refresh" @click="initPage">刷新数据</el-button>
    </div>

    <!-- KPI 卡片 -->
    <el-row :gutter="20" class="section-row">
      <el-col v-for="item in kpiCards" :key="item.key" :xs="12" :sm="12" :md="6">
        <el-card class="kpi-card" shadow="hover" v-loading="kpiLoading">
          <div class="kpi-card__icon" :class="item.theme">
            <i :class="item.icon" />
          </div>
          <div class="kpi-card__body">
            <div class="kpi-card__label">{{ item.label }}</div>
            <div class="kpi-card__value">{{ item.value }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待办快捷入口 -->
    <el-card shadow="never" class="section-card">
      <div slot="header" class="section-header">
        <span>待处理事项</span>
        <span class="section-header__hint">点击可快速跳转对应管理页面</span>
      </div>
      <el-row :gutter="16">
        <el-col v-for="item in todoItems" :key="item.key" :xs="24" :sm="8" :md="8">
          <button class="todo-entry" type="button" @click="goTo(item.path)">
            <div class="todo-entry__icon" :class="item.theme">
              <i :class="item.icon" />
            </div>
            <div class="todo-entry__body">
              <div class="todo-entry__title">{{ item.title }}</div>
              <div class="todo-entry__count">
                <span class="todo-entry__number" v-loading="todoLoading">{{ item.count }}</span>
                <span class="todo-entry__unit">条待处理</span>
              </div>
            </div>
            <i class="el-icon-arrow-right todo-entry__arrow" />
          </button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 排行榜 -->
    <el-row :gutter="20" class="section-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="section-card" v-loading="rankLoading">
          <div slot="header" class="section-header">
            <span>赛事报名排行 Top 5</span>
          </div>
          <div v-if="contestRank.length" class="rank-list">
            <div v-for="(item, idx) in contestRank" :key="'contest-' + idx" class="rank-item">
              <span class="rank-item__index" :class="'rank-' + (idx + 1)">{{ idx + 1 }}</span>
              <span class="rank-item__name">{{ item.contestName || item.title || ('赛事 #' + item.contestId) }}</span>
              <div class="rank-item__bar-wrap">
                <div class="rank-item__bar" :style="{ width: barWidth(item.enrollCount, contestMaxEnroll) }" />
              </div>
              <span class="rank-item__value">{{ item.enrollCount || 0 }}</span>
            </div>
          </div>
          <el-empty v-else description="暂无数据" :image-size="80" />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="section-card" v-loading="rankLoading">
          <div slot="header" class="section-header">
            <span>渠道返点排行 Top 5</span>
          </div>
          <div v-if="channelRank.length" class="rank-list">
            <div v-for="(item, idx) in channelRank" :key="'channel-' + idx" class="rank-item">
              <span class="rank-item__index" :class="'rank-' + (idx + 1)">{{ idx + 1 }}</span>
              <span class="rank-item__name">{{ item.channelName || ('渠道 #' + item.channelId) }}</span>
              <div class="rank-item__bar-wrap">
                <div class="rank-item__bar rank-item__bar--green" :style="{ width: barWidth(item.totalRebate, channelMaxRebate) }" />
              </div>
              <span class="rank-item__value">{{ formatMoney(item.totalRebate) }}</span>
            </div>
          </div>
          <el-empty v-else description="暂无数据" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { countTodayOrders, countPendingContests, countPendingRefunds, countPendingWithdraws, topContests, topChannels } from '@/api/admin/dashboard'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      kpiLoading: false,
      todoLoading: false,
      rankLoading: false,
      todayOrderCount: 0,
      todayRevenue: 0,
      pendingContestCount: 0,
      pendingRefundCount: 0,
      pendingWithdrawCount: 0,
      contestRank: [],
      channelRank: []
    }
  },
  computed: {
    kpiCards() {
      return [
        { key: 'orders', label: '今日订单数', value: this.todayOrderCount, icon: 'el-icon-s-order', theme: 'theme-blue' },
        { key: 'revenue', label: '今日营收(元)', value: this.formatMoney(this.todayRevenue), icon: 'el-icon-money', theme: 'theme-green' },
        { key: 'pending', label: '待处理事项', value: this.totalPending, icon: 'el-icon-bell', theme: 'theme-orange' },
        { key: 'contests', label: '待审赛事', value: this.pendingContestCount, icon: 'el-icon-s-flag', theme: 'theme-purple' }
      ]
    },
    totalPending() {
      return this.pendingContestCount + this.pendingRefundCount + this.pendingWithdrawCount
    },
    todoItems() {
      return [
        { key: 'contest', title: '待审核赛事', count: this.pendingContestCount, icon: 'el-icon-s-flag', theme: 'theme-blue', path: '/jst/event/jst_contest' },
        { key: 'refund', title: '待审核退款', count: this.pendingRefundCount, icon: 'el-icon-coin', theme: 'theme-orange', path: '/jst/order/jst_refund_record' },
        { key: 'withdraw', title: '待审核提现', count: this.pendingWithdrawCount, icon: 'el-icon-wallet', theme: 'theme-purple', path: '/jst/channel/jst_rebate_settlement' }
      ]
    },
    contestMaxEnroll() {
      if (!this.contestRank.length) return 1
      return Math.max(...this.contestRank.map(item => item.enrollCount || 0)) || 1
    },
    channelMaxRebate() {
      if (!this.channelRank.length) return 1
      return Math.max(...this.channelRank.map(item => item.totalRebate || 0)) || 1
    }
  },
  created() {
    this.initPage()
  },
  methods: {
    initPage() {
      this.loadKpi()
      this.loadTodo()
      this.loadRank()
    },
    async loadKpi() {
      this.kpiLoading = true
      try {
        const today = this.getToday()
        const res = await countTodayOrders({ beginCreateTime: today, endCreateTime: today })
        this.todayOrderCount = res.total || 0
        // 营收需从列表行聚合，暂用 total 占位
        this.todayRevenue = 0
        if (res.rows && res.rows.length) {
          this.todayRevenue = Number(res.rows[0].actualPayAmount) || 0
        }
      } catch { this.todayOrderCount = 0 }
      this.kpiLoading = false
    },
    async loadTodo() {
      this.todoLoading = true
      try {
        const [contests, refunds, withdraws] = await Promise.all([
          countPendingContests({ auditStatus: 'pending_audit' }).catch(() => ({ total: 0 })),
          countPendingRefunds({ refundStatus: 'pending' }).catch(() => ({ total: 0 })),
          countPendingWithdraws({ settlementStatus: 'pending' }).catch(() => ({ total: 0 }))
        ])
        this.pendingContestCount = contests.total || 0
        this.pendingRefundCount = refunds.total || 0
        this.pendingWithdrawCount = withdraws.total || 0
      } catch {
        this.pendingContestCount = 0
        this.pendingRefundCount = 0
        this.pendingWithdrawCount = 0
      }
      this.todoLoading = false
    },
    async loadRank() {
      this.rankLoading = true
      try {
        const [contestRes, channelRes] = await Promise.all([
          topContests({ orderByColumn: 'enroll_count', isAsc: 'desc' }).catch(() => ({ rows: [] })),
          topChannels({ orderByColumn: 'total_rebate', isAsc: 'desc' }).catch(() => ({ rows: [] }))
        ])
        this.contestRank = (contestRes.rows || []).slice(0, 5)
        this.channelRank = (channelRes.rows || []).slice(0, 5)
      } catch {
        this.contestRank = []
        this.channelRank = []
      }
      this.rankLoading = false
    },
    formatMoney(val) {
      if (!val && val !== 0) return '0.00'
      return Number(val).toFixed(2)
    },
    barWidth(value, max) {
      if (!max) return '0%'
      return Math.round((Number(value || 0) / max) * 100) + '%'
    },
    getToday() {
      const d = new Date()
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return y + '-' + m + '-' + day
    },
    goTo(path) {
      this.$router.push({ path })
    }
  }
}
</script>

<style scoped lang="scss">
.admin-dashboard {
  padding-bottom: 24px;
  background: linear-gradient(180deg, #f5f8ff 0%, #f7f9fc 180px, #ffffff 181px);
}

.hero-banner {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 24px 28px;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #123d7a 0%, #1f5ba8 45%, #2e74c8 100%);
  border-radius: 20px;
  color: #ffffff;
  box-shadow: 0 16px 40px rgba(31, 91, 168, 0.18);
}

.hero-eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 2px;
  text-transform: uppercase;
  opacity: 0.72;
}

.hero-banner h2 {
  margin: 0 0 10px;
  font-size: 28px;
  line-height: 1.3;
}

.hero-desc {
  margin: 0;
  max-width: 680px;
  font-size: 14px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.86);
}

.section-row {
  margin-bottom: 20px;
}

.section-card {
  border: none;
  border-radius: 18px;
  box-shadow: 0 10px 30px rgba(18, 61, 122, 0.08);
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-weight: 600;
}

.section-header__hint {
  font-size: 12px;
  font-weight: 400;
  color: #8b98a9;
}

/* KPI Cards */
.kpi-card {
  border: none;
  border-radius: 18px;
}

.kpi-card ::v-deep .el-card__body {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 22px 20px;
}

.kpi-card__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 16px;
  color: #fff;
  font-size: 24px;
  flex-shrink: 0;
}

.theme-blue { background: linear-gradient(135deg, #1f5ba8 0%, #4f8fe0 100%); }
.theme-green { background: linear-gradient(135deg, #1b8f6a 0%, #4dc29b 100%); }
.theme-orange { background: linear-gradient(135deg, #de7e27 0%, #f3a14e 100%); }
.theme-purple { background: linear-gradient(135deg, #6453c7 0%, #8a77ea 100%); }

.kpi-card__body {
  flex: 1;
  min-width: 0;
}

.kpi-card__label {
  margin-bottom: 6px;
  font-size: 13px;
  color: #7a8797;
}

.kpi-card__value {
  font-size: 30px;
  font-weight: 700;
  line-height: 1;
  color: #1e2b3b;
}

/* 待办入口 */
.todo-entry {
  display: flex;
  align-items: center;
  width: 100%;
  min-height: 80px;
  margin-bottom: 16px;
  padding: 18px 16px;
  text-align: left;
  background: #f8fbff;
  border: 1px solid #e7eff8;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.todo-entry:hover {
  border-color: #86abdc;
  box-shadow: 0 10px 20px rgba(31, 91, 168, 0.1);
  transform: translateY(-2px);
}

.todo-entry__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  margin-right: 14px;
  border-radius: 14px;
  color: #fff;
  font-size: 20px;
  flex-shrink: 0;
}

.todo-entry__body {
  flex: 1;
  min-width: 0;
}

.todo-entry__title {
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #223043;
}

.todo-entry__count {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.todo-entry__number {
  font-size: 22px;
  font-weight: 700;
  color: #1f5ba8;
}

.todo-entry__unit {
  font-size: 12px;
  color: #8b98a9;
}

.todo-entry__arrow {
  color: #c0c4cc;
  font-size: 16px;
  flex-shrink: 0;
}

/* 排行榜 */
.rank-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rank-item__index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 700;
  background: #f0f2f5;
  color: #8b98a9;
  flex-shrink: 0;
}

.rank-1 { background: linear-gradient(135deg, #f6d365 0%, #fda085 100%); color: #fff; }
.rank-2 { background: linear-gradient(135deg, #a8c8ff 0%, #6fa3ef 100%); color: #fff; }
.rank-3 { background: linear-gradient(135deg, #b8e6d0 0%, #6bc7a0 100%); color: #fff; }

.rank-item__name {
  width: 140px;
  font-size: 14px;
  color: #223043;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex-shrink: 0;
}

.rank-item__bar-wrap {
  flex: 1;
  min-width: 60px;
  height: 8px;
  background: #f0f3f8;
  border-radius: 4px;
  overflow: hidden;
}

.rank-item__bar {
  height: 100%;
  background: linear-gradient(90deg, #4f8fe0 0%, #1f5ba8 100%);
  border-radius: 4px;
  transition: width 0.6s ease;
}

.rank-item__bar--green {
  background: linear-gradient(90deg, #4dc29b 0%, #1b8f6a 100%);
}

.rank-item__value {
  width: 60px;
  text-align: right;
  font-size: 14px;
  font-weight: 600;
  color: #1e2b3b;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .admin-dashboard {
    padding-bottom: 8px;
  }

  .hero-banner {
    flex-direction: column;
    padding: 20px;
    border-radius: 18px;
  }

  .hero-banner h2 {
    font-size: 22px;
  }

  .kpi-card ::v-deep .el-card__body {
    padding: 18px 14px;
  }

  .kpi-card__value {
    font-size: 24px;
  }

  .rank-item__name {
    width: 100px;
    font-size: 13px;
  }

  .rank-item__value {
    width: 50px;
    font-size: 13px;
  }

  .todo-entry {
    min-height: 70px;
    padding: 14px 12px;
  }
}
</style>
