<template>
  <div class="app-container order-item-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">璁㈠崟涓績</p>
        <h2>璁㈠崟鏄庣粏琛岀鐞?/h2>
        <p class="hero-desc">鏀寔鎸夎鍗曞彿涓庡晢鍝佸悕绉版绱紝鏌ョ湅鍒嗘憡閲戦涓庨€€娆剧疮璁★紝鏀寔鍏宠仈璺宠浆璁㈠崟璇︽儏銆?/p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">鍒锋柊</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="88px" class="query-panel">
      <el-form-item label="璁㈠崟鍙?ID">
        <el-input
          v-model="queryParams.orderKeyword"
          placeholder="璇疯緭鍏ヨ鍗曞彿鎴栬鍗旾D"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="鍟嗗搧鍚嶇О">
        <el-input
          v-model="queryParams.itemName"
          placeholder="璇疯緭鍏ュ晢鍝?椤圭洰鍚嶇О"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="鏄庣粏绫诲瀷">
        <el-select v-model="queryParams.skuType" placeholder="鍏ㄩ儴" clearable>
          <el-option v-for="item in skuTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">鎼滅储</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">閲嶇疆</el-button>
      </el-form-item>
    </el-form>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.itemId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.itemName || '--' }}</div>
              <div class="mobile-sub">
                <el-link type="primary" :underline="false" @click="goOrder(row)">
                  璁㈠崟 {{ row.orderNo || row.orderId || '--' }}
                </el-link>
              </div>
            </div>
            <el-tag size="small" type="info">{{ skuTypeLabel(row.skuType) }}</el-tag>
          </div>
          <div class="mobile-amount">{{ formatMoney(row.netPayShare) }}</div>
          <div class="mobile-info-row">
            <span>鏍囦环 {{ formatMoney(row.listAmount) }}</span>
            <span class="amount-negative">宸查€€ {{ formatMoney(row.refundAmount) }}</span>
          </div>
          <div class="mobile-info-row">
            <span>鏁伴噺 {{ row.quantity || 0 }}</span>
            <span>{{ parseTime(row.createTime) || '--' }}</span>
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">鏌ョ湅璇︽儏</el-button>
            <el-button type="text" @click="goOrder(row)">鍏宠仈璁㈠崟</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="鏆傛棤璁㈠崟鏄庣粏" :image-size="96" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="鏄庣粏ID" prop="itemId" min-width="90" />
      <el-table-column label="璁㈠崟鍙?ID" min-width="170" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="goOrder(scope.row)">
            {{ scope.row.orderNo || scope.row.orderId || '--' }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="鍟嗗搧/椤圭洰鍚嶇О" prop="itemName" min-width="190" show-overflow-tooltip />
      <el-table-column label="绫诲瀷" min-width="130">
        <template slot-scope="scope">
          <el-tag size="small" type="info">{{ skuTypeLabel(scope.row.skuType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="鏁伴噺" prop="quantity" min-width="80" align="center" />
      <el-table-column label="鏍囦环閲戦" min-width="120" align="right">
        <template slot-scope="scope">{{ formatMoney(scope.row.listAmount) }}</template>
      </el-table-column>
      <el-table-column label="鍑€瀹炰粯" min-width="120" align="right">
        <template slot-scope="scope">
          <span class="amount-strong">{{ formatMoney(scope.row.netPayShare) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="宸查€€鐜伴噾" min-width="120" align="right">
        <template slot-scope="scope">
          <span class="amount-negative">{{ formatMoney(scope.row.refundAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="鍒涘缓鏃堕棿" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.createTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="鎿嶄綔" width="140" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">璇︽儏</el-button>
          <el-button type="text" @click="goOrder(scope.row)">璁㈠崟</el-button>
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

    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '680px'" title="璁㈠崟鏄庣粏璇︽儏" append-to-body>
      <div v-loading="detailLoading" class="drawer-body">
        <el-descriptions v-if="detail" :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="鏄庣粏ID">{{ detail.itemId }}</el-descriptions-item>
          <el-descriptions-item label="璁㈠崟鍙?ID">{{ detail.orderNo || detail.orderId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="绫诲瀷">{{ skuTypeLabel(detail.skuType) }}</el-descriptions-item>
          <el-descriptions-item label="鍟嗗搧/椤圭洰">{{ detail.itemName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="鏁伴噺">{{ detail.quantity || 0 }}</el-descriptions-item>
          <el-descriptions-item label="寮曠敤涓氬姟ID">{{ detail.refId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="鏍囦环閲戦">{{ formatMoney(detail.listAmount) }}</el-descriptions-item>
          <el-descriptions-item label="浼樻儬鍒稿垎鎽?>{{ formatMoney(detail.couponShare) }}</el-descriptions-item>
          <el-descriptions-item label="绉垎鍒嗘憡">{{ formatMoney(detail.pointsShare) }}</el-descriptions-item>
          <el-descriptions-item label="鍑€瀹炰粯鍒嗘憡">
            <span class="amount-strong">{{ formatMoney(detail.netPayShare) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="鏈嶅姟璐瑰垎鎽?>{{ formatMoney(detail.serviceFeeShare) }}</el-descriptions-item>
          <el-descriptions-item label="娓犻亾杩旂偣鍒嗘憡">{{ formatMoney(detail.rebateShare) }}</el-descriptions-item>
          <el-descriptions-item label="宸查€€鐜伴噾绱">
            <span class="amount-negative">{{ formatMoney(detail.refundAmount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="宸插洖閫€绉垎">{{ detail.refundPoints || 0 }}</el-descriptions-item>
          <el-descriptions-item label="鍒涘缓鏃堕棿">{{ parseTime(detail.createTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="鏇存柊鏃堕棿">{{ parseTime(detail.updateTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="澶囨敞" :span="isMobile ? 1 : 2">{{ detail.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
        <el-empty v-else description="鏆傛棤璇︽儏鏁版嵁" :image-size="96" />
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_order_item, getJst_order_item } from '@/api/jst/order/jst_order_item'

const SKU_TYPE_MAP = {
  enroll: '鎶ュ悕',
  appointment_member: '棰勭害鎴愬憳',
  goods: '鍟嗗煄鍟嗗搧',
  course: '璇剧▼'
}

export default {
  name: 'JstOrderItemManage',
  data() {
    return {
      loading: false,
      detailLoading: false,
      isMobile: false,
      list: [],
      total: 0,
      detailVisible: false,
      detail: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderKeyword: '',
        itemName: '',
        skuType: undefined
      },
      skuTypeOptions: Object.keys(SKU_TYPE_MAP).map(key => ({ value: key, label: SKU_TYPE_MAP[key] }))
    }
  },
  created() {
    this.updateViewport()
    window.addEventListener('resize', this.updateViewport)
    this.getList()
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.updateViewport)
  },
  methods: {
    updateViewport() {
      this.isMobile = window.innerWidth <= 768
    },
    isNumericKeyword(value) {
      return /^\d+$/.test(String(value || '').trim())
    },
    async getList() {
      this.loading = true
      try {
        const keyword = String(this.queryParams.orderKeyword || '').trim()
        const params = {
          pageNum: this.queryParams.pageNum,
          pageSize: this.queryParams.pageSize,
          itemName: this.queryParams.itemName || undefined,
          skuType: this.queryParams.skuType || undefined
        }
        if (this.isNumericKeyword(keyword)) {
          params.orderId = Number(keyword)
        }
        const res = await listJst_order_item(params)
        const rows = res.rows || []
        if (keyword && !this.isNumericKeyword(keyword)) {
          const localKeyword = keyword.toLowerCase()
          this.list = rows.filter(row => {
            const orderText = String(row.orderNo || row.orderId || '').toLowerCase()
            return orderText.indexOf(localKeyword) > -1
          })
          this.total = this.list.length
        } else {
          this.list = rows
          this.total = res.total || 0
        }
      } finally {
        this.loading = false
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        orderKeyword: '',
        itemName: '',
        skuType: undefined
      }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detailLoading = true
      try {
        const res = await getJst_order_item(row.itemId)
        this.detail = res.data || null
      } finally {
        this.detailLoading = false
      }
    },
    goOrder(row) {
      const orderId = row && row.orderId ? row.orderId : null
      if (!orderId) return
      this.$router.push({
        path: '/jst/order/admin-order',
        query: { orderId: String(orderId), autoOpen: '1' }
      })
    },
    skuTypeLabel(value) {
      return SKU_TYPE_MAP[value] || value || '--'
    },
    formatMoney(value) {
      const n = Number(value || 0)
      return '\u00A5' + (n / 100).toFixed(2)
    }
  }
}
</script>

<style scoped>
.order-item-page {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.page-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
  margin-bottom: 18px;
  background: #ffffff;
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

.query-panel {
  padding: 16px 16px 0;
  margin-bottom: 16px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.amount-strong {
  font-weight: 700;
}

.amount-negative {
  color: #f56c6c;
  font-weight: 600;
}

.drawer-body {
  padding: 0 24px 24px;
}

.mobile-list {
  min-height: 180px;
}

.mobile-card {
  padding: 16px;
  margin-bottom: 12px;
  background: #ffffff;
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

.mobile-amount {
  margin-top: 12px;
  font-size: 22px;
  font-weight: 700;
  color: #172033;
}

.mobile-info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
  font-size: 13px;
  color: #7a8495;
}

.mobile-actions {
  display: flex;
  gap: 14px;
  margin-top: 12px;
}

@media (max-width: 768px) {
  .order-item-page {
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

  .query-panel {
    padding-bottom: 8px;
  }

  .query-panel ::v-deep .el-form-item {
    display: block;
    margin-right: 0;
  }

  .query-panel ::v-deep .el-form-item__content,
  .query-panel ::v-deep .el-select,
  .query-panel ::v-deep .el-input {
    width: 100%;
  }

  .mobile-actions .el-button {
    min-height: 44px;
  }
}
</style>


