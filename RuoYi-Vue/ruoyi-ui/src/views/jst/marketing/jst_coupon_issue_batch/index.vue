<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="批次号" prop="batchNo">
        <el-input
          v-model="queryParams.batchNo"
          placeholder="请输入批次号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模板ID" prop="couponTemplateId">
        <el-input
          v-model="queryParams.couponTemplateId"
          placeholder="请输入模板ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="计划数量" prop="totalCount">
        <el-input
          v-model="queryParams.totalCount"
          placeholder="请输入计划数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="成功数" prop="successCount">
        <el-input
          v-model="queryParams.successCount"
          placeholder="请输入成功数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="失败数" prop="failCount">
        <el-input
          v-model="queryParams.failCount"
          placeholder="请输入失败数"
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
          v-hasPermi="['system:jst_coupon_issue_batch:add']"
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
          v-hasPermi="['system:jst_coupon_issue_batch:edit']"
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
          v-hasPermi="['system:jst_coupon_issue_batch:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_coupon_issue_batch:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_coupon_issue_batchList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="批次ID" align="center" prop="batchId" />
      <el-table-column label="批次号" align="center" prop="batchNo" />
      <el-table-column label="模板ID" align="center" prop="couponTemplateId" />
      <el-table-column label="目标类型：user/channel/campaign/tag" align="center" prop="targetType" />
      <el-table-column label="目标对象JSON" align="center" prop="targetJson" />
      <el-table-column label="计划数量" align="center" prop="totalCount" />
      <el-table-column label="成功数" align="center" prop="successCount" />
      <el-table-column label="失败数" align="center" prop="failCount" />
      <el-table-column label="状态：pending/running/completed/failed" align="center" prop="status" />
      <el-table-column label="操作人" align="center" prop="operatorId" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_coupon_issue_batch:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_coupon_issue_batch:remove']"
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

    <!-- 添加或修改发券批次对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="批次号" prop="batchNo">
              <el-input v-model="form.batchNo" placeholder="请输入批次号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="模板ID" prop="couponTemplateId">
              <el-input v-model="form.couponTemplateId" placeholder="请输入模板ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="计划数量" prop="totalCount">
              <el-input v-model="form.totalCount" placeholder="请输入计划数量" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="成功数" prop="successCount">
              <el-input v-model="form.successCount" placeholder="请输入成功数" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="失败数" prop="failCount">
              <el-input v-model="form.failCount" placeholder="请输入失败数" />
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
import { listJst_coupon_issue_batch, getJst_coupon_issue_batch, delJst_coupon_issue_batch, addJst_coupon_issue_batch, updateJst_coupon_issue_batch } from "@/api/jst/marketing/jst_coupon_issue_batch"

export default {
  name: "Jst_coupon_issue_batch",
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
      // 发券批次表格数据
      jst_coupon_issue_batchList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        batchNo: null,
        couponTemplateId: null,
        targetType: null,
        targetJson: null,
        totalCount: null,
        successCount: null,
        failCount: null,
        status: null,
        operatorId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        batchNo: [
          { required: true, message: "批次号不能为空", trigger: "blur" }
        ],
        couponTemplateId: [
          { required: true, message: "模板ID不能为空", trigger: "blur" }
        ],
        targetType: [
          { required: true, message: "目标类型：user/channel/campaign/tag不能为空", trigger: "change" }
        ],
        totalCount: [
          { required: true, message: "计划数量不能为空", trigger: "blur" }
        ],
        successCount: [
          { required: true, message: "成功数不能为空", trigger: "blur" }
        ],
        failCount: [
          { required: true, message: "失败数不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态：pending/running/completed/failed不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询发券批次列表 */
    getList() {
      this.loading = true
      listJst_coupon_issue_batch(this.queryParams).then(response => {
        this.jst_coupon_issue_batchList = response.rows
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
        batchId: null,
        batchNo: null,
        couponTemplateId: null,
        targetType: null,
        targetJson: null,
        totalCount: null,
        successCount: null,
        failCount: null,
        status: null,
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
      this.ids = selection.map(item => item.batchId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加发券批次"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const batchId = row.batchId || this.ids
      getJst_coupon_issue_batch(batchId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改发券批次"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.batchId != null) {
            updateJst_coupon_issue_batch(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_coupon_issue_batch(this.form).then(response => {
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
      const batchIds = row.batchId || this.ids
      this.$modal.confirm('是否确认删除发券批次编号为"' + batchIds + '"的数据项？').then(function() {
        return delJst_coupon_issue_batch(batchIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_coupon_issue_batch/export', {
        ...this.queryParams
      }, `jst_coupon_issue_batch_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
