function normalizePath(path) {
  if (!path) return '/'
  let normalized = path.trim()
  if (!normalized.startsWith('/')) {
    normalized = '/' + normalized
  }
  normalized = normalized.replace(/\/+/g, '/')
  if (normalized.length > 1 && normalized.endsWith('/')) {
    normalized = normalized.slice(0, -1)
  }
  return normalized
}

function joinPath(parentPath, currentPath) {
  if (!currentPath) return normalizePath(parentPath || '/')
  if (currentPath.startsWith('/')) return normalizePath(currentPath)
  return normalizePath(`${parentPath || ''}/${currentPath}`)
}

function collectPathFromRoute(route, parentPath, pathSet) {
  if (!route || typeof route !== 'object') return
  const fullPath = joinPath(parentPath, route.path || '')
  if (fullPath !== '*' && fullPath !== '/404') {
    pathSet.add(fullPath)
  }
  if (Array.isArray(route.children) && route.children.length) {
    route.children.forEach(child => collectPathFromRoute(child, fullPath, pathSet))
  }
}

export function collectAvailablePaths(permissionRoutes) {
  const pathSet = new Set()
  if (!Array.isArray(permissionRoutes)) return pathSet
  permissionRoutes.forEach(route => collectPathFromRoute(route, '', pathSet))
  return pathSet
}

export function resolveFirstAvailablePath(candidates, availableSet) {
  if (!Array.isArray(candidates) || !availableSet) return null
  for (let i = 0; i < candidates.length; i += 1) {
    const path = normalizePath(candidates[i])
    if (availableSet.has(path)) {
      return path
    }
  }
  return null
}
