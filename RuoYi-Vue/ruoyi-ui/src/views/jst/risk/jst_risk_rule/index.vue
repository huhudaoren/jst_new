<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="规则名" prop="ruleName">
        <el-input
          v-model="queryParams.ruleName"
          placeholder="请输入规则名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="维度：user/device/mobile/channel" prop="dimension">
        <el-input
          v-model="queryParams.dimension"
          placeholder="请输入维度：user/device/mobile/channel"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="命中动作：warn告警/intercept拦截/manual人工" prop="action">
        <el-input
          v-model="queryParams.action"
          placeholder="请输入命中动作：warn告警/intercept拦截/manual人工"
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
          v-hasPermi="['system:jst_risk_rule:add']"
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
          v-hasPermi="['system:jst_risk_rule:edit']"
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
          v-hasPermi="['system:jst_risk_rule:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_risk_rule:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_risk_ruleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="规则ID" align="center" prop="riskRuleId" />
      <el-table-column label="规则名" align="center" prop="ruleName" />
      <el-table-column label="类型：bind_freq/coupon_freq/refund_freq/rebate_anomaly/zero_order_freq/aftersale_anomaly" align="center" prop="ruleType" />
      <el-table-column label="维度：user/device/mobile/channel" align="center" prop="dimension" />
      <el-table-column label="阈值配置JSON" align="center" prop="thresholdJson" />
      <el-table-column label="命中动作：warn告警/intercept拦截/manual人工" align="center" prop="action" />
      <el-table-column label="启停：0停 1启" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_risk_rule:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_risk_rule:remove']"
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

    <!-- 添加或修改风控规则对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="规则名" prop="ruleName">
              <el-input v-model="form.ruleName" placeholder="请输入规则名" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="维度：user/device/mobile/channel" prop="dimension">
              <el-input v-model="form.dimension" placeholder="请输入维度：user/device/mobile/channel" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="命中动作：warn告警/intercept拦截/manual人工" prop="action">
              <el-input v-model="form.action" placeholder="请输入命中动作：warn告警/intercept拦截/manual人工" />
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
import { listJst_risk_rule, getJst_risk_rule, delJst_risk_rule, addJst_risk_rule, updateJst_risk_rule } from "@/api/jst/risk/jst_risk_rule"

export default {
  name: "Jst_risk_rule",
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
      // 风控规则表格数据
      jst_risk_ruleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ruleName: null,
        ruleType: null,
        dimension: null,
        thresholdJson: null,
        action: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        ruleName: [
          { required: true, message: "规则名不能为空", trigger: "blur" }
        ],
        ruleType: [
          { required: true, message: "类型：bind_freq/coupon_freq/refund_freq/rebate_anomaly/zero_order_freq/aftersale_anomaly不能为空", trigger: "change" }
        ],
        dimension: [
          { required: true, message: "维度：user/device/mobile/channel不能为空", trigger: "blur" }
        ],
        thresholdJson: [
          { required: true, message: "阈值配置JSON不能为空", trigger: "blur" }
        ],
        action: [
          { required: true, message: "命中动作：warn告警/intercept拦截/manual人工不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "启停：0停 1启不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询风控规则列表 */
    getList() {
      this.loading = true
      listJst_risk_rule(this.queryParams).then(response => {
        this.jst_risk_ruleList = response.rows
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
        riskRuleId: null,
        ruleName: null,
        ruleType: null,
        dimension: null,
        thresholdJson: null,
        action: null,
        status: null,
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
      this.ids = selection.map(item => item.riskRuleId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加风控规则"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const riskRuleId = row.riskRuleId || this.ids
      getJst_risk_rule(riskRuleId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改风控规则"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.riskRuleId != null) {
            updateJst_risk_rule(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_risk_rule(this.form).then(response => {
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
      const riskRuleIds = row.riskRuleId || this.ids
      this.$modal.confirm('是否确认删除风控规则编号为"' + riskRuleIds + '"的数据项？').then(function() {
        return delJst_risk_rule(riskRuleIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_risk_rule/export', {
        ...this.queryParams
      }, `jst_risk_rule_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
