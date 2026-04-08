<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="结算单号" prop="settlementNo">
        <el-input
          v-model="queryParams.settlementNo"
          placeholder="请输入结算单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道方ID" prop="channelId">
        <el-input
          v-model="queryParams.channelId"
          placeholder="请输入渠道方ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请金额" prop="applyAmount">
        <el-input
          v-model="queryParams.applyAmount"
          placeholder="请输入申请金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="负向台账抵扣金额" prop="negativeOffsetAmount">
        <el-input
          v-model="queryParams.negativeOffsetAmount"
          placeholder="请输入负向台账抵扣金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="实际打款金额" prop="actualPayAmount">
        <el-input
          v-model="queryParams.actualPayAmount"
          placeholder="请输入实际打款金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联发票ID" prop="invoiceId">
        <el-input
          v-model="queryParams.invoiceId"
          placeholder="请输入关联发票ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收款账户快照" prop="bankAccountSnapshot">
        <el-input
          v-model="queryParams.bankAccountSnapshot"
          placeholder="请输入收款账户快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input
          v-model="queryParams.auditRemark"
          placeholder="请输入审核备注"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="打款凭证URL" prop="payVoucherUrl">
        <el-input
          v-model="queryParams.payVoucherUrl"
          placeholder="请输入打款凭证URL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请时间" prop="applyTime">
        <el-date-picker clearable
          v-model="queryParams.applyTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择申请时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="打款时间" prop="payTime">
        <el-date-picker clearable
          v-model="queryParams.payTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择打款时间">
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
          v-hasPermi="['system:jst_rebate_settlement:add']"
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
          v-hasPermi="['system:jst_rebate_settlement:edit']"
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
          v-hasPermi="['system:jst_rebate_settlement:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_rebate_settlement:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_rebate_settlementList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="结算单ID" align="center" prop="settlementId" />
      <el-table-column label="结算单号" align="center" prop="settlementNo" />
      <el-table-column label="渠道方ID" align="center" prop="channelId" />
      <el-table-column label="申请金额" align="center" prop="applyAmount" />
      <el-table-column label="负向台账抵扣金额" align="center" prop="negativeOffsetAmount" />
      <el-table-column label="实际打款金额" align="center" prop="actualPayAmount" />
      <el-table-column label="发票状态：none/pending/issued/voided" align="center" prop="invoiceStatus" />
      <el-table-column label="关联发票ID" align="center" prop="invoiceId" />
      <el-table-column label="收款账户快照" align="center" prop="bankAccountSnapshot" />
      <el-table-column label="状态：pending/reviewing/rejected/approved/paid" align="center" prop="status" />
      <el-table-column label="审核备注" align="center" prop="auditRemark" />
      <el-table-column label="打款凭证URL" align="center" prop="payVoucherUrl" />
      <el-table-column label="申请时间" align="center" prop="applyTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.applyTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="打款时间" align="center" prop="payTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.payTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_rebate_settlement:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_rebate_settlement:remove']"
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

    <!-- 添加或修改渠道提现/结算单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="结算单号" prop="settlementNo">
              <el-input v-model="form.settlementNo" placeholder="请输入结算单号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="渠道方ID" prop="channelId">
              <el-input v-model="form.channelId" placeholder="请输入渠道方ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="申请金额" prop="applyAmount">
              <el-input v-model="form.applyAmount" placeholder="请输入申请金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="负向台账抵扣金额" prop="negativeOffsetAmount">
              <el-input v-model="form.negativeOffsetAmount" placeholder="请输入负向台账抵扣金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="实际打款金额" prop="actualPayAmount">
              <el-input v-model="form.actualPayAmount" placeholder="请输入实际打款金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联发票ID" prop="invoiceId">
              <el-input v-model="form.invoiceId" placeholder="请输入关联发票ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="收款账户快照" prop="bankAccountSnapshot">
              <el-input v-model="form.bankAccountSnapshot" placeholder="请输入收款账户快照" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核备注" prop="auditRemark">
              <el-input v-model="form.auditRemark" placeholder="请输入审核备注" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="打款凭证URL" prop="payVoucherUrl">
              <el-input v-model="form.payVoucherUrl" placeholder="请输入打款凭证URL" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="申请时间" prop="applyTime">
              <el-date-picker clearable
                v-model="form.applyTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择申请时间">
              </el-date-picker>
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
import { listJst_rebate_settlement, getJst_rebate_settlement, delJst_rebate_settlement, addJst_rebate_settlement, updateJst_rebate_settlement } from "@/api/jst/channel/jst_rebate_settlement"

export default {
  name: "Jst_rebate_settlement",
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
      // 渠道提现/结算单表格数据
      jst_rebate_settlementList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        settlementNo: null,
        channelId: null,
        applyAmount: null,
        negativeOffsetAmount: null,
        actualPayAmount: null,
        invoiceStatus: null,
        invoiceId: null,
        bankAccountSnapshot: null,
        status: null,
        auditRemark: null,
        payVoucherUrl: null,
        applyTime: null,
        payTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        settlementNo: [
          { required: true, message: "结算单号不能为空", trigger: "blur" }
        ],
        channelId: [
          { required: true, message: "渠道方ID不能为空", trigger: "blur" }
        ],
        applyAmount: [
          { required: true, message: "申请金额不能为空", trigger: "blur" }
        ],
        negativeOffsetAmount: [
          { required: true, message: "负向台账抵扣金额不能为空", trigger: "blur" }
        ],
        actualPayAmount: [
          { required: true, message: "实际打款金额不能为空", trigger: "blur" }
        ],
        invoiceStatus: [
          { required: true, message: "发票状态：none/pending/issued/voided不能为空", trigger: "change" }
        ],
        status: [
          { required: true, message: "状态：pending/reviewing/rejected/approved/paid不能为空", trigger: "change" }
        ],
        applyTime: [
          { required: true, message: "申请时间不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询渠道提现/结算单列表 */
    getList() {
      this.loading = true
      listJst_rebate_settlement(this.queryParams).then(response => {
        this.jst_rebate_settlementList = response.rows
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
        settlementId: null,
        settlementNo: null,
        channelId: null,
        applyAmount: null,
        negativeOffsetAmount: null,
        actualPayAmount: null,
        invoiceStatus: null,
        invoiceId: null,
        bankAccountSnapshot: null,
        status: null,
        auditRemark: null,
        payVoucherUrl: null,
        applyTime: null,
        payTime: null,
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
      this.ids = selection.map(item => item.settlementId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加渠道提现/结算单"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const settlementId = row.settlementId || this.ids
      getJst_rebate_settlement(settlementId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改渠道提现/结算单"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.settlementId != null) {
            updateJst_rebate_settlement(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_rebate_settlement(this.form).then(response => {
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
      const settlementIds = row.settlementId || this.ids
      this.$modal.confirm('是否确认删除渠道提现/结算单编号为"' + settlementIds + '"的数据项？').then(function() {
        return delJst_rebate_settlement(settlementIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_rebate_settlement/export', {
        ...this.queryParams
      }, `jst_rebate_settlement_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
