<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="券名" prop="couponName">
        <el-input
          v-model="queryParams.couponName"
          placeholder="请输入券名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="面额" prop="faceValue">
        <el-input
          v-model="queryParams.faceValue"
          placeholder="请输入面额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="折扣率" prop="discountRate">
        <el-input
          v-model="queryParams.discountRate"
          placeholder="请输入折扣率"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="门槛金额" prop="thresholdAmount">
        <el-input
          v-model="queryParams.thresholdAmount"
          placeholder="请输入门槛金额"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="适用角色：student/channel/all" prop="applicableRole">
        <el-input
          v-model="queryParams.applicableRole"
          placeholder="请输入适用角色：student/channel/all"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="相对有效天数" prop="validDays">
        <el-input
          v-model="queryParams.validDays"
          placeholder="请输入相对有效天数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="绝对有效开始" prop="validStart">
        <el-date-picker clearable
          v-model="queryParams.validStart"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择绝对有效开始">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="绝对有效结束" prop="validEnd">
        <el-date-picker clearable
          v-model="queryParams.validEnd"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择绝对有效结束">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="叠加规则" prop="stackRule">
        <el-input
          v-model="queryParams.stackRule"
          placeholder="请输入叠加规则"
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
          v-hasPermi="['system:jst_coupon_template:add']"
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
          v-hasPermi="['system:jst_coupon_template:edit']"
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
          v-hasPermi="['system:jst_coupon_template:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_coupon_template:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_coupon_templateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="模板ID" align="center" prop="couponTemplateId" />
      <el-table-column label="券名" align="center" prop="couponName" />
      <el-table-column label="类型：full_reduce满减/direct_reduce直减/discount折扣/contest_specific赛事专享" align="center" prop="couponType" />
      <el-table-column label="面额" align="center" prop="faceValue" />
      <el-table-column label="折扣率" align="center" prop="discountRate" />
      <el-table-column label="门槛金额" align="center" prop="thresholdAmount" />
      <el-table-column label="适用角色：student/channel/all" align="center" prop="applicableRole" />
      <el-table-column label="适用赛事ID列表" align="center" prop="applicableContestIds" />
      <el-table-column label="相对有效天数" align="center" prop="validDays" />
      <el-table-column label="绝对有效开始" align="center" prop="validStart" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.validStart, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="绝对有效结束" align="center" prop="validEnd" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.validEnd, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="叠加规则" align="center" prop="stackRule" />
      <el-table-column label="启停：0停 1启" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_coupon_template:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_coupon_template:remove']"
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

    <!-- 添加或修改优惠券模板对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="券名" prop="couponName">
              <el-input v-model="form.couponName" placeholder="请输入券名" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="面额" prop="faceValue">
              <el-input v-model="form.faceValue" placeholder="请输入面额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="折扣率" prop="discountRate">
              <el-input v-model="form.discountRate" placeholder="请输入折扣率" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="门槛金额" prop="thresholdAmount">
              <el-input v-model="form.thresholdAmount" placeholder="请输入门槛金额" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="适用角色：student/channel/all" prop="applicableRole">
              <el-input v-model="form.applicableRole" placeholder="请输入适用角色：student/channel/all" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="适用赛事ID列表" prop="applicableContestIds">
              <el-input v-model="form.applicableContestIds" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="相对有效天数" prop="validDays">
              <el-input v-model="form.validDays" placeholder="请输入相对有效天数" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="绝对有效开始" prop="validStart">
              <el-date-picker clearable
                v-model="form.validStart"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择绝对有效开始">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="绝对有效结束" prop="validEnd">
              <el-date-picker clearable
                v-model="form.validEnd"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择绝对有效结束">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="叠加规则" prop="stackRule">
              <el-input v-model="form.stackRule" placeholder="请输入叠加规则" />
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
import { listJst_coupon_template, getJst_coupon_template, delJst_coupon_template, addJst_coupon_template, updateJst_coupon_template } from "@/api/system/jst_coupon_template"

export default {
  name: "Jst_coupon_template",
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
      // 优惠券模板表格数据
      jst_coupon_templateList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        couponName: null,
        couponType: null,
        faceValue: null,
        discountRate: null,
        thresholdAmount: null,
        applicableRole: null,
        applicableContestIds: null,
        validDays: null,
        validStart: null,
        validEnd: null,
        stackRule: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        couponName: [
          { required: true, message: "券名不能为空", trigger: "blur" }
        ],
        couponType: [
          { required: true, message: "类型：full_reduce满减/direct_reduce直减/discount折扣/contest_specific赛事专享不能为空", trigger: "change" }
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
    /** 查询优惠券模板列表 */
    getList() {
      this.loading = true
      listJst_coupon_template(this.queryParams).then(response => {
        this.jst_coupon_templateList = response.rows
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
        couponTemplateId: null,
        couponName: null,
        couponType: null,
        faceValue: null,
        discountRate: null,
        thresholdAmount: null,
        applicableRole: null,
        applicableContestIds: null,
        validDays: null,
        validStart: null,
        validEnd: null,
        stackRule: null,
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
      this.ids = selection.map(item => item.couponTemplateId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加优惠券模板"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const couponTemplateId = row.couponTemplateId || this.ids
      getJst_coupon_template(couponTemplateId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改优惠券模板"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.couponTemplateId != null) {
            updateJst_coupon_template(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_coupon_template(this.form).then(response => {
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
      const couponTemplateIds = row.couponTemplateId || this.ids
      this.$modal.confirm('是否确认删除优惠券模板编号为"' + couponTemplateIds + '"的数据项？').then(function() {
        return delJst_coupon_template(couponTemplateIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_coupon_template/export', {
        ...this.queryParams
      }, `jst_coupon_template_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
