<template>
  <!-- DEPRECATED: 请使用 jst/course/index.vue -->
  <div class="app-container">
    <el-alert
      title="此页面已废弃"
      type="warning"
      :closable="false"
      show-icon
      style="margin-bottom: 16px"
    >
      <template slot="default">
        请使用精品页继续处理课程管理。
        <el-button type="text" @click="$router.push('/jst/course')">前往新页面</el-button>
      </template>
    </el-alert>
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="课程名称" prop="courseName">
        <el-input
          v-model="queryParams.courseName"
          placeholder="请输入课程名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="现金价格" prop="price">
        <el-input
          v-model="queryParams.price"
          placeholder="请输入现金价格"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="积分价格" prop="pointsPrice">
        <el-input
          v-model="queryParams.pointsPrice"
          placeholder="请输入积分价格"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建者ID" prop="creatorId">
        <el-input
          v-model="queryParams.creatorId"
          placeholder="请输入创建者ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="OpenMAIC 第三方课件ID" prop="maicSourceId">
        <el-input
          v-model="queryParams.maicSourceId"
          placeholder="请输入OpenMAIC 第三方课件ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="可见范围" prop="visibleScope">
        <el-input
          v-model="queryParams.visibleScope"
          placeholder="请输入可见范围"
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
          v-hasPermi="['system:jst_course:add']"
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
          v-hasPermi="['system:jst_course:edit']"
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
          v-hasPermi="['system:jst_course:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_course:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_courseList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="课程ID" align="center" prop="courseId" />
      <el-table-column label="课程名称" align="center" prop="courseName" />
      <el-table-column label="类型：normal普通 / ai_maic AI课程" align="center" prop="courseType" />
      <el-table-column label="封面" align="center" prop="coverImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.coverImage" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="简介" align="center" prop="description" />
      <el-table-column label="现金价格" align="center" prop="price" />
      <el-table-column label="积分价格" align="center" prop="pointsPrice" />
      <el-table-column label="创建者类型：platform/channel" align="center" prop="creatorType" />
      <el-table-column label="创建者ID" align="center" prop="creatorId" />
      <el-table-column label="OpenMAIC 第三方课件ID" align="center" prop="maicSourceId" />
      <el-table-column label="审核状态：draft/pending/approved/rejected" align="center" prop="auditStatus" />
      <el-table-column label="上下架：on/off" align="center" prop="status" />
      <el-table-column label="可见范围" align="center" prop="visibleScope" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_course:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_course:remove']"
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

    <!-- 添加或修改课程对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="课程名称" prop="courseName">
              <el-input v-model="form.courseName" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="封面" prop="coverImage">
              <image-upload v-model="form.coverImage"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="简介" prop="description">
              <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="现金价格" prop="price">
              <el-input v-model="form.price" placeholder="请输入现金价格" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="积分价格" prop="pointsPrice">
              <el-input v-model="form.pointsPrice" placeholder="请输入积分价格" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="创建者ID" prop="creatorId">
              <el-input v-model="form.creatorId" placeholder="请输入创建者ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="OpenMAIC 第三方课件ID" prop="maicSourceId">
              <el-input v-model="form.maicSourceId" placeholder="请输入OpenMAIC 第三方课件ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="可见范围" prop="visibleScope">
              <el-input v-model="form.visibleScope" placeholder="请输入可见范围" />
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
import { listJst_course, getJst_course, delJst_course, addJst_course, updateJst_course } from "@/api/jst/event/jst_course"

export default {
  name: "Jst_course",
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
      // 课程表格数据
      jst_courseList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseName: null,
        courseType: null,
        coverImage: null,
        description: null,
        price: null,
        pointsPrice: null,
        creatorType: null,
        creatorId: null,
        maicSourceId: null,
        auditStatus: null,
        status: null,
        visibleScope: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        courseName: [
          { required: true, message: "课程名称不能为空", trigger: "blur" }
        ],
        courseType: [
          { required: true, message: "类型：normal普通 / ai_maic AI课程不能为空", trigger: "change" }
        ],
        price: [
          { required: true, message: "现金价格不能为空", trigger: "blur" }
        ],
        creatorType: [
          { required: true, message: "创建者类型：platform/channel不能为空", trigger: "change" }
        ],
        auditStatus: [
          { required: true, message: "审核状态：draft/pending/approved/rejected不能为空", trigger: "change" }
        ],
        status: [
          { required: true, message: "上下架：on/off不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询课程列表 */
    getList() {
      this.loading = true
      listJst_course(this.queryParams).then(response => {
        this.jst_courseList = response.rows
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
        courseId: null,
        courseName: null,
        courseType: null,
        coverImage: null,
        description: null,
        price: null,
        pointsPrice: null,
        creatorType: null,
        creatorId: null,
        maicSourceId: null,
        auditStatus: null,
        status: null,
        visibleScope: null,
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
      this.ids = selection.map(item => item.courseId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加课程"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const courseId = row.courseId || this.ids
      getJst_course(courseId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改课程"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.courseId != null) {
            updateJst_course(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_course(this.form).then(response => {
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
      const courseIds = row.courseId || this.ids
      this.$modal.confirm('是否确认删除课程编号为"' + courseIds + '"的数据项？').then(function() {
        return delJst_course(courseIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_course/export', {
        ...this.queryParams
      }, `jst_course_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
