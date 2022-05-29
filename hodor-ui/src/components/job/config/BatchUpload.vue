<template>
  <div class="upload-container">
    <Breadcrumb
      :breadcrumb='breadcrumb'
      :title='title'
    />
    <br />

    <el-row class="uploadWrap">
      <el-card class="box-card">
        <div
          slot="header"
          class="clearfix"
        >
          <div>
            <h2>格式如下[required=true 为必要参数]：</h2>
            <ol>
              <li>请在添加前将group绑定好相关的会话。</li>
              <li>对于已经存在的job,我们只做更新的操作。</li>
              <li>配置文件中不需要加格外的注释。例如： #、/**/ 、// 等注释符</li>
            </ol>

          </div>
        </div>

        <pre>
[test] # server name.和下面的service 属性同名即jobName. required=true
group=testGroup #required=true
service=test #required=true
admin=jobowner #required=true
path=/data/www/devel/test/job.php #required=true
class=TestJobClass #required=true
method=callFunc #required=true
cron=0/50 * * * * ? #required=true
type=shell #required=true
timeout=600 #required=false
category=shellJob #required=false
broadcast=false #required=false
parameters=testParameterValues #required=false
postjobs=cct_testGroup#import-test-new4,groupName1#jobName1 #required=false
filename=start1.sh #required=false，选择此选项则需要上传文件的zip文件
description=dfgdfgtest #required=false
version=1 #required=false
        </pre>
        <br />
        <h2 class="redStyle">注意：系统默认统一以utf-8的编码方式处理。上传之前如果配置文件中有中文，请将文件以utf-8 的编码方式进行保存后再上传。否则可能会出现乱码。</h2>
      </el-card>
      <el-card class="box-card">

        <div>
          <form enctype="multipart/form-data">
            <el-row>
              <el-col :span="2">
                上传配置文件

              </el-col>
              <el-col :span='4'>
                <el-upload
                  class="upload-demo"
                  ref="upload"
                  action=""
                  :on-remove="(file, fileList)=>{handleRemove(file, fileList,'configFile')}"
                  :on-change="(file, fileList)=>{fileChange(file, fileList,'configFile')}"
                  :file-list="fileList"
                  :auto-upload="false"
                >
                  <el-button
                    slot="trigger"
                    size="small"
                    type="primary"
                  >选取文件</el-button>

                </el-upload>
              </el-col>
              <el-col :span="2">
                上传zip包：

              </el-col>
              <el-col :span='4'>
                <el-upload
                  class="upload-demo"
                  ref="upload"
                  action=""
                  :on-remove="(file, fileList)=>{handleRemove(file, fileList,'tarFile')}"
                  :on-change="(file, fileList)=>{fileChange(file, fileList,'tarFile')}"
                  :file-list="tarFileList"
                  :auto-upload="false"
                >
                  <el-button
                    slot="trigger"
                    size="small"
                    type="primary"
                  >选取zip包</el-button>

                </el-upload>
              </el-col>
            </el-row>
          </form>

        </div>
      </el-card>
      <div class="uploadBtn">
        <el-button
          style="margin-left: 10px;"
          size="small"
          type="success"
          @click="submitUpload"
        > 上 传 </el-button>
      </div>
      <el-row>
        <pre v-if='isErrInfo'>{{errInfo}}</pre>
        <div
          class="successDiv"
          v-if='isSuccessInfo'
        >文件上传成功 <span>{{time}}</span> 秒后 自动跳转到job 详情页！</div>
      </el-row>

    </el-row>

  </div>
</template>

<script>
import Breadcrumb from "../../common/Breadcrumb";
import { addJobApi } from "@/api/addJob";
import { successMsg, apiFn, sNameRule } from "@/assets/util";
import { saveBreadcrumb, getBreadcrumb } from "@/utils/breadcrumb";
const myMixin = {
  data: function() {
    return {
      addJobApi
    };
  }
};
export default {
  name: "BatchUpload",
  mixins: [myMixin],
  props: {
    rows: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  data() {
    return {
      title: "批量上传",
      routeItem: [],
      breadcrumb: {},
      configFile: null,
      tarFile: null,
      isSuccessInfo: false,
      isErrInfo: false,
      fileList: [],
      tarFileList: [],
      errInfo: "",
      time: 5,
      successInfo: "",
      timerId: null
    };
  },
  created() {
    this.saveBreadcrumb();
  },
  watch: {},
  updated() {},
  mounted() {},
  components: {Breadcrumb},
  methods: {
    saveBreadcrumb() {
      if (this.$route.params.breadcrumbItem) {
        this.breadcrumb = this.$route.params;
        saveBreadcrumb(this.$route.params);
      } else {
        this.breadcrumb = getBreadcrumb(this.routeItem);
      }
    },
    submitUpload() {
      let param = new FormData();
      param.append("configFile", this.configFile);
      param.append("tarFile", this.tarFile);
      apiFn()(addJobApi, "batchUpload", param, this).then(res => {
        if (res.successful) {
          this.isSuccessInfo = true;
          this.isErrInfo = false;
          this.timerInterval();
        } else {
          this.isSuccessInfo = false;
          this.isErrInfo = true;
          this.errInfo = res.msg;
        }
      });
    },
    handleRemove(file, fileList, type) {
      this[type] = file;
    },
    fileChange(file, fileList, type) {
      this[type] = file.raw;
    },
    timerInterval() {
      this.timerId = setInterval(() => {
        if (this.time > 0) {
          this.time--;
        } else {
          this.time = 0;
          clearInterval(this.timerId);
          this.$router.replace("/jobmessage");
        }
      }, 1000);
    }
  },
  destroyed() {}
};
</script>
<style>
.upload-container .uploadWrap li {
  font-size: 12px;
  color: red;
}
.upload-container .uploadWrap .redStyle {
  color: red;
}
.upload-container .uploadBtn {
  text-align: center;
  padding-top: 10px;
}
.upload-container pre {
  margin-top: 10px;
  border: 1px solid #ebeef5;
  padding: 5px;
}
.upload-container .successDiv {
  margin-top: 10px;
  border: 1px solid #ebeef5;
  padding: 5px;
  text-align: center;
  font-size: 30px;
}
.upload-container .successDiv span {
  font-size: 38px;
  font-weight: 700;
  color: #42b983;
}
</style>
