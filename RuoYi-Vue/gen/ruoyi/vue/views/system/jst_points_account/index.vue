<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="持有者业务ID" prop="ownerId">
        <el-input
          v-model="queryParams.ownerId"
          placeholder="请输入持有者业务ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="可用积分" prop="availablePoints">
        <el-input
          v-model="queryParams.availablePoints"
          placeholder="请输入可用积分"
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
      <el-form-item label="累计获取" prop="totalEarn">
        <el-input
          v-model="queryParams.totalEarn"
          placeholder="请输入累计获取"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="累计消耗" prop="totalSpend">
        <el-input
          v-model="queryParams.totalSpend"
          placeholder="请输入累计消耗"
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
      <el-form-item label="当前等级ID" prop="currentLevelId">
        <el-input
          v-model="queryParams.currentLevelId"
          placeholder="请输入当前等级ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="乐观锁版本号" prop="version">
        <el-input
          v-model="queryParams.version"
          placeholder="请输入乐观锁版本号"
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
          v-hasPermi="['system:jst_points_account:add']"
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
          v-hasPermi="['system:jst_points_account:edit']"
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
          v-hasPermi="['system:jst_points_account:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_points_account:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_points_accountList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="账户ID" align="center" prop="accountId" />
      <el-table-column label="持有者类型：student/channel" align="center" prop="ownerType" />
      <el-table-column label="持有者业务ID" align="center" prop="ownerId" />
      <el-table-column label="可用积分" align="center" prop="availablePoints" />
      <el-table-column label="冻结积分" align="center" prop="frozenPoints" />
      <el-table-column label="累计获取" align="center" prop="totalEarn" />
      <el-table-column label="累计消耗" align="center" prop="totalSpend" />
      <el-table-column label="成长值" align="center" prop="growthValue" />
      <el-table-column label="当前等级ID" align="center" prop="currentLevelId" />
      <el-table-column label="乐观锁版本号" align="center" prop="version" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_points_account:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_points_account:remove']"
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

    <!-- 添加或修改积分账户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="持有者业务ID" prop="ownerId">
              <el-input v-model="form.ownerId" placeholder="请输入持有者业务ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="可用积分" prop="availablePoints">
              <el-input v-model="form.availablePoints" placeholder="请输入可用积分" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="冻结积分" prop="frozenPoints">
              <el-input v-model="form.frozenPoints" placeholder="请输入冻结积分" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="累计获取" prop="totalEarn">
              <el-input v-model="form.totalEarn" placeholder="请输入累计获取" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="累计消耗" prop="totalSpend">
              <el-input v-model="form.totalSpend" placeholder="请输入累计消耗" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="成长值" prop="growthValue">
              <el-input v-model="form.growthValue" placeholder="请输入成长值" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="当前等级ID" prop="currentLevelId">
              <el-input v-model="form.currentLevelId" placeholder="请输入当前等级ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="乐观锁版本号" prop="version">
              <el-input v-model="form.version" placeholder="请输入乐观锁版本号" />
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
import { listJst_points_account, getJst_points_account, delJst_points_account, addJst_points_account, updateJst_points_account } from "@/api/system/jst_points_account"

export default {
  name: "Jst_points_account",
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
      // 积分账户表格数据
      jst_points_accountList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ownerType: null,
        ownerId: null,
        availablePoints: null,
        frozenPoints: null,
        totalEarn: null,
        totalSpend: null,
        growthValue: null,
        currentLevelId: null,
        version: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        ownerType: [
          { required: true, message: "持有者类型：student/channel不能为空", trigger: "change" }
        ],
        ownerId: [
          { required: true, message: "持有者业务ID不能为空", trigger: "blur" }
        ],
        availablePoints: [
          { required: true, message: "可用积分不能为空", trigger: "blur" }
        ],
        frozenPoints: [
          { required: true, message: "冻结积分不能为空", trigger: "blur" }
        ],
        totalEarn: [
          { required: true, message: "累计获取不能为空", trigger: "blur" }
        ],
        totalSpend: [
          { required: true, message: "累计消耗不能为空", trigger: "blur" }
        ],
        growthValue: [
          { required: true, message: "成长值不能为空", trigger: "blur" }
        ],
        version: [
          { required: true, message: "乐观锁版本号不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询积分账户列表 */
    getList() {
      this.loading = true
      listJst_points_account(this.queryParams).then(response => {
        this.jst_points_accountList = response.rows
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
        accountId: null,
        ownerType: null,
        ownerId: null,
        availablePoints: null,
        frozenPoints: null,
        totalEarn: null,
        totalSpend: null,
        growthValue: null,
        currentLevelId: null,
        version: null,
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
      this.ids = selection.map(item => item.accountId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加积分账户"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const accountId = row.accountId || this.ids
      getJst_points_account(accountId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改积分账户"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.accountId != null) {
            updateJst_points_account(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_points_account(this.form).then(response => {
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
      const accountIds = row.accountId || this.ids
      this.$modal.confirm('是否确认删除积分账户编号为"' + accountIds + '"的数据项？').then(function() {
        return delJst_points_account(accountIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_points_account/export', {
        ...this.queryParams
      }, `jst_points_account_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
