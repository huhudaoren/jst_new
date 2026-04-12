<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="模板编码" prop="templateCode">
        <el-input v-model="queryParams.templateCode" placeholder="请输入模板编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="模板名称" prop="templateName">
        <el-input v-model="queryParams.templateName" placeholder="请输入模板名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="发送渠道" prop="channel">
        <el-select v-model="queryParams.channel" placeholder="全部" clearable>
          <el-option v-for="item in channelOptions" :key="item.value" :label="item.label" :value="item.value" />
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
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['jst:message:message_template:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.templateId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.templateName }}</span>
            <el-tag size="mini" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>{{ row.templateCode }}</span>
            <span>{{ channelLabel(row.channel) }}</span>
            <span>{{ sceneLabel(row.scene) }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:message:message_template:edit']">编辑</el-button>
            <el-button type="text" size="mini" @click="handleToggle(row)" v-hasPermi="['jst:message:message_template:edit']">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无消息模板" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="模板ID" prop="templateId" width="90" />
      <el-table-column label="模板编码" prop="templateCode" min-width="140" />
      <el-table-column label="模板名称" prop="templateName" min-width="140" show-overflow-tooltip />
      <el-table-column label="发送渠道" min-width="120">
        <template slot-scope="{ row }">{{ channelLabel(row.channel) }}</template>
      </el-table-column>
      <el-table-column label="业务场景" min-width="140">
        <template slot-scope="{ row }">{{ sceneLabel(row.scene) }}</template>
      </el-table-column>
      <el-table-column label="模板内容" prop="content" min-width="220" show-overflow-tooltip />
      <el-table-column label="状态" width="90">
        <template slot-scope="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(row)"
            v-hasPermi="['jst:message:message_template:edit']"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="90">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:message:message_template:edit']">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" :width="isMobile ? '100%' : '680px'" :fullscreen="isMobile" append-to-body>
      <el-form ref="form" :model="form" :rules="formRules" :label-width="isMobile ? '84px' : '100px'">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="模板编码" prop="templateCode">
              <el-input v-model="form.templateCode" placeholder="例如：POINTS_CHANGE" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="模板名称" prop="templateName">
              <el-input v-model="form.templateName" placeholder="请输入模板名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="发送渠道" prop="channel">
              <el-select v-model="form.channel" placeholder="请选择">
                <el-option v-for="item in channelOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="业务场景" prop="scene">
              <el-select v-model="form.scene" placeholder="请选择">
                <el-option v-for="item in sceneOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="模板内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入模板内容" />
        </el-form-item>
        <el-alert title="变量占位符示例：${userName}、${pointsChange}、${orderNo}" type="info" :closable="false" />
        <el-form-item label="备注" style="margin-top: 12px;">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
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
import { listJst_message_template, getJst_message_template, addJst_message_template, updateJst_message_template } from '@/api/jst/message/jst_message_template'

const CHANNEL_OPTIONS = [
  { label: '站内信', value: 'inner' },
  { label: '短信', value: 'sms' },
  { label: '微信模板消息', value: 'wechat_template' },
  { label: '微信订阅消息', value: 'wechat' }
]

const SCENE_OPTIONS = [
  { label: '认证结果', value: 'auth_result' },
  { label: '提现结果', value: 'withdraw_result' },
  { label: '结算结果', value: 'settle_result' },
  { label: '积分变动', value: 'points_change' },
  { label: '发货通知', value: 'ship' },
  { label: '售后通知', value: 'aftersale' }
]

export default {
  name: 'JstMessageTemplate',
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
        templateCode: null,
        templateName: null,
        channel: null,
        status: null
      },
      channelOptions: CHANNEL_OPTIONS,
      sceneOptions: SCENE_OPTIONS,
      dialogVisible: false,
      dialogTitle: '',
      form: {},
      formRules: {
        templateCode: [{ required: true, message: '模板编码不能为空', trigger: 'blur' }],
        templateName: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }],
        channel: [{ required: true, message: '请选择发送渠道', trigger: 'change' }],
        scene: [{ required: true, message: '请选择业务场景', trigger: 'change' }],
        content: [{ required: true, message: '模板内容不能为空', trigger: 'blur' }]
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
    channelLabel(channel) {
      const match = CHANNEL_OPTIONS.find(item => item.value === channel)
      return match ? match.label : channel || '--'
    },
    sceneLabel(scene) {
      const match = SCENE_OPTIONS.find(item => item.value === scene)
      return match ? match.label : scene || '--'
    },
    getList() {
      this.loading = true
      listJst_message_template(this.queryParams).then(res => {
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
        templateId: null,
        templateCode: '',
        templateName: '',
        channel: 'inner',
        scene: 'points_change',
        content: '',
        status: 1,
        remark: ''
      }
    },
    handleAdd() {
      this.form = this.initForm()
      this.dialogTitle = '新增消息模板'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleEdit(row) {
      getJst_message_template(row.templateId).then(res => {
        this.form = { ...this.initForm(), ...(res.data || res || row) }
        this.dialogTitle = '编辑消息模板'
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
      updateJst_message_template(payload).then(() => {
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
        const api = this.form.templateId ? updateJst_message_template : addJst_message_template
        api(this.form).then(() => {
          this.$modal.msgSuccess(this.form.templateId ? '修改成功' : '新增成功')
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
