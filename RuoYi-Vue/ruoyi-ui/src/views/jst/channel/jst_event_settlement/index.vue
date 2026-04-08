<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="结算单号" prop="settlementNo">
        <el-input
          v-model="queryParams.settlementNo"
          placeholder="请输入结算单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="赛事方ID" prop="partnerId">
        <el-input
          v-model="queryParams.partnerId"
          placeholder="请输入赛事方ID"
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
      <el-form-item label="标价合计" prop="totalListAmount">
        <el-input
          v-model="queryParams.totalListAmount"
          placeholder="请输入标价合计"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="优惠券合计" prop="totalCouponAmount">
        <el-input
          v-model="queryParams.totalCouponAmount"
          placeholder="请输入优惠券合计"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="积分抵扣合计" prop="totalPointsAmount">
        <el-input
          v-model="queryParams.totalPointsAmount"
          placeholder="请输入积分抵扣合计"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="平台承担合计" prop="platformBearAmount">
        <el-input
          v-model="queryParams.platformBearAmount"
          placeholder="请输入平台承担合计"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户净实付合计" prop="totalNetPay">
        <el-input
          v-model="queryParams.totalNetPay"
          placeholder="请输入用户净实付合计"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="退款合计" prop="totalRefund">
        <el-input
          v-model="queryParams.totalRefund"
          placeholder="请输入退款合计"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道返点合计" prop="totalRebate">
        <el-input
          v-model="queryParams.totalRebate"
          placeholder="请输入渠道返点合计"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="服务费合计" prop="totalServiceFee">
        <el-input
          v-model="queryParams.totalServiceFee"
          placeholder="请输入服务费合计"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="合同约定扣项" prop="contractDeduction">
        <el-input
          v-model="queryParams.contractDeduction"
          placeholder="请输入合同约定扣项"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最终结算金额" prop="finalAmount">
        <el-input
          v-model="queryParams.finalAmount"
          placeholder="请输入最终结算金额"
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
      <el-form-item label="打款凭证URL" prop="payVoucherUrl">
        <el-input
          v-model="queryParams.payVoucherUrl"
          placeholder="请输入打款凭证URL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="打款时间" prop="payTime">
        <el-date-picker clearable
          v-model="queryParams.payTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择打款时间">
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
          v-hasPermi="['system:jst_event_settlement:add']"
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
          v-hasPermi="['system:jst_event_settlement:edit']"
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
          v-hasPermi="['system:jst_event_settlement:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_event_settlement:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_event_settlementList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="结算单ID" align="center" prop="eventSettlementId" />
      <el-table-column label="结算单号" align="center" prop="settlementNo" />
      <el-table-column label="赛事方ID" align="center" prop="partnerId" />
      <el-table-column label="赛事ID" align="center" prop="contestId" />
      <el-table-column label="标价合计" align="center" prop="totalListAmount" />
      <el-table-column label="优惠券合计" align="center" prop="totalCouponAmount" />
      <el-table-column label="积分抵扣合计" align="center" prop="totalPointsAmount" />
      <el-table-column label="平台承担合计" align="center" prop="platformBearAmount" />
      <el-table-column label="用户净实付合计" align="center" prop="totalNetPay" />
      <el-table-column label="退款合计" align="center" prop="totalRefund" />
      <el-table-column label="渠道返点合计" align="center" prop="totalRebate" />
      <el-table-column label="服务费合计" align="center" prop="totalServiceFee" />
      <el-table-column label="合同约定扣项" align="center" prop="contractDeduction" />
      <el-table-column label="最终结算金额" align="center" prop="finalAmount" />
      <el-table-column label="状态：pending_confirm/reviewing/rejected/pending_pay/paid" align="center" prop="status" />
      <el-table-column label="审核备注" align="center" prop="auditRemark" />
      <el-table-column label="打款凭证URL" align="center" prop="payVoucherUrl" />
      <el-table-column label="打款时间" align="center" prop="payTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.payTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['system:jst_event_settlement:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_event_settlement:remove']"
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

    <!-- 添加或修改赛事方结算单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="结算单号" prop="settlementNo">
              <el-input v-model="form.settlementNo" placeholder="请输入结算单号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="赛事方ID" prop="partnerId">
              <el-input v-model="form.partnerId" placeholder="请输入赛事方ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="赛事ID" prop="contestId">
              <el-input v-model="form.contestId" placeholder="请输入赛事ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="标价合计" prop="totalListAmount">
              <el-input v-model="form.totalListAmount" placeholder="请输入标价合计" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="优惠券合计" prop="totalCouponAmount">
              <el-input v-model="form.totalCouponAmount" placeholder="请输入优惠券合计" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="积分抵扣合计" prop="totalPointsAmount">
              <el-input v-model="form.totalPointsAmount" placeholder="请输入积分抵扣合计" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="平台承担合计" prop="platformBearAmount">
              <el-input v-model="form.platformBearAmount" placeholder="请输入平台承担合计" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="用户净实付合计" prop="totalNetPay">
              <el-input v-model="form.totalNetPay" placeholder="请输入用户净实付合计" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="退款合计" prop="totalRefund">
              <el-input v-model="form.totalRefund" placeholder="请输入退款合计" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="渠道返点合计" prop="totalRebate">
              <el-input v-model="form.totalRebate" placeholder="请输入渠道返点合计" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="服务费合计" prop="totalServiceFee">
              <el-input v-model="form.totalServiceFee" placeholder="请输入服务费合计" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="合同约定扣项" prop="contractDeduction">
              <el-input v-model="form.contractDeduction" placeholder="请输入合同约定扣项" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="最终结算金额" prop="finalAmount">
              <el-input v-model="form.finalAmount" placeholder="请输入最终结算金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核备注" prop="auditRemark">
              <el-input v-model="form.auditRemark" placeholder="请输入审核备注" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="打款凭证URL" prop="payVoucherUrl">
              <el-input v-model="form.payVoucherUrl" placeholder="请输入打款凭证URL" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="打款时间" prop="payTime">
              <el-date-picker clearable
                v-model="form.payTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择打款时间">
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
import { listJst_event_settlement, getJst_event_settlement, delJst_event_settlement, addJst_event_settlement, updateJst_event_settlement } from "@/api/jst/channel/jst_event_settlement"

export default {
  name: "Jst_event_settlement",
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
      // 赛事方结算单表格数据
      jst_event_settlementList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        settlementNo: null,
        partnerId: null,
        contestId: null,
        totalListAmount: null,
        totalCouponAmount: null,
        totalPointsAmount: null,
        platformBearAmount: null,
        totalNetPay: null,
        totalRefund: null,
        totalRebate: null,
        totalServiceFee: null,
        contractDeduction: null,
        finalAmount: null,
        status: null,
        auditRemark: null,
        payVoucherUrl: null,
        payTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        settlementNo: [
          { required: true, message: "结算单号不能为空", trigger: "blur" }
        ],
        partnerId: [
          { required: true, message: "赛事方ID不能为空", trigger: "blur" }
        ],
        contestId: [
          { required: true, message: "赛事ID不能为空", trigger: "blur" }
        ],
        totalListAmount: [
          { required: true, message: "标价合计不能为空", trigger: "blur" }
        ],
        totalCouponAmount: [
          { required: true, message: "优惠券合计不能为空", trigger: "blur" }
        ],
        totalPointsAmount: [
          { required: true, message: "积分抵扣合计不能为空", trigger: "blur" }
        ],
        platformBearAmount: [
          { required: true, message: "平台承担合计不能为空", trigger: "blur" }
        ],
        totalNetPay: [
          { required: true, message: "用户净实付合计不能为空", trigger: "blur" }
        ],
        totalRefund: [
          { required: true, message: "退款合计不能为空", trigger: "blur" }
        ],
        totalRebate: [
          { required: true, message: "渠道返点合计不能为空", trigger: "blur" }
        ],
        totalServiceFee: [
          { required: true, message: "服务费合计不能为空", trigger: "blur" }
        ],
        contractDeduction: [
          { required: true, message: "合同约定扣项不能为空", trigger: "blur" }
        ],
        finalAmount: [
          { required: true, message: "最终结算金额不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态：pending_confirm/reviewing/rejected/pending_pay/paid不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询赛事方结算单列表 */
    getList() {
      this.loading = true
      listJst_event_settlement(this.queryParams).then(response => {
        this.jst_event_settlementList = response.rows
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
        eventSettlementId: null,
        settlementNo: null,
        partnerId: null,
        contestId: null,
        totalListAmount: null,
        totalCouponAmount: null,
        totalPointsAmount: null,
        platformBearAmount: null,
        totalNetPay: null,
        totalRefund: null,
        totalRebate: null,
        totalServiceFee: null,
        contractDeduction: null,
        finalAmount: null,
        status: null,
        auditRemark: null,
        payVoucherUrl: null,
        payTime: null,
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
      this.ids = selection.map(item => item.eventSettlementId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加赛事方结算单"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const eventSettlementId = row.eventSettlementId || this.ids
      getJst_event_settlement(eventSettlementId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改赛事方结算单"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.eventSettlementId != null) {
            updateJst_event_settlement(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_event_settlement(this.form).then(response => {
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
      const eventSettlementIds = row.eventSettlementId || this.ids
      this.$modal.confirm('是否确认删除赛事方结算单编号为"' + eventSettlementIds + '"的数据项？').then(function() {
        return delJst_event_settlement(eventSettlementIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_event_settlement/export', {
        ...this.queryParams
      }, `jst_event_settlement_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
