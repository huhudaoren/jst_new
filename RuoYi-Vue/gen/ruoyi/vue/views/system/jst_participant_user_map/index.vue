<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="临时档案ID，FK→jst_participant" prop="participantId">
        <el-input
          v-model="queryParams.participantId"
          placeholder="请输入临时档案ID，FK→jst_participant"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="正式用户ID，FK→jst_user" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入正式用户ID，FK→jst_user"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="认领方式：auto_phone_name手机姓名自动/manual_admin管理员手动/manual_user用户手动" prop="claimMethod">
        <el-input
          v-model="queryParams.claimMethod"
          placeholder="请输入认领方式：auto_phone_name手机姓名自动/manual_admin管理员手动/manual_user用户手动"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="认领生效时间" prop="claimTime">
        <el-date-picker clearable
          v-model="queryParams.claimTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择认领生效时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="操作管理员ID" prop="operatorId">
        <el-input
          v-model="queryParams.operatorId"
          placeholder="请输入操作管理员ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="撤销原因" prop="revokeReason">
        <el-input
          v-model="queryParams.revokeReason"
          placeholder="请输入撤销原因"
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
          v-hasPermi="['system:jst_participant_user_map:add']"
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
          v-hasPermi="['system:jst_participant_user_map:edit']"
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
          v-hasPermi="['system:jst_participant_user_map:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_participant_user_map:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_participant_user_mapList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="映射ID" align="center" prop="mapId" />
      <el-table-column label="临时档案ID，FK→jst_participant" align="center" prop="participantId" />
      <el-table-column label="正式用户ID，FK→jst_user" align="center" prop="userId" />
      <el-table-column label="认领方式：auto_phone_name手机姓名自动/manual_admin管理员手动/manual_user用户手动" align="center" prop="claimMethod" />
      <el-table-column label="认领生效时间" align="center" prop="claimTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.claimTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作管理员ID" align="center" prop="operatorId" />
      <el-table-column label="映射状态：active生效/revoked已撤销" align="center" prop="status" />
      <el-table-column label="撤销原因" align="center" prop="revokeReason" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_participant_user_map:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_participant_user_map:remove']"
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

    <!-- 添加或修改参赛档案-正式用户认领映射对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="临时档案ID，FK→jst_participant" prop="participantId">
              <el-input v-model="form.participantId" placeholder="请输入临时档案ID，FK→jst_participant" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="正式用户ID，FK→jst_user" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入正式用户ID，FK→jst_user" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="认领方式：auto_phone_name手机姓名自动/manual_admin管理员手动/manual_user用户手动" prop="claimMethod">
              <el-input v-model="form.claimMethod" placeholder="请输入认领方式：auto_phone_name手机姓名自动/manual_admin管理员手动/manual_user用户手动" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="认领生效时间" prop="claimTime">
              <el-date-picker clearable
                v-model="form.claimTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择认领生效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作管理员ID" prop="operatorId">
              <el-input v-model="form.operatorId" placeholder="请输入操作管理员ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="撤销原因" prop="revokeReason">
              <el-input v-model="form.revokeReason" placeholder="请输入撤销原因" />
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
import { listJst_participant_user_map, getJst_participant_user_map, delJst_participant_user_map, addJst_participant_user_map, updateJst_participant_user_map } from "@/api/system/jst_participant_user_map"

export default {
  name: "Jst_participant_user_map",
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
      // 参赛档案-正式用户认领映射表格数据
      jst_participant_user_mapList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        participantId: null,
        userId: null,
        claimMethod: null,
        claimTime: null,
        operatorId: null,
        status: null,
        revokeReason: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        participantId: [
          { required: true, message: "临时档案ID，FK→jst_participant不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "正式用户ID，FK→jst_user不能为空", trigger: "blur" }
        ],
        claimMethod: [
          { required: true, message: "认领方式：auto_phone_name手机姓名自动/manual_admin管理员手动/manual_user用户手动不能为空", trigger: "blur" }
        ],
        claimTime: [
          { required: true, message: "认领生效时间不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "映射状态：active生效/revoked已撤销不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询参赛档案-正式用户认领映射列表 */
    getList() {
      this.loading = true
      listJst_participant_user_map(this.queryParams).then(response => {
        this.jst_participant_user_mapList = response.rows
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
        mapId: null,
        participantId: null,
        userId: null,
        claimMethod: null,
        claimTime: null,
        operatorId: null,
        status: null,
        revokeReason: null,
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
      this.ids = selection.map(item => item.mapId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加参赛档案-正式用户认领映射"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const mapId = row.mapId || this.ids
      getJst_participant_user_map(mapId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改参赛档案-正式用户认领映射"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.mapId != null) {
            updateJst_participant_user_map(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_participant_user_map(this.form).then(response => {
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
      const mapIds = row.mapId || this.ids
      this.$modal.confirm('是否确认删除参赛档案-正式用户认领映射编号为"' + mapIds + '"的数据项？').then(function() {
        return delJst_participant_user_map(mapIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_participant_user_map/export', {
        ...this.queryParams
      }, `jst_participant_user_map_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
