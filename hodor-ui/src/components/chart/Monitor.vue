<template>
  <div class="chart-container">
    <el-row>
      <label>显示任务条数：</label>
      <el-radio-group
        v-model="querytaskNum"
        size="small"
      >
        <el-radio-button
          :label="item.val"
          v-for='(item,index) in taskNums'
        >{{item.label}}</el-radio-button>
      </el-radio-group>
    </el-row>
    <br />
    <el-row class="chart">
      <chart
        width='100%'
        ref='piecharts'
        :options="barChartOption"
      ></chart>
    </el-row>
    <br/>
    <el-row>
      <label>查询条件：</label>
      <el-radio-group
        v-model="queryCondition"
        size="small"
      >
        <el-radio-button
          :label="item.val"
          v-for='(item,index) in conditions'
        >{{item.label}}</el-radio-button>
      </el-radio-group>
    </el-row>
    <br />
    <el-row>
      <chart
        width='100%'
        ref='barcharts'
        :options="lineChartOption"
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
  name: "Monitor",
  mixins: [myMixin],
  props: {},
  data() {
    return {
      querytaskNum: "1",
      queryCondition: "1",
      taskNums: [
        { label: "10", val: "1" },
        { label: "20", val: "2" },
        { label: "30", val: "3" },
        { label: "所有", val: "4" }
      ],
      conditions: [
        { label: "今天", val: "1" },
        { label: "昨天", val: "2" },
        { label: "近七天", val: "3" },
        { label: "本月", val: "4" },
        { label: "上个月", val: "5" }
      ],
      lineChartOption: {},
      barChartOption: {}
    };
  },
  created() {
    this.initChart();
  },
  updated() {},
  mounted() {},
  components: {},
  methods: {
    initChart() {
      this.setLineChart();
      this.setBarChart();
    },
    setLineChart() {
      let time = [
        "2016-07-07 01:41:23",
        "2016-07-07 02:30:00",
        "2016-07-07 03:51:06",
        "2016-07-07 04:54:12",
        "2016-07-07 05:32:45",
        "2016-07-07 06:07:43",
        "2016-07-07 07:17:43",
        "2016-07-07 08:57:03"
      ];
      let success = {
        name: "成功次数",
        data: [400, 450, 201, 624, 500, 630, 410, 1000],
        time: time
      };
      let failed = {
        name: "失败次数",
        data: [120, 221, 501, 134, 280, 130, 320, 600],
        time: time
      };
      let legendData = ["成功次数", "失败次数"];
      let chartsTitle = {
        title: "任务执行次数统计图"
      };
      this.setInitOpt(
        "lineChartOption",
        { succe: success, failed: failed },
        "line",
        legendData,
        time,
        chartsTitle,
        'left'
      );
    },
    setBarChart() {
      let xData = [
        "job-0",
        "job-1",
        "job-2",
        "job-3",
        "job-4",
        "job-5",
        "job-6",
        "job-7",
        "job-8",
        "job-9",
        "job-10"
      ];
      let data = [90, 60, 20, 90, 70, 10, 20, 100, 70, 10, 100];
      let legendData = ["执行进度"];
      let chartsTitle = {
        title: "正在执行任务--进度表"
      };
      this.setInitOpt(
        "barChartOption",
        { labels: xData, values: data },
        "bar",
        '执行进度',
        legendData,
        chartsTitle,
        'left',
        null,
        50
      );
    },

    setInitOpt(opt, data, type, title, legendData, chartsTitle,titlePosition,colorFul,barWidth) {
      this[opt] = initOptions(data, type, title, legendData, chartsTitle,titlePosition,colorFul,barWidth);
    }
  },
  destroyed() {}
};
</script>
<style scoped>
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
  height: 300px;
}
</style>
