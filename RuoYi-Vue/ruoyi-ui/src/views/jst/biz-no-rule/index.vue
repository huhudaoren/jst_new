<template>
  <div class="app-container biz-no-rule-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">系统配置</p>
        <h2>编号规则</h2>
        <p class="hero-desc">管理业务编号生成规则，包含前缀、日期格式与序号位数配置。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <!-- 搜索区 -->
    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="80px" class="query-panel">
      <el-form-item label="规则名称" prop="ruleName">
        <el-input v-model="queryParams.ruleName" placeholder="请输入规则名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="规则编码" prop="ruleCode">
        <el-input v-model="queryParams.ruleCode" placeholder="请输入规则编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="启用" :value="0" />
          <el-option label="停用" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" v-hasPermi="['jst:admin:bizNoRule:add']" @click="handleAdd">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 移动端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.ruleId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.ruleName }}</div>
              <div class="mobile-sub">{{ row.ruleCode }}</div>
            </div>
            <el-tag size="mini" :type="row.status === 0 ? 'success' : 'info'">{{ row.status === 0 ? '启用' : '停用' }}</el-tag>
          </div>
          <div class="mobile-info-row">
            <span>前缀：{{ row.prefix || '--' }}</span>
            <span>日期格式：{{ row.dateFormat || '--' }}</span>
            <span>序号位数：{{ row.seqLength || '--' }}</span>
          </div>
          <div class="mobile-actions">
            <el-button type="text" size="mini" icon="el-icon-edit" v-hasPermi="['jst:admin:bizNoRule:edit']" @click="handleUpdate(row)">编辑</el-button>
            <el-button type="text" size="mini" icon="el-icon-delete" v-hasPermi="['jst:admin:bizNoRule:remove']" @click="handleDelete(row)">删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无编号规则" />
    </div>

    <!-- PC 表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="规则名称" prop="ruleName" min-width="140" show-overflow-tooltip />
      <el-table-column label="规则编码" prop="ruleCode" min-width="120" show-overflow-tooltip />
      <el-table-column label="前缀" prop="prefix" width="100" align="center" />
      <el-table-column label="日期格式" prop="dateFormat" width="120" align="center">
        <template slot-scope="{ row }">{{ row.dateFormat || '--' }}</template>
      </el-table-column>
      <el-table-column label="序号位数" prop="seqLength" width="100" align="center" />
      <el-table-column label="起始序号" prop="seqStart" width="100" align="center" />
      <el-table-column label="状态" width="80" align="center">
        <template slot-scope="{ row }">
          <el-tag size="mini" :type="row.status === 0 ? 'success' : 'info'">{{ row.status === 0 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="说明" prop="description" min-width="160" show-overflow-tooltip>
        <template slot-scope="{ row }">{{ row.description || '--' }}</template>
      </el-table-column>
      <el-table-column label="创建时间" width="160" align="center">
        <template slot-scope="{ row }">{{ parseTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140" align="center" fixed="right">
        <template slot-scope="{ row }">
          <el-button size="mini" type="text" icon="el-icon-edit" v-hasPermi="['jst:admin:bizNoRule:edit']" @click="handleUpdate(row)">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" v-hasPermi="['jst:admin:bizNoRule:remove']" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!isMobile && !loading && list.length === 0" description="暂无编号规则" />

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogOpen" width="560px" :fullscreen="isMobile" append-to-body>
      <el-form ref="ruleForm" :model="form" :rules="rules" label-width="100px">
        <el-divider content-position="left">基本信息</el-divider>
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" placeholder="请输入规则名称，如：证书编号规则" />
        </el-form-item>
        <el-form-item label="规则编码" prop="ruleCode">
          <el-input v-model="form.ruleCode" placeholder="请输入唯一编码，如：cert_no" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="规则说明" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入规则说明" />
        </el-form-item>
        <el-divider content-position="left">编号格式</el-divider>
        <el-form-item label="前缀" prop="prefix">
          <el-input v-model="form.prefix" placeholder="如：CERT、AUTH" />
        </el-form-item>
        <el-form-item label="日期格式" prop="dateFormat">
          <el-select v-model="form.dateFormat" placeholder="选择日期格式" clearable>
            <el-option label="yyyyMMdd（如 20260415）" value="yyyyMMdd" />
            <el-option label="yyyyMM（如 202604）" value="yyyyMM" />
            <el-option label="yyyy（如 2026）" value="yyyy" />
            <el-option label="无日期" value="" />
          </el-select>
        </el-form-item>
        <el-form-item label="序号位数" prop="seqLength">
          <el-input-number v-model="form.seqLength" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="起始序号" prop="seqStart">
          <el-input-number v-model="form.seqStart" :min="1" :max="999999" />
        </el-form-item>
        <el-divider content-position="left">状态</el-divider>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">启用</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogOpen = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBizNoRule, getBizNoRule, addBizNoRule, updateBizNoRule, delBizNoRule } from '@/api/admin/bizNoRule'

export default {
  name: 'BizNoRuleManage',
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
        ruleName: undefined,
        ruleCode: undefined,
        status: undefined
      },
      dialogOpen: false,
      dialogTitle: '',
      isEdit: false,
      form: {},
      rules: {
        ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
        ruleCode: [{ required: true, message: '请输入规则编码', trigger: 'blur' }],
        seqLength: [{ required: true, message: '请输入序号位数', trigger: 'blur' }],
        seqStart: [{ required: true, message: '请输入起始序号', trigger: 'blur' }]
      }
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
      listBizNoRule(this.queryParams).then(res => {
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
    resetForm(refName) {
      this.$refs[refName] && this.$refs[refName].resetFields()
    },
    initForm() {
      return {
        ruleId: undefined,
        ruleName: '',
        ruleCode: '',
        prefix: '',
        dateFormat: 'yyyyMMdd',
        seqLength: 4,
        seqStart: 1,
        description: '',
        status: 0
      }
    },
    handleAdd() {
      this.form = this.initForm()
      this.isEdit = false
      this.dialogTitle = '新增编号规则'
      this.dialogOpen = true
      this.$nextTick(() => {
        this.$refs.ruleForm && this.$refs.ruleForm.clearValidate()
      })
    },
    handleUpdate(row) {
      getBizNoRule(row.ruleId).then(res => {
        this.form = res.data || row
        this.isEdit = true
        this.dialogTitle = '编辑编号规则'
        this.dialogOpen = true
        this.$nextTick(() => {
          this.$refs.ruleForm && this.$refs.ruleForm.clearValidate()
        })
      })
    },
    submitForm() {
      this.$refs.ruleForm.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const action = this.isEdit ? updateBizNoRule(this.form) : addBizNoRule(this.form)
        action.then(() => {
          this.$modal.msgSuccess(this.isEdit ? '修改成功' : '新增成功')
          this.dialogOpen = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除编号规则「' + row.ruleName + '」？删除后不可恢复。').then(() => {
        return delBizNoRule(row.ruleId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.biz-no-rule-page {
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
  .biz-no-rule-page {
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
