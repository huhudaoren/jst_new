<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模板名称" prop="templateName">
        <el-input
          v-model="queryParams.templateName"
          placeholder="请输入模板名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="版本号" prop="templateVersion">
        <el-input
          v-model="queryParams.templateVersion"
          placeholder="请输入版本号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属赛事方ID" prop="ownerId">
        <el-input
          v-model="queryParams.ownerId"
          placeholder="请输入所属赛事方ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="生效时间" prop="effectiveTime">
        <el-date-picker clearable
          v-model="queryParams.effectiveTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择生效时间">
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
          v-hasPermi="['system:jst_enroll_form_template:add']"
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
          v-hasPermi="['system:jst_enroll_form_template:edit']"
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
          v-hasPermi="['system:jst_enroll_form_template:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_enroll_form_template:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_enroll_form_templateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="模板ID" align="center" prop="templateId" />
      <el-table-column label="模板名称" align="center" prop="templateName" />
      <el-table-column label="版本号" align="center" prop="templateVersion" />
      <el-table-column label="所属方：platform/partner" align="center" prop="ownerType" />
      <el-table-column label="所属赛事方ID" align="center" prop="ownerId" />
      <el-table-column label="字段定义JSON：[{key,type,label,required,validators,visibleIf,sort}]" align="center" prop="schemaJson" />
      <el-table-column label="审核状态：draft/pending/approved/rejected" align="center" prop="auditStatus" />
      <el-table-column label="启停：0停 1启" align="center" prop="status" />
      <el-table-column label="生效时间" align="center" prop="effectiveTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.effectiveTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['system:jst_enroll_form_template:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_enroll_form_template:remove']"
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

    <!-- 添加或修改报名动态单模板（schema_json定义字段）对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="模板名称" prop="templateName">
              <el-input v-model="form.templateName" placeholder="请输入模板名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="版本号" prop="templateVersion">
              <el-input v-model="form.templateVersion" placeholder="请输入版本号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="所属赛事方ID" prop="ownerId">
              <el-input v-model="form.ownerId" placeholder="请输入所属赛事方ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="生效时间" prop="effectiveTime">
              <el-date-picker clearable
                v-model="form.effectiveTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择生效时间">
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
import { listJst_enroll_form_template, getJst_enroll_form_template, delJst_enroll_form_template, addJst_enroll_form_template, updateJst_enroll_form_template } from "@/api/system/jst_enroll_form_template"

export default {
  name: "Jst_enroll_form_template",
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
      // 报名动态单模板（schema_json定义字段）表格数据
      jst_enroll_form_templateList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        templateName: null,
        templateVersion: null,
        ownerType: null,
        ownerId: null,
        schemaJson: null,
        auditStatus: null,
        status: null,
        effectiveTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        templateName: [
          { required: true, message: "模板名称不能为空", trigger: "blur" }
        ],
        templateVersion: [
          { required: true, message: "版本号不能为空", trigger: "blur" }
        ],
        ownerType: [
          { required: true, message: "所属方：platform/partner不能为空", trigger: "change" }
        ],
        schemaJson: [
          { required: true, message: "字段定义JSON：[{key,type,label,required,validators,visibleIf,sort}]不能为空", trigger: "blur" }
        ],
        auditStatus: [
          { required: true, message: "审核状态：draft/pending/approved/rejected不能为空", trigger: "change" }
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
    /** 查询报名动态单模板（schema_json定义字段）列表 */
    getList() {
      this.loading = true
      listJst_enroll_form_template(this.queryParams).then(response => {
        this.jst_enroll_form_templateList = response.rows
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
        templateId: null,
        templateName: null,
        templateVersion: null,
        ownerType: null,
        ownerId: null,
        schemaJson: null,
        auditStatus: null,
        status: null,
        effectiveTime: null,
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
      this.ids = selection.map(item => item.templateId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加报名动态单模板（schema_json定义字段）"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const templateId = row.templateId || this.ids
      getJst_enroll_form_template(templateId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改报名动态单模板（schema_json定义字段）"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.templateId != null) {
            updateJst_enroll_form_template(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_enroll_form_template(this.form).then(response => {
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
      const templateIds = row.templateId || this.ids
      this.$modal.confirm('是否确认删除报名动态单模板（schema_json定义字段）编号为"' + templateIds + '"的数据项？').then(function() {
        return delJst_enroll_form_template(templateIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_enroll_form_template/export', {
        ...this.queryParams
      }, `jst_enroll_form_template_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
