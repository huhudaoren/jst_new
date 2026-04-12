<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="商品名称" prop="goodsName">
        <el-input v-model="queryParams.goodsName" placeholder="请输入商品名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="商品类型" prop="goodsType">
        <el-select v-model="queryParams.goodsType" placeholder="全部" clearable>
          <el-option label="虚拟商品" value="virtual" />
          <el-option label="实物商品" value="physical" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="上架" value="on" />
          <el-option label="下架" value="off" />
          <el-option label="草稿" value="draft" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['jst:points:mall_goods:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.goodsId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.goodsName }}</span>
            <el-tag size="mini" :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>{{ row.goodsType === 'virtual' ? '虚拟' : '实物' }}</span>
            <span>{{ row.pointsPrice || 0 }} 积分</span>
            <span class="amount-cell">¥ {{ formatAmount(row.cashPrice) }}</span>
            <span>
              库存：
              <el-tag v-if="Number(row.stock || 0) < 10" size="mini" type="danger">{{ row.stock || 0 }}</el-tag>
              <span v-else>{{ row.stock || 0 }}</span>
            </span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:points:mall_goods:edit']">编辑</el-button>
            <el-button v-if="!isOnSale(row.status)" type="text" size="mini" @click="handleToggle(row, 'on')" v-hasPermi="['jst:points:mall_goods:edit']">上架</el-button>
            <el-button v-if="isOnSale(row.status)" type="text" size="mini" @click="handleToggle(row, 'off')" v-hasPermi="['jst:points:mall_goods:edit']">下架</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无商城商品" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="商品ID" prop="goodsId" width="90" />
      <el-table-column label="商品名称" prop="goodsName" min-width="160" show-overflow-tooltip />
      <el-table-column label="类型" width="90">
        <template slot-scope="{ row }">{{ row.goodsType === 'virtual' ? '虚拟' : '实物' }}</template>
      </el-table-column>
      <el-table-column label="积分价" prop="pointsPrice" min-width="90" align="right" />
      <el-table-column label="现金补差" min-width="100" align="right">
        <template slot-scope="{ row }">
          <span class="amount-cell">¥ {{ formatAmount(row.cashPrice) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="库存" min-width="90" align="center">
        <template slot-scope="{ row }">
          <el-tag v-if="Number(row.stock || 0) < 10" size="small" type="danger">{{ row.stock || 0 }}</el-tag>
          <span v-else>{{ row.stock || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" min-width="160">
        <template slot-scope="{ row }">{{ parseTime(row.updateTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="170">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:points:mall_goods:edit']">编辑</el-button>
          <el-button v-if="!isOnSale(row.status)" type="text" size="mini" @click="handleToggle(row, 'on')" v-hasPermi="['jst:points:mall_goods:edit']">上架</el-button>
          <el-button v-if="isOnSale(row.status)" type="text" size="mini" @click="handleToggle(row, 'off')" v-hasPermi="['jst:points:mall_goods:edit']">下架</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" :width="isMobile ? '100%' : '660px'" :fullscreen="isMobile" append-to-body>
      <el-form ref="form" :model="form" :rules="formRules" :label-width="isMobile ? '84px' : '100px'">
        <el-form-item label="商品名称" prop="goodsName">
          <el-input v-model="form.goodsName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="商品类型" prop="goodsType">
              <el-select v-model="form.goodsType" placeholder="请选择">
                <el-option label="虚拟商品" value="virtual" />
                <el-option label="实物商品" value="physical" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="角色限制" prop="roleLimit">
              <el-select v-model="form.roleLimit" placeholder="请选择">
                <el-option label="学生" value="student" />
                <el-option label="渠道" value="channel" />
                <el-option label="全部" value="both" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面图">
          <image-upload v-model="form.coverImage" :limit="1" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="积分价格" prop="pointsPrice">
              <el-input-number v-model="form.pointsPrice" :min="0" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="现金补差" prop="cashPrice">
              <el-input-number v-model="form.cashPrice" :min="0" :precision="2" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="预警阈值" prop="stockWarning">
              <el-input-number v-model="form.stockWarning" :min="0" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品状态">
          <el-select v-model="form.status" placeholder="请选择">
            <el-option label="上架" value="on" />
            <el-option label="下架" value="off" />
            <el-option label="草稿" value="draft" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'
import { listJst_mall_goods, getJst_mall_goods, addJst_mall_goods, updateJst_mall_goods } from '@/api/jst/points/jst_mall_goods'

export default {
  name: 'JstMallGoodsAdmin',
  data() {
    return {
      loading: false,
      submitLoading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        goodsName: null,
        goodsType: null,
        status: null
      },
      dialogVisible: false,
      dialogTitle: '',
      form: {},
      formRules: {
        goodsName: [{ required: true, message: '商品名称不能为空', trigger: 'blur' }],
        goodsType: [{ required: true, message: '请选择商品类型', trigger: 'change' }],
        roleLimit: [{ required: true, message: '请选择角色限制', trigger: 'change' }],
        pointsPrice: [{ required: true, message: '积分价格不能为空', trigger: 'blur' }],
        cashPrice: [{ required: true, message: '现金补差不能为空', trigger: 'blur' }],
        stock: [{ required: true, message: '库存不能为空', trigger: 'blur' }]
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
    isOnSale(status) {
      return status === 'on' || status === 'on_sale'
    },
    statusTagType(status) {
      if (status === 'on' || status === 'on_sale') {
        return 'success'
      }
      if (status === 'off' || status === 'off_sale') {
        return 'info'
      }
      return 'warning'
    },
    statusLabel(status) {
      if (status === 'on' || status === 'on_sale') {
        return '上架'
      }
      if (status === 'off' || status === 'off_sale') {
        return '下架'
      }
      return '草稿'
    },
    normalizeStatus(status) {
      if (status === 'on_sale') {
        return 'on'
      }
      if (status === 'off_sale') {
        return 'off'
      }
      return status
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
      listJst_mall_goods(this.queryParams).then(res => {
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
    initForm() {
      return {
        goodsId: null,
        goodsName: '',
        goodsType: 'virtual',
        coverImage: '',
        description: '',
        pointsPrice: 0,
        cashPrice: 0,
        stock: 0,
        stockWarning: 10,
        roleLimit: 'both',
        status: 'draft',
        remark: ''
      }
    },
    handleAdd() {
      this.form = this.initForm()
      this.dialogTitle = '新增商品'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleEdit(row) {
      getJst_mall_goods(row.goodsId).then(res => {
        const data = res.data || res || row
        this.form = { ...this.initForm(), ...data, status: this.normalizeStatus(data.status) }
        this.dialogTitle = '编辑商品'
        this.dialogVisible = true
      })
    },
    handleToggle(row, targetStatus) {
      const text = targetStatus === 'on' ? '上架' : '下架'
      updateJst_mall_goods({ ...row, status: targetStatus }).then(() => {
        this.$modal.msgSuccess(text + '成功')
        this.getList()
      })
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        this.submitLoading = true
        const payload = { ...this.form, status: this.normalizeStatus(this.form.status) }
        const api = payload.goodsId ? updateJst_mall_goods : addJst_mall_goods
        api(payload).then(() => {
          this.$modal.msgSuccess(payload.goodsId ? '修改成功' : '新增成功')
          this.dialogVisible = false
          this.getList()
        }).finally(() => {
          this.submitLoading = false
        })
      })
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
