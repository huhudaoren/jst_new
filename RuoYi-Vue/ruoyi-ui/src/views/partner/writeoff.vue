<template>
  <div class="writeoff-page">
    <div class="top-bar">
      <div>
        <p class="eyebrow">现场核销</p>
        <h2>{{ currentContext.contestName }}</h2>
        <p>{{ currentContext.dateText }} · {{ currentContext.sessionCode }}</p>
      </div>
      <el-button icon="el-icon-refresh" :loading="recordLoading" @click="loadRecent">刷新</el-button>
    </div>

    <div class="stats-grid">
      <div class="stats-item">
        <span>已核销</span>
        <strong>{{ stats.used }}</strong>
      </div>
      <div class="stats-item">
        <span>总人数</span>
        <strong>{{ stats.total }}</strong>
      </div>
      <div class="stats-item">
        <span>剩余</span>
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
      <el-button class="manual-button" icon="el-icon-edit" @click="showManual = true">手工输入二维码内容</el-button>
      <p class="scan-tip">摄像头不可用时，可复制二维码内容后手工提交。</p>
    </div>

    <div v-if="showManual" class="manual-panel">
      <el-input
        v-model="manualPayload"
        type="textarea"
        :rows="4"
        placeholder="粘贴二维码内容"
        maxlength="512"
        show-word-limit
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

    <div class="records-panel">
      <div class="section-head">
        <span>最近核销</span>
        <small>最多展示 20 条</small>
      </div>
      <div v-loading="recordLoading" class="records-list">
        <div v-if="records.length">
          <div v-for="item in records" :key="item.writeoffItemId + '-' + item.writeoffTime" class="record-item">
            <div>
              <strong>{{ item.memberName || '未命名成员' }}</strong>
              <p>{{ item.itemName || item.itemType || '核销项目' }}</p>
              <p>{{ item.contestName || '--' }} · {{ item.sessionCode || '--' }}</p>
            </div>
            <span>{{ parseTime(item.writeoffTime) || '--' }}</span>
          </div>
        </div>
        <el-empty v-else description="暂无核销记录" :image-size="96" />
      </div>
    </div>

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
      lastResult: null,
      lastScanData: null,
      stream: null,
      detector: null,
      scanTimer: null,
      scannerTip: '请将二维码置于取景框内'
    }
  },
  computed: {
    currentContext() {
      const first = this.records[0] || {}
      return {
        contestName: this.$route.query.contestName || first.contestName || '现场赛事',
        dateText: this.$route.query.date || first.appointmentDate || '今日',
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
    this.loadRecent()
  },
  beforeDestroy() {
    this.stopScanner()
  },
  methods: {
    async loadRecent() {
      this.recordLoading = true
      try {
        const res = await getWriteoffRecent()
        this.records = res.rows || []
      } finally {
        this.recordLoading = false
      }
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
      const value = (this.manualPayload || '').trim()
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
          message: data.teamStatus ? '团队预约进度已更新' : '该项目已完成核销',
          memberName: data.memberName,
          itemName: data.itemName
        }
        this.manualPayload = ''
        this.showManual = false
        await this.loadRecent()
      } catch (error) {
        this.lastResult = {
          success: false,
          message: '已核销过或二维码无效'
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

<style scoped>
.writeoff-page {
  min-height: 100vh;
  padding: 16px;
  color: #172033;
  background: #f6f8fb;
}

.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 18px;
  background: #ffffff;
  border: 1px solid #e4e9f2;
  border-radius: 8px;
}

.top-bar h2 {
  margin: 0;
  font-size: 22px;
}

.top-bar p {
  margin: 6px 0 0;
  color: #6f7b8f;
}

.eyebrow {
  margin: 0 0 6px !important;
  color: #2f6fec !important;
  font-size: 13px;
  font-weight: 700;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin: 14px 0;
}

.stats-item,
.scan-panel,
.manual-panel,
.result-card,
.records-panel {
  background: #ffffff;
  border: 1px solid #e4e9f2;
  border-radius: 8px;
}

.stats-item {
  padding: 16px 10px;
  text-align: center;
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
.manual-panel {
  display: grid;
  gap: 12px;
  padding: 16px;
}

.scan-button,
.manual-button,
.manual-panel .el-button {
  width: 100%;
  min-height: 52px;
  font-size: 16px;
  border-radius: 8px;
}

.scan-tip,
.scanner-tip {
  margin: 0;
  color: #7a8495;
  font-size: 13px;
}

.manual-panel {
  margin-top: 14px;
}

.result-card {
  display: flex;
  gap: 12px;
  margin-top: 14px;
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

.records-panel {
  margin-top: 14px;
  padding: 16px;
}

.section-head,
.record-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.section-head {
  margin-bottom: 10px;
  font-weight: 700;
}

.section-head small {
  color: #7a8495;
  font-weight: 400;
}

.record-item {
  min-height: 58px;
  padding: 12px 0;
  border-top: 1px solid #eef2f7;
}

.record-item:first-child {
  border-top: 0;
}

.record-item p {
  margin: 4px 0 0;
  color: #7a8495;
  font-size: 13px;
}

.record-item > span {
  flex-shrink: 0;
  color: #7a8495;
  font-size: 12px;
  text-align: right;
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

@media (min-width: 769px) {
  .writeoff-page {
    max-width: 720px;
    min-height: calc(100vh - 84px);
    margin: 0 auto;
    padding: 24px;
  }
}

@media (max-width: 520px) {
  .top-bar {
    display: block;
  }

  .top-bar .el-button {
    width: 100%;
    min-height: 44px;
    margin-top: 14px;
  }
}
</style>
