<template>
  <div class="app-container jst-course-learn-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">学习中心</p>
        <h2>学习记录</h2>
        <p class="hero-desc">按课程与用户检索学习进度，支持进度条展示与完成时间筛选。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="88px"
      class="query-panel"
    >
      <el-form-item label="课程" prop="courseId">
        <el-input
          v-model.trim="courseKeyword"
          placeholder="课程ID/课程名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户" prop="userId">
        <el-input
          v-model.trim="userKeyword"
          placeholder="用户ID/昵称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="完成状态" prop="completeStatus">
        <el-select v-model="queryParams.completeStatus" placeholder="全部" clearable>
          <el-option v-for="item in completeStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="完成时间">
        <el-date-picker
          v-model="completeRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8 action-row">
      <el-col :xs="12" :sm="8" :md="4">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd" v-hasPermi="['jst:event:course_learn_record:add']">新增</el-button>
      </el-col>
      <el-col :xs="12" :sm="8" :md="4">
        <el-button icon="el-icon-download" @click="handleExport" v-hasPermi="['jst:event:course_learn_record:export']">导出</el-button>
      </el-col>
    </el-row>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="recordList.length">
        <div v-for="row in recordList" :key="row.learnId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.courseName || ('课程 #' + (row.courseId || '--')) }}</div>
              <div class="mobile-sub">学习ID：{{ row.learnId }}</div>
            </div>
            <JstStatusBadge :status="String(row.completeStatus || '')" :status-map="completeStatusMap" />
          </div>
          <div class="mobile-info-row">用户：{{ row.userNickname || row.userId || '--' }}</div>
          <div class="mobile-info-row">
            学习进度：
            <span class="progress-text">{{ toPercent(row.progress) }}%</span>
          </div>
          <el-progress :percentage="toPercent(row.progress)" :stroke-width="8" />
          <div class="mobile-info-row">学习时长：{{ formatDuration(row.durationSeconds) }}</div>
          <div class="mobile-info-row">测验分：{{ formatScore(row.quizScore) }}</div>
          <div class="mobile-info-row">完成时间：{{ parseTime(row.completeTime) || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
            <el-button type="text" v-hasPermi="['jst:event:course_learn_record:edit']" @click="handleUpdate(row)">编辑</el-button>
            <el-button type="text" class="danger-text" v-hasPermi="['jst:event:course_learn_record:remove']" @click="handleDelete(row)">删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无学习记录" :image-size="96" />
    </div>

    <el-table v-else v-loading="loading" :data="recordList">
      <template slot="empty">
        <el-empty description="暂无学习记录" :image-size="96" />
      </template>
      <el-table-column label="学习ID" prop="learnId" min-width="90" />
      <el-table-column label="课程" min-width="190" show-overflow-tooltip>
        <template slot-scope="scope">
          {{ scope.row.courseName || ('课程 #' + (scope.row.courseId || '--')) }}
        </template>
      </el-table-column>
      <el-table-column label="用户" min-width="130" show-overflow-tooltip>
        <template slot-scope="scope">
          {{ scope.row.userNickname || scope.row.userId || '--' }}
        </template>
      </el-table-column>
      <el-table-column label="学习进度" min-width="210">
        <template slot-scope="scope">
          <el-progress :percentage="toPercent(scope.row.progress)" :stroke-width="10" />
        </template>
      </el-table-column>
      <el-table-column label="学习时长" min-width="120">
        <template slot-scope="scope">{{ formatDuration(scope.row.durationSeconds) }}</template>
      </el-table-column>
      <el-table-column label="测验分" min-width="90" align="right">
        <template slot-scope="scope">{{ formatScore(scope.row.quizScore) }}</template>
      </el-table-column>
      <el-table-column label="完成状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.completeStatus || '')" :status-map="completeStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="完成时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.completeTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="210" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
          <el-button type="text" v-hasPermi="['jst:event:course_learn_record:edit']" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button type="text" class="danger-text" v-hasPermi="['jst:event:course_learn_record:remove']" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-drawer
      title="学习详情"
      :visible.sync="detailVisible"
      :size="isMobile ? '100%' : '50%'"
      append-to-body
    >
      <div v-loading="detailLoading" class="drawer-body">
        <template v-if="detailData">
          <el-progress :percentage="toPercent(detailData.progress)" :stroke-width="12" class="detail-progress" />
          <el-descriptions :column="isMobile ? 1 : 2" border>
            <el-descriptions-item label="学习ID">{{ detailData.learnId || '--' }}</el-descriptions-item>
            <el-descriptions-item label="课程">{{ detailData.courseName || detailData.courseId || '--' }}</el-descriptions-item>
            <el-descriptions-item label="用户">{{ detailData.userNickname || detailData.userId || '--' }}</el-descriptions-item>
            <el-descriptions-item label="学习进度">{{ toPercent(detailData.progress) }}%</el-descriptions-item>
            <el-descriptions-item label="学习时长">{{ formatDuration(detailData.durationSeconds) }}</el-descriptions-item>
            <el-descriptions-item label="测验分">{{ formatScore(detailData.quizScore) }}</el-descriptions-item>
            <el-descriptions-item label="完成状态">
              <JstStatusBadge :status="String(detailData.completeStatus || '')" :status-map="completeStatusMap" />
            </el-descriptions-item>
            <el-descriptions-item label="完成时间">{{ parseTime(detailData.completeTime) || '--' }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="isMobile ? 1 : 2">{{ detailData.remark || '--' }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </div>
    </el-drawer>

    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程" prop="courseId">
          <course-picker v-model="form.courseId" />
        </el-form-item>
        <el-form-item label="用户" prop="userId">
          <user-picker v-model="form.userId" />
        </el-form-item>
        <el-form-item label="学习进度" prop="progress">
          <el-input v-model="form.progress" placeholder="请输入学习进度百分比" />
        </el-form-item>
        <el-form-item label="学习时长" prop="durationSeconds">
          <el-input v-model="form.durationSeconds" placeholder="请输入累计学习时长(秒)" />
        </el-form-item>
        <el-form-item label="测验分" prop="quizScore">
          <el-input v-model="form.quizScore" placeholder="请输入测验分" />
        </el-form-item>
        <el-form-item label="完成状态" prop="completeStatus">
          <el-select v-model="form.completeStatus" placeholder="请选择">
            <el-option v-for="item in completeStatusOptions" :key="'f-c-' + item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="完成时间" prop="completeTime">
          <el-date-picker
            v-model="form.completeTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择完成时间"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" maxlength="255" show-word-limit placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addJst_course_learn_record,
  delJst_course_learn_record,
  getJst_course_learn_record,
  listJst_course_learn_record,
  updateJst_course_learn_record
} from '@/api/jst/event/jst_course_learn_record'

export default {
  name: 'JstCourseLearnRecord',
  data() {
    return {
      loading: false,
      total: 0,
      rawList: [],
      courseKeyword: '',
      userKeyword: '',
      completeRange: [],
      detailVisible: false,
      detailLoading: false,
      detailData: null,
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseId: undefined,
        userId: undefined,
        completeStatus: undefined
      },
      form: {},
      rules: {
        courseId: [{ required: true, message: '课程ID不能为空', trigger: 'blur' }],
        userId: [{ required: true, message: '用户ID不能为空', trigger: 'blur' }],
        progress: [{ required: true, message: '学习进度不能为空', trigger: 'blur' }],
        durationSeconds: [{ required: true, message: '累计学习时长不能为空', trigger: 'blur' }],
        completeStatus: [{ required: true, message: '完成状态不能为空', trigger: 'change' }]
      },
      completeStatusOptions: [
        { label: '学习中', value: 'learning' },
        { label: '已完成', value: 'completed' }
      ],
      completeStatusMap: {
        learning: { label: '学习中', type: 'warning' },
        completed: { label: '已完成', type: 'success' }
      }
    }
  },
  computed: {
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    },
    recordList() {
      const courseKeyword = String(this.courseKeyword || '').toLowerCase()
      const userKeyword = String(this.userKeyword || '').toLowerCase()
      const hasRange = this.completeRange && this.completeRange.length === 2
      let list = Array.isArray(this.rawList) ? this.rawList.slice() : []

      if (courseKeyword) {
        list = list.filter(row => this.containsText(row.courseName, courseKeyword) || this.containsText(row.courseId, courseKeyword))
      }
      if (userKeyword) {
        list = list.filter(row => this.containsText(row.userNickname, userKeyword) || this.containsText(row.userId, userKeyword))
      }
      if (hasRange) {
        const begin = new Date(this.completeRange[0] + ' 00:00:00').getTime()
        const end = new Date(this.completeRange[1] + ' 23:59:59').getTime()
        list = list.filter(row => {
          const time = this.toTime(row.completeTime)
          return time && time >= begin && time <= end
        })
      }
      return list
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.loading = true
      try {
        const params = { ...this.queryParams }
        if (!params.courseId && /^\d+$/.test(this.courseKeyword)) {
          params.courseId = Number(this.courseKeyword)
        }
        if (!params.userId && /^\d+$/.test(this.userKeyword)) {
          params.userId = Number(this.userKeyword)
        }
        const res = await listJst_course_learn_record(params)
        this.rawList = Array.isArray(res.rows) ? res.rows : []
        this.total = Number(res.total || 0)
      } finally {
        this.loading = false
      }
    },
    reset() {
      this.form = {
        learnId: undefined,
        courseId: undefined,
        userId: undefined,
        progress: undefined,
        durationSeconds: undefined,
        quizScore: undefined,
        completeStatus: undefined,
        completeTime: undefined,
        remark: undefined
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.courseKeyword = ''
      this.userKeyword = ''
      this.completeRange = []
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加学习记录'
    },
    handleUpdate(row) {
      const learnId = row && row.learnId
      if (!learnId) return
      this.reset()
      getJst_course_learn_record(learnId).then(res => {
        this.form = { ...this.form, ...(res.data || {}) }
        this.open = true
        this.title = '编辑学习记录'
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const api = this.form.learnId ? updateJst_course_learn_record : addJst_course_learn_record
        api(this.form).then(() => {
          this.$modal.msgSuccess(this.form.learnId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const learnId = row && row.learnId
      if (!learnId) return
      this.$modal.confirm('确认删除学习记录「' + learnId + '」吗？').then(() => {
        return delJst_course_learn_record(learnId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleExport() {
      const params = { ...this.queryParams }
      if (!params.courseId && /^\d+$/.test(this.courseKeyword)) {
        params.courseId = Number(this.courseKeyword)
      }
      if (!params.userId && /^\d+$/.test(this.userKeyword)) {
        params.userId = Number(this.userKeyword)
      }
      this.download('jst/event/jst_course_learn_record/export', params, `jst_course_learn_record_${Date.now()}.xlsx`)
    },
    openDetail(row) {
      const learnId = row && row.learnId
      this.detailData = row || null
      this.detailVisible = true
      if (!learnId) return
      this.detailLoading = true
      getJst_course_learn_record(learnId).then(res => {
        this.detailData = res.data || row
      }).finally(() => {
        this.detailLoading = false
      })
    },
    toPercent(value) {
      const num = Number(value || 0)
      if (!Number.isFinite(num)) return 0
      if (num > 1) return Math.min(Math.max(Math.round(num), 0), 100)
      return Math.min(Math.max(Math.round(num * 100), 0), 100)
    },
    formatDuration(seconds) {
      const total = Number(seconds || 0)
      if (!Number.isFinite(total) || total <= 0) return '--'
      const h = Math.floor(total / 3600)
      const m = Math.floor((total % 3600) / 60)
      const s = total % 60
      if (h > 0) return h + 'h ' + m + 'm ' + s + 's'
      if (m > 0) return m + 'm ' + s + 's'
      return s + 's'
    },
    formatScore(value) {
      if (value === undefined || value === null || value === '') return '--'
      return value
    },
    containsText(value, keyword) {
      return String(value === undefined || value === null ? '' : value).toLowerCase().indexOf(keyword) > -1
    },
    toTime(value) {
      if (!value) return 0
      const text = String(value).replace('T', ' ')
      const time = new Date(text).getTime()
      return Number.isNaN(time) ? 0 : time
    }
  }
}
</script>

<style scoped lang="scss">
.jst-course-learn-page {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.page-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
  margin-bottom: 18px;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.hero-eyebrow {
  margin: 0 0 8px;
  color: #2f6fec;
  font-size: 13px;
  font-weight: 600;
}

.page-hero h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #172033;
}

.hero-desc {
  margin: 8px 0 0;
  color: #6f7b8f;
}

.query-panel {
  padding: 16px 16px 0;
  margin-bottom: 16px;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.action-row .el-button {
  width: 100%;
}

.mobile-list {
  min-height: 180px;
}

.mobile-card {
  padding: 16px;
  margin-bottom: 12px;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.mobile-card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.mobile-title {
  font-weight: 700;
  color: #172033;
}

.mobile-sub {
  margin-top: 4px;
  font-size: 12px;
  color: #7a8495;
}

.mobile-info-row {
  margin-top: 8px;
  color: #6f7b8f;
  font-size: 13px;
}

.progress-text {
  color: #2f6fec;
  font-weight: 600;
}

.mobile-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 12px;
}

.danger-text {
  color: #f56c6c;
}

.drawer-body {
  padding: 8px 16px 16px;
}

.detail-progress {
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .jst-course-learn-page {
    padding: 12px;
  }

  .page-hero {
    display: block;
    padding: 18px;
  }

  .page-hero .el-button {
    width: 100%;
    min-height: 44px;
    margin-top: 16px;
  }

  .page-hero h2 {
    font-size: 20px;
  }

  .query-panel {
    padding-bottom: 8px;
  }

  .query-panel ::v-deep .el-form-item {
    display: block;
    margin-right: 0;
  }

  .query-panel ::v-deep .el-form-item__content,
  .query-panel ::v-deep .el-select,
  .query-panel ::v-deep .el-input,
  .query-panel ::v-deep .el-date-editor {
    width: 100%;
  }

  .mobile-actions .el-button {
    min-height: 44px;
  }
}
</style>
