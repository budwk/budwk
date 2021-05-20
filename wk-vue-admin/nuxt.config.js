'use strict'
const StyleLintPlugin = require('stylelint-webpack-plugin')
require('dotenv-flow').config()

module.exports = {
  mode: process.env.PACKMODE,

  /*
     ** Headers of the page
     */
  head: {
    title: process.env.PLATFORM_TITLE,
    meta: [
      { charset: 'utf-8' },
      {
        name: 'viewport',
        content: 'width=device-width, initial-scale=1'
      },
      {
        hid: 'description',
        name: 'description',
        content: '前后端分离框架'
      }
    ],
    link: [
      { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' },
      {
        rel: 'stylesheet',
        href: '/assets/icons/font-awesome/css/font-awesome.min.css'
      }
    ]
  },

  env: {
    FILE_URL: process.env.FILE_URL // process.env.FILE_URL
  },
  /*
     ** Customize the progress-bar color
     */
  loading: {
    color: '#409EFF',
    height: '2px'
  },

  /*
     ** Global CSS
     */
  css: [
    '@/assets/sass/main.scss'
    // // lib css
    // "codemirror/lib/codemirror.css",
    // // merge css
    // "codemirror/addon/merge/merge.css",
    // // theme css
    // "codemirror/theme/base16-dark.css"
  ],

  /*
     ** Plugins to load before mounting the App
     */
  plugins: [
    '@/plugins/element-ui/element-ui',
    '@/plugins/axios', // 顺序不可改动
    '@/plugins/i18n', // 顺序不可改动
    '@/plugins/moment',
    '@/plugins/directives',
    { src: '@/plugins/client-init.js', mode: 'client' },
    // { src: "@/plugins/route.js" }
    { src: '@/plugins/quill-editor.js', ssr: false, mode: 'client' },
    // { src: "@/plugins/codemirror.js", mode: "client" },
    { src: '@/plugins/echarts.js', mode: 'client' }
  ],

  /*
     ** Nuxt.js modules
     */
  modules: [
    // Doc: https://github.com/nuxt-community/axios-module#usage
    '@nuxtjs/axios',
    '@nuxtjs/proxy',
    ['@nuxtjs/dotenv', { filename: `.env.${process.env.NODE_ENV}` }],
    'cookie-universal-nuxt'
  ],
  /*
     ** Axios module configuration
     */
  axios: {
    proxy: true
  },
  proxy: {
    '/api': {
      target: process.env.HTTP_URL,
      changeOrigin: true,
      pathRewrite: {
        '^/api': '/'
      }
    }
  },
  server: {
    host: '0.0.0.0' // default: localhost
  },

  /*
     ** Build configuration
     */
  build: {
    /*
         ** You can extend webpack config here
         */
    extractCSS: true,
    filenames: {
      css: ({ isDev }) => (isDev ? '[name].css' : '[contenthash].css')
    },
    extend(config, ctx) {
      // Run ESLint on save
      if (ctx.isDev && ctx.isClient) {
        config.module.rules.push({
          enforce: 'pre',
          test: /\.(js|vue)$/,
          loader: 'eslint-loader',
          exclude: /(node_modules)/
        })

        config.plugins.push(
          new StyleLintPlugin({
            files: ['pages/**/*.vue', 'assets/sass/*.l?(e|c)ss']
          })
        )
      }
    }
  }
}
