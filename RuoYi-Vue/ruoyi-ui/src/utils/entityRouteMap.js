export const ENTITY_ROUTE_MAP = {
  channel:     { path: '/jst/channel/detail',             perm: 'jst:channel:list' },
  sales:       { path: '/jst/sales',                      perm: 'jst:sales:list' },
  user:        { path: '/system/user-detail',             perm: 'system:user:query' },
  jstUser:     { path: '/jst/user/detail',                perm: 'jst:user:list' },
  partner:     { path: '/jst/event/jst_event_partner',    perm: 'jst:event_partner:list' },
  contest:     { path: '/jst/event/jst_contest',          perm: 'jst:contest:list' },
  participant: { path: '/jst/participant',                perm: 'jst:participant:list' },
  order:       { path: '/jst/order/admin-order',          perm: 'jst:order:list' },
  settlement:  { path: '/jst/sales/settlement/detail',    perm: 'jst:sales:settlement:review' },
  formTemplate:   { path: '/jst/form-template',           perm: 'jst:event:enroll_form_template:list', queryKey: 'templateId' },
  certTemplate:   { path: '/partner/cert-manage',         perm: 'jst:event:cert_template:list', queryKey: 'templateId' },
  couponTemplate: { path: '/jst/coupon/template',         perm: 'jst:marketing:coupon_template:list', queryKey: 'couponTemplateId' },
  rightsTemplate: { path: '/jst/rights/template',         perm: 'jst:marketing:rights_template:list', queryKey: 'rightsTemplateId' },
  course:         { path: '/jst/course',                  perm: 'jst:event:course:list' }
}

export function resolveEntityRoute(entity, id) {
  const cfg = ENTITY_ROUTE_MAP[entity]
  if (!cfg) return null
  if (cfg.queryKey) {
    return {
      path: {
        path: cfg.path,
        query: {
          [cfg.queryKey]: String(id)
        }
      },
      perm: cfg.perm
    }
  }
  return { path: `${cfg.path}/${id}`, perm: cfg.perm }
}
