#!/usr/bin/env bash
# =====================================================================
# 文件名：fix-gen.sh
# 用途  ：清理代码生成器的 5 类技术债（机械可修复部分）
#         1. 权限标识 system: → jst:{module}:
#         2. RequestMapping URL /system/ → /jst/{module}/
#         3. menu.sql 里 perms / url / parent_id 同步修复
# 跳过  ：jst_participant / jst_participant_user_map (已手写样板)
# 用法  ：bash D:/coding/jst_v1/scripts/fix-gen.sh
# =====================================================================
set -euo pipefail

ROOT="D:/coding/jst_v1/RuoYi-Vue"
GEN_DIR="$ROOT/gen/ruoyi"

# 表 → 模块 + 父菜单ID 映射 (与 23-基础数据初始化.sql 一致)
declare -A TABLE_MODULE=(
  [jst_user]=user             [jst_channel]=user                [jst_student_channel_binding]=user
  [jst_event_partner]=event   [jst_contest]=event               [jst_enroll_form_template]=event
  [jst_enroll_record]=event   [jst_score_record]=event          [jst_cert_record]=event
  [jst_cert_template]=event   [jst_course]=event                [jst_course_learn_record]=event
  [jst_order_main]=order      [jst_order_item]=order            [jst_payment_record]=order
  [jst_refund_record]=order   [jst_team_appointment]=order      [jst_team_appointment_member]=order
  [jst_appointment_record]=order [jst_appointment_writeoff_item]=order
  [jst_rebate_rule]=channel   [jst_rebate_ledger]=channel       [jst_rebate_settlement]=channel
  [jst_event_settlement]=channel
  [jst_points_account]=points [jst_points_ledger]=points        [jst_growth_ledger]=points
  [jst_level_config]=points   [jst_points_rule]=points          [jst_mall_goods]=points
  [jst_mall_exchange_order]=points
  [jst_event_partner_apply]=organizer  [jst_channel_auth_apply]=organizer
  [jst_coupon_template]=marketing [jst_user_coupon]=marketing   [jst_coupon_issue_batch]=marketing
  [jst_rights_template]=marketing [jst_user_rights]=marketing   [jst_rights_writeoff_record]=marketing
  [jst_notice]=message        [jst_message_template]=message    [jst_message_log]=message
  [jst_audit_log]=risk        [jst_risk_rule]=risk              [jst_risk_alert]=risk
  [jst_risk_case]=risk        [jst_risk_blacklist]=risk
  [jst_contract_record]=finance [jst_invoice_record]=finance    [jst_payment_pay_record]=finance
)

# 模块 → 二级菜单父ID (与 23 文档一致)
declare -A MODULE_PARENT=(
  [user]=2100
  [event]=2200
  [order]=2300
  [channel]=2400
  [points]=2500
  [organizer]=2200  # organizer 挂在 event 目录下
  [marketing]=2600
  [message]=2700
  [risk]=2800
  [finance]=2900
)

fixed_java=0
fixed_xml=0
fixed_sql=0

for table in "${!TABLE_MODULE[@]}"; do
  module="${TABLE_MODULE[$table]}"
  parent_id="${MODULE_PARENT[$module]}"
  resource="${table#jst_}"   # 去掉 jst_ 前缀作为资源名(snake_case)

  base_java="$ROOT/jst-$module/src/main/java/com/ruoyi/jst/$module"
  ctrl_dir="$base_java/controller"

  # ====== 1. Controller: 权限标识 + RequestMapping URL ======
  for f in "$ctrl_dir"/*.java; do
    [[ -f "$f" ]] || continue
    if grep -q "system:$table" "$f" 2>/dev/null; then
      # 权限标识: 'system:jst_user:list' → 'jst:user:user:list'
      sed -i "s|'system:$table:|'jst:$module:$resource:|g" "$f"
      # URL: "/system/jst_user" → "/jst/user/jst_user"
      sed -i "s|\"/system/$table\"|\"/jst/$module/$table\"|g" "$f"
      fixed_java=$((fixed_java+1))
    fi
  done

  # ====== 2. menu.sql: 权限 + URL + parent_id ======
  menu_sql="$GEN_DIR/${table}Menu.sql"
  if [[ -f "$menu_sql" ]]; then
    # parent_id=3 (默认系统工具) → 我们的二级目录
    # 匹配 ", '3', '1', '$table'," (主菜单行的第 2 列 parent_id)
    sed -i "s|, '3', '1', '$table',|, '$parent_id', '1', '$table',|g" "$menu_sql"
    # 权限标识批量替换
    sed -i "s|'system:$table:|'jst:$module:$resource:|g" "$menu_sql"
    # URL 路径替换 (component 路径)
    sed -i "s|'system/$table|'jst/$module/$table|g" "$menu_sql"
    sed -i "s|/system/$table|/jst/$module/$table|g" "$menu_sql"
    fixed_sql=$((fixed_sql+1))
  fi
done

# ====== 3. 合并所有 menu.sql 到一个文件,方便统一执行 ======
ALL_MENU="$GEN_DIR/_all-menu-jst.sql"
echo "-- 合并自 50 个 *Menu.sql,执行前确保 23-基础数据初始化.sql 已执行" > "$ALL_MENU"
echo "-- 父菜单 2000-2900 必须已存在(由 23 文档创建)" >> "$ALL_MENU"
echo "" >> "$ALL_MENU"
for table in "${!TABLE_MODULE[@]}"; do
  menu_sql="$GEN_DIR/${table}Menu.sql"
  if [[ -f "$menu_sql" ]]; then
    echo "-- ===== $table =====" >> "$ALL_MENU"
    cat "$menu_sql" >> "$ALL_MENU"
    echo "" >> "$ALL_MENU"
  fi
done

echo
echo "===== 修复完成 ====="
echo "Controller 修复: $fixed_java 个"
echo "menu.sql 修复  : $fixed_sql 个"
echo "合并 SQL 文件  : $ALL_MENU"
