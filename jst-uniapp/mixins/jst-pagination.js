/**
 * ═══════════════════════════════════════════════════════════════
 * jst-pagination mixin — 全站统一分页 / 下拉刷新 / 上拉加载机制
 * ═══════════════════════════════════════════════════════════════
 *
 * 为什么需要: 审计显示全站分页复用率 0%、每页手写 pageNum/hasMore/loadingMore，
 * 文案/状态/边界条件各不相同。此 mixin 一次性统一，新页面必须用它。
 *
 * ▼ 子组件必须实现
 *   async fetchData({ pageNum, pageSize }) => ({ rows, total })
 *   (返回值为后端 ApiResult 的 body；rows 为本页数据，total 为总条数)
 *
 * ▼ 使用示例
 *   <script>
 *   import jstPagination from '@/mixins/jst-pagination'
 *   import { getMyOrders } from '@/api/order'
 *
 *   export default {
 *     mixins: [jstPagination],
 *     data() {
 *       return { status: '' } // 子组件自有筛选条件
 *     },
 *     onShow() { this.fetchList(true) },
 *     methods: {
 *       async fetchData({ pageNum, pageSize }) {
 *         return getMyOrders({ pageNum, pageSize, status: this.status }, { silent: true })
 *       },
 *       onFilterChange(v) { this.status = v; this.fetchList(true) }
 *     }
 *   }
 *   </script>
 *
 *   <template>
 *     <view>
 *       <jst-skeleton-plus v-if="pageLoading && !list.length" mode="card" :count="3" />
 *       <block v-else-if="list.length">
 *         <view v-for="item in list" :key="item.id">...</view>
 *         <u-loadmore :status="loadMoreStatus" />
 *       </block>
 *       <jst-empty v-else v-bind="EMPTY_ORDERS" />
 *     </view>
 *   </template>
 *
 * ▼ 约束
 *   - fetchData 必须是 async 且返回 { rows, total }，抛错将被 mixin catch
 *   - 子组件不要自己维护 pageNum / hasMore / loadingMore
 *   - 换筛选条件后调 `this.fetchList(true)` 重置
 *   - 子组件可覆盖 onReachBottom / onPullDownRefresh 实现自定义行为
 *
 * ▼ 不做的事
 *   - 不内置搜索防抖 (业务场景差异大)
 *   - 不处理首屏骨架屏显示 (由页面自己决定)
 *   - 不接管 API 调用 (调用方式由子组件决定)
 */

const DEFAULT_PAGE_SIZE = 10

export default {
  data() {
    return {
      // ─── 列表数据 ───
      list: [],
      // ─── 分页状态 ───
      pageNum: 1,
      pageSize: DEFAULT_PAGE_SIZE,
      total: 0,
      hasMore: true,
      // ─── 加载状态 ───
      pageLoading: false,      // 首次加载 / 重置加载
      loadingMore: false,      // 触底加载中
      // u-loadmore 状态: 'loadmore' | 'loading' | 'nomore'
      loadMoreStatus: 'loadmore'
    }
  },

  methods: {
    /**
     * 拉取列表
     * @param {boolean} refresh true=重置(page=1 + 清空); false=追加下一页
     */
    async fetchList(refresh = false) {
      if (typeof this.fetchData !== 'function') {
        // eslint-disable-next-line no-console
        console.error('[jst-pagination] 子组件必须实现 async fetchData({ pageNum, pageSize })')
        return
      }
      // 触底加载防重入
      if (!refresh && (this.loadingMore || this.pageLoading || !this.hasMore)) {
        return
      }

      if (refresh) {
        this.pageNum = 1
        this.hasMore = true
        this.pageLoading = true
        this.loadMoreStatus = 'loading'
      } else {
        this.loadingMore = true
        this.loadMoreStatus = 'loading'
      }

      try {
        const resp = await this.fetchData({
          pageNum: this.pageNum,
          pageSize: this.pageSize
        })
        const rows = Array.isArray(resp && resp.rows)
          ? resp.rows
          : (Array.isArray(resp) ? resp : [])
        const total = Number(resp && resp.total) || rows.length

        this.list = refresh ? rows : this.list.concat(rows)
        this.total = total

        // 边界: 本页返回空 或 累计已覆盖 total 则无更多
        const reached = this.list.length >= total || rows.length < this.pageSize
        this.hasMore = !reached && rows.length > 0

        if (rows.length > 0) {
          this.pageNum += 1
        }
        this.loadMoreStatus = this.hasMore ? 'loadmore' : 'nomore'
      } catch (e) {
        // request.js 已弹 toast, 此处仅记录状态
        if (refresh) {
          this.list = []
          this.total = 0
        }
        this.hasMore = false
        this.loadMoreStatus = 'nomore'
      } finally {
        this.pageLoading = false
        this.loadingMore = false
        uni.stopPullDownRefresh()
      }
    }
  },

  /**
   * 默认生命周期钩子 — 子组件可覆盖
   * 注意: uniapp 要求 onReachBottom / onPullDownRefresh 必须在页面组件上声明,
   * 在 mixin 中声明对于作为页面加载的组件是生效的。
   */
  onPullDownRefresh() {
    this.fetchList(true)
  },
  onReachBottom() {
    this.fetchList(false)
  }
}
