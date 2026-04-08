<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="合同编号" prop="contractNo">
        <el-input
          v-model="queryParams.contractNo"
          placeholder="请输入合同编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="对象ID" prop="targetId">
        <el-input
          v-model="queryParams.targetId"
          placeholder="请输入对象ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="合同模板ID" prop="templateId">
        <el-input
          v-model="queryParams.templateId"
          placeholder="请输入合同模板ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="合同文件URL" prop="fileUrl">
        <el-input
          v-model="queryParams.fileUrl"
          placeholder="请输入合同文件URL"
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
      <el-form-item label="签署时间" prop="signTime">
        <el-date-picker clearable
          v-model="queryParams.signTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择签署时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="关联结算单ID" prop="refSettlementId">
        <el-input
          v-model="queryParams.refSettlementId"
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
          v-hasPermi="['system:jst_contract_record:add']"
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
          v-hasPermi="['system:jst_contract_record:edit']"
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
          v-hasPermi="['system:jst_contract_record:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_contract_record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_contract_recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="合同ID" align="center" prop="contractId" />
      <el-table-column label="合同编号" align="center" prop="contractNo" />
      <el-table-column label="合同类型：partner_coop赛事方合作/channel_settle渠道结算/supplement补充" align="center" prop="contractType" />
      <el-table-column label="对象类型：partner/channel" align="center" prop="targetType" />
      <el-table-column label="对象ID" align="center" prop="targetId" />
      <el-table-column label="合同模板ID" align="center" prop="templateId" />
      <el-table-column label="合同文件URL" align="center" prop="fileUrl" />
      <el-table-column label="合同状态：draft/pending_sign/signed/expired/archived" align="center" prop="status" />
      <el-table-column label="生效时间" align="center" prop="effectiveTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.effectiveTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="签署时间" align="center" prop="signTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.signTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="关联结算单ID" align="center" prop="refSettlementId" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_contract_record:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_contract_record:remove']"
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

    <!-- 添加或修改合同记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="合同编号" prop="contractNo">
              <el-input v-model="form.contractNo" placeholder="请输入合同编号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="对象ID" prop="targetId">
              <el-input v-model="form.targetId" placeholder="请输入对象ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="合同模板ID" prop="templateId">
              <el-input v-model="form.templateId" placeholder="请输入合同模板ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="合同文件URL" prop="fileUrl">
              <el-input v-model="form.fileUrl" placeholder="请输入合同文件URL" />
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
            <el-form-item label="签署时间" prop="signTime">
              <el-date-picker clearable
                v-model="form.signTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择签署时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联结算单ID" prop="refSettlementId">
              <el-input v-model="form.refSettlementId" placeholder="请输入关联结算单ID" />
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
import { listJst_contract_record, getJst_contract_record, delJst_contract_record, addJst_contract_record, updateJst_contract_record } from "@/api/system/jst_contract_record"

export default {
  name: "Jst_contract_record",
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
      // 合同记录表格数据
      jst_contract_recordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contractNo: null,
        contractType: null,
        targetType: null,
        targetId: null,
        templateId: null,
        fileUrl: null,
        status: null,
        effectiveTime: null,
        signTime: null,
        refSettlementId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        contractNo: [
          { required: true, message: "合同编号不能为空", trigger: "blur" }
        ],
        contractType: [
          { required: true, message: "合同类型：partner_coop赛事方合作/channel_settle渠道结算/supplement补充不能为空", trigger: "change" }
        ],
        targetType: [
          { required: true, message: "对象类型：partner/channel不能为空", trigger: "change" }
        ],
        targetId: [
          { required: true, message: "对象ID不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "合同状态：draft/pending_sign/signed/expired/archived不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询合同记录列表 */
    getList() {
      this.loading = true
      listJst_contract_record(this.queryParams).then(response => {
        this.jst_contract_recordList = response.rows
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
        contractId: null,
        contractNo: null,
        contractType: null,
        targetType: null,
        targetId: null,
        templateId: null,
        fileUrl: null,
        status: null,
        effectiveTime: null,
        signTime: null,
        refSettlementId: null,
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
      this.ids = selection.map(item => item.contractId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加合同记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const contractId = row.contractId || this.ids
      getJst_contract_record(contractId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改合同记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.contractId != null) {
            updateJst_contract_record(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_contract_record(this.form).then(response => {
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
      const contractIds = row.contractId || this.ids
      this.$modal.confirm('是否确认删除合同记录编号为"' + contractIds + '"的数据项？').then(function() {
        return delJst_contract_record(contractIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_contract_record/export', {
        ...this.queryParams
      }, `jst_contract_record_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
