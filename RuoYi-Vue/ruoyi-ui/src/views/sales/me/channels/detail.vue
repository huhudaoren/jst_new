<template>
  <div class="app-container">
    <el-page-header @back="goBack" content="渠道详情" style="margin-bottom:20px" />

    <div v-loading="loading">
      <el-row :gutter="16">
        <el-col :xs="24" :sm="10">
          <el-card shadow="never" style="margin-bottom:16px">
            <div slot="header">基本信息</div>
            <el-descriptions :column="1" border size="small">
              <el-descriptions-item label="渠道ID">{{ detail.channelId || '--' }}</el-descriptions-item>
              <el-descriptions-item label="渠道名称">{{ detail.channelName || '--' }}</el-descriptions-item>
              <el-descriptions-item label="渠道类型">
                <el-tag size="mini" :type="detail.channelType === 'institution' ? '' : 'info'">
                  {{ detail.channelType === 'institution' ? '机构' : '个人' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="联系手机">{{ detail.contactMobile || '--' }}</el-descriptions-item>
              <el-descriptions-item label="认证状态">
                <el-tag size="mini" :type="detail.authStatus === 'approved' ? 'success' : 'info'">
                  {{ detail.authStatus === 'approved' ? '已认证' : '未认证' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="绑定学生">{{ detail.bindStudentCount || 0 }}</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ detail.createTime || '--' }}</el-descriptions-item>
            </el-descriptions>
          </el-card>

          <el-card shadow="never" style="margin-bottom:16px">
            <div slot="header">渠道标签</div>
            <sales-tag-chip v-if="channelId" :channel-id="channelId" :editable="true" />
          </el-card>

          <el-card shadow="never">
            <div slot="header">渠道画像</div>
            <el-empty description="渠道画像功能 plan-04 补充" :image-size="60">
              <el-button type="text" @click="goProfile">前往渠道画像</el-button>
            </el-empty>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="14">
          <el-card shadow="never">
            <followup-timeline v-if="channelId" :channel-id="channelId" ref="timeline" />
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { getChannelDetail } from '@/api/sales/me/channel'
import SalesTagChip from '@/components/sales/SalesTagChip.vue'
import FollowupTimeline from '@/components/sales/FollowupTimeline.vue'

export default {
  name: 'SalesChannelDetail',
  components: { SalesTagChip, FollowupTimeline },
  data() {
    return {
      loading: false,
      detail: {},
      channelId: null
    }
  },
  created() {
    this.channelId = parseInt(this.$route.params.id || this.$route.query.id)
    if (this.channelId) {
      this.loadDetail()
    }
  },
  methods: {
    loadDetail() {
      this.loading = true
      getChannelDetail(this.channelId).then(res => {
        this.detail = res.data || {}
      }).catch(() => {}).finally(() => { this.loading = false })
    },
    goBack() {
      this.$router.go(-1)
    },
    goProfile() {
      this.$router.push(`/sales-workbench/channels/profile?channelId=${this.channelId}`)
    }
  }
}
</script>
