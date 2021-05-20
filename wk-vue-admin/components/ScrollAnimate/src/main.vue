<template>
    <div>
        <slot />
    </div>
</template>

<script>
import _ from "lodash"
// import { mapState } from 'vuex'

export default {
    name: "ScrollAnimate",
    props: {},
    data() {
        return {
            child: []
        }
    },
    mounted() {
        this.child = this.getChilds()

        this.initActivate()
        this.scroll((direction) => {
            this.activate(direction, this.$el)
        })
    },
    methods: {
        initActivate() {
            this.child.forEach((item) => {
                const { top } = item.el.getBoundingClientRect()
                const windowHeight = document.documentElement.clientHeight
                if (top < windowHeight) {
                    // 所有小于视窗区域内的元素，全部加载
                    this.$emit("change", item.key, true)
                }
            })
        },
        activate: _.throttle(function(direction) {
            this.child.forEach((item) => {
                let { top, bottom, height } = item.el.getBoundingClientRect()
                let windowHeight = document.documentElement.clientHeight
                if (height > windowHeight) {
                    height = windowHeight - item.distance
                } else {
                    height = height - item.distance
                }

                const inWindow = top < windowHeight - height && bottom > 0
                const outWindow =
                    top > windowHeight - item.distance && bottom > 0

                if (inWindow && direction === "down") {
                    this.$emit("change", item.key, true)
                }

                if (outWindow && direction === "up") {
                    this.$emit("change", item.key, false)
                }
            })
        }, 50),
        scroll(cb) {
            let beforeScrollTop =
                document.documentElement.scrollTop ||
                window.pageYOffset ||
                document.body.scrollTop
            window.addEventListener(
                "scroll",
                () => {
                    const afterScrollTop =
                        document.documentElement.scrollTop ||
                        window.pageYOffset ||
                        document.body.scrollTop
                    let delta = afterScrollTop - beforeScrollTop
                    if (delta === 0) {
                        return false
                    }

                    cb(delta > 0 ? "down" : "up")
                    beforeScrollTop = afterScrollTop
                },
                false
            )
        },
        getChilds() {
            const ret = []
            const keyNodes = Array.from(
                this.$el.querySelectorAll("[scroll-key]")
            )

            keyNodes.forEach((child) => {
                if (!child) {
                    return
                }

                let key = child.getAttribute("scroll-key")
                if (!key) {
                    return
                }

                let distance = child.getAttribute("scroll-distance")
                if (!distance) {
                    distance = 0
                }
                ret.push({
                    key: key,
                    distance: parseInt(distance),
                    el: child
                })
            })

            return ret
        }
    }
}
</script>
