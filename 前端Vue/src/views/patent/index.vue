<template>
  <div class="patent-page">
    <div class="page-header">
      <h2>专利成果</h2>
      <p class="page-desc">管理您的专利成果信息</p>
    </div>

    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><Opportunity /></el-icon>
            已提交专利
          </span>
          <el-button type="primary" :icon="Plus" @click="openAddDialog">
            添加专利
          </el-button>
        </div>
      </template>

      <el-table :data="patentList" border style="width: 100%" stripe>
        <el-table-column label="序号" width="60" align="center">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="专利成果名称" min-width="250" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === '专利' ? 'primary' : row.type === '专著' ? 'success' : 'warning'">
              {{ row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approvalTime" label="获批时间" width="120" align="center" />
        <el-table-column prop="patentNumber" label="专利号/证书号" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="审核状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '已通过' ? 'success' : row.status === '待审核' ? 'warning' : 'danger'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" :icon="Delete" @click="deletePatent(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加专利对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增专利成果" width="700px" :close-on-click-modal="false">
      <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
        <el-form-item label="专利成果名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入专利成果名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="专利" value="专利" />
            <el-option label="专著" value="专著" />
            <el-option label="软著" value="软著" />
          </el-select>
        </el-form-item>
        <el-form-item label="获批时间" prop="approvalTime">
          <el-date-picker
            v-model="form.approvalTime"
            type="date"
            placeholder="选择获批时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="专利号/证书号">
          <el-input v-model="form.patentNumber" placeholder="请输入专利号或证书号" />
        </el-form-item>
        <el-form-item label="专利证书照片" :required="!prefillCert">
          <div v-if="prefillCert" class="prefill-cert-tip">
            <el-icon><CircleCheck /></el-icon>
            <span>证书已通过快速录入上传</span>
          </div>
          <el-upload
            v-else
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
            accept=".jpg,.jpeg,.png,.webp"
            :limit="1"
            drag
          >
            <el-icon class="upload-icon"><Upload /></el-icon>
            <div class="el-upload__text">点击或拖拽专利证书照片到此处上传</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Opportunity, Upload, CircleCheck } from '@element-plus/icons-vue'
import { queryPatentList as searchApi, addPatent as addApi, delPatent as deleteApi } from '@/api/patent.js'
import { uploadApi } from '@/api/file.js'
import { check_Item } from '@/common/quick_fill.js'

const route = useRoute(), router = useRouter()
const patentList = ref([])
const currentPage = ref(1), pageSize = ref(10), total = ref(0)
const addDialogVisible = ref(false)
const formRef = ref(null)
const fileList = ref([])
const certFile = ref(null)
const prefillCert = ref(null)
const filePath = ref(''), fileName = ref(''), fileSize = ref(0)

const form = ref({ name: '', type: '', approvalTime: '', patentNumber: '' })
const rules = {
  name: [{ required: true, message: '请输入专利成果名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  approvalTime: [{ required: true, message: '请选择获批时间', trigger: 'change' }]
}

const search = async () => {
  try {
    const res = await searchApi(currentPage.value, pageSize.value, localStorage.getItem('userId'))
    if (res.code === 1) { patentList.value = res.data.rows; 
      total.value = res.data.total }
  } catch(e) { ElMessage.error('查询失败') }
}

const handleFileChange = async (file, files) => {
  fileList.value = files
  certFile.value = file.raw
  fileName.value = file.raw.name
  fileSize.value = file.raw.size
  try {
    const r = await uploadApi(file.raw)
    if (r.code == 1) filePath.value = r.data
  } catch(e) { ElMessage.error('上传失败') }
}

const handleSizeChange = val => { pageSize.value = val; currentPage.value = 1; search() }
const handleCurrentChange = val => { currentPage.value = val; search() }

const openAddDialog = (pf) => {
  addDialogVisible.value = true
  form.value = {
    name: pf?.name ?? '', type: pf?.type ?? '',
    approvalTime: pf?.approvalTime ?? '', patentNumber: pf?.patentNumber ?? ''
  }
  fileList.value = []; certFile.value = null; prefillCert.value = null
  if (pf?.certificatePath) {
    prefillCert.value = { path: pf.certificatePath, name: pf.certificateName, size: pf.certificateSize }
  }
}

const submitForm = async () => {
  await formRef.value.validate()
  let cp = null, cn = null, cs = null
  if (prefillCert.value) {
    cp = prefillCert.value.path; cn = prefillCert.value.name; cs = prefillCert.value.size
  } else if (certFile.value) {
    cp = filePath.value; cn = fileName.value; cs = fileSize.value
  }
  if (!cp) { ElMessage.warning('请上传专利证书照片'); return }
  try {
    await addApi({ ...form.value, certificatePath: cp, certificateName: cn, certificateSize: cs })
    ElMessage.success('添加成功')
    addDialogVisible.value = false; currentPage.value = 1; search()
  } catch(e) { ElMessage.error('添加失败') }
}

const deletePatent = (id) => {
  ElMessageBox.confirm('确定删除该专利？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    await deleteApi(id)
    ElMessage.success('删除成功'); search()
  }).catch(() => {})
}

onMounted(() => { search(); check_Item(route, router, 'patent', openAddDialog) })
</script>

<style scoped>
.patent-page {
  padding: 20px; background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}
.page-header { margin-bottom: 20px }
.page-header h2 { margin: 0 0 8px; font-size: 24px; font-weight: 600; color: #303133 }
.page-desc { margin: 0; font-size: 14px; color: #909399 }
.table-card { border-radius: 8px }
.card-header { display: flex; justify-content: space-between; align-items: center }
.card-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 600; color: #303133 }
.pagination-wrapper { margin-top: 20px; display: flex; justify-content: center; padding: 16px 0 }
.upload-icon { font-size: 40px; color: #c0c4cc; margin-bottom: 8px }
.prefill-cert-tip { display: flex; align-items: center; gap: 8px; color: #67c23a; padding: 12px; background: #f0f9eb; border-radius: 8px }
</style>
