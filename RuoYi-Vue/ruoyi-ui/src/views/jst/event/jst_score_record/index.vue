<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="赛事ID" prop="contestId">
        <el-input
          v-model="queryParams.contestId"
          placeholder="请输入赛事ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报名ID" prop="enrollId">
        <el-input
          v-model="queryParams.enrollId"
          placeholder="请输入报名ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参赛人ID" prop="participantId">
        <el-input
          v-model="queryParams.participantId"
          placeholder="请输入参赛人ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分数" prop="scoreValue">
        <el-input
          v-model="queryParams.scoreValue"
          placeholder="请输入分数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="奖项等级：国际金/国际银/全国金/全国银/全国铜/优秀/参与" prop="awardLevel">
        <el-input
          v-model="queryParams.awardLevel"
          placeholder="请输入奖项等级：国际金/国际银/全国金/全国银/全国铜/优秀/参与"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="名次" prop="rankNo">
        <el-input
          v-model="queryParams.rankNo"
          placeholder="请输入名次"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="导入批次号" prop="importBatchNo">
        <el-input
          v-model="queryParams.importBatchNo"
          placeholder="请输入导入批次号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发布时间" prop="publishTime">
        <el-date-picker clearable
          v-model="queryParams.publishTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择发布时间">
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
          v-hasPermi="['system:jst_score_record:add']"
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
          v-hasPermi="['system:jst_score_record:edit']"
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
          v-hasPermi="['system:jst_score_record:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_score_record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_score_recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="成绩ID" align="center" prop="scoreId" />
      <el-table-column label="赛事ID" align="center" prop="contestId" />
      <el-table-column label="报名ID" align="center" prop="enrollId" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="参赛人ID" align="center" prop="participantId" />
      <el-table-column label="分数" align="center" prop="scoreValue" />
      <el-table-column label="奖项等级：国际金/国际银/全国金/全国银/全国铜/优秀/参与" align="center" prop="awardLevel" />
      <el-table-column label="名次" align="center" prop="rankNo" />
      <el-table-column label="导入批次号" align="center" prop="importBatchNo" />
      <el-table-column label="审核状态：pending/approved/rejected" align="center" prop="auditStatus" />
      <el-table-column label="发布状态：unpublished/published/withdrawn" align="center" prop="publishStatus" />
      <el-table-column label="发布时间" align="center" prop="publishTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.publishTime, '{y}-{m}-{d}') }}</span>
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
            v-hasPermi="['system:jst_score_record:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_score_record:remove']"
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

    <!-- 添加或修改成绩记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="赛事ID" prop="contestId">
              <el-input v-model="form.contestId" placeholder="请输入赛事ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="报名ID" prop="enrollId">
              <el-input v-model="form.enrollId" placeholder="请输入报名ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="参赛人ID" prop="participantId">
              <el-input v-model="form.participantId" placeholder="请输入参赛人ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="分数" prop="scoreValue">
              <el-input v-model="form.scoreValue" placeholder="请输入分数" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="奖项等级：国际金/国际银/全国金/全国银/全国铜/优秀/参与" prop="awardLevel">
              <el-input v-model="form.awardLevel" placeholder="请输入奖项等级：国际金/国际银/全国金/全国银/全国铜/优秀/参与" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="名次" prop="rankNo">
              <el-input v-model="form.rankNo" placeholder="请输入名次" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="导入批次号" prop="importBatchNo">
              <el-input v-model="form.importBatchNo" placeholder="请输入导入批次号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="发布时间" prop="publishTime">
              <el-date-picker clearable
                v-model="form.publishTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择发布时间">
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
import { listJst_score_record, getJst_score_record, delJst_score_record, addJst_score_record, updateJst_score_record } from "@/api/jst/event/jst_score_record"

export default {
  name: "Jst_score_record",
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
      // 成绩记录表格数据
      jst_score_recordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestId: null,
        enrollId: null,
        userId: null,
        participantId: null,
        scoreValue: null,
        awardLevel: null,
        rankNo: null,
        importBatchNo: null,
        auditStatus: null,
        publishStatus: null,
        publishTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        contestId: [
          { required: true, message: "赛事ID不能为空", trigger: "blur" }
        ],
        enrollId: [
          { required: true, message: "报名ID不能为空", trigger: "blur" }
        ],
        participantId: [
          { required: true, message: "参赛人ID不能为空", trigger: "blur" }
        ],
        auditStatus: [
          { required: true, message: "审核状态：pending/approved/rejected不能为空", trigger: "change" }
        ],
        publishStatus: [
          { required: true, message: "发布状态：unpublished/published/withdrawn不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询成绩记录列表 */
    getList() {
      this.loading = true
      listJst_score_record(this.queryParams).then(response => {
        this.jst_score_recordList = response.rows
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
        scoreId: null,
        contestId: null,
        enrollId: null,
        userId: null,
        participantId: null,
        scoreValue: null,
        awardLevel: null,
        rankNo: null,
        importBatchNo: null,
        auditStatus: null,
        publishStatus: null,
        publishTime: null,
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
      this.ids = selection.map(item => item.scoreId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加成绩记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const scoreId = row.scoreId || this.ids
      getJst_score_record(scoreId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改成绩记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.scoreId != null) {
            updateJst_score_record(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_score_record(this.form).then(response => {
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
      const scoreIds = row.scoreId || this.ids
      this.$modal.confirm('是否确认删除成绩记录编号为"' + scoreIds + '"的数据项？').then(function() {
        return delJst_score_record(scoreIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_score_record/export', {
        ...this.queryParams
      }, `jst_score_record_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
