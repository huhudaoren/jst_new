
// #ifndef VUE3
import Vue from 'vue'
import App from './App'
import uView from 'uview-ui'
import { PiniaVuePlugin } from 'pinia'
import { pinia } from '@/store/index'
import { navBarMixin } from '@/utils/navbar'

Vue.config.productionTip = false
Vue.use(PiniaVuePlugin)
Vue.use(uView)
Vue.mixin(navBarMixin)

App.mpType = 'app'

const app = new Vue({
    pinia,
    ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
export function createApp() {
  const app = createSSRApp(App)
  const pinia = createPinia()
  app.use(pinia)
  return {
    app
  }
}
// #endif
