<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="团队预约主ID" prop="teamAppointmentId">
        <el-input
          v-model="queryParams.teamAppointmentId"
          placeholder="请输入团队预约主ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="正式用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入正式用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参赛人ID" prop="participantId">
        <el-input
          v-model="queryParams.participantId"
          placeholder="请输入参赛人ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="成员编号" prop="memberNo">
        <el-input
          v-model="queryParams.memberNo"
          placeholder="请输入成员编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="姓名快照" prop="nameSnapshot">
        <el-input
          v-model="queryParams.nameSnapshot"
          placeholder="请输入姓名快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机快照" prop="mobileSnapshot">
        <el-input
          v-model="queryParams.mobileSnapshot"
          placeholder="请输入手机快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="个人子订单ID" prop="subOrderId">
        <el-input
          v-model="queryParams.subOrderId"
          placeholder="请输入个人子订单ID"
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
          v-hasPermi="['system:jst_team_appointment_member:add']"
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
          v-hasPermi="['system:jst_team_appointment_member:edit']"
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
          v-hasPermi="['system:jst_team_appointment_member:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_team_appointment_member:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_team_appointment_memberList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="成员明细ID" align="center" prop="memberId" />
      <el-table-column label="团队预约主ID" align="center" prop="teamAppointmentId" />
      <el-table-column label="正式用户ID" align="center" prop="userId" />
      <el-table-column label="参赛人ID" align="center" prop="participantId" />
      <el-table-column label="成员编号" align="center" prop="memberNo" />
      <el-table-column label="姓名快照" align="center" prop="nameSnapshot" />
      <el-table-column label="手机快照" align="center" prop="mobileSnapshot" />
      <el-table-column label="个人子订单ID" align="center" prop="subOrderId" />
      <el-table-column label="核销状态：unused/used/expired/voided" align="center" prop="writeoffStatus" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_team_appointment_member:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_team_appointment_member:remove']"
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

    <!-- 添加或修改团队预约成员明细对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="团队预约主ID" prop="teamAppointmentId">
              <el-input v-model="form.teamAppointmentId" placeholder="请输入团队预约主ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="正式用户ID" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入正式用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="参赛人ID" prop="participantId">
              <el-input v-model="form.participantId" placeholder="请输入参赛人ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="成员编号" prop="memberNo">
              <el-input v-model="form.memberNo" placeholder="请输入成员编号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="姓名快照" prop="nameSnapshot">
              <el-input v-model="form.nameSnapshot" placeholder="请输入姓名快照" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="手机快照" prop="mobileSnapshot">
              <el-input v-model="form.mobileSnapshot" placeholder="请输入手机快照" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="个人子订单ID" prop="subOrderId">
              <el-input v-model="form.subOrderId" placeholder="请输入个人子订单ID" />
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
import { listJst_team_appointment_member, getJst_team_appointment_member, delJst_team_appointment_member, addJst_team_appointment_member, updateJst_team_appointment_member } from "@/api/system/jst_team_appointment_member"

export default {
  name: "Jst_team_appointment_member",
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
      // 团队预约成员明细表格数据
      jst_team_appointment_memberList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        teamAppointmentId: null,
        userId: null,
        participantId: null,
        memberNo: null,
        nameSnapshot: null,
        mobileSnapshot: null,
        subOrderId: null,
        writeoffStatus: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        teamAppointmentId: [
          { required: true, message: "团队预约主ID不能为空", trigger: "blur" }
        ],
        participantId: [
          { required: true, message: "参赛人ID不能为空", trigger: "blur" }
        ],
        memberNo: [
          { required: true, message: "成员编号不能为空", trigger: "blur" }
        ],
        nameSnapshot: [
          { required: true, message: "姓名快照不能为空", trigger: "blur" }
        ],
        writeoffStatus: [
          { required: true, message: "核销状态：unused/used/expired/voided不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询团队预约成员明细列表 */
    getList() {
      this.loading = true
      listJst_team_appointment_member(this.queryParams).then(response => {
        this.jst_team_appointment_memberList = response.rows
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
        memberId: null,
        teamAppointmentId: null,
        userId: null,
        participantId: null,
        memberNo: null,
        nameSnapshot: null,
        mobileSnapshot: null,
        subOrderId: null,
        writeoffStatus: null,
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
      this.ids = selection.map(item => item.memberId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加团队预约成员明细"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const memberId = row.memberId || this.ids
      getJst_team_appointment_member(memberId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改团队预约成员明细"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.memberId != null) {
            updateJst_team_appointment_member(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_team_appointment_member(this.form).then(response => {
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
      const memberIds = row.memberId || this.ids
      this.$modal.confirm('是否确认删除团队预约成员明细编号为"' + memberIds + '"的数据项？').then(function() {
        return delJst_team_appointment_member(memberIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_team_appointment_member/export', {
        ...this.queryParams
      }, `jst_team_appointment_member_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
