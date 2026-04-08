#!/usr/bin/env bash
# =====================================================================
# 文件名：cut-gen.sh
# 用途  ：把 RuoYi 代码生成器输出的 51 张表 × 9 文件批量剪切到 jst-* 模块,
#         同时把 com.ruoyi.system 替换为 com.ruoyi.jst.{module}
# 来源  ：D:/coding/jst_v1/RuoYi-Vue/gen/ruoyi
# 目标  ：D:/coding/jst_v1/RuoYi-Vue/jst-{module}/src/main/...
#         D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/...
# 跳过表：jst_participant / jst_participant_user_map (已有手写样板)
# 用法  ：bash D:/coding/jst_v1/scripts/cut-gen.sh
# =====================================================================
set -euo pipefail

ROOT="D:/coding/jst_v1/RuoYi-Vue"
GEN="$ROOT/gen/ruoyi"
JAVA_SRC="$GEN/main/java/com/ruoyi/system"
MAPPER_SRC="$GEN/main/resources/mapper/system"
VUE_VIEW_SRC="$GEN/vue/views/system"
VUE_API_SRC="$GEN/vue/api/system"

UI="$ROOT/ruoyi-ui/src"

# 表 → 模块 映射 (49 张,跳过 2 张已手写)
declare -A TABLE_MODULE=(
  [jst_user]=user
  [jst_channel]=user
  [jst_student_channel_binding]=user

  [jst_event_partner]=event
  [jst_contest]=event
  [jst_enroll_form_template]=event
  [jst_enroll_record]=event
  [jst_score_record]=event
  [jst_cert_record]=event
  [jst_cert_template]=event
  [jst_course]=event
  [jst_course_learn_record]=event

  [jst_order_main]=order
  [jst_order_item]=order
  [jst_payment_record]=order
  [jst_refund_record]=order
  [jst_team_appointment]=order
  [jst_team_appointment_member]=order
  [jst_appointment_record]=order
  [jst_appointment_writeoff_item]=order

  [jst_rebate_rule]=channel
  [jst_rebate_ledger]=channel
  [jst_rebate_settlement]=channel
  [jst_event_settlement]=channel

  [jst_points_account]=points
  [jst_points_ledger]=points
  [jst_growth_ledger]=points
  [jst_level_config]=points
  [jst_points_rule]=points
  [jst_mall_goods]=points
  [jst_mall_exchange_order]=points

  [jst_event_partner_apply]=organizer
  [jst_channel_auth_apply]=organizer

  [jst_coupon_template]=marketing
  [jst_user_coupon]=marketing
  [jst_coupon_issue_batch]=marketing
  [jst_rights_template]=marketing
  [jst_user_rights]=marketing
  [jst_rights_writeoff_record]=marketing

  [jst_notice]=message
  [jst_message_template]=message
  [jst_message_log]=message

  [jst_audit_log]=risk
  [jst_risk_rule]=risk
  [jst_risk_alert]=risk
  [jst_risk_case]=risk
  [jst_risk_blacklist]=risk

  [jst_contract_record]=finance
  [jst_invoice_record]=finance
  [jst_payment_pay_record]=finance
)

# snake_case → CamelCase
to_camel() {
  echo "$1" | awk -F'_' '{for(i=1;i<=NF;i++)printf "%s%s",toupper(substr($i,1,1)),substr($i,2);print ""}'
}

moved=0
skipped=0
errors=0

for table in "${!TABLE_MODULE[@]}"; do
  module="${TABLE_MODULE[$table]}"
  ClassName=$(to_camel "$table")    # jst_user → JstUser
  pkg="com.ruoyi.jst.$module"

  # 目标目录
  base_java="$ROOT/jst-$module/src/main/java/com/ruoyi/jst/$module"
  base_xml="$ROOT/jst-$module/src/main/resources/mapper/$module"
  base_view="$UI/views/jst/$module/$table"
  base_api="$UI/api/jst/$module"

  mkdir -p "$base_java/controller" "$base_java/service/impl" \
           "$base_java/mapper" "$base_java/domain" \
           "$base_xml" "$base_view" "$base_api"

  # 8 个源文件
  src_ctrl="$JAVA_SRC/controller/${ClassName}Controller.java"
  src_svc="$JAVA_SRC/service/I${ClassName}Service.java"
  src_impl="$JAVA_SRC/service/impl/${ClassName}ServiceImpl.java"
  src_mapper="$JAVA_SRC/mapper/${ClassName}Mapper.java"
  src_domain="$JAVA_SRC/domain/${ClassName}.java"
  src_xml="$MAPPER_SRC/${ClassName}Mapper.xml"
  src_vue_view="$VUE_VIEW_SRC/$table/index.vue"
  src_vue_api="$VUE_API_SRC/${table}.js"

  # 检查必需文件存在
  for f in "$src_ctrl" "$src_svc" "$src_impl" "$src_mapper" "$src_domain" "$src_xml" "$src_vue_view" "$src_vue_api"; do
    if [[ ! -f "$f" ]]; then
      echo "  [WARN] 缺失: $f"
      errors=$((errors+1))
    fi
  done

  # 复制 + sed 替换 (用 cp 而非 mv,保留 gen 原始备份便于回滚)
  cp "$src_ctrl"   "$base_java/controller/${ClassName}Controller.java"
  cp "$src_svc"    "$base_java/service/I${ClassName}Service.java"
  cp "$src_impl"   "$base_java/service/impl/${ClassName}ServiceImpl.java"
  cp "$src_mapper" "$base_java/mapper/${ClassName}Mapper.java"
  cp "$src_domain" "$base_java/domain/${ClassName}.java"
  cp "$src_xml"    "$base_xml/${ClassName}Mapper.xml"
  cp "$src_vue_view" "$base_view/index.vue"
  cp "$src_vue_api"  "$base_api/${table}.js"

  # 包路径替换 (Java 文件)
  for f in \
    "$base_java/controller/${ClassName}Controller.java" \
    "$base_java/service/I${ClassName}Service.java" \
    "$base_java/service/impl/${ClassName}ServiceImpl.java" \
    "$base_java/mapper/${ClassName}Mapper.java" \
    "$base_java/domain/${ClassName}.java"; do
    sed -i "s|com\.ruoyi\.system|$pkg|g" "$f"
  done

  # Mapper.xml namespace 替换
  sed -i "s|com\.ruoyi\.system|$pkg|g" "$base_xml/${ClassName}Mapper.xml"

  # vue api 路径替换 (Controller URL 通常是 /system/jstXxx → /jst/{module}/jstXxx)
  # 但生成器默认用 moduleName/businessName,所以是 /system/jstUser
  sed -i "s|/system/jst|/jst/$module/jst|g" "$base_api/${table}.js"

  # vue view 中的 import 路径替换
  sed -i "s|@/api/system/jst|@/api/jst/$module/jst|g" "$base_view/index.vue"

  moved=$((moved+1))
done

# 处理跳过的 2 张表
for skip in jst_participant jst_participant_user_map; do
  echo "  [SKIP] $skip (已有手写样板)"
  skipped=$((skipped+1))
done

echo
echo "===== 完成 ====="
echo "已剪切表数: $moved"
echo "跳过表数  : $skipped"
echo "缺失文件  : $errors"
echo "总文件数  : $((moved * 8))"
