<template>
  <div class="app-container jst-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">Exchange Orders</p>
        <h2>积分兑换订单</h2>
        <p class="hero-desc">处理发货与完结动作，详情抽屉保留并统一视觉风格。</p>
      </div>
      <div class="hero-actions">
        <el-popover placement="bottom" width="280" trigger="hover">
          <div class="help-lines">
            <p>1. 待发货订单可填写物流信息发货。</p>
            <p>2. 已发货订单可确认完成。</p>
            <p>3. 详情抽屉查看订单全字段。</p>
          </div>
          <el-button slot="reference" icon="el-icon-question" plain>使用说明</el-button>
        </el-popover>
        <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
      </div>
    </div>

    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="订单号" prop="exchangeNo">
        <el-input v-model="queryParams.exchangeNo" placeholder="请输入兑换单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8 toolbar-row">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.exchangeId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.goodsName || '--' }}</span>
            <el-tag size="mini" :type="exchangeStatusType(row.status)">{{ exchangeStatusLabel(row.status) }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>{{ row.exchangeNo }}</span>
            <span>{{ row.nickname || row.userId }}</span>
            <span>{{ row.pointsUsed }}积分</span>
            <span>{{ parseTime(row.createTime) }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleDetail(row)">查看</el-button>
            <el-button v-if="row.status === 'pending_ship'" type="text" size="mini" @click="handleShip(row)" v-hasPermi="['jst:points:mall:exchange:ship']">发货</el-button>
            <el-button v-if="row.status === 'shipped'" type="text" size="mini" @click="handleComplete(row)" v-hasPermi="['jst:points:mall:exchange:ship']">确认完成</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无兑换记录" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="exchangeId" width="70" />
      <el-table-column label="兑换单号" prop="exchangeNo" width="160" show-overflow-tooltip />
      <el-table-column label="用户" min-width="100">
        <template slot-scope="{ row }">{{ row.nickname || row.userId || '--' }}</template>
      </el-table-column>
      <el-table-column label="商品" prop="goodsName" min-width="150" show-overflow-tooltip />
      <el-table-column label="积分" prop="pointsUsed" width="80" />
      <el-table-column label="现金" width="80">
        <template slot-scope="{ row }">￥{{ row.cashAmount || 0 }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="exchangeStatusType(row.status)">{{ exchangeStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="160">
        <template slot-scope="{ row }">{{ parseTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleDetail(row)">查看</el-button>
          <el-button v-if="row.status === 'pending_ship'" type="text" size="mini" @click="handleShip(row)" v-hasPermi="['jst:points:mall:exchange:ship']">发货</el-button>
          <el-button v-if="row.status === 'shipped'" type="text" size="mini" @click="handleComplete(row)" v-hasPermi="['jst:points:mall:exchange:ship']">确认完成</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="发货" :visible.sync="shipVisible" width="420px" append-to-body>
      <el-form ref="shipForm" :model="shipForm" :rules="shipRules" label-width="80px">
        <el-form-item label="物流公司" prop="logisticsCompany">
          <el-input v-model="shipForm.logisticsCompany" placeholder="请输入物流公司" />
        </el-form-item>
        <el-form-item label="物流单号" prop="logisticsNo">
          <el-input v-model="shipForm.logisticsNo" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipLoading" @click="doShip">确认发货</el-button>
      </div>
    </el-dialog>

    <el-drawer :title="'兑换详情 #' + (detailData.exchangeId || '')" :visible.sync="detailVisible" :size="isMobile ? '100%' : '420px'" append-to-body>
      <div class="detail-wrap">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="兑换单号">{{ detailData.exchangeNo }}</el-descriptions-item>
          <el-descriptions-item label="用户">{{ detailData.nickname || detailData.userId }}</el-descriptions-item>
          <el-descriptions-item label="商品">{{ detailData.goodsName }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ detailData.goodsType === 'virtual' ? '虚拟' : '实物' }}</el-descriptions-item>
          <el-descriptions-item label="数量">{{ detailData.quantity }}</el-descriptions-item>
          <el-descriptions-item label="消耗积分">{{ detailData.pointsUsed }}</el-descriptions-item>
          <el-descriptions-item label="现金补差">￥{{ detailData.cashAmount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag size="small" :type="exchangeStatusType(detailData.status)">{{ exchangeStatusLabel(detailData.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="物流公司">{{ detailData.logisticsCompany || '--' }}</el-descriptions-item>
          <el-descriptions-item label="物流单号">{{ detailData.logisticsNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ parseTime(detailData.shipTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ parseTime(detailData.completeTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ parseTime(detailData.createTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listExchangeOrder, getExchangeOrder, shipExchangeOrder, completeExchangeOrder } from '@/api/jst/mall'
import { parseTime } from '@/utils/ruoyi'

const STATUS_META = {
  pending_pay: { label: '待付款', type: 'warning' },
  paid: { label: '已付款', type: '' },
  pending_ship: { label: '待发货', type: 'warning' },
  shipped: { label: '已发货', type: '' },
  completed: { label: '已完成', type: 'success' },
  aftersale: { label: '售后中', type: 'danger' },
  closed: { label: '已关闭', type: 'info' }
}

export default {
  name: 'JstMallExchange',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, exchangeNo: null, status: null },
      statusOptions: Object.entries(STATUS_META).map(([value, m]) => ({ value, label: m.label })),
      shipVisible: false,
      shipLoading: false,
      shipForm: { logisticsCompany: '', logisticsNo: '' },
      shipTargetId: null,
      shipRules: {
        logisticsCompany: [{ required: true, message: '物流公司不能为空', trigger: 'blur' }],
        logisticsNo: [{ required: true, message: '物流单号不能为空', trigger: 'blur' }]
      },
      detailVisible: false,
      detailData: {}
    }
  },
  computed: {
    isMobile() { return this.$store.state.app.device === 'mobile' }
  },
  created() { this.getList() },
  methods: {
    parseTime,
    exchangeStatusType(s) { return (STATUS_META[s] || {}).type || 'info' },
    exchangeStatusLabel(s) { return (STATUS_META[s] || {}).label || s || '--' },
    getList() {
      this.loading = true
      listExchangeOrder(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.$refs.queryForm && this.$refs.queryForm.resetFields(); this.handleQuery() },
    handleDetail(row) {
      getExchangeOrder(row.exchangeId).then(res => {
        this.detailData = res.data || res
        this.detailVisible = true
      })
    },
    handleShip(row) {
      this.shipTargetId = row.exchangeId
      this.shipForm = { logisticsCompany: '', logisticsNo: '' }
      this.shipVisible = true
      this.$nextTick(() => { this.$refs.shipForm && this.$refs.shipForm.clearValidate() })
    },
    doShip() {
      this.$refs.shipForm.validate(valid => {
        if (!valid) return
        this.shipLoading = true
        shipExchangeOrder(this.shipTargetId, this.shipForm).then(() => {
          this.$modal.msgSuccess('发货成功')
          this.shipVisible = false
          this.getList()
        }).finally(() => { this.shipLoading = false })
      })
    },
    handleComplete(row) {
      this.$modal.confirm('确认完成兑换订单 #' + row.exchangeId + '？').then(() => {
        return completeExchangeOrder(row.exchangeId)
      }).then(() => {
        this.$modal.msgSuccess('已完成')
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.jst-page {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.page-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 22px 24px;
  margin-bottom: 16px;
  color: #fff;
  border-radius: 12px;
  background: linear-gradient(130deg, #0f766e 0%, #2563eb 58%, #1e293b 100%);
}

.hero-eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: .08em;
  text-transform: uppercase;
  opacity: .82;
}

.page-hero h2 {
  margin: 0;
  font-size: 24px;
}

.hero-desc {
  margin: 8px 0 0;
  color: rgba(255, 255, 255, 0.82);
}

.hero-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.help-lines p {
  margin: 0 0 6px;
  line-height: 1.6;
}

.help-lines p:last-child {
  margin-bottom: 0;
}

.query-panel {
  padding: 16px 16px 0;
  margin-bottom: 16px;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 10px;
}

.toolbar-row {
  margin-bottom: 12px;
}

.mobile-card-list {
  padding: 0 4px;
}

.mobile-card {
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 10px;
  padding: 14px;
  margin-bottom: 10px;
}

.mobile-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.mobile-card__title {
  font-weight: 600;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 68%;
}

.mobile-card__meta {
  font-size: 12px;
  color: #6b7280;
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.mobile-card__actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.detail-wrap {
  padding: 0 20px 20px;
}

@media (max-width: 768px) {
  .jst-page {
    padding: 12px;
  }

  .page-hero {
    flex-direction: column;
    align-items: flex-start;
    padding: 18px;
  }

  .page-hero h2 {
    font-size: 20px;
  }

  .hero-actions {
    width: 100%;
  }

  .hero-actions .el-button {
    flex: 1;
    min-height: 44px;
  }

  .query-panel {
    padding-bottom: 10px;
  }

  .query-panel ::v-deep .el-form-item {
    display: block;
    margin-right: 0;
  }

  .query-panel ::v-deep .el-form-item__content,
  .query-panel ::v-deep .el-input,
  .query-panel ::v-deep .el-select,
  .query-panel ::v-deep .el-date-editor {
    width: 100%;
  }

  .query-panel .el-button,
  .mobile-card__actions .el-button {
    min-height: 44px;
  }

  .detail-wrap {
    padding: 0 14px 14px;
  }
}
</style>
