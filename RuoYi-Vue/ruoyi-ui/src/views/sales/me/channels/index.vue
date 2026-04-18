<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="渠道名称" prop="channelName">
        <el-input v-model="queryParams.channelName" placeholder="请输入渠道名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="渠道名称" prop="channelName" min-width="140" show-overflow-tooltip />
      <el-table-column label="联系手机" width="140" align="center">
        <template slot-scope="scope">
          <span v-if="scope.row._phoneFull">{{ scope.row._phoneFull }}</span>
          <span v-else>{{ maskPhone(scope.row.contactMobile) }}</span>
          <el-button
            v-if="!scope.row._phoneFull"
            type="text"
            size="mini"
            icon="el-icon-view"
            style="margin-left:4px"
            @click="showPhone(scope.row)"
          >查看完整</el-button>
        </template>
      </el-table-column>
      <el-table-column label="标签" min-width="180">
        <template slot-scope="scope">
          <sales-tag-chip :channel-id="scope.row.channelId" :editable="true" />
        </template>
      </el-table-column>
      <el-table-column label="最近联系" prop="lastFollowupAt" width="130" align="center">
        <template slot-scope="scope">{{ formatDate(scope.row.lastFollowupAt) }}</template>
      </el-table-column>
      <el-table-column label="渠道类型" prop="channelType" width="90" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.channelType === 'institution' ? '' : 'info'">
            {{ scope.row.channelType === 'institution' ? '机构' : '个人' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="goDetail(scope.row)">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-chat-dot-round" @click="openFollowup(scope.row)">新建跟进</el-button>
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

    <!-- 新建跟进弹窗 -->
    <followup-form
      v-if="followupChannelId"
      :visible.sync="followupVisible"
      :channel-id="followupChannelId"
      @saved="getList"
    />
  </div>
</template>

<script>
import { listMyChannels, getPhoneFull } from '@/api/sales/me/channel'
import SalesTagChip from '@/components/sales/SalesTagChip.vue'
import FollowupForm from '@/components/sales/FollowupForm.vue'

export default {
  name: 'SalesMyChannels',
  components: { SalesTagChip, FollowupForm },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      showSearch: true,
      followupVisible: false,
      followupChannelId: null,
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        channelName: ''
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listMyChannels(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).catch(() => {}).finally(() => { this.loading = false })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    goDetail(row) {
      this.$router.push(`/sales-workbench/channels/${row.channelId}`)
    },
    openFollowup(row) {
      this.followupChannelId = row.channelId
      this.followupVisible = true
    },
    maskPhone(phone) {
      if (!phone) return '--'
      return String(phone).replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
    },
    showPhone(row) {
      getPhoneFull(row.channelId).then(res => {
        this.$set(row, '_phoneFull', res.data || row.contactMobile)
      }).catch(() => {
        this.$set(row, '_phoneFull', row.contactMobile)
      })
    },
    formatDate(val) {
      if (!val) return '--'
      return String(val).substring(0, 10)
    }
  }
}
</script>
