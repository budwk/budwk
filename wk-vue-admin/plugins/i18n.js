import Vue from 'vue'
import VueI18n from 'vue-i18n'
// import locale from 'element-ui/lib/locale'

Vue.use(VueI18n)

export default async({ app, store }) => {
  try {
    const localeCookie = app.$cookies.get('locale')

    store.commit('setLocale', localeCookie || 'zh-CN')

    // eslint-disable-next-line require-atomic-updates
    app.i18n = new VueI18n({
      locale: store.state.locale,
      fallbackLocale: 'zh-CN'
    })

    // locale.i18n((key, value) => app.i18n.t(key, value))
  } catch (e) {
    console.log(e)
  }
}
