import ScrollAnimate from "./src/main"

/* istanbul ignore next */
ScrollAnimate.install = function(Vue) {
    Vue.component(ScrollAnimate.name, ScrollAnimate)
}

export default ScrollAnimate
