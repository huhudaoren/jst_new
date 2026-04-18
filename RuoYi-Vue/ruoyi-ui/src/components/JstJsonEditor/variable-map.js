const SCENE_VARIABLE_META = {
  auth_result: {
    label: '认证结果',
    variables: [
      { key: 'userName', label: '用户姓名' },
      { key: 'authType', label: '认证类型' },
      { key: 'authStatus', label: '认证状态' },
      { key: 'auditRemark', label: '审核备注' }
    ],
    demoValues: {
      userName: '张同学',
      authType: '学生认证',
      authStatus: '已通过',
      auditRemark: '资料齐全'
    }
  },
  withdraw_result: {
    label: '提现结果',
    variables: [
      { key: 'userName', label: '用户姓名' },
      { key: 'withdrawAmount', label: '提现金额' },
      { key: 'withdrawStatus', label: '提现状态' },
      { key: 'auditRemark', label: '审核备注' }
    ],
    demoValues: {
      userName: '李老师',
      withdrawAmount: '258.00',
      withdrawStatus: '已到账',
      auditRemark: '已打款'
    }
  },
  settle_result: {
    label: '结算结果',
    variables: [
      { key: 'partnerName', label: '赛事方名称' },
      { key: 'settlementNo', label: '结算单号' },
      { key: 'settlementAmount', label: '结算金额' },
      { key: 'settlementStatus', label: '结算状态' }
    ],
    demoValues: {
      partnerName: '星海美育中心',
      settlementNo: 'SET20260417001',
      settlementAmount: '12800.00',
      settlementStatus: '已结算'
    }
  },
  points_change: {
    label: '积分变动',
    variables: [
      { key: 'userName', label: '用户姓名' },
      { key: 'pointsChange', label: '变动积分' },
      { key: 'pointsBalance', label: '积分余额' },
      { key: 'changeReason', label: '变动原因' }
    ],
    demoValues: {
      userName: '王家长',
      pointsChange: '+20',
      pointsBalance: '560',
      changeReason: '报名奖励'
    }
  },
  ship: {
    label: '发货通知',
    variables: [
      { key: 'userName', label: '用户姓名' },
      { key: 'orderNo', label: '订单号' },
      { key: 'logisticsCompany', label: '物流公司' },
      { key: 'logisticsNo', label: '物流单号' }
    ],
    demoValues: {
      userName: '陈同学',
      orderNo: 'OD20260417008',
      logisticsCompany: '顺丰',
      logisticsNo: 'SF1234567890'
    }
  },
  aftersale: {
    label: '售后通知',
    variables: [
      { key: 'userName', label: '用户姓名' },
      { key: 'orderNo', label: '订单号' },
      { key: 'aftersaleStatus', label: '售后状态' },
      { key: 'refundAmount', label: '退款金额' }
    ],
    demoValues: {
      userName: '刘老师',
      orderNo: 'OD20260417021',
      aftersaleStatus: '处理中',
      refundAmount: '99.00'
    }
  }
}

const VAR_REGEX = /\$\{([a-zA-Z0-9_]+)\}/g

export function parseTemplateVars(content) {
  if (!content) return []
  const parts = []
  VAR_REGEX.lastIndex = 0
  let lastIndex = 0
  let match = null
  while ((match = VAR_REGEX.exec(content)) !== null) {
    if (match.index > lastIndex) {
      parts.push({ text: content.slice(lastIndex, match.index), isVar: false, key: '' })
    }
    parts.push({ text: match[0], isVar: true, key: match[1] })
    lastIndex = VAR_REGEX.lastIndex
  }
  if (lastIndex < content.length) {
    parts.push({ text: content.slice(lastIndex), isVar: false, key: '' })
  }
  return parts
}

export function insertVariableAtCursor(content, variable, selectionStart, selectionEnd) {
  const source = content || ''
  const token = variable || ''
  const start = Number.isInteger(selectionStart) ? selectionStart : source.length
  const end = Number.isInteger(selectionEnd) ? selectionEnd : source.length
  const nextValue = source.slice(0, start) + token + source.slice(end)
  const nextPosition = start + token.length
  return {
    value: nextValue,
    selectionStart: nextPosition,
    selectionEnd: nextPosition
  }
}

export function getSceneVariableMeta(scene) {
  return SCENE_VARIABLE_META[scene] || {
    label: scene || '未选择场景',
    variables: [],
    demoValues: {}
  }
}

export function renderTemplatePreview(content, demoValues) {
  if (!content) return ''
  const values = demoValues || {}
  VAR_REGEX.lastIndex = 0
  return content.replace(VAR_REGEX, (matched, key) => {
    if (Object.prototype.hasOwnProperty.call(values, key)) {
      return values[key]
    }
    return matched
  })
}

export function getAllSceneVariableMeta() {
  return SCENE_VARIABLE_META
}
