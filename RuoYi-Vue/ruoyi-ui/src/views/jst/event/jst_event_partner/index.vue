<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="赛事方名称" prop="partnerName">
        <el-input
          v-model="queryParams.partnerName"
          placeholder="请输入赛事方名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系人姓名" prop="contactName">
        <el-input
          v-model="queryParams.contactName"
          placeholder="请输入联系人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactMobile">
        <el-input
          v-model="queryParams.contactMobile"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="营业执照号" prop="businessLicenseNo">
        <el-input
          v-model="queryParams.businessLicenseNo"
          placeholder="请输入营业执照号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核通过时间" prop="auditTime">
        <el-date-picker clearable
          v-model="queryParams.auditTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择审核通过时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input
          v-model="queryParams.auditRemark"
          placeholder="请输入审核备注"
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
          v-hasPermi="['system:jst_event_partner:add']"
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
          v-hasPermi="['system:jst_event_partner:edit']"
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
          v-hasPermi="['system:jst_event_partner:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_event_partner:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_event_partnerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="赛事方ID" align="center" prop="partnerId" />
      <el-table-column label="赛事方名称" align="center" prop="partnerName" />
      <el-table-column label="联系人姓名" align="center" prop="contactName" />
      <el-table-column label="联系电话" align="center" prop="contactMobile" />
      <el-table-column label="营业执照号" align="center" prop="businessLicenseNo" />
      <el-table-column label="资质材料附件JSON" align="center" prop="qualificationJson" />
      <el-table-column label="结算信息JSON" align="center" prop="settlementInfoJson" />
      <el-table-column label="发票信息JSON" align="center" prop="invoiceInfoJson" />
      <el-table-column label="合同附件JSON" align="center" prop="contractFilesJson" />
      <el-table-column label="入驻审核状态：draft/pending/approved/rejected/supplement" align="center" prop="auditStatus" />
      <el-table-column label="审核通过时间" align="center" prop="auditTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.auditTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核备注" align="center" prop="auditRemark" />
      <el-table-column label="账号启停：0停用 1启用" align="center" prop="accountStatus" />
      <el-table-column label="合作状态：active/expired/terminated" align="center" prop="coopStatus" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_event_partner:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_event_partner:remove']"
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

    <!-- 添加或修改赛事方档案对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="赛事方名称" prop="partnerName">
              <el-input v-model="form.partnerName" placeholder="请输入赛事方名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="联系人姓名" prop="contactName">
              <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="联系电话" prop="contactMobile">
              <el-input v-model="form.contactMobile" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="营业执照号" prop="businessLicenseNo">
              <el-input v-model="form.businessLicenseNo" placeholder="请输入营业执照号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核通过时间" prop="auditTime">
              <el-date-picker clearable
                v-model="form.auditTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择审核通过时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="审核备注" prop="auditRemark">
              <el-input v-model="form.auditRemark" placeholder="请输入审核备注" />
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
import { listJst_event_partner, getJst_event_partner, delJst_event_partner, addJst_event_partner, updateJst_event_partner } from "@/api/jst/event/jst_event_partner"

export default {
  name: "Jst_event_partner",
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
      // 赛事方档案表格数据
      jst_event_partnerList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        partnerName: null,
        contactName: null,
        contactMobile: null,
        businessLicenseNo: null,
        qualificationJson: null,
        settlementInfoJson: null,
        invoiceInfoJson: null,
        contractFilesJson: null,
        auditStatus: null,
        auditTime: null,
        auditRemark: null,
        accountStatus: null,
        coopStatus: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        partnerName: [
          { required: true, message: "赛事方名称不能为空", trigger: "blur" }
        ],
        contactName: [
          { required: true, message: "联系人姓名不能为空", trigger: "blur" }
        ],
        contactMobile: [
          { required: true, message: "联系电话不能为空", trigger: "blur" }
        ],
        auditStatus: [
          { required: true, message: "入驻审核状态：draft/pending/approved/rejected/supplement不能为空", trigger: "change" }
        ],
        accountStatus: [
          { required: true, message: "账号启停：0停用 1启用不能为空", trigger: "change" }
        ],
        coopStatus: [
          { required: true, message: "合作状态：active/expired/terminated不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询赛事方档案列表 */
    getList() {
      this.loading = true
      listJst_event_partner(this.queryParams).then(response => {
        this.jst_event_partnerList = response.rows
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
        partnerId: null,
        partnerName: null,
        contactName: null,
        contactMobile: null,
        businessLicenseNo: null,
        qualificationJson: null,
        settlementInfoJson: null,
        invoiceInfoJson: null,
        contractFilesJson: null,
        auditStatus: null,
        auditTime: null,
        auditRemark: null,
        accountStatus: null,
        coopStatus: null,
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
      this.ids = selection.map(item => item.partnerId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加赛事方档案"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const partnerId = row.partnerId || this.ids
      getJst_event_partner(partnerId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改赛事方档案"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.partnerId != null) {
            updateJst_event_partner(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_event_partner(this.form).then(response => {
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
      const partnerIds = row.partnerId || this.ids
      this.$modal.confirm('是否确认删除赛事方档案编号为"' + partnerIds + '"的数据项？').then(function() {
        return delJst_event_partner(partnerIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_event_partner/export', {
        ...this.queryParams
      }, `jst_event_partner_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
