<template>
  <!-- DEPRECATED -->
  <div class="app-container">
    <el-alert
      title="此页面已废弃"
      type="warning"
      :closable="false"
      show-icon
      style="margin-bottom: 16px"
    >
      <template slot="default">
        请使用精品页继续处理订单管理。
        <el-button type="text" @click="$router.push('/jst/order/admin-order')">前往新页面</el-button>
      </template>
    </el-alert>
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学生用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入学生用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参赛人ID快照" prop="participantId">
        <el-input
          v-model="queryParams.participantId"
          placeholder="请输入参赛人ID快照"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付时绑定的渠道方ID" prop="channelId">
        <el-input
          v-model="queryParams.channelId"
          placeholder="请输入支付时绑定的渠道方ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联赛事ID" prop="contestId">
        <el-input
          v-model="queryParams.contestId"
          placeholder="请输入关联赛事ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联赛事方ID" prop="partnerId">
        <el-input
          v-model="queryParams.partnerId"
          placeholder="请输入关联赛事方ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联团队预约主记录ID" prop="teamAppointmentId">
        <el-input
          v-model="queryParams.teamAppointmentId"
          placeholder="请输入关联团队预约主记录ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单标价金额" prop="listAmount">
        <el-input
          v-model="queryParams.listAmount"
          placeholder="请输入订单标价金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="优惠券抵扣金额" prop="couponAmount">
        <el-input
          v-model="queryParams.couponAmount"
          placeholder="请输入优惠券抵扣金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="积分抵扣折现金额" prop="pointsDeductAmount">
        <el-input
          v-model="queryParams.pointsDeductAmount"
          placeholder="请输入积分抵扣折现金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="使用积分数" prop="pointsUsed">
        <el-input
          v-model="queryParams.pointsUsed"
          placeholder="请输入使用积分数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="平台承担的优惠金额" prop="platformBearAmount">
        <el-input
          v-model="queryParams.platformBearAmount"
          placeholder="请输入平台承担的优惠金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户净实付金额" prop="netPayAmount">
        <el-input
          v-model="queryParams.netPayAmount"
          placeholder="请输入用户净实付金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="平台服务费" prop="serviceFee">
        <el-input
          v-model="queryParams.serviceFee"
          placeholder="请输入平台服务费"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付方式：wechat/bank_transfer/points/points_cash_mix" prop="payMethod">
        <el-input
          v-model="queryParams.payMethod"
          placeholder="请输入支付方式：wechat/bank_transfer/points/points_cash_mix"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付发起方：self本人/channel渠道代付" prop="payInitiator">
        <el-input
          v-model="queryParams.payInitiator"
          placeholder="请输入支付发起方：self本人/channel渠道代付"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发起方ID" prop="payInitiatorId">
        <el-input
          v-model="queryParams.payInitiatorId"
          placeholder="请输入发起方ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付完成时间" prop="payTime">
        <el-date-picker clearable
          v-model="queryParams.payTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择支付完成时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="售后截止时间 = max(赛事结束,成绩发布) + N天" prop="aftersaleDeadline">
        <el-date-picker clearable
          v-model="queryParams.aftersaleDeadline"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择售后截止时间 = max(赛事结束,成绩发布) + N天">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="使用的用户券ID" prop="couponId">
        <el-input
          v-model="queryParams.couponId"
          placeholder="请输入使用的用户券ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否允许用户自助退款：0否 1是" prop="allowSelfRefund">
        <el-input
          v-model="queryParams.allowSelfRefund"
          placeholder="请输入是否允许用户自助退款：0否 1是"
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
          v-hasPermi="['system:jst_order_main:add']"
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
          v-hasPermi="['system:jst_order_main:edit']"
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
          v-hasPermi="['system:jst_order_main:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_order_main:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_order_mainList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="订单号" align="center" prop="orderNo" />
      <el-table-column label="订单类型：normal/zero_price零元/full_deduct全额抵扣/exchange兑换/appointment预约" align="center" prop="orderType" />
      <el-table-column label="业务类型：enroll报名/course课程/mall商城/appointment预约" align="center" prop="businessType" />
      <el-table-column label="学生用户ID" align="center" prop="userId" />
      <el-table-column label="参赛人ID快照" align="center" prop="participantId" />
      <el-table-column label="支付时绑定的渠道方ID" align="center" prop="channelId" />
      <el-table-column label="关联赛事ID" align="center" prop="contestId" />
      <el-table-column label="关联赛事方ID" align="center" prop="partnerId" />
      <el-table-column label="关联团队预约主记录ID" align="center" prop="teamAppointmentId" />
      <el-table-column label="订单标价金额" align="center" prop="listAmount" />
      <el-table-column label="优惠券抵扣金额" align="center" prop="couponAmount" />
      <el-table-column label="积分抵扣折现金额" align="center" prop="pointsDeductAmount" />
      <el-table-column label="使用积分数" align="center" prop="pointsUsed" />
      <el-table-column label="平台承担的优惠金额" align="center" prop="platformBearAmount" />
      <el-table-column label="用户净实付金额" align="center" prop="netPayAmount" />
      <el-table-column label="平台服务费" align="center" prop="serviceFee" />
      <el-table-column label="支付方式：wechat/bank_transfer/points/points_cash_mix" align="center" prop="payMethod" />
      <el-table-column label="支付发起方：self本人/channel渠道代付" align="center" prop="payInitiator" />
      <el-table-column label="发起方ID" align="center" prop="payInitiatorId" />
      <el-table-column label="支付完成时间" align="center" prop="payTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.payTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" align="center" prop="orderStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.jst_order_status" :value="scope.row.orderStatus" />
        </template>
      </el-table-column>
      <el-table-column label="退款状态" align="center" prop="refundStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.jst_refund_status" :value="scope.row.refundStatus" />
        </template>
      </el-table-column>
      <el-table-column label="售后截止时间 = max(赛事结束,成绩发布) + N天" align="center" prop="aftersaleDeadline" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.aftersaleDeadline, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="使用的用户券ID" align="center" prop="couponId" />
      <el-table-column label="是否允许用户自助退款：0否 1是" align="center" prop="allowSelfRefund" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_order_main:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_order_main:remove']"
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

    <!-- 添加或修改订单主对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="订单号" prop="orderNo">
              <el-input v-model="form.orderNo" placeholder="请输入订单号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="学生用户ID" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入学生用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="参赛人ID快照" prop="participantId">
              <el-input v-model="form.participantId" placeholder="请输入参赛人ID快照" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="支付时绑定的渠道方ID" prop="channelId">
              <el-input v-model="form.channelId" placeholder="请输入支付时绑定的渠道方ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联赛事ID" prop="contestId">
              <el-input v-model="form.contestId" placeholder="请输入关联赛事ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联赛事方ID" prop="partnerId">
              <el-input v-model="form.partnerId" placeholder="请输入关联赛事方ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联团队预约主记录ID" prop="teamAppointmentId">
              <el-input v-model="form.teamAppointmentId" placeholder="请输入关联团队预约主记录ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="订单标价金额" prop="listAmount">
              <el-input v-model="form.listAmount" placeholder="请输入订单标价金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="优惠券抵扣金额" prop="couponAmount">
              <el-input v-model="form.couponAmount" placeholder="请输入优惠券抵扣金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="积分抵扣折现金额" prop="pointsDeductAmount">
              <el-input v-model="form.pointsDeductAmount" placeholder="请输入积分抵扣折现金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="使用积分数" prop="pointsUsed">
              <el-input v-model="form.pointsUsed" placeholder="请输入使用积分数" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="平台承担的优惠金额" prop="platformBearAmount">
              <el-input v-model="form.platformBearAmount" placeholder="请输入平台承担的优惠金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="用户净实付金额" prop="netPayAmount">
              <el-input v-model="form.netPayAmount" placeholder="请输入用户净实付金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="平台服务费" prop="serviceFee">
              <el-input v-model="form.serviceFee" placeholder="请输入平台服务费" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="支付方式：wechat/bank_transfer/points/points_cash_mix" prop="payMethod">
              <el-input v-model="form.payMethod" placeholder="请输入支付方式：wechat/bank_transfer/points/points_cash_mix" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="支付发起方：self本人/channel渠道代付" prop="payInitiator">
              <el-input v-model="form.payInitiator" placeholder="请输入支付发起方：self本人/channel渠道代付" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="发起方ID" prop="payInitiatorId">
              <el-input v-model="form.payInitiatorId" placeholder="请输入发起方ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="支付完成时间" prop="payTime">
              <el-date-picker clearable
                v-model="form.payTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择支付完成时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="售后截止时间 = max(赛事结束,成绩发布) + N天" prop="aftersaleDeadline">
              <el-date-picker clearable
                v-model="form.aftersaleDeadline"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择售后截止时间 = max(赛事结束,成绩发布) + N天">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="使用的用户券ID" prop="couponId">
              <el-input v-model="form.couponId" placeholder="请输入使用的用户券ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否允许用户自助退款：0否 1是" prop="allowSelfRefund">
              <el-input v-model="form.allowSelfRefund" placeholder="请输入是否允许用户自助退款：0否 1是" />
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
import { listJst_order_main, getJst_order_main, delJst_order_main, addJst_order_main, updateJst_order_main } from "@/api/jst/order/jst_order_main"

export default {
  name: "Jst_order_main",
  dicts: ['jst_order_status', 'jst_refund_status'],
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
      // 订单主表格数据
      jst_order_mainList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: null,
        orderType: null,
        businessType: null,
        userId: null,
        participantId: null,
        channelId: null,
        contestId: null,
        partnerId: null,
        teamAppointmentId: null,
        listAmount: null,
        couponAmount: null,
        pointsDeductAmount: null,
        pointsUsed: null,
        platformBearAmount: null,
        netPayAmount: null,
        serviceFee: null,
        payMethod: null,
        payInitiator: null,
        payInitiatorId: null,
        payTime: null,
        orderStatus: null,
        refundStatus: null,
        aftersaleDeadline: null,
        couponId: null,
        allowSelfRefund: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        orderNo: [
          { required: true, message: "订单号不能为空", trigger: "blur" }
        ],
        orderType: [
          { required: true, message: "订单类型：normal/zero_price零元/full_deduct全额抵扣/exchange兑换/appointment预约不能为空", trigger: "change" }
        ],
        businessType: [
          { required: true, message: "业务类型：enroll报名/course课程/mall商城/appointment预约不能为空", trigger: "change" }
        ],
        participantId: [
          { required: true, message: "参赛人ID快照不能为空", trigger: "blur" }
        ],
        listAmount: [
          { required: true, message: "订单标价金额不能为空", trigger: "blur" }
        ],
        couponAmount: [
          { required: true, message: "优惠券抵扣金额不能为空", trigger: "blur" }
        ],
        pointsDeductAmount: [
          { required: true, message: "积分抵扣折现金额不能为空", trigger: "blur" }
        ],
        pointsUsed: [
          { required: true, message: "使用积分数不能为空", trigger: "blur" }
        ],
        platformBearAmount: [
          { required: true, message: "平台承担的优惠金额不能为空", trigger: "blur" }
        ],
        netPayAmount: [
          { required: true, message: "用户净实付金额不能为空", trigger: "blur" }
        ],
        serviceFee: [
          { required: true, message: "平台服务费不能为空", trigger: "blur" }
        ],
        orderStatus: [
          { required: true, message: "订单状态：created/pending_pay/paid/reviewing/in_service/aftersale/completed/cancelled/closed不能为空", trigger: "change" }
        ],
        refundStatus: [
          { required: true, message: "退款状态：none/partial/full不能为空", trigger: "change" }
        ],
        allowSelfRefund: [
          { required: true, message: "是否允许用户自助退款：0否 1是不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询订单主列表 */
    getList() {
      this.loading = true
      listJst_order_main(this.queryParams).then(response => {
        this.jst_order_mainList = response.rows
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
        orderId: null,
        orderNo: null,
        orderType: null,
        businessType: null,
        userId: null,
        participantId: null,
        channelId: null,
        contestId: null,
        partnerId: null,
        teamAppointmentId: null,
        listAmount: null,
        couponAmount: null,
        pointsDeductAmount: null,
        pointsUsed: null,
        platformBearAmount: null,
        netPayAmount: null,
        serviceFee: null,
        payMethod: null,
        payInitiator: null,
        payInitiatorId: null,
        payTime: null,
        orderStatus: null,
        refundStatus: null,
        aftersaleDeadline: null,
        couponId: null,
        allowSelfRefund: null,
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
      this.ids = selection.map(item => item.orderId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加订单主"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const orderId = row.orderId || this.ids
      getJst_order_main(orderId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改订单主"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.orderId != null) {
            updateJst_order_main(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_order_main(this.form).then(response => {
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
      const orderIds = row.orderId || this.ids
      this.$modal.confirm('是否确认删除订单主编号为"' + orderIds + '"的数据项？').then(function() {
        return delJst_order_main(orderIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_order_main/export', {
        ...this.queryParams
      }, `jst_order_main_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
