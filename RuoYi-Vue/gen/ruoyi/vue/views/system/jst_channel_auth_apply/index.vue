<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="申请单号" prop="applyNo">
        <el-input
          v-model="queryParams.applyNo"
          placeholder="请输入申请单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请人用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入申请人用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="通过后生成的渠道方ID" prop="channelId">
        <el-input
          v-model="queryParams.channelId"
          placeholder="请输入通过后生成的渠道方ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请名称" prop="applyName">
        <el-input
          v-model="queryParams.applyName"
          placeholder="请输入申请名称"
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
      <el-form-item label="审核员ID" prop="auditUserId">
        <el-input
          v-model="queryParams.auditUserId"
          placeholder="请输入审核员ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提交时间" prop="submitTime">
        <el-date-picker clearable
          v-model="queryParams.submitTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择提交时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="审核时间" prop="auditTime">
        <el-date-picker clearable
          v-model="queryParams.auditTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择审核时间">
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
          v-hasPermi="['system:jst_channel_auth_apply:add']"
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
          v-hasPermi="['system:jst_channel_auth_apply:edit']"
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
          v-hasPermi="['system:jst_channel_auth_apply:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_channel_auth_apply:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_channel_auth_applyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="申请ID" align="center" prop="applyId" />
      <el-table-column label="申请单号" align="center" prop="applyNo" />
      <el-table-column label="申请人用户ID" align="center" prop="userId" />
      <el-table-column label="通过后生成的渠道方ID" align="center" prop="channelId" />
      <el-table-column label="渠道类型：teacher/organization/individual" align="center" prop="channelType" />
      <el-table-column label="申请名称" align="center" prop="applyName" />
      <el-table-column label="认证材料JSON" align="center" prop="materialsJson" />
      <el-table-column label="状态：pending/approved/rejected/suspended" align="center" prop="applyStatus" />
      <el-table-column label="审核备注" align="center" prop="auditRemark" />
      <el-table-column label="审核员ID" align="center" prop="auditUserId" />
      <el-table-column label="提交时间" align="center" prop="submitTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.submitTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核时间" align="center" prop="auditTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.auditTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['system:jst_channel_auth_apply:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_channel_auth_apply:remove']"
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

    <!-- 添加或修改渠道认证申请对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="申请单号" prop="applyNo">
              <el-input v-model="form.applyNo" placeholder="请输入申请单号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="申请人用户ID" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入申请人用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="通过后生成的渠道方ID" prop="channelId">
              <el-input v-model="form.channelId" placeholder="请输入通过后生成的渠道方ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="申请名称" prop="applyName">
              <el-input v-model="form.applyName" placeholder="请输入申请名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核备注" prop="auditRemark">
              <el-input v-model="form.auditRemark" placeholder="请输入审核备注" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核员ID" prop="auditUserId">
              <el-input v-model="form.auditUserId" placeholder="请输入审核员ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="提交时间" prop="submitTime">
              <el-date-picker clearable
                v-model="form.submitTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择提交时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核时间" prop="auditTime">
              <el-date-picker clearable
                v-model="form.auditTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择审核时间">
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
import { listJst_channel_auth_apply, getJst_channel_auth_apply, delJst_channel_auth_apply, addJst_channel_auth_apply, updateJst_channel_auth_apply } from "@/api/system/jst_channel_auth_apply"

export default {
  name: "Jst_channel_auth_apply",
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
      // 渠道认证申请表格数据
      jst_channel_auth_applyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        applyNo: null,
        userId: null,
        channelId: null,
        channelType: null,
        applyName: null,
        materialsJson: null,
        applyStatus: null,
        auditRemark: null,
        auditUserId: null,
        submitTime: null,
        auditTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        applyNo: [
          { required: true, message: "申请单号不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "申请人用户ID不能为空", trigger: "blur" }
        ],
        channelType: [
          { required: true, message: "渠道类型：teacher/organization/individual不能为空", trigger: "change" }
        ],
        applyName: [
          { required: true, message: "申请名称不能为空", trigger: "blur" }
        ],
        applyStatus: [
          { required: true, message: "状态：pending/approved/rejected/suspended不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询渠道认证申请列表 */
    getList() {
      this.loading = true
      listJst_channel_auth_apply(this.queryParams).then(response => {
        this.jst_channel_auth_applyList = response.rows
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
        applyId: null,
        applyNo: null,
        userId: null,
        channelId: null,
        channelType: null,
        applyName: null,
        materialsJson: null,
        applyStatus: null,
        auditRemark: null,
        auditUserId: null,
        submitTime: null,
        auditTime: null,
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
      this.ids = selection.map(item => item.applyId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加渠道认证申请"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const applyId = row.applyId || this.ids
      getJst_channel_auth_apply(applyId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改渠道认证申请"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.applyId != null) {
            updateJst_channel_auth_apply(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_channel_auth_apply(this.form).then(response => {
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
      const applyIds = row.applyId || this.ids
      this.$modal.confirm('是否确认删除渠道认证申请编号为"' + applyIds + '"的数据项？').then(function() {
        return delJst_channel_auth_apply(applyIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_channel_auth_apply/export', {
        ...this.queryParams
      }, `jst_channel_auth_apply_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
