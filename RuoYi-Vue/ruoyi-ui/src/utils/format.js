export function formatMoney(val, fromCents = false) {
  const raw = Number(val || 0)
  const safe = Number.isFinite(raw) ? raw : 0
  const amount = fromCents ? safe / 100 : safe
  return '\u00A5' + amount.toFixed(2)
}