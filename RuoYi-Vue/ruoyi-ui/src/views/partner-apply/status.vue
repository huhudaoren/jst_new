<template>
  <div class="partner-status-page">
    <div class="status-shell">
      <div class="shell-header">
        <div>
          <div class="status-tag">公开申请进度</div>
          <h1>查询赛事方入驻申请状态</h1>
          <p>当前公开查询基于申请单号。提交成功后请保存系统返回的申请编号，后续可随时回来查看状态。</p>
        </div>
        <div class="header-actions">
          <el-button plain @click="$router.push('/partner-apply')">返回入口</el-button>
          <el-button type="primary" @click="$router.push('/partner-apply/form')">新建申请</el-button>
        </div>
      </div>

      <el-alert
        v-if="submittedNotice"
        title="申请已提交成功，请保存申请单号并关注审核状态。"
        type="success"
        show-icon
        :closable="false"
        class="notice-alert"
      />

      <div class="lookup-panel">
        <el-form ref="lookupForm" :model="lookupForm" :rules="lookupRules" :inline="!isMobile">
          <el-form-item label="申请单号" prop="applyNo">
            <el-input
              v-model.trim="lookupForm.applyNo"
              placeholder="请输入申请单号"
              clearable
              @keyup.enter.native="fetchStatus"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="fetchStatus">查询状态</el-button>
          </el-form-item>
        </el-form>
        <div v-if="lastApplyNo && lastApplyNo !== lookupForm.applyNo" class="quick-link">
          最近查询申请单号：{{ lastApplyNo }}
          <el-button type="text" @click="useLastApplyNo">一键带入</el-button>
        </div>
      </div>

      <div v-if="status" class="result-panel">
        <div class="status-summary" :class="`status-${status.applyStatus}`">
          <div class="status-icon">{{ statusMeta.icon }}</div>
          <div class="status-copy">
            <div class="status-title">{{ statusMeta.title }}</div>
            <div class="status-desc">{{ statusMeta.desc }}</div>
          </div>
        </div>

        <div class="info-grid">
          <div class="info-card">
            <div class="card-title">申请信息</div>
            <div class="info-item"><span>申请单号</span><strong>{{ lookupForm.applyNo }}</strong></div>
            <div class="info-item"><span>提交时间</span><strong>{{ formatTime(status.submitTime) }}</strong></div>
            <div class="info-item"><span>审核时间</span><strong>{{ formatTime(status.auditTime) }}</strong></div>
            <div class="info-item"><span>当前状态</span><strong>{{ statusMeta.title }}</strong></div>
          </div>

          <div class="info-card">
            <div class="card-title">处理说明</div>
            <div class="remark-block">{{ status.auditRemark || statusMeta.defaultRemark }}</div>
            <div class="card-tip" v-if="status.applyStatus === 'approved'">
              审核通过后，平台将通过既有业务流程通知联系人后续账号开通信息。当前公开状态接口不返回临时账号和初始密码。
            </div>
            <div class="card-tip" v-else-if="canResubmit">
              当前状态建议重新整理资料后再次提交。重新提交会生成新的申请单号，不会覆盖旧记录。
            </div>
          </div>
        </div>

        <div class="action-row" v-if="canResubmit">
          <el-button type="primary" @click="goResubmit">重新填写申请</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import cache from '@/plugins/cache'
import { parseTime } from '@/utils/ruoyi'
import { getPartnerApplyStatus } from '@/api/partner/apply'

const LAST_APPLY_NO_KEY = 'partnerApply:lastApplyNo'

export default {
  name: 'PartnerApplyStatus',
  data() {
    return {
      isMobile: window.innerWidth < 768,
      loading: false,
      submittedNotice: this.$route.query.submitted === '1',
      lastApplyNo: cache.local.get(LAST_APPLY_NO_KEY) || '',
      lookupForm: {
        applyNo: this.$route.query.applyNo || cache.local.get(LAST_APPLY_NO_KEY) || ''
      },
      lookupRules: {
        applyNo: [{ required: true, message: '请输入申请单号', trigger: 'blur' }]
      },
      status: null
    }
  },
  computed: {
    canResubmit() {
      if (!this.status) {
        return false
      }
      return ['rejected', 'supplement'].includes(this.status.applyStatus)
    },
    statusMeta() {
      const map = {
        pending: {
          icon: '审',
          title: '审核中',
          desc: '平台正在审核当前申请，请耐心等待处理结果。',
          defaultRemark: '申请已进入审核流程，请留意平台后续通知。'
        },
        approved: {
          icon: '过',
          title: '已通过',
          desc: '申请已审核通过，后续账号开通信息请留意联系人渠道通知。',
          defaultRemark: '申请已审核通过。'
        },
        rejected: {
          icon: '驳',
          title: '已驳回',
          desc: '申请资料未通过审核，请根据驳回原因调整后重新发起申请。',
          defaultRemark: '申请已驳回，请根据审核意见重新准备材料。'
        },
        supplement: {
          icon: '补',
          title: '待补件',
          desc: '平台已提出补件要求，请按提示补充材料后重新提交申请。',
          defaultRemark: '申请待补件，请补充完善资料后重新提交。'
        }
      }
      return map[this.status?.applyStatus] || {
        icon: '查',
        title: '待查询',
        desc: '请输入申请单号查询状态。',
        defaultRemark: '暂无处理说明。'
      }
    }
  },
  created() {
    if (this.lookupForm.applyNo) {
      this.fetchStatus()
    }
  },
  mounted() {
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    handleResize() {
      this.isMobile = window.innerWidth < 768
    },
    useLastApplyNo() {
      this.lookupForm.applyNo = this.lastApplyNo
      this.fetchStatus()
    },
    fetchStatus() {
      this.$refs.lookupForm.validate(valid => {
        if (!valid) {
          return
        }
        this.loading = true
        getPartnerApplyStatus(this.lookupForm.applyNo).then(res => {
          this.status = res.data
          cache.local.set(LAST_APPLY_NO_KEY, this.lookupForm.applyNo)
          this.lastApplyNo = this.lookupForm.applyNo
        }).finally(() => {
          this.loading = false
        })
      })
    },
    formatTime(value) {
      if (!value) {
        return '—'
      }
      return parseTime(value, '{y}-{m}-{d} {h}:{i}:{s}')
    },
    goResubmit() {
      this.$router.push({
        path: '/partner-apply/form',
        query: {
          mode: 'resubmit',
          applyNo: this.lookupForm.applyNo
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.partner-status-page {
  min-height: 100vh;
  padding: 32px 24px 48px;
  background:
    radial-gradient(circle at top right, rgba(19, 95, 214, 0.12), transparent 28%),
    linear-gradient(180deg, #f4f8ff 0%, #ffffff 42%, #f8fbff 100%);
}

.status-shell {
  max-width: 1040px;
  margin: 0 auto;
  padding: 30px;
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 20px 58px rgba(26, 56, 105, 0.09);
}

.shell-header {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  align-items: flex-start;
}

.status-tag {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(25, 118, 210, 0.1);
  color: #1661cc;
  font-size: 13px;
  font-weight: 600;
}

.shell-header h1 {
  margin: 16px 0 10px;
  font-size: 34px;
  color: #21314b;
}

.shell-header p {
  margin: 0;
  line-height: 1.8;
  color: #5d6f88;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.notice-alert {
  margin-top: 22px;
}

.lookup-panel {
  margin-top: 24px;
  padding: 20px;
  border-radius: 20px;
  background: #f8fbff;
  border: 1px solid #deebff;
}

.quick-link {
  margin-top: 8px;
  color: #5b708e;
}

.result-panel {
  margin-top: 24px;
}

.status-summary {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 22px;
  border-radius: 22px;
}

.status-pending {
  background: linear-gradient(135deg, rgba(255, 171, 0, 0.12), rgba(255, 224, 130, 0.2));
}

.status-approved {
  background: linear-gradient(135deg, rgba(46, 125, 50, 0.12), rgba(129, 199, 132, 0.2));
}

.status-rejected,
.status-supplement {
  background: linear-gradient(135deg, rgba(229, 57, 53, 0.12), rgba(255, 205, 210, 0.22));
}

.status-icon {
  width: 62px;
  height: 62px;
  border-radius: 18px;
  background: #ffffff;
  color: #23436d;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
}

.status-title {
  font-size: 24px;
  font-weight: 700;
  color: #21314b;
}

.status-desc {
  margin-top: 6px;
  color: #576a85;
  line-height: 1.7;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
  margin-top: 18px;
}

.info-card {
  padding: 22px;
  border-radius: 22px;
  background: #ffffff;
  border: 1px solid #e7effb;
}

.card-title {
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 700;
  color: #22324d;
}

.info-item {
  display: grid;
  grid-template-columns: 110px 1fr;
  gap: 14px;
  padding: 10px 0;
  border-bottom: 1px solid #eef3fb;
  color: #607089;
}

.info-item strong {
  color: #22324d;
  word-break: break-word;
}

.remark-block {
  min-height: 120px;
  padding: 16px;
  border-radius: 16px;
  background: #f8fbff;
  color: #4f6481;
  line-height: 1.8;
  white-space: pre-wrap;
}

.card-tip {
  margin-top: 14px;
  color: #6b7e98;
  line-height: 1.7;
}

.action-row {
  margin-top: 18px;
}

@media (max-width: 767px) {
  .partner-status-page {
    padding: 16px 12px 28px;
  }

  .status-shell {
    padding: 18px 16px;
  }

  .shell-header {
    flex-direction: column;
  }

  .header-actions {
    width: 100%;
    flex-direction: column;
  }

  .header-actions .el-button {
    width: 100%;
  }

  .shell-header h1 {
    font-size: 28px;
  }

  .status-summary {
    flex-direction: column;
    align-items: flex-start;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .info-item {
    grid-template-columns: 1fr;
    gap: 8px;
  }
}
</style>
