<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="核销单号" prop="writeoffNo">
        <el-input
          v-model="queryParams.writeoffNo"
          placeholder="请输入核销单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户权益ID" prop="userRightsId">
        <el-input
          v-model="queryParams.userRightsId"
          placeholder="请输入用户权益ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="使用额度" prop="useAmount">
        <el-input
          v-model="queryParams.useAmount"
          placeholder="请输入使用额度"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请说明" prop="applyRemark">
        <el-input
          v-model="queryParams.applyRemark"
          placeholder="请输入申请说明"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核员" prop="auditUserId">
        <el-input
          v-model="queryParams.auditUserId"
          placeholder="请输入审核员"
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
      <el-form-item label="核销时间" prop="writeoffTime">
        <el-date-picker clearable
          v-model="queryParams.writeoffTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择核销时间">
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
          v-hasPermi="['system:jst_rights_writeoff_record:add']"
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
          v-hasPermi="['system:jst_rights_writeoff_record:edit']"
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
          v-hasPermi="['system:jst_rights_writeoff_record:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_rights_writeoff_record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_rights_writeoff_recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="核销记录ID" align="center" prop="recordId" />
      <el-table-column label="核销单号" align="center" prop="writeoffNo" />
      <el-table-column label="用户权益ID" align="center" prop="userRightsId" />
      <el-table-column label="使用额度" align="center" prop="useAmount" />
      <el-table-column label="申请说明" align="center" prop="applyRemark" />
      <el-table-column label="状态：pending/approved/rejected/confirmed/rolled_back" align="center" prop="status" />
      <el-table-column label="审核员" align="center" prop="auditUserId" />
      <el-table-column label="审核备注" align="center" prop="auditRemark" />
      <el-table-column label="核销时间" align="center" prop="writeoffTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.writeoffTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['system:jst_rights_writeoff_record:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_rights_writeoff_record:remove']"
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

    <!-- 添加或修改权益核销记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="核销单号" prop="writeoffNo">
              <el-input v-model="form.writeoffNo" placeholder="请输入核销单号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="用户权益ID" prop="userRightsId">
              <el-input v-model="form.userRightsId" placeholder="请输入用户权益ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="使用额度" prop="useAmount">
              <el-input v-model="form.useAmount" placeholder="请输入使用额度" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="申请说明" prop="applyRemark">
              <el-input v-model="form.applyRemark" placeholder="请输入申请说明" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核员" prop="auditUserId">
              <el-input v-model="form.auditUserId" placeholder="请输入审核员" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核备注" prop="auditRemark">
              <el-input v-model="form.auditRemark" placeholder="请输入审核备注" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="核销时间" prop="writeoffTime">
              <el-date-picker clearable
                v-model="form.writeoffTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择核销时间">
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
import { listJst_rights_writeoff_record, getJst_rights_writeoff_record, delJst_rights_writeoff_record, addJst_rights_writeoff_record, updateJst_rights_writeoff_record } from "@/api/system/jst_rights_writeoff_record"

export default {
  name: "Jst_rights_writeoff_record",
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
      // 权益核销记录表格数据
      jst_rights_writeoff_recordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        writeoffNo: null,
        userRightsId: null,
        useAmount: null,
        applyRemark: null,
        status: null,
        auditUserId: null,
        auditRemark: null,
        writeoffTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        writeoffNo: [
          { required: true, message: "核销单号不能为空", trigger: "blur" }
        ],
        userRightsId: [
          { required: true, message: "用户权益ID不能为空", trigger: "blur" }
        ],
        useAmount: [
          { required: true, message: "使用额度不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态：pending/approved/rejected/confirmed/rolled_back不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询权益核销记录列表 */
    getList() {
      this.loading = true
      listJst_rights_writeoff_record(this.queryParams).then(response => {
        this.jst_rights_writeoff_recordList = response.rows
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
        recordId: null,
        writeoffNo: null,
        userRightsId: null,
        useAmount: null,
        applyRemark: null,
        status: null,
        auditUserId: null,
        auditRemark: null,
        writeoffTime: null,
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
      this.ids = selection.map(item => item.recordId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加权益核销记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const recordId = row.recordId || this.ids
      getJst_rights_writeoff_record(recordId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改权益核销记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.recordId != null) {
            updateJst_rights_writeoff_record(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_rights_writeoff_record(this.form).then(response => {
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
      const recordIds = row.recordId || this.ids
      this.$modal.confirm('是否确认删除权益核销记录编号为"' + recordIds + '"的数据项？').then(function() {
        return delJst_rights_writeoff_record(recordIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_rights_writeoff_record/export', {
        ...this.queryParams
      }, `jst_rights_writeoff_record_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
