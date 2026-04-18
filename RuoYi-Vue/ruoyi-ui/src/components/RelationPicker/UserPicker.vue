<template>
  <base-picker :value="value" entity="user" :placeholder="placeholder" :clearable="clearable" :disabled="disabled" :show-detail-link="showDetailLink" @input="$emit('input', $event)" @change="$emit('change', $event)" />
</template>
<script>
import BasePicker from './BasePicker.vue'
export default {
  name: 'UserPicker',
  components: { BasePicker },
  props: {
    value: { type: [Number, String], default: null },
    placeholder: { type: String, default: '请选择系统用户（输入用户名/昵称搜索）' },
    clearable: { type: Boolean, default: true },
    disabled: { type: Boolean, default: false },
    showDetailLink: { type: Boolean, default: true }
  }
}
</script>
