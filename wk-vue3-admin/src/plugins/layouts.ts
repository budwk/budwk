import Layouts from 'vite-plugin-vue-layouts'

export default function createLayouts() {
    return Layouts({
        layoutsDirs: 'src/layouts',
        defaultLayout: 'default',
        exclude: ['**/components/*.vue']
    })
}
