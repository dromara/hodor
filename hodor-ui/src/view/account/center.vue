<template>
  <div class="center">
    <page-layout>
      <a-row :gutter="[15, 15]">
        <a-col :lg="6" :md="6" :sm="24" :xs="24">
          <a-card style="position: relative">
            <a-avatar
              class="avatar"
              :size="64"
              src="https://portrait.gitee.com/uploads/avatars/user/2813/8441097_shaynas_1610801433.png!avatar200"
            />
            <div class="username">就眠仪式</div>
            <div class="address">China</div>
            <a-divider />
            <div class="desc">江湖无名，安心练剑</div>
          </a-card>
          <a-card style="margin-top: 15px">
            标签
            <br />
            <br />
            <a-tag>很有想法</a-tag>
            <a-tag>专注设计</a-tag>
            <a-tag>帅</a-tag>
            <a-tag>海纳百川</a-tag>
            <br />
            <br />
            <br />
            团队
            <br />
            <br />
            <a-row :gutter="[20, 20]">
              <a-col span="12"
                ><a-avatar>科</a-avatar>&nbsp;&nbsp;&nbsp;科学搬砖组</a-col
              >
              <a-col span="12"
                ><a-avatar>程</a-avatar>&nbsp;&nbsp;&nbsp;程序员日常</a-col
              >
              <a-col span="12"
                ><a-avatar>中</a-avatar>&nbsp;&nbsp;&nbsp;中二少年团</a-col
              >
              <a-col span="12"
                ><a-avatar>计</a-avatar>&nbsp;&nbsp;&nbsp;计算机天团</a-col
              >
            </a-row>
          </a-card>
        </a-col>
        <a-col :lg="18" :md="18" :sm="24" :xs="24">
          <a-card>
            <a-tabs @change="callback">
              <a-tab-pane key="1" tab="基本信息">
                <a-form
                  ref="ruleForm"
                  :model="form"
                  :rules="rules"
                  :label-col="labelCol"
                  :wrapper-col="wrapperCol"
                  style="margin-top: 20px"
                >
                  <a-form-item ref="name" label="账号" name="name">
                    <a-input v-model:value="form.name" />
                  </a-form-item>
                  <a-form-item label="地区" name="region">
                    <a-select
                      v-model:value="form.region"
                      placeholder="请选择地区"
                    >
                      <a-select-option value="shanghai"> 上海 </a-select-option>
                      <a-select-option value="beijing"> 北京 </a-select-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item label="生日" required name="date1">
                    <a-date-picker
                      v-model:value="form.date1"
                      show-time
                      type="date"
                      placeholder="请选择日期"
                      style="width: 100%"
                    />
                  </a-form-item>
                  <a-form-item label="异地" name="delivery">
                    <a-switch v-model:checked="form.delivery" />
                  </a-form-item>
                  <a-form-item label="状态" name="type">
                    <a-checkbox-group v-model:value="form.type">
                      <a-checkbox value="1" name="type"> 在线 </a-checkbox>
                      <a-checkbox value="2" name="type"> 隐身 </a-checkbox>
                      <a-checkbox value="3" name="type"> 离线 </a-checkbox>
                    </a-checkbox-group>
                  </a-form-item>
                  <a-form-item label="签名" name="desc">
                    <a-textarea v-model:value="form.desc" />
                  </a-form-item>
                  <a-form-item :wrapper-col="{ span: 14, offset: 4 }">
                    <a-button type="primary" @click="onSubmit"> 保存 </a-button>
                    <a-button style="margin-left: 10px" @click="resetForm">
                      重置
                    </a-button>
                  </a-form-item>
                </a-form>
              </a-tab-pane>
              <a-tab-pane key="2" tab="账号绑定" force-render>
                Content of Tab Pane 2
              </a-tab-pane>
            </a-tabs>
          </a-card>
        </a-col>
      </a-row>
    </page-layout>
  </div>
</template>
<script>
export default {
  data() {
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 12 },
      other: "",
      form: {
        name: "就眠仪式",
        region: undefined,
        date1: undefined,
        delivery: true,
        type: ["1", "2"],
        resource: "北京",
        desc: "被岁月镂空, 亦受其雕琢"
      },
      rules: {
        name: [
          {
            required: true,
            message: "Please input Activity name",
            trigger: "blur"
          },
          {
            min: 3,
            max: 5,
            message: "Length should be 3 to 5",
            trigger: "blur"
          }
        ],
        region: [
          {
            required: true,
            message: "Please select Activity zone",
            trigger: "change"
          }
        ],
        date1: [
          {
            required: true,
            message: "Please pick a date",
            trigger: "change",
            type: "object"
          }
        ],
        type: [
          {
            type: "array",
            required: true,
            message: "Please select at least one activity type",
            trigger: "change"
          }
        ],
        resource: [
          {
            required: true,
            message: "Please select activity resource",
            trigger: "change"
          }
        ],
        desc: [
          {
            required: true,
            message: "Please input activity form",
            trigger: "blur"
          }
        ]
      }
    };
  },
  methods: {
    onSubmit() {
      this.$refs.ruleForm
        .validate()
        .then(() => {
          console.log("values", this.form);
        })
        .catch(error => {
          console.log("error", error);
        });
    },
    resetForm() {
      this.$refs.ruleForm.resetFields();
    }
  }
};
</script>
<style lang="less">
.center {
  .avatar {
    margin-left: 38%;
    margin-top: 40px;
  }
  .username {
    width: 100%;
    text-align: center;
    margin-top: 15px;
    font-size: 16px;
  }
  .address {
    width: 100%;
    text-align: center;
    font-size: 14px;
    margin-top: 10px;
  }
  .desc {
    width: 100%;
    text-align: center;
  }
}
</style>
