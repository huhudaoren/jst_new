export const FIELD_LABEL_MAP = {
  userId: '用户', sysUserId: '系统用户', participantId: '参赛档案',
  channelId: '渠道', channelName: '渠道名', salesId: '销售', salesNo: '销售编号', salesName: '销售姓名',
  managerId: '直属主管', partnerId: '赛事方', contestId: '赛事', orderId: '订单', orderNo: '订单编号',
  refundId: '退款单', enrollId: '报名记录', scoreId: '成绩记录', templateId: '模板',
  formTemplateId: '报名表单模板', certTemplateId: '证书模板',
  couponId: '优惠券', couponTemplateId: '优惠券模板', rightsTemplateId: '权益模板', userRightsId: '用户权益',
  bindingId: '绑定关系', settlementId: '结算单', ledgerId: '台账', conflictId: '冲突单',
  inviteId: '邀请关系', recordId: '记录', taskId: '任务', preId: '预录入',
  businessType: '业务类型', bindingType: '归属类型', bindSource: '归属来源',
  orderStatus: '订单状态', refundStatus: '退款状态', auditStatus: '审核状态',
  mood: '跟进意向', followupType: '跟进类型', status: '状态',
  createBy: '创建人', createTime: '创建时间', updateBy: '更新人', updateTime: '更新时间',
  baseAmount: '基数金额', rawAmount: '原始金额', amount: '金额', totalAmount: '总金额',
  payAmount: '支付金额', netPayAmount: '实付金额', commissionAmount: '提成金额', gmv: 'GMV',
  compressRatio: '压缩系数', appliedRate: '适用费率', commissionRateDefault: '默认费率',
  contactMobile: '联系手机号', followupAt: '跟进时间', accrueAt: '入账时间', accruedAt: '已入账时间',
  effectiveFrom: '生效时间', effectiveTo: '失效时间', expireAt: '过期时间'
}

export function fieldLabel(fieldName) {
  if (!fieldName) return ''
  return FIELD_LABEL_MAP[fieldName] || fieldName
}
