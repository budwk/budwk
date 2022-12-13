import autoImport from 'unplugin-auto-import/vite'

export default function createAutoImport() {
    return autoImport({
        imports: [
            'vue',
            'vue-router',
            'pinia'
        ],
        eslintrc: {
            enabled: false, // 若没此json文件，先开启，生成后在关闭
            filepath: './.eslintrc-auto-import.json',
            globalsPropValue: true, // Default `true`, (true | false | 'readonly' | 'readable' | 'writable' | 'writeable')
        },
        dts: 'src/plugins/auto-import.d.ts'
    })
}
