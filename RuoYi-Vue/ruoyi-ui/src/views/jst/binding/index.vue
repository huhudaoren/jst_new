<template>
  <div class="app-container">
    <!-- 搜索区 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
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
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="ID" prop="bindingId" width="70" />
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
        <template slot-scope="scope">{{ parseTime(scope.row.unbindTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-scissors" v-if="scope.row.status === 'active'" v-hasPermi="['jst:user:binding:forceUnbind']" @click="handleUnbind(scope.row)">强制解绑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 解绑弹窗 -->
    <el-dialog title="强制解绑" :visible.sync="unbindOpen" width="500px" append-to-body>
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
    this.getList()
  },
  methods: {
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
