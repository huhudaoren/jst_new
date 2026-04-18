<template>
  <div class="app-container">
    <el-page-header @back="goBack" content="渠道画像" style="margin-bottom:20px" />

    <el-alert
      type="info"
      title="渠道画像功能将在 plan-04 完善，届时将展示跟进次数、成交分析、活跃度趋势等数据。"
      :closable="false"
      style="margin-bottom:16px"
    />

    <el-row :gutter="16" v-loading="loading">
      <el-col :xs="8" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">跟进次数</div>
          <div class="stat-card__value">{{ profile.followupCount != null ? profile.followupCount : '--' }}</div>
        </div>
      </el-col>
      <el-col :xs="8" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">成交笔数</div>
          <div class="stat-card__value">{{ profile.orderCount != null ? profile.orderCount : '--' }}</div>
        </div>
      </el-col>
      <el-col :xs="8" :sm="6">
        <div class="stat-card">
          <div class="stat-card__label">覆盖业务类型</div>
          <div class="stat-card__value">{{ profile.bizTypeCount != null ? profile.bizTypeCount : '--' }}</div>
        </div>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top:16px">
      <div slot="header">活跃度趋势</div>
      <el-empty description="活跃度趋势图 plan-04 补充（需 ECharts 折线图接入）" :image-size="80" />
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'SalesChannelProfile',
  data() {
    return {
      loading: false,
      channelId: null,
      profile: {}
    }
  },
  created() {
    this.channelId = parseInt(this.$route.query.channelId)
    // plan-04 会补 /sales/me/channels/{id}/profile 接口
    // this.loadProfile()
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.stat-card {
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
}
.stat-card__label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}
.stat-card__value {
  font-size: 28px;
  font-weight: 700;
  color: #172033;
}
</style>
