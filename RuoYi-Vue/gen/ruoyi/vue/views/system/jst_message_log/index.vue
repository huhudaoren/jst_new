<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模板编码" prop="templateCode">
        <el-input
          v-model="queryParams.templateCode"
          placeholder="请输入模板编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="通道：inner/sms/wechat" prop="channel">
        <el-input
          v-model="queryParams.channel"
          placeholder="请输入通道：inner/sms/wechat"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="接收用户ID" prop="targetUserId">
        <el-input
          v-model="queryParams.targetUserId"
          placeholder="请输入接收用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="接收手机" prop="targetMobile">
        <el-input
          v-model="queryParams.targetMobile"
          placeholder="请输入接收手机"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="触发来源" prop="triggerSource">
        <el-input
          v-model="queryParams.triggerSource"
          placeholder="请输入触发来源"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="失败原因" prop="failReason">
        <el-input
          v-model="queryParams.failReason"
          placeholder="请输入失败原因"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发送时间" prop="sendTime">
        <el-date-picker clearable
          v-model="queryParams.sendTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择发送时间">
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
          v-hasPermi="['system:jst_message_log:add']"
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
          v-hasPermi="['system:jst_message_log:edit']"
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
          v-hasPermi="['system:jst_message_log:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_message_log:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_message_logList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="日志ID" align="center" prop="logId" />
      <el-table-column label="模板编码" align="center" prop="templateCode" />
      <el-table-column label="通道：inner/sms/wechat" align="center" prop="channel" />
      <el-table-column label="接收用户ID" align="center" prop="targetUserId" />
      <el-table-column label="接收手机" align="center" prop="targetMobile" />
      <el-table-column label="触发来源" align="center" prop="triggerSource" />
      <el-table-column label="渲染后的实际内容" align="center" prop="content" />
      <el-table-column label="发送状态：pending/success/failed" align="center" prop="sendStatus" />
      <el-table-column label="失败原因" align="center" prop="failReason" />
      <el-table-column label="发送时间" align="center" prop="sendTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.sendTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['system:jst_message_log:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_message_log:remove']"
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

    <!-- 添加或修改消息发送日志对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="模板编码" prop="templateCode">
              <el-input v-model="form.templateCode" placeholder="请输入模板编码" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="通道：inner/sms/wechat" prop="channel">
              <el-input v-model="form.channel" placeholder="请输入通道：inner/sms/wechat" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="接收用户ID" prop="targetUserId">
              <el-input v-model="form.targetUserId" placeholder="请输入接收用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="接收手机" prop="targetMobile">
              <el-input v-model="form.targetMobile" placeholder="请输入接收手机" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="触发来源" prop="triggerSource">
              <el-input v-model="form.triggerSource" placeholder="请输入触发来源" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="渲染后的实际内容">
              <editor v-model="form.content" :min-height="192"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="失败原因" prop="failReason">
              <el-input v-model="form.failReason" placeholder="请输入失败原因" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="发送时间" prop="sendTime">
              <el-date-picker clearable
                v-model="form.sendTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择发送时间">
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
import { listJst_message_log, getJst_message_log, delJst_message_log, addJst_message_log, updateJst_message_log } from "@/api/system/jst_message_log"

export default {
  name: "Jst_message_log",
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
      // 消息发送日志表格数据
      jst_message_logList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        templateCode: null,
        channel: null,
        targetUserId: null,
        targetMobile: null,
        triggerSource: null,
        content: null,
        sendStatus: null,
        failReason: null,
        sendTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        templateCode: [
          { required: true, message: "模板编码不能为空", trigger: "blur" }
        ],
        channel: [
          { required: true, message: "通道：inner/sms/wechat不能为空", trigger: "blur" }
        ],
        sendStatus: [
          { required: true, message: "发送状态：pending/success/failed不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询消息发送日志列表 */
    getList() {
      this.loading = true
      listJst_message_log(this.queryParams).then(response => {
        this.jst_message_logList = response.rows
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
        logId: null,
        templateCode: null,
        channel: null,
        targetUserId: null,
        targetMobile: null,
        triggerSource: null,
        content: null,
        sendStatus: null,
        failReason: null,
        sendTime: null,
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
      this.ids = selection.map(item => item.logId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加消息发送日志"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const logId = row.logId || this.ids
      getJst_message_log(logId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改消息发送日志"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.logId != null) {
            updateJst_message_log(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_message_log(this.form).then(response => {
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
      const logIds = row.logId || this.ids
      this.$modal.confirm('是否确认删除消息发送日志编号为"' + logIds + '"的数据项？').then(function() {
        return delJst_message_log(logIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_message_log/export', {
        ...this.queryParams
      }, `jst_message_log_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
