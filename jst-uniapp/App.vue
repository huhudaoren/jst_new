<script>
export default {
  onLaunch() {},
  onShow() {},
  onHide() {}
}
</script>

<style lang="scss">
@import '@/uni_modules/uni-scss/index.scss';
@import 'uview-ui/index.scss';
@import '@/static/customicons.css';
@import '@/styles/index.scss';
@import '@/styles/animations.scss';

page {
  background: var(--jst-color-page-bg);
  color: var(--jst-color-text);
  font-family: var(--jst-font-family);
  font-size: 28rpx;
  line-height: 1.6;
}

::-webkit-scrollbar { display: none; }

view,
text,
button,
input,
textarea,
image {
  box-sizing: border-box;
}

button {
  margin: 0;
  padding: 0;
  border: 0;
  background: transparent;
  line-height: 1.5;
}
</style>
