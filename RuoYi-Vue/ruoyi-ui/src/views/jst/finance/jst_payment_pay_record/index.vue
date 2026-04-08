<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="打款单号" prop="payNo">
        <el-input
          v-model="queryParams.payNo"
          placeholder="请输入打款单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="业务单据ID" prop="businessId">
        <el-input
          v-model="queryParams.businessId"
          placeholder="请输入业务单据ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收款方ID" prop="targetId">
        <el-input
          v-model="queryParams.targetId"
          placeholder="请输入收款方ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="打款金额" prop="amount">
        <el-input
          v-model="queryParams.amount"
          placeholder="请输入打款金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="打款账户快照" prop="payAccount">
        <el-input
          v-model="queryParams.payAccount"
          placeholder="请输入打款账户快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="打款时间" prop="payTime">
        <el-date-picker clearable
          v-model="queryParams.payTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择打款时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="打款凭证URL" prop="voucherUrl">
        <el-input
          v-model="queryParams.voucherUrl"
          placeholder="请输入打款凭证URL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作人" prop="operatorId">
        <el-input
          v-model="queryParams.operatorId"
          placeholder="请输入操作人"
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
          v-hasPermi="['system:jst_payment_pay_record:add']"
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
          v-hasPermi="['system:jst_payment_pay_record:edit']"
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
          v-hasPermi="['system:jst_payment_pay_record:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_payment_pay_record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_payment_pay_recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="打款记录ID" align="center" prop="payRecordId" />
      <el-table-column label="打款单号" align="center" prop="payNo" />
      <el-table-column label="业务类型：rebate_withdraw渠道提现/event_settlement赛事方结算" align="center" prop="businessType" />
      <el-table-column label="业务单据ID" align="center" prop="businessId" />
      <el-table-column label="收款方类型：channel/partner" align="center" prop="targetType" />
      <el-table-column label="收款方ID" align="center" prop="targetId" />
      <el-table-column label="打款金额" align="center" prop="amount" />
      <el-table-column label="打款账户快照" align="center" prop="payAccount" />
      <el-table-column label="打款时间" align="center" prop="payTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.payTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="打款凭证URL" align="center" prop="voucherUrl" />
      <el-table-column label="操作人" align="center" prop="operatorId" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_payment_pay_record:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_payment_pay_record:remove']"
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

    <!-- 添加或修改统一打款记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="打款单号" prop="payNo">
              <el-input v-model="form.payNo" placeholder="请输入打款单号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="业务单据ID" prop="businessId">
              <el-input v-model="form.businessId" placeholder="请输入业务单据ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="收款方ID" prop="targetId">
              <el-input v-model="form.targetId" placeholder="请输入收款方ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="打款金额" prop="amount">
              <el-input v-model="form.amount" placeholder="请输入打款金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="打款账户快照" prop="payAccount">
              <el-input v-model="form.payAccount" placeholder="请输入打款账户快照" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="打款时间" prop="payTime">
              <el-date-picker clearable
                v-model="form.payTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择打款时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="打款凭证URL" prop="voucherUrl">
              <el-input v-model="form.voucherUrl" placeholder="请输入打款凭证URL" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作人" prop="operatorId">
              <el-input v-model="form.operatorId" placeholder="请输入操作人" />
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
import { listJst_payment_pay_record, getJst_payment_pay_record, delJst_payment_pay_record, addJst_payment_pay_record, updateJst_payment_pay_record } from "@/api/jst/finance/jst_payment_pay_record"

export default {
  name: "Jst_payment_pay_record",
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
      // 统一打款记录表格数据
      jst_payment_pay_recordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        payNo: null,
        businessType: null,
        businessId: null,
        targetType: null,
        targetId: null,
        amount: null,
        payAccount: null,
        payTime: null,
        voucherUrl: null,
        operatorId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        payNo: [
          { required: true, message: "打款单号不能为空", trigger: "blur" }
        ],
        businessType: [
          { required: true, message: "业务类型：rebate_withdraw渠道提现/event_settlement赛事方结算不能为空", trigger: "change" }
        ],
        businessId: [
          { required: true, message: "业务单据ID不能为空", trigger: "blur" }
        ],
        targetType: [
          { required: true, message: "收款方类型：channel/partner不能为空", trigger: "change" }
        ],
        targetId: [
          { required: true, message: "收款方ID不能为空", trigger: "blur" }
        ],
        amount: [
          { required: true, message: "打款金额不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询统一打款记录列表 */
    getList() {
      this.loading = true
      listJst_payment_pay_record(this.queryParams).then(response => {
        this.jst_payment_pay_recordList = response.rows
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
        payRecordId: null,
        payNo: null,
        businessType: null,
        businessId: null,
        targetType: null,
        targetId: null,
        amount: null,
        payAccount: null,
        payTime: null,
        voucherUrl: null,
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
      this.ids = selection.map(item => item.payRecordId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加统一打款记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const payRecordId = row.payRecordId || this.ids
      getJst_payment_pay_record(payRecordId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改统一打款记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.payRecordId != null) {
            updateJst_payment_pay_record(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_payment_pay_record(this.form).then(response => {
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
      const payRecordIds = row.payRecordId || this.ids
      this.$modal.confirm('是否确认删除统一打款记录编号为"' + payRecordIds + '"的数据项？').then(function() {
        return delJst_payment_pay_record(payRecordIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_payment_pay_record/export', {
        ...this.queryParams
      }, `jst_payment_pay_record_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
