<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="证书编号，格式 JST-YYYY-ART-XXXXX" prop="certNo">
        <el-input
          v-model="queryParams.certNo"
          placeholder="请输入证书编号，格式 JST-YYYY-ART-XXXXX"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛事ID" prop="contestId">
        <el-input
          v-model="queryParams.contestId"
          placeholder="请输入赛事ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联成绩ID" prop="scoreId">
        <el-input
          v-model="queryParams.scoreId"
          placeholder="请输入关联成绩ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报名ID" prop="enrollId">
        <el-input
          v-model="queryParams.enrollId"
          placeholder="请输入报名ID"
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
      <el-form-item label="证书模板ID" prop="templateId">
        <el-input
          v-model="queryParams.templateId"
          placeholder="请输入证书模板ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="证书文件URL" prop="certFileUrl">
        <el-input
          v-model="queryParams.certFileUrl"
          placeholder="请输入证书文件URL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发放时间" prop="issueTime">
        <el-date-picker clearable
          v-model="queryParams.issueTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择发放时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="作废原因" prop="voidReason">
        <el-input
          v-model="queryParams.voidReason"
          placeholder="请输入作废原因"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公开校验码" prop="verifyCode">
        <el-input
          v-model="queryParams.verifyCode"
          placeholder="请输入公开校验码"
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
          v-hasPermi="['system:jst_cert_record:add']"
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
          v-hasPermi="['system:jst_cert_record:edit']"
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
          v-hasPermi="['system:jst_cert_record:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_cert_record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_cert_recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="证书ID" align="center" prop="certId" />
      <el-table-column label="证书编号，格式 JST-YYYY-ART-XXXXX" align="center" prop="certNo" />
      <el-table-column label="赛事ID" align="center" prop="contestId" />
      <el-table-column label="关联成绩ID" align="center" prop="scoreId" />
      <el-table-column label="报名ID" align="center" prop="enrollId" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="参赛人ID" align="center" prop="participantId" />
      <el-table-column label="证书模板ID" align="center" prop="templateId" />
      <el-table-column label="证书文件URL" align="center" prop="certFileUrl" />
      <el-table-column label="发放状态：pending/issued/voided" align="center" prop="issueStatus" />
      <el-table-column label="发放时间" align="center" prop="issueTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.issueTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="作废原因" align="center" prop="voidReason" />
      <el-table-column label="公开校验码" align="center" prop="verifyCode" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_cert_record:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_cert_record:remove']"
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

    <!-- 添加或修改证书记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="证书编号，格式 JST-YYYY-ART-XXXXX" prop="certNo">
              <el-input v-model="form.certNo" placeholder="请输入证书编号，格式 JST-YYYY-ART-XXXXX" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="赛事ID" prop="contestId">
              <el-input v-model="form.contestId" placeholder="请输入赛事ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联成绩ID" prop="scoreId">
              <el-input v-model="form.scoreId" placeholder="请输入关联成绩ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="报名ID" prop="enrollId">
              <el-input v-model="form.enrollId" placeholder="请输入报名ID" />
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
            <el-form-item label="证书模板ID" prop="templateId">
              <el-input v-model="form.templateId" placeholder="请输入证书模板ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="证书文件URL" prop="certFileUrl">
              <el-input v-model="form.certFileUrl" placeholder="请输入证书文件URL" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="发放时间" prop="issueTime">
              <el-date-picker clearable
                v-model="form.issueTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择发放时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="作废原因" prop="voidReason">
              <el-input v-model="form.voidReason" placeholder="请输入作废原因" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="公开校验码" prop="verifyCode">
              <el-input v-model="form.verifyCode" placeholder="请输入公开校验码" />
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
import { listJst_cert_record, getJst_cert_record, delJst_cert_record, addJst_cert_record, updateJst_cert_record } from "@/api/jst/event/jst_cert_record"

export default {
  name: "Jst_cert_record",
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
      // 证书记录表格数据
      jst_cert_recordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        certNo: null,
        contestId: null,
        scoreId: null,
        enrollId: null,
        userId: null,
        participantId: null,
        templateId: null,
        certFileUrl: null,
        issueStatus: null,
        issueTime: null,
        voidReason: null,
        verifyCode: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        certNo: [
          { required: true, message: "证书编号，格式 JST-YYYY-ART-XXXXX不能为空", trigger: "blur" }
        ],
        contestId: [
          { required: true, message: "赛事ID不能为空", trigger: "blur" }
        ],
        enrollId: [
          { required: true, message: "报名ID不能为空", trigger: "blur" }
        ],
        participantId: [
          { required: true, message: "参赛人ID不能为空", trigger: "blur" }
        ],
        issueStatus: [
          { required: true, message: "发放状态：pending/issued/voided不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询证书记录列表 */
    getList() {
      this.loading = true
      listJst_cert_record(this.queryParams).then(response => {
        this.jst_cert_recordList = response.rows
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
        certId: null,
        certNo: null,
        contestId: null,
        scoreId: null,
        enrollId: null,
        userId: null,
        participantId: null,
        templateId: null,
        certFileUrl: null,
        issueStatus: null,
        issueTime: null,
        voidReason: null,
        verifyCode: null,
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
      this.ids = selection.map(item => item.certId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加证书记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const certId = row.certId || this.ids
      getJst_cert_record(certId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改证书记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.certId != null) {
            updateJst_cert_record(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_cert_record(this.form).then(response => {
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
      const certIds = row.certId || this.ids
      this.$modal.confirm('是否确认删除证书记录编号为"' + certIds + '"的数据项？').then(function() {
        return delJst_cert_record(certIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_cert_record/export', {
        ...this.queryParams
      }, `jst_cert_record_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
