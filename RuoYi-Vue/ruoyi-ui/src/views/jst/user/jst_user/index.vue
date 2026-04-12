<template>
  <div class="app-container">
    <el-alert
      title="此页面已废弃"
      description="用户管理功能请使用「用户管理 > 用户账户」页面（user/index.vue），本页面为代码生成器产物，将在后续版本移除。"
      type="warning"
      :closable="false"
      show-icon
      style="margin-bottom: 16px"
    />
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="微信小程序 openid" prop="openid">
        <el-input
          v-model="queryParams.openid"
          placeholder="请输入微信小程序 openid"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="微信开放平台 unionid" prop="unionid">
        <el-input
          v-model="queryParams.unionid"
          placeholder="请输入微信开放平台 unionid"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input
          v-model="queryParams.mobile"
          placeholder="请输入手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input
          v-model="queryParams.nickname"
          placeholder="请输入昵称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="头像URL" prop="avatar">
        <el-input
          v-model="queryParams.avatar"
          placeholder="请输入头像URL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="真实姓名" prop="realName">
        <el-input
          v-model="queryParams.realName"
          placeholder="请输入真实姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="性别：0未知 1男 2女" prop="gender">
        <el-input
          v-model="queryParams.gender"
          placeholder="请输入性别：0未知 1男 2女"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="出生日期" prop="birthday">
        <el-date-picker clearable
          v-model="queryParams.birthday"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择出生日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="证件号" prop="idCardNo">
        <el-input
          v-model="queryParams.idCardNo"
          placeholder="请输入证件号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="监护人姓名" prop="guardianName">
        <el-input
          v-model="queryParams.guardianName"
          placeholder="请输入监护人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="监护人手机号" prop="guardianMobile">
        <el-input
          v-model="queryParams.guardianMobile"
          placeholder="请输入监护人手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="当前等级ID，FK→jst_level_config.level_id" prop="currentLevelId">
        <el-input
          v-model="queryParams.currentLevelId"
          placeholder="请输入当前等级ID，FK→jst_level_config.level_id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="累计获得积分" prop="totalPoints">
        <el-input
          v-model="queryParams.totalPoints"
          placeholder="请输入累计获得积分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="当前可用积分余额" prop="availablePoints">
        <el-input
          v-model="queryParams.availablePoints"
          placeholder="请输入当前可用积分余额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="冻结积分" prop="frozenPoints">
        <el-input
          v-model="queryParams.frozenPoints"
          placeholder="请输入冻结积分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="成长值" prop="growthValue">
        <el-input
          v-model="queryParams.growthValue"
          placeholder="请输入成长值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="当前有效绑定渠道方ID，FK→jst_channel.channel_id" prop="boundChannelId">
        <el-input
          v-model="queryParams.boundChannelId"
          placeholder="请输入当前有效绑定渠道方ID，FK→jst_channel.channel_id"
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
      <el-form-item label="注册时间" prop="registerTime">
        <el-date-picker clearable
          v-model="queryParams.registerTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择注册时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="最后登录时间" prop="lastLoginTime">
        <el-date-picker clearable
          v-model="queryParams.lastLoginTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择最后登录时间">
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
          v-hasPermi="['system:jst_user:add']"
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
          v-hasPermi="['system:jst_user:edit']"
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
          v-hasPermi="['system:jst_user:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_user:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_userList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="微信小程序 openid" align="center" prop="openid" />
      <el-table-column label="微信开放平台 unionid" align="center" prop="unionid" />
      <el-table-column label="手机号" align="center" prop="mobile" />
      <el-table-column label="昵称" align="center" prop="nickname" />
      <el-table-column label="头像URL" align="center" prop="avatar" />
      <el-table-column label="真实姓名" align="center" prop="realName" />
      <el-table-column label="性别：0未知 1男 2女" align="center" prop="gender" />
      <el-table-column label="出生日期" align="center" prop="birthday" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.birthday, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="证件号" align="center" prop="idCardNo" />
      <el-table-column label="监护人姓名" align="center" prop="guardianName" />
      <el-table-column label="监护人手机号" align="center" prop="guardianMobile" />
      <el-table-column label="用户类型：student学生 / parent家长 / channel渠道方" align="center" prop="userType" />
      <el-table-column label="当前等级ID，FK→jst_level_config.level_id" align="center" prop="currentLevelId" />
      <el-table-column label="累计获得积分" align="center" prop="totalPoints" />
      <el-table-column label="当前可用积分余额" align="center" prop="availablePoints" />
      <el-table-column label="冻结积分" align="center" prop="frozenPoints" />
      <el-table-column label="成长值" align="center" prop="growthValue" />
      <el-table-column label="当前有效绑定渠道方ID，FK→jst_channel.channel_id" align="center" prop="boundChannelId" />
      <el-table-column label="账号状态：0禁用 1正常 2封禁" align="center" prop="status" />
      <el-table-column label="风控标记：0正常 1黑名单 2待复核" align="center" prop="riskFlag" />
      <el-table-column label="注册时间" align="center" prop="registerTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.registerTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最后登录时间" align="center" prop="lastLoginTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastLoginTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['system:jst_user:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_user:remove']"
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

    <!-- 添加或修改用户主-学生/家长/渠道方业务账号对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="微信小程序 openid" prop="openid">
              <el-input v-model="form.openid" placeholder="请输入微信小程序 openid" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="微信开放平台 unionid" prop="unionid">
              <el-input v-model="form.unionid" placeholder="请输入微信开放平台 unionid" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="手机号" prop="mobile">
              <el-input v-model="form.mobile" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="头像URL" prop="avatar">
              <el-input v-model="form.avatar" placeholder="请输入头像URL" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="性别：0未知 1男 2女" prop="gender">
              <el-input v-model="form.gender" placeholder="请输入性别：0未知 1男 2女" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker clearable
                v-model="form.birthday"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择出生日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="证件号" prop="idCardNo">
              <el-input v-model="form.idCardNo" placeholder="请输入证件号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="监护人姓名" prop="guardianName">
              <el-input v-model="form.guardianName" placeholder="请输入监护人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="监护人手机号" prop="guardianMobile">
              <el-input v-model="form.guardianMobile" placeholder="请输入监护人手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="当前等级ID，FK→jst_level_config.level_id" prop="currentLevelId">
              <el-input v-model="form.currentLevelId" placeholder="请输入当前等级ID，FK→jst_level_config.level_id" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="累计获得积分" prop="totalPoints">
              <el-input v-model="form.totalPoints" placeholder="请输入累计获得积分" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="当前可用积分余额" prop="availablePoints">
              <el-input v-model="form.availablePoints" placeholder="请输入当前可用积分余额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="冻结积分" prop="frozenPoints">
              <el-input v-model="form.frozenPoints" placeholder="请输入冻结积分" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="成长值" prop="growthValue">
              <el-input v-model="form.growthValue" placeholder="请输入成长值" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="当前有效绑定渠道方ID，FK→jst_channel.channel_id" prop="boundChannelId">
              <el-input v-model="form.boundChannelId" placeholder="请输入当前有效绑定渠道方ID，FK→jst_channel.channel_id" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="风控标记：0正常 1黑名单 2待复核" prop="riskFlag">
              <el-input v-model="form.riskFlag" placeholder="请输入风控标记：0正常 1黑名单 2待复核" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="注册时间" prop="registerTime">
              <el-date-picker clearable
                v-model="form.registerTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择注册时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="最后登录时间" prop="lastLoginTime">
              <el-date-picker clearable
                v-model="form.lastLoginTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择最后登录时间">
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
import { listJst_user, getJst_user, delJst_user, addJst_user, updateJst_user } from "@/api/jst/user/jst_user"

export default {
  name: "Jst_user",
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
      // 用户主-学生/家长/渠道方业务账号表格数据
      jst_userList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        openid: null,
        unionid: null,
        mobile: null,
        nickname: null,
        avatar: null,
        realName: null,
        gender: null,
        birthday: null,
        idCardNo: null,
        guardianName: null,
        guardianMobile: null,
        userType: null,
        currentLevelId: null,
        totalPoints: null,
        availablePoints: null,
        frozenPoints: null,
        growthValue: null,
        boundChannelId: null,
        status: null,
        riskFlag: null,
        registerTime: null,
        lastLoginTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userType: [
          { required: true, message: "用户类型：student学生 / parent家长 / channel渠道方不能为空", trigger: "change" }
        ],
        totalPoints: [
          { required: true, message: "累计获得积分不能为空", trigger: "blur" }
        ],
        availablePoints: [
          { required: true, message: "当前可用积分余额不能为空", trigger: "blur" }
        ],
        frozenPoints: [
          { required: true, message: "冻结积分不能为空", trigger: "blur" }
        ],
        growthValue: [
          { required: true, message: "成长值不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "账号状态：0禁用 1正常 2封禁不能为空", trigger: "change" }
        ],
        riskFlag: [
          { required: true, message: "风控标记：0正常 1黑名单 2待复核不能为空", trigger: "blur" }
        ],
        registerTime: [
          { required: true, message: "注册时间不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询用户主-学生/家长/渠道方业务账号列表 */
    getList() {
      this.loading = true
      listJst_user(this.queryParams).then(response => {
        this.jst_userList = response.rows
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
        userId: null,
        openid: null,
        unionid: null,
        mobile: null,
        nickname: null,
        avatar: null,
        realName: null,
        gender: null,
        birthday: null,
        idCardNo: null,
        guardianName: null,
        guardianMobile: null,
        userType: null,
        currentLevelId: null,
        totalPoints: null,
        availablePoints: null,
        frozenPoints: null,
        growthValue: null,
        boundChannelId: null,
        status: null,
        riskFlag: null,
        registerTime: null,
        lastLoginTime: null,
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
      this.ids = selection.map(item => item.userId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加用户主-学生/家长/渠道方业务账号"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const userId = row.userId || this.ids
      getJst_user(userId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改用户主-学生/家长/渠道方业务账号"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.userId != null) {
            updateJst_user(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_user(this.form).then(response => {
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
      const userIds = row.userId || this.ids
      this.$modal.confirm('是否确认删除用户主-学生/家长/渠道方业务账号编号为"' + userIds + '"的数据项？').then(function() {
        return delJst_user(userIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_user/export', {
        ...this.queryParams
      }, `jst_user_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
