<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="规则类型" prop="ruleType">
        <el-select v-model="queryParams.ruleType" placeholder="全部" clearable>
          <el-option label="积分规则" value="points" />
          <el-option label="成长值规则" value="growth" />
        </el-select>
      </el-form-item>
      <el-form-item label="来源行为" prop="sourceType">
        <el-select v-model="queryParams.sourceType" placeholder="全部" clearable>
          <el-option v-for="item in sourceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['jst:points:points_rule:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.ruleId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.ruleType === 'growth' ? '成长值规则' : '积分规则' }} #{{ row.ruleId }}</span>
            <el-tag size="mini" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>行为：{{ sourceTypeLabel(row.sourceType) }}</span>
            <span>模式：{{ row.rewardMode }}</span>
            <span>数值：{{ row.rewardValue }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:points:points_rule:edit']">编辑</el-button>
            <el-button type="text" size="mini" @click="handleToggle(row)" v-hasPermi="['jst:points:points_rule:edit']">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无积分规则" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="规则ID" prop="ruleId" width="90" />
      <el-table-column label="规则类型" width="100">
        <template slot-scope="{ row }">{{ row.ruleType === 'growth' ? '成长值' : '积分' }}</template>
      </el-table-column>
      <el-table-column label="来源行为" min-width="120">
        <template slot-scope="{ row }">{{ sourceTypeLabel(row.sourceType) }}</template>
      </el-table-column>
      <el-table-column label="模式" prop="rewardMode" width="90" />
      <el-table-column label="数值" prop="rewardValue" min-width="100" align="right" />
      <el-table-column label="适用角色" prop="applicableRole" min-width="110" />
      <el-table-column label="生效时间" min-width="150">
        <template slot-scope="{ row }">{{ parseTime(row.effectiveTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="失效时间" min-width="150">
        <template slot-scope="{ row }">{{ parseTime(row.expireTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template slot-scope="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(row)"
            v-hasPermi="['jst:points:points_rule:edit']"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="90">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:points:points_rule:edit']">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" :width="isMobile ? '100%' : '640px'" :fullscreen="isMobile" append-to-body>
      <el-form ref="form" :model="form" :rules="formRules" :label-width="isMobile ? '84px' : '100px'">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="规则类型" prop="ruleType">
              <el-select v-model="form.ruleType" placeholder="请选择">
                <el-option label="积分规则" value="points" />
                <el-option label="成长值规则" value="growth" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="来源行为" prop="sourceType">
              <el-select v-model="form.sourceType" placeholder="请选择">
                <el-option v-for="item in sourceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="奖励模式" prop="rewardMode">
              <el-select v-model="form.rewardMode" placeholder="请选择">
                <el-option label="固定值" value="fixed" />
                <el-option label="比例" value="rate" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="奖励数值" prop="rewardValue">
              <el-input-number v-model="form.rewardValue" :precision="4" :min="0" :max="999999" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="适用角色" prop="applicableRole">
          <el-select v-model="form.applicableRole" placeholder="请选择">
            <el-option label="学生" value="student" />
            <el-option label="渠道" value="channel" />
            <el-option label="全部" value="both" />
          </el-select>
        </el-form-item>
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="生效时间">
              <el-date-picker v-model="form.effectiveTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="可选" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="失效时间">
              <el-date-picker v-model="form.expireTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="可选" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="互斥组">
          <el-input v-model="form.mutexGroup" placeholder="请输入互斥组编码" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'
import { listJst_points_rule, getJst_points_rule, addJst_points_rule, updateJst_points_rule } from '@/api/jst/points/jst_points_rule'

const SOURCE_TYPE_OPTIONS = [
  { label: '报名', value: 'enroll' },
  { label: '课程', value: 'course' },
  { label: '签到', value: 'sign' },
  { label: '邀请', value: 'invite' },
  { label: '学习', value: 'learn' },
  { label: '获奖', value: 'award' }
]

export default {
  name: 'JstPointsRule',
  data() {
    return {
      loading: false,
      submitLoading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ruleType: null,
        sourceType: null,
        status: null
      },
      sourceTypeOptions: SOURCE_TYPE_OPTIONS,
      dialogVisible: false,
      dialogTitle: '',
      form: {},
      formRules: {
        ruleType: [{ required: true, message: '请选择规则类型', trigger: 'change' }],
        sourceType: [{ required: true, message: '请选择来源行为', trigger: 'change' }],
        rewardMode: [{ required: true, message: '请选择奖励模式', trigger: 'change' }],
        rewardValue: [{ required: true, message: '奖励数值不能为空', trigger: 'blur' }],
        applicableRole: [{ required: true, message: '请选择适用角色', trigger: 'change' }]
      }
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
  methods: {
    parseTime,
    sourceTypeLabel(sourceType) {
      const match = SOURCE_TYPE_OPTIONS.find(item => item.value === sourceType)
      return match ? match.label : sourceType || '--'
    },
    getList() {
      this.loading = true
      listJst_points_rule(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    initForm() {
      return {
        ruleId: null,
        ruleType: 'points',
        sourceType: 'enroll',
        rewardMode: 'fixed',
        rewardValue: 0,
        applicableRole: 'student',
        effectiveTime: null,
        expireTime: null,
        mutexGroup: '',
        status: 1,
        remark: ''
      }
    },
    handleAdd() {
      this.form = this.initForm()
      this.dialogTitle = '新增积分规则'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleEdit(row) {
      getJst_points_rule(row.ruleId).then(res => {
        this.form = { ...this.initForm(), ...(res.data || res || row) }
        this.dialogTitle = '编辑积分规则'
        this.dialogVisible = true
      })
    },
    handleToggle(row) {
      const status = row.status === 1 ? 0 : 1
      this.updateStatus({ ...row, status })
    },
    handleStatusChange(row) {
      this.updateStatus(row)
    },
    updateStatus(payload) {
      updateJst_points_rule(payload).then(() => {
        this.$modal.msgSuccess('状态更新成功')
        this.getList()
      }).catch(() => {
        this.getList()
      })
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        this.submitLoading = true
        const api = this.form.ruleId ? updateJst_points_rule : addJst_points_rule
        api(this.form).then(() => {
          this.$modal.msgSuccess(this.form.ruleId ? '修改成功' : '新增成功')
          this.dialogVisible = false
          this.getList()
        }).finally(() => {
          this.submitLoading = false
        })
      })
    }
  }
}
</script>

<style scoped>
.mobile-card-list {
  padding: 0 4px;
}

.mobile-card {
  background: #fff;
  border-radius: 8px;
  padding: 12px 14px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.mobile-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.mobile-card__title {
  font-weight: 600;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 65%;
}

.mobile-card__meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.mobile-card__actions {
  display: flex;
  gap: 6px;
}
</style>
