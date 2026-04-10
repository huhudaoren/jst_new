<template>
  <div class="partner-apply-page">
    <div class="hero-section">
      <div class="hero-inner">
        <div class="hero-copy">
          <div class="hero-brand">
            <img :src="brandLogo" alt="logo" class="brand-logo">
            <span class="brand-text">{{ title }}</span>
          </div>
          <div class="hero-tag">赛事方公开申请入口</div>
          <h1>入驻竞赛通，快速开通赛事方工作台</h1>
          <p>
            在线提交赛事方资料，审核通过后接入独立后台、赛事配置、报名审核和结算能力。
            当前公开查询按申请单号进行，提交后请妥善保存系统生成的申请编号。
          </p>
          <div class="hero-actions">
            <el-button type="primary" size="medium" @click="goApply">立即申请</el-button>
            <el-button plain size="medium" @click="goStatus">查看申请进度</el-button>
          </div>
          <div v-if="lastApplyNo" class="last-apply-card">
            <span>最近一次申请单号：{{ lastApplyNo }}</span>
            <el-button type="text" @click="goLastStatus">直接查看</el-button>
          </div>
        </div>

        <div class="hero-panel">
          <div class="panel-card lookup-card">
            <div class="panel-title">查询申请进度</div>
            <p>输入提交成功后生成的申请单号，查看当前审核状态与处理意见。</p>
            <el-form ref="lookupForm" :model="lookupForm" :rules="lookupRules" label-position="top">
              <el-form-item label="申请单号" prop="applyNo">
                <el-input
                  v-model.trim="lookupForm.applyNo"
                  placeholder="例如：PA202604100001"
                  clearable
                  @keyup.enter.native="handleLookup"
                />
              </el-form-item>
            </el-form>
            <el-button type="primary" :loading="lookupLoading" @click="handleLookup">立即查询</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="content-section">
      <div class="benefit-grid">
        <div v-for="item in benefitList" :key="item.title" class="benefit-card">
          <div class="benefit-icon">{{ item.icon }}</div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.desc }}</p>
        </div>
      </div>

      <div class="timeline-card">
        <div class="section-title">申请流程</div>
        <el-steps :active="2" finish-status="success" simple class="flow-steps">
          <el-step title="在线填报资料" />
          <el-step title="平台审核" />
          <el-step title="开通赛事方账号" />
        </el-steps>
        <div class="timeline-note">
          审核通过后，请关注申请联系人手机号与邮箱的后续通知。若被驳回或要求补件，可根据状态页提示重新整理资料后再次提交。
        </div>
      </div>
    </div>

    <div class="footer-bar">{{ footerContent }}</div>
  </div>
</template>

<script>
import cache from '@/plugins/cache'
import defaultSettings from '@/settings'
import { getPartnerApplyStatus } from '@/api/partner/apply'

const LAST_APPLY_NO_KEY = 'partnerApply:lastApplyNo'

export default {
  name: 'PartnerApplyEntry',
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      footerContent: defaultSettings.footerContent,
      brandLogo: require('@/assets/images/light.svg'),
      lastApplyNo: cache.local.get(LAST_APPLY_NO_KEY) || '',
      lookupLoading: false,
      lookupForm: {
        applyNo: cache.local.get(LAST_APPLY_NO_KEY) || ''
      },
      lookupRules: {
        applyNo: [
          { required: true, message: '请输入申请单号', trigger: 'blur' }
        ]
      },
      benefitList: [
        {
          icon: '01',
          title: '独立赛事方工作台',
          desc: '审核通过后开通赛事方账号，进入专属后台管理赛事、报名与成绩发布。'
        },
        {
          icon: '02',
          title: '数据隔离与权限控制',
          desc: '赛事方查询与操作数据独立隔离，后续业务均在独立工作台范围内完成。'
        },
        {
          icon: '03',
          title: '标准化审核流程',
          desc: '公开申请、平台审核、补件反馈和账号开通形成闭环，减少线下沟通成本。'
        }
      ]
    }
  },
  methods: {
    goApply() {
      this.$router.push('/partner-apply/form')
    },
    goStatus() {
      this.$router.push('/partner-apply/status')
    },
    goLastStatus() {
      if (!this.lastApplyNo) {
        return
      }
      this.$router.push({ path: '/partner-apply/status', query: { applyNo: this.lastApplyNo } })
    },
    handleLookup() {
      this.$refs.lookupForm.validate(valid => {
        if (!valid) {
          return
        }
        this.lookupLoading = true
        getPartnerApplyStatus(this.lookupForm.applyNo).then(() => {
          cache.local.set(LAST_APPLY_NO_KEY, this.lookupForm.applyNo)
          this.lastApplyNo = this.lookupForm.applyNo
          this.$router.push({ path: '/partner-apply/status', query: { applyNo: this.lookupForm.applyNo } })
        }).finally(() => {
          this.lookupLoading = false
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.partner-apply-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(30, 136, 229, 0.14), transparent 28%),
    linear-gradient(180deg, #f4f8ff 0%, #ffffff 42%, #f8fbff 100%);
  color: #20304a;
}

.hero-section {
  padding: 48px 24px 20px;
}

.hero-inner,
.content-section {
  max-width: 1180px;
  margin: 0 auto;
}

.hero-inner {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(320px, 420px);
  gap: 28px;
  align-items: stretch;
}

.hero-copy {
  padding: 40px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.84);
  box-shadow: 0 24px 60px rgba(32, 48, 74, 0.08);
  backdrop-filter: blur(10px);
}

.hero-brand {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 18px;
}

.brand-logo {
  width: 38px;
  height: 38px;
}

.brand-text {
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.hero-tag {
  display: inline-flex;
  padding: 6px 14px;
  border-radius: 999px;
  background: rgba(30, 136, 229, 0.1);
  color: #1e65c9;
  font-size: 13px;
  font-weight: 600;
}

.hero-copy h1 {
  margin: 18px 0 14px;
  font-size: 42px;
  line-height: 1.16;
  letter-spacing: -0.02em;
}

.hero-copy p {
  margin: 0;
  font-size: 16px;
  line-height: 1.8;
  color: #55667f;
}

.hero-actions {
  display: flex;
  gap: 12px;
  margin-top: 28px;
}

.last-apply-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 18px;
  padding: 14px 16px;
  border-radius: 16px;
  background: #f7fbff;
  border: 1px solid #d7e8ff;
  color: #29466f;
}

.hero-panel {
  display: flex;
}

.panel-card {
  width: 100%;
  padding: 30px 28px;
  border-radius: 28px;
  background: linear-gradient(180deg, #0f4fa8 0%, #163d7a 100%);
  color: #ffffff;
  box-shadow: 0 24px 60px rgba(18, 54, 105, 0.18);
}

.panel-title {
  font-size: 24px;
  font-weight: 700;
}

.lookup-card p {
  margin: 12px 0 18px;
  color: rgba(255, 255, 255, 0.78);
  line-height: 1.7;
}

.lookup-card ::v-deep .el-form-item__label {
  color: rgba(255, 255, 255, 0.82);
  padding-bottom: 8px;
}

.lookup-card ::v-deep .el-input__inner {
  border-radius: 14px;
  border: none;
  height: 46px;
}

.lookup-card .el-button {
  width: 100%;
  margin-top: 6px;
  height: 46px;
  border-radius: 14px;
}

.content-section {
  padding: 12px 24px 72px;
}

.benefit-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.benefit-card,
.timeline-card {
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 18px 48px rgba(26, 56, 105, 0.08);
}

.benefit-card {
  padding: 28px;
}

.benefit-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: linear-gradient(135deg, #1e88e5 0%, #0f5bd7 100%);
  color: #ffffff;
  font-size: 18px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.benefit-card h3 {
  margin: 18px 0 10px;
  font-size: 20px;
}

.benefit-card p {
  margin: 0;
  color: #607189;
  line-height: 1.75;
}

.timeline-card {
  margin-top: 22px;
  padding: 28px;
}

.section-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 18px;
}

.flow-steps {
  border-radius: 18px;
  overflow: hidden;
}

.timeline-note {
  margin-top: 18px;
  padding: 14px 16px;
  border-radius: 16px;
  background: #f7faff;
  color: #56677f;
  line-height: 1.7;
}

.footer-bar {
  padding: 18px 24px 26px;
  text-align: center;
  color: #7d8da7;
  font-size: 13px;
}

@media (max-width: 960px) {
  .hero-inner {
    grid-template-columns: 1fr;
  }

  .benefit-grid {
    grid-template-columns: 1fr;
  }

  .hero-copy {
    padding: 28px 22px;
  }

  .hero-copy h1 {
    font-size: 32px;
  }
}

@media (max-width: 767px) {
  .hero-section {
    padding: 20px 14px 10px;
  }

  .content-section {
    padding: 10px 14px 48px;
  }

  .hero-actions {
    flex-direction: column;
  }

  .last-apply-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-copy h1 {
    font-size: 28px;
  }
}
</style>
