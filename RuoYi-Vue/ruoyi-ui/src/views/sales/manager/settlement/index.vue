<template>
  <div class="app-container">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">销售主管</p>
        <h2>业绩明细</h2>
        <p class="hero-desc">查看团队销售提成明细与月结审核。</p>
      </div>
    </div>

    <el-alert
      type="info"
      title="业绩明细与 Admin 月结审核流程将在 plan-04 完善（Admin 月结 API 补齐后接入）。"
      :closable="false"
      style="margin-bottom:16px"
    />

    <el-card shadow="never">
      <div slot="header">功能规划</div>
      <ul style="line-height:2;color:#606266;padding-left:20px">
        <li>团队销售提成台账列表（按月份 + 状态过滤）</li>
        <li>月结审核：运营主管确认后由 HR 发放</li>
        <li>提成明细 Drawer：展示每笔订单归因明细</li>
        <li>导出 Excel 报表</li>
      </ul>
      <el-empty description="plan-04 补充完整实现" :image-size="80" style="margin-top:16px" />
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'SalesManagerSettlement'
}
</script>

<style scoped>
.page-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px 24px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
  margin-bottom: 16px;
}
.hero-eyebrow { margin: 0; font-size: 12px; color: #409EFF; text-transform: uppercase; }
.page-hero h2 { margin: 4px 0; font-size: 22px; font-weight: 700; color: #172033; }
.hero-desc { margin: 4px 0 0; color: #6f7b8f; }
</style>
