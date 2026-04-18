import { fieldLabel } from '@/utils/fieldLabelMap'
export default {
  install(Vue) { Vue.prototype.$fieldLabel = fieldLabel }
}
