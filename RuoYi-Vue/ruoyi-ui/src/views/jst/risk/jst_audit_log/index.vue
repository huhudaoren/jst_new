<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="操作人ID" prop="operatorId">
        <el-input
          v-model="queryParams.operatorId"
          placeholder="请输入操作人ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作人姓名" prop="operatorName">
        <el-input
          v-model="queryParams.operatorName"
          placeholder="请输入操作人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模块名" prop="module">
        <el-input
          v-model="queryParams.module"
          placeholder="请输入模块名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="动作：review/pay/adjust/refund/grant/revoke 等" prop="action">
        <el-input
          v-model="queryParams.action"
          placeholder="请输入动作：review/pay/adjust/refund/grant/revoke 等"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="目标对象ID" prop="targetId">
        <el-input
          v-model="queryParams.targetId"
          placeholder="请输入目标对象ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作IP" prop="ip">
        <el-input
          v-model="queryParams.ip"
          placeholder="请输入操作IP"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="终端：web/h5/api" prop="terminal">
        <el-input
          v-model="queryParams.terminal"
          placeholder="请输入终端：web/h5/api"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="结果：success/fail" prop="result">
        <el-input
          v-model="queryParams.result"
          placeholder="请输入结果：success/fail"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作时间" prop="opTime">
        <el-date-picker clearable
          v-model="queryParams.opTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择操作时间">
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
          v-hasPermi="['system:jst_audit_log:add']"
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
          v-hasPermi="['system:jst_audit_log:edit']"
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
          v-hasPermi="['system:jst_audit_log:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_audit_log:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_audit_logList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="日志ID" align="center" prop="auditId" />
      <el-table-column label="操作人ID" align="center" prop="operatorId" />
      <el-table-column label="操作人姓名" align="center" prop="operatorName" />
      <el-table-column label="操作人类型：admin/partner/channel/system" align="center" prop="operatorType" />
      <el-table-column label="模块名" align="center" prop="module" />
      <el-table-column label="动作：review/pay/adjust/refund/grant/revoke 等" align="center" prop="action" />
      <el-table-column label="目标对象类型" align="center" prop="targetType" />
      <el-table-column label="目标对象ID" align="center" prop="targetId" />
      <el-table-column label="变更前快照" align="center" prop="beforeJson" />
      <el-table-column label="变更后快照" align="center" prop="afterJson" />
      <el-table-column label="操作IP" align="center" prop="ip" />
      <el-table-column label="终端：web/h5/api" align="center" prop="terminal" />
      <el-table-column label="结果：success/fail" align="center" prop="result" />
      <el-table-column label="操作时间" align="center" prop="opTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.opTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['system:jst_audit_log:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_audit_log:remove']"
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

    <!-- 添加或修改操作审计日志对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="操作人ID" prop="operatorId">
              <el-input v-model="form.operatorId" placeholder="请输入操作人ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作人姓名" prop="operatorName">
              <el-input v-model="form.operatorName" placeholder="请输入操作人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="模块名" prop="module">
              <el-input v-model="form.module" placeholder="请输入模块名" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="动作：review/pay/adjust/refund/grant/revoke 等" prop="action">
              <el-input v-model="form.action" placeholder="请输入动作：review/pay/adjust/refund/grant/revoke 等" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="目标对象ID" prop="targetId">
              <el-input v-model="form.targetId" placeholder="请输入目标对象ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作IP" prop="ip">
              <el-input v-model="form.ip" placeholder="请输入操作IP" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="终端：web/h5/api" prop="terminal">
              <el-input v-model="form.terminal" placeholder="请输入终端：web/h5/api" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="结果：success/fail" prop="result">
              <el-input v-model="form.result" placeholder="请输入结果：success/fail" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作时间" prop="opTime">
              <el-date-picker clearable
                v-model="form.opTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择操作时间">
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
import { listJst_audit_log, getJst_audit_log, delJst_audit_log, addJst_audit_log, updateJst_audit_log } from "@/api/jst/risk/jst_audit_log"

export default {
  name: "Jst_audit_log",
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
      // 操作审计日志表格数据
      jst_audit_logList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        operatorId: null,
        operatorName: null,
        operatorType: null,
        module: null,
        action: null,
        targetType: null,
        targetId: null,
        beforeJson: null,
        afterJson: null,
        ip: null,
        terminal: null,
        result: null,
        opTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        operatorId: [
          { required: true, message: "操作人ID不能为空", trigger: "blur" }
        ],
        operatorType: [
          { required: true, message: "操作人类型：admin/partner/channel/system不能为空", trigger: "change" }
        ],
        module: [
          { required: true, message: "模块名不能为空", trigger: "blur" }
        ],
        action: [
          { required: true, message: "动作：review/pay/adjust/refund/grant/revoke 等不能为空", trigger: "blur" }
        ],
        result: [
          { required: true, message: "结果：success/fail不能为空", trigger: "blur" }
        ],
        opTime: [
          { required: true, message: "操作时间不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询操作审计日志列表 */
    getList() {
      this.loading = true
      listJst_audit_log(this.queryParams).then(response => {
        this.jst_audit_logList = response.rows
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
        auditId: null,
        operatorId: null,
        operatorName: null,
        operatorType: null,
        module: null,
        action: null,
        targetType: null,
        targetId: null,
        beforeJson: null,
        afterJson: null,
        ip: null,
        terminal: null,
        result: null,
        opTime: null,
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
      this.ids = selection.map(item => item.auditId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加操作审计日志"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const auditId = row.auditId || this.ids
      getJst_audit_log(auditId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改操作审计日志"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.auditId != null) {
            updateJst_audit_log(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_audit_log(this.form).then(response => {
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
      const auditIds = row.auditId || this.ids
      this.$modal.confirm('是否确认删除操作审计日志编号为"' + auditIds + '"的数据项？').then(function() {
        return delJst_audit_log(auditIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_audit_log/export', {
        ...this.queryParams
      }, `jst_audit_log_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
