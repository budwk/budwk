export default async function({ store, redirect, route }) {
  var path = route.path
  if (path.lastIndexOf('/') === (path.length - 1)) {
    path = path.substring(0, path.length - 1)
  }
  const has_permissions = store.getters.userPermissionUrl.includes(path)

  // Todo 应该是跳往403
  if (!has_permissions) {
    return redirect('/403')
  }
}

