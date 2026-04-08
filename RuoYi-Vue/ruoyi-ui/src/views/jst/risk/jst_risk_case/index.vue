<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工单号" prop="caseNo">
        <el-input
          v-model="queryParams.caseNo"
          placeholder="请输入工单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联预警" prop="alertId">
        <el-input
          v-model="queryParams.alertId"
          placeholder="请输入关联预警"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="处理人" prop="assigneeId">
        <el-input
          v-model="queryParams.assigneeId"
          placeholder="请输入处理人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="处理结论" prop="conclusion">
        <el-input
          v-model="queryParams.conclusion"
          placeholder="请输入处理结论"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="复核人" prop="reviewerId">
        <el-input
          v-model="queryParams.reviewerId"
          placeholder="请输入复核人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关闭时间" prop="closeTime">
        <el-date-picker clearable
          v-model="queryParams.closeTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择关闭时间">
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
          v-hasPermi="['system:jst_risk_case:add']"
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
          v-hasPermi="['system:jst_risk_case:edit']"
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
          v-hasPermi="['system:jst_risk_case:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_risk_case:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_risk_caseList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="工单ID" align="center" prop="caseId" />
      <el-table-column label="工单号" align="center" prop="caseNo" />
      <el-table-column label="关联预警" align="center" prop="alertId" />
      <el-table-column label="处理人" align="center" prop="assigneeId" />
      <el-table-column label="状态：open/assigned/processing/reviewing/closed" align="center" prop="status" />
      <el-table-column label="处理结论" align="center" prop="conclusion" />
      <el-table-column label="复核人" align="center" prop="reviewerId" />
      <el-table-column label="关闭时间" align="center" prop="closeTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.closeTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['system:jst_risk_case:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_risk_case:remove']"
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

    <!-- 添加或修改风险工单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="工单号" prop="caseNo">
              <el-input v-model="form.caseNo" placeholder="请输入工单号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联预警" prop="alertId">
              <el-input v-model="form.alertId" placeholder="请输入关联预警" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="处理人" prop="assigneeId">
              <el-input v-model="form.assigneeId" placeholder="请输入处理人" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="处理结论" prop="conclusion">
              <el-input v-model="form.conclusion" placeholder="请输入处理结论" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="复核人" prop="reviewerId">
              <el-input v-model="form.reviewerId" placeholder="请输入复核人" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关闭时间" prop="closeTime">
              <el-date-picker clearable
                v-model="form.closeTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择关闭时间">
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
import { listJst_risk_case, getJst_risk_case, delJst_risk_case, addJst_risk_case, updateJst_risk_case } from "@/api/jst/risk/jst_risk_case"

export default {
  name: "Jst_risk_case",
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
      // 风险工单表格数据
      jst_risk_caseList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        caseNo: null,
        alertId: null,
        assigneeId: null,
        status: null,
        conclusion: null,
        reviewerId: null,
        closeTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        caseNo: [
          { required: true, message: "工单号不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态：open/assigned/processing/reviewing/closed不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询风险工单列表 */
    getList() {
      this.loading = true
      listJst_risk_case(this.queryParams).then(response => {
        this.jst_risk_caseList = response.rows
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
        caseId: null,
        caseNo: null,
        alertId: null,
        assigneeId: null,
        status: null,
        conclusion: null,
        reviewerId: null,
        closeTime: null,
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
      this.ids = selection.map(item => item.caseId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加风险工单"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const caseId = row.caseId || this.ids
      getJst_risk_case(caseId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改风险工单"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.caseId != null) {
            updateJst_risk_case(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_risk_case(this.form).then(response => {
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
      const caseIds = row.caseId || this.ids
      this.$modal.confirm('是否确认删除风险工单编号为"' + caseIds + '"的数据项？').then(function() {
        return delJst_risk_case(caseIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_risk_case/export', {
        ...this.queryParams
      }, `jst_risk_case_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
