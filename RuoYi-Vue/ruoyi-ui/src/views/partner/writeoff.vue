<template>
  <div class="app-container partner-writeoff-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">现场核销</p>
        <h2>{{ currentContext.contestName }}</h2>
        <p class="hero-desc">{{ currentContext.dateText }} · {{ currentContext.sessionCode }}</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="recordLoading" @click="getList">刷新记录</el-button>
    </div>

    <div class="stats-grid">
      <div class="stats-item">
        <span>本页已核销</span>
        <strong>{{ stats.used }}</strong>
      </div>
      <div class="stats-item">
        <span>团队总人数</span>
        <strong>{{ stats.total }}</strong>
      </div>
      <div class="stats-item">
        <span>团队剩余</span>
        <strong>{{ stats.remain }}</strong>
      </div>
    </div>

    <div class="scan-panel">
      <el-button
        class="scan-button"
        type="primary"
        icon="el-icon-camera"
        :loading="scanLoading"
        @click="startScanner"
      >
        扫码核销
      </el-button>
      <el-button class="manual-button" icon="el-icon-edit" @click="showManual = true">手工输入二维码</el-button>
      <p class="scan-tip">摄像头不可用时，可复制二维码内容后手工提交。</p>
    </div>

    <div v-if="showManual" class="manual-panel">
      <el-input
        v-model.trim="manualPayload"
        type="textarea"
        :rows="4"
        maxlength="512"
        show-word-limit
        placeholder="粘贴二维码内容"
      />
      <el-button type="primary" :loading="scanLoading" @click="submitManual">确认核销</el-button>
    </div>

    <div v-if="lastResult" class="result-card" :class="{ 'is-error': !lastResult.success }">
      <i :class="lastResult.success ? 'el-icon-success' : 'el-icon-error'" />
      <div>
        <strong>{{ lastResult.success ? '核销成功' : '核销失败' }}</strong>
        <p>{{ lastResult.message }}</p>
        <p v-if="lastResult.success">{{ lastResult.memberName || '--' }} · {{ lastResult.itemName || '--' }}</p>
      </div>
    </div>

    <el-form
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="82px"
      class="query-panel"
    >
      <el-form-item label="赛事ID" prop="contestId">
        <el-input
          v-model.trim="queryParams.contestId"
          placeholder="输入赛事ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="核销时间">
        <el-date-picker
          v-model="writeoffRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div v-if="isMobile" v-loading="recordLoading" class="mobile-list">
      <div v-if="records.length">
        <div v-for="item in records" :key="item.writeoffItemId + '-' + item.writeoffTime" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ item.memberName || item.teamName || '未命名成员' }}</div>
              <div class="mobile-sub">{{ item.itemName || item.itemType || '核销项目' }}</div>
            </div>
            <JstStatusBadge :status="String(item.status || '')" :status-map="statusMap" />
          </div>
          <div class="mobile-info-row">赛事：{{ item.contestName || '--' }}</div>
          <div class="mobile-info-row">场次：{{ item.sessionCode || '--' }}</div>
          <div class="mobile-info-row">核销终端：{{ item.writeoffTerminal || '--' }}</div>
          <div class="mobile-info-row">核销时间：{{ parseTime(item.writeoffTime) || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(item)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无核销记录" :image-size="96" />
    </div>

    <el-table v-else v-loading="recordLoading" :data="records">
      <template slot="empty">
        <el-empty description="暂无核销记录" :image-size="96" />
      </template>
      <el-table-column label="核销ID" prop="writeoffItemId" min-width="96" />
      <el-table-column label="赛事" prop="contestName" min-width="180" show-overflow-tooltip />
      <el-table-column label="成员/团队" min-width="140" show-overflow-tooltip>
        <template slot-scope="scope">
          {{ scope.row.memberName || scope.row.teamName || '--' }}
        </template>
      </el-table-column>
      <el-table-column label="核销项目" min-width="140" show-overflow-tooltip>
        <template slot-scope="scope">
          {{ scope.row.itemName || scope.row.itemType || '--' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" min-width="100">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.status || '')" :status-map="statusMap" />
        </template>
      </el-table-column>
      <el-table-column label="场次" prop="sessionCode" min-width="120" show-overflow-tooltip />
      <el-table-column label="核销终端" prop="writeoffTerminal" min-width="120" />
      <el-table-column label="核销时间" min-width="170">
        <template slot-scope="scope">{{ parseTime(scope.row.writeoffTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
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

    <el-drawer
      title="核销详情"
      :visible.sync="detailVisible"
      :size="isMobile ? '100%' : '46%'"
      append-to-body
    >
      <div class="drawer-body">
        <el-descriptions v-if="detailData" :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="核销ID">{{ detailData.writeoffItemId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="赛事ID">{{ detailData.contestId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="赛事名称">{{ detailData.contestName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="场次编码">{{ detailData.sessionCode || '--' }}</el-descriptions-item>
          <el-descriptions-item label="成员">{{ detailData.memberName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="团队">{{ detailData.teamName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="核销项目">{{ detailData.itemName || detailData.itemType || '--' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <JstStatusBadge :status="String(detailData.status || '')" :status-map="statusMap" />
          </el-descriptions-item>
          <el-descriptions-item label="预约日期">{{ parseTime(detailData.appointmentDate, '{y}-{m}-{d}') || '--' }}</el-descriptions-item>
          <el-descriptions-item label="核销时间">{{ parseTime(detailData.writeoffTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="核销终端">{{ detailData.writeoffTerminal || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>

    <el-dialog
      title="扫码核销"
      :visible.sync="scannerVisible"
      width="420px"
      append-to-body
      @close="stopScanner"
    >
      <div class="scanner-box">
        <video ref="video" autoplay playsinline muted />
        <canvas ref="canvas" class="hidden-canvas" />
      </div>
      <p class="scanner-tip">{{ scannerTip }}</p>
      <div slot="footer">
        <el-button @click="stopScanner">关闭</el-button>
        <el-button type="primary" @click="showManualFromScanner">手工输入</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getWriteoffRecent, scanWriteoff } from '@/api/wx/writeoff'

export default {
  name: 'PartnerWriteoff',
  data() {
    return {
      recordLoading: false,
      scanLoading: false,
      scannerVisible: false,
      showManual: false,
      manualPayload: '',
      records: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestId: undefined
      },
      writeoffRange: [],
      detailVisible: false,
      detailData: null,
      lastResult: null,
      lastScanData: null,
      stream: null,
      detector: null,
      scanTimer: null,
      scannerTip: '请将二维码置于取景框内',
      statusMap: {
        used: { label: '已核销', type: 'success' },
        unused: { label: '未核销', type: 'info' },
        expired: { label: '已过期', type: 'warning' },
        voided: { label: '已作废', type: 'danger' }
      }
    }
  },
  computed: {
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    },
    currentContext() {
      const first = this.records[0] || {}
      return {
        contestName: this.$route.query.contestName || first.contestName || '现场赛事',
        dateText: this.$route.query.date || (this.writeoffRange && this.writeoffRange.length ? this.writeoffRange.join(' 至 ') : '今日'),
        sessionCode: this.$route.query.sessionCode || first.sessionCode || '全部场次'
      }
    },
    stats() {
      if (this.lastScanData && this.lastScanData.teamTotalPersons != null) {
        const used = Number(this.lastScanData.teamWriteoffPersons || 0)
        const total = Number(this.lastScanData.teamTotalPersons || 0)
        return {
          used,
          total,
          remain: Math.max(total - used, 0)
        }
      }
      return {
        used: this.records.length,
        total: '--',
        remain: '--'
      }
    }
  },
  created() {
    this.getList()
  },
  beforeDestroy() {
    this.stopScanner()
  },
  methods: {
    async getList() {
      this.recordLoading = true
      try {
        const params = {
          ...this.queryParams
        }
        if (this.writeoffRange && this.writeoffRange.length === 2) {
          params.beginDate = this.writeoffRange[0]
          params.endDate = this.writeoffRange[1]
        }
        const res = await getWriteoffRecent(params)
        this.records = Array.isArray(res.rows) ? res.rows : []
        this.total = Number(res.total || 0)
      } finally {
        this.recordLoading = false
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.writeoffRange = []
      this.queryParams.pageNum = 1
      this.getList()
    },
    openDetail(row) {
      this.detailData = row || null
      this.detailVisible = true
    },
    async startScanner() {
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia || !window.BarcodeDetector) {
        this.showManual = true
        this.$modal.msgWarning('当前浏览器不支持扫码，已切换到手工输入')
        return
      }
      this.scanLoading = true
      try {
        this.detector = new window.BarcodeDetector({ formats: ['qr_code'] })
        this.stream = await navigator.mediaDevices.getUserMedia({
          video: { facingMode: { ideal: 'environment' } },
          audio: false
        })
        this.scannerVisible = true
        this.$nextTick(() => {
          const video = this.$refs.video
          if (!video) return
          video.srcObject = this.stream
          video.play()
          this.scanTimer = window.setInterval(this.detectFrame, 600)
        })
      } catch (error) {
        this.showManual = true
        this.$modal.msgWarning('摄像头不可用，已切换到手工输入')
      } finally {
        this.scanLoading = false
      }
    },
    async detectFrame() {
      const video = this.$refs.video
      const canvas = this.$refs.canvas
      if (!video || !canvas || !this.detector || video.readyState < 2) {
        return
      }
      const width = video.videoWidth
      const height = video.videoHeight
      if (!width || !height) {
        return
      }
      canvas.width = width
      canvas.height = height
      canvas.getContext('2d').drawImage(video, 0, 0, width, height)
      try {
        const codes = await this.detector.detect(canvas)
        if (codes && codes.length) {
          const value = codes[0].rawValue
          this.stopScanner()
          this.submitPayload(value)
        }
      } catch (error) {
        this.scannerTip = '识别中，请保持二维码清晰'
      }
    },
    submitManual() {
      const value = String(this.manualPayload || '').trim()
      if (!value) {
        this.$modal.msgWarning('请先输入二维码内容')
        return
      }
      this.submitPayload(value)
    },
    async submitPayload(value) {
      this.scanLoading = true
      try {
        const res = await scanWriteoff(value)
        const data = (res && res.data) || {}
        this.lastScanData = data
        this.lastResult = {
          success: true,
          message: data.teamStatus ? '团队预约进度已更新' : '核销成功',
          memberName: data.memberName,
          itemName: data.itemName
        }
        this.manualPayload = ''
        this.showManual = false
        this.queryParams.pageNum = 1
        await this.getList()
      } catch (error) {
        this.lastResult = {
          success: false,
          message: (error && error.msg) || '已核销过或二维码无效'
        }
      } finally {
        this.scanLoading = false
      }
    },
    stopScanner() {
      if (this.scanTimer) {
        window.clearInterval(this.scanTimer)
        this.scanTimer = null
      }
      if (this.stream) {
        this.stream.getTracks().forEach(track => track.stop())
        this.stream = null
      }
      this.scannerVisible = false
    },
    showManualFromScanner() {
      this.stopScanner()
      this.showManual = true
    }
  }
}
</script>

<style scoped lang="scss">
.partner-writeoff-page {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.page-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
  margin-bottom: 14px;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.hero-eyebrow {
  margin: 0 0 8px;
  color: #2f6fec;
  font-size: 13px;
  font-weight: 600;
}

.page-hero h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #172033;
}

.hero-desc {
  margin: 8px 0 0;
  color: #6f7b8f;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 14px;
}

.stats-item {
  padding: 16px 10px;
  text-align: center;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.stats-item span {
  display: block;
  color: #7a8495;
  font-size: 13px;
}

.stats-item strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
}

.scan-panel,
.manual-panel,
.result-card {
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.scan-panel,
.manual-panel {
  display: grid;
  gap: 12px;
  padding: 16px;
}

.manual-panel {
  margin-top: 12px;
}

.scan-button,
.manual-button,
.manual-panel .el-button {
  width: 100%;
  min-height: 50px;
}

.scan-tip,
.scanner-tip {
  margin: 0;
  font-size: 13px;
  color: #7a8495;
}

.result-card {
  display: flex;
  gap: 12px;
  margin-top: 12px;
  margin-bottom: 14px;
  padding: 16px;
  border-color: #c8ead6;
  background: #f3fbf6;
}

.result-card i {
  color: #27ae60;
  font-size: 28px;
}

.result-card p {
  margin: 4px 0 0;
  color: #4d5b70;
}

.result-card.is-error {
  border-color: #f3c8c8;
  background: #fff6f6;
}

.result-card.is-error i {
  color: #e45656;
}

.query-panel {
  padding: 16px 16px 0;
  margin-bottom: 16px;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.mobile-list {
  min-height: 180px;
}

.mobile-card {
  padding: 16px;
  margin-bottom: 12px;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.mobile-card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.mobile-title {
  font-weight: 700;
  color: #172033;
}

.mobile-sub {
  margin-top: 4px;
  font-size: 12px;
  color: #7a8495;
}

.mobile-info-row {
  margin-top: 8px;
  color: #6f7b8f;
  font-size: 13px;
}

.mobile-actions {
  display: flex;
  gap: 16px;
  margin-top: 12px;
}

.drawer-body {
  padding: 8px 16px 16px;
}

.scanner-box {
  position: relative;
  overflow: hidden;
  background: #101828;
  border-radius: 8px;
  aspect-ratio: 1 / 1;
}

.scanner-box video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hidden-canvas {
  display: none;
}

@media (max-width: 768px) {
  .partner-writeoff-page {
    padding: 12px;
  }

  .page-hero {
    display: block;
    padding: 18px;
  }

  .page-hero .el-button {
    width: 100%;
    min-height: 44px;
    margin-top: 16px;
  }

  .page-hero h2 {
    font-size: 20px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .query-panel {
    padding-bottom: 8px;
  }

  .query-panel ::v-deep .el-form-item {
    display: block;
    margin-right: 0;
  }

  .query-panel ::v-deep .el-form-item__content,
  .query-panel ::v-deep .el-select,
  .query-panel ::v-deep .el-input,
  .query-panel ::v-deep .el-date-editor {
    width: 100%;
  }

  .mobile-actions .el-button {
    min-height: 44px;
  }
}
</style>
