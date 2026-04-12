<template>
  <div class="app-container partner-settings-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">账号设置</p>
        <h2>赛事方账号管理</h2>
        <p class="hero-desc">维护赛事方账号资料、联系方式与登录密码。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="loadProfile">刷新信息</el-button>
    </div>

    <el-row :gutter="16" class="section-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="section-card" v-loading="loading">
          <div slot="header" class="section-header">
            <span>赛事方信息</span>
          </div>
          <el-descriptions :column="isMobile ? 1 : 2" border class="info-desc">
            <el-descriptions-item label="账号">{{ user.userName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ user.nickName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ user.phonenumber || '--' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ user.email || '--' }}</el-descriptions-item>
            <el-descriptions-item label="角色组" :span="isMobile ? 1 : 2">{{ roleGroup || '--' }}</el-descriptions-item>
            <el-descriptions-item label="岗位组" :span="isMobile ? 1 : 2">{{ postGroup || '--' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间" :span="isMobile ? 1 : 2">{{ user.createTime || '--' }}</el-descriptions-item>
          </el-descriptions>
          <el-alert
            title="赛事方扩展资料（机构名称、资质、结算信息）由入驻档案模块维护。"
            type="info"
            :closable="false"
            show-icon
            class="tip-alert"
          />
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="section-card">
          <div slot="header" class="section-header">
            <span>联系方式编辑</span>
          </div>
          <el-form ref="contactFormRef" :model="contactForm" :rules="contactRules" label-width="86px">
            <el-form-item label="昵称" prop="nickName">
              <el-input v-model.trim="contactForm.nickName" maxlength="30" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="手机号" prop="phonenumber">
              <el-input v-model.trim="contactForm.phonenumber" maxlength="11" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model.trim="contactForm.email" maxlength="50" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="contactForm.sex">
                <el-radio label="0">男</el-radio>
                <el-radio label="1">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item class="form-actions">
              <el-button type="primary" :loading="contactSaving" @click="submitContact">保存联系方式</el-button>
              <el-button @click="resetContactForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="section-card password-card">
      <div slot="header" class="section-header">
        <span>修改密码</span>
      </div>
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="92px">
        <el-row :gutter="16">
          <el-col :xs="24" :md="8">
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入旧密码" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="8">
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="8">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请确认新密码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item class="form-actions">
          <el-button type="primary" :loading="passwordSaving" @click="submitPassword">更新密码</el-button>
          <el-button @click="resetPasswordForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getUserProfile, updateUserProfile, updateUserPwd } from '@/api/system/user'

export default {
  name: 'PartnerSettings',
  data() {
    const confirmPwdValidator = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
        return
      }
      callback()
    }
    return {
      loading: false,
      contactSaving: false,
      passwordSaving: false,
      user: {},
      roleGroup: '',
      postGroup: '',
      contactForm: {
        nickName: '',
        phonenumber: '',
        email: '',
        sex: '0'
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      contactRules: {
        nickName: [{ required: true, message: '昵称不能为空', trigger: 'blur' }],
        phonenumber: [
          { required: true, message: '手机号不能为空', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '邮箱不能为空', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ]
      },
      passwordRules: {
        oldPassword: [{ required: true, message: '旧密码不能为空', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '新密码不能为空', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
          { pattern: /^[^<>"'|\\]+$/, message: '不能包含非法字符：< > \" \' \\ |', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '确认密码不能为空', trigger: 'blur' },
          { validator: confirmPwdValidator, trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    }
  },
  created() {
    this.loadProfile()
  },
  methods: {
    async loadProfile() {
      this.loading = true
      try {
        const res = await getUserProfile()
        this.user = res.data || {}
        this.roleGroup = res.roleGroup || ''
        this.postGroup = res.postGroup || ''
        this.contactForm = {
          nickName: this.user.nickName || '',
          phonenumber: this.user.phonenumber || '',
          email: this.user.email || '',
          sex: this.user.sex || '0'
        }
      } finally {
        this.loading = false
      }
    },
    resetContactForm() {
      this.contactForm = {
        nickName: this.user.nickName || '',
        phonenumber: this.user.phonenumber || '',
        email: this.user.email || '',
        sex: this.user.sex || '0'
      }
      this.resetForm('contactFormRef')
    },
    submitContact() {
      this.$refs.contactFormRef.validate(async valid => {
        if (!valid) return
        this.contactSaving = true
        try {
          await updateUserProfile({
            nickName: this.contactForm.nickName,
            phonenumber: this.contactForm.phonenumber,
            email: this.contactForm.email,
            sex: this.contactForm.sex
          })
          this.$modal.msgSuccess('联系方式更新成功')
          await this.loadProfile()
        } finally {
          this.contactSaving = false
        }
      })
    },
    resetPasswordForm() {
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      this.resetForm('passwordFormRef')
    },
    submitPassword() {
      this.$refs.passwordFormRef.validate(async valid => {
        if (!valid) return
        this.passwordSaving = true
        try {
          await updateUserPwd(this.passwordForm.oldPassword, this.passwordForm.newPassword)
          this.$modal.msgSuccess('密码修改成功')
          this.resetPasswordForm()
        } finally {
          this.passwordSaving = false
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.partner-settings-page {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.page-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
  margin-bottom: 16px;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.hero-eyebrow {
  margin: 0 0 8px;
  color: #2f6fec;
  font-size: 13px;
  font-weight: 600;
}

.page-hero h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #172033;
}

.hero-desc {
  margin: 8px 0 0;
  color: #6f7b8f;
}

.section-row {
  margin-bottom: 16px;
}

.section-card {
  border: 1px solid #e5eaf2;
}

.section-header {
  font-weight: 600;
  color: #172033;
}

.info-desc {
  margin-bottom: 12px;
}

.tip-alert {
  margin-top: 12px;
}

.password-card {
  margin-bottom: 8px;
}

.form-actions {
  margin-bottom: 0;
}

@media (max-width: 768px) {
  .partner-settings-page {
    padding: 12px;
  }

  .page-hero {
    display: block;
    padding: 18px;
  }

  .page-hero .el-button {
    width: 100%;
    min-height: 44px;
    margin-top: 16px;
  }

  .page-hero h2 {
    font-size: 20px;
  }

  .form-actions ::v-deep .el-button {
    width: 100%;
    margin: 0 0 10px;
  }

  .form-actions ::v-deep .el-button + .el-button {
    margin-left: 0;
  }
}
</style>
