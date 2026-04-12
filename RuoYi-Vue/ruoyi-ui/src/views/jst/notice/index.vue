<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="68px">
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
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['jst:message:notice:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 手机卡片 -->
    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.noticeId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.title }}</span>
            <el-tag size="mini" :type="statusMeta(row.status).type">{{ statusMeta(row.status).label }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>{{ categoryLabel(row.category) }}</span>
            <span v-if="row.topFlag === 1" style="color:#E6A23C">置顶</span>
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

    <!-- 桌面表格 -->
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

    <!-- 编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" :width="isMobile ? '100%' : '760px'" :fullscreen="isMobile" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" @change="onCategoryChange">
            <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面图" :prop="form.category === 'topic' ? 'coverImage' : ''">
          <image-upload v-model="form.coverImage" :limit="1" />
          <div v-if="form.category === 'topic'" class="el-form-item__tip" style="color:#E6A23C;font-size:12px">专题活动封面图必填，将在小程序首页展示</div>
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="form.topFlag" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item v-if="form.category === 'topic'" label="跳转链接">
          <el-input v-model="form.remark" placeholder="请输入跳转链接 URL" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <editor v-model="form.content" :min-height="260" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>
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
.mobile-card-list { padding: 0 4px; }
.mobile-card { background: #fff; border-radius: 8px; padding: 12px 14px; margin-bottom: 10px; box-shadow: 0 1px 4px rgba(0,0,0,.06); }
.mobile-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.mobile-card__title { font-weight: 600; font-size: 14px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 70%; }
.mobile-card__meta { font-size: 12px; color: #909399; display: flex; gap: 12px; margin-bottom: 8px; }
.mobile-card__actions { display: flex; gap: 6px; }
</style>
