<template>
  <div class="app-container jst-home">
    <el-row :gutter="20">
      <el-col :xs="24" :lg="13">
        <el-card class="hero-card" shadow="never">
          <p class="hero-tag">JingSaiTong Admin</p>
          <h2>竞赛通管理后台</h2>
          <p class="hero-desc">统一处理内容配置、营销权益、履约与运营复盘，让管理动作更高效。</p>
          <div class="hero-meta">
            <span>当前版本：v{{ version }}</span>
            <el-tag size="mini" type="success">稳定运行</el-tag>
          </div>
          <div class="hero-actions">
            <el-button
              v-for="item in resolvedHeroActions"
              :key="item.key"
              size="mini"
              :type="item.type || 'default'"
              :plain="item.plain"
              :icon="item.icon"
              class="hero-entry-btn"
              :class="{ 'is-disabled': !item.enabled }"
              @click="handleEntryClick(item)"
            >
              {{ item.title }}
            </el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="11">
        <el-card class="quick-card" shadow="never">
          <div slot="header" class="clearfix">
            <span>快捷入口</span>
          </div>
          <div class="quick-grid">
            <button
              v-for="item in resolvedQuickEntries"
              :key="item.key"
              class="quick-item"
              :class="{ 'is-disabled': !item.enabled }"
              type="button"
              @click="handleEntryClick(item)"
            >
              <i :class="item.icon"></i>
              <span>{{ item.title }}</span>
            </button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="flow-panel" shadow="never">
      <div slot="header" class="flow-header">
        <span>管理端流程导航</span>
        <el-tag size="mini" type="info">仅后台流程</el-tag>
      </div>
      <div class="flow-grid">
        <article
          v-for="item in resolvedFlowEntries"
          :key="item.key"
          class="flow-item"
          :class="{ 'is-disabled': !item.enabled }"
        >
          <div class="flow-index">{{ item.index }}</div>
          <h4>{{ item.title }}</h4>
          <p>{{ item.desc }}</p>
          <button class="flow-link" type="button" @click="handleEntryClick(item)">{{ item.actionText }}</button>
        </article>
      </div>
    </el-card>
  </div>
</template>

<script>
import { collectAvailablePaths, resolveFirstAvailablePath } from '@/utils/route-access'

export default {
  name: 'Index',
  data() {
    return {
      version: require('../../package.json').version,
      heroActions: [
        {
          key: 'dashboard',
          title: '进入运营看板',
          icon: 'el-icon-data-analysis',
          type: 'primary',
          plain: false,
          candidates: ['/jst/dashboard']
        },
        {
          key: 'notice',
          title: '发布平台公告',
          icon: 'el-icon-bell',
          type: 'default',
          plain: true,
          candidates: ['/jst/notice']
        }
      ],
      quickEntries: [
        {
          key: 'course',
          title: '课程管理',
          icon: 'el-icon-reading',
          candidates: ['/jst/course']
        },
        {
          key: 'formTemplate',
          title: '表单模板',
          icon: 'el-icon-edit-outline',
          candidates: ['/jst/form-template']
        },
        {
          key: 'couponTemplate',
          title: '优惠券模板',
          icon: 'el-icon-tickets',
          candidates: ['/jst/coupon/template']
        },
        {
          key: 'mallGoods',
          title: '商城商品',
          icon: 'el-icon-shopping-bag-2',
          candidates: ['/jst/mall']
        }
      ],
      flowEntries: [
        {
          key: 'courseFlow',
          index: '01',
          title: '内容配置',
          desc: '维护课程信息与报名表单模板。',
          actionText: '进入课程管理',
          candidates: ['/jst/course']
        },
        {
          key: 'rightsFlow',
          index: '02',
          title: '营销权益',
          desc: '配置优惠券与用户权益发放规则。',
          actionText: '进入优惠管理',
          candidates: ['/jst/coupon/template', '/jst/rights/template']
        },
        {
          key: 'exchangeFlow',
          index: '03',
          title: '履约处理',
          desc: '管理商城商品、库存与兑换订单。',
          actionText: '进入履约中心',
          candidates: ['/jst/mall/exchange']
        },
        {
          key: 'noticeFlow',
          index: '04',
          title: '公告发布',
          desc: '维护平台通知并管理上线状态。',
          actionText: '进入公告中心',
          candidates: ['/jst/notice']
        },
        {
          key: 'dashboardFlow',
          index: '05',
          title: '数据复盘',
          desc: '通过看板追踪趋势并优化运营策略。',
          actionText: '进入运营看板',
          candidates: ['/jst/dashboard']
        }
      ]
    }
  },
  computed: {
    availablePathSet() {
      const routeSet = collectAvailablePaths(this.$store.getters.permission_routes || [])
      const sidebarSet = collectAvailablePaths(this.$store.getters.sidebarRouters || [])
      sidebarSet.forEach(path => routeSet.add(path))
      return routeSet
    },
    resolvedHeroActions() {
      return this.decorateEntries(this.heroActions)
    },
    resolvedQuickEntries() {
      return this.decorateEntries(this.quickEntries)
    },
    resolvedFlowEntries() {
      return this.decorateEntries(this.flowEntries)
    }
  },
  methods: {
    decorateEntries(entries) {
      return entries.map(entry => {
        const targetPath = resolveFirstAvailablePath(entry.candidates, this.availablePathSet)
        return {
          ...entry,
          targetPath,
          enabled: Boolean(targetPath)
        }
      })
    },
    notifyNoAccess() {
      const text = '当前账号未配置该菜单或无权限'
      if (this.$modal && this.$modal.msgWarning) {
        this.$modal.msgWarning(text)
        return
      }
      if (this.$message && this.$message.warning) {
        this.$message.warning(text)
      }
    },
    handleEntryClick(entry) {
      if (!entry || !entry.enabled || !entry.targetPath) {
        this.notifyNoAccess()
        return
      }
      this.$router.push(entry.targetPath)
    }
  }
}
</script>

<style scoped lang="scss">
.jst-home {
  .hero-card,
  .quick-card,
  .flow-panel {
    border-radius: 12px;
    border: 1px solid #e6edf8;
  }

  .hero-card {
    min-height: 260px;
    background: linear-gradient(135deg, #f0f7ff 0%, #f7fffb 100%);
  }

  .hero-tag {
    display: inline-block;
    margin: 0 0 8px;
    padding: 3px 10px;
    border-radius: 999px;
    font-size: 12px;
    color: #0b5ed7;
    background: #dcecff;
  }

  h2 {
    margin: 0 0 12px;
    color: #1f2d3d;
    font-size: 28px;
    font-weight: 700;
  }

  .hero-desc {
    margin: 0;
    color: #4f5d75;
    line-height: 1.7;
  }

  .hero-meta {
    margin-top: 16px;
    display: flex;
    align-items: center;
    gap: 10px;
    color: #5b6b86;
  }

  .hero-actions {
    margin-top: 18px;
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
  }

  .hero-entry-btn.is-disabled {
    opacity: .45;
    cursor: not-allowed;
    pointer-events: auto;
  }

  .quick-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 10px;
  }

  .quick-item {
    display: flex;
    align-items: center;
    gap: 8px;
    width: 100%;
    min-height: 44px;
    border: 1px solid #d9e6fb;
    border-radius: 10px;
    background: #f8fbff;
    color: #355178;
    padding: 0 12px;
    font-size: 14px;
    cursor: pointer;
    transition: all .2s ease;

    i {
      color: #3a7afe;
      font-size: 16px;
    }
  }

  .quick-item:hover {
    border-color: #93b7ff;
    background: #edf4ff;
    transform: translateY(-1px);
  }

  .quick-item.is-disabled {
    border-color: #e5e8ef;
    background: #f7f8fb;
    color: #9ca8ba;
    cursor: not-allowed;

    i {
      color: #b7c1d1;
    }
  }

  .quick-item.is-disabled:hover {
    transform: none;
    box-shadow: none;
    border-color: #e5e8ef;
    background: #f7f8fb;
  }

  .flow-panel {
    margin-top: 16px;
  }

  .flow-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .flow-grid {
    display: grid;
    grid-template-columns: repeat(5, minmax(0, 1fr));
    gap: 12px;
  }

  .flow-item {
    padding: 14px 12px;
    border-radius: 10px;
    border: 1px solid #e3ecfb;
    background: linear-gradient(180deg, #ffffff 0%, #f6faff 100%);

    h4 {
      margin: 6px 0;
      color: #1f2d3d;
      font-size: 15px;
    }

    p {
      margin: 0 0 8px;
      color: #5b6b86;
      line-height: 1.6;
      min-height: 44px;
      font-size: 13px;
    }
  }

  .flow-item.is-disabled {
    border-color: #e9edf5;
    background: #f8f9fc;

    h4, p {
      color: #a0abbb;
    }
  }

  .flow-index {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 40px;
    padding: 1px 8px;
    border-radius: 999px;
    background: #eaf2ff;
    color: #2e62cf;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: .04em;
  }

  .flow-link {
    border: none;
    padding: 0;
    background: transparent;
    color: #2e62cf;
    font-size: 13px;
    cursor: pointer;
  }

  .flow-item.is-disabled .flow-link {
    color: #b1b9c8;
    cursor: not-allowed;
  }

  .flow-item ::v-deep .el-button,
  .flow-link {
    padding: 0;
    font-size: 13px;
  }
}

@media (max-width: 1200px) {
  .jst-home {
    .flow-grid {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }
  }
}

@media (max-width: 768px) {
  .jst-home {
    h2 {
      font-size: 22px;
    }

    .hero-meta {
      flex-wrap: wrap;
    }

    .hero-actions .el-button,
    .quick-item {
      min-height: 40px;
    }

    .quick-grid {
      grid-template-columns: 1fr;
    }

    .flow-panel {
      margin-top: 12px;
    }

    .flow-header {
      gap: 8px;
      flex-wrap: wrap;
    }

    .flow-grid {
      grid-template-columns: 1fr;
    }

    .flow-item p {
      min-height: 0;
    }
  }
}
</style>
