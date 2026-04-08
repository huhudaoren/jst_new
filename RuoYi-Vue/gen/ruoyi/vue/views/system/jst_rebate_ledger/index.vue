<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单ID" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入订单ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单明细ID" prop="itemId">
        <el-input
          v-model="queryParams.itemId"
          placeholder="请输入订单明细ID"
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
      <el-form-item label="赛事ID" prop="contestId">
        <el-input
          v-model="queryParams.contestId"
          placeholder="请输入赛事ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="命中规则ID" prop="ruleId">
        <el-input
          v-model="queryParams.ruleId"
          placeholder="请输入命中规则ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="标价金额" prop="listAmount">
        <el-input
          v-model="queryParams.listAmount"
          placeholder="请输入标价金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="净实付金额" prop="netPayAmount">
        <el-input
          v-model="queryParams.netPayAmount"
          placeholder="请输入净实付金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="服务费" prop="serviceFee">
        <el-input
          v-model="queryParams.serviceFee"
          placeholder="请输入服务费"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="返点基数 = max(0, 标价 - 服务费)" prop="rebateBase">
        <el-input
          v-model="queryParams.rebateBase"
          placeholder="请输入返点基数 = max(0, 标价 - 服务费)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="返点金额" prop="rebateAmount">
        <el-input
          v-model="queryParams.rebateAmount"
          placeholder="请输入返点金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="方向：positive正向 / negative负向" prop="direction">
        <el-input
          v-model="queryParams.direction"
          placeholder="请输入方向：positive正向 / negative负向"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="计提时间" prop="accrualTime">
        <el-date-picker clearable
          v-model="queryParams.accrualTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择计提时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="赛事结束时间快照" prop="eventEndTime">
        <el-date-picker clearable
          v-model="queryParams.eventEndTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择赛事结束时间快照">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="关联结算单ID" prop="settlementId">
        <el-input
          v-model="queryParams.settlementId"
          placeholder="请输入关联结算单ID"
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
          v-hasPermi="['system:jst_rebate_ledger:add']"
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
          v-hasPermi="['system:jst_rebate_ledger:edit']"
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
          v-hasPermi="['system:jst_rebate_ledger:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_rebate_ledger:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_rebate_ledgerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="台账ID" align="center" prop="ledgerId" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="订单明细ID" align="center" prop="itemId" />
      <el-table-column label="渠道方ID" align="center" prop="channelId" />
      <el-table-column label="赛事ID" align="center" prop="contestId" />
      <el-table-column label="命中规则ID" align="center" prop="ruleId" />
      <el-table-column label="标价金额" align="center" prop="listAmount" />
      <el-table-column label="净实付金额" align="center" prop="netPayAmount" />
      <el-table-column label="服务费" align="center" prop="serviceFee" />
      <el-table-column label="返点基数 = max(0, 标价 - 服务费)" align="center" prop="rebateBase" />
      <el-table-column label="返点金额" align="center" prop="rebateAmount" />
      <el-table-column label="方向：positive正向 / negative负向" align="center" prop="direction" />
      <el-table-column label="台账状态：pending_accrual/withdrawable/in_review/paid/rolled_back/negative" align="center" prop="status" />
      <el-table-column label="计提时间" align="center" prop="accrualTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.accrualTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="赛事结束时间快照" align="center" prop="eventEndTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.eventEndTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="关联结算单ID" align="center" prop="settlementId" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_rebate_ledger:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_rebate_ledger:remove']"
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

    <!-- 添加或修改返点计提台账对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="订单ID" prop="orderId">
              <el-input v-model="form.orderId" placeholder="请输入订单ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="订单明细ID" prop="itemId">
              <el-input v-model="form.itemId" placeholder="请输入订单明细ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="渠道方ID" prop="channelId">
              <el-input v-model="form.channelId" placeholder="请输入渠道方ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="赛事ID" prop="contestId">
              <el-input v-model="form.contestId" placeholder="请输入赛事ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="命中规则ID" prop="ruleId">
              <el-input v-model="form.ruleId" placeholder="请输入命中规则ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="标价金额" prop="listAmount">
              <el-input v-model="form.listAmount" placeholder="请输入标价金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="净实付金额" prop="netPayAmount">
              <el-input v-model="form.netPayAmount" placeholder="请输入净实付金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="服务费" prop="serviceFee">
              <el-input v-model="form.serviceFee" placeholder="请输入服务费" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返点基数 = max(0, 标价 - 服务费)" prop="rebateBase">
              <el-input v-model="form.rebateBase" placeholder="请输入返点基数 = max(0, 标价 - 服务费)" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="返点金额" prop="rebateAmount">
              <el-input v-model="form.rebateAmount" placeholder="请输入返点金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="方向：positive正向 / negative负向" prop="direction">
              <el-input v-model="form.direction" placeholder="请输入方向：positive正向 / negative负向" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="计提时间" prop="accrualTime">
              <el-date-picker clearable
                v-model="form.accrualTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择计提时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="赛事结束时间快照" prop="eventEndTime">
              <el-date-picker clearable
                v-model="form.eventEndTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择赛事结束时间快照">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联结算单ID" prop="settlementId">
              <el-input v-model="form.settlementId" placeholder="请输入关联结算单ID" />
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
import { listJst_rebate_ledger, getJst_rebate_ledger, delJst_rebate_ledger, addJst_rebate_ledger, updateJst_rebate_ledger } from "@/api/system/jst_rebate_ledger"

export default {
  name: "Jst_rebate_ledger",
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
      // 返点计提台账表格数据
      jst_rebate_ledgerList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderId: null,
        itemId: null,
        channelId: null,
        contestId: null,
        ruleId: null,
        listAmount: null,
        netPayAmount: null,
        serviceFee: null,
        rebateBase: null,
        rebateAmount: null,
        direction: null,
        status: null,
        accrualTime: null,
        eventEndTime: null,
        settlementId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        orderId: [
          { required: true, message: "订单ID不能为空", trigger: "blur" }
        ],
        itemId: [
          { required: true, message: "订单明细ID不能为空", trigger: "blur" }
        ],
        channelId: [
          { required: true, message: "渠道方ID不能为空", trigger: "blur" }
        ],
        contestId: [
          { required: true, message: "赛事ID不能为空", trigger: "blur" }
        ],
        listAmount: [
          { required: true, message: "标价金额不能为空", trigger: "blur" }
        ],
        netPayAmount: [
          { required: true, message: "净实付金额不能为空", trigger: "blur" }
        ],
        serviceFee: [
          { required: true, message: "服务费不能为空", trigger: "blur" }
        ],
        rebateBase: [
          { required: true, message: "返点基数 = max(0, 标价 - 服务费)不能为空", trigger: "blur" }
        ],
        rebateAmount: [
          { required: true, message: "返点金额不能为空", trigger: "blur" }
        ],
        direction: [
          { required: true, message: "方向：positive正向 / negative负向不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "台账状态：pending_accrual/withdrawable/in_review/paid/rolled_back/negative不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询返点计提台账列表 */
    getList() {
      this.loading = true
      listJst_rebate_ledger(this.queryParams).then(response => {
        this.jst_rebate_ledgerList = response.rows
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
        ledgerId: null,
        orderId: null,
        itemId: null,
        channelId: null,
        contestId: null,
        ruleId: null,
        listAmount: null,
        netPayAmount: null,
        serviceFee: null,
        rebateBase: null,
        rebateAmount: null,
        direction: null,
        status: null,
        accrualTime: null,
        eventEndTime: null,
        settlementId: null,
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
      this.ids = selection.map(item => item.ledgerId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加返点计提台账"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const ledgerId = row.ledgerId || this.ids
      getJst_rebate_ledger(ledgerId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改返点计提台账"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.ledgerId != null) {
            updateJst_rebate_ledger(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_rebate_ledger(this.form).then(response => {
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
      const ledgerIds = row.ledgerId || this.ids
      this.$modal.confirm('是否确认删除返点计提台账编号为"' + ledgerIds + '"的数据项？').then(function() {
        return delJst_rebate_ledger(ledgerIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_rebate_ledger/export', {
        ...this.queryParams
      }, `jst_rebate_ledger_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
