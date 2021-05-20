<template>
    <div>
        <div v-if="tag === 'div'">
            <slot />
        </div>
        <ul v-if="tag === 'ul'">
            <slot />
        </ul>
        <ol v-if="tag === 'ol'">
            <slot />
        </ol>
        <form v-if="tag === 'form'">
            <slot />
        </form>
    </div>
</template>

<script>
import _ from "lodash"

export default {
    name: "QueueAnimate",
    props: {
        tag: {
            type: String,
            default: "div",
            required: false
        },
        show: {
            type: Boolean,
            default: false,
            required: false
        },
        easing: {
            type: String,
            default: "ease",
            required: false
        },
        duration: {
            type: Number,
            default: 200,
            required: false
        },
        delay: {
            type: Number,
            default: 100,
            required: false
        },
        enterAnimation: {
            type: Object,
            default: () => {
                return {}
            },
            required: true
        },
        leaveAnimation: {
            type: Object,
            default: () => {
                return {}
            },
            required: true
        }
    },
    data() {
        return {
            child: [],
            childrenShow: false
        }
    },
    watch: {
        show(val) {
            this.childrenShow = val
            this.$nextTick(() => {
                setTimeout(() => {
                    this.enter()
                }, 50)
            })
        }
    },
    mounted() {
        this.child = this.getChilds()
        this.childrenShow = this.show

        if (this.childrenShow) {
            this.enter()
        }
    },
    methods: {
        enter() {
            const velocity = require("velocity-animate")
            if (
                _.isEmpty(this.enterAnimation) ||
                _.isEmpty(this.leaveAnimation)
            ) {
                console.error("animation参数是个空对象")
                return false
            }
            this.child.forEach((item, index) => {
                velocity(item.el, "stop")
                if (this.childrenShow) {
                    velocity(item.el, this.enterAnimation, {
                        duration: this.duration,
                        easing: this.easing,
                        delay: index * this.delay,
                        visibility: "visible"
                    })
                } else {
                    velocity(item.el, this.leaveAnimation, {
                        duration: this.duration,
                        easing: this.easing,
                        delay: index * this.delay,
                        visibility: "hidden"
                    })
                }
            })
        },
        getChilds() {
            const ret = []
            const keyNodes = Array.from(this.$el.querySelectorAll("[anim-key]"))

            keyNodes.forEach((child) => {
                if (!child) {
                    return
                }

                const key = child.getAttribute("anim-key")
                if (!key) {
                    return
                }
                ret.push({
                    key: key,
                    el: child
                })
            })

            return _.sortBy(ret, (o) => {
                return o.key
            })
        }
    }
}
</script>
