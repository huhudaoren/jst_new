<template>
  <div class="app-container partner-home">
    <div class="hero-banner">
      <div>
        <p class="hero-eyebrow">赛事方工作台</p>
        <h2>今天先看最关键的进度</h2>
        <p class="hero-desc">聚合报名、成绩、证书与平台通知，帮助赛事方账号登录后直接进入日常处理节奏。</p>
      </div>
      <el-button type="primary" plain icon="el-icon-refresh" @click="initPage">刷新数据</el-button>
    </div>

    <el-alert
      v-if="hasLoadError"
      title="首页接口尚未在后端落地，当前以空态渲染页面并保留接线。字段需求已同步到 ui-feedback 文档。"
      type="warning"
      :closable="false"
      show-icon
      class="load-alert"
    />

    <el-row :gutter="20" class="section-row">
      <el-col v-for="item in kpiCards" :key="item.key" :xs="12" :sm="12" :md="6">
        <el-card class="kpi-card" shadow="hover" v-loading="summaryLoading">
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

    <el-card shadow="never" class="section-card quick-menu-card">
      <div slot="header" class="section-header">
        <span>快捷菜单</span>
        <span class="section-header__hint">9 个高频入口，手机端自动切成 2 列</span>
      </div>
      <el-row :gutter="16">
        <el-col v-for="item in quickMenus" :key="item.path" :xs="12" :sm="8" :md="8">
          <button
            class="quick-menu"
            :class="{ 'is-disabled': item.disabled }"
            type="button"
            @click="handleQuickMenu(item)"
          >
            <div class="quick-menu__icon">
              <i :class="item.icon" />
            </div>
            <div class="quick-menu__body">
              <div class="quick-menu__title">
                <span>{{ item.title }}</span>
                <el-tag v-if="item.badge" size="mini" type="info">{{ item.badge }}</el-tag>
              </div>
              <div class="quick-menu__desc">{{ item.desc }}</div>
            </div>
          </button>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20" class="section-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="section-card" v-loading="todoLoading">
          <div slot="header" class="section-header">
            <span>待审报名 Top 5</span>
            <el-button type="text" @click="goTo('/partner/enroll-manage')">查看全部</el-button>
          </div>
          <div v-if="todoEnrollList.length" class="todo-list">
            <button
              v-for="item in todoEnrollList"
              :key="'enroll-' + getItemKey(item, 'enrollId')"
              class="todo-item"
              type="button"
              @click="openTodoItem(item, '/partner/enroll-manage', 'enrollId')"
            >
              <div class="todo-item__main">{{ getTodoTitle(item, '待审核报名') }}</div>
              <div class="todo-item__sub">{{ getTodoSubtitle(item, 'studentName', 'submitTime') }}</div>
            </button>
          </div>
          <el-empty v-else description="暂无待审报名" :image-size="96" />
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="section-card" v-loading="todoLoading">
          <div slot="header" class="section-header">
            <span>待发成绩 Top 5</span>
            <el-button type="text" @click="goTo('/partner/score-manage')">查看全部</el-button>
          </div>
          <div v-if="todoScoreList.length" class="todo-list">
            <button
              v-for="item in todoScoreList"
              :key="'score-' + getItemKey(item, 'scoreId')"
              class="todo-item"
              type="button"
              @click="openTodoItem(item, '/partner/score-manage', 'scoreId')"
            >
              <div class="todo-item__main">{{ getTodoTitle(item, '待发成绩') }}</div>
              <div class="todo-item__sub">{{ getTodoSubtitle(item, 'stageName', 'deadlineTime') }}</div>
            </button>
          </div>
          <el-empty v-else description="暂无待发成绩" :image-size="96" />
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="section-card">
      <div slot="header" class="section-header">
        <span>平台通知</span>
        <span class="section-header__hint">最近 3 条平台公告</span>
      </div>
      <div v-loading="noticeLoading">
        <div v-if="noticeList.length" class="notice-list">
          <div v-for="item in noticeList" :key="getItemKey(item, 'noticeId')" class="notice-item">
            <div class="notice-item__top">
              <span class="notice-item__title">{{ item.title || item.noticeTitle || '平台通知' }}</span>
              <span class="notice-item__time">{{ formatTime(item.publishTime || item.createTime) }}</span>
            </div>
            <div class="notice-item__content">{{ item.summary || item.content || item.noticeContent || '暂无摘要' }}</div>
          </div>
        </div>
        <el-empty v-else description="暂无平台通知" :image-size="96" />
      </div>
    </el-card>
  </div>
</template>

<script>
import { getPartnerRecentNotice, getPartnerSummary, getPartnerTodo } from '@/api/partner/dashboard'

export default {
  name: 'PartnerHome',
  data() {
    return {
      summaryLoading: false,
      todoLoading: false,
      noticeLoading: false,
      summaryError: false,
      todoError: false,
      noticeError: false,
      summary: {
        pendingEnrollCount: 0,
        monthEnrollCount: 0,
        pendingScoreCount: 0,
        pendingCertificateCount: 0
      },
      todoEnrollList: [],
      todoScoreList: [],
      noticeList: [],
      quickMenus: [
        { title: '赛事管理', desc: '查看赛事与场次', path: '/partner/contest-list', icon: 'el-icon-s-flag' },
        { title: '报名审核', desc: '处理待审核报名', path: '/partner/enroll-manage', icon: 'el-icon-s-order' },
        { title: '成绩管理', desc: '录入与发布成绩', path: '/partner/score-manage', icon: 'el-icon-data-analysis' },
        { title: '证书管理', desc: '查看证书发放进度', path: '/partner/cert-manage', icon: 'el-icon-tickets' },
        { title: '现场核销', desc: '扫码核销与签到', path: '/partner/writeoff', icon: 'el-icon-s-claim' },
        { title: '结算中心', desc: '查看结算与打款', path: '/partner/settlement', icon: 'el-icon-wallet' },
        { title: '合同发票', desc: '后续阶段补齐', path: '/partner/contract-invoice', icon: 'el-icon-document', disabled: true, badge: 'F-2' },
        { title: '数据统计', desc: '按赛事查看经营数据', path: '/partner/stats', icon: 'el-icon-pie-chart' },
        { title: '账号设置', desc: '维护账号与资料', path: '/partner/settings', icon: 'el-icon-setting' }
      ]
    }
  },
  computed: {
    hasLoadError() {
      return this.summaryError || this.todoError || this.noticeError
    },
    kpiCards() {
      return [
        {
          key: 'pendingEnrollCount',
          label: '待审报名数',
          value: this.summary.pendingEnrollCount,
          icon: 'el-icon-s-order',
          theme: 'theme-blue'
        },
        {
          key: 'monthEnrollCount',
          label: '本月报名总数',
          value: this.summary.monthEnrollCount,
          icon: 'el-icon-date',
          theme: 'theme-green'
        },
        {
          key: 'pendingScoreCount',
          label: '待发成绩数',
          value: this.summary.pendingScoreCount,
          icon: 'el-icon-data-analysis',
          theme: 'theme-orange'
        },
        {
          key: 'pendingCertificateCount',
          label: '待领证书数',
          value: this.summary.pendingCertificateCount,
          icon: 'el-icon-tickets',
          theme: 'theme-purple'
        }
      ]
    }
  },
  created() {
    this.initPage()
  },
  methods: {
    initPage() {
      this.loadSummary()
      this.loadTodo()
      this.loadNotices()
    },
    async loadSummary() {
      this.summaryLoading = true
      this.summaryError = false
      try {
        const res = await getPartnerSummary()
        const data = (res && res.data) || {}
        this.summary = {
          pendingEnrollCount: this.toCount(data.pendingEnrollCount),
          monthEnrollCount: this.toCount(data.monthEnrollCount),
          pendingScoreCount: this.toCount(data.pendingScoreCount),
          pendingCertificateCount: this.toCount(data.pendingCertificateCount)
        }
      } catch (error) {
        this.summaryError = true
        this.summary = {
          pendingEnrollCount: 0,
          monthEnrollCount: 0,
          pendingScoreCount: 0,
          pendingCertificateCount: 0
        }
      } finally {
        this.summaryLoading = false
      }
    },
    async loadTodo() {
      this.todoLoading = true
      this.todoError = false
      try {
        const res = await getPartnerTodo()
        const data = (res && res.data) || {}
        this.todoEnrollList = Array.isArray(data.pendingEnrollList) ? data.pendingEnrollList.slice(0, 5) : []
        this.todoScoreList = Array.isArray(data.pendingScoreList) ? data.pendingScoreList.slice(0, 5) : []
      } catch (error) {
        this.todoError = true
        this.todoEnrollList = []
        this.todoScoreList = []
      } finally {
        this.todoLoading = false
      }
    },
    async loadNotices() {
      this.noticeLoading = true
      this.noticeError = false
      try {
        const res = await getPartnerRecentNotice()
        const data = (res && res.data) || []
        this.noticeList = Array.isArray(data) ? data.slice(0, 3) : []
      } catch (error) {
        this.noticeError = true
        this.noticeList = []
      } finally {
        this.noticeLoading = false
      }
    },
    handleQuickMenu(item) {
      if (item.disabled) {
        this.$message.info('合同发票入口将在 F-2 阶段开放')
        return
      }
      this.goTo(item.path)
    },
    goTo(path, query) {
      this.$router.push({ path, query: query || {} })
    },
    openTodoItem(item, path, key) {
      const query = {}
      if (item && item[key] !== undefined && item[key] !== null && item[key] !== '') {
        query[key] = item[key]
      }
      this.goTo(path, query)
    },
    getTodoTitle(item, fallback) {
      return item.title || item.contestName || item.name || fallback
    },
    getTodoSubtitle(item, labelKey, timeKey) {
      const label = item[labelKey] || item.studentName || item.enrollNo || '待补充信息'
      const time = this.formatTime(item[timeKey])
      if (time === '--') {
        return label
      }
      return `${label} · ${time}`
    },
    getItemKey(item, fallbackKey) {
      return item[fallbackKey] || item.noticeId || item.scoreId || item.enrollId || item.id || [
        item.title,
        item.contestName,
        item.studentName,
        item.stageName
      ].filter(Boolean).join('-') || fallbackKey
    },
    formatTime(value) {
      if (!value) {
        return '--'
      }
      return String(value).replace('T', ' ').slice(0, 16)
    },
    toCount(value) {
      const count = Number(value)
      return Number.isFinite(count) ? count : 0
    }
  }
}
</script>

<style scoped lang="scss">
.partner-home {
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

.load-alert {
  margin-bottom: 20px;
}

.section-row {
  margin-bottom: 20px;
}

.section-card {
  border: none;
  border-radius: 18px;
  box-shadow: 0 10px 30px rgba(18, 61, 122, 0.08);
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
}

.theme-blue {
  background: linear-gradient(135deg, #1f5ba8 0%, #4f8fe0 100%);
}

.theme-green {
  background: linear-gradient(135deg, #1b8f6a 0%, #4dc29b 100%);
}

.theme-orange {
  background: linear-gradient(135deg, #de7e27 0%, #f3a14e 100%);
}

.theme-purple {
  background: linear-gradient(135deg, #6453c7 0%, #8a77ea 100%);
}

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

.quick-menu-card ::v-deep .el-card__body {
  padding-top: 8px;
}

.quick-menu {
  display: flex;
  align-items: center;
  width: 100%;
  min-height: 88px;
  margin-bottom: 16px;
  padding: 18px 16px;
  text-align: left;
  background: #f8fbff;
  border: 1px solid #e7eff8;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-menu:hover {
  border-color: #86abdc;
  box-shadow: 0 10px 20px rgba(31, 91, 168, 0.1);
  transform: translateY(-2px);
}

.quick-menu.is-disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.quick-menu__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  margin-right: 14px;
  background: #eaf2fe;
  border-radius: 14px;
  color: #1f5ba8;
  font-size: 20px;
}

.quick-menu__body {
  flex: 1;
  min-width: 0;
}

.quick-menu__title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 15px;
  font-weight: 600;
  color: #223043;
}

.quick-menu__desc {
  font-size: 12px;
  color: #7a8797;
}

.todo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.todo-item {
  width: 100%;
  padding: 14px 16px;
  text-align: left;
  background: #f9fbfd;
  border: 1px solid #e7edf3;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.todo-item:hover {
  border-color: #9bb7db;
  background: #f4f8fd;
}

.todo-item__main {
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #223043;
}

.todo-item__sub {
  font-size: 12px;
  color: #7a8797;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.notice-item {
  padding: 16px 18px;
  background: #fbfcfe;
  border: 1px solid #edf1f5;
  border-radius: 16px;
}

.notice-item__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.notice-item__title {
  font-size: 15px;
  font-weight: 600;
  color: #223043;
}

.notice-item__time {
  flex-shrink: 0;
  font-size: 12px;
  color: #94a0af;
}

.notice-item__content {
  font-size: 13px;
  line-height: 1.7;
  color: #5d6b7c;
}

@media (max-width: 768px) {
  .partner-home {
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

  .quick-menu {
    min-height: 82px;
    padding: 16px 14px;
  }

  .quick-menu__title {
    align-items: flex-start;
    flex-direction: column;
  }

  .notice-item__top {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
