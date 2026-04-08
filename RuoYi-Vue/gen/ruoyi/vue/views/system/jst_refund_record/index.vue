<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="售后单号" prop="refundNo">
        <el-input
          v-model="queryParams.refundNo"
          placeholder="请输入售后单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联订单" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入关联订单"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联明细" prop="itemId">
        <el-input
          v-model="queryParams.itemId"
          placeholder="请输入关联明细"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请来源：user/admin/system" prop="applySource">
        <el-input
          v-model="queryParams.applySource"
          placeholder="请输入申请来源：user/admin/system"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请原因" prop="reason">
        <el-input
          v-model="queryParams.reason"
          placeholder="请输入申请原因"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请退现金" prop="applyCash">
        <el-input
          v-model="queryParams.applyCash"
          placeholder="请输入申请退现金"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请退积分" prop="applyPoints">
        <el-input
          v-model="queryParams.applyPoints"
          placeholder="请输入申请退积分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="实退现金" prop="actualCash">
        <el-input
          v-model="queryParams.actualCash"
          placeholder="请输入实退现金"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="实退积分" prop="actualPoints">
        <el-input
          v-model="queryParams.actualPoints"
          placeholder="请输入实退积分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="优惠券是否回退：0否 1是" prop="couponReturned">
        <el-input
          v-model="queryParams.couponReturned"
          placeholder="请输入优惠券是否回退：0否 1是"
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
      <el-form-item label="操作人" prop="operatorId">
        <el-input
          v-model="queryParams.operatorId"
          placeholder="请输入操作人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="完成时间" prop="completeTime">
        <el-date-picker clearable
          v-model="queryParams.completeTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择完成时间">
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
          v-hasPermi="['system:jst_refund_record:add']"
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
          v-hasPermi="['system:jst_refund_record:edit']"
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
          v-hasPermi="['system:jst_refund_record:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_refund_record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_refund_recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="售后ID" align="center" prop="refundId" />
      <el-table-column label="售后单号" align="center" prop="refundNo" />
      <el-table-column label="关联订单" align="center" prop="orderId" />
      <el-table-column label="关联明细" align="center" prop="itemId" />
      <el-table-column label="类型：refund_only仅退款/return_refund退货退款/special_refund特批" align="center" prop="refundType" />
      <el-table-column label="申请来源：user/admin/system" align="center" prop="applySource" />
      <el-table-column label="申请原因" align="center" prop="reason" />
      <el-table-column label="申请退现金" align="center" prop="applyCash" />
      <el-table-column label="申请退积分" align="center" prop="applyPoints" />
      <el-table-column label="实退现金" align="center" prop="actualCash" />
      <el-table-column label="实退积分" align="center" prop="actualPoints" />
      <el-table-column label="优惠券是否回退：0否 1是" align="center" prop="couponReturned" />
      <el-table-column label="状态：pending/approved/rejected/refunding/completed/closed" align="center" prop="status" />
      <el-table-column label="审核备注" align="center" prop="auditRemark" />
      <el-table-column label="操作人" align="center" prop="operatorId" />
      <el-table-column label="完成时间" align="center" prop="completeTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.completeTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['system:jst_refund_record:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_refund_record:remove']"
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

    <!-- 添加或修改退款/售后单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="售后单号" prop="refundNo">
              <el-input v-model="form.refundNo" placeholder="请输入售后单号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联订单" prop="orderId">
              <el-input v-model="form.orderId" placeholder="请输入关联订单" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联明细" prop="itemId">
              <el-input v-model="form.itemId" placeholder="请输入关联明细" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="申请来源：user/admin/system" prop="applySource">
              <el-input v-model="form.applySource" placeholder="请输入申请来源：user/admin/system" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="申请原因" prop="reason">
              <el-input v-model="form.reason" placeholder="请输入申请原因" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="申请退现金" prop="applyCash">
              <el-input v-model="form.applyCash" placeholder="请输入申请退现金" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="申请退积分" prop="applyPoints">
              <el-input v-model="form.applyPoints" placeholder="请输入申请退积分" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="实退现金" prop="actualCash">
              <el-input v-model="form.actualCash" placeholder="请输入实退现金" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="实退积分" prop="actualPoints">
              <el-input v-model="form.actualPoints" placeholder="请输入实退积分" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="优惠券是否回退：0否 1是" prop="couponReturned">
              <el-input v-model="form.couponReturned" placeholder="请输入优惠券是否回退：0否 1是" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核备注" prop="auditRemark">
              <el-input v-model="form.auditRemark" placeholder="请输入审核备注" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作人" prop="operatorId">
              <el-input v-model="form.operatorId" placeholder="请输入操作人" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="完成时间" prop="completeTime">
              <el-date-picker clearable
                v-model="form.completeTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择完成时间">
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
import { listJst_refund_record, getJst_refund_record, delJst_refund_record, addJst_refund_record, updateJst_refund_record } from "@/api/system/jst_refund_record"

export default {
  name: "Jst_refund_record",
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
      // 退款/售后单表格数据
      jst_refund_recordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        refundNo: null,
        orderId: null,
        itemId: null,
        refundType: null,
        applySource: null,
        reason: null,
        applyCash: null,
        applyPoints: null,
        actualCash: null,
        actualPoints: null,
        couponReturned: null,
        status: null,
        auditRemark: null,
        operatorId: null,
        completeTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        refundNo: [
          { required: true, message: "售后单号不能为空", trigger: "blur" }
        ],
        orderId: [
          { required: true, message: "关联订单不能为空", trigger: "blur" }
        ],
        refundType: [
          { required: true, message: "类型：refund_only仅退款/return_refund退货退款/special_refund特批不能为空", trigger: "change" }
        ],
        applySource: [
          { required: true, message: "申请来源：user/admin/system不能为空", trigger: "blur" }
        ],
        applyCash: [
          { required: true, message: "申请退现金不能为空", trigger: "blur" }
        ],
        applyPoints: [
          { required: true, message: "申请退积分不能为空", trigger: "blur" }
        ],
        actualCash: [
          { required: true, message: "实退现金不能为空", trigger: "blur" }
        ],
        actualPoints: [
          { required: true, message: "实退积分不能为空", trigger: "blur" }
        ],
        couponReturned: [
          { required: true, message: "优惠券是否回退：0否 1是不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态：pending/approved/rejected/refunding/completed/closed不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询退款/售后单列表 */
    getList() {
      this.loading = true
      listJst_refund_record(this.queryParams).then(response => {
        this.jst_refund_recordList = response.rows
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
        refundId: null,
        refundNo: null,
        orderId: null,
        itemId: null,
        refundType: null,
        applySource: null,
        reason: null,
        applyCash: null,
        applyPoints: null,
        actualCash: null,
        actualPoints: null,
        couponReturned: null,
        status: null,
        auditRemark: null,
        operatorId: null,
        completeTime: null,
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
      this.ids = selection.map(item => item.refundId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加退款/售后单"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const refundId = row.refundId || this.ids
      getJst_refund_record(refundId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改退款/售后单"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.refundId != null) {
            updateJst_refund_record(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_refund_record(this.form).then(response => {
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
      const refundIds = row.refundId || this.ids
      this.$modal.confirm('是否确认删除退款/售后单编号为"' + refundIds + '"的数据项？').then(function() {
        return delJst_refund_record(refundIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_refund_record/export', {
        ...this.queryParams
      }, `jst_refund_record_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
