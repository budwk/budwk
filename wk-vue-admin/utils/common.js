export function generateMenus(data, pid) {
  const tree = {}
  data.forEach((item) => {
    if (item.parentId === pid && item.type === 'menu' && item.showit) {
      const menuItem = {
        id: item.id,
        name: item.name,
        identify: item.permission,
        alias: item.alias,
        type: item.type,
        showit: item.showit,
        hasChildren: item.hasChildren,
        icon: item.icon,
        sortId: item.sortId,
        parentId: item.parentId,
        url: item.href,
        path: item.path,
        createdAt: item.createdAt,
        updatedAt: item.updatedAt,
        meta: { title: item.name, icon: item.icon },
        children: generateMenus(data, item.id)
      }
      tree[item.path] = menuItem
    }
  })

  return tree
}
