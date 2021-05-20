// 验证手机号码
export function checkMobile(value) {
  const regex = /^(1)[3-9]\d{9}$/
  return regex.test(value)
}
// 验证身份证号
export function checkIDCard(value) {
  const regex = /(^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$)|(^[1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}$)/
  return regex.test(value)
}
// 验证角色或其他code
export function checkCode(value) {
  const regex = new RegExp('^[a-zA-Z][a-zA-Z0-9_]{1,29}$')
  return regex.test(value)
}
// 验证用户名
export function checkLoginname(value) {
  const regex = new RegExp('^[a-zA-Z][a-zA-Z0-9_]{1,29}$')
  return regex.test(value)
}
