const path = require('path')

module.exports = {
  transpileDependencies: ['uview-ui'],
  configureWebpack: {
    resolve: {
      alias: {
        '@vue/composition-api': path.resolve(__dirname, 'node_modules/@vue/composition-api')
      }
    }
  }
}
