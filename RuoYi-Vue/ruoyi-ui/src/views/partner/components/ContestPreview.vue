<template>
  <el-dialog
    title="手机预览"
    :visible.sync="dialogVisible"
    width="435px"
    custom-class="preview-dialog"
    append-to-body
    @close="$emit('update:visible', false)"
  >
    <div class="phone-frame">
      <div class="phone-screen">
        <!-- Banner / Cover -->
        <div class="preview-banner">
          <img v-if="data.bannerImage || data.coverImage" :src="data.bannerImage || data.coverImage" class="banner-img" />
          <div v-else class="banner-placeholder">暂无封面</div>
          <div class="banner-overlay">
            <div class="preview-title">{{ data.contestName || '赛事名称' }}</div>
          </div>
        </div>

        <!-- Info Section -->
        <div class="preview-section">
          <div class="info-row" v-if="data.category">
            <span class="info-label">分类</span>
            <span class="info-value">{{ data.category }}</span>
          </div>
          <div class="info-row" v-if="data.organizer">
            <span class="info-label">主办方</span>
            <span class="info-value">{{ data.organizer }}</span>
          </div>
          <div class="info-row" v-if="data.eventAddress">
            <span class="info-label">地点</span>
            <span class="info-value">{{ data.eventAddress }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">报名时间</span>
            <span class="info-value">{{ formatTime(data.enrollStartTime) }} ~ {{ formatTime(data.enrollEndTime) }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">比赛时间</span>
            <span class="info-value">{{ formatTime(data.eventStartTime) }} ~ {{ formatTime(data.eventEndTime) }}</span>
          </div>
        </div>

        <!-- Price -->
        <div class="preview-section price-section">
          <span class="price-tag" v-if="Number(data.price) > 0">&#xffe5;{{ Number(data.price).toFixed(2) }}</span>
          <span class="price-tag free" v-else>免费</span>
          <span class="quota-tag" v-if="data.totalQuota > 0">限{{ data.totalQuota }}人</span>
        </div>

        <!-- Schedule -->
        <div class="preview-section" v-if="scheduleList.length">
          <div class="section-title">赛程安排</div>
          <div class="timeline">
            <div v-for="(item, idx) in scheduleList" :key="'s' + idx" class="timeline-item">
              <div class="timeline-dot"></div>
              <div class="timeline-content">
                <div class="timeline-name">{{ item.stageName }}</div>
                <div class="timeline-time">{{ formatTime(item.startTime) }}</div>
                <div class="timeline-venue" v-if="item.venue">{{ item.venue }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Awards -->
        <div class="preview-section" v-if="awardList.length">
          <div class="section-title">奖项设置</div>
          <div v-for="(item, idx) in awardList" :key="'a' + idx" class="award-item">
            <span class="award-level">{{ item.awardLevel || item.awardName }}</span>
            <span class="award-name">{{ item.awardName }}</span>
            <span class="award-quota" v-if="item.quota">{{ item.quota }}名</span>
          </div>
        </div>

        <!-- Description (truncated) -->
        <div class="preview-section" v-if="data.description">
          <div class="section-title">赛事详情</div>
          <div class="desc-content" v-html="truncateHtml(data.description, 200)"></div>
        </div>

        <!-- FAQ -->
        <div class="preview-section" v-if="faqList.length">
          <div class="section-title">常见问题</div>
          <div v-for="(item, idx) in faqList" :key="'f' + idx" class="faq-item">
            <div class="faq-q">Q: {{ item.question }}</div>
            <div class="faq-a">A: {{ item.answer }}</div>
          </div>
        </div>

        <!-- Bottom Bar -->
        <div class="preview-bottom-bar">
          <div class="btn-placeholder">立即报名</div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'

export default {
  name: 'ContestPreview',
  props: {
    visible: { type: Boolean, default: false },
    contestData: { type: Object, default: () => ({}) },
    scheduleList: { type: Array, default: () => [] },
    awardList: { type: Array, default: () => [] },
    faqList: { type: Array, default: () => [] }
  },
  computed: {
    dialogVisible: {
      get() { return this.visible },
      set(val) { this.$emit('update:visible', val) }
    },
    data() { return this.contestData || {} }
  },
  methods: {
    formatTime(val) {
      return val ? parseTime(val, '{y}-{m}-{d} {h}:{i}') : '--'
    },
    truncateHtml(html, maxLen) {
      if (!html) return ''
      const text = html.replace(/<[^>]+>/g, '')
      if (text.length <= maxLen) return html
      return text.substring(0, maxLen) + '...'
    }
  }
}
</script>

<style scoped>
.preview-dialog >>> .el-dialog__body {
  padding: 0 20px 20px;
}

.phone-frame {
  width: 375px;
  margin: 0 auto;
  border: 2px solid #1f2d3d;
  border-radius: 28px;
  background: #000;
  padding: 8px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.18);
}

.phone-screen {
  width: 100%;
  height: 667px;
  overflow-y: auto;
  border-radius: 20px;
  background: #fff;
}

.preview-banner {
  position: relative;
  width: 100%;
  height: 180px;
  background: linear-gradient(135deg, #0f766e 0%, #2563eb 100%);
  overflow: hidden;
}

.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255,255,255,0.6);
  font-size: 14px;
}

.banner-overlay {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 12px 16px;
  background: linear-gradient(transparent, rgba(0,0,0,0.5));
}

.preview-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.4;
}

.preview-section {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f2f5;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 13px;
  line-height: 1.5;
}

.info-label {
  flex-shrink: 0;
  color: #8c98a8;
  width: 56px;
}

.info-value {
  color: #303133;
}

.price-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.price-tag {
  font-size: 22px;
  font-weight: 700;
  color: #f56c6c;
}

.price-tag.free {
  color: #67c23a;
}

.quota-tag {
  font-size: 12px;
  color: #909399;
  padding: 2px 8px;
  background: #f4f4f5;
  border-radius: 10px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2d3d;
  margin-bottom: 10px;
}

.timeline {
  padding-left: 12px;
}

.timeline-item {
  display: flex;
  gap: 10px;
  padding-bottom: 12px;
  position: relative;
}

.timeline-item:not(:last-child)::before {
  content: '';
  position: absolute;
  left: 4px;
  top: 12px;
  bottom: 0;
  width: 1px;
  background: #dcdfe6;
}

.timeline-dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: #0f766e;
  margin-top: 4px;
  flex-shrink: 0;
}

.timeline-content {
  flex: 1;
}

.timeline-name {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
}

.timeline-time,
.timeline-venue {
  font-size: 12px;
  color: #909399;
}

.award-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  font-size: 13px;
}

.award-level {
  padding: 1px 8px;
  border-radius: 4px;
  background: #fdf6ec;
  color: #e6a23c;
  font-size: 12px;
}

.award-name {
  flex: 1;
  color: #303133;
}

.award-quota {
  color: #909399;
  font-size: 12px;
}

.desc-content {
  font-size: 13px;
  color: #606266;
  line-height: 1.7;
}

.faq-item {
  margin-bottom: 10px;
}

.faq-q {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
}

.faq-a {
  font-size: 12px;
  color: #606266;
  line-height: 1.6;
}

.preview-bottom-bar {
  position: sticky;
  bottom: 0;
  padding: 10px 16px;
  background: #fff;
  border-top: 1px solid #f0f2f5;
}

.btn-placeholder {
  text-align: center;
  padding: 10px;
  border-radius: 20px;
  background: linear-gradient(135deg, #0f766e 0%, #2563eb 100%);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
}

@media (max-width: 500px) {
  .phone-frame {
    width: 100%;
  }
}
</style>
