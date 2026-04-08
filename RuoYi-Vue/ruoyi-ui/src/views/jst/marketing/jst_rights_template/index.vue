<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="权益名称" prop="rightsName">
        <el-input
          v-model="queryParams.rightsName"
          placeholder="请输入权益名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="口径：times次数/amount金额/period周期" prop="quotaMode">
        <el-input
          v-model="queryParams.quotaMode"
          placeholder="请输入口径：times次数/amount金额/period周期"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="数值" prop="quotaValue">
        <el-input
          v-model="queryParams.quotaValue"
          placeholder="请输入数值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="有效期天数" prop="validDays">
        <el-input
          v-model="queryParams.validDays"
          placeholder="请输入有效期天数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="核销方式：online_auto/manual_review/offline_confirm" prop="writeoffMode">
        <el-input
          v-model="queryParams.writeoffMode"
          placeholder="请输入核销方式：online_auto/manual_review/offline_confirm"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="角色：student/channel/both" prop="applicableRole">
        <el-input
          v-model="queryParams.applicableRole"
          placeholder="请输入角色：student/channel/both"
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
          v-hasPermi="['system:jst_rights_template:add']"
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
          v-hasPermi="['system:jst_rights_template:edit']"
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
          v-hasPermi="['system:jst_rights_template:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_rights_template:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_rights_templateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="权益模板ID" align="center" prop="rightsTemplateId" />
      <el-table-column label="权益名称" align="center" prop="rightsName" />
      <el-table-column label="类型：enroll_deduct报名抵扣/venue_reduce场地减免/exclusive_course专属课程/cs_priority优先客服/custom自定义" align="center" prop="rightsType" />
      <el-table-column label="口径：times次数/amount金额/period周期" align="center" prop="quotaMode" />
      <el-table-column label="数值" align="center" prop="quotaValue" />
      <el-table-column label="有效期天数" align="center" prop="validDays" />
      <el-table-column label="核销方式：online_auto/manual_review/offline_confirm" align="center" prop="writeoffMode" />
      <el-table-column label="角色：student/channel/both" align="center" prop="applicableRole" />
      <el-table-column label="启停：0停 1启" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_rights_template:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_rights_template:remove']"
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

    <!-- 添加或修改权益模板对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="权益名称" prop="rightsName">
              <el-input v-model="form.rightsName" placeholder="请输入权益名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="口径：times次数/amount金额/period周期" prop="quotaMode">
              <el-input v-model="form.quotaMode" placeholder="请输入口径：times次数/amount金额/period周期" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="数值" prop="quotaValue">
              <el-input v-model="form.quotaValue" placeholder="请输入数值" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="有效期天数" prop="validDays">
              <el-input v-model="form.validDays" placeholder="请输入有效期天数" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="核销方式：online_auto/manual_review/offline_confirm" prop="writeoffMode">
              <el-input v-model="form.writeoffMode" placeholder="请输入核销方式：online_auto/manual_review/offline_confirm" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="角色：student/channel/both" prop="applicableRole">
              <el-input v-model="form.applicableRole" placeholder="请输入角色：student/channel/both" />
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
import { listJst_rights_template, getJst_rights_template, delJst_rights_template, addJst_rights_template, updateJst_rights_template } from "@/api/jst/marketing/jst_rights_template"

export default {
  name: "Jst_rights_template",
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
      // 权益模板表格数据
      jst_rights_templateList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        rightsName: null,
        rightsType: null,
        quotaMode: null,
        quotaValue: null,
        validDays: null,
        writeoffMode: null,
        applicableRole: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        rightsName: [
          { required: true, message: "权益名称不能为空", trigger: "blur" }
        ],
        rightsType: [
          { required: true, message: "类型：enroll_deduct报名抵扣/venue_reduce场地减免/exclusive_course专属课程/cs_priority优先客服/custom自定义不能为空", trigger: "change" }
        ],
        quotaMode: [
          { required: true, message: "口径：times次数/amount金额/period周期不能为空", trigger: "blur" }
        ],
        writeoffMode: [
          { required: true, message: "核销方式：online_auto/manual_review/offline_confirm不能为空", trigger: "blur" }
        ],
        applicableRole: [
          { required: true, message: "角色：student/channel/both不能为空", trigger: "blur" }
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
    /** 查询权益模板列表 */
    getList() {
      this.loading = true
      listJst_rights_template(this.queryParams).then(response => {
        this.jst_rights_templateList = response.rows
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
        rightsTemplateId: null,
        rightsName: null,
        rightsType: null,
        quotaMode: null,
        quotaValue: null,
        validDays: null,
        writeoffMode: null,
        applicableRole: null,
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
      this.ids = selection.map(item => item.rightsTemplateId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加权益模板"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const rightsTemplateId = row.rightsTemplateId || this.ids
      getJst_rights_template(rightsTemplateId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改权益模板"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.rightsTemplateId != null) {
            updateJst_rights_template(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_rights_template(this.form).then(response => {
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
      const rightsTemplateIds = row.rightsTemplateId || this.ids
      this.$modal.confirm('是否确认删除权益模板编号为"' + rightsTemplateIds + '"的数据项？').then(function() {
        return delJst_rights_template(rightsTemplateIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_rights_template/export', {
        ...this.queryParams
      }, `jst_rights_template_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
