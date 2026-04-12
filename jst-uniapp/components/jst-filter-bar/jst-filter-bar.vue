<!-- 中文注释: 通用筛选栏组件；对照原型 小程序原型图/contest-list.html 筛选区域 -->
<template>
  <view class="jst-filter-bar">
    <!-- 顶部筛选按钮行 -->
    <view class="jst-filter-bar__row">
      <view
        v-for="(filter, idx) in filters"
        :key="filter.key"
        class="jst-filter-bar__btn"
        :class="{
          'jst-filter-bar__btn--active': isFilterActive(filter) || activeDropdown === idx
        }"
        @tap="toggleDropdown(idx)"
      >
        <text class="jst-filter-bar__btn-text">{{ getFilterLabel(filter) }}</text>
        <text class="jst-filter-bar__btn-arrow">{{ activeDropdown === idx ? '∧' : '∨' }}</text>
      </view>
    </view>

    <!-- 下拉面板 + 遮罩 -->
    <view
      v-if="activeDropdown !== -1"
      class="jst-filter-bar__overlay"
      @tap="closeDropdown"
    >
      <view class="jst-filter-bar__panel" @tap.stop>
        <!-- type: grid — 网格单选 -->
        <template v-if="activeFilter && activeFilter.type === 'grid'">
          <view class="jst-filter-bar__grid">
            <view
              class="jst-filter-bar__grid-item"
              :class="{ 'jst-filter-bar__grid-item--active': !tempValue[activeFilter.key] }"
              @tap="selectSingle(activeFilter.key, '')"
            >
              <text class="jst-filter-bar__grid-text">全部</text>
            </view>
            <view
              v-for="opt in activeFilter.options"
              :key="opt.value"
              class="jst-filter-bar__grid-item"
              :class="{ 'jst-filter-bar__grid-item--active': tempValue[activeFilter.key] === opt.value }"
              @tap="selectSingle(activeFilter.key, opt.value)"
            >
              <text class="jst-filter-bar__grid-text">{{ opt.label }}</text>
            </view>
          </view>
        </template>

        <!-- type: radio — 单选列表 -->
        <template v-if="activeFilter && activeFilter.type === 'radio'">
          <view class="jst-filter-bar__radio-list">
            <view
              class="jst-filter-bar__radio-item"
              :class="{ 'jst-filter-bar__radio-item--active': !tempValue[activeFilter.key] || tempValue[activeFilter.key] === 'default' }"
              @tap="selectSingle(activeFilter.key, 'default')"
            >
              <text class="jst-filter-bar__radio-text">综合排序</text>
              <text v-if="!tempValue[activeFilter.key] || tempValue[activeFilter.key] === 'default'" class="jst-filter-bar__radio-check">✓</text>
            </view>
            <view
              v-for="opt in activeFilter.options"
              :key="opt.value"
              class="jst-filter-bar__radio-item"
              :class="{ 'jst-filter-bar__radio-item--active': tempValue[activeFilter.key] === opt.value }"
              @tap="selectSingle(activeFilter.key, opt.value)"
            >
              <text class="jst-filter-bar__radio-text">{{ opt.label }}</text>
              <text v-if="tempValue[activeFilter.key] === opt.value" class="jst-filter-bar__radio-check">✓</text>
            </view>
          </view>
        </template>

        <!-- type: multi — 分组多选 + 重置/确认 -->
        <template v-if="activeFilter && activeFilter.type === 'multi'">
          <view class="jst-filter-bar__multi-body">
            <view
              v-for="group in (activeFilter.groups || [])"
              :key="group.key"
              class="jst-filter-bar__multi-group"
            >
              <text class="jst-filter-bar__multi-title">{{ group.title }}</text>
              <view class="jst-filter-bar__multi-tags">
                <view
                  v-for="opt in group.options"
                  :key="opt.value"
                  class="jst-filter-bar__multi-tag"
                  :class="{ 'jst-filter-bar__multi-tag--active': tempValue[group.key] === opt.value }"
                  @tap="selectSingle(group.key, tempValue[group.key] === opt.value ? '' : opt.value)"
                >
                  <text class="jst-filter-bar__multi-tag-text">{{ opt.label }}</text>
                </view>
              </view>
            </view>
          </view>
          <view class="jst-filter-bar__multi-footer">
            <view class="jst-filter-bar__multi-reset" @tap="resetMulti">
              <text class="jst-filter-bar__multi-reset-text">重置</text>
            </view>
            <view class="jst-filter-bar__multi-confirm" @tap="confirmMulti">
              <text class="jst-filter-bar__multi-confirm-text">确认</text>
            </view>
          </view>
        </template>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'JstFilterBar',
  props: {
    filters: {
      type: Array,
      default: () => []
    },
    value: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      activeDropdown: -1,
      tempValue: {}
    }
  },
  computed: {
    activeFilter() {
      if (this.activeDropdown < 0 || this.activeDropdown >= this.filters.length) return null
      return this.filters[this.activeDropdown]
    }
  },
  watch: {
    value: {
      handler(val) {
        this.tempValue = { ...val }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    // 中文注释: 判断筛选项是否有非默认值
    isFilterActive(filter) {
      if (filter.type === 'multi') {
        return (filter.groups || []).some(g => !!this.value[g.key])
      }
      const val = this.value[filter.key]
      return !!val && val !== 'default'
    },

    // 中文注释: 获取按钮文案——选中时显示选中值标签，否则显示默认 label
    getFilterLabel(filter) {
      if (filter.type === 'multi') {
        const count = (filter.groups || []).filter(g => !!this.value[g.key]).length
        return count > 0 ? `${filter.label}(${count})` : filter.label
      }
      const val = this.value[filter.key]
      if (!val || val === 'default') return filter.label
      const opt = (filter.options || []).find(o => o.value === val)
      return opt ? opt.label : filter.label
    },

    toggleDropdown(idx) {
      if (this.activeDropdown === idx) {
        this.closeDropdown()
      } else {
        this.tempValue = { ...this.value }
        this.activeDropdown = idx
      }
    },

    closeDropdown() {
      this.activeDropdown = -1
    },

    // 中文注释: grid/radio 单选——选中即关闭面板并 emit
    selectSingle(key, val) {
      const newValue = { ...this.value, [key]: val }
      this.$emit('input', newValue)
      this.$emit('change', newValue)
      this.closeDropdown()
    },

    // 中文注释: multi 重置——清空当前面板所有 group 的值
    resetMulti() {
      if (!this.activeFilter || !this.activeFilter.groups) return
      const updated = { ...this.tempValue }
      this.activeFilter.groups.forEach(g => {
        updated[g.key] = ''
      })
      this.tempValue = updated
    },

    // 中文注释: multi 确认——应用并关闭
    confirmMulti() {
      this.$emit('input', { ...this.tempValue })
      this.$emit('change', { ...this.tempValue })
      this.closeDropdown()
    }
  }
}
</script>

<style scoped lang="scss">
@import '@/styles/design-tokens.scss';

.jst-filter-bar {
  position: relative;
}

/* 按钮行 */
.jst-filter-bar__row {
  display: flex;
  background: $jst-bg-card;
  border-bottom: 2rpx solid $jst-border;
}

.jst-filter-bar__btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $jst-space-xs;
  padding: $jst-space-lg $jst-space-md;
  transition: color $jst-duration-fast;
}

.jst-filter-bar__btn-text {
  font-size: $jst-font-sm;
  font-weight: $jst-weight-medium;
  color: $jst-text-regular;
}

.jst-filter-bar__btn-arrow {
  font-size: $jst-font-xs;
  color: $jst-text-secondary;
}

.jst-filter-bar__btn--active {
  .jst-filter-bar__btn-text {
    color: $jst-brand;
    font-weight: $jst-weight-semibold;
  }
  .jst-filter-bar__btn-arrow {
    color: $jst-brand;
  }
}

/* 遮罩 */
.jst-filter-bar__overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(26, 31, 46, 0.45);
  z-index: 200;
}

/* 下拉面板 */
.jst-filter-bar__panel {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  background: $jst-bg-card;
  border-radius: 0 0 $jst-radius-lg $jst-radius-lg;
  box-shadow: $jst-shadow-lg;
  max-height: 70vh;
  overflow-y: auto;
  animation: jst-filter-slide 200ms $jst-easing;
}

@keyframes jst-filter-slide {
  from { opacity: 0; transform: translateY(-16rpx); }
  to { opacity: 1; transform: translateY(0); }
}

/* grid 网格 */
.jst-filter-bar__grid {
  display: flex;
  flex-wrap: wrap;
  padding: $jst-space-lg;
  gap: $jst-space-md;
}

.jst-filter-bar__grid-item {
  width: calc(25% - #{$jst-space-md} * 3 / 4);
  height: 68rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: $jst-radius-round;
  border: 2rpx solid $jst-border;
  background: $jst-bg-card;
  transition: all $jst-duration-fast;

  &:active { transform: scale(0.95); }

  &--active {
    background: $jst-brand-light;
    border-color: $jst-brand;
    .jst-filter-bar__grid-text {
      color: $jst-brand;
      font-weight: $jst-weight-semibold;
    }
  }
}

.jst-filter-bar__grid-text {
  font-size: $jst-font-sm;
  color: $jst-text-regular;
}

/* radio 单选列表 */
.jst-filter-bar__radio-list {
  padding: $jst-space-sm 0;
}

.jst-filter-bar__radio-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $jst-space-lg $jst-space-xl;
  transition: background $jst-duration-fast;

  &:active { background: $jst-bg-grey; }

  &--active {
    .jst-filter-bar__radio-text {
      color: $jst-brand;
      font-weight: $jst-weight-semibold;
    }
  }
}

.jst-filter-bar__radio-text {
  font-size: $jst-font-base;
  color: $jst-text-primary;
}

.jst-filter-bar__radio-check {
  font-size: $jst-font-md;
  color: $jst-brand;
  font-weight: $jst-weight-bold;
}

/* multi 分组多选 */
.jst-filter-bar__multi-body {
  padding: $jst-space-lg $jst-space-xl;
  max-height: 60vh;
  overflow-y: auto;
}

.jst-filter-bar__multi-group {
  margin-bottom: $jst-space-lg;
}

.jst-filter-bar__multi-title {
  display: block;
  font-size: $jst-font-sm;
  font-weight: $jst-weight-bold;
  color: $jst-text-regular;
  margin-bottom: $jst-space-md;
}

.jst-filter-bar__multi-tags {
  display: flex;
  flex-wrap: wrap;
  gap: $jst-space-md;
}

.jst-filter-bar__multi-tag {
  padding: 14rpx 28rpx;
  border-radius: $jst-radius-round;
  border: 2rpx solid $jst-border;
  background: $jst-bg-card;
  transition: all $jst-duration-fast;

  &:active { transform: scale(0.95); }

  &--active {
    background: $jst-brand-light;
    border-color: $jst-brand;
    .jst-filter-bar__multi-tag-text {
      color: $jst-brand;
      font-weight: $jst-weight-semibold;
    }
  }
}

.jst-filter-bar__multi-tag-text {
  font-size: $jst-font-sm;
  color: $jst-text-regular;
}

.jst-filter-bar__multi-footer {
  display: flex;
  gap: $jst-space-md;
  padding: $jst-space-lg $jst-space-xl;
  border-top: 2rpx solid $jst-border;
}

.jst-filter-bar__multi-reset {
  flex: 1;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: $jst-radius-lg;
  border: 2rpx solid $jst-border;
  background: $jst-bg-card;
  transition: transform $jst-duration-fast;

  &:active { transform: scale(0.98); }
}

.jst-filter-bar__multi-reset-text {
  font-size: $jst-font-base;
  color: $jst-text-secondary;
}

.jst-filter-bar__multi-confirm {
  flex: 2;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: $jst-radius-lg;
  background: $jst-brand;
  transition: transform $jst-duration-fast;

  &:active { transform: scale(0.98); opacity: 0.92; }
}

.jst-filter-bar__multi-confirm-text {
  font-size: $jst-font-base;
  font-weight: $jst-weight-semibold;
  color: $jst-text-inverse;
}
</style>
