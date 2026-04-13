<template>
  <div class="login-page">
    <div class="login-shell">
      <section class="login-brand">
        <p class="brand-tag">JingSaiTong Admin</p>
        <h1>{{ title }}</h1>
        <p class="brand-desc">统一管理赛事、订单、营销与运营数据，支持桌面和手机端快速处理后台任务。</p>
        <ul class="brand-list">
          <li>赛事全链路审核与发布</li>
          <li>订单、退款、预约一站管理</li>
          <li>积分商城与营销配置可视化</li>
        </ul>
      </section>

      <section class="login-panel">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <h3 class="title">后台登录</h3>
          <p class="title-sub">请输入账号信息后继续</p>

          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="账号"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="密码"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item v-if="captchaEnabled" prop="code">
            <div class="code-row">
              <el-input
                v-model="loginForm.code"
                auto-complete="off"
                placeholder="验证码"
                @keyup.enter.native="handleLogin"
              >
                <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
              </el-input>
              <div class="login-code">
                <img :src="codeUrl" @click="getCode" class="login-code-img" />
              </div>
            </div>
          </el-form-item>

          <div class="remember-row">
            <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
            <router-link v-if="register" class="link-type" :to="'/register'">立即注册</router-link>
          </div>

          <el-form-item class="submit-item">
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              class="submit-btn"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
          </el-form-item>
        </el-form>
      </section>
    </div>

    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'
import defaultSettings from '@/settings'

export default {
  name: "Login",
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      footerContent: defaultSettings.footerContent,
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true,
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(() => {})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.login-page {
  --jst-blue: #2563eb;
  --jst-cyan: #0f766e;
  --jst-gold: #f59e0b;
  position: relative;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 28px 24px 72px;
  background-image:
    linear-gradient(135deg, rgba(6, 95, 70, 0.84) 0%, rgba(29, 78, 216, 0.82) 50%, rgba(15, 23, 42, 0.86) 100%),
    url("../assets/images/login-background.jpg");
  background-size: cover;
  background-position: center;
  overflow: hidden;
  isolation: isolate;
}

.login-page::before,
.login-page::after {
  content: '';
  position: absolute;
  border-radius: 999px;
  filter: blur(0px);
  opacity: .22;
  z-index: -1;
}

.login-page::before {
  width: 360px;
  height: 360px;
  top: -120px;
  left: -90px;
  background: radial-gradient(circle at center, rgba(251, 191, 36, 0.9) 0%, rgba(251, 191, 36, 0) 70%);
}

.login-page::after {
  width: 420px;
  height: 420px;
  right: -130px;
  bottom: -160px;
  background: radial-gradient(circle at center, rgba(45, 212, 191, 0.85) 0%, rgba(45, 212, 191, 0) 70%);
}

.login-shell {
  width: min(1000px, 100%);
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  border-radius: 22px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.26);
  box-shadow: 0 24px 64px rgba(15, 23, 42, 0.38);
  background: rgba(248, 250, 252, 0.14);
  backdrop-filter: blur(14px);
}

.login-brand {
  padding: 48px 42px;
  color: #fff;
  background:
    linear-gradient(155deg, rgba(15, 23, 42, 0.66) 0%, rgba(8, 47, 73, 0.44) 60%, rgba(7, 89, 133, 0.32) 100%);

  h1 {
    margin: 10px 0 14px;
    font-size: 34px;
    line-height: 1.25;
    letter-spacing: .01em;
  }
}

.brand-tag {
  display: inline-block;
  margin: 0;
  padding: 5px 12px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: .08em;
  text-transform: uppercase;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.28);
}

.brand-desc {
  margin: 0 0 22px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.7;
}

.brand-list {
  margin: 0;
  padding: 0;
  list-style: none;

  li {
    position: relative;
    margin-bottom: 12px;
    padding-left: 18px;
    color: rgba(255, 255, 255, 0.9);
    line-height: 1.6;
  }

  li::before {
    content: '';
    position: absolute;
    left: 0;
    top: 10px;
    width: 8px;
    height: 8px;
    border-radius: 999px;
    background: #fcd34d;
  }
}

.login-panel {
  padding: 38px 34px 30px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96) 0%, rgba(248, 250, 252, 0.95) 100%);
}

.login-form {
  width: 100%;
}

.title {
  margin: 0;
  text-align: left;
  color: #0f172a;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: .01em;
}

.title-sub {
  margin: 10px 0 24px;
  color: #5b6b84;
  font-size: 13px;
}

.login-form ::v-deep .el-form-item {
  margin-bottom: 18px;
}

.login-form ::v-deep .el-input {
  height: 44px;

  input {
    height: 44px;
    padding-left: 42px;
    border-radius: 12px;
    border: 1px solid #d4deed;
    background: rgba(255, 255, 255, 0.96);
    transition: all .2s ease;
  }

  input:focus {
    border-color: var(--jst-blue);
    box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.14);
  }
}

.input-icon {
  height: 44px;
  width: 14px;
  margin-left: 2px;
}

.code-row {
  display: grid;
  grid-template-columns: 1fr 120px;
  gap: 10px;
  align-items: center;
}

.login-code {
  width: 100%;
  height: 44px;

  img {
    display: block;
    width: 100%;
    height: 44px;
    border-radius: 12px;
    cursor: pointer;
    border: 1px solid #d4deed;
  }
}

.remember-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0 0 20px;
  font-size: 13px;
}

.submit-item {
  margin-bottom: 0;
}

.submit-btn {
  width: 100%;
  height: 44px;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  background: linear-gradient(120deg, var(--jst-blue) 0%, var(--jst-cyan) 62%, var(--jst-gold) 120%);
  box-shadow: 0 12px 24px rgba(37, 99, 235, 0.3);
}

.link-type {
  color: var(--jst-blue);
}

.el-login-footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 40px;
  line-height: 40px;
  text-align: center;
  color: rgba(255, 255, 255, 0.9);
  font-family: Arial;
  font-size: 12px;
  letter-spacing: .6px;
  background: linear-gradient(180deg, rgba(15, 23, 42, 0) 0%, rgba(15, 23, 42, 0.4) 100%);
  pointer-events: none;
}

.login-code-img {
  height: 44px;
}

@media (max-width: 1024px) {
  .login-shell {
    grid-template-columns: 1fr;
    max-width: 540px;
  }

  .login-brand {
    display: block;
    padding: 24px 24px 14px;

    h1 {
      font-size: 26px;
      margin-bottom: 8px;
    }
  }

  .brand-desc {
    margin-bottom: 0;
    font-size: 13px;
  }

  .brand-list {
    display: none;
  }

  .login-panel {
    padding: 24px 24px 20px;
  }
}

@media (max-width: 768px) {
  .login-page {
    padding: 16px 14px 72px;
    align-items: center;
  }

  .login-shell {
    margin-top: 0;
    max-width: 420px;
    border-radius: 16px;
    box-shadow: 0 18px 40px rgba(15, 23, 42, 0.3);
  }

  .login-brand {
    padding: 18px 16px 12px;

    h1 {
      margin: 8px 0 4px;
      font-size: 22px;
    }
  }

  .brand-tag {
    font-size: 11px;
    padding: 4px 10px;
  }

  .brand-desc {
    font-size: 12px;
    line-height: 1.6;
  }

  .login-panel {
    padding: 18px 14px 14px;
  }

  .title {
    font-size: 22px;
  }

  .title-sub {
    margin: 6px 0 18px;
  }

  .code-row {
    grid-template-columns: 1fr 108px;
  }

  .remember-row {
    flex-wrap: wrap;
    row-gap: 8px;
  }

  .submit-btn {
    min-height: 46px;
  }

  .el-login-footer {
    height: 34px;
    line-height: 34px;
    font-size: 11px;
  }
}

@media (max-width: 420px) {
  .login-page {
    padding: 12px 10px 70px;
  }

  .login-shell {
    border-radius: 14px;
  }

  .login-brand {
    padding: 16px 12px 10px;
  }

  .login-panel {
    padding: 16px 12px 12px;
  }

  .code-row {
    grid-template-columns: 1fr;
  }

  .login-code,
  .login-code-img {
    height: 42px;
  }

  .el-login-footer {
    letter-spacing: .3px;
  }
}
</style>
