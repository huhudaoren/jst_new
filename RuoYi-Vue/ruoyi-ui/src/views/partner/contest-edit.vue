<template>
  <div class="app-container partner-contest-edit">
    <div class="edit-hero">
      <div>
        <div class="edit-eyebrow">Contest Builder</div>
        <div class="edit-title">{{ isEdit ? '编辑赛事' : '新建赛事' }}</div>
        <div class="edit-desc">
          按 6 个模块完成赛事资料、报名规则、成绩证书与预约配置，保存草稿后可直接提交平台审核。
        </div>
      </div>
      <div class="hero-actions">
        <el-button icon="el-icon-back" @click="goBack">返回列表</el-button>
        <el-button type="primary" plain :loading="saveLoading" @click="saveDraft">保存草稿</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitAudit">提交审核</el-button>
      </div>
    </div>

    <el-alert
      title="当前页面已切换至 /jst/partner/contest 包装路由；若仍有字段缺口，请按 ui-feedback 文档跟进。"
      type="info"
      show-icon
      :closable="false"
      class="page-alert"
    />

    <el-form
      ref="contestForm"
      v-loading="detailLoading"
      :model="form"
      :rules="rules"
      label-width="124px"
      class="contest-form"
    >
      <el-tabs v-model="activeTab" class="config-tabs">
        <el-tab-pane label="A 基本信息" name="basic">
          <el-card shadow="never" class="section-card">
            <div slot="header" class="section-title">赛事基础资料</div>
            <el-row :gutter="24">
              <el-col :xs="24" :md="12">
                <el-form-item label="赛事名称" prop="contestName">
                  <el-input v-model="form.contestName" maxlength="80" show-word-limit placeholder="请输入面向用户展示的赛事名称" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="赛事分类" prop="category">
                  <el-select v-model="form.category" filterable allow-create clearable placeholder="请选择或输入分类" class="full-width">
                    <el-option v-for="item in categoryOptions" :key="item" :label="item" :value="item" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="组别/层级" prop="groupLevels">
                  <el-input v-model="form.groupLevels" placeholder="如：幼儿组、小学组、初中组；多组可逗号分隔" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="封面图" prop="coverImage">
                  <ImageUpload v-model="form.coverImage" :limit="1" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-alert
                  title="主办方、协办方、线下地址与详情图集字段当前后端未提供保存/详情字段，已在字段反馈中列出；本页先保留核心可提交字段。"
                  type="warning"
                  show-icon
                  :closable="false"
                  class="inline-alert"
                />
              </el-col>
              <el-col :span="24">
                <el-form-item label="赛事详情" prop="description">
                  <Editor v-model="form.description" :min-height="260" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>
        </el-tab-pane>

        <el-tab-pane label="B 时间与场次" name="schedule">
          <el-card shadow="never" class="section-card">
            <div slot="header" class="section-title">报名与比赛时间</div>
            <el-row :gutter="24">
              <el-col :xs="24" :md="12">
                <el-form-item label="报名开始" prop="enrollStartTime">
                  <el-date-picker
                    v-model="form.enrollStartTime"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    placeholder="请选择报名开始时间"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="报名结束" prop="enrollEndTime">
                  <el-date-picker
                    v-model="form.enrollEndTime"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    placeholder="请选择报名结束时间"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="比赛开始" prop="eventStartTime">
                  <el-date-picker
                    v-model="form.eventStartTime"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    placeholder="请选择比赛开始时间"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="比赛结束" prop="eventEndTime">
                  <el-date-picker
                    v-model="form.eventEndTime"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    placeholder="请选择比赛结束时间"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="售后宽限" prop="aftersaleDays">
                  <el-input-number v-model="form.aftersaleDays" :min="0" :max="365" controls-position="right" />
                  <span class="field-tip">天</span>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-alert
                  title="明确成绩发布时间、具体场次/日程与售后截止时间当前缺少结构化字段，暂通过详情说明承接并已反馈给后端。"
                  type="warning"
                  show-icon
                  :closable="false"
                  class="inline-alert"
                />
              </el-col>
            </el-row>
          </el-card>
        </el-tab-pane>

        <el-tab-pane label="C 报名规则" name="enroll">
          <el-card shadow="never" class="section-card">
            <div slot="header" class="section-title">价格、渠道与报名表</div>
            <el-row :gutter="24">
              <el-col :xs="24" :md="8">
                <el-form-item label="报名价格" prop="price">
                  <el-input-number v-model="form.price" :min="0" :precision="2" :step="10" controls-position="right" class="full-width" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="8">
                <el-form-item label="渠道报名" prop="supportChannelEnroll">
                  <el-switch v-model="form.supportChannelEnroll" :active-value="1" :inactive-value="0" active-text="支持" inactive-text="关闭" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="8">
                <el-form-item label="积分抵扣" prop="supportPointsDeduct">
                  <el-switch v-model="form.supportPointsDeduct" :active-value="1" :inactive-value="0" active-text="支持" inactive-text="关闭" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="报名表单" prop="formTemplateId">
                  <el-select v-model="form.formTemplateId" clearable filterable placeholder="请选择报名表模板" class="full-width">
                    <el-option
                      v-for="item in formTemplateOptions"
                      :key="item.templateId"
                      :label="formatTemplateLabel(item)"
                      :value="item.templateId"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="预约可退" prop="allowAppointmentRefund">
                  <el-switch
                    v-model="form.allowAppointmentRefund"
                    :active-value="1"
                    :inactive-value="0"
                    active-text="允许退预约"
                    inactive-text="不可退预约"
                  />
                  <div class="field-help">Q-07 开关已随保存 payload 附带；当前后端 DTO/VO 尚未完整接入，可能暂不回显。</div>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-alert
                  title="总名额、单人限购、优惠券适用范围与报名表字段快照当前缺少后端字段或结构，已在字段反馈中列出。"
                  type="warning"
                  show-icon
                  :closable="false"
                  class="inline-alert"
                />
              </el-col>
            </el-row>
          </el-card>
        </el-tab-pane>

        <el-tab-pane label="D 成绩与证书" name="scoreCert">
          <el-card shadow="never" class="section-card">
            <div slot="header" class="section-title">成绩规则与证书规则</div>
            <el-row :gutter="24">
              <el-col :xs="24" :md="12">
                <el-form-item label="成绩规则" prop="scoreRuleJson">
                  <el-select v-model="scoreRuleForm.publishMode" class="full-width" placeholder="请选择发布模式">
                    <el-option label="手动发布" value="manual" />
                    <el-option label="自动发布" value="auto" />
                  </el-select>
                  <div class="score-items-editor">
                    <span class="editor-label">成绩项</span>
                    <div class="tag-editor">
                      <el-tag
                        v-for="(item, index) in scoreRuleForm.scoreItems"
                        :key="item + '_' + index"
                        closable
                        class="tag-item"
                        @close="removeScoreItem(index)"
                      >
                        {{ item }}
                      </el-tag>
                      <el-input
                        v-if="scoreItemInputVisible"
                        ref="scoreItemInput"
                        v-model="scoreItemInputValue"
                        class="tag-input"
                        size="small"
                        @keyup.enter.native="confirmScoreItem"
                        @blur="confirmScoreItem"
                      />
                      <el-button v-else size="small" class="tag-add-btn" icon="el-icon-plus" @click="showScoreItemInput">添加成绩项</el-button>
                    </div>
                  </div>
                  <div class="field-help">常用项：总分、技巧、表现力。新增后会自动转为可提交 JSON。</div>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="证书规则" prop="certRuleJson">
                  <el-select v-model="certRuleForm.issueMode" class="full-width" placeholder="请选择颁发模式">
                    <el-option label="手动颁发" value="manual" />
                    <el-option label="自动颁发" value="auto" />
                  </el-select>
                  <div class="score-items-editor">
                    <span class="editor-label">证书模板</span>
                    <el-select
                      v-model="certRuleForm.templateIds"
                      class="full-width"
                      multiple
                      collapse-tags
                      filterable
                      clearable
                      placeholder="请选择证书模板（可多选）"
                    >
                      <el-option
                        v-for="item in certTemplateOptions"
                        :key="item.templateId"
                        :label="formatCertTemplateLabel(item)"
                        :value="item.templateId"
                      />
                    </el-select>
                  </div>
                  <div class="field-help">未选择模板时可先保存草稿，后续在证书管理里补充完善。</div>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-alert
                  title="页面使用表单化编辑，系统会自动写回 scoreRuleJson / certRuleJson 字符串字段，保存协议不变。"
                  type="info"
                  show-icon
                  :closable="false"
                  class="inline-alert"
                />
              </el-col>
            </el-row>
          </el-card>
        </el-tab-pane>

        <el-tab-pane label="E 线下预约" name="appointment">
          <el-card shadow="never" class="section-card">
            <div slot="header" class="section-title">预约与核销</div>
            <el-row :gutter="24">
              <el-col :xs="24" :md="8">
                <el-form-item label="开启预约" prop="supportAppointment">
                  <el-switch v-model="form.supportAppointment" :active-value="1" :inactive-value="0" active-text="开启" inactive-text="关闭" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="8">
                <el-form-item label="预约容量" prop="appointmentCapacity">
                  <el-input-number
                    v-model="form.appointmentCapacity"
                    :min="0"
                    :max="999999"
                    controls-position="right"
                    class="full-width"
                    :disabled="form.supportAppointment !== 1"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="8">
                <el-form-item label="重复预约" prop="allowRepeatAppointment">
                  <el-switch
                    v-model="form.allowRepeatAppointment"
                    :active-value="1"
                    :inactive-value="0"
                    active-text="允许"
                    inactive-text="不允许"
                    :disabled="form.supportAppointment !== 1"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="核销配置" prop="writeoffConfig">
                  <el-row :gutter="16">
                    <el-col :xs="24" :md="12">
                      <div class="editor-label">核销方式</div>
                      <el-select
                        v-model="writeoffForm.writeoffMode"
                        class="full-width"
                        placeholder="请选择核销方式"
                        :disabled="form.supportAppointment !== 1"
                      >
                        <el-option label="二维码" value="qr" />
                        <el-option label="手动输入" value="manual" />
                      </el-select>
                    </el-col>
                    <el-col :xs="24" :md="12">
                      <div class="editor-label">需要签到</div>
                      <el-switch
                        v-model="writeoffForm.needSignIn"
                        :disabled="form.supportAppointment !== 1"
                        active-text="是"
                        inactive-text="否"
                      />
                    </el-col>
                  </el-row>
                  <div class="field-help">预约关闭时核销配置将不生效，开启后会自动带入并提交。</div>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-alert
                  title="预约日期、时间段、场地、每段容量与候补策略当前缺少结构化字段，本页先覆盖现有支持预约、容量、重复预约与核销 JSON。"
                  type="warning"
                  show-icon
                  :closable="false"
                  class="inline-alert"
                />
              </el-col>
            </el-row>
          </el-card>
        </el-tab-pane>

        <el-tab-pane label="F 团队预约配置" name="team">
          <el-card shadow="never" class="section-card team-placeholder">
            <div class="placeholder-icon">
              <i class="el-icon-user-solid" />
            </div>
            <div class="placeholder-title">团队预约字段等待后端协议</div>
            <div class="placeholder-desc">
              任务卡要求覆盖团队预约配置入口；当前 F7 赛事保存 DTO 与详情 VO 未提供团队报名/团队预约的最小人数、最大人数、队伍成员字段、联系人字段与批量预约规则。
            </div>
            <el-alert
              title="本页保留配置入口并将字段清单写入 ui-feedback，待后端补齐字段后可在此标签内无破坏扩展。"
              type="info"
              show-icon
              :closable="false"
            />
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </el-form>

    <div class="sticky-actions">
      <el-button icon="el-icon-back" @click="goBack">返回</el-button>
      <el-button type="primary" plain :loading="saveLoading" @click="saveDraft">保存草稿</el-button>
      <el-button type="primary" :loading="submitLoading" @click="submitAudit">保存并提交审核</el-button>
    </div>
  </div>
</template>

<script>
import {
  createPartnerContest,
  getPartnerContest,
  listPartnerFormTemplates,
  submitPartnerContest,
  updatePartnerContest
} from '@/api/partner/contest'
import { listCertTemplates } from '@/api/partner/cert'

function createDefaultScoreRule() {
  return {
    publishMode: 'manual',
    scoreItems: []
  }
}

function createDefaultCertRule() {
  return {
    issueMode: 'manual',
    templateIds: []
  }
}

function createDefaultWriteoffConfig() {
  return {
    writeoffMode: 'qr',
    needSignIn: true
  }
}

const DEFAULT_FORM = {
  contestId: null,
  contestName: '',
  category: '',
  groupLevels: '',
  coverImage: '',
  description: '',
  enrollStartTime: null,
  enrollEndTime: null,
  eventStartTime: null,
  eventEndTime: null,
  price: 0,
  supportChannelEnroll: 0,
  supportPointsDeduct: 0,
  supportAppointment: 0,
  appointmentCapacity: 0,
  writeoffConfig: '',
  allowRepeatAppointment: 0,
  allowAppointmentRefund: 0,
  certRuleJson: '',
  scoreRuleJson: '',
  formTemplateId: null,
  aftersaleDays: 0
}

export default {
  name: 'PartnerContestEdit',
  data() {
    return {
      activeTab: 'basic',
      detailLoading: false,
      saveLoading: false,
      submitLoading: false,
      form: { ...DEFAULT_FORM },
      formTemplateOptions: [],
      certTemplateOptions: [],
      scoreRuleForm: createDefaultScoreRule(),
      certRuleForm: createDefaultCertRule(),
      writeoffForm: createDefaultWriteoffConfig(),
      scoreItemInputVisible: false,
      scoreItemInputValue: '',
      categoryOptions: ['艺术', '音乐', '舞蹈', '美术', '朗诵戏剧', '文化', '科技', '体育'],
      rules: {
        contestName: [{ required: true, message: '请输入赛事名称', trigger: 'blur' }],
        category: [{ required: true, message: '请选择赛事分类', trigger: 'change' }],
        coverImage: [{ required: true, message: '请上传赛事封面图', trigger: 'change' }],
        enrollStartTime: [{ required: true, message: '请选择报名开始时间', trigger: 'change' }],
        enrollEndTime: [{ required: true, message: '请选择报名结束时间', trigger: 'change' }],
        eventStartTime: [{ required: true, message: '请选择比赛开始时间', trigger: 'change' }],
        eventEndTime: [{ required: true, message: '请选择比赛结束时间', trigger: 'change' }],
        price: [{ required: true, message: '请填写报名价格', trigger: 'blur' }],
        supportChannelEnroll: [{ required: true, message: '请选择是否支持渠道报名', trigger: 'change' }],
        supportPointsDeduct: [{ required: true, message: '请选择是否支持积分抵扣', trigger: 'change' }],
        supportAppointment: [{ required: true, message: '请选择是否开启线下预约', trigger: 'change' }],
        aftersaleDays: [{ required: true, message: '请填写售后宽限天数', trigger: 'blur' }]
      }
    }
  },
  computed: {
    isEdit() {
      return Boolean(this.form.contestId)
    },
    routeContestId() {
      return this.$route.params.contestId || this.$route.query.contestId
    }
  },
  watch: {
    scoreRuleForm: {
      handler() {
        this.syncScoreRuleJson()
      },
      deep: true
    },
    certRuleForm: {
      handler() {
        this.syncCertRuleJson()
      },
      deep: true
    },
    writeoffForm: {
      handler() {
        this.syncWriteoffConfig()
      },
      deep: true
    }
  },
  created() {
    this.loadFormTemplates()
    this.loadCertTemplates()
    this.hydrateRuleFormsFromJson()
    if (this.routeContestId) {
      this.loadDetail(this.routeContestId)
    }
  },
  methods: {
    loadDetail(contestId) {
      this.detailLoading = true
      getPartnerContest(contestId).then(response => {
        const data = response.data || {}
        this.form = {
          ...DEFAULT_FORM,
          ...data,
          contestId: data.contestId || contestId,
          supportChannelEnroll: this.toSwitch(data.supportChannelEnroll),
          supportPointsDeduct: this.toSwitch(data.supportPointsDeduct),
          supportAppointment: this.toSwitch(data.supportAppointment),
          allowRepeatAppointment: this.toSwitch(data.allowRepeatAppointment),
          allowAppointmentRefund: this.toSwitch(data.allowAppointmentRefund),
          appointmentCapacity: Number(data.appointmentCapacity || 0),
          aftersaleDays: Number(data.aftersaleDays || 0),
          price: Number(data.price || 0)
        }
        this.hydrateRuleFormsFromJson()
      }).finally(() => {
        this.detailLoading = false
      })
    },
    loadFormTemplates() {
      listPartnerFormTemplates({ pageNum: 1, pageSize: 100 }).then(response => {
        this.formTemplateOptions = Array.isArray(response.rows) ? response.rows : []
      }).catch(() => {
        this.formTemplateOptions = []
      })
    },
    loadCertTemplates() {
      listCertTemplates().then(response => {
        this.certTemplateOptions = Array.isArray(response.data) ? response.data : []
      }).catch(() => {
        this.certTemplateOptions = []
      })
    },
    hydrateRuleFormsFromJson() {
      this.scoreRuleForm = this.normalizeScoreRule(this.parseJsonObject(this.form.scoreRuleJson))
      this.certRuleForm = this.normalizeCertRule(this.parseJsonObject(this.form.certRuleJson))
      this.writeoffForm = this.normalizeWriteoffConfig(this.parseJsonObject(this.form.writeoffConfig))
      this.scoreItemInputVisible = false
      this.scoreItemInputValue = ''
      this.syncAllJsonFields()
    },
    parseJsonObject(value) {
      if (!value) return {}
      if (typeof value === 'object' && !Array.isArray(value)) return value
      try {
        const parsed = JSON.parse(value)
        return parsed && typeof parsed === 'object' ? parsed : {}
      } catch (e) {
        return {}
      }
    },
    normalizeStringArray(value) {
      const list = Array.isArray(value) ? value : []
      const result = []
      list.forEach(item => {
        const text = String(item || '').trim()
        if (text && !result.includes(text)) {
          result.push(text)
        }
      })
      return result
    },
    normalizeNumberArray(value) {
      const list = Array.isArray(value) ? value : []
      const result = []
      list.forEach(item => {
        const num = Number(item)
        if (Number.isFinite(num) && !result.includes(num)) {
          result.push(num)
        }
      })
      return result
    },
    normalizeScoreRule(raw) {
      return {
        publishMode: raw.publishMode === 'auto' ? 'auto' : 'manual',
        scoreItems: this.normalizeStringArray(raw.scoreItems)
      }
    },
    normalizeCertRule(raw) {
      return {
        issueMode: raw.issueMode === 'auto' ? 'auto' : 'manual',
        templateIds: this.normalizeNumberArray(raw.templateIds || raw.templates)
      }
    },
    normalizeWriteoffConfig(raw) {
      return {
        writeoffMode: raw.writeoffMode === 'manual' ? 'manual' : 'qr',
        needSignIn: raw.needSignIn !== false
      }
    },
    syncAllJsonFields() {
      this.syncScoreRuleJson()
      this.syncCertRuleJson()
      this.syncWriteoffConfig()
    },
    syncScoreRuleJson() {
      const payload = {
        publishMode: this.scoreRuleForm.publishMode === 'auto' ? 'auto' : 'manual',
        scoreItems: this.normalizeStringArray(this.scoreRuleForm.scoreItems)
      }
      this.form.scoreRuleJson = JSON.stringify(payload)
    },
    syncCertRuleJson() {
      const payload = {
        issueMode: this.certRuleForm.issueMode === 'auto' ? 'auto' : 'manual',
        templateIds: this.normalizeNumberArray(this.certRuleForm.templateIds)
      }
      this.form.certRuleJson = JSON.stringify(payload)
    },
    syncWriteoffConfig() {
      const payload = {
        writeoffMode: this.writeoffForm.writeoffMode === 'manual' ? 'manual' : 'qr',
        needSignIn: this.writeoffForm.needSignIn !== false
      }
      this.form.writeoffConfig = JSON.stringify(payload)
    },
    showScoreItemInput() {
      this.scoreItemInputVisible = true
      this.$nextTick(() => {
        if (this.$refs.scoreItemInput && this.$refs.scoreItemInput.focus) {
          this.$refs.scoreItemInput.focus()
        }
      })
    },
    confirmScoreItem() {
      const text = String(this.scoreItemInputValue || '').trim()
      if (text && !this.scoreRuleForm.scoreItems.includes(text)) {
        this.scoreRuleForm.scoreItems.push(text)
      }
      this.scoreItemInputValue = ''
      this.scoreItemInputVisible = false
    },
    removeScoreItem(index) {
      this.scoreRuleForm.scoreItems.splice(index, 1)
    },
    saveDraft() {
      this.validateForm().then(() => {
        this.saveLoading = true
        return this.persistContest()
      }).then(contestId => {
        this.$modal.msgSuccess('赛事草稿已保存')
        if (!this.form.contestId && contestId) {
          this.form.contestId = contestId
          this.$router.replace(`/partner/contest-edit/${contestId}`)
        }
      }).catch(() => {
      }).finally(() => {
        this.saveLoading = false
      })
    },
    submitAudit() {
      this.validateForm().then(() => {
        this.submitLoading = true
        return this.persistContest()
      }).then(contestId => {
        return submitPartnerContest(contestId || this.form.contestId)
      }).then(() => {
        this.$modal.msgSuccess('已提交平台审核')
        this.goBack()
      }).catch(() => {
      }).finally(() => {
        this.submitLoading = false
      })
    },
    persistContest() {
      const payload = this.buildPayload()
      if (this.form.contestId) {
        return updatePartnerContest(this.form.contestId, payload).then(() => this.form.contestId)
      }
      return createPartnerContest(payload).then(response => {
        const data = response.data || {}
        return data.contestId || data.id || response.contestId
      })
    },
    validateForm() {
      return new Promise((resolve, reject) => {
        this.$refs.contestForm.validate(valid => {
          if (!valid) {
            this.$modal.msgWarning('请先补齐必填项')
            reject(new Error('form invalid'))
            return
          }
          if (!this.validateJsonFields()) {
            reject(new Error('json invalid'))
            return
          }
          resolve()
        })
      })
    },
    validateJsonFields() {
      this.syncAllJsonFields()
      const fields = [
        { key: 'scoreRuleJson', label: '成绩规则' },
        { key: 'certRuleJson', label: '证书规则' },
        { key: 'writeoffConfig', label: '核销配置' }
      ]
      for (let i = 0; i < fields.length; i++) {
        const item = fields[i]
        const value = this.form[item.key]
        if (value && !this.isValidJson(value)) {
          this.$modal.msgError(`${item.label}不是合法 JSON`)
          return false
        }
      }
      return true
    },
    isValidJson(value) {
      try {
        JSON.parse(value)
        return true
      } catch (e) {
        return false
      }
    },
    buildPayload() {
      this.syncAllJsonFields()
      return {
        contestId: this.form.contestId,
        contestName: this.form.contestName,
        category: this.form.category,
        groupLevels: this.form.groupLevels,
        coverImage: this.form.coverImage,
        description: this.form.description,
        enrollStartTime: this.form.enrollStartTime,
        enrollEndTime: this.form.enrollEndTime,
        eventStartTime: this.form.eventStartTime,
        eventEndTime: this.form.eventEndTime,
        price: this.form.price,
        supportChannelEnroll: this.toSwitch(this.form.supportChannelEnroll),
        supportPointsDeduct: this.toSwitch(this.form.supportPointsDeduct),
        supportAppointment: this.toSwitch(this.form.supportAppointment),
        appointmentCapacity: this.form.supportAppointment === 1 ? this.form.appointmentCapacity : 0,
        writeoffConfig: this.form.writeoffConfig,
        allowRepeatAppointment: this.form.supportAppointment === 1 ? this.toSwitch(this.form.allowRepeatAppointment) : 0,
        allowAppointmentRefund: this.toSwitch(this.form.allowAppointmentRefund),
        certRuleJson: this.form.certRuleJson,
        scoreRuleJson: this.form.scoreRuleJson,
        formTemplateId: this.form.formTemplateId,
        aftersaleDays: this.form.aftersaleDays
      }
    },
    toSwitch(value) {
      return value === true || value === 1 || value === '1' ? 1 : 0
    },
    formatTemplateLabel(item) {
      const name = item.templateName || item.name || `模板 ${item.templateId}`
      const version = item.templateVersion || item.version
      return version ? `${name}（${version}）` : name
    },
    formatCertTemplateLabel(item) {
      const name = item.templateName || item.name || `模板 ${item.templateId}`
      const status = item.auditStatus === 'approved' ? '已审' : '待审'
      return `${name}（${status}）`
    },
    goBack() {
      this.$router.push('/partner/contest-list')
    }
  }
}
</script>

<style scoped>
.partner-contest-edit {
  background: #f6f8fb;
}

.edit-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 16px;
  padding: 22px 24px;
  color: #fff;
  border-radius: 18px;
  background: linear-gradient(135deg, #0f766e 0%, #2563eb 54%, #1e293b 100%);
  box-shadow: 0 14px 34px rgba(15, 118, 110, 0.18);
}

.edit-eyebrow {
  margin-bottom: 8px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  opacity: 0.78;
}

.edit-title {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.3;
}

.edit-desc {
  max-width: 680px;
  margin-top: 8px;
  color: rgba(255, 255, 255, 0.82);
  line-height: 1.7;
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.page-alert {
  margin-bottom: 16px;
}

.contest-form {
  padding-bottom: 80px;
}

.config-tabs {
  padding: 18px 20px 4px;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.06);
}

.section-card {
  border: 1px solid #e8edf4;
  border-radius: 14px;
}

.section-title {
  font-size: 15px;
  font-weight: 700;
  color: #1f2d3d;
}

.full-width {
  width: 100%;
}

.field-tip {
  margin-left: 8px;
  color: #64748b;
}

.field-help {
  margin-top: 6px;
  color: #8c98a8;
  font-size: 12px;
  line-height: 1.6;
}

.score-items-editor {
  margin-top: 12px;
}

.editor-label {
  margin-bottom: 8px;
  color: #475569;
  font-size: 12px;
  line-height: 1.5;
}

.tag-editor {
  min-height: 44px;
  padding: 10px;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
  background: #fafcff;
}

.tag-item {
  margin-right: 8px;
  margin-bottom: 8px;
}

.tag-input {
  width: 140px;
  vertical-align: top;
}

.tag-add-btn {
  margin-bottom: 8px;
}

.inline-alert {
  margin-bottom: 18px;
}

.team-placeholder {
  text-align: center;
}

.placeholder-icon {
  width: 64px;
  height: 64px;
  margin: 8px auto 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 22px;
  color: #0f766e;
  background: #e6fffb;
  font-size: 28px;
}

.placeholder-title {
  font-size: 18px;
  font-weight: 700;
  color: #1f2d3d;
}

.placeholder-desc {
  max-width: 760px;
  margin: 10px auto 18px;
  color: #64748b;
  line-height: 1.8;
}

.sticky-actions {
  position: fixed;
  right: 28px;
  bottom: 24px;
  z-index: 10;
  display: flex;
  gap: 10px;
  padding: 12px;
  border: 1px solid #e8edf4;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 16px 38px rgba(15, 23, 42, 0.14);
  backdrop-filter: blur(10px);
}

@media (max-width: 768px) {
  .edit-hero {
    align-items: flex-start;
    flex-direction: column;
  }

  .hero-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .sticky-actions {
    left: 12px;
    right: 12px;
    bottom: 12px;
    justify-content: space-between;
    overflow-x: auto;
  }
}
</style>
