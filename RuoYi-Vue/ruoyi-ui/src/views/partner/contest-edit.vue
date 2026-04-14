<template>
  <div class="app-container partner-contest-edit">
    <div class="edit-hero">
      <div>
        <div class="edit-eyebrow">Contest Builder</div>
        <div class="edit-title">{{ isEdit ? '编辑赛事' : '新建赛事' }}</div>
        <div class="edit-desc">
          按 7 个模块完成赛事资料、报名规则、成绩证书与预约配置，保存草稿后可直接提交平台审核。
        </div>
      </div>
      <div class="hero-actions">
        <el-button icon="el-icon-back" @click="goBack">返回列表</el-button>
        <el-button icon="el-icon-view" @click="showPreview = true">预览</el-button>
        <el-button type="primary" plain :loading="saveLoading" @click="saveDraft">保存草稿</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitAudit">提交审核</el-button>
      </div>
    </div>

    <el-form
      ref="contestForm"
      v-loading="detailLoading"
      :model="form"
      :rules="rules"
      label-width="124px"
      class="contest-form"
    >
      <el-tabs v-model="activeTab" class="config-tabs">
        <!-- ==================== Tab A 基本信息 ==================== -->
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
                    <el-option v-for="dict in dict.type.jst_contest_category" :key="dict.value" :label="dict.label" :value="dict.value" />
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
              <el-col :xs="24" :md="12">
                <el-form-item label="Banner 大图" prop="bannerImage">
                  <ImageUpload v-model="form.bannerImage" :limit="1" />
                  <div class="field-help">推荐 16:9 比例，首页轮播展示图</div>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="主办方" prop="organizer">
                  <el-input v-model="form.organizer" maxlength="200" placeholder="请输入主办方名称" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="协办方" prop="coOrganizer">
                  <el-input v-model="form.coOrganizer" maxlength="200" placeholder="请输入协办方名称" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="比赛地址" prop="eventAddress">
                  <el-input v-model="form.eventAddress" maxlength="500" placeholder="请输入比赛场地地址" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="总名额" prop="totalQuota">
                  <el-input-number v-model="form.totalQuota" :min="0" :max="999999" controls-position="right" class="full-width" />
                  <div class="field-help">0 表示不限名额</div>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="单人限购" prop="perUserLimit">
                  <el-input-number v-model="form.perUserLimit" :min="1" :max="99" controls-position="right" class="full-width" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="赛事详情" prop="description">
                  <Editor v-model="form.description" :min-height="260" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>
        </el-tab-pane>

        <!-- ==================== Tab B 赛程管理 ==================== -->
        <el-tab-pane label="B 时间与赛程" name="schedule">
          <el-card shadow="never" class="section-card">
            <div slot="header" class="section-title">报名与比赛时间</div>
            <el-row :gutter="24">
              <el-col :xs="24" :md="12">
                <el-form-item label="报名开始" prop="enrollStartTime">
                  <el-date-picker v-model="form.enrollStartTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择" class="full-width" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="报名结束" prop="enrollEndTime">
                  <el-date-picker v-model="form.enrollEndTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择" class="full-width" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="比赛开始" prop="eventStartTime">
                  <el-date-picker v-model="form.eventStartTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择" class="full-width" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="比赛结束" prop="eventEndTime">
                  <el-date-picker v-model="form.eventEndTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择" class="full-width" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="成绩发布时间" prop="scorePublishTime">
                  <el-date-picker v-model="form.scorePublishTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择" class="full-width" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="售后截止时间" prop="aftersaleDeadline">
                  <el-date-picker v-model="form.aftersaleDeadline" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择" class="full-width" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="售后宽限" prop="aftersaleDays">
                  <el-input-number v-model="form.aftersaleDays" :min="0" :max="365" controls-position="right" />
                  <span class="field-tip">天</span>
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>

          <el-card shadow="never" class="section-card" style="margin-top: 16px">
            <div slot="header" class="section-title">
              赛程阶段管理
              <el-button type="primary" size="small" icon="el-icon-plus" style="float:right" @click="addScheduleRow">添加阶段</el-button>
            </div>
            <el-table :data="scheduleList" border size="small" class="inline-edit-table">
              <el-table-column label="序号" type="index" width="50" />
              <el-table-column label="阶段名称" min-width="140">
                <template slot-scope="{ row }">
                  <el-input v-model="row.stageName" size="small" placeholder="如：初赛" />
                </template>
              </el-table-column>
              <el-table-column label="开始时间" min-width="180">
                <template slot-scope="{ row }">
                  <el-date-picker v-model="row.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" size="small" placeholder="开始" class="full-width" />
                </template>
              </el-table-column>
              <el-table-column label="结束时间" min-width="180">
                <template slot-scope="{ row }">
                  <el-date-picker v-model="row.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" size="small" placeholder="结束" class="full-width" />
                </template>
              </el-table-column>
              <el-table-column label="场地" min-width="120">
                <template slot-scope="{ row }">
                  <el-input v-model="row.venue" size="small" placeholder="场地" />
                </template>
              </el-table-column>
              <el-table-column label="说明" min-width="140">
                <template slot-scope="{ row }">
                  <el-input v-model="row.description" size="small" placeholder="说明" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" fixed="right">
                <template slot-scope="{ $index }">
                  <el-button type="text" size="mini" :disabled="$index === 0" @click="moveRow(scheduleList, $index, -1)">上移</el-button>
                  <el-button type="text" size="mini" :disabled="$index === scheduleList.length - 1" @click="moveRow(scheduleList, $index, 1)">下移</el-button>
                  <el-button type="text" size="mini" class="danger-text" @click="removeRow(scheduleList, $index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-tab-pane>

        <!-- ==================== Tab C 报名规则 ==================== -->
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
                  <div class="inline-btn-group">
                    <el-select v-model="form.formTemplateId" clearable filterable placeholder="请选择报名表模板" class="flex-select">
                      <el-option
                        v-for="item in formTemplateOptions"
                        :key="item.templateId"
                        :label="formatTemplateLabel(item)"
                        :value="item.templateId"
                      />
                    </el-select>
                    <el-button type="primary" plain size="small" icon="el-icon-plus" @click="showFormTemplateDrawer = true">新建表单</el-button>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="预约可退" prop="allowAppointmentRefund">
                  <el-switch v-model="form.allowAppointmentRefund" :active-value="1" :inactive-value="0" active-text="允许退预约" inactive-text="不可退预约" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>
        </el-tab-pane>

        <!-- ==================== Tab D 成绩与证书 ==================== -->
        <el-tab-pane label="D 成绩与证书" name="scoreCert">
          <el-card shadow="never" class="section-card">
            <div slot="header" class="section-title">成绩配置</div>
            <el-row :gutter="24">
              <el-col :xs="24" :md="12">
                <el-form-item label="成绩发布模式">
                  <el-select v-model="form.scorePublishMode" class="full-width" placeholder="请选择">
                    <el-option label="手动发布" value="manual" />
                    <el-option label="自动发布" value="auto" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>

          <el-card shadow="never" class="section-card" style="margin-top: 16px">
            <div slot="header" class="section-title">
              成绩项管理
              <el-button type="primary" size="small" icon="el-icon-plus" style="float:right" @click="addScoreItemRow">添加成绩项</el-button>
            </div>
            <el-table :data="scoreItemList" border size="small" class="inline-edit-table">
              <el-table-column label="序号" type="index" width="50" />
              <el-table-column label="项目名称" min-width="160">
                <template slot-scope="{ row }">
                  <el-input v-model="row.itemName" size="small" placeholder="如：总分、技巧" />
                </template>
              </el-table-column>
              <el-table-column label="满分" min-width="100">
                <template slot-scope="{ row }">
                  <el-input-number v-model="row.maxScore" :min="0" :precision="1" size="small" controls-position="right" class="full-width" />
                </template>
              </el-table-column>
              <el-table-column label="权重" min-width="100">
                <template slot-scope="{ row }">
                  <el-input-number v-model="row.weight" :min="0" :max="1" :step="0.1" :precision="2" size="small" controls-position="right" class="full-width" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" fixed="right">
                <template slot-scope="{ $index }">
                  <el-button type="text" size="mini" class="danger-text" @click="removeRow(scoreItemList, $index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>

          <el-card shadow="never" class="section-card" style="margin-top: 16px">
            <div slot="header" class="section-title">
              奖项设置
              <div style="float:right">
                <el-dropdown trigger="click" @command="fillAwardPreset">
                  <el-button size="small" plain>快速填充 <i class="el-icon-arrow-down el-icon--right" /></el-button>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="standard">常规模板（一/二/三等奖 + 优秀奖）</el-dropdown-item>
                    <el-dropdown-item command="medal">金银铜模板</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
                <el-button type="primary" size="small" icon="el-icon-plus" @click="addAwardRow">添加奖项</el-button>
              </div>
            </div>
            <el-table :data="awardList" border size="small" class="inline-edit-table">
              <el-table-column label="序号" type="index" width="50" />
              <el-table-column label="奖项名称" min-width="140">
                <template slot-scope="{ row }">
                  <el-input v-model="row.awardName" size="small" placeholder="如：一等奖" />
                </template>
              </el-table-column>
              <el-table-column label="等级" min-width="100">
                <template slot-scope="{ row }">
                  <el-input v-model="row.awardLevel" size="small" placeholder="如：一等" />
                </template>
              </el-table-column>
              <el-table-column label="说明" min-width="140">
                <template slot-scope="{ row }">
                  <el-input v-model="row.description" size="small" placeholder="奖项说明" />
                </template>
              </el-table-column>
              <el-table-column label="名额" min-width="80">
                <template slot-scope="{ row }">
                  <el-input-number v-model="row.quota" :min="0" size="small" controls-position="right" class="full-width" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" fixed="right">
                <template slot-scope="{ $index }">
                  <el-button type="text" size="mini" class="danger-text" @click="removeRow(awardList, $index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>

          <el-card shadow="never" class="section-card" style="margin-top: 16px">
            <div slot="header" class="section-title">证书配置</div>
            <el-row :gutter="24">
              <el-col :xs="24" :md="12">
                <el-form-item label="证书颁发模式">
                  <el-select v-model="form.certIssueMode" class="full-width" placeholder="请选择">
                    <el-option label="手动颁发" value="manual" />
                    <el-option label="自动颁发" value="auto" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="证书模板">
                  <div class="inline-btn-group">
                    <el-select v-model="certTemplateIds" class="flex-select" multiple collapse-tags filterable clearable placeholder="请选择证书模板（可多选）">
                      <el-option
                        v-for="item in certTemplateOptions"
                        :key="item.templateId"
                        :label="formatCertTemplateLabel(item)"
                        :value="item.templateId"
                      />
                    </el-select>
                    <el-button type="primary" plain size="small" icon="el-icon-plus" @click="showCertDrawer = true">新建模板</el-button>
                  </div>
                  <div class="field-help">未选择模板时可先保存草稿，后续在证书管理里补充完善。</div>
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>
        </el-tab-pane>

        <!-- ==================== Tab E 线下预约 ==================== -->
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
                  <el-input-number v-model="form.appointmentCapacity" :min="0" :max="999999" controls-position="right" class="full-width" :disabled="form.supportAppointment !== 1" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="8">
                <el-form-item label="重复预约" prop="allowRepeatAppointment">
                  <el-switch v-model="form.allowRepeatAppointment" :active-value="1" :inactive-value="0" active-text="允许" inactive-text="不允许" :disabled="form.supportAppointment !== 1" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="核销方式" prop="writeoffMode">
                  <el-select v-model="form.writeoffMode" class="full-width" placeholder="请选择" :disabled="form.supportAppointment !== 1">
                    <el-option label="二维码" value="qr" />
                    <el-option label="手动输入" value="manual" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="需要签到" prop="needSignIn">
                  <el-switch v-model="form.needSignIn" :active-value="1" :inactive-value="0" active-text="是" inactive-text="否" :disabled="form.supportAppointment !== 1" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>

          <el-card shadow="never" class="section-card" style="margin-top: 16px">
            <div slot="header" class="section-title">
              预约时间段
              <div style="float:right">
                <el-button size="small" plain icon="el-icon-date" :disabled="form.supportAppointment !== 1" @click="showSlotGenerator = true">批量生成</el-button>
                <el-button type="primary" size="small" icon="el-icon-plus" :disabled="form.supportAppointment !== 1" @click="addSlotRow">添加时间段</el-button>
              </div>
            </div>
            <el-table :data="appointmentSlotList" border size="small" class="inline-edit-table">
              <el-table-column label="序号" type="index" width="50" />
              <el-table-column label="日期" min-width="150">
                <template slot-scope="{ row }">
                  <el-date-picker v-model="row.slotDate" type="date" value-format="yyyy-MM-dd" size="small" placeholder="日期" class="full-width" />
                </template>
              </el-table-column>
              <el-table-column label="开始时间" min-width="110">
                <template slot-scope="{ row }">
                  <el-time-picker v-model="row.startTime" value-format="HH:mm" format="HH:mm" size="small" placeholder="开始" class="full-width" />
                </template>
              </el-table-column>
              <el-table-column label="结束时间" min-width="110">
                <template slot-scope="{ row }">
                  <el-time-picker v-model="row.endTime" value-format="HH:mm" format="HH:mm" size="small" placeholder="结束" class="full-width" />
                </template>
              </el-table-column>
              <el-table-column label="场地" min-width="120">
                <template slot-scope="{ row }">
                  <el-input v-model="row.venue" size="small" placeholder="场地" />
                </template>
              </el-table-column>
              <el-table-column label="容量" min-width="80">
                <template slot-scope="{ row }">
                  <el-input-number v-model="row.capacity" :min="1" size="small" controls-position="right" class="full-width" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="60" fixed="right">
                <template slot-scope="{ $index }">
                  <el-button type="text" size="mini" class="danger-text" @click="removeRow(appointmentSlotList, $index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-tab-pane>

        <!-- ==================== Tab F 团队配置 ==================== -->
        <el-tab-pane label="F 团队配置" name="team">
          <el-card shadow="never" class="section-card">
            <div slot="header" class="section-title">团队预约配置</div>
            <el-row :gutter="24">
              <el-col :xs="24" :md="8">
                <el-form-item label="开启团队">
                  <el-switch v-model="teamEnabled" active-text="开启" inactive-text="关闭" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="8">
                <el-form-item label="最小人数">
                  <el-input-number v-model="form.teamMinSize" :min="0" :max="99" controls-position="right" :disabled="!teamEnabled" class="full-width" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="8">
                <el-form-item label="最大人数">
                  <el-input-number v-model="form.teamMaxSize" :min="0" :max="99" controls-position="right" :disabled="!teamEnabled" class="full-width" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="队长需填字段">
                  <el-checkbox-group v-model="teamLeaderFieldsArr" :disabled="!teamEnabled">
                    <el-checkbox label="姓名" />
                    <el-checkbox label="手机" />
                    <el-checkbox label="身份证" />
                    <el-checkbox label="学校" />
                    <el-checkbox label="年级" />
                  </el-checkbox-group>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :md="12">
                <el-form-item label="队员需填字段">
                  <el-checkbox-group v-model="teamMemberFieldsArr" :disabled="!teamEnabled">
                    <el-checkbox label="姓名" />
                    <el-checkbox label="手机" />
                    <el-checkbox label="身份证" />
                    <el-checkbox label="学校" />
                    <el-checkbox label="年级" />
                  </el-checkbox-group>
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>
        </el-tab-pane>

        <!-- ==================== Tab G 评审老师 ==================== -->
        <el-tab-pane label="G 评审老师" name="reviewer">
          <el-card shadow="never" class="section-card">
            <div slot="header" class="section-title">
              评审老师配置
              <el-button type="primary" size="small" icon="el-icon-plus" style="float:right" @click="addReviewerRow">添加评审</el-button>
            </div>
            <div class="reviewer-hint">配置本赛事的评审老师，评审老师登录后将只能看到本赛事的报名记录。</div>
            <el-table :data="reviewerList" border size="small" class="inline-edit-table">
              <el-table-column label="序号" type="index" width="50" />
              <el-table-column label="老师姓名" min-width="200">
                <template slot-scope="{ row }">
                  <el-select
                    v-model="row.userId"
                    filterable
                    remote
                    :remote-method="searchReviewerUsers"
                    :loading="reviewerSearchLoading"
                    placeholder="搜索系统用户"
                    size="small"
                    class="full-width"
                    @change="onReviewerSelected(row, $event)"
                  >
                    <el-option
                      v-for="u in reviewerUserOptions"
                      :key="u.userId"
                      :label="u.nickName || u.userName"
                      :value="u.userId"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="显示姓名" min-width="140">
                <template slot-scope="{ row }">
                  <el-input v-model="row.reviewerName" size="small" placeholder="老师姓名" />
                </template>
              </el-table-column>
              <el-table-column label="角色" min-width="140">
                <template slot-scope="{ row }">
                  <el-select v-model="row.role" size="small" class="full-width">
                    <el-option label="主评" value="chief_reviewer" />
                    <el-option label="副评" value="reviewer" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="90">
                <template slot-scope="{ row }">
                  <el-switch v-model="row.status" :active-value="1" :inactive-value="0" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" fixed="right">
                <template slot-scope="{ $index }">
                  <el-button type="text" size="mini" class="danger-text" @click="removeRow(reviewerList, $index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="isEdit" class="reviewer-save-row">
              <el-button type="primary" size="small" :loading="reviewerSaving" @click="saveReviewers">保存评审老师</el-button>
            </div>
          </el-card>
        </el-tab-pane>

        <!-- ==================== Tab H 常见问题 ==================== -->
        <el-tab-pane label="H 常见问题" name="faq">
          <el-card shadow="never" class="section-card">
            <div slot="header" class="section-title">
              常见问题（FAQ）
              <el-button type="primary" size="small" icon="el-icon-plus" style="float:right" @click="addFaqRow">添加问题</el-button>
            </div>
            <el-table :data="faqList" border size="small" class="inline-edit-table">
              <el-table-column label="序号" type="index" width="50" />
              <el-table-column label="问题" min-width="200">
                <template slot-scope="{ row }">
                  <el-input v-model="row.question" size="small" placeholder="请输入问题" />
                </template>
              </el-table-column>
              <el-table-column label="回答" min-width="280">
                <template slot-scope="{ row }">
                  <el-input v-model="row.answer" type="textarea" :rows="2" size="small" placeholder="请输入回答" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" fixed="right">
                <template slot-scope="{ $index }">
                  <el-button type="text" size="mini" :disabled="$index === 0" @click="moveRow(faqList, $index, -1)">上移</el-button>
                  <el-button type="text" size="mini" :disabled="$index === faqList.length - 1" @click="moveRow(faqList, $index, 1)">下移</el-button>
                  <el-button type="text" size="mini" class="danger-text" @click="removeRow(faqList, $index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </el-form>

    <div class="sticky-actions">
      <el-button icon="el-icon-back" @click="goBack">返回</el-button>
      <el-button icon="el-icon-view" @click="showPreview = true">预览</el-button>
      <el-button type="primary" plain :loading="saveLoading" @click="saveDraft">保存草稿</el-button>
      <el-button type="primary" :loading="submitLoading" @click="submitAudit">保存并提交审核</el-button>
    </div>

    <!-- ==================== 手机预览 ==================== -->
    <ContestPreview
      :visible.sync="showPreview"
      :contest-data="form"
      :schedule-list="scheduleList"
      :award-list="awardList"
      :faq-list="faqList"
    />

    <!-- ==================== 报名表单快速创建 Drawer ==================== -->
    <el-drawer
      title="快速创建报名表单"
      :visible.sync="showFormTemplateDrawer"
      size="80%"
      direction="rtl"
      append-to-body
    >
      <div class="drawer-body">
        <el-form :model="newTemplate" :rules="templateRules" ref="templateForm" label-width="100px">
          <el-form-item label="模板名称" prop="templateName">
            <el-input v-model="newTemplate.templateName" maxlength="80" placeholder="请输入模板名称" />
          </el-form-item>
        </el-form>
        <div class="drawer-section-title">
          字段列表
          <el-button type="primary" size="small" icon="el-icon-plus" style="float:right" @click="addTemplateField">添加字段</el-button>
        </div>
        <el-table :data="newTemplate.fields" border size="small" class="inline-edit-table">
          <el-table-column label="序号" type="index" width="50" />
          <el-table-column label="字段名" min-width="140">
            <template slot-scope="{ row }">
              <el-input v-model="row.label" size="small" placeholder="如：姓名" />
            </template>
          </el-table-column>
          <el-table-column label="字段类型" min-width="120">
            <template slot-scope="{ row }">
              <el-select v-model="row.type" size="small" class="full-width">
                <el-option label="文本" value="text" />
                <el-option label="单选" value="radio" />
                <el-option label="多选" value="checkbox" />
                <el-option label="文件" value="file" />
                <el-option label="年龄" value="age" />
                <el-option label="数字" value="number" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="必填" width="80">
            <template slot-scope="{ row }">
              <el-switch v-model="row.required" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="60">
            <template slot-scope="{ $index }">
              <el-button type="text" size="mini" class="danger-text" @click="newTemplate.fields.splice($index, 1)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="drawer-footer">
          <el-button @click="showFormTemplateDrawer = false">取消</el-button>
          <el-button type="primary" :loading="templateSaving" @click="saveNewTemplate">保存模板</el-button>
        </div>
      </div>
    </el-drawer>

    <!-- ==================== 证书模板快速创建 Drawer (设计器) ==================== -->
    <el-drawer
      title="快速创建证书模板"
      :visible.sync="showCertDrawer"
      size="95%"
      direction="rtl"
      append-to-body
      :before-close="onCertDrawerClose"
      class="cert-designer-drawer"
    >
      <div class="cert-drawer-body">
        <el-form :model="newCertTemplate" :rules="certTemplateRules" ref="certTemplateForm" :inline="true" size="small" class="cert-name-bar">
          <el-form-item label="模板名称" prop="templateName">
            <el-input v-model="newCertTemplate.templateName" maxlength="80" placeholder="请输入证书模板名称" style="width: 280px" />
          </el-form-item>
        </el-form>
        <CertDesigner
          v-if="showCertDrawer"
          ref="certDesignerInContest"
          owner-type="contest"
          title="证书模板设计器"
          @save="onCertDesignerSave"
        />
      </div>
    </el-drawer>

    <!-- ==================== 批量生成时间段 Dialog ==================== -->
    <el-dialog title="批量生成预约时间段" :visible.sync="showSlotGenerator" width="520px" append-to-body>
      <el-form :model="slotGenForm" label-width="100px" size="small">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="slotGenForm.dateRange"
            type="daterange"
            value-format="yyyy-MM-dd"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            class="full-width"
          />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-picker v-model="slotGenForm.startTime" value-format="HH:mm" format="HH:mm" placeholder="如 09:00" class="full-width" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-time-picker v-model="slotGenForm.endTime" value-format="HH:mm" format="HH:mm" placeholder="如 17:00" class="full-width" />
        </el-form-item>
        <el-form-item label="间隔(分钟)">
          <el-input-number v-model="slotGenForm.interval" :min="15" :max="480" :step="15" controls-position="right" />
        </el-form-item>
        <el-form-item label="每段容量">
          <el-input-number v-model="slotGenForm.capacity" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item label="场地名称">
          <el-input v-model="slotGenForm.venue" placeholder="请输入场地" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="showSlotGenerator = false">取消</el-button>
        <el-button type="primary" @click="generateSlots">生成</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  createPartnerContest,
  getPartnerContest,
  listPartnerFormTemplates,
  createFormTemplate,
  submitPartnerContest,
  updatePartnerContest,
  listContestReviewers,
  saveContestReviewers,
  searchSystemUsers
} from '@/api/partner/contest'
import { listCertTemplates, saveCertTemplate } from '@/api/partner/cert'
import ContestPreview from './components/ContestPreview'
import CertDesigner from './components/CertDesigner/index.vue'

const DEFAULT_FORM = {
  contestId: null,
  contestName: '',
  category: '',
  groupLevels: '',
  coverImage: '',
  bannerImage: '',
  description: '',
  organizer: '',
  coOrganizer: '',
  eventAddress: '',
  totalQuota: 0,
  perUserLimit: 1,
  enrollStartTime: null,
  enrollEndTime: null,
  eventStartTime: null,
  eventEndTime: null,
  scorePublishTime: null,
  aftersaleDeadline: null,
  price: 0,
  supportChannelEnroll: 0,
  supportPointsDeduct: 0,
  supportAppointment: 0,
  appointmentCapacity: 0,
  writeoffMode: 'qr',
  needSignIn: 0,
  allowRepeatAppointment: 0,
  allowAppointmentRefund: 0,
  certIssueMode: 'manual',
  certRuleJson: '',
  scorePublishMode: 'manual',
  scoreRuleJson: '',
  writeoffConfig: '',
  formTemplateId: null,
  aftersaleDays: 0,
  teamMinSize: 0,
  teamMaxSize: 0,
  teamLeaderFields: '',
  teamMemberFields: '',
  recommendTags: ''
}

export default {
  name: 'PartnerContestEdit',
  components: { ContestPreview, CertDesigner },
  dicts: ['jst_contest_category'],
  data() {
    return {
      activeTab: 'basic',
      detailLoading: false,
      saveLoading: false,
      submitLoading: false,
      form: { ...DEFAULT_FORM },
      formTemplateOptions: [],
      certTemplateOptions: [],
      certTemplateIds: [],
      // Sub-table lists
      scheduleList: [],
      awardList: [],
      faqList: [],
      scoreItemList: [],
      appointmentSlotList: [],
      // Reviewers
      reviewerList: [],
      reviewerUserOptions: [],
      reviewerSearchLoading: false,
      reviewerSaving: false,
      // Preview
      showPreview: false,
      // Form template drawer
      showFormTemplateDrawer: false,
      templateSaving: false,
      newTemplate: { templateName: '', fields: [] },
      templateRules: {
        templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }]
      },
      // Cert template drawer
      showCertDrawer: false,
      certTemplateSaving: false,
      newCertTemplate: { templateName: '', backgroundImage: '' },
      certTemplateRules: {
        templateName: [{ required: true, message: '请输入证书模板名称', trigger: 'blur' }]
      },
      // Slot generator dialog
      showSlotGenerator: false,
      slotGenForm: {
        dateRange: [],
        startTime: '09:00',
        endTime: '17:00',
        interval: 60,
        capacity: 20,
        venue: ''
      },
      // Validation rules
      rules: {
        contestName: [{ required: true, message: '请输入赛事名称', trigger: 'blur' }],
        category: [{ required: true, message: '请选择赛事分类', trigger: 'change' }],
        coverImage: [{ required: true, message: '请上传赛事封面图', trigger: 'change' }],
        enrollStartTime: [{ required: true, message: '请选择报名开始时间', trigger: 'change' }],
        enrollEndTime: [{ required: true, message: '请选择报名结束时间', trigger: 'change' }],
        eventStartTime: [{ required: true, message: '请选择比赛开始时间', trigger: 'change' }],
        eventEndTime: [{ required: true, message: '请选择比赛结束时间', trigger: 'change' }],
        price: [{ required: true, message: '请填写报名价格', trigger: 'blur' }],
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
    },
    teamEnabled: {
      get() { return this.form.teamMinSize > 0 },
      set(val) {
        if (!val) {
          this.form.teamMinSize = 0
          this.form.teamMaxSize = 0
        } else if (this.form.teamMinSize === 0) {
          this.form.teamMinSize = 2
          this.form.teamMaxSize = 5
        }
      }
    },
    teamLeaderFieldsArr: {
      get() { return this.form.teamLeaderFields ? this.form.teamLeaderFields.split(',').filter(Boolean) : [] },
      set(val) { this.form.teamLeaderFields = val.join(',') }
    },
    teamMemberFieldsArr: {
      get() { return this.form.teamMemberFields ? this.form.teamMemberFields.split(',').filter(Boolean) : [] },
      set(val) { this.form.teamMemberFields = val.join(',') }
    }
  },
  created() {
    this.loadFormTemplates()
    this.loadCertTemplates()
    if (this.routeContestId) {
      this.loadDetail(this.routeContestId)
    }
  },
  methods: {
    // ==================== Data Loading ====================
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
          needSignIn: this.toSwitch(data.needSignIn),
          appointmentCapacity: Number(data.appointmentCapacity || 0),
          aftersaleDays: Number(data.aftersaleDays || 0),
          price: Number(data.price || 0),
          totalQuota: Number(data.totalQuota || 0),
          perUserLimit: Number(data.perUserLimit || 1),
          teamMinSize: Number(data.teamMinSize || 0),
          teamMaxSize: Number(data.teamMaxSize || 0),
          writeoffMode: data.writeoffMode || 'qr',
          certIssueMode: data.certIssueMode || 'manual',
          scorePublishMode: data.scorePublishMode || 'manual'
        }
        this.scheduleList = Array.isArray(data.scheduleList) ? data.scheduleList : []
        this.awardList = Array.isArray(data.awardList) ? data.awardList : []
        this.faqList = Array.isArray(data.faqList) ? data.faqList : []
        this.scoreItemList = Array.isArray(data.scoreItemList) ? data.scoreItemList : []
        this.appointmentSlotList = Array.isArray(data.appointmentSlotList) ? data.appointmentSlotList : []
        // Load reviewers
        this.loadReviewers(data.contestId || contestId)
        // Hydrate cert template ids from certRuleJson
        this.hydrateCertTemplateIds(data.certRuleJson)
      }).finally(() => {
        this.detailLoading = false
      })
    },
    hydrateCertTemplateIds(json) {
      if (!json) { this.certTemplateIds = []; return }
      try {
        const obj = typeof json === 'object' ? json : JSON.parse(json)
        this.certTemplateIds = Array.isArray(obj.templateIds) ? obj.templateIds : []
      } catch (e) {
        this.certTemplateIds = []
      }
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

    // ==================== Row Operations ====================
    addScheduleRow() {
      this.scheduleList.push({ stageName: '', startTime: null, endTime: null, venue: '', description: '', sortOrder: this.scheduleList.length })
    },
    addScoreItemRow() {
      this.scoreItemList.push({ itemName: '', maxScore: 100, weight: 1.0, sortOrder: this.scoreItemList.length })
    },
    addAwardRow() {
      this.awardList.push({ awardName: '', awardLevel: '', description: '', quota: 0, sortOrder: this.awardList.length })
    },
    addFaqRow() {
      this.faqList.push({ question: '', answer: '', sortOrder: this.faqList.length })
    },
    addSlotRow() {
      this.appointmentSlotList.push({ slotDate: null, startTime: null, endTime: null, venue: '', capacity: 20 })
    },
    removeRow(list, index) {
      list.splice(index, 1)
    },
    moveRow(list, index, direction) {
      const target = index + direction
      if (target < 0 || target >= list.length) return
      const temp = list[target]
      this.$set(list, target, list[index])
      this.$set(list, index, temp)
    },

    // ==================== Award Presets ====================
    fillAwardPreset(command) {
      if (command === 'standard') {
        this.awardList = [
          { awardName: '一等奖', awardLevel: '一等', description: '', quota: 0, sortOrder: 0 },
          { awardName: '二等奖', awardLevel: '二等', description: '', quota: 0, sortOrder: 1 },
          { awardName: '三等奖', awardLevel: '三等', description: '', quota: 0, sortOrder: 2 },
          { awardName: '优秀奖', awardLevel: '优秀', description: '', quota: 0, sortOrder: 3 }
        ]
      } else if (command === 'medal') {
        this.awardList = [
          { awardName: '金奖', awardLevel: '金', description: '', quota: 0, sortOrder: 0 },
          { awardName: '银奖', awardLevel: '银', description: '', quota: 0, sortOrder: 1 },
          { awardName: '铜奖', awardLevel: '铜', description: '', quota: 0, sortOrder: 2 }
        ]
      }
    },

    // ==================== Slot Batch Generator ====================
    generateSlots() {
      const { dateRange, startTime, endTime, interval, capacity, venue } = this.slotGenForm
      if (!dateRange || dateRange.length !== 2) {
        this.$modal.msgWarning('请选择日期范围')
        return
      }
      if (!startTime || !endTime) {
        this.$modal.msgWarning('请选择开始和结束时间')
        return
      }
      const startDate = new Date(dateRange[0])
      const endDate = new Date(dateRange[1])
      const generated = []
      for (let d = new Date(startDate); d <= endDate; d.setDate(d.getDate() + 1)) {
        const dateStr = this.formatDateStr(d)
        const startMinutes = this.timeToMinutes(startTime)
        const endMinutes = this.timeToMinutes(endTime)
        for (let m = startMinutes; m + interval <= endMinutes; m += interval) {
          generated.push({
            slotDate: dateStr,
            startTime: this.minutesToTime(m),
            endTime: this.minutesToTime(m + interval),
            venue: venue,
            capacity: capacity
          })
        }
      }
      if (generated.length === 0) {
        this.$modal.msgWarning('无法生成时间段，请检查参数')
        return
      }
      this.appointmentSlotList = this.appointmentSlotList.concat(generated)
      this.showSlotGenerator = false
      this.$modal.msgSuccess(`已生成 ${generated.length} 个时间段`)
    },
    timeToMinutes(timeStr) {
      const parts = timeStr.split(':')
      return parseInt(parts[0]) * 60 + parseInt(parts[1])
    },
    minutesToTime(minutes) {
      const h = String(Math.floor(minutes / 60)).padStart(2, '0')
      const m = String(minutes % 60).padStart(2, '0')
      return `${h}:${m}`
    },
    formatDateStr(date) {
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      return `${y}-${m}-${d}`
    },

    // ==================== Template Drawers ====================
    addTemplateField() {
      this.newTemplate.fields.push({ label: '', type: 'text', required: false })
    },
    saveNewTemplate() {
      this.$refs.templateForm.validate(valid => {
        if (!valid) return
        if (this.newTemplate.fields.length === 0) {
          this.$modal.msgWarning('请至少添加一个字段')
          return
        }
        this.templateSaving = true
        const data = {
          templateName: this.newTemplate.templateName,
          fieldsJson: JSON.stringify(this.newTemplate.fields)
        }
        createFormTemplate(data).then(() => {
          this.$modal.msgSuccess('表单模板创建成功')
          this.showFormTemplateDrawer = false
          this.newTemplate = { templateName: '', fields: [] }
          this.loadFormTemplates()
        }).catch(() => {
          this.$modal.msgError('创建失败，请重试')
        }).finally(() => {
          this.templateSaving = false
        })
      })
    },
    saveNewCertTemplate() {
      this.$refs.certTemplateForm.validate(valid => {
        if (!valid) return
        this.certTemplateSaving = true
        saveCertTemplate({
          templateName: this.newCertTemplate.templateName,
          backgroundImage: this.newCertTemplate.backgroundImage
        }).then(() => {
          this.$modal.msgSuccess('证书模板创建成功')
          this.showCertDrawer = false
          this.newCertTemplate = { templateName: '', backgroundImage: '' }
          this.loadCertTemplates()
        }).catch(() => {
          this.$modal.msgError('创建失败，请重试')
        }).finally(() => {
          this.certTemplateSaving = false
        })
      })
    },
    onCertDesignerSave({ layoutJson, thumbnail }) {
      this.$refs.certTemplateForm.validate(valid => {
        if (!valid) return
        this.certTemplateSaving = true
        saveCertTemplate({
          templateName: this.newCertTemplate.templateName,
          layoutJson,
          thumbnail
        }).then(() => {
          this.$modal.msgSuccess('证书模板创建成功')
          this.showCertDrawer = false
          this.newCertTemplate = { templateName: '', backgroundImage: '' }
          this.loadCertTemplates()
        }).catch(() => {
          this.$modal.msgError('创建失败，请重试')
        }).finally(() => {
          this.certTemplateSaving = false
        })
      })
    },
    onCertDrawerClose(done) {
      this.$confirm('关闭将丢失未保存的设计，确认关闭？', '提示', {
        type: 'warning'
      }).then(() => done()).catch(() => {})
    },

    // ==================== Reviewers ====================
    addReviewerRow() {
      this.reviewerList.push({ userId: null, reviewerName: '', role: 'reviewer', status: 1 })
    },
    loadReviewers(contestId) {
      if (!contestId) return
      listContestReviewers(contestId).then(response => {
        const data = Array.isArray(response.data) ? response.data : (Array.isArray(response.rows) ? response.rows : [])
        this.reviewerList = data.map(r => ({
          id: r.id,
          userId: r.userId,
          reviewerName: r.reviewerName || '',
          role: r.role || 'reviewer',
          status: r.status != null ? r.status : 1
        }))
      }).catch(() => {
        this.reviewerList = []
      })
    },
    searchReviewerUsers(query) {
      if (!query || query.length < 1) {
        this.reviewerUserOptions = []
        return
      }
      this.reviewerSearchLoading = true
      searchSystemUsers(query).then(response => {
        this.reviewerUserOptions = Array.isArray(response.rows) ? response.rows : []
      }).catch(() => {
        this.reviewerUserOptions = []
      }).finally(() => {
        this.reviewerSearchLoading = false
      })
    },
    onReviewerSelected(row, userId) {
      const user = this.reviewerUserOptions.find(u => u.userId === userId)
      if (user) {
        row.reviewerName = user.nickName || user.userName || ''
      }
    },
    saveReviewers() {
      if (!this.form.contestId) {
        this.$modal.msgWarning('请先保存赛事后再配置评审老师')
        return
      }
      const valid = this.reviewerList.filter(r => r.userId)
      if (!valid.length) {
        this.$modal.msgWarning('请至少添加一名评审老师')
        return
      }
      this.reviewerSaving = true
      saveContestReviewers(this.form.contestId, valid).then(() => {
        this.$modal.msgSuccess('评审老师配置已保存')
        this.loadReviewers(this.form.contestId)
      }).catch(() => {
        this.$modal.msgError('保存失败，请重试')
      }).finally(() => {
        this.reviewerSaving = false
      })
    },

    // ==================== Save & Submit ====================
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
          resolve()
        })
      })
    },
    buildPayload() {
      // Assign sortOrder
      this.scheduleList.forEach((item, i) => { item.sortOrder = i })
      this.awardList.forEach((item, i) => { item.sortOrder = i })
      this.faqList.forEach((item, i) => { item.sortOrder = i })
      this.scoreItemList.forEach((item, i) => { item.sortOrder = i })

      // Build certRuleJson from certTemplateIds
      const certRule = {
        issueMode: this.form.certIssueMode || 'manual',
        templateIds: this.certTemplateIds || []
      }

      return {
        contestId: this.form.contestId,
        contestName: this.form.contestName,
        category: this.form.category,
        groupLevels: this.form.groupLevels,
        coverImage: this.form.coverImage,
        bannerImage: this.form.bannerImage,
        description: this.form.description,
        organizer: this.form.organizer,
        coOrganizer: this.form.coOrganizer,
        eventAddress: this.form.eventAddress,
        totalQuota: this.form.totalQuota,
        perUserLimit: this.form.perUserLimit,
        enrollStartTime: this.form.enrollStartTime,
        enrollEndTime: this.form.enrollEndTime,
        eventStartTime: this.form.eventStartTime,
        eventEndTime: this.form.eventEndTime,
        scorePublishTime: this.form.scorePublishTime,
        aftersaleDeadline: this.form.aftersaleDeadline,
        price: this.form.price,
        supportChannelEnroll: this.toSwitch(this.form.supportChannelEnroll),
        supportPointsDeduct: this.toSwitch(this.form.supportPointsDeduct),
        supportAppointment: this.toSwitch(this.form.supportAppointment),
        appointmentCapacity: this.form.supportAppointment === 1 ? this.form.appointmentCapacity : 0,
        writeoffMode: this.form.writeoffMode || 'qr',
        needSignIn: this.toSwitch(this.form.needSignIn),
        allowRepeatAppointment: this.form.supportAppointment === 1 ? this.toSwitch(this.form.allowRepeatAppointment) : 0,
        allowAppointmentRefund: this.toSwitch(this.form.allowAppointmentRefund),
        certIssueMode: this.form.certIssueMode || 'manual',
        certRuleJson: JSON.stringify(certRule),
        scorePublishMode: this.form.scorePublishMode || 'manual',
        formTemplateId: this.form.formTemplateId,
        aftersaleDays: this.form.aftersaleDays,
        teamMinSize: this.form.teamMinSize,
        teamMaxSize: this.form.teamMaxSize,
        teamLeaderFields: this.form.teamLeaderFields,
        teamMemberFields: this.form.teamMemberFields,
        recommendTags: this.form.recommendTags,
        // Sub-tables
        scheduleList: this.scheduleList,
        awardList: this.awardList,
        faqList: this.faqList,
        scoreItemList: this.scoreItemList,
        appointmentSlotList: this.form.supportAppointment === 1 ? this.appointmentSlotList : []
      }
    },

    // ==================== Helpers ====================
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

.danger-text {
  color: #f56c6c;
}

.inline-edit-table {
  width: 100%;
}

.inline-btn-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.flex-select {
  flex: 1;
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

.drawer-body {
  padding: 20px;
}

.drawer-section-title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2d3d;
  margin: 16px 0 10px;
}

.drawer-footer {
  margin-top: 20px;
  text-align: right;
}

.reviewer-hint {
  margin-bottom: 14px;
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
}

.reviewer-save-row {
  margin-top: 14px;
  text-align: right;
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

  .inline-btn-group {
    flex-wrap: wrap;
  }
}
.cert-drawer-body {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
  padding: 0 12px 12px;
}
.cert-name-bar {
  padding: 8px 0;
  flex-shrink: 0;
}
</style>

<style>
/* Unscoped: cert designer drawer body padding override */
.cert-designer-drawer .el-drawer__body {
  padding: 0;
  overflow: hidden;
}
</style>
