<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="关联用户账号ID，FK→jst_user" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入关联用户账号ID，FK→jst_user"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道方名称" prop="channelName">
        <el-input
          v-model="queryParams.channelName"
          placeholder="请输入渠道方名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系手机" prop="contactMobile">
        <el-input
          v-model="queryParams.contactMobile"
          placeholder="请输入联系手机"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="证件号" prop="idCardNo">
        <el-input
          v-model="queryParams.idCardNo"
          placeholder="请输入证件号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="营业执照号" prop="businessLicenseNo">
        <el-input
          v-model="queryParams.businessLicenseNo"
          placeholder="请输入营业执照号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="认证通过时间" prop="authTime">
        <el-date-picker clearable
          v-model="queryParams.authTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择认证通过时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="审核备注" prop="authRemark">
        <el-input
          v-model="queryParams.authRemark"
          placeholder="请输入审核备注"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="当前等级ID，FK→jst_level_config" prop="currentLevelId">
        <el-input
          v-model="queryParams.currentLevelId"
          placeholder="请输入当前等级ID，FK→jst_level_config"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="风控标记：0正常 1黑名单 2待复核" prop="riskFlag">
        <el-input
          v-model="queryParams.riskFlag"
          placeholder="请输入风控标记：0正常 1黑名单 2待复核"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道标签" prop="tags">
        <el-input
          v-model="queryParams.tags"
          placeholder="请输入渠道标签"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收款户名" prop="bankAccountName">
        <el-input
          v-model="queryParams.bankAccountName"
          placeholder="请输入收款户名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收款账号" prop="bankAccountNo">
        <el-input
          v-model="queryParams.bankAccountNo"
          placeholder="请输入收款账号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="开户行" prop="bankName">
        <el-input
          v-model="queryParams.bankName"
          placeholder="请输入开户行"
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
          v-hasPermi="['system:jst_channel:add']"
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
          v-hasPermi="['system:jst_channel:edit']"
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
          v-hasPermi="['system:jst_channel:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_channel:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_channelList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="渠道方ID" align="center" prop="channelId" />
      <el-table-column label="关联用户账号ID，FK→jst_user" align="center" prop="userId" />
      <el-table-column label="渠道类型：teacher教师/organization机构/individual个人" align="center" prop="channelType" />
      <el-table-column label="渠道方名称" align="center" prop="channelName" />
      <el-table-column label="联系手机" align="center" prop="contactMobile" />
      <el-table-column label="证件号" align="center" prop="idCardNo" />
      <el-table-column label="营业执照号" align="center" prop="businessLicenseNo" />
      <el-table-column label="认证材料附件JSON：[{type,name,url}]" align="center" prop="certMaterialsJson" />
      <el-table-column label="认证状态：pending待审/approved通过/rejected驳回/suspended暂停" align="center" prop="authStatus" />
      <el-table-column label="认证通过时间" align="center" prop="authTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.authTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核备注" align="center" prop="authRemark" />
      <el-table-column label="当前等级ID，FK→jst_level_config" align="center" prop="currentLevelId" />
      <el-table-column label="启停：0停用 1启用" align="center" prop="status" />
      <el-table-column label="风控标记：0正常 1黑名单 2待复核" align="center" prop="riskFlag" />
      <el-table-column label="渠道标签" align="center" prop="tags" />
      <el-table-column label="收款户名" align="center" prop="bankAccountName" />
      <el-table-column label="收款账号" align="center" prop="bankAccountNo" />
      <el-table-column label="开户行" align="center" prop="bankName" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_channel:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_channel:remove']"
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

    <!-- 添加或修改渠道方档案对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="关联用户账号ID，FK→jst_user" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入关联用户账号ID，FK→jst_user" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="渠道方名称" prop="channelName">
              <el-input v-model="form.channelName" placeholder="请输入渠道方名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="联系手机" prop="contactMobile">
              <el-input v-model="form.contactMobile" placeholder="请输入联系手机" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="证件号" prop="idCardNo">
              <el-input v-model="form.idCardNo" placeholder="请输入证件号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="营业执照号" prop="businessLicenseNo">
              <el-input v-model="form.businessLicenseNo" placeholder="请输入营业执照号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="认证通过时间" prop="authTime">
              <el-date-picker clearable
                v-model="form.authTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择认证通过时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核备注" prop="authRemark">
              <el-input v-model="form.authRemark" placeholder="请输入审核备注" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="当前等级ID，FK→jst_level_config" prop="currentLevelId">
              <el-input v-model="form.currentLevelId" placeholder="请输入当前等级ID，FK→jst_level_config" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="风控标记：0正常 1黑名单 2待复核" prop="riskFlag">
              <el-input v-model="form.riskFlag" placeholder="请输入风控标记：0正常 1黑名单 2待复核" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="渠道标签" prop="tags">
              <el-input v-model="form.tags" placeholder="请输入渠道标签" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="收款户名" prop="bankAccountName">
              <el-input v-model="form.bankAccountName" placeholder="请输入收款户名" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="收款账号" prop="bankAccountNo">
              <el-input v-model="form.bankAccountNo" placeholder="请输入收款账号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="开户行" prop="bankName">
              <el-input v-model="form.bankName" placeholder="请输入开户行" />
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
import { listJst_channel, getJst_channel, delJst_channel, addJst_channel, updateJst_channel } from "@/api/jst/user/jst_channel"

export default {
  name: "Jst_channel",
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
      // 渠道方档案表格数据
      jst_channelList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        channelType: null,
        channelName: null,
        contactMobile: null,
        idCardNo: null,
        businessLicenseNo: null,
        certMaterialsJson: null,
        authStatus: null,
        authTime: null,
        authRemark: null,
        currentLevelId: null,
        status: null,
        riskFlag: null,
        tags: null,
        bankAccountName: null,
        bankAccountNo: null,
        bankName: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "关联用户账号ID，FK→jst_user不能为空", trigger: "blur" }
        ],
        channelType: [
          { required: true, message: "渠道类型：teacher教师/organization机构/individual个人不能为空", trigger: "change" }
        ],
        channelName: [
          { required: true, message: "渠道方名称不能为空", trigger: "blur" }
        ],
        contactMobile: [
          { required: true, message: "联系手机不能为空", trigger: "blur" }
        ],
        authStatus: [
          { required: true, message: "认证状态：pending待审/approved通过/rejected驳回/suspended暂停不能为空", trigger: "change" }
        ],
        status: [
          { required: true, message: "启停：0停用 1启用不能为空", trigger: "change" }
        ],
        riskFlag: [
          { required: true, message: "风控标记：0正常 1黑名单 2待复核不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询渠道方档案列表 */
    getList() {
      this.loading = true
      listJst_channel(this.queryParams).then(response => {
        this.jst_channelList = response.rows
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
        channelId: null,
        userId: null,
        channelType: null,
        channelName: null,
        contactMobile: null,
        idCardNo: null,
        businessLicenseNo: null,
        certMaterialsJson: null,
        authStatus: null,
        authTime: null,
        authRemark: null,
        currentLevelId: null,
        status: null,
        riskFlag: null,
        tags: null,
        bankAccountName: null,
        bankAccountNo: null,
        bankName: null,
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
      this.ids = selection.map(item => item.channelId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加渠道方档案"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const channelId = row.channelId || this.ids
      getJst_channel(channelId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改渠道方档案"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.channelId != null) {
            updateJst_channel(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_channel(this.form).then(response => {
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
      const channelIds = row.channelId || this.ids
      this.$modal.confirm('是否确认删除渠道方档案编号为"' + channelIds + '"的数据项？').then(function() {
        return delJst_channel(channelIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_channel/export', {
        ...this.queryParams
      }, `jst_channel_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
