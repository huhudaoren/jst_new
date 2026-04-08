<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="学生用户ID，FK→jst_user" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入学生用户ID，FK→jst_user"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道方ID，FK→jst_channel" prop="channelId">
        <el-input
          v-model="queryParams.channelId"
          placeholder="请输入渠道方ID，FK→jst_channel"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="绑定生效时间" prop="bindTime">
        <el-date-picker clearable
          v-model="queryParams.bindTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择绑定生效时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="失效时间" prop="unbindTime">
        <el-date-picker clearable
          v-model="queryParams.unbindTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择失效时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="解绑原因" prop="unbindReason">
        <el-input
          v-model="queryParams.unbindReason"
          placeholder="请输入解绑原因"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="解绑操作人ID" prop="operatorId">
        <el-input
          v-model="queryParams.operatorId"
          placeholder="请输入解绑操作人ID"
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
          v-hasPermi="['system:jst_student_channel_binding:add']"
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
          v-hasPermi="['system:jst_student_channel_binding:edit']"
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
          v-hasPermi="['system:jst_student_channel_binding:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_student_channel_binding:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_student_channel_bindingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="绑定ID" align="center" prop="bindingId" />
      <el-table-column label="学生用户ID，FK→jst_user" align="center" prop="userId" />
      <el-table-column label="渠道方ID，FK→jst_channel" align="center" prop="channelId" />
      <el-table-column label="绑定生效时间" align="center" prop="bindTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.bindTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="失效时间" align="center" prop="unbindTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.unbindTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态：active生效/expired过期/replaced被覆盖/manual_unbound人工解绑" align="center" prop="status" />
      <el-table-column label="解绑原因" align="center" prop="unbindReason" />
      <el-table-column label="解绑操作人ID" align="center" prop="operatorId" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_student_channel_binding:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_student_channel_binding:remove']"
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

    <!-- 添加或修改学生-渠道方绑定关系（同一user同时仅1条active）对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="学生用户ID，FK→jst_user" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入学生用户ID，FK→jst_user" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="渠道方ID，FK→jst_channel" prop="channelId">
              <el-input v-model="form.channelId" placeholder="请输入渠道方ID，FK→jst_channel" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="绑定生效时间" prop="bindTime">
              <el-date-picker clearable
                v-model="form.bindTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择绑定生效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="失效时间" prop="unbindTime">
              <el-date-picker clearable
                v-model="form.unbindTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择失效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="解绑原因" prop="unbindReason">
              <el-input v-model="form.unbindReason" placeholder="请输入解绑原因" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="解绑操作人ID" prop="operatorId">
              <el-input v-model="form.operatorId" placeholder="请输入解绑操作人ID" />
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
import { listJst_student_channel_binding, getJst_student_channel_binding, delJst_student_channel_binding, addJst_student_channel_binding, updateJst_student_channel_binding } from "@/api/system/jst_student_channel_binding"

export default {
  name: "Jst_student_channel_binding",
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
      // 学生-渠道方绑定关系（同一user同时仅1条active）表格数据
      jst_student_channel_bindingList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        channelId: null,
        bindTime: null,
        unbindTime: null,
        status: null,
        unbindReason: null,
        operatorId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "学生用户ID，FK→jst_user不能为空", trigger: "blur" }
        ],
        channelId: [
          { required: true, message: "渠道方ID，FK→jst_channel不能为空", trigger: "blur" }
        ],
        bindTime: [
          { required: true, message: "绑定生效时间不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态：active生效/expired过期/replaced被覆盖/manual_unbound人工解绑不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询学生-渠道方绑定关系（同一user同时仅1条active）列表 */
    getList() {
      this.loading = true
      listJst_student_channel_binding(this.queryParams).then(response => {
        this.jst_student_channel_bindingList = response.rows
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
        bindingId: null,
        userId: null,
        channelId: null,
        bindTime: null,
        unbindTime: null,
        status: null,
        unbindReason: null,
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
      this.ids = selection.map(item => item.bindingId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加学生-渠道方绑定关系（同一user同时仅1条active）"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const bindingId = row.bindingId || this.ids
      getJst_student_channel_binding(bindingId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改学生-渠道方绑定关系（同一user同时仅1条active）"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.bindingId != null) {
            updateJst_student_channel_binding(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_student_channel_binding(this.form).then(response => {
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
      const bindingIds = row.bindingId || this.ids
      this.$modal.confirm('是否确认删除学生-渠道方绑定关系（同一user同时仅1条active）编号为"' + bindingIds + '"的数据项？').then(function() {
        return delJst_student_channel_binding(bindingIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_student_channel_binding/export', {
        ...this.queryParams
      }, `jst_student_channel_binding_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
