<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="权益模板ID" prop="rightsTemplateId">
        <el-input
          v-model="queryParams.rightsTemplateId"
          placeholder="请输入权益模板ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="持有方ID" prop="ownerId">
        <el-input
          v-model="queryParams.ownerId"
          placeholder="请输入持有方ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="来源单据ID" prop="sourceRefId">
        <el-input
          v-model="queryParams.sourceRefId"
          placeholder="请输入来源单据ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="剩余额度" prop="remainQuota">
        <el-input
          v-model="queryParams.remainQuota"
          placeholder="请输入剩余额度"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="生效时间" prop="validStart">
        <el-date-picker clearable
          v-model="queryParams.validStart"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择生效时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="失效时间" prop="validEnd">
        <el-date-picker clearable
          v-model="queryParams.validEnd"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择失效时间">
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
          v-hasPermi="['system:jst_user_rights:add']"
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
          v-hasPermi="['system:jst_user_rights:edit']"
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
          v-hasPermi="['system:jst_user_rights:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_user_rights:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_user_rightsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户权益ID" align="center" prop="userRightsId" />
      <el-table-column label="权益模板ID" align="center" prop="rightsTemplateId" />
      <el-table-column label="持有方：student/channel" align="center" prop="ownerType" />
      <el-table-column label="持有方ID" align="center" prop="ownerId" />
      <el-table-column label="来源：level等级/campaign活动/manual手工" align="center" prop="sourceType" />
      <el-table-column label="来源单据ID" align="center" prop="sourceRefId" />
      <el-table-column label="剩余额度" align="center" prop="remainQuota" />
      <el-table-column label="生效时间" align="center" prop="validStart" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.validStart, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="失效时间" align="center" prop="validEnd" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.validEnd, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态：available/locked/used/expired/revoked" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_user_rights:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_user_rights:remove']"
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

    <!-- 添加或修改用户权益持有对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="权益模板ID" prop="rightsTemplateId">
              <el-input v-model="form.rightsTemplateId" placeholder="请输入权益模板ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="持有方ID" prop="ownerId">
              <el-input v-model="form.ownerId" placeholder="请输入持有方ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="来源单据ID" prop="sourceRefId">
              <el-input v-model="form.sourceRefId" placeholder="请输入来源单据ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="剩余额度" prop="remainQuota">
              <el-input v-model="form.remainQuota" placeholder="请输入剩余额度" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="生效时间" prop="validStart">
              <el-date-picker clearable
                v-model="form.validStart"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择生效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="失效时间" prop="validEnd">
              <el-date-picker clearable
                v-model="form.validEnd"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择失效时间">
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
import { listJst_user_rights, getJst_user_rights, delJst_user_rights, addJst_user_rights, updateJst_user_rights } from "@/api/system/jst_user_rights"

export default {
  name: "Jst_user_rights",
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
      // 用户权益持有表格数据
      jst_user_rightsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        rightsTemplateId: null,
        ownerType: null,
        ownerId: null,
        sourceType: null,
        sourceRefId: null,
        remainQuota: null,
        validStart: null,
        validEnd: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        rightsTemplateId: [
          { required: true, message: "权益模板ID不能为空", trigger: "blur" }
        ],
        ownerType: [
          { required: true, message: "持有方：student/channel不能为空", trigger: "change" }
        ],
        ownerId: [
          { required: true, message: "持有方ID不能为空", trigger: "blur" }
        ],
        sourceType: [
          { required: true, message: "来源：level等级/campaign活动/manual手工不能为空", trigger: "change" }
        ],
        remainQuota: [
          { required: true, message: "剩余额度不能为空", trigger: "blur" }
        ],
        validStart: [
          { required: true, message: "生效时间不能为空", trigger: "blur" }
        ],
        validEnd: [
          { required: true, message: "失效时间不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态：available/locked/used/expired/revoked不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询用户权益持有列表 */
    getList() {
      this.loading = true
      listJst_user_rights(this.queryParams).then(response => {
        this.jst_user_rightsList = response.rows
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
        userRightsId: null,
        rightsTemplateId: null,
        ownerType: null,
        ownerId: null,
        sourceType: null,
        sourceRefId: null,
        remainQuota: null,
        validStart: null,
        validEnd: null,
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
      this.ids = selection.map(item => item.userRightsId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加用户权益持有"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const userRightsId = row.userRightsId || this.ids
      getJst_user_rights(userRightsId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改用户权益持有"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.userRightsId != null) {
            updateJst_user_rights(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_user_rights(this.form).then(response => {
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
      const userRightsIds = row.userRightsId || this.ids
      this.$modal.confirm('是否确认删除用户权益持有编号为"' + userRightsIds + '"的数据项？').then(function() {
        return delJst_user_rights(userRightsIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_user_rights/export', {
        ...this.queryParams
      }, `jst_user_rights_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
