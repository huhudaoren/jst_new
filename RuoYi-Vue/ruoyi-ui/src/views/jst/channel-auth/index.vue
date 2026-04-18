<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="申请名称" prop="applyName">
        <el-input v-model="queryParams.applyName" placeholder="请输入申请名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="渠道类型" prop="channelType">
        <el-select v-model="queryParams.channelType" placeholder="全部" clearable>
          <el-option label="个人" value="personal" />
          <el-option label="机构" value="institution" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="applyStatus">
        <el-select v-model="queryParams.applyStatus" placeholder="全部" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="提交时间">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-search" size="mini" @click="showSearch = !showSearch">{{ showSearch ? '隐藏搜索' : '显示搜索' }}</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="ID" prop="applyId" width="70" />
      <el-table-column label="申请名称" prop="applyName" min-width="120" show-overflow-tooltip />
      <el-table-column label="地区" prop="region" min-width="120" show-overflow-tooltip>
        <template slot-scope="scope">
          <!-- PATCH-5: 用字典翻译 region 拼音 value → 中文 label -->
          <dict-tag v-if="scope.row.region" :options="dict.type.jst_region_province" :value="scope.row.region" />
          <span v-else class="region-empty">未设置</span>
        </template>
      </el-table-column>
      <el-table-column label="渠道类型" prop="channelType" width="90" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.channelType === 'institution' ? '' : 'info'">{{ scope.row.channelType === 'institution' ? '机构' : '个人' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" prop="applyStatus" width="100" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="statusTagType(scope.row.applyStatus)">{{ statusLabel(scope.row.applyStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="驳回次数" prop="rejectCount" width="90" align="center" />
      <el-table-column label="提交时间" prop="submitTime" width="160" align="center">
        <template slot-scope="scope">{{ parseTime(scope.row.submitTime || scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="审核时间" prop="auditTime" width="160" align="center">
        <template slot-scope="scope">{{ parseTime(scope.row.auditTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="320" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">材料</el-button>
          <el-button size="mini" type="text" icon="el-icon-check" v-if="scope.row.applyStatus === 'pending'" v-hasPermi="['jst:organizer:channelApply:approve']" @click="handleApprove(scope.row)">通过</el-button>
          <el-button size="mini" type="text" icon="el-icon-close" v-if="scope.row.applyStatus === 'pending'" v-hasPermi="['jst:organizer:channelApply:reject']" @click="handleReject(scope.row)">驳回</el-button>
          <el-button size="mini" type="text" icon="el-icon-video-pause" v-if="scope.row.applyStatus === 'approved'" v-hasPermi="['jst:organizer:channelApply:suspend']" @click="handleSuspend(scope.row)">暂停</el-button>
          <!-- PATCH-5: 编辑地区 — admin / 运营主管 -->
          <el-button size="mini" type="text" icon="el-icon-edit" v-if="canEditRegion" @click="handleEditRegion(scope.row)">编辑地区</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 驳回弹窗 -->
    <el-dialog title="驳回申请" :visible.sync="rejectOpen" width="500px" append-to-body>
      <el-form ref="rejectForm" :model="rejectForm" :rules="rejectRules" label-width="80px">
        <el-form-item label="驳回原因" prop="rejectReason">
          <el-input v-model="rejectForm.rejectReason" type="textarea" :rows="4" placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="rejectOpen = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitReject">确 定</el-button>
      </div>
    </el-dialog>

    <!-- PATCH-5: 编辑地区弹窗 -->
    <el-dialog title="修改地区" :visible.sync="regionOpen" width="420px" append-to-body>
      <el-form ref="regionForm" :model="regionForm" :rules="regionRules" label-width="80px">
        <el-form-item label="省份" prop="region">
          <el-select v-model="regionForm.region" placeholder="请选择省份" filterable style="width: 100%;">
            <el-option
              v-for="dict in dict.type.jst_region_province"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="regionOpen = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitRegion">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 材料详情抽屉 -->
    <el-drawer title="申请材料详情" :visible.sync="detailOpen" :size="drawerSize" direction="rtl">
      <div style="padding: 20px;" v-if="detailData">
        <el-descriptions :column="1" border size="medium">
          <el-descriptions-item label="申请ID">{{ detailData.applyId }}</el-descriptions-item>
          <el-descriptions-item label="申请名称">{{ detailData.applyName }}</el-descriptions-item>
          <el-descriptions-item label="地区">
            <!-- PATCH-5: 抽屉也用字典翻译 -->
            <dict-tag v-if="detailData.region" :options="dict.type.jst_region_province" :value="detailData.region" />
            <span v-else>--</span>
          </el-descriptions-item>
          <el-descriptions-item label="渠道类型">{{ detailData.channelType === 'institution' ? '机构' : '个人' }}</el-descriptions-item>
          <el-descriptions-item label="联系手机">{{ detailData.contactMobile }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <el-tag size="mini" :type="statusTagType(detailData.applyStatus)">{{ statusLabel(detailData.applyStatus) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="驳回次数">{{ detailData.rejectCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ parseTime(detailData.submitTime || detailData.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="审核时间">{{ parseTime(detailData.auditTime) }}</el-descriptions-item>
          <el-descriptions-item label="驳回原因" v-if="detailData.rejectReason">{{ detailData.rejectReason }}</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin: 20px 0 12px;">证件材料</h4>
        <div v-if="materialsList.length" class="materials-grid">
          <div v-for="(item, idx) in materialsList" :key="idx" class="material-item">
            <el-image :src="item.url || item" :preview-src-list="materialsUrls" fit="cover" class="material-img" />
            <div class="material-label">{{ item.label || ('材料 ' + (idx + 1)) }}</div>
          </div>
        </div>
        <el-empty v-else description="暂无材料" :image-size="80" />
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listChannelAuth, getChannelAuth, approveChannelAuth, rejectChannelAuth, suspendChannelAuth, updateChannelAuthRegion } from '@/api/admin/channel-auth'

export default {
  name: 'ChannelAuth',
  // PATCH-5: 注入省级行政区字典
  dicts: ['jst_region_province'],
  data() {
    return {
      loading: false,
      submitLoading: false,
      showSearch: true,
      list: [],
      total: 0,
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        applyName: undefined,
        channelType: undefined,
        applyStatus: undefined
      },
      statusOptions: [
        { label: '待审核', value: 'pending' },
        { label: '已通过', value: 'approved' },
        { label: '已驳回', value: 'rejected' },
        { label: '已暂停', value: 'suspended' }
      ],
      // 驳回弹窗
      rejectOpen: false,
      rejectForm: { rejectReason: '' },
      rejectRules: { rejectReason: [{ required: true, message: '请输入驳回原因', trigger: 'blur' }] },
      currentApplyId: null,
      // 详情抽屉
      detailOpen: false,
      detailData: null,
      // PATCH-5: 编辑地区弹窗
      regionOpen: false,
      regionForm: { region: '' },
      regionRules: { region: [{ required: true, message: '请选择省份', trigger: 'change' }] }
    }
  },
  computed: {
    // PATCH-5: 权限 — admin 或 jst_operator 可编辑
    canEditRegion() {
      const roles = (this.$store && this.$store.getters && this.$store.getters.roles) || []
      return roles.includes('admin') || roles.includes('jst_operator')
    },
    drawerSize() {
      return window.innerWidth <= 768 ? '100%' : '500px'
    },
    materialsList() {
      if (!this.detailData) return []
      try {
        const raw = this.detailData.materialsJson
        if (!raw) return []
        const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw
        return Array.isArray(parsed) ? parsed : [parsed]
      } catch { return [] }
    },
    materialsUrls() {
      return this.materialsList.map(item => item.url || item)
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      const params = this.addDateRange(this.queryParams, this.dateRange, 'submitTime')
      listChannelAuth(params).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.handleQuery()
    },
    statusTagType(status) {
      const map = { pending: 'warning', approved: 'success', rejected: 'danger', suspended: 'info' }
      return map[status] || 'info'
    },
    statusLabel(status) {
      const map = { pending: '待审核', approved: '已通过', rejected: '已驳回', suspended: '已暂停' }
      return map[status] || status
    },
    handleApprove(row) {
      this.$confirm('确认通过「' + row.applyName + '」的渠道认证申请？', '审核确认', { type: 'warning' }).then(() => {
        return approveChannelAuth(row.applyId)
      }).then(() => {
        this.$modal.msgSuccess('审核通过')
        this.getList()
      }).catch(() => {})
    },
    handleReject(row) {
      this.currentApplyId = row.applyId
      this.rejectForm.rejectReason = ''
      this.rejectOpen = true
    },
    submitReject() {
      this.$refs.rejectForm.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        rejectChannelAuth(this.currentApplyId, this.rejectForm).then(() => {
          this.$modal.msgSuccess('已驳回')
          this.rejectOpen = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    handleSuspend(row) {
      this.$confirm('确认暂停「' + row.applyName + '」的渠道资格？暂停后该渠道无法继续运营。', '暂停确认', { type: 'warning' }).then(() => {
        return suspendChannelAuth(row.applyId)
      }).then(() => {
        this.$modal.msgSuccess('已暂停')
        this.getList()
      }).catch(() => {})
    },
    handleDetail(row) {
      this.detailData = row
      this.detailOpen = true
      // 如果需要更详细的信息，可以再请求一次详情
      getChannelAuth(row.applyId).then(res => {
        this.detailData = res.data || row
      }).catch(() => {})
    },
    // PATCH-5: 打开编辑地区弹窗
    handleEditRegion(row) {
      this.currentApplyId = row.applyId
      this.regionForm.region = row.region || ''
      this.regionOpen = true
      this.$nextTick(() => {
        if (this.$refs.regionForm) this.$refs.regionForm.clearValidate()
      })
    },
    submitRegion() {
      this.$refs.regionForm.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        updateChannelAuthRegion(this.currentApplyId, this.regionForm.region).then(() => {
          this.$modal.msgSuccess('地区已更新')
          this.regionOpen = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    }
  }
}
</script>

<style scoped>
.materials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}
.material-item {
  text-align: center;
}
.material-img {
  width: 100%;
  height: 120px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}
.material-label {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
}
/* PATCH-5: 未设置地区灰化提示 */
.region-empty {
  color: #c0c4cc;
  font-size: 12px;
}
</style>
