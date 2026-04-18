<template>
  <span class="sales-tag-chip">
    <el-tag
      v-for="tag in tags"
      :key="tag.id"
      :color="tag.tagColor || undefined"
      size="mini"
      :closable="editable"
      @close="handleRemove(tag)"
      style="margin-right:4px"
    >{{ tag.tagCode }}</el-tag>

    <el-button
      v-if="editable"
      type="text"
      size="mini"
      icon="el-icon-plus"
      @click="dialogVisible = true"
    >添加</el-button>

    <!-- 添加标签弹窗 -->
    <el-dialog
      title="添加渠道标签"
      :visible.sync="dialogVisible"
      width="400px"
      append-to-body
      @closed="resetForm"
    >
      <el-form :model="form" label-width="80px" size="small">
        <el-form-item label="标签名称">
          <el-input v-model="form.tagCode" placeholder="输入标签名称（如 KA客户、高意向）" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="标签颜色">
          <el-color-picker v-model="form.tagColor" size="small" />
          <span v-if="form.tagColor" style="margin-left:8px;font-size:12px;color:#606266">{{ form.tagColor }}</span>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button size="small" @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" size="small" :loading="saving" @click="handleAdd">确定</el-button>
      </div>
    </el-dialog>
  </span>
</template>

<script>
import { listTags, addTag, removeTag } from '@/api/sales/me/tag'

export default {
  name: 'SalesTagChip',
  props: {
    channelId: {
      type: [Number, String],
      required: true
    },
    /** 是否可编辑（显示添加/删除按钮） */
    editable: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      tags: [],
      dialogVisible: false,
      saving: false,
      form: {
        tagCode: '',
        tagColor: '#409EFF'
      }
    }
  },
  created() {
    this.loadTags()
  },
  watch: {
    channelId() {
      this.loadTags()
    }
  },
  methods: {
    loadTags() {
      if (!this.channelId) return
      listTags(this.channelId).then(res => {
        this.tags = res.data || []
      }).catch(() => {})
    },
    handleRemove(tag) {
      this.$confirm(`确定删除标签"${tag.tagCode}"？`, '提示', { type: 'warning' }).then(() => {
        removeTag(tag.id).then(() => {
          this.$message.success('已删除')
          this.loadTags()
        }).catch(e => this.$message.error(e.msg || '删除失败'))
      }).catch(() => {})
    },
    handleAdd() {
      if (!this.form.tagCode.trim()) {
        this.$message.warning('请输入标签名称')
        return
      }
      this.saving = true
      addTag({
        channelId: this.channelId,
        tagCode: this.form.tagCode.trim(),
        tagColor: this.form.tagColor
      }).then(() => {
        this.$message.success('已添加')
        this.dialogVisible = false
        this.loadTags()
      }).catch(e => this.$message.error(e.msg || '添加失败')).finally(() => {
        this.saving = false
      })
    },
    resetForm() {
      this.form = { tagCode: '', tagColor: '#409EFF' }
    }
  }
}
</script>

<style scoped>
.sales-tag-chip {
  display: inline-flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
}
</style>
