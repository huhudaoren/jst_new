<template>
  <div class="app-container jst-course-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">Course Center</p>
        <h2>课程管理</h2>
        <p class="hero-desc">统一处理课程编辑、审核与上下架，支持手机端卡片查看。</p>
      </div>
      <div class="hero-actions">
        <el-popover placement="bottom" width="280" trigger="hover" popper-class="hero-help-popover">
          <div class="help-lines">
            <p>1. 草稿可继续编辑并提交审核。</p>
            <p>2. 待审核支持通过或驳回。</p>
            <p>3. 通过后可执行上架/下架。</p>
          </div>
          <el-button slot="reference" icon="el-icon-question" plain>操作说明</el-button>
        </el-popover>
        <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
      </div>
    </div>

    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="80px"
      class="query-panel"
    >
      <el-form-item label="课程名称" prop="courseName">
        <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="courseType">
        <el-select v-model="queryParams.courseType" placeholder="全部类型" clearable>
          <el-option label="普通课程" value="normal" />
          <el-option label="AI课程" value="ai_maic" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="全部" clearable>
          <el-option v-for="item in auditStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="上/下架" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="上架" value="on" />
          <el-option label="下架" value="off" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8 toolbar-row">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['jst:event:course:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.courseId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.courseName }}</span>
            <el-tag size="mini" :type="auditMeta(row.auditStatus).type">{{ auditMeta(row.auditStatus).label }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>{{ row.courseType === 'ai_maic' ? 'AI课程' : '普通课程' }}</span>
            <span>￥{{ row.price || 0 }}</span>
            <span>{{ row.lessonCount || 0 }}课时</span>
            <span>{{ row.learnerCount || 0 }}人学习</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:event:course:edit']">编辑</el-button>
            <el-button v-if="row.auditStatus === 'draft'" type="text" size="mini" @click="handleSubmitAudit(row)" v-hasPermi="['jst:event:course:audit']">提审</el-button>
            <el-button v-if="row.auditStatus === 'pending'" type="text" size="mini" @click="handleApprove(row)" v-hasPermi="['jst:event:course:audit']">通过</el-button>
            <el-button v-if="row.auditStatus === 'pending'" type="text" size="mini" @click="handleReject(row)" v-hasPermi="['jst:event:course:audit']">驳回</el-button>
            <el-button v-if="row.auditStatus === 'approved' && row.status !== 'on'" type="text" size="mini" @click="handleOn(row)" v-hasPermi="['jst:event:course:on']">上架</el-button>
            <el-button v-if="row.status === 'on'" type="text" size="mini" @click="handleOff(row)" v-hasPermi="['jst:event:course:off']">下架</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无课程" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="courseId" width="70" />
      <el-table-column label="课程名称" prop="courseName" min-width="200" show-overflow-tooltip />
      <el-table-column label="类型" width="100">
        <template slot-scope="{ row }">{{ row.courseType === 'ai_maic' ? 'AI课程' : '普通课程' }}</template>
      </el-table-column>
      <el-table-column label="价格(元)" prop="price" width="90" />
      <el-table-column label="课时" prop="lessonCount" width="70" />
      <el-table-column label="学习人数" prop="learnerCount" width="90" />
      <el-table-column label="审核状态" width="100">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="auditMeta(row.auditStatus).type">{{ auditMeta(row.auditStatus).label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="上/下架" width="80">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="row.status === 'on' ? 'success' : 'info'">{{ row.status === 'on' ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="280">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:event:course:edit']">编辑</el-button>
          <el-button v-if="row.auditStatus === 'draft'" type="text" size="mini" @click="handleSubmitAudit(row)" v-hasPermi="['jst:event:course:audit']">提审</el-button>
          <el-button v-if="row.auditStatus === 'pending'" type="text" size="mini" @click="handleApprove(row)" v-hasPermi="['jst:event:course:audit']">通过</el-button>
          <el-button v-if="row.auditStatus === 'pending'" type="text" size="mini" @click="handleReject(row)" v-hasPermi="['jst:event:course:audit']">驳回</el-button>
          <el-button v-if="row.auditStatus === 'approved' && row.status !== 'on'" type="text" size="mini" @click="handleOn(row)" v-hasPermi="['jst:event:course:on']">上架</el-button>
          <el-button v-if="row.status === 'on'" type="text" size="mini" @click="handleOff(row)" v-hasPermi="['jst:event:course:off']">下架</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listCourse, submitCourse, approveCourse, rejectCourse, onCourse, offCourse } from '@/api/jst/course'
import { parseTime } from '@/utils/ruoyi'

const AUDIT_META = {
  draft: { label: '草稿', type: 'info' },
  pending: { label: '审核中', type: 'warning' },
  approved: { label: '已通过', type: 'success' },
  rejected: { label: '已驳回', type: 'danger' }
}

export default {
  name: 'JstCourseList',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, courseName: null, courseType: null, auditStatus: null, status: null },
      auditStatusOptions: [
        { label: '草稿', value: 'draft' },
        { label: '审核中', value: 'pending' },
        { label: '已通过', value: 'approved' },
        { label: '已驳回', value: 'rejected' }
      ]
    }
  },
  computed: {
    isMobile() { return this.$store.state.app.device === 'mobile' }
  },
  created() { this.getList() },
  methods: {
    parseTime,
    auditMeta(s) { return AUDIT_META[s] || { label: s || '--', type: 'info' } },
    getList() {
      this.loading = true
      listCourse(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    resetForm(ref) { this.$refs[ref] && this.$refs[ref].resetFields() },
    handleAdd() { this.$router.push('/jst/course-edit') },
    handleEdit(row) { this.$router.push('/jst/course-edit/' + row.courseId) },
    confirmAction(msg, apiFn, id) {
      this.$modal.confirm(msg).then(() => apiFn(id)).then(() => {
        this.$modal.msgSuccess('操作成功')
        this.getList()
      }).catch(() => {})
    },
    handleSubmitAudit(row) { this.confirmAction('确认提交审核「' + row.courseName + '」？', submitCourse, row.courseId) },
    handleApprove(row) { this.confirmAction('确认通过「' + row.courseName + '」？', approveCourse, row.courseId) },
    handleReject(row) { this.confirmAction('确认驳回「' + row.courseName + '」？', rejectCourse, row.courseId) },
    handleOn(row) { this.confirmAction('确认上架「' + row.courseName + '」？', onCourse, row.courseId) },
    handleOff(row) { this.confirmAction('确认下架「' + row.courseName + '」？', offCourse, row.courseId) }
  }
}
</script>

<style scoped>
.jst-course-page {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.page-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 22px 24px;
  margin-bottom: 16px;
  color: #fff;
  border-radius: 12px;
  background: linear-gradient(130deg, #0f766e 0%, #2563eb 58%, #1e293b 100%);
}

.hero-eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: .08em;
  text-transform: uppercase;
  opacity: .82;
}

.page-hero h2 {
  margin: 0;
  font-size: 24px;
}

.hero-desc {
  margin: 8px 0 0;
  color: rgba(255, 255, 255, 0.82);
}

.hero-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.help-lines p {
  margin: 0 0 6px;
  line-height: 1.6;
}

.help-lines p:last-child {
  margin-bottom: 0;
}

.query-panel {
  padding: 16px 16px 0;
  margin-bottom: 16px;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 10px;
}

.toolbar-row {
  margin-bottom: 12px;
}

.mobile-card-list {
  padding: 0 4px;
}

.mobile-card {
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 10px;
  padding: 14px;
  margin-bottom: 10px;
}

.mobile-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.mobile-card__title {
  font-weight: 600;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 68%;
}

.mobile-card__meta {
  font-size: 12px;
  color: #6b7280;
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.mobile-card__actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .jst-course-page {
    padding: 12px;
  }

  .page-hero {
    flex-direction: column;
    align-items: flex-start;
    padding: 18px;
  }

  .page-hero h2 {
    font-size: 20px;
  }

  .hero-actions {
    width: 100%;
  }

  .hero-actions .el-button {
    flex: 1;
    min-height: 44px;
  }

  .query-panel {
    padding-bottom: 10px;
  }

  .query-panel ::v-deep .el-form-item {
    display: block;
    margin-right: 0;
  }

  .query-panel ::v-deep .el-form-item__content,
  .query-panel ::v-deep .el-input,
  .query-panel ::v-deep .el-select,
  .query-panel ::v-deep .el-date-editor {
    width: 100%;
  }

  .query-panel .el-button,
  .mobile-card__actions .el-button {
    min-height: 44px;
  }
}
</style>
