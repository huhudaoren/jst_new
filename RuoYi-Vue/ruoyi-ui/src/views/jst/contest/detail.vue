<template>
  <el-drawer
    title="赛事详情"
    :visible.sync="drawerVisible"
    :size="isMobile ? '100%' : '900px'"
    append-to-body
    custom-class="jst-contest-detail-drawer"
  >
    <div v-loading="loading" class="detail-wrap">
      <template v-if="detail">
        <el-descriptions title="基本信息" :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="赛事ID">{{ detail.contestId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="赛事名称">{{ detail.contestName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ detail.category || '--' }}</el-descriptions-item>
          <el-descriptions-item label="组别">{{ detail.groupLevels || '--' }}</el-descriptions-item>
          <el-descriptions-item label="赛事方">{{ detail.partnerName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="赛事方ID">{{ detail.partnerId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="价格">¥{{ formatMoney(detail.price) }}</el-descriptions-item>
          <el-descriptions-item label="报名人数">{{ detail.enrollCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <JstStatusBadge :status="String(detail.auditStatus || '')" :status-map="auditStatusMap" />
          </el-descriptions-item>
          <el-descriptions-item label="业务状态">
            <JstStatusBadge :status="String(detail.status || '')" :status-map="contestStatusMap" />
          </el-descriptions-item>
          <el-descriptions-item label="报名时间">
            {{ formatRange(detail.enrollStartTime, detail.enrollEndTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="比赛时间">
            {{ formatRange(detail.eventStartTime, detail.eventEndTime) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="detail.auditRemark" :span="isMobile ? 1 : 2" label="审核备注">
            {{ detail.auditRemark }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="section-title">赛事说明</div>
        <div class="desc-card" v-html="detail.description || '--'" />

        <div class="section-title">赛程安排</div>
        <el-table v-if="scheduleRows.length" :data="scheduleRows" size="small" max-height="260">
          <el-table-column label="阶段" prop="stageName" min-width="130" />
          <el-table-column label="开始时间" min-width="160">
            <template slot-scope="scope">{{ formatDateTime(scope.row.startTime) }}</template>
          </el-table-column>
          <el-table-column label="结束时间" min-width="160">
            <template slot-scope="scope">{{ formatDateTime(scope.row.endTime) }}</template>
          </el-table-column>
          <el-table-column label="说明" prop="description" min-width="220" show-overflow-tooltip />
        </el-table>
        <JstEmptyState v-else description="暂无赛程安排" />

        <div class="section-title">奖项设置</div>
        <el-table v-if="awardRows.length" :data="awardRows" size="small" max-height="260">
          <el-table-column label="奖项" prop="awardName" min-width="120" />
          <el-table-column label="等级" prop="level" min-width="120" />
          <el-table-column label="名额" min-width="100" align="right">
            <template slot-scope="scope">{{ scope.row.quota || '--' }}</template>
          </el-table-column>
          <el-table-column label="说明" prop="description" min-width="220" show-overflow-tooltip />
        </el-table>
        <JstEmptyState v-else description="暂无奖项设置" />

        <div class="section-title">常见问题</div>
        <el-table v-if="faqRows.length" :data="faqRows" size="small" max-height="260">
          <el-table-column label="问题" prop="question" min-width="240" show-overflow-tooltip />
          <el-table-column label="答案" prop="answer" min-width="320" show-overflow-tooltip />
        </el-table>
        <JstEmptyState v-else description="暂无常见问题" />
      </template>
      <JstEmptyState v-else description="暂无赛事信息" />
    </div>
  </el-drawer>
</template>

<script>
import { getAdminContest } from '@/api/jst/event/admin-contest'
import { formatMoney as formatMoneyUtil } from '@/utils/format'

export default {
  name: 'JstContestDetailDrawer',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    contestId: {
      type: [String, Number],
      default: null
    }
  },
  data() {
    return {
      loading: false,
      detail: null,
      scheduleRows: [],
      awardRows: [],
      faqRows: [],
      auditStatusMap: {
        draft: { label: '草稿', type: 'info' },
        pending: { label: '待审核', type: 'warning' },
        approved: { label: '审核通过', type: 'success' },
        rejected: { label: '已驳回', type: 'danger' },
        online: { label: '已上线', type: 'success' },
        offline: { label: '已下线', type: 'info' }
      },
      contestStatusMap: {
        not_started: { label: '未开始', type: 'info' },
        enrolling: { label: '报名中', type: 'warning' },
        closed: { label: '已截止', type: 'info' },
        scoring: { label: '评分中', type: 'warning' },
        published: { label: '已公布', type: 'success' },
        ended: { label: '已结束', type: 'info' }
      }
    }
  },
  computed: {
    drawerVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    },
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    }
  },
  watch: {
    visible(val) {
      if (val && this.contestId) {
        this.loadData()
      }
    },
    contestId(val) {
      if (this.visible && val) {
        this.loadData()
      }
    }
  },
  methods: {
    async loadData() {
      if (!this.contestId) {
        return
      }
      this.loading = true
      try {
        const res = await getAdminContest(this.contestId)
        const data = res.data || {}
        this.detail = data
        this.scheduleRows = this.normalizeSchedule(data.scheduleJson)
        this.awardRows = this.normalizeAwards(data.awardsJson)
        this.faqRows = this.normalizeFaq(data.faqJson)
      } catch (e) {
        this.detail = null
        this.scheduleRows = []
        this.awardRows = []
        this.faqRows = []
      } finally {
        this.loading = false
      }
    },
    parseJsonRows(value) {
      if (!value) {
        return []
      }
      if (Array.isArray(value)) {
        return value
      }
      if (typeof value === 'object') {
        if (Array.isArray(value.rows)) return value.rows
        if (Array.isArray(value.list)) return value.list
        return []
      }
      try {
        const parsed = JSON.parse(value)
        if (Array.isArray(parsed)) return parsed
        if (parsed && Array.isArray(parsed.rows)) return parsed.rows
        if (parsed && Array.isArray(parsed.list)) return parsed.list
        return []
      } catch (e) {
        return []
      }
    },
    normalizeSchedule(value) {
      return this.parseJsonRows(value).map(item => ({
        stageName: item.stageName || item.stage || item.name || '',
        startTime: item.startTime || item.beginTime || item.start || '',
        endTime: item.endTime || item.finishTime || item.end || '',
        description: item.description || item.desc || ''
      }))
    },
    normalizeAwards(value) {
      return this.parseJsonRows(value).map(item => ({
        awardName: item.awardName || item.name || item.title || '',
        level: item.level || item.rank || '',
        quota: item.quota || item.count || '',
        description: item.description || item.desc || ''
      }))
    },
    normalizeFaq(value) {
      return this.parseJsonRows(value).map(item => ({
        question: item.question || item.q || '',
        answer: item.answer || item.a || ''
      }))
    },
    formatMoney(value) {
      return formatMoneyUtil(value).replace('\u00A5', '')
    },
    formatDateTime(value) {
      return this.parseTime(value) || '--'
    },
    formatRange(start, end) {
      const s = this.parseTime(start) || '--'
      const e = this.parseTime(end) || '--'
      return s + ' 至 ' + e
    }
  }
}
</script>

<style scoped>
.detail-wrap {
  padding: 0 18px 20px;
}

.section-title {
  margin: 20px 0 10px;
  font-size: 15px;
  font-weight: 700;
  color: #172033;
}

.desc-card {
  padding: 12px 14px;
  line-height: 1.7;
  color: #4b5565;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
  background: #fafcff;
}

@media (max-width: 768px) {
  .detail-wrap {
    padding: 0 12px 16px;
  }
}
</style>
