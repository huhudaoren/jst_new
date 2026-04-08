# jst-uniapp 工程

> 竞赛通微信小程序前端
> 架构详见: D:/coding/jst_v1/架构设计/26-Uniapp用户端架构.md
> API 契约: D:/coding/jst_v1/架构设计/27-用户端API契约.md
> 原型参考: D:/coding/jst_v1/小程序原型图/

---

## 当前已就位

```
jst-uniapp/
├── api/                       # 接口封装
│   ├── request.js            # ⭐ 核心,自动注 token + 401 跳登录
│   ├── auth.js               # 登录/资料
│   └── participant.js        # 临时档案
├── store/
│   └── user.js               # Pinia (需先 npm i pinia)
├── pages/
│   └── login/login.vue       # ⭐ 最小登录页 (Mock 联调)
├── App.vue
├── main.js
├── pages.json
└── README.md (本文件)
```

---

## 联调步骤

### 1. 安装依赖
```bash
cd D:\coding\jst_v1\jst-uniapp
npm install
npm install pinia
```

### 2. 配置 main.js (启用 Pinia)

在 `main.js` 中添加:
```javascript
import { createSSRApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'

export function createApp() {
  const app = createSSRApp(App)
  app.use(createPinia())
  return { app }
}
```

### 3. 配置 pages.json (添加 login 页)
```json
{
  "pages": [
    { "path": "pages/login/login", "style": { "navigationBarTitleText": "登录" } },
    { "path": "pages/index/index", "style": { "navigationBarTitleText": "竞赛通" } }
  ]
}
```

### 4. 启动后端
确保 ruoyi-admin 已启动在 `http://127.0.0.1:8080`

### 5. 在 HBuilderX 运行
- 选「运行 → 运行到内置浏览器」
- 或「运行到微信开发者工具」(需配 AppID)

### 6. 测试登录
- 打开 login 页面
- 点击「Mock 登录」按钮
- 看到「登录成功! userId=1001 昵称=测试_张妈妈」即成功

---

## 后续扩展(等 Backend Agent 完成更多 feature 后)

按 26 文档 §2 的目录树扩展:
- pages/index/index.vue (首页)
- pages/contest/list.vue (赛事)
- pages-sub/auth/profile-edit.vue (资料编辑)
- pages-sub/my/binding.vue (我的绑定)
- ...

---

## 强约束 (与后端一致)

❌ 禁止页面内 mock 数据 (架构设计/25-从0到1开发流程.md §3.8)
❌ 禁止绕过 api/request.js 直接 uni.request
✅ 所有接口路径见 27 文档
✅ 测试数据来自后端 99-test-fixtures.sql

---

## 跨域问题

如果浏览器调试报跨域:
- 后端 ruoyi-admin 已默认 CORS 开放(`@CrossOrigin` 在若依 framework 已配)
- 如还有问题,微信开发者工具勾选「不校验合法域名...」
