<template>
  <div class="form-container">

    <el-form
      ref="forms"
      :rules="rules"
      :model="forms"
    >
      <el-row>
        <el-col
          :span="item.spans?item.spans:24"
          v-if='formOptions.options'
          v-for='(item,index) in formOptions.options'
          class="colStyle"
        >
          <el-form-item
            :label="item.label"
            :label-width="item.formLabelWidth?item.formLabelWidth:formLabelWidth"
            :key='index'
            :prop="item.key"
            v-if='item.ifShow?!showCorninput:true'
          >
            <el-input
              v-if='item.type=="input"'
              v-model.trim="forms[item.key]"
              :placeholder="item.placeholder"
            ></el-input>
            <el-input
              v-if='item.type=="textarea"'
              type="textarea"
              :rows="2"
              v-model.trim="forms[item.key]"
            ></el-input>
            <el-input
              v-if='item.type=="password"'
              type="password"
              :rows="2"
              v-model.trim="forms[item.key]"
            ></el-input>
            <el-checkbox
              v-if='item.type=="checkbox"&&selectOpt[item.key]==null'
              v-model="forms[item.key]"
            ></el-checkbox>
            <el-checkbox-group
              class='checkboxGroup'
              v-if='item.type=="checkbox"&&selectOpt[item.key]&&selectOpt[item.key].length>0'
              v-model="forms[item.key]"
            >
              <el-checkbox
                :label="check.value"
                v-for="(check,index) in selectOpt[item.key]"
                :key="index"
              >{{check.label}}</el-checkbox>

            </el-checkbox-group>
            <el-radio
              v-if='item.type=="radio"'
              v-model="forms[item.key]"
              :label="radio.val"
              v-for="(radio,index) in selectOpt[item.key]"
              :key="index"
            >{{radio.label}}</el-radio>
            <el-autocomplete
              v-if='item.type=="autocomplete"'
              class="inline-input width100"
              v-model.trim="forms[item.key]"
              @focus='focusAutocomplete(selectOpt[item.key])'
              :fetch-suggestions="querySearch"
              placeholder="请输入内容"
            ></el-autocomplete>
            <el-select
              class="select-style"
              v-if='item.type=="select"'
              v-model="forms[item.key]"
              filterable
              :multiple='item.multiple'
              placeholder="请选择"
            >
              <el-option
                v-for="(opt,index) in selectOpt[item.key]"
                :key="index"
                :label="item.show?(item.preShow?(opt[item.show]+'【'+opt[item.preShow])+'】':item.valuePre?opt[item.valuePre]+'#'+opt[item.value]:opt[item.show]):opt"
                :value="item.value?(item.valuePre?opt[item.valuePre]+'#'+opt[item.value]:opt[item.value]):opt"
              >
              </el-option>
            </el-select>
            <el-upload
              v-if='item.type=="upload"'
              class="upload-demo"
              :action="item.url"
              :on-remove="handleRemove"
              :before-remove="beforeRemove"
              multiple
              :limit="1"
              :on-exceed="handleExceed"
              :on-change='fileChange'
              :file-list="forms[item.key]"
              :auto-upload="false"
            >
              <el-button
                size="small"
                type="primary"
              >选择文件</el-button>

            </el-upload>
            <el-date-picker
              class="dataPicker"
              v-if='item.type=="time"'
              v-model="forms[item.key]"
              type="datetime"
              placeholder="选择日期时间"
              value-format='yyyy-MM-dd HH:mm:ss'
            >
            </el-date-picker>

          </el-form-item>
          <el-col
            :span='1'
            class="tooltipStyle"
          >
            <el-tooltip
              v-if='item.info'
              class="item"
              effect="dark"
              :content="item.info"
              placement="right"
            >
              <i class="el-icon-info"></i>
            </el-tooltip>
          </el-col>

        </el-col>
      </el-row>

    </el-form>
    <div class="footerDiv">
      <span
        slot="footer"
        class="dialog-footer"
        v-if='show'
      >
        <el-button @click="close">取 消</el-button>
        <el-button
          type="primary"
          @click="confirm('forms')"
        >确 定</el-button>
      </span>
    </div>

  </div>
</template>

<script>
export default {
  name: "Forms",
  props: {
    formOptions: {
      type: Object,
      default: () => {
        return {};
      }
    },
    rules: {
      type: Object,
      default: () => {
        return {};
      }
    },
    show: {
      type: Boolean,
      default: () => {
        return false;
      }
    },
    selectOpt: {
      type: Object,
      default: () => {
        return {};
      }
    },
    editType: {
      type: String,
      default: () => {
        return "add";
      }
    },
    formVal: {
      type: Object,
      default: () => {
        return {};
      }
    },
    showCorninput: {
      type: Boolean,
      default: () => {
        return false;
      }
    }
  },
  data() {
    return {
      formLabelWidth: "120px",
      forms: {},
      autoInputList: [],
      checkLabel: [
        { label: "错过马上执行", value: "misfire" },
        { label: "马上调度", value: "fireNow" },
        { label: "广播模式", value: "ifBroadcast" }
      ]
    };
  },
  created() {
    this.setForm(this.formVal);
  },
  updated() {},
  mounted() {},
  components: {},
  watch: {
    formVal: {
      handler(newVal, oldVal) {
        if (this.editType == "new") {
          newVal.postjobs = newVal.hasOwnProperty("postjobs")
            ? newVal.postjobs
            : (newVal.postjobs = []);

          for (let key in newVal) {
            newVal[key] == null ? (newVal[key] = "") : "";
          }
        }
        this.setForm(newVal);
      },
      deep: true
    }
  },
  methods: {
    setForm(form) {
      this.forms = Object.assign({});
      this.forms.checkFire = [];
      if (this.$refs.forms) {
        this.$refs.forms.resetFields();
      }
      if (this.editType != "add") {
        this.forms = Object.assign({}, this.forms, form);
        this.forms.checkFire = this.initChechFire(form);
      }
    },
    initChechFire(vals) {
      let temp = [];
      this.checkLabel.forEach(ele => {
        if (vals[ele.value]) {
          temp.push(ele.value);
        }
      });
      return temp;
    },
    close() {
      this.$emit("handledialog");
    },
    validate(name) {
      let flag = false;
      this.$refs[name].validate(valid => {
        if (valid) {
          flag = true;
        }
      });
      return flag;
    },
    confirm(name) {
      this.$refs[name].validate(valid => {
        if (valid) {
          this.$emit("handledialog", this.forms);
        }
      });
    },

    showFilter(cb, rows) {
      if (cb) {
        cb(rows);
      } else {
        return;
      }
    },
    bindEvent(cb, param) {
      return cb(param);
    },
    handleRemove(file, fileList) {
      this.$emit("update:file", null);
    },
    handleExceed(files, fileList) {
      this.$message.warning(`当前限制选择 1个文件`);
    },
    fileChange(files, fileList) {
      this.$emit("update:file", files);
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${file.name}？`);
    },
    focusAutocomplete(data) {
      this.autoInputList = this.filterAutocompleteData(data);
    },

    filterAutocompleteData(data) {
      return data.map(item => {
        return { value: item };
      });
    },
    querySearch(queryString, cb) {
      var res = this.autoInputList;
      var results = queryString
        ? res.filter(this.createFilter(queryString))
        : res;
      // 调用 callback 返回建议列表的数据
      cb(results);
    },
    createFilter(queryString) {
      return res => {
        return res.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0;
      };
    }
  },
  destroyed() {}
};
</script>
<style>
.footerDiv {
  text-align: right;
}
.select-style {
  width: 100%;
}
.form-container .dataPicker {
  width: 100%;
}
.form-container .checkboxGroup {
  margin-bottom: -5px;
}
.form-container .colStyle {
  position: relative;
}
.form-container .tooltipStyle {
  position: absolute;
  top: 13px;
  left: 8px;
}
</style>
