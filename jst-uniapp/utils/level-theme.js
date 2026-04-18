// ═════════════════════════════════════════════════
// 等级主题色系 — 统一学生/渠道方等级配色 (游戏化分段色)
// 按 level_no (1-8) 映射：新手 → 青铜 → 白银 → 黄金 → 铂金 → 钻石 → 星耀 → 王者
// 用于 my/index.vue 积分卡、积分中心 Hero、庆祝动画等
// ─────────────────────────────────────────────────
// 用法:
//   import { getLevelTheme } from '@/utils/level-theme'
//   const theme = getLevelTheme(profile.levelName, profile.currentLevelId)
//   // theme.gradient / theme.accent / theme.badgeBg / theme.badgeText / theme.barFill / theme.icon / theme.name
// ═════════════════════════════════════════════════

const THEMES = {
  // Lv.1 — 新手 / 普通 （冷灰白，起步）
  1: {
    key: 'novice',
    name: '新手',
    gradient: 'linear-gradient(135deg, #B0BEC5 0%, #607D8B 100%)',
    accent: '#546E7A',
    badgeBg: 'rgba(255,255,255,0.22)',
    badgeText: '#FFFFFF',
    barFill: 'rgba(255,255,255,0.85)',
    barBg: 'rgba(255,255,255,0.22)',
    icon: '🌱'
  },
  // Lv.2 — 青铜 （铜色，温暖）
  2: {
    key: 'bronze',
    name: '青铜',
    gradient: 'linear-gradient(135deg, #D4A574 0%, #8B5A2B 100%)',
    accent: '#8B5A2B',
    badgeBg: 'rgba(255,220,180,0.30)',
    badgeText: '#FFECD4',
    barFill: '#FFECD4',
    barBg: 'rgba(255,255,255,0.22)',
    icon: '🥉'
  },
  // Lv.3 — 白银 （银灰，冷静）
  3: {
    key: 'silver',
    name: '白银',
    gradient: 'linear-gradient(135deg, #BCC6CC 0%, #455A64 100%)',
    accent: '#455A64',
    badgeBg: 'rgba(255,255,255,0.28)',
    badgeText: '#FFFFFF',
    barFill: '#ECEFF1',
    barBg: 'rgba(255,255,255,0.22)',
    icon: '🥈'
  },
  // Lv.4 — 黄金 （金橙，经典）
  4: {
    key: 'gold',
    name: '黄金',
    gradient: 'linear-gradient(135deg, #FFD54F 0%, #FF8F00 100%)',
    accent: '#F57C00',
    badgeBg: 'rgba(255,255,255,0.28)',
    badgeText: '#FFF8E1',
    barFill: '#FFF8E1',
    barBg: 'rgba(0,0,0,0.18)',
    icon: '🥇'
  },
  // Lv.5 — 铂金 （翠绿，稀有）
  5: {
    key: 'platinum',
    name: '铂金',
    gradient: 'linear-gradient(135deg, #80CBC4 0%, #00695C 100%)',
    accent: '#00695C',
    badgeBg: 'rgba(255,255,255,0.28)',
    badgeText: '#E0F2F1',
    barFill: '#B2DFDB',
    barBg: 'rgba(255,255,255,0.22)',
    icon: '🏆'
  },
  // Lv.6 — 钻石 （冰蓝，璀璨）
  6: {
    key: 'diamond',
    name: '钻石',
    gradient: 'linear-gradient(135deg, #4FC3F7 0%, #01579B 100%)',
    accent: '#01579B',
    badgeBg: 'rgba(255,255,255,0.28)',
    badgeText: '#E1F5FE',
    barFill: '#B3E5FC',
    barBg: 'rgba(255,255,255,0.22)',
    icon: '💎'
  },
  // Lv.7 — 星耀 / 传奇 （紫钻，神秘）
  7: {
    key: 'legend',
    name: '星耀',
    gradient: 'linear-gradient(135deg, #BA68C8 0%, #4A148C 100%)',
    accent: '#4A148C',
    badgeBg: 'rgba(255,255,255,0.28)',
    badgeText: '#F3E5F5',
    barFill: '#E1BEE7',
    barBg: 'rgba(255,255,255,0.22)',
    icon: '⭐'
  },
  // Lv.8 — 王者 / 极光 （金-粉-紫三色渐变，巅峰）
  8: {
    key: 'king',
    name: '王者',
    gradient: 'linear-gradient(135deg, #FFD700 0%, #FF1493 50%, #4A148C 100%)',
    accent: '#4A148C',
    badgeBg: 'rgba(255,255,255,0.35)',
    badgeText: '#FFFFFF',
    barFill: '#FFFFFF',
    barBg: 'rgba(0,0,0,0.22)',
    icon: '👑'
  }
}

// 等级名字关键词 → level_no 映射
// 覆盖常见命名：普通学员/银卡学员/金卡学员、青铜渠道/白银渠道/黄金渠道 等
const NAME_RULES = [
  { re: /王者|至尊|极光|巅峰/, no: 8 },
  { re: /星耀|星钻|传奇|紫钻/, no: 7 },
  { re: /钻石|冰钻|蓝钻/, no: 6 },
  { re: /铂金|铂白|翡翠/, no: 5 },
  { re: /黄金|金牌|金卡/, no: 4 },
  { re: /白银|银牌|银卡/, no: 3 },
  { re: /青铜|铜牌|铜卡/, no: 2 },
  { re: /普通|新手|初级|lv\.?0|lv\.?1/i, no: 1 }
]

/**
 * 按等级名/ID 获取主题
 * @param {string} levelName 等级中文名（优先）
 * @param {string|number} currentLevelId 等级 ID 兜底
 * @returns {object} 主题对象 { key, name, gradient, accent, badgeBg, badgeText, barFill, barBg, icon, levelNo }
 */
export function getLevelTheme(levelName, currentLevelId) {
  // 优先按名字关键词匹配
  if (levelName && typeof levelName === 'string') {
    for (const rule of NAME_RULES) {
      if (rule.re.test(levelName)) return { ...THEMES[rule.no], levelNo: rule.no }
    }
  }
  // 兜底：按 levelId 末位数字 (fixtures 9811=L1, 9812=L2, 9813=L3...)
  if (currentLevelId) {
    const s = String(currentLevelId)
    const last = parseInt(s.slice(-1), 10)
    if (last >= 1 && last <= 8 && THEMES[last]) {
      return { ...THEMES[last], levelNo: last }
    }
  }
  // 最终兜底：新手
  return { ...THEMES[1], levelNo: 1 }
}

/**
 * 按 level_no 直接取主题 (当后端返回 levelNo 字段时用)
 */
export function getLevelThemeByNo(levelNo) {
  const n = parseInt(levelNo, 10) || 1
  return THEMES[n] ? { ...THEMES[n], levelNo: n } : { ...THEMES[1], levelNo: 1 }
}

export default { getLevelTheme, getLevelThemeByNo }
