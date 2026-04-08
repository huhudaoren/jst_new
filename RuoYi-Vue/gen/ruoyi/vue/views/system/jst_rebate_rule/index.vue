<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="赛事ID" prop="contestId">
        <el-input
          v-model="queryParams.contestId"
          placeholder="请输入赛事ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道方ID" prop="channelId">
        <el-input
          v-model="queryParams.channelId"
          placeholder="请输入渠道方ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="返点模式：rate按比例 / fixed固定金额" prop="rebateMode">
        <el-input
          v-model="queryParams.rebateMode"
          placeholder="请输入返点模式：rate按比例 / fixed固定金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="返点值" prop="rebateValue">
        <el-input
          v-model="queryParams.rebateValue"
          placeholder="请输入返点值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="服务费模式：fixed/rate/none" prop="serviceFeeMode">
        <el-input
          v-model="queryParams.serviceFeeMode"
          placeholder="请输入服务费模式：fixed/rate/none"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="服务费值" prop="serviceFeeValue">
        <el-input
          v-model="queryParams.serviceFeeValue"
          placeholder="请输入服务费值"
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
          v-hasPermi="['system:jst_rebate_rule:add']"
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
          v-hasPermi="['system:jst_rebate_rule:edit']"
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
          v-hasPermi="['system:jst_rebate_rule:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_rebate_rule:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_rebate_ruleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="规则ID" align="center" prop="ruleId" />
      <el-table-column label="赛事ID" align="center" prop="contestId" />
      <el-table-column label="渠道方ID" align="center" prop="channelId" />
      <el-table-column label="返点模式：rate按比例 / fixed固定金额" align="center" prop="rebateMode" />
      <el-table-column label="返点值" align="center" prop="rebateValue" />
      <el-table-column label="服务费模式：fixed/rate/none" align="center" prop="serviceFeeMode" />
      <el-table-column label="服务费值" align="center" prop="serviceFeeValue" />
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
      <el-table-column label="启停：0停 1启" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_rebate_rule:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_rebate_rule:remove']"
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

    <!-- 添加或修改渠道返点规则对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="赛事ID" prop="contestId">
              <el-input v-model="form.contestId" placeholder="请输入赛事ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="渠道方ID" prop="channelId">
              <el-input v-model="form.channelId" placeholder="请输入渠道方ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返点模式：rate按比例 / fixed固定金额" prop="rebateMode">
              <el-input v-model="form.rebateMode" placeholder="请输入返点模式：rate按比例 / fixed固定金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返点值" prop="rebateValue">
              <el-input v-model="form.rebateValue" placeholder="请输入返点值" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="服务费模式：fixed/rate/none" prop="serviceFeeMode">
              <el-input v-model="form.serviceFeeMode" placeholder="请输入服务费模式：fixed/rate/none" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="服务费值" prop="serviceFeeValue">
              <el-input v-model="form.serviceFeeValue" placeholder="请输入服务费值" />
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
import { listJst_rebate_rule, getJst_rebate_rule, delJst_rebate_rule, addJst_rebate_rule, updateJst_rebate_rule } from "@/api/system/jst_rebate_rule"

export default {
  name: "Jst_rebate_rule",
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
      // 渠道返点规则表格数据
      jst_rebate_ruleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestId: null,
        channelId: null,
        rebateMode: null,
        rebateValue: null,
        serviceFeeMode: null,
        serviceFeeValue: null,
        effectiveTime: null,
        expireTime: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        contestId: [
          { required: true, message: "赛事ID不能为空", trigger: "blur" }
        ],
        rebateMode: [
          { required: true, message: "返点模式：rate按比例 / fixed固定金额不能为空", trigger: "blur" }
        ],
        rebateValue: [
          { required: true, message: "返点值不能为空", trigger: "blur" }
        ],
        serviceFeeMode: [
          { required: true, message: "服务费模式：fixed/rate/none不能为空", trigger: "blur" }
        ],
        serviceFeeValue: [
          { required: true, message: "服务费值不能为空", trigger: "blur" }
        ],
        effectiveTime: [
          { required: true, message: "生效时间不能为空", trigger: "blur" }
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
    /** 查询渠道返点规则列表 */
    getList() {
      this.loading = true
      listJst_rebate_rule(this.queryParams).then(response => {
        this.jst_rebate_ruleList = response.rows
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
        contestId: null,
        channelId: null,
        rebateMode: null,
        rebateValue: null,
        serviceFeeMode: null,
        serviceFeeValue: null,
        effectiveTime: null,
        expireTime: null,
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
      this.title = "添加渠道返点规则"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const ruleId = row.ruleId || this.ids
      getJst_rebate_rule(ruleId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改渠道返点规则"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.ruleId != null) {
            updateJst_rebate_rule(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_rebate_rule(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除渠道返点规则编号为"' + ruleIds + '"的数据项？').then(function() {
        return delJst_rebate_rule(ruleIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_rebate_rule/export', {
        ...this.queryParams
      }, `jst_rebate_rule_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
