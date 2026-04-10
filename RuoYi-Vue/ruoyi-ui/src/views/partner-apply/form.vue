<template>
  <div class="partner-form-page">
    <div class="page-header">
      <div class="header-inner">
        <div>
          <div class="page-tag">赛事方公开申请</div>
          <h1>填写入驻资料</h1>
          <p>请按步骤补充机构信息、联系人信息与资质附件。提交成功后，系统会生成申请单号用于后续查询。</p>
        </div>
        <el-button plain @click="$router.push('/partner-apply')">返回入口</el-button>
      </div>
    </div>

    <div class="page-body">
      <div class="form-shell">
        <el-alert
          v-if="isResubmit"
          title="当前为重新发起申请。系统将按新申请单重新提交，不会覆盖历史申请记录。"
          type="warning"
          :closable="false"
          show-icon
          class="top-alert"
        />

        <el-steps :active="activeStep" :direction="isMobile ? 'vertical' : 'horizontal'" finish-status="success" class="steps">
          <el-step title="基本信息" />
          <el-step title="联系人信息" />
          <el-step title="资质材料" />
          <el-step title="确认提交" />
        </el-steps>

        <el-form
          ref="applyForm"
          :model="form"
          :rules="rules"
          :label-position="isMobile ? 'top' : 'left'"
          label-width="120px"
          class="apply-form"
        >
          <div v-show="activeStep === 0" class="step-panel">
            <div class="panel-title">Step 1 - 基本信息</div>
            <el-row :gutter="20">
              <el-col :xs="24" :sm="24" :md="12">
                <el-form-item label="机构名称" prop="institutionName">
                  <el-input v-model.trim="form.institutionName" maxlength="128" show-word-limit placeholder="请输入机构全称" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="12">
                <el-form-item label="统一社会信用代码" prop="socialCreditCode">
                  <el-input v-model.trim="form.socialCreditCode" maxlength="18" placeholder="请输入 18 位统一社会信用代码" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="12">
                <el-form-item label="机构类型" prop="institutionType">
                  <el-select v-model="form.institutionType" placeholder="请选择机构类型" style="width: 100%">
                    <el-option label="赛事方" value="event_organizer" />
                    <el-option label="培训机构" value="training_org" />
                    <el-option label="其他" value="other" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="12">
                <el-form-item label="所在省份" prop="province">
                  <el-input v-model.trim="form.province" maxlength="32" placeholder="例如：浙江省" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="12">
                <el-form-item label="所在城市" prop="city">
                  <el-input v-model.trim="form.city" maxlength="32" placeholder="例如：杭州市" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="12">
                <el-form-item label="所在区县" prop="district">
                  <el-input v-model.trim="form.district" maxlength="32" placeholder="例如：西湖区" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="详细地址" prop="detailAddress">
                  <el-input
                    v-model.trim="form.detailAddress"
                    type="textarea"
                    :rows="3"
                    maxlength="200"
                    show-word-limit
                    placeholder="请输入机构详细地址"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <div v-show="activeStep === 1" class="step-panel">
            <div class="panel-title">Step 2 - 联系人信息</div>
            <el-row :gutter="20">
              <el-col :xs="24" :sm="24" :md="12">
                <el-form-item label="法人姓名" prop="legalPersonName">
                  <el-input v-model.trim="form.legalPersonName" maxlength="32" placeholder="请输入法人姓名" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="12">
                <el-form-item label="法人身份证号" prop="legalPersonIdNo">
                  <el-input v-model.trim="form.legalPersonIdNo" maxlength="18" placeholder="请输入法人身份证号" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="12">
                <el-form-item label="联系人姓名" prop="contactName">
                  <el-input v-model.trim="form.contactName" maxlength="32" placeholder="请输入联系人姓名" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="12">
                <el-form-item label="联系人手机号" prop="contactMobile">
                  <el-input v-model.trim="form.contactMobile" maxlength="11" placeholder="请输入联系人手机号" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="联系人邮箱" prop="contactEmail">
                  <el-input v-model.trim="form.contactEmail" maxlength="64" placeholder="请输入联系人邮箱" />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <div v-show="activeStep === 2" class="step-panel">
            <div class="panel-title">Step 3 - 资质材料上传</div>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="营业执照照片" prop="businessLicenseFiles">
                  <div class="upload-card">
                    <div class="upload-copy">
                      <strong>必传</strong>
                      <span>支持 JPG / PNG / PDF，单个文件不超过 10MB。</span>
                    </div>
                    <el-upload
                      action="#"
                      :show-file-list="false"
                      :http-request="option => handleUpload(option, 'businessLicenseFiles', 1)"
                      :before-upload="file => beforeUpload(file, 'businessLicenseFiles', 1)"
                      accept=".jpg,.jpeg,.png,.pdf"
                    >
                      <el-button size="mini" type="primary" :loading="uploading.businessLicenseFiles">上传文件</el-button>
                    </el-upload>
                  </div>
                  <div class="file-list">
                    <div v-for="file in form.businessLicenseFiles" :key="file.uid" class="file-item">
                      <div class="file-meta">
                        <span class="file-name">{{ file.name }}</span>
                        <a v-if="file.previewUrl" :href="file.previewUrl" target="_blank" rel="noopener noreferrer">预览</a>
                      </div>
                      <el-button type="text" @click="removeFile('businessLicenseFiles', file.uid)">删除</el-button>
                    </div>
                  </div>
                </el-form-item>
              </el-col>

              <el-col :span="24">
                <el-form-item label="组织机构代码证" prop="organizationCodeFiles">
                  <div class="upload-card">
                    <div class="upload-copy">
                      <strong>选传</strong>
                      <span>如有组织机构代码证，可一并上传。</span>
                    </div>
                    <el-upload
                      action="#"
                      :show-file-list="false"
                      :http-request="option => handleUpload(option, 'organizationCodeFiles', 1)"
                      :before-upload="file => beforeUpload(file, 'organizationCodeFiles', 1)"
                      accept=".jpg,.jpeg,.png,.pdf"
                    >
                      <el-button size="mini" plain :loading="uploading.organizationCodeFiles">上传附件</el-button>
                    </el-upload>
                  </div>
                  <div class="file-list">
                    <div v-for="file in form.organizationCodeFiles" :key="file.uid" class="file-item">
                      <div class="file-meta">
                        <span class="file-name">{{ file.name }}</span>
                        <a v-if="file.previewUrl" :href="file.previewUrl" target="_blank" rel="noopener noreferrer">预览</a>
                      </div>
                      <el-button type="text" @click="removeFile('organizationCodeFiles', file.uid)">删除</el-button>
                    </div>
                  </div>
                </el-form-item>
              </el-col>

              <el-col :span="24">
                <el-form-item label="其他证明材料" prop="otherFiles">
                  <div class="upload-card">
                    <div class="upload-copy">
                      <strong>选传</strong>
                      <span>可上传赛事举办经验证明、合作资质等补充材料，最多 6 个附件。</span>
                    </div>
                    <el-upload
                      action="#"
                      :show-file-list="false"
                      :http-request="option => handleUpload(option, 'otherFiles', 6)"
                      :before-upload="file => beforeUpload(file, 'otherFiles', 6)"
                      accept=".jpg,.jpeg,.png,.pdf"
                    >
                      <el-button size="mini" plain :loading="uploading.otherFiles">添加附件</el-button>
                    </el-upload>
                  </div>
                  <div class="file-list">
                    <div v-for="file in form.otherFiles" :key="file.uid" class="file-item">
                      <div class="file-meta">
                        <span class="file-name">{{ file.name }}</span>
                        <a v-if="file.previewUrl" :href="file.previewUrl" target="_blank" rel="noopener noreferrer">预览</a>
                      </div>
                      <el-button type="text" @click="removeFile('otherFiles', file.uid)">删除</el-button>
                    </div>
                  </div>
                </el-form-item>
              </el-col>

              <el-col :span="24">
                <el-form-item label="赛事举办经验说明" prop="experience">
                  <el-input
                    v-model.trim="form.experience"
                    type="textarea"
                    :rows="5"
                    maxlength="500"
                    show-word-limit
                    placeholder="请简要介绍机构的赛事、培训或合作经验"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <div v-show="activeStep === 3" class="step-panel">
            <div class="panel-title">Step 4 - 确认提交</div>
            <div class="summary-grid">
              <div class="summary-card">
                <div class="summary-title">基本信息</div>
                <div class="summary-item"><span>机构名称</span><strong>{{ form.institutionName || '—' }}</strong></div>
                <div class="summary-item"><span>统一社会信用代码</span><strong>{{ form.socialCreditCode || '—' }}</strong></div>
                <div class="summary-item"><span>机构类型</span><strong>{{ institutionTypeLabel(form.institutionType) }}</strong></div>
                <div class="summary-item"><span>所在地区</span><strong>{{ areaText || '—' }}</strong></div>
                <div class="summary-item full"><span>详细地址</span><strong>{{ form.detailAddress || '—' }}</strong></div>
              </div>
              <div class="summary-card">
                <div class="summary-title">联系人信息</div>
                <div class="summary-item"><span>法人姓名</span><strong>{{ form.legalPersonName || '—' }}</strong></div>
                <div class="summary-item"><span>法人身份证号</span><strong>{{ form.legalPersonIdNo || '—' }}</strong></div>
                <div class="summary-item"><span>联系人姓名</span><strong>{{ form.contactName || '—' }}</strong></div>
                <div class="summary-item"><span>联系人手机号</span><strong>{{ form.contactMobile || '—' }}</strong></div>
                <div class="summary-item full"><span>联系人邮箱</span><strong>{{ form.contactEmail || '—' }}</strong></div>
              </div>
              <div class="summary-card full-width">
                <div class="summary-title">附件与经验说明</div>
                <div class="summary-files">
                  <div class="summary-file-block">
                    <span class="block-label">营业执照</span>
                    <span>{{ fileNames(form.businessLicenseFiles) }}</span>
                  </div>
                  <div class="summary-file-block">
                    <span class="block-label">组织机构代码证</span>
                    <span>{{ fileNames(form.organizationCodeFiles) }}</span>
                  </div>
                  <div class="summary-file-block">
                    <span class="block-label">其他证明材料</span>
                    <span>{{ fileNames(form.otherFiles) }}</span>
                  </div>
                  <div class="summary-file-block full">
                    <span class="block-label">经验说明</span>
                    <span>{{ form.experience || '未填写' }}</span>
                  </div>
                </div>
              </div>
            </div>

            <el-form-item prop="agreementAccepted" class="agreement-item">
              <el-checkbox v-model="form.agreementAccepted">
                我已阅读并同意《赛事方合作协议》，确认所填资料真实有效。
              </el-checkbox>
            </el-form-item>
          </div>
        </el-form>

        <div class="action-bar">
          <el-button v-if="activeStep > 0" @click="prevStep">上一步</el-button>
          <el-button v-if="activeStep < 3" type="primary" @click="nextStep">下一步</el-button>
          <el-button v-else type="primary" :loading="submitting" @click="submit">提交申请</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import cache from '@/plugins/cache'
import { submitPartnerApply, uploadPartnerApplyFile } from '@/api/partner/apply'

const LAST_APPLY_NO_KEY = 'partnerApply:lastApplyNo'
const LAST_DRAFT_KEY = 'partnerApply:lastDraft'

const createInitialForm = () => ({
  institutionName: '',
  socialCreditCode: '',
  institutionType: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  legalPersonName: '',
  legalPersonIdNo: '',
  contactName: '',
  contactMobile: '',
  contactEmail: '',
  businessLicenseFiles: [],
  organizationCodeFiles: [],
  otherFiles: [],
  experience: '',
  agreementAccepted: false
})

export default {
  name: 'PartnerApplyForm',
  data() {
    const validateArrayRequired = message => (rule, value, callback) => {
      if (Array.isArray(value) && value.length > 0) {
        callback()
        return
      }
      callback(new Error(message))
    }

    return {
      activeStep: 0,
      isMobile: window.innerWidth < 768,
      isResubmit: false,
      submitting: false,
      form: createInitialForm(),
      uploading: {
        businessLicenseFiles: false,
        organizationCodeFiles: false,
        otherFiles: false
      },
      rules: {
        institutionName: [{ required: true, message: '请输入机构名称', trigger: 'blur' }],
        socialCreditCode: [
          { required: true, message: '请输入统一社会信用代码', trigger: 'blur' },
          { pattern: /^[0-9A-Z]{18}$/, message: '统一社会信用代码需为 18 位大写字母或数字', trigger: 'blur' }
        ],
        institutionType: [{ required: true, message: '请选择机构类型', trigger: 'change' }],
        province: [{ required: true, message: '请输入所在省份', trigger: 'blur' }],
        city: [{ required: true, message: '请输入所在城市', trigger: 'blur' }],
        district: [{ required: true, message: '请输入所在区县', trigger: 'blur' }],
        detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
        legalPersonName: [{ required: true, message: '请输入法人姓名', trigger: 'blur' }],
        legalPersonIdNo: [
          { required: true, message: '请输入法人身份证号', trigger: 'blur' },
          { pattern: /^(\d{15}|\d{17}[\dXx])$/, message: '法人身份证号格式不正确', trigger: 'blur' }
        ],
        contactName: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
        contactMobile: [
          { required: true, message: '请输入联系人手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        contactEmail: [
          { required: true, message: '请输入联系人邮箱', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] }
        ],
        businessLicenseFiles: [
          { validator: validateArrayRequired('请上传营业执照照片或 PDF'), trigger: 'change' }
        ],
        agreementAccepted: [
          {
            validator: (rule, value, callback) => {
              if (value) {
                callback()
                return
              }
              callback(new Error('请先同意赛事方合作协议'))
            },
            trigger: 'change'
          }
        ]
      }
    }
  },
  computed: {
    areaText() {
      return [this.form.province, this.form.city, this.form.district].filter(Boolean).join(' / ')
    },
    stepFields() {
      return [
        ['institutionName', 'socialCreditCode', 'institutionType', 'province', 'city', 'district', 'detailAddress'],
        ['legalPersonName', 'legalPersonIdNo', 'contactName', 'contactMobile', 'contactEmail'],
        ['businessLicenseFiles'],
        ['agreementAccepted']
      ]
    }
  },
  created() {
    this.isResubmit = this.$route.query.mode === 'resubmit'
    const lastDraft = cache.local.getJSON(LAST_DRAFT_KEY)
    if (this.isResubmit && lastDraft) {
      this.form = this.normalizeDraft(lastDraft)
    }
  },
  mounted() {
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    handleResize() {
      this.isMobile = window.innerWidth < 768
    },
    nextStep() {
      this.validateStep(this.stepFields[this.activeStep]).then(valid => {
        if (!valid) {
          return
        }
        this.activeStep += 1
      })
    },
    prevStep() {
      if (this.activeStep > 0) {
        this.activeStep -= 1
      }
    },
    validateStep(fields) {
      if (!fields || fields.length === 0) {
        return Promise.resolve(true)
      }
      const tasks = fields.map(field => {
        return new Promise(resolve => {
          this.$refs.applyForm.validateField(field, error => resolve(!error))
        })
      })
      return Promise.all(tasks).then(results => results.every(Boolean))
    },
    beforeUpload(file, field, limit) {
      const extension = file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase()
      const allowed = ['jpg', 'jpeg', 'png', 'pdf']
      if (!allowed.includes(extension)) {
        this.$modal.msgError('仅支持 JPG、PNG、PDF 格式附件')
        return false
      }
      if (file.size / 1024 / 1024 > 10) {
        this.$modal.msgError('附件大小不能超过 10MB')
        return false
      }
      if ((this.form[field] || []).length >= limit) {
        this.$modal.msgError(`该栏位最多上传 ${limit} 个附件`)
        return false
      }
      return true
    },
    handleUpload(option, field, limit) {
      if ((this.form[field] || []).length >= limit) {
        option.onError(new Error('limit exceeded'))
        return
      }
      this.uploading[field] = true
      uploadPartnerApplyFile(option.file).then(res => {
        const data = res.data || {}
        const fileItem = {
          uid: `${field}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
          name: data.originalFilename || option.file.name,
          objectKey: data.objectKey,
          previewUrl: data.url || ''
        }
        this.form[field].push(fileItem)
        this.$refs.applyForm.validateField(field)
        this.$modal.msgSuccess('附件上传成功')
        option.onSuccess(res, option.file)
      }).catch(error => {
        option.onError(error)
      }).finally(() => {
        this.uploading[field] = false
      })
    },
    removeFile(field, uid) {
      this.form[field] = (this.form[field] || []).filter(item => item.uid !== uid)
      this.$refs.applyForm.validateField(field)
    },
    institutionTypeLabel(value) {
      const map = {
        event_organizer: '赛事方',
        training_org: '培训机构',
        other: '其他'
      }
      return map[value] || '—'
    },
    fileNames(files) {
      if (!files || files.length === 0) {
        return '未上传'
      }
      return files.map(item => item.name).join('、')
    },
    buildPayload() {
      const qualification = {
        institutionType: this.form.institutionType,
        region: {
          province: this.form.province,
          city: this.form.city,
          district: this.form.district
        },
        detailAddress: this.form.detailAddress,
        legalPersonName: this.form.legalPersonName,
        legalPersonIdNo: this.form.legalPersonIdNo,
        contactEmail: this.form.contactEmail,
        experience: this.form.experience
      }

      const contractFiles = {
        businessLicenseFiles: this.toStoredFiles(this.form.businessLicenseFiles),
        organizationCodeFiles: this.toStoredFiles(this.form.organizationCodeFiles),
        otherFiles: this.toStoredFiles(this.form.otherFiles)
      }

      return {
        partnerName: this.form.institutionName,
        contactName: this.form.contactName,
        contactMobile: this.form.contactMobile,
        businessLicenseNo: this.form.socialCreditCode,
        qualificationJson: JSON.stringify(qualification),
        settlementInfoJson: JSON.stringify({
          applySource: 'ruoyi-ui-public',
          applyRegion: this.areaText,
          detailAddress: this.form.detailAddress
        }),
        invoiceInfoJson: JSON.stringify({
          agreementAccepted: this.form.agreementAccepted,
          contactEmail: this.form.contactEmail
        }),
        contractFilesJson: JSON.stringify(contractFiles)
      }
    },
    toStoredFiles(files) {
      return (files || []).map(item => ({
        name: item.name,
        objectKey: item.objectKey
      }))
    },
    normalizeDraft(draft) {
      const next = Object.assign(createInitialForm(), draft || {})
      next.businessLicenseFiles = (next.businessLicenseFiles || []).map(this.normalizeStoredFile)
      next.organizationCodeFiles = (next.organizationCodeFiles || []).map(this.normalizeStoredFile)
      next.otherFiles = (next.otherFiles || []).map(this.normalizeStoredFile)
      return next
    },
    normalizeStoredFile(file) {
      return {
        uid: file.uid || `cached-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
        name: file.name,
        objectKey: file.objectKey,
        previewUrl: file.previewUrl || file.url || ''
      }
    },
    buildSnapshot(applyNo) {
      return {
        institutionName: this.form.institutionName,
        socialCreditCode: this.form.socialCreditCode,
        institutionType: this.form.institutionType,
        province: this.form.province,
        city: this.form.city,
        district: this.form.district,
        detailAddress: this.form.detailAddress,
        legalPersonName: this.form.legalPersonName,
        legalPersonIdNo: this.form.legalPersonIdNo,
        contactName: this.form.contactName,
        contactMobile: this.form.contactMobile,
        contactEmail: this.form.contactEmail,
        businessLicenseFiles: this.form.businessLicenseFiles,
        organizationCodeFiles: this.form.organizationCodeFiles,
        otherFiles: this.form.otherFiles,
        experience: this.form.experience,
        agreementAccepted: this.form.agreementAccepted,
        lastApplyNo: applyNo
      }
    },
    submit() {
      this.$refs.applyForm.validate(valid => {
        if (!valid) {
          return
        }
        this.submitting = true
        submitPartnerApply(this.buildPayload()).then(res => {
          const data = res.data || {}
          const applyNo = data.applyNo
          if (applyNo) {
            cache.local.set(LAST_APPLY_NO_KEY, applyNo)
          }
          cache.local.setJSON(LAST_DRAFT_KEY, this.buildSnapshot(applyNo))
          this.$router.replace({
            path: '/partner-apply/status',
            query: {
              applyNo,
              submitted: '1'
            }
          })
        }).finally(() => {
          this.submitting = false
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.partner-form-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f3f8ff 0%, #ffffff 38%, #f8fbff 100%);
}

.page-header {
  padding: 32px 24px 12px;
}

.header-inner,
.page-body {
  max-width: 1120px;
  margin: 0 auto;
}

.header-inner {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.page-tag {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(31, 113, 214, 0.12);
  color: #165dca;
  font-size: 13px;
  font-weight: 600;
}

.header-inner h1 {
  margin: 18px 0 12px;
  font-size: 36px;
  color: #21314b;
}

.header-inner p {
  margin: 0;
  color: #5e6f87;
  line-height: 1.8;
}

.page-body {
  padding: 8px 24px 56px;
}

.form-shell {
  padding: 28px;
  border-radius: 28px;
  background: #ffffff;
  box-shadow: 0 18px 52px rgba(26, 56, 105, 0.08);
}

.top-alert {
  margin-bottom: 20px;
}

.steps {
  margin-bottom: 30px;
}

.panel-title {
  margin-bottom: 22px;
  font-size: 24px;
  font-weight: 700;
  color: #21314b;
}

.upload-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 16px;
  border: 1px dashed #c8d9ef;
  border-radius: 16px;
  background: #f8fbff;
}

.upload-copy {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #58708e;
}

.upload-copy strong {
  color: #20324b;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 12px;
}

.file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 14px;
  background: #f7faff;
}

.file-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.file-name {
  color: #29456d;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.summary-card {
  padding: 20px;
  border-radius: 20px;
  background: #f9fbff;
  border: 1px solid #e3edfb;
}

.summary-card.full-width {
  grid-column: 1 / -1;
}

.summary-title {
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 700;
  color: #20324b;
}

.summary-item {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 14px;
  padding: 10px 0;
  border-bottom: 1px solid #edf3fc;
  color: #5c708b;
}

.summary-item strong,
.summary-file-block span:last-child {
  color: #22324d;
  word-break: break-word;
}

.summary-files {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.summary-file-block {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 14px;
  border-radius: 16px;
  background: #ffffff;
}

.summary-file-block.full {
  grid-column: 1 / -1;
}

.block-label {
  color: #6b7e98;
  font-size: 13px;
}

.agreement-item {
  margin-top: 22px;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 28px;
}

@media (max-width: 767px) {
  .page-header {
    padding: 20px 14px 6px;
  }

  .page-body {
    padding: 8px 14px 32px;
  }

  .header-inner {
    flex-direction: column;
  }

  .header-inner h1 {
    font-size: 28px;
  }

  .form-shell {
    padding: 18px 16px;
  }

  .upload-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .summary-grid,
  .summary-files {
    grid-template-columns: 1fr;
  }

  .summary-item {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .action-bar {
    flex-direction: column;
  }

  .action-bar .el-button {
    width: 100%;
  }
}
</style>
