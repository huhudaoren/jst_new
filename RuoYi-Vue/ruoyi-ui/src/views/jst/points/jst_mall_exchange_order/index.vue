<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="订单号" prop="exchangeNo">
        <el-input v-model="queryParams.exchangeNo" placeholder="请输入兑换订单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.exchangeId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.goodsName || '--' }}</span>
            <el-tag size="mini" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>{{ row.exchangeNo }}</span>
            <span>用户：{{ row.userId || '--' }}</span>
            <span>{{ row.pointsUsed || 0 }} 积分</span>
            <span class="amount-cell">¥ {{ formatAmount(row.cashAmount) }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 'pending_ship'" type="text" size="mini" @click="openShipDialog(row)" v-hasPermi="['jst:points:mall:exchange:ship']">发货</el-button>
            <el-button v-if="row.status === 'shipped'" type="text" size="mini" @click="handleComplete(row)" v-hasPermi="['jst:points:mall:exchange:ship']">完成</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无兑换订单" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="订单ID" prop="exchangeId" width="90" />
      <el-table-column label="订单号" prop="exchangeNo" min-width="150" show-overflow-tooltip />
      <el-table-column label="用户ID" prop="userId" min-width="100" />
      <el-table-column label="商品ID" prop="goodsId" min-width="90" />
      <el-table-column label="数量" prop="quantity" min-width="70" align="right" />
      <el-table-column label="积分消耗" prop="pointsUsed" min-width="100" align="right" />
      <el-table-column label="现金补差" min-width="100" align="right">
        <template slot-scope="{ row }">
          <span class="amount-cell">¥ {{ formatAmount(row.cashAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发货时间" min-width="160">
        <template slot-scope="{ row }">{{ parseTime(row.shipTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="创建时间" min-width="160">
        <template slot-scope="{ row }">{{ parseTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="170">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
          <el-button v-if="row.status === 'pending_ship'" type="text" size="mini" @click="openShipDialog(row)" v-hasPermi="['jst:points:mall:exchange:ship']">发货</el-button>
          <el-button v-if="row.status === 'shipped'" type="text" size="mini" @click="handleComplete(row)" v-hasPermi="['jst:points:mall:exchange:ship']">完成</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="订单发货" :visible.sync="shipVisible" :width="isMobile ? '100%' : '460px'" :fullscreen="isMobile" append-to-body>
      <el-form ref="shipForm" :model="shipForm" :rules="shipRules" :label-width="isMobile ? '84px' : '96px'">
        <el-form-item label="物流公司" prop="logisticsCompany">
          <el-input v-model="shipForm.logisticsCompany" placeholder="请输入物流公司" />
        </el-form-item>
        <el-form-item label="物流单号" prop="logisticsNo">
          <el-input v-model="shipForm.logisticsNo" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="shipVisible = false">取 消</el-button>
        <el-button type="primary" :loading="shipLoading" @click="submitShip">确认发货</el-button>
      </div>
    </el-dialog>

    <el-drawer :title="'兑换订单详情 #' + (detailData.exchangeId || '')" :visible.sync="detailVisible" :size="isMobile ? '100%' : '520px'" append-to-body>
      <div class="detail-body">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="订单号">{{ detailData.exchangeNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ detailData.userId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="商品ID">{{ detailData.goodsId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="数量">{{ detailData.quantity || 0 }}</el-descriptions-item>
          <el-descriptions-item label="积分消耗">{{ detailData.pointsUsed || 0 }}</el-descriptions-item>
          <el-descriptions-item label="现金补差">¥ {{ formatAmount(detailData.cashAmount) }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag size="small" :type="statusType(detailData.status)">{{ statusLabel(detailData.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="物流公司">{{ detailData.logisticsCompany || '--' }}</el-descriptions-item>
          <el-descriptions-item label="物流单号">{{ detailData.logisticsNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ parseTime(detailData.shipTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ parseTime(detailData.completeTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="地址快照">{{ detailData.addressSnapshotJson || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detailData.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'
import { listExchangeOrder, getExchangeOrder, shipExchangeOrder, completeExchangeOrder } from '@/api/jst/mall'

const STATUS_META = {
  pending_pay: { label: '待付款', type: 'warning' },
  paid: { label: '已支付', type: '' },
  pending_ship: { label: '待发货', type: 'warning' },
  shipped: { label: '已发货', type: '' },
  completed: { label: '已完成', type: 'success' },
  aftersale: { label: '售后中', type: 'danger' },
  closed: { label: '已关闭', type: 'info' }
}

export default {
  name: 'JstMallExchangeOrderAdmin',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        exchangeNo: null,
        userId: null,
        status: null
      },
      statusOptions: Object.keys(STATUS_META).map(key => ({ value: key, label: STATUS_META[key].label })),
      detailVisible: false,
      detailData: {},
      shipVisible: false,
      shipLoading: false,
      shipTargetId: null,
      shipForm: {
        logisticsCompany: '',
        logisticsNo: ''
      },
      shipRules: {
        logisticsCompany: [{ required: true, message: '物流公司不能为空', trigger: 'blur' }],
        logisticsNo: [{ required: true, message: '物流单号不能为空', trigger: 'blur' }]
      }
    }
  },
  computed: {
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    parseTime,
    statusType(status) {
      return (STATUS_META[status] || {}).type || 'info'
    },
    statusLabel(status) {
      return (STATUS_META[status] || {}).label || status || '--'
    },
    formatAmount(value) {
      if (value === null || value === undefined || value === '') {
        return '0.00'
      }
      const num = Number(value)
      if (Number.isNaN(num)) {
        return value
      }
      const displayNum = Number.isInteger(num) ? num / 100 : num
      return displayNum.toFixed(2)
    },
    getList() {
      this.loading = true
      listExchangeOrder(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    handleDetail(row) {
      getExchangeOrder(row.exchangeId).then(res => {
        this.detailData = res.data || res || row
        this.detailVisible = true
      })
    },
    openShipDialog(row) {
      this.shipTargetId = row.exchangeId
      this.shipForm = { logisticsCompany: '', logisticsNo: '' }
      this.shipVisible = true
      this.$nextTick(() => {
        this.$refs.shipForm && this.$refs.shipForm.clearValidate()
      })
    },
    submitShip() {
      this.$refs.shipForm.validate(valid => {
        if (!valid) {
          return
        }
        this.shipLoading = true
        shipExchangeOrder(this.shipTargetId, this.shipForm).then(() => {
          this.$modal.msgSuccess('发货成功')
          this.shipVisible = false
          this.getList()
        }).finally(() => {
          this.shipLoading = false
        })
      })
    },
    handleComplete(row) {
      this.$modal.confirm('确认将该订单标记为已完成吗？').then(() => {
        return completeExchangeOrder(row.exchangeId)
      }).then(() => {
        this.$modal.msgSuccess('操作成功')
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.amount-cell {
  text-align: right;
  display: inline-block;
  min-width: 68px;
}

.detail-body {
  padding: 0 16px 16px;
}

.mobile-card-list {
  padding: 0 4px;
}

.mobile-card {
  background: #fff;
  border-radius: 8px;
  padding: 12px 14px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.mobile-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.mobile-card__title {
  font-weight: 600;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 65%;
}

.mobile-card__meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.mobile-card__actions {
  display: flex;
  gap: 6px;
}
</style>
