<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="命中规则ID" prop="riskRuleId">
        <el-input
          v-model="queryParams.riskRuleId"
          placeholder="请输入命中规则ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="风险等级：low/mid/high" prop="riskLevel">
        <el-input
          v-model="queryParams.riskLevel"
          placeholder="请输入风险等级：low/mid/high"
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
      <el-form-item label="关联工单ID" prop="caseId">
        <el-input
          v-model="queryParams.caseId"
          placeholder="请输入关联工单ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预警时间" prop="alertTime">
        <el-date-picker clearable
          v-model="queryParams.alertTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择预警时间">
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
          v-hasPermi="['system:jst_risk_alert:add']"
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
          v-hasPermi="['system:jst_risk_alert:edit']"
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
          v-hasPermi="['system:jst_risk_alert:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_risk_alert:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_risk_alertList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="预警ID" align="center" prop="alertId" />
      <el-table-column label="命中规则ID" align="center" prop="riskRuleId" />
      <el-table-column label="风险等级：low/mid/high" align="center" prop="riskLevel" />
      <el-table-column label="对象类型：user/channel/order" align="center" prop="targetType" />
      <el-table-column label="对象ID" align="center" prop="targetId" />
      <el-table-column label="命中详情JSON" align="center" prop="hitDetailJson" />
      <el-table-column label="处理状态：open/processing/closed/false_alarm" align="center" prop="status" />
      <el-table-column label="关联工单ID" align="center" prop="caseId" />
      <el-table-column label="预警时间" align="center" prop="alertTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.alertTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['system:jst_risk_alert:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_risk_alert:remove']"
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

    <!-- 添加或修改风险预警对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="命中规则ID" prop="riskRuleId">
              <el-input v-model="form.riskRuleId" placeholder="请输入命中规则ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="风险等级：low/mid/high" prop="riskLevel">
              <el-input v-model="form.riskLevel" placeholder="请输入风险等级：low/mid/high" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="对象ID" prop="targetId">
              <el-input v-model="form.targetId" placeholder="请输入对象ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联工单ID" prop="caseId">
              <el-input v-model="form.caseId" placeholder="请输入关联工单ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="预警时间" prop="alertTime">
              <el-date-picker clearable
                v-model="form.alertTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择预警时间">
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
import { listJst_risk_alert, getJst_risk_alert, delJst_risk_alert, addJst_risk_alert, updateJst_risk_alert } from "@/api/system/jst_risk_alert"

export default {
  name: "Jst_risk_alert",
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
      // 风险预警表格数据
      jst_risk_alertList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        riskRuleId: null,
        riskLevel: null,
        targetType: null,
        targetId: null,
        hitDetailJson: null,
        status: null,
        caseId: null,
        alertTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        riskRuleId: [
          { required: true, message: "命中规则ID不能为空", trigger: "blur" }
        ],
        riskLevel: [
          { required: true, message: "风险等级：low/mid/high不能为空", trigger: "blur" }
        ],
        targetType: [
          { required: true, message: "对象类型：user/channel/order不能为空", trigger: "change" }
        ],
        targetId: [
          { required: true, message: "对象ID不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "处理状态：open/processing/closed/false_alarm不能为空", trigger: "change" }
        ],
        alertTime: [
          { required: true, message: "预警时间不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询风险预警列表 */
    getList() {
      this.loading = true
      listJst_risk_alert(this.queryParams).then(response => {
        this.jst_risk_alertList = response.rows
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
        alertId: null,
        riskRuleId: null,
        riskLevel: null,
        targetType: null,
        targetId: null,
        hitDetailJson: null,
        status: null,
        caseId: null,
        alertTime: null,
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
      this.ids = selection.map(item => item.alertId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加风险预警"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const alertId = row.alertId || this.ids
      getJst_risk_alert(alertId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改风险预警"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.alertId != null) {
            updateJst_risk_alert(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_risk_alert(this.form).then(response => {
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
      const alertIds = row.alertId || this.ids
      this.$modal.confirm('是否确认删除风险预警编号为"' + alertIds + '"的数据项？').then(function() {
        return delJst_risk_alert(alertIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_risk_alert/export', {
        ...this.queryParams
      }, `jst_risk_alert_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
