<template>
  <div>
    <page-header
      title="基础表单"
      describe="表单页用于向用户收集或验证信息，基础表单常见于数据项较少的表单场景。表单域标签也可支持响应式."
    ></page-header>
    <page-layout>
      <a-card>
        <a-row type="flex" justify="center">
          <a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="22" :xxl="19">
            <a-form
              ref="ruleForm"
              :model="form"
              :rules="rules"
              :label-col="labelCol"
              :wrapper-col="wrapperCol"
            >
              <a-form-item ref="name" label="事件" name="name">
                <a-input v-model:value="form.name" />
              </a-form-item>
              <a-form-item label="地点" name="region">
                <a-select
                  v-model:value="form.region"
                  placeholder="please select your zone"
                >
                  <a-select-option value="shanghai"> Zone one </a-select-option>
                  <a-select-option value="beijing"> Zone two </a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="时间" required name="date1">
                <a-date-picker
                  v-model:value="form.date1"
                  show-time
                  type="date"
                  placeholder="Pick a date"
                  style="width: 100%"
                />
              </a-form-item>
              <a-form-item label="启用" name="delivery">
                <a-switch v-model:checked="form.delivery" />
              </a-form-item>
              <a-form-item label="状态" name="type">
                <a-checkbox-group v-model:value="form.type">
                  <a-checkbox value="1" name="type"> Online </a-checkbox>
                  <a-checkbox value="2" name="type"> Promotion </a-checkbox>
                  <a-checkbox value="3" name="type"> Offline </a-checkbox>
                </a-checkbox-group>
              </a-form-item>
              <a-form-item label="资源" name="resource">
                <a-radio-group v-model:value="form.resource">
                  <a-radio value="1"> Sponsor </a-radio>
                  <a-radio value="2"> Venue </a-radio>
                </a-radio-group>
              </a-form-item>
              <a-form-item label="描述" name="desc">
                <a-textarea v-model:value="form.desc" />
              </a-form-item>
              <a-form-item :wrapper-col="{ span: 14, offset: 4 }">
                <a-button type="primary" @click="onSubmit"> Create </a-button>
                <a-button style="margin-left: 10px" @click="resetForm">
                  Reset
                </a-button>
              </a-form-item>
            </a-form>
          </a-col>
        </a-row>
      </a-card>
    </page-layout>
    <page-footer></page-footer>
  </div>
</template>
<script>
export default {
  data() {
    return {
      labelCol: { xs: 4, sm: 3, md: 3, lg: 3, xl: 2, xxl: 3 },
      wrapperCol: { xs: 20, sm: 21, md: 21, lg: 21, xl: 20, xxl: 17 },
      other: "",
      form: {
        name: "",
        region: undefined,
        date1: undefined,
        delivery: false,
        type: [],
        resource: "",
        content: "",
        desc: ""
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
