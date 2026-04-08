<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="发票号码" prop="invoiceNo">
        <el-input
          v-model="queryParams.invoiceNo"
          placeholder="请输入发票号码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="对象ID" prop="targetId">
        <el-input
          v-model="queryParams.targetId"
          placeholder="请输入对象ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联结算/提现单号" prop="refSettlementNo">
        <el-input
          v-model="queryParams.refSettlementNo"
          placeholder="请输入关联结算/提现单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发票抬头" prop="invoiceTitle">
        <el-input
          v-model="queryParams.invoiceTitle"
          placeholder="请输入发票抬头"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="税号" prop="taxNo">
        <el-input
          v-model="queryParams.taxNo"
          placeholder="请输入税号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="金额" prop="amount">
        <el-input
          v-model="queryParams.amount"
          placeholder="请输入金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="票据附件URL" prop="fileUrl">
        <el-input
          v-model="queryParams.fileUrl"
          placeholder="请输入票据附件URL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="开票时间" prop="issueTime">
        <el-date-picker clearable
          v-model="queryParams.issueTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择开票时间">
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
          v-hasPermi="['system:jst_invoice_record:add']"
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
          v-hasPermi="['system:jst_invoice_record:edit']"
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
          v-hasPermi="['system:jst_invoice_record:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_invoice_record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_invoice_recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="发票ID" align="center" prop="invoiceId" />
      <el-table-column label="发票号码" align="center" prop="invoiceNo" />
      <el-table-column label="对象类型：channel/partner" align="center" prop="targetType" />
      <el-table-column label="对象ID" align="center" prop="targetId" />
      <el-table-column label="关联结算/提现单号" align="center" prop="refSettlementNo" />
      <el-table-column label="发票抬头" align="center" prop="invoiceTitle" />
      <el-table-column label="税号" align="center" prop="taxNo" />
      <el-table-column label="金额" align="center" prop="amount" />
      <el-table-column label="状态：pending_apply/reviewing/issuing/issued/voided/red_offset" align="center" prop="status" />
      <el-table-column label="票据附件URL" align="center" prop="fileUrl" />
      <el-table-column label="快递状态" align="center" prop="expressStatus" />
      <el-table-column label="开票时间" align="center" prop="issueTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.issueTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['system:jst_invoice_record:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_invoice_record:remove']"
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

    <!-- 添加或修改发票记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="发票号码" prop="invoiceNo">
              <el-input v-model="form.invoiceNo" placeholder="请输入发票号码" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="对象ID" prop="targetId">
              <el-input v-model="form.targetId" placeholder="请输入对象ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联结算/提现单号" prop="refSettlementNo">
              <el-input v-model="form.refSettlementNo" placeholder="请输入关联结算/提现单号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="发票抬头" prop="invoiceTitle">
              <el-input v-model="form.invoiceTitle" placeholder="请输入发票抬头" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="税号" prop="taxNo">
              <el-input v-model="form.taxNo" placeholder="请输入税号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="金额" prop="amount">
              <el-input v-model="form.amount" placeholder="请输入金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="票据附件URL" prop="fileUrl">
              <el-input v-model="form.fileUrl" placeholder="请输入票据附件URL" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="开票时间" prop="issueTime">
              <el-date-picker clearable
                v-model="form.issueTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择开票时间">
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
import { listJst_invoice_record, getJst_invoice_record, delJst_invoice_record, addJst_invoice_record, updateJst_invoice_record } from "@/api/jst/finance/jst_invoice_record"

export default {
  name: "Jst_invoice_record",
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
      // 发票记录表格数据
      jst_invoice_recordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        invoiceNo: null,
        targetType: null,
        targetId: null,
        refSettlementNo: null,
        invoiceTitle: null,
        taxNo: null,
        amount: null,
        status: null,
        fileUrl: null,
        expressStatus: null,
        issueTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        targetType: [
          { required: true, message: "对象类型：channel/partner不能为空", trigger: "change" }
        ],
        targetId: [
          { required: true, message: "对象ID不能为空", trigger: "blur" }
        ],
        invoiceTitle: [
          { required: true, message: "发票抬头不能为空", trigger: "blur" }
        ],
        amount: [
          { required: true, message: "金额不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态：pending_apply/reviewing/issuing/issued/voided/red_offset不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询发票记录列表 */
    getList() {
      this.loading = true
      listJst_invoice_record(this.queryParams).then(response => {
        this.jst_invoice_recordList = response.rows
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
        invoiceId: null,
        invoiceNo: null,
        targetType: null,
        targetId: null,
        refSettlementNo: null,
        invoiceTitle: null,
        taxNo: null,
        amount: null,
        status: null,
        fileUrl: null,
        expressStatus: null,
        issueTime: null,
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
      this.ids = selection.map(item => item.invoiceId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加发票记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const invoiceId = row.invoiceId || this.ids
      getJst_invoice_record(invoiceId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改发票记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.invoiceId != null) {
            updateJst_invoice_record(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_invoice_record(this.form).then(response => {
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
      const invoiceIds = row.invoiceId || this.ids
      this.$modal.confirm('是否确认删除发票记录编号为"' + invoiceIds + '"的数据项？').then(function() {
        return delJst_invoice_record(invoiceIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_invoice_record/export', {
        ...this.queryParams
      }, `jst_invoice_record_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
