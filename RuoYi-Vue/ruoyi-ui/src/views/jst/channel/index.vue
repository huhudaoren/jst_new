<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="渠道名称" prop="channelName">
        <el-input v-model="queryParams.channelName" placeholder="请输入渠道名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="渠道类型" prop="channelType">
        <el-select v-model="queryParams.channelType" placeholder="全部" clearable>
          <el-option label="个人" value="personal" />
          <el-option label="机构" value="institution" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" v-hasPermi="['jst:user:channel:export']" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="ID" prop="channelId" width="70" />
      <el-table-column label="渠道名称" prop="channelName" min-width="130" show-overflow-tooltip />
      <el-table-column label="渠道类型" prop="channelType" width="90" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.channelType === 'institution' ? '' : 'info'">{{ scope.row.channelType === 'institution' ? '机构' : '个人' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="联系手机" prop="contactMobile" width="120" align="center" />
      <el-table-column label="认证状态" prop="authStatus" width="90" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.authStatus === 'approved' ? 'success' : 'info'">{{ scope.row.authStatus === 'approved' ? '已认证' : '未认证' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="绑定学生" prop="bindStudentCount" width="90" align="center">
        <template slot-scope="scope">{{ scope.row.bindStudentCount || 0 }}</template>
      </el-table-column>
      <el-table-column label="累计返点(元)" prop="totalRebate" width="110" align="right">
        <template slot-scope="scope">{{ formatMoney(scope.row.totalRebate) }}</template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-video-pause" v-if="scope.row.status === '0'" v-hasPermi="['jst:user:channel:edit']" @click="handleToggle(scope.row, '1')">停用</el-button>
          <el-button size="mini" type="text" icon="el-icon-video-play" v-if="scope.row.status === '1'" v-hasPermi="['jst:user:channel:edit']" @click="handleToggle(scope.row, '0')">启用</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer title="渠道详情" :visible.sync="detailOpen" :size="drawerSize" direction="rtl">
      <div style="padding: 20px;" v-if="detailData">
        <el-tabs v-model="detailTab">
          <el-tab-pane label="渠道信息" name="info">
            <el-descriptions :column="1" border size="medium">
              <el-descriptions-item label="渠道ID">{{ detailData.channelId }}</el-descriptions-item>
              <el-descriptions-item label="渠道名称">{{ detailData.channelName }}</el-descriptions-item>
              <el-descriptions-item label="渠道类型">{{ detailData.channelType === 'institution' ? '机构' : '个人' }}</el-descriptions-item>
              <el-descriptions-item label="联系手机">{{ detailData.contactMobile }}</el-descriptions-item>
              <el-descriptions-item label="证件号">{{ detailData.idCardNo }}</el-descriptions-item>
              <el-descriptions-item label="营业执照号" v-if="detailData.businessLicenseNo">{{ detailData.businessLicenseNo }}</el-descriptions-item>
              <el-descriptions-item label="认证状态">
                <el-tag size="mini" :type="detailData.authStatus === 'approved' ? 'success' : 'info'">{{ detailData.authStatus === 'approved' ? '已认证' : '未认证' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="认证时间">{{ parseTime(detailData.authTime) }}</el-descriptions-item>
              <el-descriptions-item label="绑定学生数">{{ detailData.bindStudentCount || 0 }}</el-descriptions-item>
              <el-descriptions-item label="累计返点">{{ formatMoney(detailData.totalRebate) }} 元</el-descriptions-item>
              <el-descriptions-item label="渠道标签" v-if="detailData.tags">{{ detailData.tags }}</el-descriptions-item>
              <el-descriptions-item label="风控标记">
                <el-tag size="mini" :type="riskTagType(detailData.riskFlag)">{{ riskLabel(detailData.riskFlag) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="审核备注" v-if="detailData.authRemark">{{ detailData.authRemark }}</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ parseTime(detailData.createTime) }}</el-descriptions-item>
            </el-descriptions>
          </el-tab-pane>
          <el-tab-pane label="绑定用户" name="bindings">
            <el-table :data="channelBindings" v-loading="bindingLoading" border stripe size="small">
              <el-table-column label="学生姓名" prop="studentName" min-width="100" show-overflow-tooltip />
              <el-table-column label="手机号" prop="mobile" width="130" align="center" />
              <el-table-column label="绑定时间" prop="bindTime" width="160" align="center">
                <template slot-scope="scope">{{ parseTime(scope.row.bindTime || scope.row.createTime) }}</template>
              </el-table-column>
              <el-table-column label="操作" width="100" align="center">
                <template slot-scope="scope">
                  <el-button size="mini" type="text" icon="el-icon-view" @click="viewParticipant(scope.row)">查看档案</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-empty v-if="!bindingLoading && !channelBindings.length" description="暂无绑定用户" :image-size="60" />
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listChannel, getChannel, updateChannel } from '@/api/admin/channel'
import { listBinding } from '@/api/admin/binding'
import { formatMoney as formatMoneyUtil } from '@/utils/format'

export default {
  name: 'ChannelList',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        channelName: undefined,
        channelType: undefined,
        status: undefined
      },
      detailOpen: false,
      detailData: null,
      detailTab: 'info',
      channelBindings: [],
      bindingLoading: false,
      lastAutoOpenKey: ''
    }
  },
  computed: {
    drawerSize() {
      return window.innerWidth <= 768 ? '100%' : '500px'
    }
  },
  created() {
    this.getList()
  },
  watch: {
    '$route.query': {
      handler() {
        this.tryAutoOpenFromRoute()
      },
      deep: true
    },
    detailTab(val) {
      if (val === 'bindings' && this.detailData) {
        this.loadBindings(this.detailData.channelId)
      }
    }
  },
  methods: {
    getList() {
      this.loading = true
      listChannel(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
        this.tryAutoOpenFromRoute()
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    formatMoney(val) {
      if (!val && val !== 0) return '-'
      return formatMoneyUtil(val).replace('\u00A5', '')
    },
    riskTagType(flag) {
      const map = { 0: 'success', 1: 'danger', 2: 'warning' }
      return map[flag] || 'info'
    },
    riskLabel(flag) {
      const map = { 0: '正常', 1: '黑名单', 2: '待复核' }
      return map[flag] || '未知'
    },
    handleDetail(row) {
      this.detailData = row
      this.detailTab = 'info'
      this.channelBindings = []
      this.detailOpen = true
      getChannel(row.channelId).then(res => {
        this.detailData = res.data || row
      }).catch(() => {})
    },
    loadBindings(channelId) {
      if (!channelId) return
      this.bindingLoading = true
      listBinding({ channelId, pageNum: 1, pageSize: 200 }).then(res => {
        this.channelBindings = res.rows || []
      }).catch(() => {
        this.channelBindings = []
      }).finally(() => {
        this.bindingLoading = false
      })
    },
    viewParticipant(row) {
      const participantId = row.participantId || row.studentId
      if (participantId) {
        this.$router.push({ path: '/jst/participant', query: { autoOpen: '1', participantId } })
      } else {
        this.$message.info('该用户暂无参赛档案')
      }
    },
    tryAutoOpenFromRoute() {
      const query = this.$route.query || {}
      if (query.autoOpen !== '1' || !query.channelId) return
      const key = `channel-${query.channelId}-${query.autoOpen}`
      if (this.lastAutoOpenKey === key) return
      const target = this.list.find(item => String(item.channelId) === String(query.channelId))
      const channelId = Number(query.channelId)
      if (!target && !channelId) return
      this.lastAutoOpenKey = key
      this.handleDetail(target || { channelId })
    },
    handleToggle(row, newStatus) {
      const action = newStatus === '1' ? '停用' : '启用'
      this.$confirm('确认' + action + '渠道「' + row.channelName + '」？', action + '确认', { type: 'warning' }).then(() => {
        return updateChannel({ channelId: row.channelId, status: newStatus })
      }).then(() => {
        this.$modal.msgSuccess(action + '成功')
        this.getList()
      }).catch(() => {})
    },
    handleExport() {
      this.download('/jst/user/jst_channel/export', this.queryParams, '渠道列表.xlsx')
    }
  }
}
</script>
