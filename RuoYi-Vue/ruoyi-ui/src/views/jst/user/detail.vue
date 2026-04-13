<template>
  <el-drawer
    title="用户详情"
    :visible.sync="drawerVisible"
    :size="isMobile ? '100%' : '860px'"
    append-to-body
    custom-class="jst-user-detail-drawer"
  >
    <div v-loading="loading" class="detail-wrap">
      <template v-if="user">
        <el-descriptions title="基本信息" :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="用户ID">{{ user.userId }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ user.nickname || '--' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ user.mobile || '--' }}</el-descriptions-item>
          <el-descriptions-item label="用户类型">
            <JstStatusBadge :status="user.userType" :status-map="userTypeMap" />
          </el-descriptions-item>
          <el-descriptions-item label="账号状态">
            <JstStatusBadge :status="String(user.status)" :status-map="accountStatusMap" />
          </el-descriptions-item>
          <el-descriptions-item label="渠道绑定">{{ boundChannelDisplay(user) }}</el-descriptions-item>
          <el-descriptions-item label="监护人">{{ user.guardianName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="监护人手机号">{{ user.guardianMobile || '--' }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ parseTime(user.registerTime || user.createTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="最后登录">{{ parseTime(user.lastLoginTime) || '--' }}</el-descriptions-item>
        </el-descriptions>

        <div class="section-title">参赛档案列表</div>
        <el-table v-if="participants.length" :data="participants" size="small" max-height="220">
          <el-table-column label="档案ID" prop="participantId" width="96" />
          <el-table-column label="姓名" prop="name" min-width="120" show-overflow-tooltip>
            <template slot-scope="scope">{{ scope.row.name || '--' }}</template>
          </el-table-column>
          <el-table-column label="年龄" prop="age" width="72" />
          <el-table-column label="学校" prop="school" min-width="140" show-overflow-tooltip>
            <template slot-scope="scope">{{ scope.row.school || '--' }}</template>
          </el-table-column>
          <el-table-column label="认领状态" min-width="120">
            <template slot-scope="scope">
              <JstStatusBadge :status="scope.row.claimStatus" :status-map="claimStatusMap" />
            </template>
          </el-table-column>
          <el-table-column label="认领用户" min-width="100">
            <template slot-scope="scope">{{ participantClaimDisplay(scope.row) }}</template>
          </el-table-column>
          <el-table-column label="认领时间" min-width="160">
            <template slot-scope="scope">{{ parseTime(scope.row.claimedTime || scope.row.claimTime) || '--' }}</template>
          </el-table-column>
        </el-table>
        <JstEmptyState v-else description="暂无参赛档案" />

        <div class="section-title">绑定关系</div>
        <el-table v-if="bindings.length" :data="bindings" size="small" max-height="220">
          <el-table-column label="绑定ID" prop="bindingId" width="96" />
          <el-table-column label="渠道" min-width="160" show-overflow-tooltip>
            <template slot-scope="scope">{{ bindingChannelDisplay(scope.row) }}</template>
          </el-table-column>
          <el-table-column label="渠道类型" prop="channelType" min-width="100">
            <template slot-scope="scope">{{ scope.row.channelType || '--' }}</template>
          </el-table-column>
          <el-table-column label="状态" min-width="110">
            <template slot-scope="scope">
              <JstStatusBadge :status="scope.row.status" :status-map="bindingStatusMap" />
            </template>
          </el-table-column>
          <el-table-column label="绑定时间" min-width="160">
            <template slot-scope="scope">{{ parseTime(scope.row.bindTime) || '--' }}</template>
          </el-table-column>
          <el-table-column label="解绑时间" min-width="160">
            <template slot-scope="scope">{{ parseTime(scope.row.unbindTime) || '--' }}</template>
          </el-table-column>
        </el-table>
        <JstEmptyState v-else description="暂无绑定关系" />

        <div class="section-title">积分记录</div>
        <el-table v-if="pointsLogs.length" :data="pointsLogs" size="small" max-height="260">
          <el-table-column label="流水ID" prop="ledgerId" width="96" />
          <el-table-column label="变更类型" min-width="120">
            <template slot-scope="scope">
              <JstStatusBadge :status="scope.row.changeType" :status-map="changeTypeMap" />
            </template>
          </el-table-column>
          <el-table-column label="来源" prop="sourceType" min-width="120">
            <template slot-scope="scope">{{ scope.row.sourceType || '--' }}</template>
          </el-table-column>
          <el-table-column label="变更积分" min-width="110" align="right">
            <template slot-scope="scope">
              <span :class="Number(scope.row.pointsChange || 0) >= 0 ? 'points-plus' : 'points-minus'">
                {{ formatPoints(scope.row.pointsChange) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="变更后余额" prop="balanceAfter" min-width="120" align="right">
            <template slot-scope="scope">{{ scope.row.balanceAfter != null ? scope.row.balanceAfter : '--' }}</template>
          </el-table-column>
          <el-table-column label="时间" min-width="160">
            <template slot-scope="scope">{{ parseTime(scope.row.createTime) || '--' }}</template>
          </el-table-column>
        </el-table>
        <JstEmptyState v-else description="暂无积分记录" />
      </template>
      <JstEmptyState v-else description="暂无用户信息" />
    </div>
  </el-drawer>
</template>

<script>
import {
  getAdminUser,
  listParticipantMaps,
  listUserBindings,
  listUserPointsLedger
} from '@/api/jst/user/admin-user'
import { getAdminParticipant, listAdminParticipants } from '@/api/jst/user/admin-participant'

export default {
  name: 'JstAdminUserDetail',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    userId: {
      type: [Number, String],
      default: null
    }
  },
  data() {
    return {
      loading: false,
      user: null,
      participants: [],
      bindings: [],
      pointsLogs: [],
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
      claimStatusMap: {
        unclaimed: { label: '未认领', type: 'info' },
        auto_claimed: { label: '自动认领', type: 'success' },
        manual_claimed: { label: '人工认领', type: 'primary' },
        pending_manual: { label: '待人工', type: 'warning' }
      },
      bindingStatusMap: {
        active: { label: '生效', type: 'success' },
        expired: { label: '过期', type: 'info' },
        replaced: { label: '已替换', type: 'warning' },
        manual_unbound: { label: '人工解绑', type: 'danger' }
      },
      changeTypeMap: {
        earn: { label: '增加', type: 'success' },
        spend: { label: '消费', type: 'danger' },
        freeze: { label: '冻结', type: 'warning' },
        unfreeze: { label: '解冻', type: 'primary' },
        adjust: { label: '调整', type: 'info' },
        rollback: { label: '回滚', type: 'warning' }
      }
    }
  },
  computed: {
    drawerVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    },
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    }
  },
  watch: {
    visible(val) {
      if (val && this.userId) {
        this.loadData()
      }
    },
    userId(val) {
      if (this.visible && val) {
        this.loadData()
      }
    }
  },
  methods: {
    async loadData() {
      if (!this.userId) {
        return
      }
      this.loading = true
      try {
        const [userRes, bindingsRes, pointsRes, mapsRes, participantsRes] = await Promise.all([
          getAdminUser(this.userId),
          listUserBindings({ pageNum: 1, pageSize: 50, userId: this.userId }),
          listUserPointsLedger({ pageNum: 1, pageSize: 50, ownerId: this.userId }),
          listParticipantMaps({ pageNum: 1, pageSize: 50, userId: this.userId }),
          listAdminParticipants({ pageNum: 1, pageSize: 50, userId: this.userId })
        ])

        this.user = userRes.data || null
        this.bindings = Array.isArray(bindingsRes.rows) ? bindingsRes.rows : []
        this.pointsLogs = Array.isArray(pointsRes.rows) ? pointsRes.rows : []

        const directParticipants = Array.isArray(participantsRes.rows) ? participantsRes.rows : []
        if (directParticipants.length) {
          this.participants = directParticipants
        } else {
          const mapRows = Array.isArray(mapsRes.rows) ? mapsRes.rows : []
          this.participants = await this.loadParticipantsByMap(mapRows)
        }
      } catch (e) {
        this.user = null
        this.participants = []
        this.bindings = []
        this.pointsLogs = []
      } finally {
        this.loading = false
      }
    },
    async loadParticipantsByMap(mapRows) {
      if (!mapRows.length) {
        return []
      }
      const tasks = mapRows.map(async(row) => {
        try {
          const detail = await getAdminParticipant(row.participantId)
          return {
            ...(detail.data || {}),
            claimMethod: row.claimMethod,
            claimTime: row.claimTime,
            mapStatus: row.status
          }
        } catch (e) {
          return {
            participantId: row.participantId,
            claimMethod: row.claimMethod,
            claimTime: row.claimTime,
            claimStatus: row.status,
            name: '--'
          }
        }
      })
      return Promise.all(tasks)
    },
    formatPoints(value) {
      const n = Number(value || 0)
      return n >= 0 ? '+' + n : String(n)
    },
    participantClaimDisplay(row) {
      if (!row) return '--'
      return row.claimUserName || row.claimedUserName || row.userName || row.claimedUserId || row.userId || '--'
    },
    bindingChannelDisplay(row) {
      if (!row) return '--'
      return row.channelName || row.boundChannelName || row.channelId || '--'
    },
    boundChannelDisplay(row) {
      if (!row) return '--'
      return row.boundChannelName || row.channelName || row.boundChannelId || '--'
    }
  }
}
</script>

<style scoped>
.detail-wrap {
  padding: 0 18px 20px;
}

.section-title {
  margin: 20px 0 10px;
  font-size: 15px;
  font-weight: 600;
  color: #172033;
}

.points-plus {
  color: #67c23a;
  font-weight: 600;
}

.points-minus {
  color: #f56c6c;
  font-weight: 600;
}

@media (max-width: 768px) {
  .detail-wrap {
    padding: 0 12px 16px;
  }
}
</style>
