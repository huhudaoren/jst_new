<template>
  <div class="app-container jst-course-edit">
    <div class="edit-hero">
      <div>
        <p class="hero-eyebrow">Course Studio</p>
        <h2>{{ isEdit ? '编辑课程' : '新建课程' }}</h2>
        <p class="hero-desc">按 5 个标签完成课程配置，保存与提交链路保持不变。</p>
      </div>
      <div class="hero-actions">
        <el-popover placement="bottom" width="280" trigger="hover">
          <div class="help-lines">
            <p>1. 按标签逐步填写即可完成课程配置。</p>
            <p>2. 目录支持章节与课时排序。</p>
            <p>3. 保存逻辑与原页面完全一致。</p>
          </div>
          <el-button slot="reference" icon="el-icon-question" plain>填写说明</el-button>
        </el-popover>
        <el-button icon="el-icon-back" @click="goBack">返回列表</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存课程</el-button>
      </div>
    </div>

    <el-form ref="form" :model="form" :rules="rules" label-width="100px" class="edit-form">
      <el-tabs class="edit-tabs">
        <el-tab-pane label="基本信息">
          <p class="tab-help">填写课程名称、价格与可见范围，用于课程前台展示与购买。</p>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12">
              <el-form-item label="课程名称" prop="courseName">
                <el-input v-model="form.courseName" placeholder="请输入课程名称" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="课程类型" prop="courseType">
                <el-select v-model="form.courseType" placeholder="请选择" class="full-width">
                  <el-option label="普通课程" value="normal" />
                  <el-option label="AI课程" value="ai_maic" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="封面图">
                <image-upload v-model="form.coverImage" :limit="1" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="简介">
                <el-input v-model="form.description" type="textarea" :rows="3" placeholder="课程简介" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="8">
              <el-form-item label="现金价格">
                <el-input-number v-model="form.price" :min="0" :precision="2" controls-position="right" class="full-width" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="8">
              <el-form-item label="积分价格">
                <el-input-number v-model="form.pointsPrice" :min="0" :precision="0" controls-position="right" class="full-width" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="8">
              <el-form-item label="可见范围">
                <el-select v-model="form.visibleScope" placeholder="请选择" class="full-width">
                  <el-option label="全部可见" value="all" />
                  <el-option label="仅学生" value="student" />
                  <el-option label="仅渠道" value="channel" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-tab-pane>

        <el-tab-pane label="讲师信息">
          <p class="tab-help">讲师信息会展示在课程详情页，建议完善头像、姓名与简介。</p>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="8">
              <el-form-item label="讲师姓名">
                <el-input v-model="form.teacherName" placeholder="请输入讲师姓名" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="8">
              <el-form-item label="讲师头像">
                <image-upload v-model="form.teacherAvatar" :limit="1" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="8">
              <el-form-item label="讲师简介">
                <el-input v-model="form.teacherDesc" type="textarea" :rows="3" placeholder="讲师简介" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-tab-pane>

        <el-tab-pane label="课程目录">
          <p class="tab-help">支持章节和课时增删改排序，原有 `add/move/remove` 方法继续生效。</p>
          <div class="catalog-head">
            <el-button type="primary" size="mini" icon="el-icon-plus" @click="addChapter">添加章节</el-button>
          </div>
          <div v-if="chapters.length === 0" class="catalog-empty">暂无章节，请先添加章节。</div>
          <div v-for="(chapter, ci) in chapters" :key="ci" class="chapter-block">
            <div class="chapter-header">
              <el-input v-model="chapter.name" placeholder="章节名称" size="small" class="chapter-input" />
              <span class="chapter-ops">
                <el-button type="text" size="mini" @click="addLesson(ci)">添加课时</el-button>
                <el-button type="text" size="mini" class="danger-text" @click="removeChapter(ci)">删除章节</el-button>
                <el-button v-if="ci > 0" type="text" size="mini" icon="el-icon-top" @click="moveChapter(ci, -1)" />
                <el-button v-if="ci < chapters.length - 1" type="text" size="mini" icon="el-icon-bottom" @click="moveChapter(ci, 1)" />
              </span>
            </div>
            <el-table v-if="chapter.lessons.length" :data="chapter.lessons" size="mini" border class="lesson-table">
              <el-table-column label="课时名称" min-width="180">
                <template slot-scope="{ row }">
                  <el-input v-model="row.name" size="mini" placeholder="课时名称" />
                </template>
              </el-table-column>
              <el-table-column label="时长" width="120">
                <template slot-scope="{ row }">
                  <el-input v-model="row.duration" size="mini" placeholder="如 15:30" />
                </template>
              </el-table-column>
              <el-table-column label="免费" width="70">
                <template slot-scope="{ row }">
                  <el-switch v-model="row.free" :active-value="true" :inactive-value="false" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template slot-scope="{ $index }">
                  <el-button type="text" size="mini" class="danger-text" @click="removeLesson(ci, $index)">删除</el-button>
                  <el-button v-if="$index > 0" type="text" size="mini" icon="el-icon-top" @click="moveLesson(ci, $index, -1)" />
                  <el-button v-if="$index < chapter.lessons.length - 1" type="text" size="mini" icon="el-icon-bottom" @click="moveLesson(ci, $index, 1)" />
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <el-tab-pane label="课程介绍">
          <p class="tab-help">这里填写课程图文详情，用户会在课程详情页看到该内容。</p>
          <editor v-model="form.remark" :min-height="300" />
        </el-tab-pane>

        <el-tab-pane label="统计信息">
          <p class="tab-help">维护课时数与总时长，学习人数仅作展示，系统会自动累计。</p>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="8">
              <el-form-item label="课时数">
                <el-input-number v-model="form.lessonCount" :min="0" controls-position="right" class="full-width" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="8">
              <el-form-item label="总时长">
                <el-input v-model="form.totalDuration" placeholder="如：12小时30分" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="8">
              <el-form-item label="学习人数">
                <el-input-number v-model="form.learnerCount" :min="0" controls-position="right" disabled class="full-width" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>

      <div class="action-row">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </div>
    </el-form>
  </div>
</template>

<script>
import { getCourse, addCourse, editCourse } from '@/api/jst/course'

export default {
  name: 'JstCourseEdit',
  data() {
    return {
      isEdit: false,
      submitLoading: false,
      form: {
        courseName: '',
        courseType: 'normal',
        coverImage: '',
        description: '',
        price: 0,
        pointsPrice: 0,
        lessonCount: 0,
        learnerCount: 0,
        totalDuration: '',
        teacherName: '',
        teacherAvatar: '',
        teacherDesc: '',
        visibleScope: 'all',
        remark: ''
      },
      chapters: [],
      rules: {
        courseName: [{ required: true, message: '课程名称不能为空', trigger: 'blur' }],
        courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }]
      }
    }
  },
  computed: {
    isMobile() { return this.$store.state.app.device === 'mobile' }
  },
  created() {
    const courseId = this.$route.params.courseId
    if (courseId) {
      this.isEdit = true
      this.loadCourse(courseId)
    }
  },
  methods: {
    goBack() { this.$router.go(-1) },
    loadCourse(courseId) {
      getCourse(courseId).then(res => {
        const d = res.data || res
        this.form = {
          courseId: d.courseId,
          courseName: d.courseName || '',
          courseType: d.courseType || 'normal',
          coverImage: d.coverImage || '',
          description: d.description || '',
          price: d.price || 0,
          pointsPrice: d.pointsPrice || 0,
          lessonCount: d.lessonCount || 0,
          learnerCount: d.learnerCount || 0,
          totalDuration: d.totalDuration || '',
          teacherName: d.teacherName || '',
          teacherAvatar: d.teacherAvatar || '',
          teacherDesc: d.teacherDesc || '',
          visibleScope: d.visibleScope || 'all',
          remark: d.remark || ''
        }
        try {
          this.chapters = typeof d.chaptersJson === 'string' ? JSON.parse(d.chaptersJson) : (d.chaptersJson || [])
        } catch (e) {
          this.$modal.msgError('章节数据解析失败')
          this.chapters = []
        }
      })
    },
    addChapter() { this.chapters.push({ name: '', lessons: [] }) },
    removeChapter(ci) { this.chapters.splice(ci, 1) },
    moveChapter(ci, dir) {
      const target = ci + dir
      const temp = this.chapters.splice(ci, 1)[0]
      this.chapters.splice(target, 0, temp)
    },
    addLesson(ci) { this.chapters[ci].lessons.push({ name: '', duration: '', free: false }) },
    removeLesson(ci, li) { this.chapters[ci].lessons.splice(li, 1) },
    moveLesson(ci, li, dir) {
      const lessons = this.chapters[ci].lessons
      const target = li + dir
      const temp = lessons.splice(li, 1)[0]
      lessons.splice(target, 0, temp)
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const data = { ...this.form, chaptersJson: JSON.stringify(this.chapters) }
        const api = this.isEdit ? editCourse : addCourse
        api(data).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.goBack()
        }).finally(() => { this.submitLoading = false })
      })
    }
  }
}
</script>

<style scoped>
.jst-course-edit {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.edit-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 22px 24px;
  margin-bottom: 16px;
  color: #fff;
  border-radius: 12px;
  background: linear-gradient(135deg, #0f766e 0%, #2563eb 55%, #0f172a 100%);
}

.hero-eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: .08em;
  opacity: .8;
}

.edit-hero h2 {
  margin: 0;
  font-size: 24px;
}

.hero-desc {
  margin: 8px 0 0;
  color: rgba(255, 255, 255, 0.84);
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

.edit-form {
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 10px;
  padding: 16px;
}

.tab-help {
  margin: 0 0 14px;
  padding: 10px 12px;
  background: #f8fafc;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
  color: #6b7280;
  line-height: 1.65;
}

.catalog-head {
  margin-bottom: 10px;
}

.catalog-empty {
  padding: 18px;
  margin-bottom: 12px;
  text-align: center;
  color: #909399;
  background: #f8fafc;
  border-radius: 8px;
}

.chapter-block {
  background: #f8f9fb;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
}

.chapter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.chapter-input {
  width: 280px;
  max-width: 60%;
}

.chapter-ops {
  display: flex;
  gap: 4px;
  align-items: center;
}

.lesson-table {
  margin-top: 8px;
}

.full-width {
  width: 100%;
}

.danger-text {
  color: #f56c6c !important;
}

.action-row {
  margin-top: 14px;
  text-align: center;
}

@media (max-width: 768px) {
  .jst-course-edit {
    padding: 12px;
  }

  .edit-hero {
    flex-direction: column;
    align-items: flex-start;
    padding: 18px;
  }

  .edit-hero h2 {
    font-size: 20px;
  }

  .hero-actions {
    width: 100%;
  }

  .hero-actions .el-button {
    flex: 1;
    min-height: 44px;
  }

  .edit-form {
    padding: 12px;
  }

  .chapter-input {
    width: 100%;
    max-width: 100%;
  }

  .action-row .el-button {
    min-height: 44px;
    min-width: 120px;
  }

  .edit-tabs ::v-deep .el-tabs__item {
    padding: 0 12px;
  }
}
</style>
