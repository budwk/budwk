import pkg from '../../package.json'
import fs from 'fs-extra'

export const run = () => {
    console.log('copy font-awesome.min.css & fonts\r\n')
    fs.copy('src/assets/styles/font-awesome.min.css','dist/assets/styles/font-awesome.min.css')
    fs.copy('src/assets/fonts','dist/assets/fonts')
    console.log(`✨ ${pkg.name} - build successfully!`)
}
run()
