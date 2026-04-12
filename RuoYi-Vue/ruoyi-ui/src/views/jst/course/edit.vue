<template>
  <div class="app-container">
    <el-page-header @back="goBack" :content="isEdit ? '编辑课程' : '新增课程'" style="margin-bottom: 20px" />

    <el-form ref="form" :model="form" :rules="rules" :label-width="isMobile ? '80px' : '100px'">
      <!-- 基本信息 -->
      <el-card shadow="hover" style="margin-bottom:16px">
        <div slot="header">基本信息</div>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12">
            <el-form-item label="课程名称" prop="courseName">
              <el-input v-model="form.courseName" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="课程类型" prop="courseType">
              <el-select v-model="form.courseType" placeholder="请选择">
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
              <el-input-number v-model="form.price" :min="0" :precision="2" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="积分价格">
              <el-input-number v-model="form.pointsPrice" :min="0" :precision="0" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="可见范围">
              <el-select v-model="form.visibleScope" placeholder="请选择">
                <el-option label="全部可见" value="all" />
                <el-option label="仅学生" value="student" />
                <el-option label="仅渠道" value="channel" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 统计信息 -->
      <el-card shadow="hover" style="margin-bottom:16px">
        <div slot="header">统计信息</div>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="8">
            <el-form-item label="课时数">
              <el-input-number v-model="form.lessonCount" :min="0" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="总时长">
              <el-input v-model="form.totalDuration" placeholder="如：12小时30分" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="学习人数">
              <el-input-number v-model="form.learnerCount" :min="0" controls-position="right" disabled style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 讲师信息 -->
      <el-card shadow="hover" style="margin-bottom:16px">
        <div slot="header">讲师信息</div>
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
      </el-card>

      <!-- 课程目录 -->
      <el-card shadow="hover" style="margin-bottom:16px">
        <div slot="header">
          <span>课程目录</span>
          <el-button type="primary" size="mini" icon="el-icon-plus" style="float:right" @click="addChapter">添加章节</el-button>
        </div>
        <div v-if="chapters.length === 0" style="text-align:center;color:#909399;padding:20px">暂无章节，请点击"添加章节"</div>
        <div v-for="(chapter, ci) in chapters" :key="ci" class="chapter-block">
          <div class="chapter-header">
            <el-input v-model="chapter.name" placeholder="章节名称" size="small" style="width:280px;max-width:60%" />
            <span class="chapter-ops">
              <el-button type="text" size="mini" @click="addLesson(ci)">添加课时</el-button>
              <el-button type="text" size="mini" class="danger-text" @click="removeChapter(ci)">删除章节</el-button>
              <el-button v-if="ci > 0" type="text" size="mini" icon="el-icon-top" @click="moveChapter(ci, -1)" />
              <el-button v-if="ci < chapters.length - 1" type="text" size="mini" icon="el-icon-bottom" @click="moveChapter(ci, 1)" />
            </span>
          </div>
          <el-table :data="chapter.lessons" size="mini" border style="margin-top:8px" v-if="chapter.lessons.length">
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
            <el-table-column label="操作" width="100">
              <template slot-scope="{ $index }">
                <el-button type="text" size="mini" class="danger-text" @click="removeLesson(ci, $index)">删除</el-button>
                <el-button v-if="$index > 0" type="text" size="mini" icon="el-icon-top" @click="moveLesson(ci, $index, -1)" />
                <el-button v-if="$index < chapter.lessons.length - 1" type="text" size="mini" icon="el-icon-bottom" @click="moveLesson(ci, $index, 1)" />
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>

      <!-- 课程介绍（富文本）-->
      <el-card shadow="hover" style="margin-bottom:16px">
        <div slot="header">课程介绍</div>
        <editor v-model="form.remark" :min-height="260" />
      </el-card>

      <!-- 提交 -->
      <div style="text-align:center;padding:20px 0">
        <el-button @click="goBack">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保 存</el-button>
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
        } catch (e) { this.chapters = [] }
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
.chapter-block { background: #f8f9fb; border-radius: 8px; padding: 12px; margin-bottom: 12px; }
.chapter-header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 8px; }
.chapter-ops { display: flex; gap: 4px; align-items: center; }
.danger-text { color: #F56C6C !important; }
</style>
