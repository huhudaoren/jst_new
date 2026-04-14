<template>
  <div class="app-container level-config-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">积分管理</p>
        <h2>等级配置</h2>
        <p class="hero-desc">配置用户等级体系，设置成长值门槛与权益。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="等级名称" prop="levelName">
        <el-input v-model="queryParams.levelName" placeholder="请输入等级名称" clearable @keyup.enter.native="handleQuery" />
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
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['jst:points:level_config:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="sortedList.length" class="mobile-card-list">
        <div v-for="row in sortedList" :key="row.levelId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">Lv.{{ row.levelNo }} {{ row.levelName }}</span>
            <el-tag size="mini" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>编码：{{ row.levelCode }}</span>
            <span>门槛：{{ row.growthThreshold }}</span>
            <span>角色：{{ row.applicableRole }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:points:level_config:edit']">编辑</el-button>
            <el-button type="text" size="mini" @click="handleToggle(row)" v-hasPermi="['jst:points:level_config:edit']">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无等级配置" />
    </div>

    <el-table v-else v-loading="loading" :data="sortedList">
      <el-table-column label="等级ID" prop="levelId" width="90" />
      <el-table-column label="等级" width="70" align="center">
        <template slot-scope="{ row }"><span class="level-badge">{{ row.levelNo }}</span></template>
      </el-table-column>
      <el-table-column label="等级编码" prop="levelCode" min-width="120" />
      <el-table-column label="等级名称" prop="levelName" min-width="120" />
      <el-table-column label="成长值门槛" prop="growthThreshold" min-width="110" align="right" />
      <el-table-column label="适用角色" prop="applicableRole" min-width="100" />
      <el-table-column label="权益描述" prop="remark" min-width="180" show-overflow-tooltip />
      <el-table-column label="状态" width="90">
        <template slot-scope="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(row)"
            v-hasPermi="['jst:points:level_config:edit']"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="90">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:points:level_config:edit']">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" :width="isMobile ? '100%' : '620px'" :fullscreen="isMobile" append-to-body>
      <el-form ref="form" :model="form" :rules="formRules" :label-width="isMobile ? '84px' : '100px'">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="等级编码" prop="levelCode">
              <el-input v-model="form.levelCode" placeholder="请输入等级编码" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="等级名称" prop="levelName">
              <el-input v-model="form.levelName" placeholder="请输入等级名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="等级序号" prop="levelNo">
              <el-input-number v-model="form.levelNo" :min="1" :max="9999" controls-position="right" class="full-width" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="成长阈值" prop="growthThreshold">
              <el-input-number v-model="form.growthThreshold" :min="0" :max="99999999" controls-position="right" class="full-width" />
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
        <el-form-item label="权益描述" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入权益描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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
import { listJst_level_config, getJst_level_config, addJst_level_config, updateJst_level_config } from '@/api/jst/points/jst_level_config'

export default {
  name: 'JstLevelConfig',
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
        levelName: null,
        status: null
      },
      dialogVisible: false,
      dialogTitle: '',
      form: {},
      formRules: {
        levelCode: [{ required: true, message: '等级编码不能为空', trigger: 'blur' }],
        levelName: [{ required: true, message: '等级名称不能为空', trigger: 'blur' }],
        levelNo: [{ required: true, message: '等级序号不能为空', trigger: 'blur' }],
        growthThreshold: [{ required: true, message: '成长阈值不能为空', trigger: 'blur' }],
        applicableRole: [{ required: true, message: '请选择适用角色', trigger: 'change' }]
      }
    }
  },
  computed: {
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    },
    sortedList() {
      return (this.list || []).slice().sort((a, b) => Number(a.levelNo || 0) - Number(b.levelNo || 0))
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listJst_level_config(this.queryParams).then(res => {
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
        levelId: null,
        levelCode: '',
        levelName: '',
        levelNo: 1,
        growthThreshold: 0,
        icon: '',
        applicableRole: 'student',
        status: 1,
        remark: ''
      }
    },
    handleAdd() {
      this.form = this.initForm()
      this.dialogTitle = '新增等级配置'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleEdit(row) {
      getJst_level_config(row.levelId).then(res => {
        this.form = { ...this.initForm(), ...(res.data || res || row) }
        this.dialogTitle = '编辑等级配置'
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
      updateJst_level_config(payload).then(() => {
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
        const api = this.form.levelId ? updateJst_level_config : addJst_level_config
        api(this.form).then(() => {
          this.$modal.msgSuccess(this.form.levelId ? '修改成功' : '新增成功')
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
.level-config-page {
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
  background: #fff;
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
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.level-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #2f6fec;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  margin-right: 6px;
}

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

@media (max-width: 768px) {
  .level-config-page {
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

  .query-panel >>> .el-form-item {
    display: block;
    margin-right: 0;
  }

  .query-panel >>> .el-form-item__content,
  .query-panel >>> .el-select,
  .query-panel >>> .el-input {
    width: 100%;
  }

  .mobile-card__actions .el-button {
    min-height: 44px;
  }
}
</style>
