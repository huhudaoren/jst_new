# F-CONTRACT-INVOICE-FE — 合同/发票小程序页面

> 优先级：P2 | 预估：S | Agent：MiniProgram Agent
> 依赖：F-CONTRACT-INVOICE-BE（需要后端接口就绪）

---

## 一、背景

渠道方 my/index.vue 的"合同中心"和"开票中心"入口此前已移除（F-2 批次灰标）。现后端接口即将补齐，需新建 4 个小程序页面并恢复入口。

## 二、新增页面（4 页）

### 1. `pages-sub/channel/contract-list.vue` — 合同列表

**接口**：`GET /jst/wx/contract/list`

**UI 结构**：
- 顶部筛选 Tab：全部 / 待签署 / 已签署 / 已到期
- 列表卡片：合同编号 + 合同类型标签 + 状态标签 + 生效时间 + 签署时间
- 空态：u-empty
- 加载更多：u-loadmore

**状态标签映射**（§4.8 规范）：
- draft → info "草稿"
- pending_sign → warning "待签署"
- signed → success "已签署"
- expired → info "已到期"
- archived → info "已归档"

**合同类型标签**：
- partner_coop → "合作协议"
- channel_settle → "结算合同"
- supplement → "补充协议"

### 2. `pages-sub/channel/contract-detail.vue` — 合同详情

**接口**：`GET /jst/wx/contract/{contractId}`

**UI 结构**：
- 顶部状态卡：大号状态标签 + 合同编号
- 信息区块：合同类型、生效时间、签署时间、备注
- 文件预览区：fileUrl 不为空时展示"查看合同文件"按钮，调用 `uni.downloadFile` + `uni.openDocument` 或 web-view 打开 PDF
- 底部操作：已签署状态无操作，待签署可提示联系管理员

### 3. `pages-sub/channel/invoice-list.vue` — 发票列表

**接口**：`GET /jst/wx/invoice/list`

**UI 结构**：
- 顶部筛选 Tab：全部 / 待审核 / 开票中 / 已开票
- 列表卡片：发票号 + 抬头 + 金额（¥ 格式）+ 状态标签 + 申请时间
- 右上角"申请开票"按钮 → 跳转 invoice-apply
- 空态 + 加载更多

**状态标签映射**：
- pending_apply → warning "待审核"
- reviewing → warning "审核中"
- issuing → primary "开票中"
- issued → success "已开票"
- voided → danger "已作废"
- red_offset → danger "红冲"

### 4. `pages-sub/channel/invoice-apply.vue` — 申请开票

**接口**：`POST /jst/wx/invoice/apply`

**UI 结构**：
- 表单：
  - 关联结算单号（可选择，从提现记录中选取已打款的单号）
  - 发票抬头（必填）
  - 税号（必填，格式校验 15-20 位字母数字）
  - 金额（自动填充，只读或可调小）
  - 备注（选填）
- 提交按钮（u-button type="primary"，loading 态）
- 提交成功后跳转 invoice-list

**辅助接口**：`GET /jst/wx/channel/withdraw/list?status=paid` 获取可开票的提现记录

## 三、路由注册

在 `pages.json` 的 `pages-sub/channel` subPackage 中追加：

```json
{ "path": "contract-list", "style": { "navigationBarTitleText": "合同中心", "navigationStyle": "custom" } },
{ "path": "contract-detail", "style": { "navigationBarTitleText": "合同详情", "navigationStyle": "custom" } },
{ "path": "invoice-list", "style": { "navigationBarTitleText": "开票中心", "navigationStyle": "custom" } },
{ "path": "invoice-apply", "style": { "navigationBarTitleText": "申请开票", "navigationStyle": "custom" } }
```

## 四、API 文件

新建 `jst-uniapp/api/finance.js`：

```javascript
import request from '@/utils/request'

// 合同
export function getContractList(params) {
  return request({ url: '/jst/wx/contract/list', method: 'get', params })
}
export function getContractDetail(contractId) {
  return request({ url: `/jst/wx/contract/${contractId}`, method: 'get' })
}

// 发票
export function getInvoiceList(params) {
  return request({ url: '/jst/wx/invoice/list', method: 'get', params })
}
export function getInvoiceDetail(invoiceId) {
  return request({ url: `/jst/wx/invoice/${invoiceId}`, method: 'get' })
}
export function applyInvoice(data) {
  return request({ url: '/jst/wx/invoice/apply', method: 'post', data })
}
```

## 五、入口恢复

在 `pages/my/index.vue` 渠道方视角 "基础管理" section 中追加 2 个 cell：

```html
<view class="my-page__cell" @tap="navigateContractList">
  <view class="my-page__cell-icon my-page__cell-icon--blue">📄</view>
  <view class="my-page__cell-body">
    <text class="my-page__cell-title">合同中心</text>
  </view>
  <text class="my-page__cell-arrow">›</text>
</view>
<view class="my-page__cell" @tap="navigateInvoiceList">
  <view class="my-page__cell-icon my-page__cell-icon--orange">🧾</view>
  <view class="my-page__cell-body">
    <text class="my-page__cell-title">开票中心</text>
  </view>
  <text class="my-page__cell-arrow">›</text>
</view>
```

对应方法：
```javascript
navigateContractList() { uni.navigateTo({ url: '/pages-sub/channel/contract-list' }) },
navigateInvoiceList() { uni.navigateTo({ url: '/pages-sub/channel/invoice-list' }) },
```

## 六、视觉规范

- 遵循 Design Token 体系（`@/styles/design-tokens.scss`），零硬编码
- 使用 uView 组件（u-button / u-empty / u-loadmore / u-tag）
- 状态标签对齐 §4.8 语义色映射
- 金额展示：¥ 前缀 + 两位小数

## 七、DoD
- [ ] 4 个新页面功能完整
- [ ] pages.json 路由注册
- [ ] api/finance.js 接口封装
- [ ] my/index.vue 入口恢复（渠道方视角）
- [ ] Design Token 零硬编码
- [ ] script 块无非业务改动
- [ ] 报告交付
