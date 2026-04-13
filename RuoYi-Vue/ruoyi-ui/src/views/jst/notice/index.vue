<template>
  <div class="app-container jst-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">Notice Center</p>
        <h2>公告管理</h2>
        <p class="hero-desc">统一维护公告分类、发布状态与置顶信息，编辑区改为抽屉。</p>
      </div>
      <div class="hero-actions">
        <el-popover placement="bottom" width="280" trigger="hover">
          <div class="help-lines">
            <p>1. 草稿可编辑后发布。</p>
            <p>2. 已发布公告可一键下线。</p>
            <p>3. 专题活动公告需上传封面图。</p>
          </div>
          <el-button slot="reference" icon="el-icon-question" plain>使用说明</el-button>
        </el-popover>
        <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
      </div>
    </div>

    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="68px" class="query-panel">
      <el-form-item label="标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入标题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-select v-model="queryParams.category" placeholder="全部分类" clearable>
          <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8 toolbar-row">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['jst:message:notice:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.noticeId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.title }}</span>
            <el-tag size="mini" :type="statusMeta(row.status).type">{{ statusMeta(row.status).label }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>{{ categoryLabel(row.category) }}</span>
            <span v-if="row.topFlag === 1" class="top-text">置顶</span>
            <span>{{ parseTime(row.publishTime || row.createTime) }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:message:notice:edit']">编辑</el-button>
            <el-button v-if="row.status === 'draft'" type="text" size="mini" @click="handlePublish(row)" v-hasPermi="['jst:message:notice:publish']">发布</el-button>
            <el-button v-if="row.status === 'published'" type="text" size="mini" @click="handleOffline(row)" v-hasPermi="['jst:message:notice:offline']">下线</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无公告" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="noticeId" width="70" />
      <el-table-column label="标题" prop="title" min-width="200" show-overflow-tooltip />
      <el-table-column label="分类" width="120">
        <template slot-scope="{ row }">{{ categoryLabel(row.category) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="statusMeta(row.status).type">{{ statusMeta(row.status).label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="置顶" width="70">
        <template slot-scope="{ row }">
          <el-tag v-if="row.topFlag === 1" size="small" type="warning">是</el-tag>
          <span v-else>否</span>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" width="160">
        <template slot-scope="{ row }">{{ parseTime(row.publishTime) }}</template>
      </el-table-column>
      <el-table-column label="创建时间" width="160">
        <template slot-scope="{ row }">{{ parseTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="220">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:message:notice:edit']">编辑</el-button>
          <el-button v-if="row.status === 'draft'" type="text" size="mini" @click="handlePublish(row)" v-hasPermi="['jst:message:notice:publish']">发布</el-button>
          <el-button v-if="row.status === 'published'" type="text" size="mini" @click="handleOffline(row)" v-hasPermi="['jst:message:notice:offline']">下线</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-drawer
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :size="isMobile ? '100%' : '55%'"
      append-to-body
      :with-header="true"
    >
      <div class="drawer-body">
        <el-form ref="form" :model="form" :rules="rules" :label-width="isMobile ? '90px' : '96px'">
          <el-form-item label="标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入公告标题" />
          </el-form-item>
          <el-form-item label="分类" prop="category">
            <el-select v-model="form.category" placeholder="请选择分类" class="full-width" @change="onCategoryChange">
              <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="封面图" :prop="form.category === 'topic' ? 'coverImage' : ''">
            <image-upload v-model="form.coverImage" :limit="1" />
            <div v-if="form.category === 'topic'" class="field-tip">专题活动封面图必填，将在小程序首页展示。</div>
          </el-form-item>
          <el-form-item label="置顶">
            <el-switch v-model="form.topFlag" :active-value="1" :inactive-value="0" />
          </el-form-item>
          <el-form-item v-if="form.category === 'topic'" label="跳转链接">
            <el-input v-model="form.remark" placeholder="请输入跳转链接 URL" />
          </el-form-item>
          <el-form-item label="内容" prop="content">
            <editor v-model="form.content" :min-height="280" />
          </el-form-item>
        </el-form>
      </div>
      <div class="drawer-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确认</el-button>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listNotice, addNotice, editNotice, publishNotice, offlineNotice } from '@/api/jst/notice'
import { parseTime } from '@/utils/ruoyi'

const STATUS_META = {
  draft: { label: '草稿', type: 'info' },
  published: { label: '已发布', type: 'success' },
  offline: { label: '已下线', type: 'info' }
}

const CATEGORY_OPTIONS = [
  { label: '平台公告', value: 'platform' },
  { label: '赛事公告', value: 'contest' },
  { label: '积分公告', value: 'points' },
  { label: '商城公告', value: 'mall' },
  { label: '专题活动', value: 'topic' }
]

const STATUS_OPTIONS = [
  { label: '草稿', value: 'draft' },
  { label: '已发布', value: 'published' },
  { label: '已下线', value: 'offline' }
]

export default {
  name: 'JstNoticeManage',
  data() {
    return {
      loading: false,
      submitLoading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, title: null, category: null, status: null },
      categoryOptions: CATEGORY_OPTIONS,
      statusOptions: STATUS_OPTIONS,
      dialogVisible: false,
      dialogTitle: '',
      form: {},
      rules: {
        title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
        category: [{ required: true, message: '请选择分类', trigger: 'change' }],
        content: [{ required: true, message: '内容不能为空', trigger: 'blur' }],
        coverImage: [{ required: true, message: '专题活动封面图必填', trigger: 'change' }]
      }
    }
  },
  computed: {
    isMobile() { return this.$store.state.app.device === 'mobile' }
  },
  created() { this.getList() },
  methods: {
    parseTime,
    statusMeta(s) { return STATUS_META[s] || { label: s || '--', type: 'info' } },
    categoryLabel(c) {
      const item = CATEGORY_OPTIONS.find(o => o.value === c)
      return item ? item.label : c || '--'
    },
    getList() {
      this.loading = true
      listNotice(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    resetForm(ref) { this.$refs[ref] && this.$refs[ref].resetFields() },
    onCategoryChange() {
      if (this.form.category !== 'topic') {
        this.form.remark = ''
      }
    },
    handleAdd() {
      this.form = { title: '', category: 'platform', content: '', coverImage: '', topFlag: 0, remark: '' }
      this.dialogTitle = '新增公告'
      this.dialogVisible = true
      this.$nextTick(() => { this.$refs.form && this.$refs.form.clearValidate() })
    },
    handleEdit(row) {
      this.form = {
        noticeId: row.noticeId,
        title: row.title,
        category: row.category,
        content: row.content,
        coverImage: row.coverImage,
        topFlag: row.topFlag || 0,
        remark: row.remark || ''
      }
      this.dialogTitle = '编辑公告'
      this.dialogVisible = true
      this.$nextTick(() => { this.$refs.form && this.$refs.form.clearValidate() })
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.category === 'topic' && !this.form.coverImage) {
          this.$modal.msgWarning('专题活动封面图必填')
          return
        }
        this.submitLoading = true
        const api = this.form.noticeId ? editNotice : addNotice
        api(this.form).then(() => {
          this.$modal.msgSuccess(this.form.noticeId ? '修改成功' : '新增成功')
          this.dialogVisible = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    handlePublish(row) {
      this.$modal.confirm('确认发布公告「' + row.title + '」？').then(() => {
        return publishNotice(row.noticeId)
      }).then(() => {
        this.$modal.msgSuccess('发布成功')
        this.getList()
      }).catch(() => {})
    },
    handleOffline(row) {
      this.$modal.confirm('确认下线公告「' + row.title + '」？').then(() => {
        return offlineNotice(row.noticeId)
      }).then(() => {
        this.$modal.msgSuccess('下线成功')
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.jst-page {
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
  max-width: 70%;
}

.mobile-card__meta {
  font-size: 12px;
  color: #6b7280;
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.top-text {
  color: #e6a23c;
  font-weight: 600;
}

.mobile-card__actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.drawer-body {
  padding: 8px 20px 90px;
}

.drawer-footer {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 20px;
  border-top: 1px solid #e5eaf2;
  background: #fff;
}

.field-tip {
  color: #e6a23c;
  font-size: 12px;
  margin-top: 4px;
}

.full-width {
  width: 100%;
}

@media (max-width: 768px) {
  .jst-page {
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

  .drawer-body {
    padding: 8px 14px 96px;
  }

  .drawer-footer {
    padding: 10px 14px;
  }

  .drawer-footer .el-button {
    min-height: 44px;
  }
}
</style>
