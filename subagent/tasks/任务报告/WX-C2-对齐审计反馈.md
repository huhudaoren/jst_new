# 致主 Agent：WX-C2 对齐审计反馈

> 发起人：WX-C2 子 Agent
> 时间：2026-04-09
> 类型：审计反馈 + 疑点请示（非任务卡）

主 Agent 您好，我是刚完成 WX-C2 渠道方前端的子 Agent。按您要求对已完成的 P*/WX* 任务做了原型对齐审计，有如下发现与建议请您裁决。

---

## 一、审计结论

核心闭环（P1~P6 + WX-C1 + WX-C2）基本对齐原型。**仅发现 1 处纯前端瑕疵 + 4 处因后端接口缺失导致的原型未对齐**，后者全部集中在渠道方模块。

---

## 二、问题清单

### 纯前端问题（1 条）

- `jst-uniapp/pages/my/index.vue:L41-L54` 菜单 tile 重复：已实现的"我的订单/报名/课程"真实导航项与旧的 toast 占位项并存。这是 P1 遗留，我在做 WX-C2 时未清理（不敢越权改非本任务文件）。影响：用户点到占位项会看到"待开放"toast，体验割裂。

### 因后端接口缺失而未对齐（4 条）

这几条全部是我在 WX-C2 交付时做了骨架页/占位的地方，当时按任务卡约定"等 F-CHANNEL-DASHBOARD 后端接口 ready 再迭代"。审计确认后端目前**确实没有**这些接口：

| 涉及页面 | 缺失接口（建议路径） | 当前状态 |
|---|---|---|
| `channel-home.png` · 月度 KPI | `GET /jst/wx/channel/dashboard/monthly` | `home.vue` 里写死 `--` 占位 |
| `channel-orders.png` | `GET /jst/wx/channel/orders` | `orders.vue` 仅"功能开发中"骨架 |
| `channel-students.png` | `GET /jst/wx/channel/students` | `students.vue` 骨架 |
| `channel-data.png` | `GET /jst/wx/channel/data/stats` | `data.vue` 骨架 |

> 备注：C5a 的 6 个返点/提现接口**已完整实现并被前端正确接入**，WX-C2 核心页（rebate / withdraw-*）无对齐问题。

---

## 三、需要您裁决的疑点

在您出任务卡前，有几个问题我拿不准，需要您拍板：

1. **`F-CHANNEL-DASHBOARD` 该拆几个接口？**
   四个页面的数据差异较大（KPI 数字 vs 订单列表 vs 学生列表 vs 图表），是合并成 1 个大 dashboard 接口，还是拆成 4 个独立 RESTful 接口？我倾向后者（分页/筛选各自独立更干净），但需要您定调。

2. **`channel-orders` 的业务边界不清**：
   这里的"订单"是指"绑定到本渠道的学生所下的订单"吗？与 `jst-order` 模块的关系是新增 wx controller 还是 join 查询？是否涉及新表？这块我没有足够上下文，**建议您先明确业务口径再出后端任务卡**。

3. **P1 遗留的 profile-edit 字段持久化**（审计顺带发现）：
   `profile-edit.vue` 允许编辑 `birthday` / `guardianMobile`，但我没在后端 `WxUserController` 的更新白名单里看到这俩字段的映射，可能存在"前端能填、后端丢弃"的静默失败。**但这是 P1 范围，不是我 WX-C2 的锅**，只是顺手提醒，是否要修请您判断。

4. **优先级排序**：
   从用户可见度看我的建议是 `my/index.vue 菜单清理（P0）` → `channel-home KPI（P1）` → `orders/students/data 三骨架页（P2）`。是否符合您当前的 roadmap？

---

## 四、建议的整改顺序（供您参考）

如果您认可上述问题，我建议后续任务卡按这个依赖顺序派发，避免返工：

```
【即时】my/index.vue 菜单清理              ← 纯前端，与其他解耦
          │
【后端先行】F-CHANNEL-DASHBOARD 4 接口       ← 阻塞下面全部
          ├──→ channel-home KPI 接入
          └──→ orders/students/data 三页展开
```

- `my/index.vue` 那条可以单独起一张小任务卡或塞进 `DEBT-2` 合并清理
- 后端四接口建议作为一个整任务卡打包（业务同源，DTO 可复用）
- 前端再按页面粒度拆 2~3 张

---

## 五、我的请求

请您审阅上述问题清单 + 疑点，确认：

- 是否认可这 5 条技术债的定性与优先级？
- 第三节 4 个疑点您的倾向是什么？尤其是 **F-CHANNEL-DASHBOARD 的接口拆分粒度**和 **channel-orders 的业务口径**，这两点会直接影响任务卡内容。
- 是否有我没发现的其他对齐问题需要我补查？（我只详查了 WX-C2 和 P1 my 页，P2~P6、WX-C1 仅做了粗扫）

等您确认方向后，您再派发具体任务卡，我按卡执行。
