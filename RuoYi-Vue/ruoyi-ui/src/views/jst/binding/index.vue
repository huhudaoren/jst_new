<template>
  <div class="app-container binding-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">用户渠道</p>
        <h2>绑定管理</h2>
        <p class="hero-desc">查看学生与渠道方的绑定关系，支持强制解绑操作。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px" class="query-panel">
      <el-form-item label="学生姓名" prop="studentName">
        <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="渠道名称" prop="channelName">
        <el-input v-model="queryParams.channelName" placeholder="请输入渠道名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="有效" value="active" />
          <el-option label="已解绑" value="unbound" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 移动端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.bindingId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.studentName || row.userId }}</div>
              <div class="mobile-sub">渠道：{{ row.channelName || row.channelId }}</div>
            </div>
            <el-tag size="mini" :type="row.status === 'active' ? 'success' : 'info'">{{ row.status === 'active' ? '有效' : '已解绑' }}</el-tag>
          </div>
          <div class="mobile-info-row">
            <span>绑定时间：{{ parseTime(row.bindTime) || '--' }}</span>
            <span v-if="row.unbindTime">解绑时间：{{ parseTime(row.unbindTime) }}</span>
          </div>
          <div class="mobile-actions">
            <el-button type="text" size="mini" icon="el-icon-scissors" v-if="row.status === 'active'" v-hasPermi="['jst:user:binding:forceUnbind']" @click="handleUnbind(row)">强制解绑</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无绑定记录" />
    </div>

    <!-- PC 表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="学生姓名" prop="studentName" min-width="100" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.studentName || scope.row.userId }}</template>
      </el-table-column>
      <el-table-column label="渠道名称" prop="channelName" min-width="120" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.channelName || scope.row.channelId }}</template>
      </el-table-column>
      <el-table-column label="绑定时间" prop="bindTime" width="160" align="center">
        <template slot-scope="scope">{{ parseTime(scope.row.bindTime) }}</template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="90" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.status === 'active' ? 'success' : 'info'">{{ scope.row.status === 'active' ? '有效' : '已解绑' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="解绑时间" prop="unbindTime" width="160" align="center">
        <template slot-scope="scope">{{ parseTime(scope.row.unbindTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-scissors" v-if="scope.row.status === 'active'" v-hasPermi="['jst:user:binding:forceUnbind']" @click="handleUnbind(scope.row)">强制解绑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!isMobile && !loading && list.length === 0" description="暂无绑定记录" />

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 解绑弹窗 -->
    <el-dialog title="强制解绑" :visible.sync="unbindOpen" width="500px" :fullscreen="isMobile" append-to-body>
      <el-alert title="强制解绑后，该学生将不再归属此渠道，且相关返点将停止计算。" type="warning" :closable="false" show-icon style="margin-bottom: 16px;" />
      <el-form ref="unbindForm" :model="unbindForm" :rules="unbindRules" label-width="80px">
        <el-form-item label="解绑原因" prop="reason">
          <el-input v-model="unbindForm.reason" type="textarea" :rows="3" placeholder="请输入解绑原因" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="unbindOpen = false">取 消</el-button>
        <el-button type="danger" :loading="submitLoading" @click="submitUnbind">确认解绑</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBinding, forceUnbind } from '@/api/admin/binding'

export default {
  name: 'BindingManage',
  data() {
    return {
      loading: false,
      submitLoading: false,
      showSearch: true,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentName: undefined,
        channelName: undefined,
        status: undefined
      },
      unbindOpen: false,
      unbindForm: { reason: '' },
      unbindRules: { reason: [{ required: true, message: '请输入解绑原因', trigger: 'blur' }] },
      currentBindingId: null
    }
  },
  created() {
    this.updateViewport()
    window.addEventListener('resize', this.updateViewport)
    this.getList()
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.updateViewport)
  },
  methods: {
    updateViewport() {
      this.isMobile = window.innerWidth <= 768
    },
    getList() {
      this.loading = true
      listBinding(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleUnbind(row) {
      this.currentBindingId = row.bindingId
      this.unbindForm.reason = ''
      this.unbindOpen = true
    },
    submitUnbind() {
      this.$refs.unbindForm.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        forceUnbind(this.currentBindingId, this.unbindForm).then(() => {
          this.$modal.msgSuccess('解绑成功')
          this.unbindOpen = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    }
  }
}
</script>

<style scoped>
.binding-page {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.page-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
  margin-bottom: 18px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.hero-eyebrow {
  margin: 0 0 8px;
  color: #2f6fec;
  font-size: 13px;
  font-weight: 600;
}

.page-hero h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #172033;
}

.hero-desc {
  margin: 8px 0 0;
  color: #6f7b8f;
}

.query-panel {
  padding: 16px 16px 0;
  margin-bottom: 16px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.mobile-list {
  min-height: 180px;
}

.mobile-card {
  padding: 16px;
  margin-bottom: 12px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.mobile-card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.mobile-title {
  font-weight: 700;
  color: #172033;
}

.mobile-sub {
  margin-top: 4px;
  font-size: 12px;
  color: #7a8495;
}

.mobile-info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
  font-size: 13px;
  color: #7a8495;
}

.mobile-actions {
  display: flex;
  gap: 14px;
  margin-top: 12px;
}

@media (max-width: 768px) {
  .binding-page {
    padding: 12px;
  }

  .page-hero {
    display: block;
    padding: 18px;
  }

  .page-hero .el-button {
    width: 100%;
    min-height: 44px;
    margin-top: 16px;
  }

  .page-hero h2 {
    font-size: 20px;
  }

  .query-panel {
    padding-bottom: 8px;
  }

  .query-panel ::v-deep .el-form-item {
    display: block;
    margin-right: 0;
  }

  .query-panel ::v-deep .el-form-item__content,
  .query-panel ::v-deep .el-select,
  .query-panel ::v-deep .el-input {
    width: 100%;
  }

  .mobile-actions .el-button {
    min-height: 44px;
  }
}
</style>
