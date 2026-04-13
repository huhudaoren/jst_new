<template>
  <!-- DEPRECATED: 请使用 jst/enroll/index.vue -->
  <div class="app-container">
    <el-alert
      title="此页面已废弃"
      type="warning"
      :closable="false"
      show-icon
      style="margin-bottom: 16px"
    >
      <template slot="default">
        请使用精品页继续处理报名审核。
        <el-button type="text" @click="$router.push('/jst/enroll')">前往新页面</el-button>
      </template>
    </el-alert>
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="报名编号" prop="enrollNo">
        <el-input
          v-model="queryParams.enrollNo"
          placeholder="请输入报名编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛事ID，FK→jst_contest" prop="contestId">
        <el-input
          v-model="queryParams.contestId"
          placeholder="请输入赛事ID，FK→jst_contest"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学生用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入学生用户ID"
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
      <el-form-item label="关联渠道方ID" prop="channelId">
        <el-input
          v-model="queryParams.channelId"
          placeholder="请输入关联渠道方ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报名来源：self自助/channel_single渠道单条/channel_batch渠道批量" prop="enrollSource">
        <el-input
          v-model="queryParams.enrollSource"
          placeholder="请输入报名来源：self自助/channel_single渠道单条/channel_batch渠道批量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="使用的表单模板ID" prop="templateId">
        <el-input
          v-model="queryParams.templateId"
          placeholder="请输入使用的表单模板ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="表单模板版本号" prop="templateVersion">
        <el-input
          v-model="queryParams.templateVersion"
          placeholder="请输入表单模板版本号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联订单ID" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入关联订单ID"
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
      <el-form-item label="提交时间" prop="submitTime">
        <el-date-picker clearable
          v-model="queryParams.submitTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择提交时间">
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
          v-hasPermi="['system:jst_enroll_record:add']"
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
          v-hasPermi="['system:jst_enroll_record:edit']"
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
          v-hasPermi="['system:jst_enroll_record:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_enroll_record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_enroll_recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="报名ID" align="center" prop="enrollId" />
      <el-table-column label="报名编号" align="center" prop="enrollNo" />
      <el-table-column label="赛事ID，FK→jst_contest" align="center" prop="contestId" />
      <el-table-column label="学生用户ID" align="center" prop="userId" />
      <el-table-column label="参赛人ID" align="center" prop="participantId" />
      <el-table-column label="关联渠道方ID" align="center" prop="channelId" />
      <el-table-column label="报名来源：self自助/channel_single渠道单条/channel_batch渠道批量" align="center" prop="enrollSource" />
      <el-table-column label="使用的表单模板ID" align="center" prop="templateId" />
      <el-table-column label="表单模板版本号" align="center" prop="templateVersion" />
      <el-table-column label="报名字段全量快照JSON：动态字段值+附件URL" align="center" prop="formSnapshotJson" />
      <el-table-column label="关联订单ID" align="center" prop="orderId" />
      <el-table-column label="资料状态：draft/submitted/supplement" align="center" prop="materialStatus" />
      <el-table-column label="审核状态：pending/approved/rejected/supplement" align="center" prop="auditStatus" />
      <el-table-column label="审核备注" align="center" prop="auditRemark" />
      <el-table-column label="提交时间" align="center" prop="submitTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.submitTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['system:jst_enroll_record:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_enroll_record:remove']"
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

    <!-- 添加或修改报名记录（含动态单快照）对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="报名编号" prop="enrollNo">
              <el-input v-model="form.enrollNo" placeholder="请输入报名编号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="赛事ID，FK→jst_contest" prop="contestId">
              <el-input v-model="form.contestId" placeholder="请输入赛事ID，FK→jst_contest" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="学生用户ID" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入学生用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="参赛人ID" prop="participantId">
              <el-input v-model="form.participantId" placeholder="请输入参赛人ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联渠道方ID" prop="channelId">
              <el-input v-model="form.channelId" placeholder="请输入关联渠道方ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="报名来源：self自助/channel_single渠道单条/channel_batch渠道批量" prop="enrollSource">
              <el-input v-model="form.enrollSource" placeholder="请输入报名来源：self自助/channel_single渠道单条/channel_batch渠道批量" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="使用的表单模板ID" prop="templateId">
              <el-input v-model="form.templateId" placeholder="请输入使用的表单模板ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="表单模板版本号" prop="templateVersion">
              <el-input v-model="form.templateVersion" placeholder="请输入表单模板版本号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联订单ID" prop="orderId">
              <el-input v-model="form.orderId" placeholder="请输入关联订单ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核备注" prop="auditRemark">
              <el-input v-model="form.auditRemark" placeholder="请输入审核备注" />
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
import { listJst_enroll_record, getJst_enroll_record, delJst_enroll_record, addJst_enroll_record, updateJst_enroll_record } from "@/api/jst/event/jst_enroll_record"

export default {
  name: "Jst_enroll_record",
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
      // 报名记录（含动态单快照）表格数据
      jst_enroll_recordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        enrollNo: null,
        contestId: null,
        userId: null,
        participantId: null,
        channelId: null,
        enrollSource: null,
        templateId: null,
        templateVersion: null,
        formSnapshotJson: null,
        orderId: null,
        materialStatus: null,
        auditStatus: null,
        auditRemark: null,
        submitTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        enrollNo: [
          { required: true, message: "报名编号不能为空", trigger: "blur" }
        ],
        contestId: [
          { required: true, message: "赛事ID，FK→jst_contest不能为空", trigger: "blur" }
        ],
        participantId: [
          { required: true, message: "参赛人ID不能为空", trigger: "blur" }
        ],
        enrollSource: [
          { required: true, message: "报名来源：self自助/channel_single渠道单条/channel_batch渠道批量不能为空", trigger: "blur" }
        ],
        templateId: [
          { required: true, message: "使用的表单模板ID不能为空", trigger: "blur" }
        ],
        templateVersion: [
          { required: true, message: "表单模板版本号不能为空", trigger: "blur" }
        ],
        formSnapshotJson: [
          { required: true, message: "报名字段全量快照JSON：动态字段值+附件URL不能为空", trigger: "blur" }
        ],
        materialStatus: [
          { required: true, message: "资料状态：draft/submitted/supplement不能为空", trigger: "change" }
        ],
        auditStatus: [
          { required: true, message: "审核状态：pending/approved/rejected/supplement不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询报名记录（含动态单快照）列表 */
    getList() {
      this.loading = true
      listJst_enroll_record(this.queryParams).then(response => {
        this.jst_enroll_recordList = response.rows
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
        enrollId: null,
        enrollNo: null,
        contestId: null,
        userId: null,
        participantId: null,
        channelId: null,
        enrollSource: null,
        templateId: null,
        templateVersion: null,
        formSnapshotJson: null,
        orderId: null,
        materialStatus: null,
        auditStatus: null,
        auditRemark: null,
        submitTime: null,
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
      this.ids = selection.map(item => item.enrollId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加报名记录（含动态单快照）"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const enrollId = row.enrollId || this.ids
      getJst_enroll_record(enrollId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改报名记录（含动态单快照）"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.enrollId != null) {
            updateJst_enroll_record(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_enroll_record(this.form).then(response => {
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
      const enrollIds = row.enrollId || this.ids
      this.$modal.confirm('是否确认删除报名记录（含动态单快照）编号为"' + enrollIds + '"的数据项？').then(function() {
        return delJst_enroll_record(enrollIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_enroll_record/export', {
        ...this.queryParams
      }, `jst_enroll_record_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
