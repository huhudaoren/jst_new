import Vue from 'vue'

import Cookies from 'js-cookie'

import Element from 'element-ui'
import './assets/styles/element-variables.scss'

import '@/assets/styles/index.scss' // global css
import '@/assets/styles/ruoyi.scss' // ruoyi css
import App from './App'
import store from './store'
import router from './router'
import directive from './directive' // directive
import plugins from './plugins' // plugins
import { download } from '@/utils/request'

import './assets/icons' // icon
import './permission' // permission control
import { getDicts } from "@/api/system/dict/data"
import { getConfigKey } from "@/api/system/config"
import { parseTime, resetForm, addDateRange, selectDictLabel, selectDictLabels, handleTree } from "@/utils/ruoyi"
// 分页组件
import Pagination from "@/components/Pagination"
// 自定义表格工具组件
import RightToolbar from "@/components/RightToolbar"
// 富文本组件
import Editor from "@/components/Editor"
// 文件上传组件
import FileUpload from "@/components/FileUpload"
// 图片上传组件
import ImageUpload from "@/components/ImageUpload"
// 图片预览组件
import ImagePreview from "@/components/ImagePreview"
// 字典标签组件
import DictTag from '@/components/DictTag'
// 字典数据组件
import DictData from '@/components/DictData'
// 业务公共组件
import JstStatusBadge from '@/components/JstStatusBadge'
import JstEmptyState from '@/components/JstEmptyState'
import JsonDisplay from '@/components/JsonDisplay'
// Plan-06 UX 精品化基础组件
import PageHeader from '@/components/PageHeader.vue'
import EmptyStateCTA from '@/components/EmptyStateCTA.vue'
import EntityLink from '@/components/EntityLink.vue'
import StatCardGroup from '@/components/StatCardGroup.vue'
import FieldLabelPlugin from '@/plugins/field-label'
// Plan-06 RelationPicker 族
import ChannelPicker from '@/components/RelationPicker/ChannelPicker.vue'
import UserPicker from '@/components/RelationPicker/UserPicker.vue'
import SalesPicker from '@/components/RelationPicker/SalesPicker.vue'
import ContestPicker from '@/components/RelationPicker/ContestPicker.vue'
import ParticipantPicker from '@/components/RelationPicker/ParticipantPicker.vue'
import PartnerPicker from '@/components/RelationPicker/PartnerPicker.vue'
import OrderPicker from '@/components/RelationPicker/OrderPicker.vue'
import FormTemplatePicker from '@/components/RelationPicker/FormTemplatePicker.vue'
import CertTemplatePicker from '@/components/RelationPicker/CertTemplatePicker.vue'
import CouponTemplatePicker from '@/components/RelationPicker/CouponTemplatePicker.vue'
import RightsTemplatePicker from '@/components/RelationPicker/RightsTemplatePicker.vue'
import CoursePicker from '@/components/RelationPicker/CoursePicker.vue'

// 全局方法挂载
Vue.prototype.getDicts = getDicts
Vue.prototype.getConfigKey = getConfigKey
Vue.prototype.parseTime = parseTime
Vue.prototype.resetForm = resetForm
Vue.prototype.addDateRange = addDateRange
Vue.prototype.selectDictLabel = selectDictLabel
Vue.prototype.selectDictLabels = selectDictLabels
Vue.prototype.download = download
Vue.prototype.handleTree = handleTree

// 全局组件挂载
Vue.component('DictTag', DictTag)
Vue.component('Pagination', Pagination)
Vue.component('RightToolbar', RightToolbar)
Vue.component('Editor', Editor)
Vue.component('FileUpload', FileUpload)
Vue.component('ImageUpload', ImageUpload)
Vue.component('ImagePreview', ImagePreview)
Vue.component('JstStatusBadge', JstStatusBadge)
Vue.component('JstEmptyState', JstEmptyState)
Vue.component('JsonDisplay', JsonDisplay)
Vue.component('PageHeader', PageHeader)
Vue.component('EmptyStateCTA', EmptyStateCTA)
Vue.component('EntityLink', EntityLink)
Vue.component('StatCardGroup', StatCardGroup)
Vue.use(FieldLabelPlugin)
Vue.component('ChannelPicker', ChannelPicker)
Vue.component('UserPicker', UserPicker)
Vue.component('SalesPicker', SalesPicker)
Vue.component('ContestPicker', ContestPicker)
Vue.component('ParticipantPicker', ParticipantPicker)
Vue.component('PartnerPicker', PartnerPicker)
Vue.component('OrderPicker', OrderPicker)
Vue.component('FormTemplatePicker', FormTemplatePicker)
Vue.component('CertTemplatePicker', CertTemplatePicker)
Vue.component('CouponTemplatePicker', CouponTemplatePicker)
Vue.component('RightsTemplatePicker', RightsTemplatePicker)
Vue.component('CoursePicker', CoursePicker)

Vue.use(directive)
Vue.use(plugins)
DictData.install()

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */

Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
