<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="等级编码" prop="levelCode">
        <el-input
          v-model="queryParams.levelCode"
          placeholder="请输入等级编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="等级名称" prop="levelName">
        <el-input
          v-model="queryParams.levelName"
          placeholder="请输入等级名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="等级序号" prop="levelNo">
        <el-input
          v-model="queryParams.levelNo"
          placeholder="请输入等级序号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="成长值门槛" prop="growthThreshold">
        <el-input
          v-model="queryParams.growthThreshold"
          placeholder="请输入成长值门槛"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="等级图标URL" prop="icon">
        <el-input
          v-model="queryParams.icon"
          placeholder="请输入等级图标URL"
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
          v-hasPermi="['system:jst_level_config:add']"
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
          v-hasPermi="['system:jst_level_config:edit']"
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
          v-hasPermi="['system:jst_level_config:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_level_config:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_level_configList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="等级ID" align="center" prop="levelId" />
      <el-table-column label="等级编码" align="center" prop="levelCode" />
      <el-table-column label="等级名称" align="center" prop="levelName" />
      <el-table-column label="等级序号" align="center" prop="levelNo" />
      <el-table-column label="成长值门槛" align="center" prop="growthThreshold" />
      <el-table-column label="等级图标URL" align="center" prop="icon" />
      <el-table-column label="适用角色：student/channel/both" align="center" prop="applicableRole" />
      <el-table-column label="启停：0停 1启" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_level_config:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_level_config:remove']"
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

    <!-- 添加或修改等级配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="等级编码" prop="levelCode">
              <el-input v-model="form.levelCode" placeholder="请输入等级编码" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="等级名称" prop="levelName">
              <el-input v-model="form.levelName" placeholder="请输入等级名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="等级序号" prop="levelNo">
              <el-input v-model="form.levelNo" placeholder="请输入等级序号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="成长值门槛" prop="growthThreshold">
              <el-input v-model="form.growthThreshold" placeholder="请输入成长值门槛" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="等级图标URL" prop="icon">
              <el-input v-model="form.icon" placeholder="请输入等级图标URL" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="适用角色：student/channel/both" prop="applicableRole">
              <el-input v-model="form.applicableRole" placeholder="请输入适用角色：student/channel/both" />
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
import { listJst_level_config, getJst_level_config, delJst_level_config, addJst_level_config, updateJst_level_config } from "@/api/jst/points/jst_level_config"

export default {
  name: "Jst_level_config",
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
      // 等级配置表格数据
      jst_level_configList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        levelCode: null,
        levelName: null,
        levelNo: null,
        growthThreshold: null,
        icon: null,
        applicableRole: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        levelCode: [
          { required: true, message: "等级编码不能为空", trigger: "blur" }
        ],
        levelName: [
          { required: true, message: "等级名称不能为空", trigger: "blur" }
        ],
        levelNo: [
          { required: true, message: "等级序号不能为空", trigger: "blur" }
        ],
        growthThreshold: [
          { required: true, message: "成长值门槛不能为空", trigger: "blur" }
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
    /** 查询等级配置列表 */
    getList() {
      this.loading = true
      listJst_level_config(this.queryParams).then(response => {
        this.jst_level_configList = response.rows
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
        levelId: null,
        levelCode: null,
        levelName: null,
        levelNo: null,
        growthThreshold: null,
        icon: null,
        applicableRole: null,
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
      this.ids = selection.map(item => item.levelId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加等级配置"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const levelId = row.levelId || this.ids
      getJst_level_config(levelId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改等级配置"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.levelId != null) {
            updateJst_level_config(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_level_config(this.form).then(response => {
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
      const levelIds = row.levelId || this.ids
      this.$modal.confirm('是否确认删除等级配置编号为"' + levelIds + '"的数据项？').then(function() {
        return delJst_level_config(levelIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_level_config/export', {
        ...this.queryParams
      }, `jst_level_config_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
