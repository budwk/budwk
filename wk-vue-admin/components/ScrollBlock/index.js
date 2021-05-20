import ScrollBlock from "./src/main"

/* istanbul ignore next */
ScrollBlock.install = function(Vue) {
    Vue.component(ScrollBlock.name, ScrollBlock)
}

export default ScrollBlock
