<template>
  <el-dialog
    :title="dialogTitle"
    :visible.sync="dialogVisible"
    :width="isMobile ? '100%' : '1080px'"
    :fullscreen="isMobile"
    top="4vh"
    append-to-body
    destroy-on-close
    custom-class="jst-contest-edit-dialog"
    @open="handleOpen"
  >
    <div v-loading="loading" class="edit-wrap">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        :label-width="isMobile ? '92px' : '110px'"
      >
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="16">
              <el-col :xs="24" :md="12">
                <el-form-item label="赛事名称" prop="contestName">
                  <el-input v-model="form.contestName" maxlength="80" show-word-limit placeholder="请输入赛事名称" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="赛事分类" prop="category">
                  <el-input v-model="form.category" maxlength="32" placeholder="如：美术/舞蹈/朗诵" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="组别" prop="groupLevels">
                  <el-input v-model="form.groupLevels" maxlength="255" placeholder="如：小学组, 初中组" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="报名价格" prop="price">
                  <el-input-number
                    v-model="form.price"
                    :min="0"
                    :precision="2"
                    controls-position="right"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="报名开始" prop="enrollStartTime">
                  <el-date-picker
                    v-model="form.enrollStartTime"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    placeholder="选择开始时间"
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
                    placeholder="选择结束时间"
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
                    placeholder="选择开始时间"
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
                    placeholder="选择结束时间"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="封面图">
                  <ImageUpload v-model="form.coverImage" :limit="1" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="售后天数" prop="aftersaleDays">
                  <el-input-number
                    v-model="form.aftersaleDays"
                    :min="0"
                    :max="365"
                    controls-position="right"
                    class="full-width"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="8">
                <el-form-item label="渠道代报" prop="supportChannelEnroll">
                  <el-switch v-model="form.supportChannelEnroll" :active-value="1" :inactive-value="0" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="8">
                <el-form-item label="积分抵扣" prop="supportPointsDeduct">
                  <el-switch v-model="form.supportPointsDeduct" :active-value="1" :inactive-value="0" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="8">
                <el-form-item label="线下预约" prop="supportAppointment">
                  <el-switch v-model="form.supportAppointment" :active-value="1" :inactive-value="0" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="8">
                <el-form-item label="预约容量">
                  <el-input-number
                    v-model="form.appointmentCapacity"
                    :min="0"
                    controls-position="right"
                    class="full-width"
                    :disabled="form.supportAppointment !== 1"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="8">
                <el-form-item label="重复预约">
                  <el-switch
                    v-model="form.allowRepeatAppointment"
                    :active-value="1"
                    :inactive-value="0"
                    :disabled="form.supportAppointment !== 1"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="8">
                <el-form-item label="预约可退">
                  <el-switch v-model="form.allowAppointmentRefund" :active-value="1" :inactive-value="0" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="赛事说明">
                  <Editor v-model="form.description" :min-height="220" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <el-tab-pane label="主办方 / 联系方式" name="organizer">
            <el-alert
              type="info"
              :closable="false"
              title="这里的信息会显示在小程序赛事详情页的主办方卡和联系咨询卡上。"
              style="margin-bottom: 16px;"
            />
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="主办方名称">
                  <el-input v-model="form.organizer" maxlength="200" show-word-limit placeholder="如：清华大学计算机系" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="协办方">
                  <el-input v-model="form.coOrganizer" maxlength="200" show-word-limit placeholder="如：北京 XX 教育集团" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="主办方 LOGO">
                  <ImageUpload v-model="form.organizerLogo" :limit="1" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="比赛地址">
                  <el-input v-model="form.eventAddress" maxlength="500" show-word-limit placeholder="如：北京市海淀区清华大学 XX 体育馆" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="主办方简介">
                  <el-input
                    v-model="form.organizerDesc"
                    type="textarea"
                    :rows="3"
                    maxlength="500"
                    show-word-limit
                    placeholder="简要介绍主办方的背景、资质、过往赛事组织经验等"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="咨询电话">
                  <el-input v-model="form.contactPhone" maxlength="32" placeholder="如：400-123-5678" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="咨询微信">
                  <el-input v-model="form.contactWechat" maxlength="64" placeholder="如：jst_service" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="咨询邮箱">
                  <el-input v-model="form.contactEmail" maxlength="128" placeholder="如：service@jst.com" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <el-tab-pane label="赛程安排" name="schedule">
            <div class="table-action">
              <el-button type="primary" size="mini" icon="el-icon-plus" @click="addScheduleRow">新增赛程</el-button>
            </div>
            <el-table :data="scheduleRows" size="small" border>
              <el-table-column label="阶段名称" min-width="150">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.stageName" size="small" placeholder="如：初赛/决赛" />
                </template>
              </el-table-column>
              <el-table-column label="开始时间" min-width="180">
                <template slot-scope="scope">
                  <el-date-picker
                    v-model="scope.row.startTime"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    size="small"
                    class="full-width"
                  />
                </template>
              </el-table-column>
              <el-table-column label="结束时间" min-width="180">
                <template slot-scope="scope">
                  <el-date-picker
                    v-model="scope.row.endTime"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    size="small"
                    class="full-width"
                  />
                </template>
              </el-table-column>
              <el-table-column label="说明" min-width="220">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.description" size="small" placeholder="本阶段说明" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="84" fixed="right">
                <template slot-scope="scope">
                  <el-button type="text" class="danger-text" @click="removeScheduleRow(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <JstEmptyState v-if="!scheduleRows.length" description="暂无赛程数据" :image-size="92" />
          </el-tab-pane>

          <el-tab-pane label="奖项设置" name="awards">
            <div class="table-action">
              <el-button type="primary" size="mini" icon="el-icon-plus" @click="addAwardRow">新增奖项</el-button>
            </div>
            <el-table :data="awardRows" size="small" border>
              <el-table-column label="奖项名称" min-width="150">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.awardName" size="small" placeholder="如：一等奖" />
                </template>
              </el-table-column>
              <el-table-column label="等级" min-width="120">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.level" size="small" placeholder="如：金奖" />
                </template>
              </el-table-column>
              <el-table-column label="名额" min-width="100">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.quota" size="small" placeholder="如：10" />
                </template>
              </el-table-column>
              <el-table-column label="说明" min-width="240">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.description" size="small" placeholder="奖项说明" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="84" fixed="right">
                <template slot-scope="scope">
                  <el-button type="text" class="danger-text" @click="removeAwardRow(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <JstEmptyState v-if="!awardRows.length" description="暂无奖项数据" :image-size="92" />
          </el-tab-pane>

          <el-tab-pane label="常见问题" name="faq">
            <div class="table-action">
              <el-button type="primary" size="mini" icon="el-icon-plus" @click="addFaqRow">新增问题</el-button>
            </div>
            <el-table :data="faqRows" size="small" border>
              <el-table-column label="问题" min-width="280">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.question" size="small" placeholder="请输入问题" />
                </template>
              </el-table-column>
              <el-table-column label="答案" min-width="360">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.answer" size="small" placeholder="请输入答案" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="84" fixed="right">
                <template slot-scope="scope">
                  <el-button type="text" class="danger-text" @click="removeFaqRow(scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <JstEmptyState v-if="!faqRows.length" description="暂无常见问题" :image-size="92" />
          </el-tab-pane>

          <el-tab-pane label="标签设置" name="tags">
            <div class="tag-box">
              <el-tag
                v-for="(tag, index) in tags"
                :key="tag + '_' + index"
                closable
                class="tag-item"
                @close="removeTag(index)"
              >
                {{ tag }}
              </el-tag>
              <el-input
                v-if="tagInputVisible"
                ref="tagInputRef"
                v-model="tagInputValue"
                class="tag-input"
                size="small"
                @keyup.enter.native="confirmTag"
                @blur="confirmTag"
              />
              <el-button v-else class="tag-button" size="small" @click="showTagInput">+ 新增标签</el-button>
            </div>
            <div class="tag-tip">将同步写入 `recommend_tags`（英文逗号分隔）</div>

            <el-divider />

            <el-row :gutter="16">
              <el-col :xs="24" :md="8">
                <el-form-item label="报名表单ID">
                  <form-template-picker v-model="form.formTemplateId" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="成绩发布模式">
                  <el-select v-model="scoreRuleForm.publishMode" class="full-width" placeholder="请选择发布模式">
                    <el-option label="手动发布" value="manual" />
                    <el-option label="自动发布" value="auto" />
                  </el-select>
                  <div class="editor-inline-title">成绩项</div>
                  <div class="tag-box score-tag-box">
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
                      ref="scoreItemInputRef"
                      v-model="scoreItemInputValue"
                      class="tag-input"
                      size="small"
                      @keyup.enter.native="confirmScoreItem"
                      @blur="confirmScoreItem"
                    />
                    <el-button v-else class="tag-button" size="small" @click="showScoreItemInput">+ 添加成绩项</el-button>
                  </div>
                  <div class="editor-help">例如：总分、技巧、表现力。会自动转成可提交 JSON。</div>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="证书颁发模式">
                  <el-select v-model="certRuleForm.issueMode" class="full-width" placeholder="请选择颁发模式">
                    <el-option label="手动颁发" value="manual" />
                    <el-option label="自动颁发" value="auto" />
                  </el-select>
                  <div class="editor-inline-title">证书模板</div>
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
                  <div class="editor-help">可先保存后再补模板，系统会保持已选配置。</div>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="24">
                <el-form-item label="核销配置">
                  <el-row :gutter="16">
                    <el-col :xs="24" :md="12">
                      <div class="editor-inline-title">核销方式</div>
                      <el-select
                        v-model="writeoffForm.writeoffMode"
                        class="full-width"
                        :disabled="form.supportAppointment !== 1"
                        placeholder="请选择核销方式"
                      >
                        <el-option label="二维码" value="qr" />
                        <el-option label="手动输入" value="manual" />
                      </el-select>
                    </el-col>
                    <el-col :xs="24" :md="12">
                      <div class="editor-inline-title">需要签到</div>
                      <el-switch
                        v-model="writeoffForm.needSignIn"
                        :disabled="form.supportAppointment !== 1"
                        active-text="是"
                        inactive-text="否"
                      />
                    </el-col>
                  </el-row>
                  <div class="editor-help">关闭线下预约时该配置不会生效，开启后自动写回提交字段。</div>
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
      </el-form>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getAdminContest, updateAdminContest } from '@/api/jst/event/admin-contest'
import { listJst_cert_template } from '@/api/jst/event/jst_cert_template'
import FormTemplatePicker from '@/components/RelationPicker/FormTemplatePicker.vue'

function createDefaultForm() {
  return {
    contestId: null,
    partnerId: null,
    contestName: '',
    category: '',
    groupLevels: '',
    coverImage: '',
    description: '',
    enrollStartTime: '',
    enrollEndTime: '',
    eventStartTime: '',
    eventEndTime: '',
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
    scheduleJson: '[]',
    awardsJson: '[]',
    faqJson: '[]',
    recommendTags: '',
    formTemplateId: null,
    aftersaleDays: 0,
    // 主办方与联系方式（B3）
    organizer: '',
    coOrganizer: '',
    eventAddress: '',
    organizerLogo: '',
    organizerDesc: '',
    contactPhone: '',
    contactWechat: '',
    contactEmail: ''
  }
}

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

export default {
  name: 'JstContestEditDialog',
  components: {
    FormTemplatePicker
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    contestId: {
      type: [String, Number],
      default: null
    }
  },
  data() {
    return {
      activeTab: 'basic',
      loading: false,
      submitting: false,
      form: createDefaultForm(),
      scheduleRows: [],
      awardRows: [],
      faqRows: [],
      tags: [],
      certTemplateOptions: [],
      scoreRuleForm: createDefaultScoreRule(),
      certRuleForm: createDefaultCertRule(),
      writeoffForm: createDefaultWriteoffConfig(),
      tagInputVisible: false,
      tagInputValue: '',
      scoreItemInputVisible: false,
      scoreItemInputValue: '',
      rules: {
        contestName: [{ required: true, message: '请输入赛事名称', trigger: 'blur' }],
        category: [{ required: true, message: '请输入赛事分类', trigger: 'blur' }],
        price: [{ required: true, message: '请输入报名价格', trigger: 'change' }],
        enrollStartTime: [{ required: true, message: '请选择报名开始时间', trigger: 'change' }],
        enrollEndTime: [{ required: true, message: '请选择报名结束时间', trigger: 'change' }],
        eventStartTime: [{ required: true, message: '请选择比赛开始时间', trigger: 'change' }],
        eventEndTime: [{ required: true, message: '请选择比赛结束时间', trigger: 'change' }],
        supportChannelEnroll: [{ required: true, message: '请选择是否支持渠道代报', trigger: 'change' }],
        supportPointsDeduct: [{ required: true, message: '请选择是否支持积分抵扣', trigger: 'change' }],
        supportAppointment: [{ required: true, message: '请选择是否支持线下预约', trigger: 'change' }],
        aftersaleDays: [{ required: true, message: '请输入售后天数', trigger: 'change' }]
      }
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    },
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    },
    dialogTitle() {
      return this.contestId ? '编辑赛事' : '新建赛事'
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
  methods: {
    handleOpen() {
      this.activeTab = 'basic'
      this.tagInputVisible = false
      this.tagInputValue = ''
      this.scoreItemInputVisible = false
      this.scoreItemInputValue = ''
      this.loadCertTemplates()
      if (this.contestId) {
        this.loadDetail()
      } else {
        this.resetData()
      }
    },
    resetData() {
      this.form = createDefaultForm()
      this.scheduleRows = []
      this.awardRows = []
      this.faqRows = []
      this.tags = []
      this.scoreRuleForm = createDefaultScoreRule()
      this.certRuleForm = createDefaultCertRule()
      this.writeoffForm = createDefaultWriteoffConfig()
      this.syncAllJsonFields()
    },
    async loadDetail() {
      if (!this.contestId) return
      this.loading = true
      try {
        const res = await getAdminContest(this.contestId)
        const data = res.data || {}
        this.form = {
          ...createDefaultForm(),
          ...data,
          contestId: data.contestId || this.contestId,
          price: Number(data.price || 0),
          supportChannelEnroll: this.toSwitch(data.supportChannelEnroll),
          supportPointsDeduct: this.toSwitch(data.supportPointsDeduct),
          supportAppointment: this.toSwitch(data.supportAppointment),
          allowRepeatAppointment: this.toSwitch(data.allowRepeatAppointment),
          allowAppointmentRefund: this.toSwitch(data.allowAppointmentRefund),
          appointmentCapacity: Number(data.appointmentCapacity || 0),
          aftersaleDays: Number(data.aftersaleDays || 0)
        }
        this.scheduleRows = this.normalizeSchedule(data.scheduleJson)
        this.awardRows = this.normalizeAwards(data.awardsJson)
        this.faqRows = this.normalizeFaq(data.faqJson)
        this.tags = this.normalizeTags(data.recommendTags)
        this.hydrateRuleFormsFromJson()
      } catch (e) {
        this.resetData()
      } finally {
        this.loading = false
      }
    },
    loadCertTemplates() {
      listJst_cert_template({ pageNum: 1, pageSize: 200 }).then(res => {
        this.certTemplateOptions = Array.isArray(res.rows) ? res.rows : []
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
    parseJsonRows(value) {
      if (!value) return []
      if (Array.isArray(value)) return value
      if (typeof value === 'object') {
        if (Array.isArray(value.rows)) return value.rows
        if (Array.isArray(value.list)) return value.list
        return []
      }
      try {
        const parsed = JSON.parse(value)
        if (Array.isArray(parsed)) return parsed
        if (parsed && Array.isArray(parsed.rows)) return parsed.rows
        if (parsed && Array.isArray(parsed.list)) return parsed.list
        return []
      } catch (e) {
        return []
      }
    },
    normalizeSchedule(value) {
      return this.parseJsonRows(value).map(item => ({
        stageName: item.stageName || item.stage || item.name || '',
        startTime: item.startTime || item.beginTime || item.start || '',
        endTime: item.endTime || item.finishTime || item.end || '',
        description: item.description || item.desc || ''
      }))
    },
    normalizeAwards(value) {
      return this.parseJsonRows(value).map(item => ({
        awardName: item.awardName || item.name || item.title || '',
        level: item.level || item.rank || '',
        quota: item.quota || item.count || '',
        description: item.description || item.desc || ''
      }))
    },
    normalizeFaq(value) {
      return this.parseJsonRows(value).map(item => ({
        question: item.question || item.q || '',
        answer: item.answer || item.a || ''
      }))
    },
    normalizeTags(value) {
      if (!value) return []
      const list = Array.isArray(value) ? value : String(value).split(/[，,]/)
      return Array.from(new Set(list.map(item => String(item || '').trim()).filter(Boolean)))
    },
    addScheduleRow() {
      this.scheduleRows.push({ stageName: '', startTime: '', endTime: '', description: '' })
    },
    removeScheduleRow(index) {
      this.scheduleRows.splice(index, 1)
    },
    addAwardRow() {
      this.awardRows.push({ awardName: '', level: '', quota: '', description: '' })
    },
    removeAwardRow(index) {
      this.awardRows.splice(index, 1)
    },
    addFaqRow() {
      this.faqRows.push({ question: '', answer: '' })
    },
    removeFaqRow(index) {
      this.faqRows.splice(index, 1)
    },
    showTagInput() {
      this.tagInputVisible = true
      this.$nextTick(() => {
        if (this.$refs.tagInputRef && this.$refs.tagInputRef.focus) {
          this.$refs.tagInputRef.focus()
        }
      })
    },
    confirmTag() {
      const value = String(this.tagInputValue || '').trim()
      if (value && !this.tags.includes(value)) {
        this.tags.push(value)
      }
      this.tagInputValue = ''
      this.tagInputVisible = false
    },
    removeTag(index) {
      this.tags.splice(index, 1)
    },
    showScoreItemInput() {
      this.scoreItemInputVisible = true
      this.$nextTick(() => {
        if (this.$refs.scoreItemInputRef && this.$refs.scoreItemInputRef.focus) {
          this.$refs.scoreItemInputRef.focus()
        }
      })
    },
    confirmScoreItem() {
      const value = String(this.scoreItemInputValue || '').trim()
      if (value && !this.scoreRuleForm.scoreItems.includes(value)) {
        this.scoreRuleForm.scoreItems.push(value)
      }
      this.scoreItemInputValue = ''
      this.scoreItemInputVisible = false
    },
    removeScoreItem(index) {
      this.scoreRuleForm.scoreItems.splice(index, 1)
    },
    formatCertTemplateLabel(item) {
      const name = item.templateName || item.name || `模板 ${item.templateId}`
      const status = item.auditStatus === 'approved' ? '已审' : '待审'
      return `${name}（${status}）`
    },
    toSwitch(value) {
      return value === 1 || value === true || value === '1' ? 1 : 0
    },
    serializeScheduleRows() {
      return this.scheduleRows
        .map(item => ({
          stageName: String(item.stageName || '').trim(),
          startTime: item.startTime || '',
          endTime: item.endTime || '',
          description: String(item.description || '').trim()
        }))
        .filter(item => item.stageName || item.startTime || item.endTime || item.description)
    },
    serializeAwardRows() {
      return this.awardRows
        .map(item => ({
          awardName: String(item.awardName || '').trim(),
          level: String(item.level || '').trim(),
          quota: String(item.quota || '').trim(),
          description: String(item.description || '').trim()
        }))
        .filter(item => item.awardName || item.level || item.quota || item.description)
    },
    serializeFaqRows() {
      return this.faqRows
        .map(item => ({
          question: String(item.question || '').trim(),
          answer: String(item.answer || '').trim()
        }))
        .filter(item => item.question || item.answer)
    },
    validateTimeOrder() {
      const checks = [
        { start: this.form.enrollStartTime, end: this.form.enrollEndTime, msg: '报名结束时间必须晚于开始时间' },
        { start: this.form.eventStartTime, end: this.form.eventEndTime, msg: '比赛结束时间必须晚于开始时间' }
      ]
      for (let i = 0; i < checks.length; i++) {
        const item = checks[i]
        if (item.start && item.end && new Date(item.start).getTime() >= new Date(item.end).getTime()) {
          this.$modal.msgError(item.msg)
          return false
        }
      }
      return true
    },
    buildPayload() {
      this.syncAllJsonFields()
      const scheduleRows = this.serializeScheduleRows()
      const awardRows = this.serializeAwardRows()
      const faqRows = this.serializeFaqRows()
      const tags = this.normalizeTags(this.tags)
      return {
        ...this.form,
        contestId: this.form.contestId || this.contestId,
        price: Number(this.form.price || 0),
        supportChannelEnroll: this.toSwitch(this.form.supportChannelEnroll),
        supportPointsDeduct: this.toSwitch(this.form.supportPointsDeduct),
        supportAppointment: this.toSwitch(this.form.supportAppointment),
        appointmentCapacity: this.form.supportAppointment === 1 ? Number(this.form.appointmentCapacity || 0) : 0,
        allowRepeatAppointment: this.form.supportAppointment === 1 ? this.toSwitch(this.form.allowRepeatAppointment) : 0,
        allowAppointmentRefund: this.toSwitch(this.form.allowAppointmentRefund),
        aftersaleDays: Number(this.form.aftersaleDays || 0),
        recommendTags: tags.join(','),
        scheduleJson: JSON.stringify(scheduleRows),
        awardsJson: JSON.stringify(awardRows),
        faqJson: JSON.stringify(faqRows)
      }
    },
    submitForm() {
      if (!this.contestId && !this.form.contestId) {
        this.$modal.msgWarning('未获取到赛事ID，无法保存')
        return
      }
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        if (!this.validateTimeOrder()) return
        this.submitting = true
        try {
          const payload = this.buildPayload()
          await updateAdminContest(payload)
          this.$modal.msgSuccess('保存成功')
          this.$emit('success')
          this.dialogVisible = false
        } finally {
          this.submitting = false
        }
      })
    }
  }
}
</script>

<style scoped>
.edit-wrap {
  min-height: 220px;
  max-height: calc(100vh - 180px);
  overflow-y: auto;
  padding-right: 4px;
}

.full-width {
  width: 100%;
}

.table-action {
  margin-bottom: 10px;
}

.danger-text {
  color: #f56c6c;
}

.tag-box {
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

.tag-button {
  margin-bottom: 8px;
}

.tag-tip {
  margin-top: 8px;
  color: #738094;
  font-size: 12px;
}

.score-tag-box {
  margin-top: 8px;
}

.editor-inline-title {
  margin: 10px 0 6px;
  color: #475569;
  font-size: 12px;
}

.editor-help {
  margin-top: 6px;
  color: #738094;
  font-size: 12px;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .edit-wrap {
    max-height: calc(100vh - 130px);
  }
}
</style>
