<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单ID" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入订单ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="引用业务ID" prop="refId">
        <el-input
          v-model="queryParams.refId"
          placeholder="请输入引用业务ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品/项目名称" prop="itemName">
        <el-input
          v-model="queryParams.itemName"
          placeholder="请输入商品/项目名称"
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
      <el-form-item label="标价金额" prop="listAmount">
        <el-input
          v-model="queryParams.listAmount"
          placeholder="请输入标价金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="优惠券分摊金额" prop="couponShare">
        <el-input
          v-model="queryParams.couponShare"
          placeholder="请输入优惠券分摊金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="积分抵扣分摊金额" prop="pointsShare">
        <el-input
          v-model="queryParams.pointsShare"
          placeholder="请输入积分抵扣分摊金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="净实付分摊金额" prop="netPayShare">
        <el-input
          v-model="queryParams.netPayShare"
          placeholder="请输入净实付分摊金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="服务费分摊" prop="serviceFeeShare">
        <el-input
          v-model="queryParams.serviceFeeShare"
          placeholder="请输入服务费分摊"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道返点分摊" prop="rebateShare">
        <el-input
          v-model="queryParams.rebateShare"
          placeholder="请输入渠道返点分摊"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="已退现金累计" prop="refundAmount">
        <el-input
          v-model="queryParams.refundAmount"
          placeholder="请输入已退现金累计"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="已回退积分累计" prop="refundPoints">
        <el-input
          v-model="queryParams.refundPoints"
          placeholder="请输入已回退积分累计"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['system:jst_order_item:add']"
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
          v-hasPermi="['system:jst_order_item:edit']"
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
          v-hasPermi="['system:jst_order_item:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_order_item:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_order_itemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="明细ID" align="center" prop="itemId" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="类型：enroll/appointment_member/goods/course" align="center" prop="skuType" />
      <el-table-column label="引用业务ID" align="center" prop="refId" />
      <el-table-column label="商品/项目名称" align="center" prop="itemName" />
      <el-table-column label="数量" align="center" prop="quantity" />
      <el-table-column label="标价金额" align="center" prop="listAmount" />
      <el-table-column label="优惠券分摊金额" align="center" prop="couponShare" />
      <el-table-column label="积分抵扣分摊金额" align="center" prop="pointsShare" />
      <el-table-column label="净实付分摊金额" align="center" prop="netPayShare" />
      <el-table-column label="服务费分摊" align="center" prop="serviceFeeShare" />
      <el-table-column label="渠道返点分摊" align="center" prop="rebateShare" />
      <el-table-column label="已退现金累计" align="center" prop="refundAmount" />
      <el-table-column label="已回退积分累计" align="center" prop="refundPoints" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_order_item:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_order_item:remove']"
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

    <!-- 添加或修改订单明细（最小核算单元，承载分摊与回滚）对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="订单ID" prop="orderId">
              <el-input v-model="form.orderId" placeholder="请输入订单ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="引用业务ID" prop="refId">
              <el-input v-model="form.refId" placeholder="请输入引用业务ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品/项目名称" prop="itemName">
              <el-input v-model="form.itemName" placeholder="请输入商品/项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="数量" prop="quantity">
              <el-input v-model="form.quantity" placeholder="请输入数量" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="标价金额" prop="listAmount">
              <el-input v-model="form.listAmount" placeholder="请输入标价金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="优惠券分摊金额" prop="couponShare">
              <el-input v-model="form.couponShare" placeholder="请输入优惠券分摊金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="积分抵扣分摊金额" prop="pointsShare">
              <el-input v-model="form.pointsShare" placeholder="请输入积分抵扣分摊金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="净实付分摊金额" prop="netPayShare">
              <el-input v-model="form.netPayShare" placeholder="请输入净实付分摊金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="服务费分摊" prop="serviceFeeShare">
              <el-input v-model="form.serviceFeeShare" placeholder="请输入服务费分摊" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="渠道返点分摊" prop="rebateShare">
              <el-input v-model="form.rebateShare" placeholder="请输入渠道返点分摊" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="已退现金累计" prop="refundAmount">
              <el-input v-model="form.refundAmount" placeholder="请输入已退现金累计" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="已回退积分累计" prop="refundPoints">
              <el-input v-model="form.refundPoints" placeholder="请输入已回退积分累计" />
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
import { listJst_order_item, getJst_order_item, delJst_order_item, addJst_order_item, updateJst_order_item } from "@/api/jst/order/jst_order_item"

export default {
  name: "Jst_order_item",
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
      // 订单明细（最小核算单元，承载分摊与回滚）表格数据
      jst_order_itemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderId: null,
        skuType: null,
        refId: null,
        itemName: null,
        quantity: null,
        listAmount: null,
        couponShare: null,
        pointsShare: null,
        netPayShare: null,
        serviceFeeShare: null,
        rebateShare: null,
        refundAmount: null,
        refundPoints: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        orderId: [
          { required: true, message: "订单ID不能为空", trigger: "blur" }
        ],
        skuType: [
          { required: true, message: "类型：enroll/appointment_member/goods/course不能为空", trigger: "change" }
        ],
        itemName: [
          { required: true, message: "商品/项目名称不能为空", trigger: "blur" }
        ],
        quantity: [
          { required: true, message: "数量不能为空", trigger: "blur" }
        ],
        listAmount: [
          { required: true, message: "标价金额不能为空", trigger: "blur" }
        ],
        couponShare: [
          { required: true, message: "优惠券分摊金额不能为空", trigger: "blur" }
        ],
        pointsShare: [
          { required: true, message: "积分抵扣分摊金额不能为空", trigger: "blur" }
        ],
        netPayShare: [
          { required: true, message: "净实付分摊金额不能为空", trigger: "blur" }
        ],
        serviceFeeShare: [
          { required: true, message: "服务费分摊不能为空", trigger: "blur" }
        ],
        rebateShare: [
          { required: true, message: "渠道返点分摊不能为空", trigger: "blur" }
        ],
        refundAmount: [
          { required: true, message: "已退现金累计不能为空", trigger: "blur" }
        ],
        refundPoints: [
          { required: true, message: "已回退积分累计不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询订单明细（最小核算单元，承载分摊与回滚）列表 */
    getList() {
      this.loading = true
      listJst_order_item(this.queryParams).then(response => {
        this.jst_order_itemList = response.rows
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
        itemId: null,
        orderId: null,
        skuType: null,
        refId: null,
        itemName: null,
        quantity: null,
        listAmount: null,
        couponShare: null,
        pointsShare: null,
        netPayShare: null,
        serviceFeeShare: null,
        rebateShare: null,
        refundAmount: null,
        refundPoints: null,
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
      this.ids = selection.map(item => item.itemId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加订单明细（最小核算单元，承载分摊与回滚）"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const itemId = row.itemId || this.ids
      getJst_order_item(itemId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改订单明细（最小核算单元，承载分摊与回滚）"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.itemId != null) {
            updateJst_order_item(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_order_item(this.form).then(response => {
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
      const itemIds = row.itemId || this.ids
      this.$modal.confirm('是否确认删除订单明细（最小核算单元，承载分摊与回滚）编号为"' + itemIds + '"的数据项？').then(function() {
        return delJst_order_item(itemIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_order_item/export', {
        ...this.queryParams
      }, `jst_order_item_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
