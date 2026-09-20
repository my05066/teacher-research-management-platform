<template>
  <div class="help-page">
    <div class="page-header">
      <h2>帮助中心</h2>
      <p class="page-desc">使用说明和常见问题</p>
    </div>

    <el-row :gutter="20">
      <el-col :xs="24" :md="6">
        <el-card class="nav-card" shadow="hover">
          <el-menu :default-active="activeMenu" @select="handleMenuSelect">
            <el-menu-item index="guide">
              <el-icon><Document /></el-icon>
              <span>使用指南</span>
            </el-menu-item>
            <el-menu-item index="faq">
              <el-icon><QuestionFilled /></el-icon>
              <span>常见问题</span>
            </el-menu-item>
            <el-menu-item index="contact">
              <el-icon><Phone /></el-icon>
              <span>联系我们</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="18">
        <el-card class="content-card" shadow="hover">
          <!-- 使用指南 -->
          <div v-if="activeMenu === 'guide'" class="help-content">
            <h3>使用指南</h3>
            <el-collapse v-model="activeGuide">
              <el-collapse-item title="1. 科研项目录入" name="1">
                <div>
                  <p><strong>纵向项目：</strong></p>
                  <ul>
                    <li>选择项目级别（国家级/省部级/地市级）</li>
                    <li>填写项目名称和下达时间</li>
                    <li>上传结题材料</li>
                    <li>支持批量录入多个项目</li>
                  </ul>
                  <p><strong>横向项目：</strong></p>
                  <ul>
                    <li>选择是否签约企业</li>
                    <li>如签约企业，需填写企业名称和签约时间</li>
                    <li>填写执行周期</li>
                    <li>选择项目完成状态</li>
                    <li>已完成项目需上传结题材料，未完成项目需填写预计完成时间</li>
                  </ul>
                </div>
              </el-collapse-item>
              <el-collapse-item title="2. 发表论文录入" name="2">
                <div>
                  <ul>
                    <li>填写论文题目</li>
                    <li>选择论文等级（Science/Nature/SCI一区/SCI二区/SCI三区/SCI四区/EI/CSCD/CSSCI/CSCD-E/中文核心期刊）</li>
                    <li>选择作者信息（第一作者/通讯作者）</li>
                    <li>填写期刊名称和发表时间（可选）</li>
                  </ul>
                </div>
              </el-collapse-item>
              <el-collapse-item title="3. 专利成果录入" name="3">
                <div>
                  <ul>
                    <li>填写专利成果名称</li>
                    <li>选择类型（专利/专著/软著）</li>
                    <li>填写获批时间</li>
                    <li>填写专利号/证书号（可选）</li>
                  </ul>
                </div>
              </el-collapse-item>
              <el-collapse-item title="4. 科研获奖录入" name="4">
                <div>
                  <ul>
                    <li>填写获奖名称和获奖级别</li>
                    <li>选择获奖时间</li>
                    <li>填写颁奖机构（可选）</li>
                    <li>上传证书文件（可选）</li>
                  </ul>
                </div>
              </el-collapse-item>
              <el-collapse-item title="5. 学术会议录入" name="5">
                <div>
                  <ul>
                    <li>填写会议名称</li>
                    <li>选择会议类别（国际学术会议/国内学术会议）</li>
                    <li>选择是否做报告</li>
                    <li>如做报告，需上传佐证材料</li>
                    <li>填写会议时间、地点等信息（可选）</li>
                  </ul>
                </div>
              </el-collapse-item>
              <el-collapse-item title="6. 指导学生获奖录入" name="6">
                <div>
                  <ul>
                    <li>填写学生负责人名字</li>
                    <li>填写获奖项目名称</li>
                    <li>填写获奖类型</li>
                    <li>选择获奖时间（可选）</li>
                  </ul>
                </div>
              </el-collapse-item>
              <el-collapse-item title="7. 其他科研成果录入" name="7">
                <div>
                  <ul>
                    <li>填写成果标题</li>
                    <li>填写成果描述（可选）</li>
                    <li>上传证明文件（可选）</li>
                  </ul>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>

          <!-- 常见问题 -->
          <div v-if="activeMenu === 'faq'" class="help-content">
            <h3>常见问题</h3>
            <el-collapse v-model="activeFaq">
              <el-collapse-item title="Q1: 如何修改已提交的成果？" name="1">
                <div>
                  <p>已提交的成果在审核通过前可以删除后重新提交。审核通过后的成果如需修改，请联系管理员。</p>
                </div>
              </el-collapse-item>
              <el-collapse-item title="Q2: 文件上传有什么要求？" name="2">
                <div>
                  <p>支持的文件格式：PDF、DOC、DOCX、TXT、JPG、PNG</p>
                  <p>单个文件大小不超过10MB</p>
                </div>
              </el-collapse-item>
              <el-collapse-item title="Q3: 审核需要多长时间？" name="3">
                <div>
                  <p>一般情况下，审核会在3-5个工作日内完成。如有紧急情况，请联系管理员。</p>
                </div>
              </el-collapse-item>
              <el-collapse-item title="Q4: 如何查看审核结果？" name="4">
                <div>
                  <p>在对应的成果列表中，可以通过审核状态列查看审核结果。如被驳回，可查看审核备注了解原因。</p>
                </div>
              </el-collapse-item>
              <el-collapse-item title="Q5: 忘记密码怎么办？" name="5">
                <div>
                  <p>可以在登录页面点击"忘记密码"，通过工号和注册邮箱找回密码。如仍无法找回，请联系管理员。</p>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>

          <!-- 联系我们 -->
          <div v-if="activeMenu === 'contact'" class="help-content">
            <h3>联系我们</h3>
            <div class="contact-info">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="技术支持">
                  <el-icon><Phone /></el-icon>
                  理学系大数据实验室技术部
                </el-descriptions-item>
                <el-descriptions-item label="邮箱">
                  <el-icon><Message /></el-icon>
                  bigdata@tit.edu.cn
                </el-descriptions-item>
                <el-descriptions-item label="办公地址">
                  <el-icon><Location /></el-icon>
                  理学系实验楼***
                </el-descriptions-item>
                <el-descriptions-item label="工作时间">
                  <el-icon><Clock /></el-icon>
                  周一至周五 9:00-17:00
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Document, QuestionFilled, Phone, Message, Location, Clock } from '@element-plus/icons-vue';

const activeMenu = ref('guide');
const activeGuide = ref(['1']);
const activeFaq = ref(['1']);

const handleMenuSelect = (key) => {
  activeMenu.value = key;
};
</script>

<style scoped>
.help-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-desc {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.nav-card,
.content-card {
  border-radius: 8px;
}

.help-content h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.help-content ul {
  margin: 10px 0;
  padding-left: 20px;
}

.help-content li {
  margin: 8px 0;
  line-height: 1.6;
}

.help-content p {
  margin: 10px 0;
  line-height: 1.6;
}

.contact-info {
  margin-top: 20px;
}

.contact-info .el-icon {
  margin-right: 8px;
  color: #409eff;
}
</style>
