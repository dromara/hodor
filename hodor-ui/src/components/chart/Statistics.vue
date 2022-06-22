<template>
  <div class="chart-container">
    <el-row
      v-if='jobStatusStatisticList'
      class='jobStatusStatisticList'
    >
      <el-row>
        <el-col :span="12">
          <el-row>
            <el-col
              :span="24/4"
              :offset="index>0?1:0"
              v-for='(item,index) in jobStatusStatisticList.jobStatus'
              :key='index'
            >
              <el-card
                class="box-card"
                shadow="always"
                v-bind:style="{ background:jobStatus[item.status].color }"
              >
                <div class="text item">
                  <h3> {{formatNums(item.nums)}}</h3>
                  <span>{{jobStatus[item.status].label}}</span>

                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-col>
        <el-col :span="12">
          <el-row>
            <el-col
              :span="24/6"
              :offset="index>0?1:0"
              v-for='(item,index) in jobStatusStatisticList.basicJobStatus'
              :key='index'
            >
              <el-card
                class="box-card"
                shadow="hover"
                v-bind:style="{ background:basicJobStatus[item.status].color }"
              >
                <div class="text item">
                  <h3> {{formatNums(item.nums)}}</h3>
                  <span>{{basicJobStatus[item.status].label}}</span>

                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
    </el-row>
    <br />
    <el-row>
      <el-form
        :inline="true"
        :model="formSearch"
        class="demo-form-inline"
      >
        <el-row>
          <el-col :span="8">
            <el-form-item class="formItemWidth">
              <el-select
                v-model="formSearch.groupName"
                filterable
                clearable
                size="small"
                placeholder="请选择任务组"
                class="width100"
                @change="onSearch"
              >
                <el-option
                  v-for="item in groupList"
                  :key="item.groupName"
                  :label="item.groupName"
                  :value="item.groupName"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-form-item label="执行时间">
            <el-date-picker
              size="small"
              v-model="formSearch.time"
              :picker-options="pickerOptions"
              type="datetimerange"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format='yyyy-MM-dd HH:mm:ss'
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button
              size="small"
              type="primary"
              @click="onSearch"
            >查询</el-button>
          </el-form-item>
        </el-row>

      </el-form>
    </el-row>
    <el-row class="chart">
      <chart
        width='100%'
        ref='piecharts'
        :options="lineChartOption"
      ></chart>
    </el-row>
    <el-row>
      <chart
        width='100%'
        ref='barcharts'
        :options="barChartOption"
      ></chart>
    </el-row>
  </div>
</template>

<script>
import { statisticsApi } from "@/api/statistics";
import { successMsg, apiFn } from "@/assets/util";
import { initOptions } from "@/utils/charts";
const myMixin = {
  data: function() {
    return {
      statisticsApi
    };
  }
};
export default {
  name: "Statistics",
  mixins: [myMixin],
  props: {},
  data() {
    return {
      formSearch: {},
      groupList: [],
      lineChartOption: {},
      barChartOption: {},
      colors: {
        failedBg: "#f56c6c",
        successBg: "#67c23a",
        runningBg: "#409eff",
        initBg: "#e6a23c"
      },
      jobStatus: [
        { label: "待激活任务总数", color: "#e6a23c" },
        { label: "正在运行任务总数", color: "#409eff" },
        { label: "正在运行任务总数", color: "#409eff" },
        { label: "暂停任务总数", color: "#f56c6c" }
      ],
      basicJobStatus: [
        { label: "任务执行失败总数", color: "#f56c6c" },
        { label: "任务执行成功总数", color: "#67c23a" },
        { label: "执行中任务总数", color: "#409eff" },
        { label: "KILLED任务总数", color: "#e6a23c" },
        { label: "待执行任务总数", color: "#47c4ff" }
      ],
      jobStatusStatisticList: null,

      keys: {
        normalJob: "正常调用job数",
        unusualJob: "未调度的job数",
        accumulateJob: "等待执行的job数",
        notAcceptedJob: "等待分配job数"
      },
      pieData: {},
      legendData: [
        "正常调用job数",
        "未调度的job数",
        "等待执行的job数",
        "等待分配job数"
      ],
      resultData: [],
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
        shortcuts: [
          {
            text: "最近一周",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", [start, end]);
            }
          },
          {
            text: "最近一个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit("pick", [start, end]);
            }
          },
          {
            text: "最近三个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit("pick", [start, end]);
            }
          }
        ]
      }
    };
  },
  created() {},
  updated() {},
  mounted() {
    this.getJobStatusStatistic();
    this.initGroup();
    this.onSearch();
  },
  components: {},
  methods: {
    initGroup() {
      apiFn()(statisticsApi, "initGroup", null, this).then(res => {
        if (Object.prototype.toString.call(res) == "[object Array]") {
          this.groupList = res;
        } else {
          this.groupList = [];
        }
      });
    },

    onSearch() {
      let param = {
        groupName: this.formSearch.groupName ? this.formSearch.groupName : "",
        startTime:
          this.formSearch.time && this.formSearch.time.length > 0
            ? this.formSearch.time[0]
            : "",
        endTime:
          this.formSearch.time && this.formSearch.time.length > 0
            ? this.formSearch.time[1]
            : ""
      };
      this.getJobExeInfo(param);
      this.searchResult(param);
    },
    getJobExeInfo(param) {
      this.showLoading("piecharts");
      apiFn()(statisticsApi, "jobExeInfo", param, this).then(res => {
        this.hideLoading("piecharts");
        if (res) {
          if (res.successful) {
            this.pieData = res.data;
            let gName = this.formSearch.groupName
              ? this.formSearch.groupName
              : "";
            let chartsTitle = {
              title: "job的统计信息",
              subtext: "组" + gName + "的统计信息"
            };
            this.setInitOpt(
              "lineChartOption",
              this.resetData(res.data),
              "pie",
              "job信息",
              this.legendData,
              chartsTitle
            );
          }
        }
      });
    },
    getJobStatusStatistic() {
      apiFn()(statisticsApi, "jobStatusStatistic", null, this).then(res => {
        if (res && res.successful) {
          this.jobStatusStatisticList = res.data;
        }
      });
    },
    formatNums(num) {
      if (num < 10000) {
        return num;
      } else {
        return num / 10000 + " w";
      }
    },
    resetData(obj) {
      let data = [];
      for (let key in obj) {
        let o = {};
        (o.value = obj[key]), (o.name = this.keys[key]);
        data.push(o);
      }
      return data;
    },
    showLoading(refChart) {
      this.$nextTick(() => {
        this.$refs[refChart].showLoading({
          text: "正在加载数据"
        });
      });
    },
    hideLoading(refChart) {
      this.$nextTick(() => {
        this.$refs[refChart].hideLoading();
      });
    },
    searchResult(param) {
      this.showLoading("barcharts");
      apiFn()(statisticsApi, "search", param, this).then(res => {
        this.hideLoading("barcharts");
        if (res) {
          if (res.successful) {
            this.resultData = res.data;
            let gName = this.formSearch.groupName
              ? this.formSearch.groupName
              : "";
            let chartsTitle = {
              title: "job耗时排名信息",
              subtext: "组" + gName + "耗时最多的前十个job"
            };
            this.setInitOpt(
              "barChartOption",
              this.resetBarData(res.data, "jobName", "consumerTime"),
              "bar",
              "job耗时排名信息",
              null,
              chartsTitle,
              "left",
              true
            );
          }
        }
      });
    },
    resetBarData(data, label, val) {
      let values = [];
      let labels = [];
      data.forEach(ele => {
        values.push(ele[val]);
        labels.push(ele[label]);
      });
      return { values: values, labels: labels };
    },
    setInitOpt(
      opt,
      data,
      type,
      title,
      legendData,
      chartsTitle,
      titlePositon,
      colorFul
    ) {
      this[opt] = initOptions(
        data,
        type,
        title,
        legendData,
        chartsTitle,
        titlePositon,
        colorFul
      );
    }
  },
  destroyed() {}
};
</script>
<style>
.mark {
  color: #f56c6c;
}
.formItemWidth {
  width: 100%;
}
.formItemWidth .el-form-item__content {
  width: 95%;
}
.chart-container .echarts {
  width: 100%;
}
.jobStatusStatisticList {
  padding: 5px;
  box-shadow: 0 1px 1px 0 rgba(0, 0, 0, 0.1);
}
.jobStatusStatisticList .el-card {
  color: white;
}

.jobStatusStatisticList .el-card h3 {
  font-size: 25px;
  font-weight: 200;
}
.jobStatusStatisticList .el-card span {
  font-size: 10px;
}
.jobStatusStatisticList .el-card .el-card__body {
  padding: 10px;
}
</style>
