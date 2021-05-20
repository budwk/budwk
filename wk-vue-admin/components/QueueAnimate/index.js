import QueueAnimate from "./src/main"

/* istanbul ignore next */
QueueAnimate.install = function(Vue) {
    Vue.component(QueueAnimate.name, QueueAnimate)
}

export default QueueAnimate
