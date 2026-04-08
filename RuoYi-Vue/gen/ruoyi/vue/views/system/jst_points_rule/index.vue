<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模式：fixed/rate" prop="rewardMode">
        <el-input
          v-model="queryParams.rewardMode"
          placeholder="请输入模式：fixed/rate"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="数值" prop="rewardValue">
        <el-input
          v-model="queryParams.rewardValue"
          placeholder="请输入数值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="适用角色：student/channel/both" prop="applicableRole">
        <el-input
          v-model="queryParams.applicableRole"
          placeholder="请输入适用角色：student/channel/both"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="生效时间" prop="effectiveTime">
        <el-date-picker clearable
          v-model="queryParams.effectiveTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择生效时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="失效时间" prop="expireTime">
        <el-date-picker clearable
          v-model="queryParams.expireTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择失效时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="互斥组" prop="mutexGroup">
        <el-input
          v-model="queryParams.mutexGroup"
          placeholder="请输入互斥组"
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
          v-hasPermi="['system:jst_points_rule:add']"
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
          v-hasPermi="['system:jst_points_rule:edit']"
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
          v-hasPermi="['system:jst_points_rule:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_points_rule:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_points_ruleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="规则ID" align="center" prop="ruleId" />
      <el-table-column label="类型：points积分 / growth成长值" align="center" prop="ruleType" />
      <el-table-column label="来源行为：enroll/course/sign/invite/learn/award" align="center" prop="sourceType" />
      <el-table-column label="模式：fixed/rate" align="center" prop="rewardMode" />
      <el-table-column label="数值" align="center" prop="rewardValue" />
      <el-table-column label="适用角色：student/channel/both" align="center" prop="applicableRole" />
      <el-table-column label="生效时间" align="center" prop="effectiveTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.effectiveTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="失效时间" align="center" prop="expireTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.expireTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="互斥组" align="center" prop="mutexGroup" />
      <el-table-column label="启停：0停 1启" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_points_rule:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_points_rule:remove']"
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

    <!-- 添加或修改积分/成长值规则对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="模式：fixed/rate" prop="rewardMode">
              <el-input v-model="form.rewardMode" placeholder="请输入模式：fixed/rate" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="数值" prop="rewardValue">
              <el-input v-model="form.rewardValue" placeholder="请输入数值" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="适用角色：student/channel/both" prop="applicableRole">
              <el-input v-model="form.applicableRole" placeholder="请输入适用角色：student/channel/both" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="生效时间" prop="effectiveTime">
              <el-date-picker clearable
                v-model="form.effectiveTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择生效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="失效时间" prop="expireTime">
              <el-date-picker clearable
                v-model="form.expireTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择失效时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="互斥组" prop="mutexGroup">
              <el-input v-model="form.mutexGroup" placeholder="请输入互斥组" />
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
import { listJst_points_rule, getJst_points_rule, delJst_points_rule, addJst_points_rule, updateJst_points_rule } from "@/api/system/jst_points_rule"

export default {
  name: "Jst_points_rule",
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
      // 积分/成长值规则表格数据
      jst_points_ruleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ruleType: null,
        sourceType: null,
        rewardMode: null,
        rewardValue: null,
        applicableRole: null,
        effectiveTime: null,
        expireTime: null,
        mutexGroup: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        ruleType: [
          { required: true, message: "类型：points积分 / growth成长值不能为空", trigger: "change" }
        ],
        sourceType: [
          { required: true, message: "来源行为：enroll/course/sign/invite/learn/award不能为空", trigger: "change" }
        ],
        rewardMode: [
          { required: true, message: "模式：fixed/rate不能为空", trigger: "blur" }
        ],
        rewardValue: [
          { required: true, message: "数值不能为空", trigger: "blur" }
        ],
        applicableRole: [
          { required: true, message: "适用角色：student/channel/both不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "启停：0停 1启不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询积分/成长值规则列表 */
    getList() {
      this.loading = true
      listJst_points_rule(this.queryParams).then(response => {
        this.jst_points_ruleList = response.rows
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
        ruleId: null,
        ruleType: null,
        sourceType: null,
        rewardMode: null,
        rewardValue: null,
        applicableRole: null,
        effectiveTime: null,
        expireTime: null,
        mutexGroup: null,
        status: null,
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
      this.ids = selection.map(item => item.ruleId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加积分/成长值规则"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const ruleId = row.ruleId || this.ids
      getJst_points_rule(ruleId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改积分/成长值规则"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.ruleId != null) {
            updateJst_points_rule(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_points_rule(this.form).then(response => {
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
      const ruleIds = row.ruleId || this.ids
      this.$modal.confirm('是否确认删除积分/成长值规则编号为"' + ruleIds + '"的数据项？').then(function() {
        return delJst_points_rule(ruleIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_points_rule/export', {
        ...this.queryParams
      }, `jst_points_rule_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
