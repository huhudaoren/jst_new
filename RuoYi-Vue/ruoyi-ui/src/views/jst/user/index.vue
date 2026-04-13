<template>
  <div class="app-container jst-admin-user-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">用户中心</p>
        <h2>用户管理</h2>
        <p class="hero-desc">管理学生/家长/渠道方账号，支持查看详情与启用禁用。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="80px"
      class="query-panel"
    >
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="queryParams.nickname" placeholder="请输入昵称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="queryParams.mobile" placeholder="请输入手机号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="用户类型" prop="userType">
        <el-select v-model="queryParams.userType" placeholder="全部" clearable>
          <el-option v-for="item in userTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="注册时间">
        <el-date-picker
          v-model="registerRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始"
          end-placeholder="结束"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="userList.length">
        <div v-for="row in userList" :key="row.userId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.nickname || '--' }}</div>
              <div class="mobile-sub">UID: {{ row.userId }}</div>
            </div>
            <JstStatusBadge :status="row.userType" :status-map="userTypeMap" />
          </div>
          <div class="mobile-info-row">手机号：{{ row.mobile || '--' }}</div>
          <div class="mobile-info-row">渠道绑定：{{ row.boundChannelId || '--' }}</div>
          <div class="mobile-info-row">注册时间：{{ parseTime(row.registerTime || row.createTime) || '--' }}</div>
          <div class="mobile-info-row">
            账号状态：
            <JstStatusBadge :status="String(row.status)" :status-map="accountStatusMap" />
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">查看详情</el-button>
            <el-button
              type="text"
              v-hasPermi="['jst:user:user:edit']"
              @click="toggleUserStatus(row)"
            >
              {{ Number(row.status) === 1 ? '禁用' : '启用' }}
            </el-button>
          </div>
        </div>
      </div>
      <JstEmptyState v-else description="暂无用户数据" />
    </div>

    <el-table v-else v-loading="loading" :data="userList">
      <el-table-column label="用户ID" prop="userId" min-width="92" />
      <el-table-column label="昵称" prop="nickname" min-width="120" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.nickname || '--' }}</template>
      </el-table-column>
      <el-table-column label="手机号" prop="mobile" min-width="130">
        <template slot-scope="scope">{{ scope.row.mobile || '--' }}</template>
      </el-table-column>
      <el-table-column label="用户类型" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="scope.row.userType" :status-map="userTypeMap" />
        </template>
      </el-table-column>
      <el-table-column label="渠道绑定ID" prop="boundChannelId" min-width="110">
        <template slot-scope="scope">{{ scope.row.boundChannelId || '--' }}</template>
      </el-table-column>
      <el-table-column label="账号状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.status)" :status-map="accountStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.registerTime || scope.row.createTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="190" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" v-hasPermi="['jst:user:user:query']" @click="openDetail(scope.row)">详情</el-button>
          <el-button
            type="text"
            v-hasPermi="['jst:user:user:edit']"
            @click="toggleUserStatus(scope.row)"
          >
            {{ Number(scope.row.status) === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <UserDetail :visible.sync="detailVisible" :user-id="currentUserId" />
  </div>
</template>

<script>
import { listAdminUsers, updateAdminUser } from '@/api/jst/user/admin-user'
import UserDetail from './detail.vue'

export default {
  name: 'JstAdminUserIndex',
  components: {
    UserDetail
  },
  data() {
    return {
      loading: false,
      total: 0,
      userList: [],
      registerRange: [],
      detailVisible: false,
      currentUserId: null,
      lastAutoOpenKey: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        nickname: undefined,
        mobile: undefined,
        userType: undefined
      },
      userTypeMap: {
        student: { label: '学生', type: 'success' },
        parent: { label: '家长', type: 'primary' },
        channel: { label: '渠道方', type: 'warning' }
      },
      accountStatusMap: {
        0: { label: '禁用', type: 'danger' },
        1: { label: '正常', type: 'success' },
        2: { label: '封禁', type: 'warning' }
      },
      userTypeOptions: [
        { label: '学生', value: 'student' },
        { label: '家长', value: 'parent' },
        { label: '渠道方', value: 'channel' }
      ]
    }
  },
  computed: {
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    }
  },
  created() {
    this.getList()
  },
  watch: {
    '$route.query': {
      handler() {
        this.tryAutoOpenFromRoute()
      },
      deep: true
    }
  },
  methods: {
    async getList() {
      this.loading = true
      try {
        const params = { ...this.queryParams }
        if (this.registerRange && this.registerRange.length === 2) {
          params.beginRegisterTime = this.registerRange[0]
          params.endRegisterTime = this.registerRange[1]
        }
        const res = await listAdminUsers(params)
        this.userList = Array.isArray(res.rows) ? res.rows : []
        this.total = Number(res.total || 0)
      } finally {
        this.loading = false
        this.tryAutoOpenFromRoute()
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.registerRange = []
      this.queryParams.pageNum = 1
      this.getList()
    },
    openDetail(row) {
      this.currentUserId = row.userId
      this.detailVisible = true
    },
    tryAutoOpenFromRoute() {
      const query = this.$route.query || {}
      if (query.autoOpen !== '1' || !query.userId) return
      const key = `user-${query.userId}-${query.autoOpen}`
      if (this.lastAutoOpenKey === key) return
      const target = this.userList.find(item => String(item.userId) === String(query.userId))
      const userId = Number(query.userId)
      if (!target && !userId) return
      this.lastAutoOpenKey = key
      this.openDetail(target || { userId })
    },
    toggleUserStatus(row) {
      const nextStatus = Number(row.status) === 1 ? 0 : 1
      const actionText = nextStatus === 1 ? '启用' : '禁用'
      this.$modal.confirm('确认' + actionText + '用户「' + (row.nickname || row.userId) + '」吗？').then(() => {
        return updateAdminUser({
          userId: row.userId,
          status: nextStatus
        })
      }).then(() => {
        this.$modal.msgSuccess(actionText + '成功')
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.jst-admin-user-page {
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
  margin-top: 8px;
  color: #7a8495;
  font-size: 13px;
}

.mobile-actions {
  display: flex;
  gap: 14px;
  margin-top: 12px;
}

@media (max-width: 768px) {
  .jst-admin-user-page {
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
  .query-panel ::v-deep .el-input,
  .query-panel ::v-deep .el-date-editor {
    width: 100%;
  }

  .mobile-actions .el-button {
    min-height: 44px;
  }
}
</style>
