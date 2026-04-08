<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="预约号" prop="appointmentNo">
        <el-input
          v-model="queryParams.appointmentNo"
          placeholder="请输入预约号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联活动" prop="contestId">
        <el-input
          v-model="queryParams.contestId"
          placeholder="请输入关联活动"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
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
      <el-form-item label="锁定渠道方" prop="channelId">
        <el-input
          v-model="queryParams.channelId"
          placeholder="请输入锁定渠道方"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联团队主记录" prop="teamAppointmentId">
        <el-input
          v-model="queryParams.teamAppointmentId"
          placeholder="请输入关联团队主记录"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预约日期" prop="appointmentDate">
        <el-date-picker clearable
          v-model="queryParams.appointmentDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择预约日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="场次/时段" prop="sessionCode">
        <el-input
          v-model="queryParams.sessionCode"
          placeholder="请输入场次/时段"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联订单" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入关联订单"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="主二维码URL" prop="qrCode">
        <el-input
          v-model="queryParams.qrCode"
          placeholder="请输入主二维码URL"
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
          v-hasPermi="['system:jst_appointment_record:add']"
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
          v-hasPermi="['system:jst_appointment_record:edit']"
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
          v-hasPermi="['system:jst_appointment_record:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_appointment_record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_appointment_recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="预约ID" align="center" prop="appointmentId" />
      <el-table-column label="预约号" align="center" prop="appointmentNo" />
      <el-table-column label="关联活动" align="center" prop="contestId" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="参赛人ID" align="center" prop="participantId" />
      <el-table-column label="锁定渠道方" align="center" prop="channelId" />
      <el-table-column label="类型：individual/team" align="center" prop="appointmentType" />
      <el-table-column label="关联团队主记录" align="center" prop="teamAppointmentId" />
      <el-table-column label="预约日期" align="center" prop="appointmentDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.appointmentDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="场次/时段" align="center" prop="sessionCode" />
      <el-table-column label="关联订单" align="center" prop="orderId" />
      <el-table-column label="主状态：unbooked/booked/partial_writeoff/fully_writeoff/partial_writeoff_ended/cancelled/expired/no_show" align="center" prop="mainStatus" />
      <el-table-column label="主二维码URL" align="center" prop="qrCode" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_appointment_record:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_appointment_record:remove']"
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

    <!-- 添加或修改个人预约记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="预约号" prop="appointmentNo">
              <el-input v-model="form.appointmentNo" placeholder="请输入预约号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联活动" prop="contestId">
              <el-input v-model="form.contestId" placeholder="请输入关联活动" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="参赛人ID" prop="participantId">
              <el-input v-model="form.participantId" placeholder="请输入参赛人ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="锁定渠道方" prop="channelId">
              <el-input v-model="form.channelId" placeholder="请输入锁定渠道方" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联团队主记录" prop="teamAppointmentId">
              <el-input v-model="form.teamAppointmentId" placeholder="请输入关联团队主记录" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="预约日期" prop="appointmentDate">
              <el-date-picker clearable
                v-model="form.appointmentDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择预约日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="场次/时段" prop="sessionCode">
              <el-input v-model="form.sessionCode" placeholder="请输入场次/时段" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联订单" prop="orderId">
              <el-input v-model="form.orderId" placeholder="请输入关联订单" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="主二维码URL" prop="qrCode">
              <el-input v-model="form.qrCode" placeholder="请输入主二维码URL" />
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
import { listJst_appointment_record, getJst_appointment_record, delJst_appointment_record, addJst_appointment_record, updateJst_appointment_record } from "@/api/jst/order/jst_appointment_record"

export default {
  name: "Jst_appointment_record",
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
      // 个人预约记录表格数据
      jst_appointment_recordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        appointmentNo: null,
        contestId: null,
        userId: null,
        participantId: null,
        channelId: null,
        appointmentType: null,
        teamAppointmentId: null,
        appointmentDate: null,
        sessionCode: null,
        orderId: null,
        mainStatus: null,
        qrCode: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        appointmentNo: [
          { required: true, message: "预约号不能为空", trigger: "blur" }
        ],
        contestId: [
          { required: true, message: "关联活动不能为空", trigger: "blur" }
        ],
        participantId: [
          { required: true, message: "参赛人ID不能为空", trigger: "blur" }
        ],
        appointmentType: [
          { required: true, message: "类型：individual/team不能为空", trigger: "change" }
        ],
        appointmentDate: [
          { required: true, message: "预约日期不能为空", trigger: "blur" }
        ],
        mainStatus: [
          { required: true, message: "主状态：unbooked/booked/partial_writeoff/fully_writeoff/partial_writeoff_ended/cancelled/expired/no_show不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询个人预约记录列表 */
    getList() {
      this.loading = true
      listJst_appointment_record(this.queryParams).then(response => {
        this.jst_appointment_recordList = response.rows
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
        appointmentId: null,
        appointmentNo: null,
        contestId: null,
        userId: null,
        participantId: null,
        channelId: null,
        appointmentType: null,
        teamAppointmentId: null,
        appointmentDate: null,
        sessionCode: null,
        orderId: null,
        mainStatus: null,
        qrCode: null,
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
      this.ids = selection.map(item => item.appointmentId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加个人预约记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const appointmentId = row.appointmentId || this.ids
      getJst_appointment_record(appointmentId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改个人预约记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.appointmentId != null) {
            updateJst_appointment_record(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_appointment_record(this.form).then(response => {
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
      const appointmentIds = row.appointmentId || this.ids
      this.$modal.confirm('是否确认删除个人预约记录编号为"' + appointmentIds + '"的数据项？').then(function() {
        return delJst_appointment_record(appointmentIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_appointment_record/export', {
        ...this.queryParams
      }, `jst_appointment_record_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
