<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="单号" prop="exchangeNo">
        <el-input
          v-model="queryParams.exchangeNo"
          placeholder="请输入单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品ID" prop="goodsId">
        <el-input
          v-model="queryParams.goodsId"
          placeholder="请输入商品ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="数量" prop="quantity">
        <el-input
          v-model="queryParams.quantity"
          placeholder="请输入数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="消耗积分" prop="pointsUsed">
        <el-input
          v-model="queryParams.pointsUsed"
          placeholder="请输入消耗积分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="现金补差金额" prop="cashAmount">
        <el-input
          v-model="queryParams.cashAmount"
          placeholder="请输入现金补差金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联现金支付订单ID" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入关联现金支付订单ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="物流公司" prop="logisticsCompany">
        <el-input
          v-model="queryParams.logisticsCompany"
          placeholder="请输入物流公司"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="物流单号" prop="logisticsNo">
        <el-input
          v-model="queryParams.logisticsNo"
          placeholder="请输入物流单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发货时间" prop="shipTime">
        <el-date-picker clearable
          v-model="queryParams.shipTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择发货时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="完成时间" prop="completeTime">
        <el-date-picker clearable
          v-model="queryParams.completeTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择完成时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:jst_mall_exchange_order:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:jst_mall_exchange_order:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:jst_mall_exchange_order:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_mall_exchange_order:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_mall_exchange_orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="兑换订单ID" align="center" prop="exchangeId" />
      <el-table-column label="单号" align="center" prop="exchangeNo" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="商品ID" align="center" prop="goodsId" />
      <el-table-column label="数量" align="center" prop="quantity" />
      <el-table-column label="消耗积分" align="center" prop="pointsUsed" />
      <el-table-column label="现金补差金额" align="center" prop="cashAmount" />
      <el-table-column label="关联现金支付订单ID" align="center" prop="orderId" />
      <el-table-column label="收货地址快照JSON" align="center" prop="addressSnapshotJson" />
      <el-table-column label="订单状态：pending_pay/paid/pending_ship/shipped/completed/aftersale/closed" align="center" prop="status" />
      <el-table-column label="物流公司" align="center" prop="logisticsCompany" />
      <el-table-column label="物流单号" align="center" prop="logisticsNo" />
      <el-table-column label="发货时间" align="center" prop="shipTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.shipTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="完成时间" align="center" prop="completeTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.completeTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="售后状态：none/applying/refunding/refunded" align="center" prop="aftersaleStatus" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_mall_exchange_order:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_mall_exchange_order:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改积分商城兑换订单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="单号" prop="exchangeNo">
              <el-input v-model="form.exchangeNo" placeholder="请输入单号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品ID" prop="goodsId">
              <el-input v-model="form.goodsId" placeholder="请输入商品ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="数量" prop="quantity">
              <el-input v-model="form.quantity" placeholder="请输入数量" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="消耗积分" prop="pointsUsed">
              <el-input v-model="form.pointsUsed" placeholder="请输入消耗积分" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="现金补差金额" prop="cashAmount">
              <el-input v-model="form.cashAmount" placeholder="请输入现金补差金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联现金支付订单ID" prop="orderId">
              <el-input v-model="form.orderId" placeholder="请输入关联现金支付订单ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="物流公司" prop="logisticsCompany">
              <el-input v-model="form.logisticsCompany" placeholder="请输入物流公司" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="物流单号" prop="logisticsNo">
              <el-input v-model="form.logisticsNo" placeholder="请输入物流单号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="发货时间" prop="shipTime">
              <el-date-picker clearable
                v-model="form.shipTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择发货时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="完成时间" prop="completeTime">
              <el-date-picker clearable
                v-model="form.completeTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择完成时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="逻辑删除：0存在 2删除" prop="delFlag">
              <el-input v-model="form.delFlag" placeholder="请输入逻辑删除：0存在 2删除" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listJst_mall_exchange_order, getJst_mall_exchange_order, delJst_mall_exchange_order, addJst_mall_exchange_order, updateJst_mall_exchange_order } from "@/api/jst/points/jst_mall_exchange_order"

export default {
  name: "Jst_mall_exchange_order",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 积分商城兑换订单表格数据
      jst_mall_exchange_orderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        exchangeNo: null,
        userId: null,
        goodsId: null,
        quantity: null,
        pointsUsed: null,
        cashAmount: null,
        orderId: null,
        addressSnapshotJson: null,
        status: null,
        logisticsCompany: null,
        logisticsNo: null,
        shipTime: null,
        completeTime: null,
        aftersaleStatus: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        exchangeNo: [
          { required: true, message: "单号不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        goodsId: [
          { required: true, message: "商品ID不能为空", trigger: "blur" }
        ],
        quantity: [
          { required: true, message: "数量不能为空", trigger: "blur" }
        ],
        pointsUsed: [
          { required: true, message: "消耗积分不能为空", trigger: "blur" }
        ],
        cashAmount: [
          { required: true, message: "现金补差金额不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "订单状态：pending_pay/paid/pending_ship/shipped/completed/aftersale/closed不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询积分商城兑换订单列表 */
    getList() {
      this.loading = true
      listJst_mall_exchange_order(this.queryParams).then(response => {
        this.jst_mall_exchange_orderList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        exchangeId: null,
        exchangeNo: null,
        userId: null,
        goodsId: null,
        quantity: null,
        pointsUsed: null,
        cashAmount: null,
        orderId: null,
        addressSnapshotJson: null,
        status: null,
        logisticsCompany: null,
        logisticsNo: null,
        shipTime: null,
        completeTime: null,
        aftersaleStatus: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        delFlag: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.exchangeId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加积分商城兑换订单"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const exchangeId = row.exchangeId || this.ids
      getJst_mall_exchange_order(exchangeId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改积分商城兑换订单"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.exchangeId != null) {
            updateJst_mall_exchange_order(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_mall_exchange_order(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const exchangeIds = row.exchangeId || this.ids
      this.$modal.confirm('是否确认删除积分商城兑换订单编号为"' + exchangeIds + '"的数据项？').then(function() {
        return delJst_mall_exchange_order(exchangeIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_mall_exchange_order/export', {
        ...this.queryParams
      }, `jst_mall_exchange_order_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
