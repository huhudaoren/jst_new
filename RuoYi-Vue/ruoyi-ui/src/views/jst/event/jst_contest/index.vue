<template>
  <!-- DEPRECATED: 请使用 jst/contest/index.vue -->
  <div class="app-container">
    <el-alert
      title="此页面已废弃"
      type="warning"
      :closable="false"
      show-icon
      style="margin-bottom: 16px"
    >
      <template slot="default">
        请使用精品页继续处理赛事管理。
        <el-button type="text" @click="$router.push('/jst/contest')">前往新页面</el-button>
      </template>
    </el-alert>
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="赛事名称" prop="contestName">
        <el-input
          v-model="queryParams.contestName"
          placeholder="请输入赛事名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属赛事方ID，FK→jst_event_partner" prop="partnerId">
        <el-input
          v-model="queryParams.partnerId"
          placeholder="请输入所属赛事方ID，FK→jst_event_partner"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分类：艺术/音乐/舞蹈/美术/朗诵戏剧/文化/科技/体育" prop="category">
        <el-input
          v-model="queryParams.category"
          placeholder="请输入分类：艺术/音乐/舞蹈/美术/朗诵戏剧/文化/科技/体育"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参赛组别多选" prop="groupLevels">
        <el-input
          v-model="queryParams.groupLevels"
          placeholder="请输入参赛组别多选"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报名开始时间" prop="enrollStartTime">
        <el-date-picker clearable
          v-model="queryParams.enrollStartTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择报名开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="报名结束时间" prop="enrollEndTime">
        <el-date-picker clearable
          v-model="queryParams.enrollEndTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择报名结束时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="比赛开始时间" prop="eventStartTime">
        <el-date-picker clearable
          v-model="queryParams.eventStartTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择比赛开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="比赛结束时间" prop="eventEndTime">
        <el-date-picker clearable
          v-model="queryParams.eventEndTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择比赛结束时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="报名标价" prop="price">
        <el-input
          v-model="queryParams.price"
          placeholder="请输入报名标价"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否支持渠道代报名：0否 1是" prop="supportChannelEnroll">
        <el-input
          v-model="queryParams.supportChannelEnroll"
          placeholder="请输入是否支持渠道代报名：0否 1是"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否支持积分抵扣：0否 1是" prop="supportPointsDeduct">
        <el-input
          v-model="queryParams.supportPointsDeduct"
          placeholder="请输入是否支持积分抵扣：0否 1是"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否支持线下预约：0否 1是" prop="supportAppointment">
        <el-input
          v-model="queryParams.supportAppointment"
          placeholder="请输入是否支持线下预约：0否 1是"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="默认报名表单模板ID，FK→jst_enroll_form_template" prop="formTemplateId">
        <el-input
          v-model="queryParams.formTemplateId"
          placeholder="请输入默认报名表单模板ID，FK→jst_enroll_form_template"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="售后宽限天数" prop="aftersaleDays">
        <el-input
          v-model="queryParams.aftersaleDays"
          placeholder="请输入售后宽限天数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建人用户ID" prop="createdUserId">
        <el-input
          v-model="queryParams.createdUserId"
          placeholder="请输入创建人用户ID"
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
          v-hasPermi="['system:jst_contest:add']"
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
          v-hasPermi="['system:jst_contest:edit']"
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
          v-hasPermi="['system:jst_contest:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:jst_contest:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jst_contestList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="赛事ID" align="center" prop="contestId" />
      <el-table-column label="赛事名称" align="center" prop="contestName" />
      <el-table-column label="赛事来源：platform平台自营/partner赛事方" align="center" prop="sourceType" />
      <el-table-column label="所属赛事方ID，FK→jst_event_partner" align="center" prop="partnerId" />
      <el-table-column label="分类：艺术/音乐/舞蹈/美术/朗诵戏剧/文化/科技/体育" align="center" prop="category" />
      <el-table-column label="参赛组别多选" align="center" prop="groupLevels" />
      <el-table-column label="封面图URL" align="center" prop="coverImage" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.coverImage" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="赛事介绍HTML/富文本" align="center" prop="description" />
      <el-table-column label="报名开始时间" align="center" prop="enrollStartTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.enrollStartTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="报名结束时间" align="center" prop="enrollEndTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.enrollEndTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="比赛开始时间" align="center" prop="eventStartTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.eventStartTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="比赛结束时间" align="center" prop="eventEndTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.eventEndTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="报名标价" align="center" prop="price" />
      <el-table-column label="是否支持渠道代报名：0否 1是" align="center" prop="supportChannelEnroll" />
      <el-table-column label="是否支持积分抵扣：0否 1是" align="center" prop="supportPointsDeduct" />
      <el-table-column label="是否支持线下预约：0否 1是" align="center" prop="supportAppointment" />
      <el-table-column label="证书发放规则JSON" align="center" prop="certRuleJson" />
      <el-table-column label="成绩规则JSON" align="center" prop="scoreRuleJson" />
      <el-table-column label="默认报名表单模板ID，FK→jst_enroll_form_template" align="center" prop="formTemplateId" />
      <el-table-column label="售后宽限天数" align="center" prop="aftersaleDays" />
      <el-table-column label="审核状态：draft/pending/approved/rejected/online/offline" align="center" prop="auditStatus" />
      <el-table-column label="业务状态：not_started/enrolling/closed/scoring/published/ended" align="center" prop="status" />
      <el-table-column label="创建人用户ID" align="center" prop="createdUserId" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:jst_contest:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:jst_contest:remove']"
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

    <!-- 添加或修改赛事主对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="赛事名称" prop="contestName">
              <el-input v-model="form.contestName" placeholder="请输入赛事名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="所属赛事方ID，FK→jst_event_partner" prop="partnerId">
              <el-input v-model="form.partnerId" placeholder="请输入所属赛事方ID，FK→jst_event_partner" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="分类：艺术/音乐/舞蹈/美术/朗诵戏剧/文化/科技/体育" prop="category">
              <el-input v-model="form.category" placeholder="请输入分类：艺术/音乐/舞蹈/美术/朗诵戏剧/文化/科技/体育" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="参赛组别多选" prop="groupLevels">
              <el-input v-model="form.groupLevels" placeholder="请输入参赛组别多选" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="封面图URL" prop="coverImage">
              <image-upload v-model="form.coverImage"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="赛事介绍HTML/富文本" prop="description">
              <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="报名开始时间" prop="enrollStartTime">
              <el-date-picker clearable
                v-model="form.enrollStartTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择报名开始时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="报名结束时间" prop="enrollEndTime">
              <el-date-picker clearable
                v-model="form.enrollEndTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择报名结束时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="比赛开始时间" prop="eventStartTime">
              <el-date-picker clearable
                v-model="form.eventStartTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择比赛开始时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="比赛结束时间" prop="eventEndTime">
              <el-date-picker clearable
                v-model="form.eventEndTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择比赛结束时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="报名标价" prop="price">
              <el-input v-model="form.price" placeholder="请输入报名标价" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否支持渠道代报名：0否 1是" prop="supportChannelEnroll">
              <el-input v-model="form.supportChannelEnroll" placeholder="请输入是否支持渠道代报名：0否 1是" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否支持积分抵扣：0否 1是" prop="supportPointsDeduct">
              <el-input v-model="form.supportPointsDeduct" placeholder="请输入是否支持积分抵扣：0否 1是" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否支持线下预约：0否 1是" prop="supportAppointment">
              <el-input v-model="form.supportAppointment" placeholder="请输入是否支持线下预约：0否 1是" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="默认报名表单模板ID，FK→jst_enroll_form_template" prop="formTemplateId">
              <el-input v-model="form.formTemplateId" placeholder="请输入默认报名表单模板ID，FK→jst_enroll_form_template" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="售后宽限天数" prop="aftersaleDays">
              <el-input v-model="form.aftersaleDays" placeholder="请输入售后宽限天数" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="创建人用户ID" prop="createdUserId">
              <el-input v-model="form.createdUserId" placeholder="请输入创建人用户ID" />
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
import { listJst_contest, getJst_contest, delJst_contest, addJst_contest, updateJst_contest } from "@/api/jst/event/jst_contest"

export default {
  name: "Jst_contest",
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
      // 赛事主表格数据
      jst_contestList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestName: null,
        sourceType: null,
        partnerId: null,
        category: null,
        groupLevels: null,
        coverImage: null,
        description: null,
        enrollStartTime: null,
        enrollEndTime: null,
        eventStartTime: null,
        eventEndTime: null,
        price: null,
        supportChannelEnroll: null,
        supportPointsDeduct: null,
        supportAppointment: null,
        certRuleJson: null,
        scoreRuleJson: null,
        formTemplateId: null,
        aftersaleDays: null,
        auditStatus: null,
        status: null,
        createdUserId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        contestName: [
          { required: true, message: "赛事名称不能为空", trigger: "blur" }
        ],
        sourceType: [
          { required: true, message: "赛事来源：platform平台自营/partner赛事方不能为空", trigger: "change" }
        ],
        category: [
          { required: true, message: "分类：艺术/音乐/舞蹈/美术/朗诵戏剧/文化/科技/体育不能为空", trigger: "blur" }
        ],
        enrollStartTime: [
          { required: true, message: "报名开始时间不能为空", trigger: "blur" }
        ],
        enrollEndTime: [
          { required: true, message: "报名结束时间不能为空", trigger: "blur" }
        ],
        price: [
          { required: true, message: "报名标价不能为空", trigger: "blur" }
        ],
        supportChannelEnroll: [
          { required: true, message: "是否支持渠道代报名：0否 1是不能为空", trigger: "blur" }
        ],
        supportPointsDeduct: [
          { required: true, message: "是否支持积分抵扣：0否 1是不能为空", trigger: "blur" }
        ],
        supportAppointment: [
          { required: true, message: "是否支持线下预约：0否 1是不能为空", trigger: "blur" }
        ],
        aftersaleDays: [
          { required: true, message: "售后宽限天数不能为空", trigger: "blur" }
        ],
        auditStatus: [
          { required: true, message: "审核状态：draft/pending/approved/rejected/online/offline不能为空", trigger: "change" }
        ],
        status: [
          { required: true, message: "业务状态：not_started/enrolling/closed/scoring/published/ended不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询赛事主列表 */
    getList() {
      this.loading = true
      listJst_contest(this.queryParams).then(response => {
        this.jst_contestList = response.rows
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
        contestId: null,
        contestName: null,
        sourceType: null,
        partnerId: null,
        category: null,
        groupLevels: null,
        coverImage: null,
        description: null,
        enrollStartTime: null,
        enrollEndTime: null,
        eventStartTime: null,
        eventEndTime: null,
        price: null,
        supportChannelEnroll: null,
        supportPointsDeduct: null,
        supportAppointment: null,
        certRuleJson: null,
        scoreRuleJson: null,
        formTemplateId: null,
        aftersaleDays: null,
        auditStatus: null,
        status: null,
        createdUserId: null,
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
      this.ids = selection.map(item => item.contestId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加赛事主"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const contestId = row.contestId || this.ids
      getJst_contest(contestId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改赛事主"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.contestId != null) {
            updateJst_contest(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addJst_contest(this.form).then(response => {
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
      const contestIds = row.contestId || this.ids
      this.$modal.confirm('是否确认删除赛事主编号为"' + contestIds + '"的数据项？').then(function() {
        return delJst_contest(contestIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/jst_contest/export', {
        ...this.queryParams
      }, `jst_contest_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
