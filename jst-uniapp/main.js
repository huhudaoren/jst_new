
// #ifndef VUE3
import Vue from 'vue'
import App from './App'
import { createPinia, PiniaVuePlugin } from 'pinia'

Vue.config.productionTip = false
Vue.use(PiniaVuePlugin)

App.mpType = 'app'

const pinia = createPinia()

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
