<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="个人预约ID" prop="appointmentId">
        <el-input
          v-model="queryParams.appointmentId"
          placeholder="请输入个人预约ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="团队预约ID" prop="teamAppointmentId">
        <el-input
          v-model="queryParams.teamAppointmentId"
          placeholder="请输入团队预约ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="自定义子项名称" prop="itemName">
        <el-input
          v-model="queryParams.itemName"
          placeholder="请输入自定义子项名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="独立二维码URL" prop="qrCode">
        <el-input
          v-model="queryParams.qrCode"
          placeholder="请输入独立二维码URL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="核销时间" prop="writeoffTime">
        <el-date-picker clearable
          v-model="queryParams.writeoffTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择核销时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="核销操作人" prop="writeoffUserId">
        <el-input
          v-model="queryParams.writeoffUserId"
          placeholder="请输入核销操作人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="核销终端" prop="writeoffTerminal">
        <el-input
          v-model="queryParams.writeoffTerminal"
          placeholder="请输入核销终端"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="已核销人数" prop="writeoffQty">
        <el-input
          v-model="queryParams.writeoffQty"
          placeholder="请输入已核销人数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="总人数" prop="totalQty">
        <el-input
          v-model="queryParams.totalQty"
          placeholder="请输入总人数"
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
          v-hasPermi="['system:jst_appointment_writeoff_item:add']"
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
          v-hasPermi="['system:jst_appointment_writeoff_item:edit']"
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
          v-hasPermi="['system:jst_appointment_writeoff_item:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_appointment_writeoff_item:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_appointment_writeoff_itemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="子项ID" align="center" prop="writeoffItemId" />
      <el-table-column label="个人预约ID" align="center" prop="appointmentId" />
      <el-table-column label="团队预约ID" align="center" prop="teamAppointmentId" />
      <el-table-column label="子项类型：arrival到场/gift礼品/ceremony仪式/custom自定义" align="center" prop="itemType" />
      <el-table-column label="自定义子项名称" align="center" prop="itemName" />
      <el-table-column label="独立二维码URL" align="center" prop="qrCode" />
      <el-table-column label="状态：unused/used/expired/voided" align="center" prop="status" />
      <el-table-column label="核销时间" align="center" prop="writeoffTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.writeoffTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="核销操作人" align="center" prop="writeoffUserId" />
      <el-table-column label="核销终端" align="center" prop="writeoffTerminal" />
      <el-table-column label="已核销人数" align="center" prop="writeoffQty" />
      <el-table-column label="总人数" align="center" prop="totalQty" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_appointment_writeoff_item:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_appointment_writeoff_item:remove']"
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

    <!-- 添加或修改核销子项（团队部分核销/到场/礼品/仪式独立流转）对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="个人预约ID" prop="appointmentId">
              <el-input v-model="form.appointmentId" placeholder="请输入个人预约ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="团队预约ID" prop="teamAppointmentId">
              <el-input v-model="form.teamAppointmentId" placeholder="请输入团队预约ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="自定义子项名称" prop="itemName">
              <el-input v-model="form.itemName" placeholder="请输入自定义子项名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="独立二维码URL" prop="qrCode">
              <el-input v-model="form.qrCode" placeholder="请输入独立二维码URL" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="核销时间" prop="writeoffTime">
              <el-date-picker clearable
                v-model="form.writeoffTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择核销时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="核销操作人" prop="writeoffUserId">
              <el-input v-model="form.writeoffUserId" placeholder="请输入核销操作人" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="核销终端" prop="writeoffTerminal">
              <el-input v-model="form.writeoffTerminal" placeholder="请输入核销终端" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="已核销人数" prop="writeoffQty">
              <el-input v-model="form.writeoffQty" placeholder="请输入已核销人数" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="总人数" prop="totalQty">
              <el-input v-model="form.totalQty" placeholder="请输入总人数" />
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
import { listJst_appointment_writeoff_item, getJst_appointment_writeoff_item, delJst_appointment_writeoff_item, addJst_appointment_writeoff_item, updateJst_appointment_writeoff_item } from "@/api/system/jst_appointment_writeoff_item"

export default {
  name: "Jst_appointment_writeoff_item",
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
      // 核销子项（团队部分核销/到场/礼品/仪式独立流转）表格数据
      jst_appointment_writeoff_itemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        appointmentId: null,
        teamAppointmentId: null,
        itemType: null,
        itemName: null,
        qrCode: null,
        status: null,
        writeoffTime: null,
        writeoffUserId: null,
        writeoffTerminal: null,
        writeoffQty: null,
        totalQty: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        itemType: [
          { required: true, message: "子项类型：arrival到场/gift礼品/ceremony仪式/custom自定义不能为空", trigger: "change" }
        ],
        qrCode: [
          { required: true, message: "独立二维码URL不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态：unused/used/expired/voided不能为空", trigger: "change" }
        ],
        writeoffQty: [
          { required: true, message: "已核销人数不能为空", trigger: "blur" }
        ],
        totalQty: [
          { required: true, message: "总人数不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询核销子项（团队部分核销/到场/礼品/仪式独立流转）列表 */
    getList() {
      this.loading = true
      listJst_appointment_writeoff_item(this.queryParams).then(response => {
        this.jst_appointment_writeoff_itemList = response.rows
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
        writeoffItemId: null,
        appointmentId: null,
        teamAppointmentId: null,
        itemType: null,
        itemName: null,
        qrCode: null,
        status: null,
        writeoffTime: null,
        writeoffUserId: null,
        writeoffTerminal: null,
        writeoffQty: null,
        totalQty: null,
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
      this.ids = selection.map(item => item.writeoffItemId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加核销子项（团队部分核销/到场/礼品/仪式独立流转）"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const writeoffItemId = row.writeoffItemId || this.ids
      getJst_appointment_writeoff_item(writeoffItemId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改核销子项（团队部分核销/到场/礼品/仪式独立流转）"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.writeoffItemId != null) {
            updateJst_appointment_writeoff_item(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_appointment_writeoff_item(this.form).then(response => {
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
      const writeoffItemIds = row.writeoffItemId || this.ids
      this.$modal.confirm('是否确认删除核销子项（团队部分核销/到场/礼品/仪式独立流转）编号为"' + writeoffItemIds + '"的数据项？').then(function() {
        return delJst_appointment_writeoff_item(writeoffItemIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_appointment_writeoff_item/export', {
        ...this.queryParams
      }, `jst_appointment_writeoff_item_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
