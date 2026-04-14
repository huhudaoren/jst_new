<!-- 中文注释: 证书详情页；对应接口 GET /jst/wx/cert/${id}；包含全图预览+保存相册+Canvas海报+分享 -->
<template>
  <view class="cert-detail-page">
    <!-- 头部导航 -->
    <view class="cert-detail-page__header">
      <view class="cert-detail-page__back" @tap="goBack">&lt;</view>
      <text class="cert-detail-page__header-title">证书详情</text>
      <view class="cert-detail-page__header-placeholder"></view>
    </view>

    <jst-loading :loading="loading" text="证书加载中..." />

    <template v-if="detail && !loading">
      <!-- 证书全图预览 -->
      <view class="cert-detail-page__preview">
        <image
          v-if="detail.certImageUrl"
          class="cert-detail-page__preview-img"
          :src="detail.certImageUrl"
          mode="widthFix"
          @tap="previewImage"
        />
        <!-- Canvas 渲染证书（layoutJson 可用时） -->
        <view v-else-if="hasLayout" class="cert-detail-page__preview-canvas">
          <canvas
            type="2d"
            id="certCanvas"
            class="cert-detail-page__cert-canvas"
            :style="{ width: certCanvasW + 'px', height: certCanvasH + 'px' }"
            @tap="previewCertCanvas"
          />
          <view v-if="rendering" class="cert-detail-page__rendering">
            <text class="cert-detail-page__rendering-text">证书生成中...</text>
          </view>
        </view>
        <!-- 无真实图片和 layoutJson 时显示模拟证书 -->
        <view v-else class="cert-detail-page__preview-mock" :class="'cert-detail-page__preview-mock--' + awardTheme">
          <view class="cert-detail-page__preview-border"></view>
          <view class="cert-detail-page__preview-inner">
            <text class="cert-detail-page__preview-title">获 奖 证 书</text>
            <text class="cert-detail-page__preview-holder">{{ detail.holderName || '--' }}</text>
            <text class="cert-detail-page__preview-text">在{{ detail.contestName || '--' }}中</text>
            <text class="cert-detail-page__preview-text">荣获 {{ detail.awardLevel || '--' }}</text>
            <text class="cert-detail-page__preview-org">{{ detail.issueOrg || '竞赛通平台' }}</text>
            <text class="cert-detail-page__preview-date">{{ formatDate(detail.issueDate) }}</text>
          </view>
          <text class="cert-detail-page__preview-badge">{{ awardIcon }}</text>
        </view>
      </view>

      <!-- 证书信息 -->
      <view class="cert-detail-page__card">
        <text class="cert-detail-page__card-title">证书信息</text>
        <view class="cert-detail-page__info-row">
          <text class="cert-detail-page__info-key">证书编号</text>
          <text class="cert-detail-page__info-val">{{ detail.certNo || '--' }}</text>
        </view>
        <view class="cert-detail-page__info-row">
          <text class="cert-detail-page__info-key">获奖人</text>
          <text class="cert-detail-page__info-val">{{ detail.holderName || '--' }}</text>
        </view>
        <view class="cert-detail-page__info-row">
          <text class="cert-detail-page__info-key">赛事名称</text>
          <text class="cert-detail-page__info-val">{{ detail.contestName || '--' }}</text>
        </view>
        <view class="cert-detail-page__info-row">
          <text class="cert-detail-page__info-key">获奖等级</text>
          <text class="cert-detail-page__info-val">{{ awardIcon }} {{ detail.awardLevel || '--' }}</text>
        </view>
        <view class="cert-detail-page__info-row">
          <text class="cert-detail-page__info-key">参赛组别</text>
          <text class="cert-detail-page__info-val">{{ detail.groupLevel || detail.category || '--' }}</text>
        </view>
        <view class="cert-detail-page__info-row">
          <text class="cert-detail-page__info-key">发证机构</text>
          <text class="cert-detail-page__info-val">{{ detail.issueOrg || '--' }}</text>
        </view>
        <view class="cert-detail-page__info-row">
          <text class="cert-detail-page__info-key">发证日期</text>
          <text class="cert-detail-page__info-val">{{ formatDate(detail.issueDate) }}</text>
        </view>
      </view>

      <!-- 操作按钮区 -->
      <view class="cert-detail-page__actions">
        <view class="cert-detail-page__action cert-detail-page__action--primary" @tap="saveCertToAlbum">
          <text class="cert-detail-page__action-icon">&#11015;</text>
          <text class="cert-detail-page__action-text">保存到相册</text>
        </view>
        <view class="cert-detail-page__action cert-detail-page__action--brand" @tap="generatePoster">
          <text class="cert-detail-page__action-icon">&#128196;</text>
          <text class="cert-detail-page__action-text">生成分享海报</text>
        </view>
        <view class="cert-detail-page__action" @tap="verifyCert">
          <text class="cert-detail-page__action-icon">&#128269;</text>
          <text class="cert-detail-page__action-text">验证证书</text>
        </view>
        <button class="cert-detail-page__action cert-detail-page__action--share" open-type="share">
          <text class="cert-detail-page__action-icon">&#128279;</text>
          <text class="cert-detail-page__action-text">分享给好友</text>
        </button>
      </view>

      <!-- Canvas 海报（隐藏，用于绘制） -->
      <view class="cert-detail-page__canvas-wrap">
        <canvas canvas-id="certPoster" class="cert-detail-page__canvas" style="width: 750rpx; height: 1200rpx;"></canvas>
      </view>
    </template>

    <jst-empty v-else-if="!loading" text="暂无证书信息" icon="🏅" />
  </view>
</template>

<script>
import { getCertDetail } from '@/api/cert'
import { renderCert, CERT_WIDTH, CERT_HEIGHT } from '@/utils/cert-renderer'
import JstEmpty from '@/components/jst-empty/jst-empty.vue'
import JstLoading from '@/components/jst-loading/jst-loading.vue'

export default {
  components: { JstEmpty, JstLoading },
  data() {
    return {
      loading: false,
      certId: '',
      detail: null,
      posterGenerating: false,
      hasLayout: false,
      rendering: false,
      certCanvasW: 0,
      certCanvasH: 0,
      canvasNode: null
    }
  },
  computed: {
    awardTheme() {
      var level = this.detail && this.detail.awardLevel
      if (!level) return 'blue'
      if (level.indexOf('一等') !== -1) return 'gold'
      if (level.indexOf('二等') !== -1) return 'silver'
      return 'blue'
    },
    awardIcon() {
      var level = this.detail && this.detail.awardLevel
      if (!level) return '🏅'
      if (level.indexOf('一等') !== -1) return '🥇'
      if (level.indexOf('二等') !== -1) return '🥈'
      if (level.indexOf('三等') !== -1) return '🥉'
      return '🏅'
    }
  },
  onLoad(query) {
    this.certId = query.id || query.certId || ''
    this.loadDetail()
  },
  // 微信分享
  onShareAppMessage() {
    var d = this.detail || {}
    return {
      title: (d.holderName || '') + '的获奖证书 - ' + (d.contestName || '竞赛通'),
      path: '/pages-sub/public/cert-verify?certNo=' + encodeURIComponent(d.certNo || '')
    }
  },
  methods: {
    async loadDetail() {
      if (!this.certId) return
      this.loading = true
      try {
        var res = await getCertDetail(this.certId)
        this.detail = res || null

        // 检测 layoutJson 是否可用，触发 Canvas 渲染
        if (this.detail && this.detail.layoutJson && !this.detail.certImageUrl) {
          this.hasLayout = true
          this.initCanvasSize()
          var that = this
          this.$nextTick(function() {
            that.renderCertCanvas()
          })
        }
      } catch (e) {
        this.detail = null
      } finally {
        this.loading = false
      }
    },
    // 计算画布显示尺寸（保持 A4 比例）
    initCanvasSize() {
      var sysInfo = uni.getSystemInfoSync()
      // 减去两侧页面边距 (24rpx * 2 = 48rpx ≈ screenWidth * 48/750)
      var displayW = sysInfo.windowWidth - Math.round(sysInfo.windowWidth * 48 / 750)
      var displayH = Math.round(displayW * (CERT_HEIGHT / CERT_WIDTH))
      this.certCanvasW = displayW
      this.certCanvasH = displayH
    },
    // 用 cert-renderer 将 layoutJson 渲染到 Canvas 2D
    renderCertCanvas() {
      var that = this
      that.rendering = true
      var query = uni.createSelectorQuery().in(that)
      query.select('#certCanvas').fields({ node: true, size: true }).exec(function(res) {
        if (!res || !res[0] || !res[0].node) {
          console.warn('[cert-detail] Canvas 节点获取失败')
          that.rendering = false
          return
        }
        var canvas = res[0].node
        that.canvasNode = canvas
        var ctx = canvas.getContext('2d')
        var dpr = uni.getSystemInfoSync().pixelRatio

        // 设置画布内部分辨率（Fabric.js 原始尺寸 * DPR）
        canvas.width = CERT_WIDTH * dpr
        canvas.height = CERT_HEIGHT * dpr
        ctx.scale(dpr, dpr)

        var layoutData = {}
        try {
          layoutData = JSON.parse(that.detail.layoutJson)
        } catch (e) {
          console.warn('[cert-detail] layoutJson 解析失败:', e)
          that.rendering = false
          return
        }

        var variables = that.detail.variables || {}

        renderCert(ctx, layoutData, variables, {
          width: CERT_WIDTH,
          height: CERT_HEIGHT,
          canvas: canvas
        }).then(function() {
          that.rendering = false
        }).catch(function(e) {
          console.warn('[cert-detail] 证书渲染失败:', e)
          that.rendering = false
        })
      })
    },
    formatDate(val) {
      if (!val) return '--'
      var d = new Date(val)
      if (isNaN(d.getTime())) return val
      return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0')
    },
    // 预览大图
    previewImage() {
      if (this.detail && this.detail.certImageUrl) {
        uni.previewImage({ urls: [this.detail.certImageUrl] })
      }
    },
    // 预览 Canvas 渲染的证书大图
    previewCertCanvas() {
      var that = this
      if (!that.canvasNode) return
      uni.canvasToTempFilePath({
        canvas: that.canvasNode,
        width: that.canvasNode.width,
        height: that.canvasNode.height,
        destWidth: that.canvasNode.width,
        destHeight: that.canvasNode.height,
        success: function(res) {
          uni.previewImage({ urls: [res.tempFilePath] })
        },
        fail: function() {
          uni.showToast({ title: '预览失败', icon: 'none' })
        }
      })
    },
    // 保存证书图片到相册
    saveCertToAlbum() {
      var that = this
      if (!this.detail) return
      // 优先保存真实证书图
      var imageUrl = this.detail.certImageUrl
      if (imageUrl) {
        that.downloadAndSave(imageUrl)
      } else if (that.canvasNode && that.hasLayout) {
        // Canvas 渲染的证书 → 导出并保存
        that.saveCertCanvasToAlbum()
      } else {
        // 无真实图片时生成海报再保存
        that.generateAndSavePoster()
      }
    },
    // 将 Canvas 2D 渲染的证书导出并保存到相册
    saveCertCanvasToAlbum() {
      var that = this
      uni.showLoading({ title: '保存中...' })
      uni.canvasToTempFilePath({
        canvas: that.canvasNode,
        width: that.canvasNode.width,
        height: that.canvasNode.height,
        destWidth: that.canvasNode.width,
        destHeight: that.canvasNode.height,
        success: function(res) {
          uni.saveImageToPhotosAlbum({
            filePath: res.tempFilePath,
            success: function() {
              uni.hideLoading()
              uni.showToast({ title: '已保存到相册', icon: 'success' })
            },
            fail: function(err) {
              uni.hideLoading()
              if (err.errMsg && err.errMsg.indexOf('auth deny') !== -1) {
                uni.showModal({
                  title: '需要相册权限',
                  content: '请在设置中允许访问相册',
                  confirmText: '去设置',
                  success: function(modalRes) {
                    if (modalRes.confirm) uni.openSetting()
                  }
                })
              } else {
                uni.showToast({ title: '保存失败', icon: 'none' })
              }
            }
          })
        },
        fail: function() {
          uni.hideLoading()
          uni.showToast({ title: '导出失败', icon: 'none' })
        }
      })
    },
    // 下载网络图片并保存到相册
    downloadAndSave(url) {
      uni.showLoading({ title: '保存中...' })
      uni.downloadFile({
        url: url,
        success: function(res) {
          if (res.statusCode === 200) {
            uni.saveImageToPhotosAlbum({
              filePath: res.tempFilePath,
              success: function() {
                uni.hideLoading()
                uni.showToast({ title: '已保存到相册', icon: 'success' })
              },
              fail: function(err) {
                uni.hideLoading()
                // 权限被拒绝
                if (err.errMsg && err.errMsg.indexOf('auth deny') !== -1) {
                  uni.showModal({
                    title: '需要相册权限',
                    content: '请在设置中允许访问相册',
                    confirmText: '去设置',
                    success: function(modalRes) {
                      if (modalRes.confirm) {
                        uni.openSetting()
                      }
                    }
                  })
                } else {
                  uni.showToast({ title: '保存失败', icon: 'none' })
                }
              }
            })
          } else {
            uni.hideLoading()
            uni.showToast({ title: '下载失败', icon: 'none' })
          }
        },
        fail: function() {
          uni.hideLoading()
          uni.showToast({ title: '下载失败', icon: 'none' })
        }
      })
    },
    // 生成海报并保存
    generateAndSavePoster() {
      var that = this
      that.drawPoster(function(tempFilePath) {
        uni.saveImageToPhotosAlbum({
          filePath: tempFilePath,
          success: function() {
            uni.hideLoading()
            uni.showToast({ title: '已保存到相册', icon: 'success' })
          },
          fail: function(err) {
            uni.hideLoading()
            if (err.errMsg && err.errMsg.indexOf('auth deny') !== -1) {
              uni.showModal({
                title: '需要相册权限',
                content: '请在设置中允许访问相册',
                confirmText: '去设置',
                success: function(modalRes) {
                  if (modalRes.confirm) uni.openSetting()
                }
              })
            } else {
              uni.showToast({ title: '保存失败', icon: 'none' })
            }
          }
        })
      })
    },
    // 生成分享海报
    generatePoster() {
      var that = this
      if (that.posterGenerating) return
      that.posterGenerating = true
      uni.showLoading({ title: '海报生成中...' })
      that.drawPoster(function(tempFilePath) {
        uni.hideLoading()
        that.posterGenerating = false
        // 预览海报
        uni.previewImage({ urls: [tempFilePath] })
      })
    },
    // Canvas 绘制海报
    drawPoster(callback) {
      var that = this
      var d = that.detail || {}
      var ctx = uni.createCanvasContext('certPoster', that)
      var w = 375  // 逻辑宽度（750rpx / 2）
      var h = 600  // 逻辑高度

      // 背景
      ctx.setFillStyle('#FFFFFF')
      ctx.fillRect(0, 0, w, h)

      // 顶部品牌渐变条
      var grd = ctx.createLinearGradient(0, 0, w, 80)
      grd.addColorStop(0, '#1A3A6E')
      grd.addColorStop(1, '#2B6CFF')
      ctx.setFillStyle(grd)
      ctx.fillRect(0, 0, w, 80)

      // 品牌文字
      ctx.setFontSize(18)
      ctx.setFillStyle('#FFFFFF')
      ctx.setTextAlign('center')
      ctx.fillText('竞赛通', w / 2, 35)
      ctx.setFontSize(10)
      ctx.setFillStyle('rgba(255,255,255,0.7)')
      ctx.fillText('一站式竞赛服务平台', w / 2, 55)

      // 证书区域背景
      var certY = 100
      var certH = 200
      var certPad = 30
      // 根据奖项选择背景色
      var certBg = '#F8FAFD'
      if (that.awardTheme === 'gold') certBg = '#FFFDF5'
      else if (that.awardTheme === 'silver') certBg = '#F8FAFB'
      ctx.setFillStyle(certBg)
      that.roundRect(ctx, certPad, certY, w - certPad * 2, certH, 12)
      ctx.fill()

      // 证书边框
      ctx.setStrokeStyle('rgba(0,0,0,0.08)')
      ctx.setLineWidth(1)
      that.roundRect(ctx, certPad + 6, certY + 6, w - certPad * 2 - 12, certH - 12, 8)
      ctx.stroke()

      // 证书标题
      ctx.setFontSize(14)
      ctx.setFillStyle('#1A1A1A')
      ctx.setTextAlign('center')
      ctx.fillText('获 奖 证 书', w / 2, certY + 40)

      // 获奖人
      ctx.setFontSize(22)
      ctx.setFillStyle('#1A1A1A')
      ctx.fillText(d.holderName || '--', w / 2, certY + 80)

      // 赛事 + 奖项
      ctx.setFontSize(11)
      ctx.setFillStyle('#8A8A8A')
      ctx.fillText('在 ' + (d.contestName || '--') + ' 中', w / 2, certY + 110)
      ctx.fillText('荣获 ' + (d.awardLevel || '--'), w / 2, certY + 130)

      // 日期
      ctx.setFontSize(9)
      ctx.setFillStyle('#C0C4CC')
      ctx.fillText(that.formatDate(d.issueDate), w / 2, certY + 160)

      // 恭喜语
      var infoY = certY + certH + 30
      ctx.setFontSize(14)
      ctx.setFillStyle('#1A1A1A')
      ctx.setTextAlign('center')
      ctx.fillText('恭喜 ' + (d.holderName || '--') + ' 同学', w / 2, infoY)

      ctx.setFontSize(12)
      ctx.setFillStyle('#4A4A4A')
      ctx.fillText('荣获 ' + (d.contestName || '--'), w / 2, infoY + 25)
      ctx.fillText((d.awardLevel || ''), w / 2, infoY + 45)

      // 证书编号
      ctx.setFontSize(10)
      ctx.setFillStyle('#8A8A8A')
      ctx.fillText('证书编号：' + (d.certNo || '--'), w / 2, infoY + 75)
      ctx.fillText('颁发日期：' + that.formatDate(d.issueDate), w / 2, infoY + 95)

      // 扫码提示
      var qrY = infoY + 120
      ctx.setFontSize(10)
      ctx.setFillStyle('#8A8A8A')
      ctx.fillText('扫描小程序码查看完整证书', w / 2, qrY + 10)

      // 底部品牌
      ctx.setFontSize(10)
      ctx.setFillStyle('#C0C4CC')
      ctx.fillText('— 竞赛通 · 一站式竞赛平台 —', w / 2, h - 20)

      // 绘制完成后导出
      ctx.draw(false, function() {
        setTimeout(function() {
          uni.canvasToTempFilePath({
            canvasId: 'certPoster',
            width: w,
            height: h,
            destWidth: w * 2,
            destHeight: h * 2,
            success: function(res) {
              callback(res.tempFilePath)
            },
            fail: function() {
              uni.hideLoading()
              that.posterGenerating = false
              uni.showToast({ title: '海报生成失败', icon: 'none' })
            }
          }, that)
        }, 300)
      })
    },
    // 绘制圆角矩形路径
    roundRect(ctx, x, y, w, h, r) {
      ctx.beginPath()
      ctx.moveTo(x + r, y)
      ctx.lineTo(x + w - r, y)
      ctx.arcTo(x + w, y, x + w, y + r, r)
      ctx.lineTo(x + w, y + h - r)
      ctx.arcTo(x + w, y + h, x + w - r, y + h, r)
      ctx.lineTo(x + r, y + h)
      ctx.arcTo(x, y + h, x, y + h - r, r)
      ctx.lineTo(x, y + r)
      ctx.arcTo(x, y, x + r, y, r)
      ctx.closePath()
    },
    // 验证证书
    verifyCert() {
      if (this.detail && this.detail.certNo) {
        uni.navigateTo({
          url: '/pages-sub/public/cert-verify?certNo=' + encodeURIComponent(this.detail.certNo)
        })
      }
    },
    goBack() {
      if (getCurrentPages().length > 1) {
        uni.navigateBack()
        return
      }
      uni.navigateTo({ url: '/pages-sub/my/cert' })
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.cert-detail-page {
  min-height: 100vh;
  padding-bottom: calc(60rpx + env(safe-area-inset-bottom));
  background: $jst-bg-subtle;
}

// 头部导航
.cert-detail-page__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $jst-space-lg $jst-page-padding;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-sm;
}

.cert-detail-page__back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  border-radius: 22rpx;
  background: $jst-bg-page;
  font-size: $jst-font-md;
  color: $jst-text-primary;
}

.cert-detail-page__header-title {
  font-size: 34rpx;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.cert-detail-page__header-placeholder {
  width: 72rpx;
}

// 证书预览区
.cert-detail-page__preview {
  margin: $jst-space-lg $jst-page-padding 0;
  border-radius: $jst-radius-card;
  overflow: hidden;
  box-shadow: $jst-shadow-ring, $jst-shadow-ambient, $jst-shadow-lift;
}

// Canvas 渲染区
.cert-detail-page__preview-canvas {
  position: relative;
  border-radius: $jst-radius-card;
  overflow: hidden;
  background: $jst-bg-card;
}

.cert-detail-page__cert-canvas {
  display: block;
  width: 100%;
}

.cert-detail-page__rendering {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.85);
}

.cert-detail-page__rendering-text {
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.cert-detail-page__preview-img {
  width: 100%;
  display: block;
}

.cert-detail-page__preview-mock {
  position: relative;
  min-height: 480rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $jst-space-xl;
}

.cert-detail-page__preview-mock--gold {
  background: linear-gradient(135deg, #FFFDF5 0%, #FFF7E0 100%);
}

.cert-detail-page__preview-mock--silver {
  background: linear-gradient(135deg, #F8FAFB 0%, #E8EEF2 100%);
}

.cert-detail-page__preview-mock--blue {
  background: linear-gradient(135deg, #F0F4FA 0%, #E0EAFF 100%);
}

.cert-detail-page__preview-border {
  position: absolute;
  top: 16rpx;
  left: 16rpx;
  right: 16rpx;
  bottom: 16rpx;
  border: 2rpx solid rgba(0, 0, 0, 0.06);
  border-radius: $jst-radius-md;
  pointer-events: none;
}

.cert-detail-page__preview-mock--gold .cert-detail-page__preview-border {
  border-color: rgba(255, 213, 79, 0.4);
}

.cert-detail-page__preview-inner {
  text-align: center;
  z-index: 1;
}

.cert-detail-page__preview-title {
  display: block;
  font-size: $jst-font-lg;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
  letter-spacing: 8rpx;
  margin-bottom: $jst-space-lg;
}

.cert-detail-page__preview-holder {
  display: block;
  font-size: 44rpx;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
  letter-spacing: 4rpx;
  margin-bottom: $jst-space-md;
}

.cert-detail-page__preview-text {
  display: block;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
  line-height: 2;
}

.cert-detail-page__preview-org {
  display: block;
  margin-top: $jst-space-lg;
  font-size: $jst-font-xs;
  color: $jst-text-placeholder;
}

.cert-detail-page__preview-date {
  display: block;
  margin-top: $jst-space-xs;
  font-size: $jst-font-xs;
  color: $jst-text-placeholder;
}

.cert-detail-page__preview-badge {
  position: absolute;
  top: $jst-space-xl;
  right: $jst-space-xl;
  font-size: 64rpx;
  opacity: 0.5;
}

// 证书信息卡
.cert-detail-page__card {
  margin: $jst-space-lg $jst-page-padding 0;
  padding: $jst-space-lg;
  border-radius: $jst-radius-card;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-ring, $jst-shadow-ambient;
}

.cert-detail-page__card-title {
  display: flex;
  align-items: center;
  gap: $jst-space-sm;
  margin-bottom: $jst-space-lg;
  font-size: $jst-font-md;
  font-weight: $jst-weight-bold;
  color: $jst-text-primary;
}

.cert-detail-page__card-title::before {
  content: '';
  display: inline-block;
  width: 6rpx;
  height: 28rpx;
  border-radius: $jst-radius-xs;
  background: $jst-brand;
}

.cert-detail-page__info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $jst-space-md 0;
  border-bottom: 1rpx solid $jst-border;
}

.cert-detail-page__info-row:last-child {
  border-bottom: none;
}

.cert-detail-page__info-key {
  flex-shrink: 0;
  font-size: $jst-font-sm;
  color: $jst-text-secondary;
}

.cert-detail-page__info-val {
  font-size: $jst-font-sm;
  font-weight: $jst-weight-medium;
  color: $jst-text-primary;
  text-align: right;
}

// 操作按钮区
.cert-detail-page__actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $jst-space-md;
  margin: $jst-space-xl $jst-page-padding 0;
}

.cert-detail-page__action {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $jst-space-sm;
  height: 96rpx;
  border-radius: $jst-radius-card;
  background: $jst-bg-card;
  box-shadow: $jst-shadow-ring, $jst-shadow-ambient;
  font-size: $jst-font-sm;
  color: $jst-text-regular;
  transition: transform $jst-duration-fast $jst-easing;
  // 重置 button 样式
  padding: 0;
  margin: 0;
  border: none;
  line-height: normal;
}

.cert-detail-page__action::after {
  display: none;
}

.cert-detail-page__action:active {
  transform: scale(0.97);
}

.cert-detail-page__action--primary {
  background: $jst-brand;
  color: $jst-text-inverse;
}

.cert-detail-page__action--brand {
  background: $jst-hero-gradient;
  color: $jst-text-inverse;
}

.cert-detail-page__action--share {
  background: $jst-success;
  color: $jst-text-inverse;
}

.cert-detail-page__action-icon {
  font-size: $jst-font-md;
}

.cert-detail-page__action-text {
  font-size: $jst-font-sm;
  font-weight: $jst-weight-medium;
}

// Canvas 容器（隐藏在屏幕外）
.cert-detail-page__canvas-wrap {
  position: fixed;
  left: -9999rpx;
  top: -9999rpx;
}

.cert-detail-page__canvas {
  width: 750rpx;
  height: 1200rpx;
}
</style>
