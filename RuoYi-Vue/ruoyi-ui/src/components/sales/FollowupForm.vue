<template>
  <el-dialog
    :title="record ? '编辑跟进记录' : '新建跟进记录'"
    :visible="visible"
    width="560px"
    append-to-body
    @update:visible="$emit('update:visible', $event)"
    @closed="resetForm"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="90px" size="small">
      <el-form-item label="跟进方式" prop="followupType">
        <el-select v-model="form.followupType" placeholder="请选择" style="width:100%">
          <el-option
            v-for="d in followupTypeOptions"
            :key="d.dictValue"
            :label="d.dictLabel"
            :value="d.dictValue"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="跟进时间" prop="followupAt">
        <el-date-picker
          v-model="form.followupAt"
          type="datetime"
          placeholder="选择时间"
          value-format="yyyy-MM-dd HH:mm:ss"
          style="width:100%"
        />
      </el-form-item>

      <el-form-item label="跟进心情" prop="mood">
        <el-radio-group v-model="form.mood">
          <el-radio
            v-for="d in moodOptions"
            :key="d.dictValue"
            :label="d.dictValue"
          >{{ d.dictLabel }}</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="跟进内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="4"
          maxlength="1000"
          show-word-limit
          placeholder="描述本次跟进内容..."
        />
      </el-form-item>

      <el-form-item label="下次跟进">
        <el-date-picker
          v-model="form.nextFollowupAt"
          type="date"
          placeholder="选择日期（选填）"
          value-format="yyyy-MM-dd"
          style="width:100%"
        />
      </el-form-item>
    </el-form>

    <div slot="footer">
      <el-button size="small" @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" size="small" :loading="saving" @click="handleSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { create, update } from '@/api/sales/me/followup'

export default {
  name: 'FollowupForm',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    channelId: {
      type: [Number, String],
      required: true
    },
    /** 编辑模式传入已有记录对象，新建时不传 */
    record: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      saving: false,
      followupTypeOptions: [],
      moodOptions: [],
      form: {
        followupType: '',
        followupAt: '',
        mood: '',
        content: '',
        nextFollowupAt: ''
      },
      rules: {
        followupType: [{ required: true, message: '请选择跟进方式', trigger: 'change' }],
        followupAt: [{ required: true, message: '请选择跟进时间', trigger: 'change' }],
        content: [{ required: true, message: '请填写跟进内容', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getDicts('jst_sales_followup_type').then(res => { this.followupTypeOptions = res.data })
    this.getDicts('jst_sales_mood').then(res => { this.moodOptions = res.data })
  },
  watch: {
    visible(val) {
      if (val && this.record) {
        this.form = {
          followupType: this.record.followupType || '',
          followupAt: this.record.followupAt || '',
          mood: this.record.mood || '',
          content: this.record.content || '',
          nextFollowupAt: this.record.nextFollowupAt || ''
        }
      }
    }
  },
  methods: {
    resetForm() {
      this.form = { followupType: '', followupAt: '', mood: '', content: '', nextFollowupAt: '' }
      this.$refs.form && this.$refs.form.resetFields()
    },
    handleSave() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.saving = true
        const payload = { ...this.form, channelId: this.channelId }
        const req = this.record
          ? update(this.record.recordId, payload)
          : create(payload)
        req.then(() => {
          this.$message.success('保存成功')
          this.$emit('update:visible', false)
          this.$emit('saved')
        }).catch(e => this.$message.error(e.msg || '保存失败')).finally(() => {
          this.saving = false
        })
      })
    }
  }
}
</script>
