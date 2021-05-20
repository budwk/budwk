import Vue from 'vue'
export default ({ store }) => {
  Vue.directive('permission', {
    inserted(el, binding, vnode) {
      const { value } = binding
      const permissions = store.getters && store.getters.userPermissions
      if (value) {
        const currentPermissions = value
        const hasPermission = permissions.some((permission) => {
          if (value instanceof Array && value.length > 0) {
            return currentPermissions.includes(permission)
          } else if (typeof value === 'string') {
            return currentPermissions === permission
          }
        })
        // 演示环境时,不判断权限
        if (process.env.DEMO_ENV === 'true') {
          return
        }
        if (!hasPermission) {
          el.parentNode && el.parentNode.removeChild(el)
        }
      } else {
        throw new Error(
          `need permissions! Like v-permission="'sys.permission'" or v-permission="['sys.permission','sys.roles']"`
        )
      }
    }
  })
}
