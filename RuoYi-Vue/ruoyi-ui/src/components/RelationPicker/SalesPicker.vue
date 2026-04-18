<template>
  <base-picker :value="value" entity="sales" :placeholder="placeholder" :clearable="clearable" :disabled="disabled" :show-detail-link="showDetailLink" @input="$emit('input', $event)" @change="$emit('change', $event)" />
</template>
<script>
import BasePicker from './BasePicker.vue'
export default {
  name: 'SalesPicker',
  components: { BasePicker },
  props: {
    value: { type: [Number, String], default: null },
    placeholder: { type: String, default: '请选择销售（输入姓名/编号/手机号搜索）' },
    clearable: { type: Boolean, default: true },
    disabled: { type: Boolean, default: false },
    showDetailLink: { type: Boolean, default: true }
  }
}
</script>
