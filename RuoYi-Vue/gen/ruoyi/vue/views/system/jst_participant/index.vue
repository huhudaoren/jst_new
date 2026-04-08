<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="关联正式账号ID，允许NULL" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入关联正式账号ID，允许NULL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参赛人姓名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入参赛人姓名"
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
      <el-form-item label="年龄" prop="age">
        <el-input
          v-model="queryParams.age"
          placeholder="请输入年龄"
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
      <el-form-item label="学校" prop="school">
        <el-input
          v-model="queryParams.school"
          placeholder="请输入学校"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属机构" prop="organization">
        <el-input
          v-model="queryParams.organization"
          placeholder="请输入所属机构"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="班级" prop="className">
        <el-input
          v-model="queryParams.className"
          placeholder="请输入班级"
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
      <el-form-item label="创建该档案的渠道方ID，FK→jst_channel" prop="createdByChannelId">
        <el-input
          v-model="queryParams.createdByChannelId"
          placeholder="请输入创建该档案的渠道方ID，FK→jst_channel"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="已认领的正式用户ID" prop="claimedUserId">
        <el-input
          v-model="queryParams.claimedUserId"
          placeholder="请输入已认领的正式用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="认领生效时间" prop="claimedTime">
        <el-date-picker clearable
          v-model="queryParams.claimedTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择认领生效时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="可见范围：channel_only仅创建渠道/platform平台/event_partner赛事方" prop="visibleScope">
        <el-input
          v-model="queryParams.visibleScope"
          placeholder="请输入可见范围：channel_only仅创建渠道/platform平台/event_partner赛事方"
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
          v-hasPermi="['system:jst_participant:add']"
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
          v-hasPermi="['system:jst_participant:edit']"
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
          v-hasPermi="['system:jst_participant:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_participant:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_participantList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="参赛人ID" align="center" prop="participantId" />
      <el-table-column label="关联正式账号ID，允许NULL" align="center" prop="userId" />
      <el-table-column label="档案类型：registered_participant正式 / temporary_participant临时" align="center" prop="participantType" />
      <el-table-column label="参赛人姓名" align="center" prop="name" />
      <el-table-column label="性别：0未知 1男 2女" align="center" prop="gender" />
      <el-table-column label="出生日期" align="center" prop="birthday" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.birthday, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="年龄" align="center" prop="age" />
      <el-table-column label="监护人姓名" align="center" prop="guardianName" />
      <el-table-column label="监护人手机号" align="center" prop="guardianMobile" />
      <el-table-column label="学校" align="center" prop="school" />
      <el-table-column label="所属机构" align="center" prop="organization" />
      <el-table-column label="班级" align="center" prop="className" />
      <el-table-column label="证件类型：id_card身份证/passport护照/..." align="center" prop="idCardType" />
      <el-table-column label="证件号" align="center" prop="idCardNo" />
      <el-table-column label="创建该档案的渠道方ID，FK→jst_channel" align="center" prop="createdByChannelId" />
      <el-table-column label="认领状态：unclaimed未认领/auto_claimed自动认领/manual_claimed人工认领/pending_manual待人工" align="center" prop="claimStatus" />
      <el-table-column label="已认领的正式用户ID" align="center" prop="claimedUserId" />
      <el-table-column label="认领生效时间" align="center" prop="claimedTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.claimedTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="可见范围：channel_only仅创建渠道/platform平台/event_partner赛事方" align="center" prop="visibleScope" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_participant:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_participant:remove']"
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

    <!-- 添加或修改临时参赛档案-允许无正式账号存在对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="关联正式账号ID，允许NULL" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入关联正式账号ID，允许NULL" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="参赛人姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入参赛人姓名" />
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
            <el-form-item label="年龄" prop="age">
              <el-input v-model="form.age" placeholder="请输入年龄" />
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
            <el-form-item label="学校" prop="school">
              <el-input v-model="form.school" placeholder="请输入学校" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="所属机构" prop="organization">
              <el-input v-model="form.organization" placeholder="请输入所属机构" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="班级" prop="className">
              <el-input v-model="form.className" placeholder="请输入班级" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="证件号" prop="idCardNo">
              <el-input v-model="form.idCardNo" placeholder="请输入证件号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="创建该档案的渠道方ID，FK→jst_channel" prop="createdByChannelId">
              <el-input v-model="form.createdByChannelId" placeholder="请输入创建该档案的渠道方ID，FK→jst_channel" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="已认领的正式用户ID" prop="claimedUserId">
              <el-input v-model="form.claimedUserId" placeholder="请输入已认领的正式用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="认领生效时间" prop="claimedTime">
              <el-date-picker clearable
                v-model="form.claimedTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择认领生效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="可见范围：channel_only仅创建渠道/platform平台/event_partner赛事方" prop="visibleScope">
              <el-input v-model="form.visibleScope" placeholder="请输入可见范围：channel_only仅创建渠道/platform平台/event_partner赛事方" />
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
import { listJst_participant, getJst_participant, delJst_participant, addJst_participant, updateJst_participant } from "@/api/system/jst_participant"

export default {
  name: "Jst_participant",
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
      // 临时参赛档案-允许无正式账号存在表格数据
      jst_participantList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        participantType: null,
        name: null,
        gender: null,
        birthday: null,
        age: null,
        guardianName: null,
        guardianMobile: null,
        school: null,
        organization: null,
        className: null,
        idCardType: null,
        idCardNo: null,
        createdByChannelId: null,
        claimStatus: null,
        claimedUserId: null,
        claimedTime: null,
        visibleScope: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        participantType: [
          { required: true, message: "档案类型：registered_participant正式 / temporary_participant临时不能为空", trigger: "change" }
        ],
        name: [
          { required: true, message: "参赛人姓名不能为空", trigger: "blur" }
        ],
        claimStatus: [
          { required: true, message: "认领状态：unclaimed未认领/auto_claimed自动认领/manual_claimed人工认领/pending_manual待人工不能为空", trigger: "change" }
        ],
        visibleScope: [
          { required: true, message: "可见范围：channel_only仅创建渠道/platform平台/event_partner赛事方不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询临时参赛档案-允许无正式账号存在列表 */
    getList() {
      this.loading = true
      listJst_participant(this.queryParams).then(response => {
        this.jst_participantList = response.rows
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
        participantId: null,
        userId: null,
        participantType: null,
        name: null,
        gender: null,
        birthday: null,
        age: null,
        guardianName: null,
        guardianMobile: null,
        school: null,
        organization: null,
        className: null,
        idCardType: null,
        idCardNo: null,
        createdByChannelId: null,
        claimStatus: null,
        claimedUserId: null,
        claimedTime: null,
        visibleScope: null,
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
      this.ids = selection.map(item => item.participantId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加临时参赛档案-允许无正式账号存在"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const participantId = row.participantId || this.ids
      getJst_participant(participantId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改临时参赛档案-允许无正式账号存在"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.participantId != null) {
            updateJst_participant(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_participant(this.form).then(response => {
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
      const participantIds = row.participantId || this.ids
      this.$modal.confirm('是否确认删除临时参赛档案-允许无正式账号存在编号为"' + participantIds + '"的数据项？').then(function() {
        return delJst_participant(participantIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_participant/export', {
        ...this.queryParams
      }, `jst_participant_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
