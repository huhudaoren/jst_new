# 任务报告 - F-CONTRACT-INVOICE-FE 合同/发票小程序页面

## A. 任务前自检（Step 2 答题）

1. **对应原型**: 无专属原型 PNG/HTML（合同/发票为新增功能，原型中无此页面）
2. **调用接口**:
   - `GET /jst/wx/contract/list` — 合同列表
   - `GET /jst/wx/contract/{contractId}` — 合同详情
   - `GET /jst/wx/invoice/list` — 发票列表
   - `POST /jst/wx/invoice/apply` — 申请开票
   - `GET /jst/wx/channel/withdraw/list?status=paid` — 辅助（已打款提现记录，用于开票选结算单）
3. **复用 store/api**: `api/channel.js`（getWithdrawList），新建 `api/finance.js`
4. **新建文件**:
   - `jst-uniapp/api/finance.js`
   - `jst-uniapp/pages-sub/channel/contract-list.vue`
   - `jst-uniapp/pages-sub/channel/contract-detail.vue`
   - `jst-uniapp/pages-sub/channel/invoice-list.vue`
   - `jst-uniapp/pages-sub/channel/invoice-apply.vue`
5. **数据流**:
   - contract-list: onLoad → getContractList → 渲染卡片 → tap → 跳 contract-detail
   - contract-detail: onLoad(id) → getContractDetail → 渲染详情 → 查看文件按钮(downloadFile+openDocument)
   - invoice-list: onLoad → getInvoiceList → 渲染卡片 → 右上角"申请开票"跳 invoice-apply
   - invoice-apply: loadSettlements → 选结算单(弹层) → 填表单 → applyInvoice → navigateBack
6. **双视角**: 否，仅渠道方可见（my/index.vue 渠道方视角"基础管理"入口）
7. **复用样板**: `withdraw-list.vue` 的 Tab + 分页模式；`rebate.vue` 的导航栏风格
8. **核心 Token**:
   - 卡片背景: `$jst-bg-card`
   - 品牌色/Tab 激活: `$jst-brand`
   - 卡片圆角: `$jst-radius-md`
   - 卡片阴影: `$jst-shadow-sm`
   - 金额颜色: `$jst-warning`
   - 时间/辅助文字: `$jst-text-secondary`
   - 页面背景: `$jst-bg-page`
   - 间距: `$jst-space-xl` / `$jst-page-padding`

## B. 交付物清单

### 新增文件 (5)

| 文件 | 说明 |
|---|---|
| `jst-uniapp/api/finance.js` | 合同 + 发票 API 封装（5 个函数） |
| `jst-uniapp/pages-sub/channel/contract-list.vue` | 合同列表：Tab 筛选(全部/待签署/已签署/已到期) + 分页 + 状态标签 |
| `jst-uniapp/pages-sub/channel/contract-detail.vue` | 合同详情：状态卡 + 信息区块 + 文件预览(downloadFile+openDocument) + 待签署提示 |
| `jst-uniapp/pages-sub/channel/invoice-list.vue` | 发票列表：Tab 筛选(全部/待审核/开票中/已开票) + 金额展示 + 右上角"申请开票"入口 |
| `jst-uniapp/pages-sub/channel/invoice-apply.vue` | 申请开票：结算单选择弹层(u-popup) + 表单(抬头/税号/金额/备注) + 税号正则校验 |

### 修改文件 (2)

| 文件 | 变更内容 |
|---|---|
| `jst-uniapp/pages.json` | `pages-sub/channel` subPackage 追加 4 条路由：contract-list, contract-detail, invoice-list, invoice-apply |
| `jst-uniapp/pages/my/index.vue` | 渠道方视角"基础管理"section 追加 2 个 cell（合同中心 + 开票中心）+ 2 个 navigate 方法 |

## C. 联调测试预期

1. 渠道方用户 → 我的 → 切换渠道方视角 → 基础管理 → 显示"合同中心"(📄蓝底) + "开票中心"(🧾橙底)
2. 点"合同中心" → contract-list 页 → 4 Tab 切换筛选 → 触底分页加载 → 点卡片跳详情
3. 合同详情 → 居中大号状态标签 + 合同编号 → 信息区(类型/生效/签署/到期/备注) → 有 fileUrl 时显示"查看合同文件"按钮 → 待签署状态显示黄色提示
4. 点"开票中心" → invoice-list 页 → 4 Tab 切换 → 卡片显示抬头+金额(¥+两位小数)+状态
5. 右上角"申请开票" → invoice-apply → 点选结算单(底部弹层，展示已打款提现记录) → 自动填充金额 → 填抬头+税号 → 税号校验(15-20位字母数字) → 提交成功 toast → 1.5s 后返回列表
6. 空数据时显示 u-empty，加载中/无更多显示 u-loadmore
7. 网络异常 → request.js 统一 toast
8. 401 → 自动跳登录页

## D. 视觉对比

- 无原型 PNG 对标（新增功能），视觉风格完全对齐现有 channel/ 页面
- Tab 样式: 复用 withdraw-list.vue 的 scroll-view + active 下划线模式
- 卡片样式: 复用统一的 card + head/body 布局
- 导航栏: 复用 orders.vue 的 back + title 模式
- 所有颜色/间距/圆角/阴影使用 Design Token，零硬编码

## E. 遗留 TODO

| 项 | 说明 |
|---|---|
| 后端接口未实现 | F-CONTRACT-INVOICE-BE 需先完成，当前前端可编译但无数据 |
| 合同文件预览 | 依赖真实 OSS 文件 URL，当前 downloadFile + openDocument 逻辑已就绪 |
| 发票详情页 | 任务卡未要求，api/finance.js 已预留 getInvoiceDetail，后续可按需补页面 |

## F. 我做了任务卡之外的什么

- 无

## G. 自检确认

- [x] 没有页面内 mock 数据
- [x] 所有 API 通过 api/request.js 封装（api/finance.js）
- [x] 金额统一 ¥ + toFixed(2) 格式
- [x] 没有引入新依赖
- [x] 没有改 RuoYi-Vue
- [x] 没有改架构文档
- [x] DOM 标签已全部转为 view/text/image
- [x] 样式全部应用 design-tokens.scss 变量，未硬编码像素值
- [x] 使用 uView 组件（u-tag / u-empty / u-loadmore / u-button / u-popup / u-loading-icon）
- [x] 税号校验: `/^[A-Za-z0-9]{15,20}$/`
- [x] 安全区处理: 详情页和申请页 padding-bottom: env(safe-area-inset-bottom)
- [x] 全局 navBarMixin 自动注入 navPaddingTop，所有页面顶部已应用
