<template>
  <div class="cornConfig-container">

    <el-row>
      <el-form
        :model="cornForm"
        status-icon
        ref="cornForm"
        class="demo-ruleForm"
        :rules="cornFormRules"
      >
        <el-row>
          <el-col :span="4">
            <el-form-item
              label=""
              prop="chooseSpecial"
              label-width="120px"
            >

              <el-radio
                v-model="cornForm.chooseSpecial"
                label="true"
              >按选择的时间表运行…</el-radio>

            </el-form-item>

          </el-col>
          <el-col :span="4">
            <el-form-item
              label=""
              prop="special"
              label-width="40px"
            >
              <el-select
                v-model="cornForm.special"
                placeholder="请选择"
                size="small"
              >
                <el-option
                  v-for="item in special"
                  :key="item.val"
                  :label="item.label"
                  :value="item.val"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span='4'>
            <el-form-item
              label=""
              prop="chooseSpecial"
              label-width="10px"
            >

              <el-radio
                v-model="cornForm.chooseSpecial"
                label="false"
              >按下面选择的时间运行…</el-radio>
            </el-form-item>
          </el-col>
          <el-col :span='2'>
            <el-form-item
              label=""
              prop="chooseSpecial"
              label-width="10px"
            >

              <el-radio
                v-model="cornForm.chooseSpecial"
                label="default"
              >默认</el-radio>
            </el-form-item>

          </el-col>
          <el-col :span='3'>
            <el-form-item
              label=""
              prop="chooseSpecial"
              label-width="10px"
            >

              <el-radio
                v-model="cornForm.chooseSpecial"
                label="customize"
              >输入cron时间表达式:</el-radio>
            </el-form-item>
          </el-col>
          <el-col
            :span='4'
            v-if='cornForm.chooseSpecial=="customize"'
          >
            <el-form-item
              label=""
              prop="customizeVal"
              label-width="10px"
            >
              <el-input
                v-model="cornForm.customizeVal"
                placeholder="请输入cron时间表达式"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="line" />
        <el-row>
          <el-col
            v-for='(time,index) in timeList'
            :span='24/timeList.length'
            :key='index'
          >
            <el-form-item
              label=""
              :prop="time.key"
              label-width="10px"
            >
              <h2>{{time.title}}</h2>

              <el-radio
                v-for='(radios,i) in timeRadio'
                :key='i'
                v-model="cornForm[time.key]"
                :label="radios.val"
              >{{radios.label}}</el-radio>

            </el-form-item>

          </el-col>

        </el-row>
        <el-row>

          <el-col
            v-for='(time,index) in timeList'
            :span='24/timeList.length'
            :key='index'
          >
            <el-form-item
              label=""
              :prop="time.option"
              label-width="10px"
            >
              <div
                class="lisDiv"
                :style="'width:'+time.divWidth"
                @click='selectLi($event,time.option)'
                :ref='time.option'
              >
                <ul :style="'column-count:'+ time.count+';width:'+time.width">
                  <li
                    v-for='(list,index) in time.list'
                    :id='list'
                  >{{list}}</li>
                </ul>
              </div>

            </el-form-item>
          </el-col>

        </el-row>
        <el-row>
          <h3 class="warn">注意：不可以同时指定天和周。</h3>
        </el-row>

      </el-form>
    </el-row>

  </div>
</template>

<script>
export default {
  name: "CornConfig",

  props: {
    cornValue: {
      type: Object,
      default: () => {
        return null;
      }
    }
  },
  data() {
    let arr60 = this.createList(60);
    return {
      cornForm: {
        chooseSpecial: true,
        customizeVal: "",
        special: "hourly",
        allSeconds: "true",
        allMins: "true",
        allHours: "true",
        allDays: "true",
        allMonths: "true",
        allWeekdays: "true"
      },
      cornFormRules: {
        customizeVal: [
          {
            required: true,
            message: "请输入cron时间表达式",
            trigger: "blur"
          }
        ]
      },
      mins: [],
      seconds: [],
      hours: [],
      days: [],
      months: [],
      weekdays: [],
      special: [
        { label: "每小时", val: "hourly" },
        { label: "每天（在午夜）", val: "daily" },
        { label: "每周（在星期日）", val: "weekly" },
        { label: "每月（在第一天）", val: "monthly" },
        { label: "每年（在1月1日）", val: "yearly" }
      ],
      timeRadio: [
        { label: "全部", val: "true" },
        { label: "已选择的...", val: "false" }
      ],
      timeList: [
        {
          title: "秒",
          key: "allSeconds",
          option: "seconds",
          list: [].concat(arr60),
          count: 5,
          width: "100px",
          divWidth: "154px"
        },
        {
          title: "分钟",
          key: "allMins",
          option: "mins",
          list: [].concat(arr60),
          count: 5,
          width: "100px",
          divWidth: "154px"
        },
        {
          title: "小时",
          key: "allHours",
          option: "hours",
          list: this.createList(24),
          count: 2,
          width: "50px",
          divWidth: "82px"
        },
        {
          title: "天",
          key: "allDays",
          option: "days",
          list: this.createList(32),
          count: 3,
          width: "50px",
          divWidth: "108px"
        },
        {
          title: "月",
          key: "allMonths",
          option: "months",
          list: this.createList(12, "月", "after"),
          count: 1,
          width: "50px",
          divWidth: "108px"
        },
        {
          title: "周",
          key: "allWeekdays",
          option: "weekdays",
          list: this.createList(7, "星期"),
          count: 1,
          width: "50px",
          divWidth: "56px"
        }
      ],
      upperLable: [
        "一",
        "二",
        "三",
        "四",
        "五",
        "六",
        "七",
        "八",
        "九",
        "十",
        "十一",
        "十二"
      ]
    };
  },
  created() {},
  watch: {
    cornValue: {
      handler(val) {
        if (val) {
          this.cornForm = this.resetCornForm(val);
          this.$nextTick(() => {
            this.setSelect(this.cornForm);
          });
        }
      },
      deep: true,
      immediate: true
    }
  },
  updated() {},
  mounted() {},
  components: {},
  methods: {
    setSelect(obj) {
      let item = ["days", "hours", "mins", "months", "seconds", "weekdays"];
      item.forEach(ele => {
        obj[ele] ? this.filterSelect(ele, obj[ele]) : "";
      });
    },
    filterSelect(key, val) {
      let lis = this.$refs[key][0].childNodes[0].childNodes;
      lis.forEach(ele => {
        val.forEach(ele1 => {
          ele.innerHTML == ele1 ? this.selectLi({}, key, ele) : "";
        });
      });
    },
    resetCornForm(val) {
      let obj = {};
      for (let key in val) {
        if (key == "chooseSpecial") {
          obj[key] =val[key]
        } else {
          if (key == "customizeVal" || key == "special") {
            obj[key] = val[key] ? val[key] : "";
          }
          else if (key == "days" || key == "hours" || key == "mins" ||
              key == "months" || key == "seconds" || key == "weekdays") {
            obj[key] = val[key] ? val[key] : null;
          }
          else {
            obj[key] = val[key] ? val[key] + "" : "false";
          }
        }
      }
      return obj;
    },
    createList(len, label, type) {
      let upperLable = [
        "一",
        "二",
        "三",
        "四",
        "五",
        "六",
        "七",
        "八",
        "九",
        "十",
        "十一",
        "十二"
      ];
      if (label == null) {
        return Object.keys(Array.from({ length: len })).map(item => {
          return +item;
        });
      } else {
        return Object.keys(Array.from({ length: len })).map(item => {
          return type ? upperLable[+item] + label : label + upperLable[+item];
        });
      }
    },
    selectLi(event, option, ele) {
      let val = ele ? ele.id : event.target.id;
      if (val) {
        if (option == "months" || option == "weekdays") {
          val = this.filterMonthAndWeek(val, option);
        }
        let i = this.checkExt(this[option], val);
        i > -1
          ? this.removeLi(event, option, i, ele)
          : this.addLi(event, option, val, ele);
      }
    },
    addLi(event, option, val, ele) {
      ele
        ? ele.classList.add("addColor")
        : event.target.classList.add("addColor");
      this[option].push(val);
      this.cornForm[option] = this[option];
    },
    removeLi(event, option, i, ele) {
      ele
        ? ele.classList.remove("addColor")
        : event.target.classList.remove("addColor");
      this[option].splice(i, 1);
      this.cornForm[option] = this[option];
    },
    filterMonthAndWeek(val, option) {
      let start = option == "months" ? 0 : 2;
      let len = option == "months" ? val.length - 1 : val.length;
      var s = val.substring(start, len);
      return this.upperLable.indexOf(s) + "";
    },
    checkExt(data, val) {
      return data.indexOf(val);
    }
  },
  destroyed() {}
};
</script>
<style scopd>
.cornConfig-container .line {
  height: 5px;
  border-bottom: 1px dashed #e7eaec;
}
.cornConfig-container ul {
  margin: 0px;
  padding: 0px;
  height: 250px;
  width: 100px;
  text-align: center;
  -moz-column-gap: 4px;
  -moz-column-count: 3;
  -moz-column-rule: 1px solid #d8d8d8;
  -moz-column-width: 22px;
  -webkit-column-gap: 4px;
  -webkit-column-count: 3;
  -webkit-column-rule: 1px solid #d8d8d8;
  -webkit-column-width: 22px;
  column-gap: 4px;
  column-count: 3;
  column-width: 22px;
  column-rule: 1px solid #d8d8d8;
}
.cornConfig-container ul li {
  list-style: none;
  height: 25px;
  line-height: 20px;
  cursor: pointer;
}
.cornConfig-container .lisDiv {
  border: 1px solid #d8d8d8;
  margin: auto;
}
.addColor {
  background: #42b983;
  color: white;
}
.cornConfig-container .warn {
  color: #f56c6c;
}
</style>
