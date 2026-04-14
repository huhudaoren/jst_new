/**
 * 赛事展示工具
 * 用途：统一处理赛事卡片/详情页的字段归一、状态文案、时间格式与富文本安全兜底
 */

const STATUS_MAP = {
  not_started: {
    text: '未开始',
    tone: 'brand'
  },
  enrolling: {
    text: '报名中',
    tone: 'primary'
  },
  closed: {
    text: '已截止',
    tone: 'muted'
  },
  scoring: {
    text: '评审中',
    tone: 'warning'
  },
  published: {
    text: '已发布',
    tone: 'success'
  },
  ended: {
    text: '已结束',
    tone: 'muted'
  }
}

export function normalizeContestCard(contest = {}) {
  return {
    contestId: contest.contestId || contest.id || '',
    contestName: contest.contestName || contest.name || '',
    coverImage: contest.coverImage || contest.cover || '',
    category: contest.category || contest.categoryName || '',
    price: contest.price,
    enrollStartTime: contest.enrollStartTime || '',
    enrollEndTime: contest.enrollEndTime || '',
    eventStartTime: contest.eventStartTime || '',
    eventEndTime: contest.eventEndTime || '',
    status: contest.status || '',
    enrollOpen: typeof contest.enrollOpen === 'boolean' ? contest.enrollOpen : null,
    partnerName: contest.partnerName || '',
    groupLevels: contest.groupLevels || '',
    description: contest.description || '',
    supportChannelEnroll: contest.supportChannelEnroll,
    supportPointsDeduct: contest.supportPointsDeduct,
    supportAppointment: contest.supportAppointment,
    certRuleJson: contest.certRuleJson || '',
    scoreRuleJson: contest.scoreRuleJson || '',
    aftersaleDays: contest.aftersaleDays,
    scheduleJson: contest.scheduleJson || '',
    awardsJson: contest.awardsJson || '',
    faqJson: contest.faqJson || '',
    recommendTags: contest.recommendTags || '',
    teamMinSize: contest.teamMinSize || 0,
    teamMaxSize: contest.teamMaxSize || 0,
    teamLeaderFields: contest.teamLeaderFields || '',
    teamMemberFields: contest.teamMemberFields || '',
    allowRepeatAppointment: contest.allowRepeatAppointment,
    appointmentSlotList: Array.isArray(contest.appointmentSlotList) ? contest.appointmentSlotList : []
  }
}

export function getContestStatusInfo(status, enrollOpen) {
  if (enrollOpen === true) {
    return STATUS_MAP.enrolling
  }

  return STATUS_MAP[status] || {
    text: '进行中',
    tone: 'brand'
  }
}

export function getContestEnrollAction(contest = {}) {
  const status = contest.status || ''

  if (contest.enrollOpen === true || status === 'enrolling') {
    return {
      text: '立即报名',
      disabled: false
    }
  }

  if (status === 'not_started') {
    return {
      text: '未开始',
      disabled: true
    }
  }

  return {
    text: '已结束',
    disabled: true
  }
}

export function formatContestDate(value) {
  if (!value) {
    return '--'
  }

  const date = new Date(String(value).replace(/ /g, 'T'))
  if (Number.isNaN(date.getTime())) {
    if (typeof value === 'string') {
      return value.slice(0, 10)
    }
    return '--'
  }

  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${year}-${month}-${day}`
}

export function formatContestPeriod(startTime, endTime) {
  if (!startTime && !endTime) {
    return '时间待公布'
  }

  if (!startTime) {
    return `截止 ${formatContestDate(endTime)}`
  }

  if (!endTime) {
    return `开始 ${formatContestDate(startTime)}`
  }

  return `${formatContestDate(startTime)} 至 ${formatContestDate(endTime)}`
}

export function formatContestPrice(price) {
  if (price === null || price === undefined || price === '') {
    return '--'
  }

  const numeric = Number(price)
  if (!Number.isNaN(numeric) && numeric === 0) {
    return '免费'
  }

  if (Number.isNaN(numeric)) {
    return `¥${price}`
  }

  return `¥${numeric % 1 === 0 ? numeric.toFixed(0) : numeric.toFixed(2)}`
}

export function isFreeContest(price) {
  const numeric = Number(price)
  return !Number.isNaN(numeric) && numeric === 0
}

export function sanitizeRichText(html) {
  if (!html) {
    return ''
  }

  return String(html)
    .replace(/<script[\s\S]*?>[\s\S]*?<\/script>/gi, '')
    .replace(/<style[\s\S]*?>[\s\S]*?<\/style>/gi, '')
    .replace(/<iframe[\s\S]*?>[\s\S]*?<\/iframe>/gi, '')
    .replace(/\son\w+="[^"]*"/gi, '')
    .replace(/\son\w+='[^']*'/gi, '')
    .replace(/javascript:/gi, '')
    .replace(/<img/gi, '<img style="max-width:100%;height:auto;display:block;"')
}

export function getContestCategoryIcon(category) {
  const iconMap = {
    艺术: '🏆',
    音乐: '🎵',
    舞蹈: '💃',
    美术: '🎨',
    朗诵: '🗣️',
    戏剧: '🎭',
    文化: '📚',
    科技: '🚀',
    体育: '🏅'
  }

  return iconMap[category] || '🏆'
}
