<template>
  <div class="app-container admin-dashboard">
    <!-- Hero Banner -->
    <div class="hero-banner">
      <div>
        <p class="hero-eyebrow">平台管理中心</p>
        <h2>运营数据总览</h2>
        <p class="hero-desc">聚合平台核心指标与待办事项，快速掌握运营全局。</p>
      </div>
      <div class="hero-actions">
        <el-popover placement="bottom-end" width="320" trigger="click">
          <div class="help-popover__content">
            <p>看板数据分为今日、月度与累计三层，适合先看趋势再下钻页面处理。</p>
            <p>待处理事项展示当前最紧急任务，点击任意卡片可直接进入对应管理页。</p>
            <p>快速操作区提供新手高频入口，先完成审核与退款，再处理内容发布。</p>
            <p>排行榜用于定位热点赛事和高贡献渠道，便于优先安排运营动作。</p>
          </div>
          <el-button slot="reference" circle icon="el-icon-question" class="hero-help-btn" />
        </el-popover>
        <el-button type="primary" plain icon="el-icon-refresh" @click="initPage">刷新数据</el-button>
      </div>
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
        <el-col v-for="item in todoItems" :key="item.key" :xs="12" :sm="8" :md="4">
          <button class="todo-entry" :class="{ 'is-disabled': !item.enabled }" type="button" @click="handleEntryClick(item)">
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

    <el-card shadow="never" class="section-card">
      <div slot="header" class="section-header">
        <span>快速操作</span>
        <span class="section-header__hint">面向新手的高频入口，点击即可处理</span>
      </div>
      <el-row :gutter="16">
        <el-col v-for="item in quickActions" :key="item.key" :xs="12" :sm="12" :md="8">
          <button class="todo-entry quick-entry" :class="{ 'is-disabled': !item.enabled }" type="button" @click="handleEntryClick(item)">
            <div class="todo-entry__icon" :class="item.theme">
              <i :class="item.icon" />
            </div>
            <div class="todo-entry__body">
              <div class="todo-entry__title">{{ item.title }}</div>
              <div class="quick-entry__desc">{{ item.desc }}</div>
            </div>
            <i class="el-icon-arrow-right todo-entry__arrow" />
          </button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 功能导航 -->
    <el-card shadow="never" class="section-card">
      <div slot="header" class="section-header">
        <span>功能导航</span>
        <span class="section-header__hint">按业务分组快速进入对应管理模块</span>
      </div>
      <el-row :gutter="16">
        <el-col v-for="item in navGroups" :key="item.key" :xs="12" :sm="8" :md="6">
          <button class="nav-group-entry" :class="{ 'is-disabled': !item.enabled }" type="button" @click="handleEntryClick(item)">
            <div class="nav-group-entry__icon" :class="item.theme">
              <i :class="item.icon" />
            </div>
            <div class="nav-group-entry__body">
              <div class="nav-group-entry__title">{{ item.title }}</div>
              <div class="nav-group-entry__desc">{{ item.desc }}</div>
            </div>
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
import { getOverview, getTodo, getTopContests, getTopChannels } from '@/api/admin/dashboard'
import { collectAvailablePaths, resolveFirstAvailablePath } from '@/utils/route-access'
import { formatMoney as formatMoneyUtil } from '@/utils/format'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      kpiLoading: false,
      todoLoading: false,
      rankLoading: false,
      overview: {
        todayOrderCount: 0,
        todayRevenue: 0,
        monthRevenue: 0,
        totalUserCount: 0,
        totalContestCount: 0,
        totalEnrollCount: 0
      },
      todo: {
        pendingContestAudit: 0,
        pendingEnrollAudit: 0,
        pendingRefund: 0,
        pendingWithdraw: 0,
        pendingPartnerApply: 0,
        pendingChannelAuth: 0
      },
      navGroupConfigs: [
        { key: 'contest', title: '赛事运营', desc: '赛事管理、报名审核、课程管理、入驻审核', icon: 'el-icon-trophy', theme: 'theme-blue', candidates: ['/jst/contest', '/jst/group-contest/contest'] },
        { key: 'order', title: '订单交易', desc: '订单管理、退款处理、预约记录', icon: 'el-icon-shopping-bag-1', theme: 'theme-green', candidates: ['/jst/order/admin-order', '/jst/group-order/order/admin-order'] },
        { key: 'user', title: '用户渠道', desc: '用户管理、参赛档案、渠道管理、绑定关系', icon: 'el-icon-user', theme: 'theme-purple', candidates: ['/jst/user', '/jst/group-user/user'] },
        { key: 'marketing', title: '营销权益', desc: '优惠券、权益模板、发放管理', icon: 'el-icon-present', theme: 'theme-orange', candidates: ['/jst/coupon/template', '/jst/group-marketing/coupon/template'] },
        { key: 'points', title: '积分商城', desc: '积分账户、规则配置、商城商品、兑换订单', icon: 'el-icon-coin', theme: 'theme-blue', candidates: ['/jst/points/points-account', '/jst/group-points/points/points-account'] },
        { key: 'event', title: '赛事数据', desc: '成绩记录、证书管理、表单模板', icon: 'el-icon-document', theme: 'theme-green', candidates: ['/jst/event/score-record', '/jst/group-event/event/score-record'] },
        { key: 'finance', title: '财务管理', desc: '打款记录、合同管理、发票管理', icon: 'el-icon-wallet', theme: 'theme-purple', candidates: ['/jst/finance/payout', '/jst/group-finance/finance/payout'] },
        { key: 'risk', title: '风控审计', desc: '风控规则、告警处理、黑名单、审计日志', icon: 'el-icon-warning', theme: 'theme-orange', candidates: ['/jst/risk/risk-rule', '/jst/group-risk/risk/risk-rule'] }
      ],
      todoItemConfigs: [
        {
          key: 'contest',
          title: '待审核赛事',
          countKey: 'pendingContestAudit',
          icon: 'el-icon-s-flag',
          theme: 'theme-blue',
          candidates: ['/jst/contest', '/jst/group-contest/contest']
        },
        {
          key: 'enroll',
          title: '待审核报名',
          countKey: 'pendingEnrollAudit',
          icon: 'el-icon-edit-outline',
          theme: 'theme-green',
          candidates: ['/jst/enroll', '/jst/group-contest/enroll']
        },
        {
          key: 'refund',
          title: '待处理退款',
          countKey: 'pendingRefund',
          icon: 'el-icon-coin',
          theme: 'theme-orange',
          candidates: ['/jst/order/admin-refund', '/jst/group-order/order/admin-refund']
        },
        {
          key: 'withdraw',
          title: '待审核提现',
          countKey: 'pendingWithdraw',
          icon: 'el-icon-wallet',
          theme: 'theme-purple',
          candidates: ['/jst/channel/admin-withdraw', '/jst/group-user/channel/admin-withdraw']
        },
        {
          key: 'partner',
          title: '待审入驻申请',
          countKey: 'pendingPartnerApply',
          icon: 'el-icon-office-building',
          theme: 'theme-blue',
          candidates: ['/jst/partner-apply', '/jst/group-contest/partner-apply']
        },
        {
          key: 'channel',
          title: '待审渠道认证',
          countKey: 'pendingChannelAuth',
          icon: 'el-icon-postcard',
          theme: 'theme-green',
          candidates: ['/jst/channel-auth', '/jst/group-user/channel-auth']
        }
      ],
      quickActionConfigs: [
        {
          key: 'createContest',
          title: '创建赛事',
          desc: '按 6 个模块快速发起新赛事',
          icon: 'el-icon-plus',
          theme: 'theme-blue',
          candidates: ['/jst/contest-edit', '/jst/group-contest/contest-edit']
        },
        {
          key: 'auditEnroll',
          title: '审核报名',
          desc: '处理通过、驳回与补充材料',
          icon: 'el-icon-edit-outline',
          theme: 'theme-green',
          candidates: ['/jst/enroll', '/jst/group-contest/enroll']
        },
        {
          key: 'dealRefund',
          title: '处理退款',
          desc: '优先审核待处理退款申请',
          icon: 'el-icon-money',
          theme: 'theme-orange',
          candidates: ['/jst/order/admin-refund', '/jst/group-order/order/admin-refund']
        },
        {
          key: 'auditWithdraw',
          title: '审核提现',
          desc: '审核后执行渠道打款流程',
          icon: 'el-icon-wallet',
          theme: 'theme-purple',
          candidates: ['/jst/channel/admin-withdraw', '/jst/group-user/channel/admin-withdraw']
        },
        {
          key: 'publishNotice',
          title: '发布公告',
          desc: '统一发布平台与赛事公告',
          icon: 'el-icon-bell',
          theme: 'theme-blue',
          candidates: ['/jst/notice', '/jst/group-contest/notice']
        },
        {
          key: 'viewChannel',
          title: '查看渠道',
          desc: '查看渠道状态和关键数据',
          icon: 'el-icon-user-solid',
          theme: 'theme-green',
          candidates: ['/jst/channel', '/jst/group-user/channel']
        }
      ],
      contestRank: [],
      channelRank: []
    }
  },
  computed: {
    kpiCards() {
      return [
        { key: 'orders', label: '今日订单数', value: this.overview.todayOrderCount, icon: 'el-icon-s-order', theme: 'theme-blue' },
        { key: 'revenue', label: '今日营收(元)', value: this.formatMoney(this.overview.todayRevenue), icon: 'el-icon-money', theme: 'theme-green' },
        { key: 'monthRevenue', label: '本月营收(元)', value: this.formatMoney(this.overview.monthRevenue), icon: 'el-icon-data-analysis', theme: 'theme-orange' },
        { key: 'users', label: '累计用户', value: this.overview.totalUserCount, icon: 'el-icon-user', theme: 'theme-purple' }
      ]
    },
    totalPending() {
      const t = this.todo
      return t.pendingContestAudit + t.pendingEnrollAudit + t.pendingRefund + t.pendingWithdraw + t.pendingPartnerApply + t.pendingChannelAuth
    },
    availablePathSet() {
      const routeSet = collectAvailablePaths(this.$store.getters.permission_routes || [])
      const sidebarSet = collectAvailablePaths(this.$store.getters.sidebarRouters || [])
      sidebarSet.forEach(path => routeSet.add(path))
      return routeSet
    },
    navGroups() {
      return this.navGroupConfigs.map(item => {
        const targetPath = resolveFirstAvailablePath(item.candidates, this.availablePathSet)
        return { ...item, targetPath, enabled: Boolean(targetPath) }
      })
    },
    todoItems() {
      return this.todoItemConfigs.map(item => {
        const targetPath = resolveFirstAvailablePath(item.candidates, this.availablePathSet)
        return {
          ...item,
          count: this.todo[item.countKey] || 0,
          targetPath,
          enabled: Boolean(targetPath)
        }
      })
    },
    quickActions() {
      return this.quickActionConfigs.map(item => {
        const targetPath = resolveFirstAvailablePath(item.candidates, this.availablePathSet)
        return {
          ...item,
          targetPath,
          enabled: Boolean(targetPath)
        }
      })
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
        const res = await getOverview()
        if (res.code === 200) {
          this.overview = res.data || this.overview
        }
      } catch { /* keep defaults */ }
      this.kpiLoading = false
    },
    async loadTodo() {
      this.todoLoading = true
      try {
        const res = await getTodo()
        if (res.code === 200) {
          this.todo = res.data || this.todo
        }
      } catch { /* keep defaults */ }
      this.todoLoading = false
    },
    async loadRank() {
      this.rankLoading = true
      try {
        const [contestRes, channelRes] = await Promise.all([
          getTopContests(5).catch(() => ({ data: [] })),
          getTopChannels(5).catch(() => ({ data: [] }))
        ])
        this.contestRank = (contestRes.code === 200 ? contestRes.data : []) || []
        this.channelRank = (channelRes.code === 200 ? channelRes.data : []) || []
      } catch {
        this.contestRank = []
        this.channelRank = []
      }
      this.rankLoading = false
    },
    formatMoney(val) {
      return formatMoneyUtil(val).replace('\u00A5', '')
    },
    barWidth(value, max) {
      if (!max) return '0%'
      return Math.round((Number(value || 0) / max) * 100) + '%'
    },
    notifyNoAccess() {
      const text = '当前账号未配置该菜单或无权限'
      if (this.$modal && this.$modal.msgWarning) {
        this.$modal.msgWarning(text)
        return
      }
      if (this.$message && this.$message.warning) {
        this.$message.warning(text)
      }
    },
    handleEntryClick(entry) {
      if (!entry || !entry.enabled || !entry.targetPath) {
        this.notifyNoAccess()
        return
      }
      this.$router.push({ path: entry.targetPath })
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

.hero-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.hero-help-btn {
  border: none;
  color: #1f5ba8;
}

.help-popover__content p {
  margin: 0 0 8px;
  color: #475569;
  line-height: 1.6;
}

.help-popover__content p:last-child {
  margin-bottom: 0;
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

.todo-entry.is-disabled {
  border-color: #e5eaf2;
  background: #f7f9fc;
  cursor: not-allowed;
}

.todo-entry.is-disabled:hover {
  border-color: #e5eaf2;
  box-shadow: none;
  transform: none;
}

.todo-entry.is-disabled .todo-entry__title,
.todo-entry.is-disabled .quick-entry__desc,
.todo-entry.is-disabled .todo-entry__unit {
  color: #a0acbd;
}

.todo-entry.is-disabled .todo-entry__number {
  color: #97a6bc;
}

.todo-entry.is-disabled .todo-entry__arrow {
  color: #d1d8e4;
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

.quick-entry {
  min-height: 96px;
}

.quick-entry__desc {
  font-size: 12px;
  color: #7d8da0;
  line-height: 1.5;
}

/* 功能导航 */
.nav-group-entry {
  display: flex;
  align-items: center;
  width: 100%;
  min-height: 80px;
  margin-bottom: 16px;
  padding: 16px 14px;
  text-align: left;
  background: #f8fbff;
  border: 1px solid #e7eff8;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.nav-group-entry:hover {
  border-color: #86abdc;
  box-shadow: 0 8px 18px rgba(31, 91, 168, 0.1);
  transform: translateY(-2px);
}

.nav-group-entry.is-disabled {
  border-color: #e5eaf2;
  background: #f7f9fc;
  cursor: not-allowed;
}

.nav-group-entry.is-disabled:hover {
  border-color: #e5eaf2;
  box-shadow: none;
  transform: none;
}

.nav-group-entry__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  margin-right: 12px;
  border-radius: 12px;
  color: #fff;
  font-size: 20px;
  flex-shrink: 0;
}

.nav-group-entry__body {
  flex: 1;
  min-width: 0;
}

.nav-group-entry__title {
  margin-bottom: 4px;
  font-size: 14px;
  font-weight: 600;
  color: #223043;
}

.nav-group-entry__desc {
  font-size: 12px;
  color: #7d8da0;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.nav-group-entry.is-disabled .nav-group-entry__title,
.nav-group-entry.is-disabled .nav-group-entry__desc {
  color: #a0acbd;
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

  .hero-actions {
    width: 100%;
    justify-content: flex-start;
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
