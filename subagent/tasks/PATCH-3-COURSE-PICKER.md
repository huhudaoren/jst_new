# PATCH-3 — CoursePicker 补齐 + courseId 收尾

> 优先级：**🔴 P0** | 预估：**XS（1-2h）** | Agent：**Web Admin Agent**（跨栈）
> 派发时间：2026-04-18 | 版本：任务卡 v1
> 起因：POLISH-PICKER-APPLY §8.1 阻塞——`jst_course_learn_record/index.vue` 的 `courseId` 仍是手输 ID 框，仓库无 CoursePicker

---

## 一、必读上下文

1. `RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/ChannelPicker.vue`（**抄它**）
2. `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/EntityBriefController.java`（扩 case）
3. `RuoYi-Vue/ruoyi-ui/src/main.js`（全局注册）
4. `RuoYi-Vue/ruoyi-ui/src/utils/entityRouteMap.js`
5. `RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_course_learn_record/index.vue`（替换目标）

---

## 二、Step 0：DESCRIBE jst_course

```bash
mysql -u root -p123456 jst -e "DESCRIBE jst_course"
```

**预期字段**（以实际为准）：`course_id`, `course_name`, `category`/`course_type`, `status`, `del_flag`。
如果字段名不一致，按实际改 SQL。

---

## three、Step 1：后端扩 EntityBriefController

**文件**：`RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/EntityBriefController.java`

`searchByType` 加 case：
```java
case "course": return searchCourse(kw, limit);
```

`buildBriefSql` 加 case：
```java
case "course":
    return "SELECT course_id AS id, course_name AS name, category AS subtitle, status AS statusTag"
            + " FROM jst_course WHERE del_flag = '0' AND course_id = ?";
```

新增 private 方法（**字段名以 DESCRIBE 为准**）：
```java
/**
 * jst_course: course_id, course_name, category, status (DESCRIBE 实测)
 */
private List<Map<String, Object>> searchCourse(String kw, int limit) {
    String base = "SELECT course_id AS id, course_name AS name, category AS subtitle, status AS statusTag"
            + " FROM jst_course WHERE del_flag = '0' ";
    if (StringUtils.isNotBlank(kw)) {
        String like = "%" + kw + "%";
        return jdbcTemplate.queryForList(
                base + "AND course_name LIKE ? ORDER BY course_id DESC LIMIT ?",
                like, limit);
    }
    return jdbcTemplate.queryForList(base + "ORDER BY course_id DESC LIMIT ?", limit);
}
```

更新顶部 Javadoc 字段表加一行 `jst_course: course_id, course_name, ...`。

---

## 四、Step 2：前端 CoursePicker

**新文件**：`RuoYi-Vue/ruoyi-ui/src/components/RelationPicker/CoursePicker.vue`

```vue
<template>
  <base-picker :value="value" entity="course" :placeholder="placeholder" :clearable="clearable" :disabled="disabled" :show-detail-link="showDetailLink" @input="$emit('input', $event)" @change="$emit('change', $event)" />
</template>
<script>
import BasePicker from './BasePicker.vue'
export default {
  name: 'CoursePicker',
  components: { BasePicker },
  props: {
    value: { type: [Number, String], default: null },
    placeholder: { type: String, default: '请选择课程（输入名称搜索）' },
    clearable: { type: Boolean, default: true },
    disabled: { type: Boolean, default: false },
    showDetailLink: { type: Boolean, default: true }
  }
}
</script>
```

---

## 五、Step 3：全局注册 + 路由映射

**文件 1**：`RuoYi-Vue/ruoyi-ui/src/main.js`

照其他 11 个 Picker 的方式加：
```javascript
import CoursePicker from '@/components/RelationPicker/CoursePicker.vue'
Vue.component('CoursePicker', CoursePicker)
```

**文件 2**：`RuoYi-Vue/ruoyi-ui/src/utils/entityRouteMap.js`

```javascript
course: { path: '/jst/course/edit', perm: 'jst:event:jst_course:list' }
```

**注**：实际 path 用 grep 确认：
```bash
grep -rn "name: 'Course'\|path: '/jst/course'" RuoYi-Vue/ruoyi-ui/src/router | head -5
```
按真实路由写。

---

## 六、Step 4：替换 jst_course_learn_record/index.vue

**文件**：`RuoYi-Vue/ruoyi-ui/src/views/jst/event/jst_course_learn_record/index.vue`

定位 `form.courseId`（参考报告 L175）：
- template 里 `<el-input v-model="form.courseId" .../>` → `<course-picker v-model="form.courseId" />`
- script 顶部 `import CoursePicker from '@/components/RelationPicker/CoursePicker.vue'`
- `components: { ..., CoursePicker }`

如果筛选区也有 `queryParams.courseId`，一并改。

---

## 七、验证

```bash
# 1) 后端编译
cd D:/coding/jst_v1/RuoYi-Vue && mvn -pl ruoyi-admin -am clean compile -DskipTests
# 期望 BUILD SUCCESS

# 2) 前端构建
cd ruoyi-ui && npm run build:prod
# 期望无新 Error

# 3) 真实接口
curl -i "http://localhost:8080/admin/entity/search?type=course&kw=&limit=5" -H "Authorization: Bearer <admin-token>"
# 期望：返回 jst_course 前 5 行的 {id,name,subtitle,statusTag}
```

### 浏览器手测

打开 `/jst/event/jst_course_learn_record`：
- [ ] 新建 dialog → 课程字段是搜索下拉，能搜出实际课程名
- [ ] 选中后 form.courseId 有值
- [ ] 「查看详情 →」跳到 course/edit 列表（PATCH-2 完成后会自动定位）

把这三项验证证据贴报告。

---

## 八、回归扫描（**最后一步，防漏**）

```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui
grep -rEn 'v-model="[^"]*\.courseId[^"]*"' src/views/jst src/views/partner src/views/sales | grep -v 'CoursePicker\|course-picker'
```

期望：**无输出**（所有 courseId 输入框都已用 CoursePicker）。如果还有 case，跟着改完。

---

## 九、DoD

- [ ] DESCRIBE jst_course 字段贴报告
- [ ] EntityBriefController 加 course case + 顶部 Javadoc 更新
- [ ] CoursePicker.vue 创建
- [ ] main.js 注册 + entityRouteMap.js 加映射
- [ ] jst_course_learn_record/index.vue 替换完成
- [ ] 回归扫描无残留
- [ ] mvn + npm build 都绿
- [ ] curl 接口 + 浏览器手测 3 项通过
- [ ] commit：`feat(admin): PATCH-3 CoursePicker 补齐 + jst_course_learn_record courseId 收尾`

---

## 十、红线

- ❌ 不改 BasePicker.vue
- ❌ 不引入新依赖
- ❌ 不改若依系统页面
- ❌ 不脑补字段名，必须 DESCRIBE
- ❌ 不许 SQL 写 `select *`，显式列字段

派发时间：2026-04-18 | 主 Agent：竞赛通架构师
