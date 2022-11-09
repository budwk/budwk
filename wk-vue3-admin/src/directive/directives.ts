import { App, nextTick } from 'vue'
import horizontalScroll from '/@/utils/horizontalScroll'
import { useUserInfo } from '/@/stores/userInfo'

export function directives(app: App) {
    // 权限验证
    permissionDirective(app)
    // 角色验证
    roleDirective(app)
    // 拖动指令
    dragDirective(app)
    // 缩放指令
    zoomDirective(app)
    // 点击后自动失焦指令
    blurDirective(app)
    // 表格横向拖动指令
    tableLateralDragDirective(app)
}

/**
 * 权限判断
 * @description v-permission="['sys.list','sys.create']
 */
function permissionDirective(app: App) {
    app.directive('permission', {
        mounted(el, binding) {
            const { value } = binding
            if (value && value instanceof Array && value.length>0) {
                const permissions = useUserInfo().permissions
                if(permissions && permissions instanceof Array){
                    //只需包含其中一个
                    const hasPermission = permissions.some(permission => {
                        return value.includes(permission)
                    })
                    if (!hasPermission) {
                        el.parentNode && el.parentNode.removeChild(el)
                    }
                }

            }else {
                throw new Error(`need permission! Like v-permission="['sys.list','sys.create']"`)
            }
        }
    })
}

/**
 * 角色判断
 * @description v-role="['sysadmin','admin']"
 */
function roleDirective(app: App) {
    app.directive('role', {
        mounted(el, binding) {
            const { value } = binding
            if (value && value instanceof Array && value.length>0) {
                const roles = useUserInfo().roles
                if(roles && roles instanceof Array){
                    //只需包含其中一个
                    const hasRole = roles.some(role => {
                        return value.includes(role)
                    })
                    if (!hasRole) {
                        el.parentNode && el.parentNode.removeChild(el)
                    }
                }

            }else {
                throw new Error(`need role! Like v-role="['sysadmin','admin']"`)
            }
        }
    })
}

/**
 * 表格横向滚动指令
 * @description v-table-lateral-drag
 */
function tableLateralDragDirective(app: App) {
    app.directive('tableLateralDrag', {
        created(el) {
            new horizontalScroll(el.querySelector('.el-table__body-wrapper .el-scrollbar .el-scrollbar__wrap'))
        },
    })
}

/**
 * 点击后自动失焦指令
 * @description v-blur
 */
function blurDirective(app: App) {
    app.directive('blur', {
        mounted(el) {
            el.addEventListener('focus', () => {
                el.blur()
            })
        },
    })
}

/**
 * 缩放指令
 * @description v-zoom="[domEl]"
 * @description domEl=要开启缩放的元素
 */
function zoomDirective(app: App) {
    app.directive('zoom', {
        mounted(el, binding) {
            if (!binding.value) return false

            nextTick(() => {
                const zoomDom = document.querySelector(binding.value) as HTMLElement
                const zoomhandleEl = document.createElement('div')
                zoomhandleEl.className = 'zoom-handle'
                zoomhandleEl.onmouseenter = () => {
                    zoomhandleEl.onmousedown = (e: MouseEvent) => {
                        const x = e.clientX
                        const y = e.clientY
                        const zoomDomWidth = zoomDom.offsetWidth
                        const zoomDomHeight = zoomDom.offsetHeight
                        document.onmousemove = (e: MouseEvent) => {
                            e.preventDefault() // 移动时禁用默认事件
                            const w = zoomDomWidth + (e.clientX - x) * 2
                            const h = zoomDomHeight + (e.clientY - y)

                            zoomDom.style.width = `${w}px`
                            zoomDom.style.height = `${h}px`
                        }

                        document.onmouseup = function () {
                            document.onmousemove = null
                            document.onmouseup = null
                        }
                    }
                }

                zoomDom.appendChild(zoomhandleEl)
            })
        },
    })
}

/**
 * 拖动指令
 * @description v-drag="[domEl,handleEl]"
 * @description domEl=被拖动的元素，handleEl=在此元素内可以拖动`dom`
 */
interface downReturn {
    [key: string]: number
}
function dragDirective(app: App) {
    app.directive('drag', {
        mounted(el, binding) {
            if (!binding.value) return false

            const dragDom = document.querySelector(binding.value[0]) as HTMLElement
            const dragHandle = document.querySelector(binding.value[1]) as HTMLElement

            if (!dragHandle || !dragDom) {
                return false
            }

            dragHandle.onmouseover = () => (dragHandle.style.cursor = `move`)

            function down(e: MouseEvent | TouchEvent, type: string): downReturn {
                // 鼠标按下，计算当前元素距离可视区的距离
                const disX =
                    type === 'pc' ? (e as MouseEvent).clientX - dragHandle.offsetLeft : (e as TouchEvent).touches[0].clientX - dragHandle.offsetLeft
                const disY =
                    type === 'pc' ? (e as MouseEvent).clientY - dragHandle.offsetTop : (e as TouchEvent).touches[0].clientY - dragHandle.offsetTop

                // body宽度
                const screenWidth = document.body.clientWidth
                const screenHeight = document.body.clientHeight || document.documentElement.clientHeight

                // 被拖动元素宽度
                const dragDomWidth = dragDom.offsetWidth
                // 被拖动元素高度
                const dragDomheight = dragDom.offsetHeight

                // 拖动限位
                const minDragDomLeft = dragDom.offsetLeft
                const maxDragDomLeft = screenWidth - dragDom.offsetLeft - dragDomWidth
                const minDragDomTop = dragDom.offsetTop
                const maxDragDomTop = screenHeight - dragDom.offsetTop - dragDomheight

                // 获取到的值带px 正则匹配替换
                let styL: string | number = getComputedStyle(dragDom).left
                let styT: string | number = getComputedStyle(dragDom).top
                styL = +styL.replace(/\px/g, '')
                styT = +styT.replace(/\px/g, '')

                return {
                    disX,
                    disY,
                    minDragDomLeft,
                    maxDragDomLeft,
                    minDragDomTop,
                    maxDragDomTop,
                    styL,
                    styT,
                }
            }

            function move(e: MouseEvent | TouchEvent, type: string, obj: downReturn) {
                const { disX, disY, minDragDomLeft, maxDragDomLeft, minDragDomTop, maxDragDomTop, styL, styT } = obj

                // 通过事件委托，计算移动的距离
                let left = type === 'pc' ? (e as MouseEvent).clientX - disX : (e as TouchEvent).touches[0].clientX - disX
                let top = type === 'pc' ? (e as MouseEvent).clientY - disY : (e as TouchEvent).touches[0].clientY - disY

                // 边界处理
                if (-left > minDragDomLeft) {
                    left = -minDragDomLeft
                } else if (left > maxDragDomLeft) {
                    left = maxDragDomLeft
                }

                if (-top > minDragDomTop) {
                    top = -minDragDomTop
                } else if (top > maxDragDomTop) {
                    top = maxDragDomTop
                }

                // 移动当前元素
                dragDom.style.cssText += `;left:${left + styL}px;top:${top + styT}px;`
            }

            dragHandle.onmousedown = (e) => {
                const obj = down(e, 'pc')
                document.onmousemove = (e) => {
                    move(e, 'pc', obj)
                }
                document.onmouseup = () => {
                    document.onmousemove = null
                    document.onmouseup = null
                }
            }
            dragHandle.ontouchstart = (e) => {
                const obj = down(e, 'app')
                document.ontouchmove = (e) => {
                    move(e, 'app', obj)
                }
                document.ontouchend = () => {
                    document.ontouchmove = null
                    document.ontouchend = null
                }
            }
        },
    })
}
