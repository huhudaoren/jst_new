<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="内部支付流水号" prop="paymentNo">
        <el-input
          v-model="queryParams.paymentNo"
          placeholder="请输入内部支付流水号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单ID" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入订单ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付方式：wechat/bank_transfer/points/mix" prop="payMethod">
        <el-input
          v-model="queryParams.payMethod"
          placeholder="请输入支付方式：wechat/bank_transfer/points/mix"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="现金金额" prop="cashAmount">
        <el-input
          v-model="queryParams.cashAmount"
          placeholder="请输入现金金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="积分折现金额" prop="pointsAmount">
        <el-input
          v-model="queryParams.pointsAmount"
          placeholder="请输入积分折现金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="消耗积分数" prop="pointsUsed">
        <el-input
          v-model="queryParams.pointsUsed"
          placeholder="请输入消耗积分数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="第三方流水号" prop="thirdPartyNo">
        <el-input
          v-model="queryParams.thirdPartyNo"
          placeholder="请输入第三方流水号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="对公转账凭证URL" prop="voucherUrl">
        <el-input
          v-model="queryParams.voucherUrl"
          placeholder="请输入对公转账凭证URL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="完成时间" prop="payTime">
        <el-date-picker clearable
          v-model="queryParams.payTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择完成时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="操作人ID" prop="operatorId">
        <el-input
          v-model="queryParams.operatorId"
          placeholder="请输入操作人ID"
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
          v-hasPermi="['system:jst_payment_record:add']"
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
          v-hasPermi="['system:jst_payment_record:edit']"
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
          v-hasPermi="['system:jst_payment_record:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_payment_record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_payment_recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="支付ID" align="center" prop="paymentId" />
      <el-table-column label="内部支付流水号" align="center" prop="paymentNo" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="支付方式：wechat/bank_transfer/points/mix" align="center" prop="payMethod" />
      <el-table-column label="现金金额" align="center" prop="cashAmount" />
      <el-table-column label="积分折现金额" align="center" prop="pointsAmount" />
      <el-table-column label="消耗积分数" align="center" prop="pointsUsed" />
      <el-table-column label="第三方流水号" align="center" prop="thirdPartyNo" />
      <el-table-column label="对公转账凭证URL" align="center" prop="voucherUrl" />
      <el-table-column label="凭证审核状态：pending/approved/rejected" align="center" prop="voucherAuditStatus" />
      <el-table-column label="支付状态：pending/success/failed/refunding/refunded" align="center" prop="payStatus" />
      <el-table-column label="完成时间" align="center" prop="payTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.payTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作人ID" align="center" prop="operatorId" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_payment_record:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_payment_record:remove']"
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

    <!-- 添加或修改支付记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="内部支付流水号" prop="paymentNo">
              <el-input v-model="form.paymentNo" placeholder="请输入内部支付流水号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="订单ID" prop="orderId">
              <el-input v-model="form.orderId" placeholder="请输入订单ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="支付方式：wechat/bank_transfer/points/mix" prop="payMethod">
              <el-input v-model="form.payMethod" placeholder="请输入支付方式：wechat/bank_transfer/points/mix" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="现金金额" prop="cashAmount">
              <el-input v-model="form.cashAmount" placeholder="请输入现金金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="积分折现金额" prop="pointsAmount">
              <el-input v-model="form.pointsAmount" placeholder="请输入积分折现金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="消耗积分数" prop="pointsUsed">
              <el-input v-model="form.pointsUsed" placeholder="请输入消耗积分数" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="第三方流水号" prop="thirdPartyNo">
              <el-input v-model="form.thirdPartyNo" placeholder="请输入第三方流水号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="对公转账凭证URL" prop="voucherUrl">
              <el-input v-model="form.voucherUrl" placeholder="请输入对公转账凭证URL" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="完成时间" prop="payTime">
              <el-date-picker clearable
                v-model="form.payTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择完成时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作人ID" prop="operatorId">
              <el-input v-model="form.operatorId" placeholder="请输入操作人ID" />
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
import { listJst_payment_record, getJst_payment_record, delJst_payment_record, addJst_payment_record, updateJst_payment_record } from "@/api/system/jst_payment_record"

export default {
  name: "Jst_payment_record",
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
      // 支付记录表格数据
      jst_payment_recordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        paymentNo: null,
        orderId: null,
        payMethod: null,
        cashAmount: null,
        pointsAmount: null,
        pointsUsed: null,
        thirdPartyNo: null,
        voucherUrl: null,
        voucherAuditStatus: null,
        payStatus: null,
        payTime: null,
        operatorId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        paymentNo: [
          { required: true, message: "内部支付流水号不能为空", trigger: "blur" }
        ],
        orderId: [
          { required: true, message: "订单ID不能为空", trigger: "blur" }
        ],
        payMethod: [
          { required: true, message: "支付方式：wechat/bank_transfer/points/mix不能为空", trigger: "blur" }
        ],
        cashAmount: [
          { required: true, message: "现金金额不能为空", trigger: "blur" }
        ],
        pointsAmount: [
          { required: true, message: "积分折现金额不能为空", trigger: "blur" }
        ],
        pointsUsed: [
          { required: true, message: "消耗积分数不能为空", trigger: "blur" }
        ],
        payStatus: [
          { required: true, message: "支付状态：pending/success/failed/refunding/refunded不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询支付记录列表 */
    getList() {
      this.loading = true
      listJst_payment_record(this.queryParams).then(response => {
        this.jst_payment_recordList = response.rows
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
        paymentId: null,
        paymentNo: null,
        orderId: null,
        payMethod: null,
        cashAmount: null,
        pointsAmount: null,
        pointsUsed: null,
        thirdPartyNo: null,
        voucherUrl: null,
        voucherAuditStatus: null,
        payStatus: null,
        payTime: null,
        operatorId: null,
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
      this.ids = selection.map(item => item.paymentId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加支付记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const paymentId = row.paymentId || this.ids
      getJst_payment_record(paymentId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改支付记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.paymentId != null) {
            updateJst_payment_record(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_payment_record(this.form).then(response => {
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
      const paymentIds = row.paymentId || this.ids
      this.$modal.confirm('是否确认删除支付记录编号为"' + paymentIds + '"的数据项？').then(function() {
        return delJst_payment_record(paymentIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_payment_record/export', {
        ...this.queryParams
      }, `jst_payment_record_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
