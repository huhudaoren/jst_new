export const ENTITY_ROUTE_MAP = {
  channel:     { path: '/jst/channel/detail',             perm: 'jst:channel:list' },
  sales:       { path: '/jst/sales',                      perm: 'jst:sales:list' },
  user:        { path: '/system/user-detail',             perm: 'system:user:query' },
  jstUser:     { path: '/jst/user/detail',                perm: 'jst:user:list' },
  partner:     { path: '/jst/event/jst_event_partner',    perm: 'jst:event_partner:list' },
  contest:     { path: '/jst/event/jst_contest',          perm: 'jst:contest:list' },
  participant: { path: '/jst/participant',                perm: 'jst:participant:list' },
  order:       { path: '/jst/order/admin-order',          perm: 'jst:order:list' },
  settlement:  { path: '/jst/sales/settlement/detail',    perm: 'jst:sales:settlement:review' }
}

export function resolveEntityRoute(entity, id) {
  const cfg = ENTITY_ROUTE_MAP[entity]
  if (!cfg) return null
  return { path: `${cfg.path}/${id}`, perm: cfg.perm }
}
