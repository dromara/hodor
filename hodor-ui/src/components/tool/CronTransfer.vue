<template>
  <div class="tool-transfer-container">
    <el-row class="cron-transfer">
      <el-tabs type="card">
        <el-tab-pane label="秒">
          <div class="tabWrap">
            <el-form
              label-width="10px"
              :model="secondForm"
            >
              <el-form-item>
                <el-radio-group v-model="secondForm.radio">
                  <el-row class="radioGroup">
                    <el-radio :label="1">每秒 允许的通配符[, - * /]</el-radio>
                  </el-row>
                  <el-row class="radioGroup middleLine">
                    <el-radio :label="2">
                      <el-row class="line-row">
                        <el-col
                          :span='1'
                          class="line-label"
                        >从</el-col>
                        <el-col :span="2">
                          <el-input
                            v-model="secondForm.startAt"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='3'
                          class="line-label"
                        > &nbsp;秒开始，到</el-col>
                        <el-col :span="2">
                          <el-input
                            v-model="secondForm.endAt"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='2'
                          class="line-label"
                        > &nbsp;秒，每</el-col>
                        <el-col :span="3">
                          <el-input
                            v-model="secondForm.interval"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='4'
                          class="line-label"
                        > &nbsp;秒执行一次</el-col>
                      </el-row>

                    </el-radio>
                  </el-row>
                  <el-row class="radioGroup">
                    <el-radio :label="3">指定</el-radio>
                    <el-checkbox-group
                      v-model="secondForm.checkVal"
                      class="checkGroup"
                    >
                      <el-checkbox
                        v-for="num in num60"
                        :label="num"
                        :key="num"
                      >{{add0(num)}}</el-checkbox>
                    </el-checkbox-group>
                  </el-row>

                </el-radio-group>

              </el-form-item>
            </el-form>
          </div>

        </el-tab-pane>
        <el-tab-pane label="分">
          <div class="tabWrap">
            <el-form
              label-width="10px"
              :model="minForm"
            >
              <el-form-item>
                <el-radio-group v-model="minForm.radio">
                  <el-row class="radioGroup">
                    <el-radio :label="1">每秒 允许的通配符[, - * /]</el-radio>
                  </el-row>
                  <el-row class="radioGroup middleLine">
                    <el-radio :label="2">
                      <el-row class="line-row">
                        <el-col
                          :span='1'
                          class="line-label"
                        >从</el-col>
                        <el-col :span="2">
                          <el-input
                            v-model="minForm.startAt"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='3'
                          class="line-label"
                        > &nbsp;分钟开始，到</el-col>
                        <el-col :span="2">
                          <el-input
                            v-model="minForm.endAt"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='2'
                          class="line-label"
                        > &nbsp;分钟，每</el-col>
                        <el-col :span="3">
                          <el-input
                            v-model="minForm.interval"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='4'
                          class="line-label"
                        > &nbsp;分钟执行一次</el-col>
                      </el-row>

                    </el-radio>
                  </el-row>
                  <el-row class="radioGroup">
                    <el-radio :label="3">指定</el-radio>
                    <el-checkbox-group
                      v-model="minForm.checkVal"
                      class="checkGroup"
                    >
                      <el-checkbox
                        v-for="num in num60"
                        :label="num"
                        :key="num"
                      >{{add0(num)}}</el-checkbox>
                    </el-checkbox-group>
                  </el-row>

                </el-radio-group>

              </el-form-item>
            </el-form>
          </div>

        </el-tab-pane>
        <el-tab-pane label="小时">
          <div class="tabWrap">
            <el-form
              label-width="10px"
              :model="hourForm"
            >
              <el-form-item>
                <el-radio-group v-model="hourForm.radio">
                  <el-row class="radioGroup">
                    <el-radio :label="1">每小时 允许的通配符[, - * /]</el-radio>
                  </el-row>
                  <el-row class="radioGroup middleLine">
                    <el-radio :label="2">
                      <el-row class="line-row">
                        <el-col
                          :span='1'
                          class="line-label"
                        >从</el-col>
                        <el-col :span="2">
                          <el-input
                            v-model="hourForm.startAt"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='3'
                          class="line-label"
                        > &nbsp;小时开始，到</el-col>
                        <el-col :span="2">
                          <el-input
                            v-model="hourForm.endAt"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='2'
                          class="line-label"
                        > &nbsp;小时，每</el-col>
                        <el-col :span="3">
                          <el-input
                            v-model="hourForm.interval"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='4'
                          class="line-label"
                        > &nbsp;小时执行一次</el-col>
                      </el-row>

                    </el-radio>
                  </el-row>
                  <el-row class="radioGroup">
                    <el-radio :label="3">指定</el-radio>
                    <el-checkbox-group
                      v-model="hourForm.checkVal"
                      class="checkGroup"
                    >
                      <el-checkbox
                        v-for="num in num24"
                        :label="num"
                        :key="num"
                      >{{add0(num)}}</el-checkbox>
                    </el-checkbox-group>
                  </el-row>

                </el-radio-group>

              </el-form-item>
            </el-form>
          </div>

        </el-tab-pane>
        <el-tab-pane label="日">
          <div class="tabWrap">
            <el-form
              label-width="10px"
              :model="dayForm"
            >
              <el-form-item>
                <el-radio-group v-model="dayForm.radio">
                  <el-row class="radioGroup">
                    <el-radio :label="1">每日 允许的通配符[, - * / L W]</el-radio>
                  </el-row>
                  <el-row class="radioGroup">
                    <el-radio :label="2">不指定</el-radio>
                  </el-row>
                  <el-row class="radioGroup middleLine">
                    <el-radio :label="3">
                      <el-row class="line-row">
                        <el-col
                          :span='2'
                          class="line-label"
                        >周期从</el-col>
                        <el-col :span="4">
                          <el-input
                            v-model="dayForm.startAt"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span="1"
                          class="line-label"
                        >
                          &nbsp;-
                        </el-col>
                        <el-col :span="4">
                          <el-input
                            v-model="dayForm.endAt"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='2'
                          class="line-label"
                        >&nbsp;日，每</el-col>
                        <el-col :span="3">
                          <el-input
                            v-model="dayForm.interval"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='4'
                          class="line-label"
                        > &nbsp;天执行一次</el-col>
                      </el-row>
                    </el-radio>
                  </el-row>
                  <el-row class="radioGroup middleLine">
                    <el-radio :label="4">
                      <el-row class="line-row">
                        <el-col
                          :span='2'
                          class="line-label"
                        >每月</el-col>
                        <el-col :span="4">
                          <el-input
                            v-model="dayForm.nearestweekday"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='6'
                          class="line-label"
                        >&nbsp;号最近的那个工作日</el-col>

                      </el-row>
                    </el-radio>
                  </el-row>

                  <el-row class="radioGroup middleLine">
                    <el-radio :label="5">
                      <el-row class="line-row">
                        <el-col
                          :span='7'
                          class="line-label"
                        >本月倒数第</el-col>
                        <el-col :span="6">
                          <el-input
                            v-model="dayForm.lastIndexDay"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='2'
                          class="line-label"
                        >&nbsp;天</el-col>
                      </el-row>

                    </el-radio>
                  </el-row>
                  <el-row class="radioGroup">
                    <el-radio :label="3">指定</el-radio>
                    <el-checkbox-group
                      v-model="dayForm.checkVal"
                      class="checkGroup"
                    >
                      <el-checkbox
                        v-for="num in num31"
                        :label="num+1"
                        :key="num"
                      >{{add0(num+1)}}</el-checkbox>
                    </el-checkbox-group>
                  </el-row>

                </el-radio-group>

              </el-form-item>
            </el-form>
          </div>

        </el-tab-pane>
        <el-tab-pane label="月">
          <div class="tabWrap">
            <el-form
              label-width="10px"
              :model="monthForm"
            >
              <el-form-item>
                <el-radio-group v-model="monthForm.radio">
                  <el-row class="radioGroup">
                    <el-radio :label="1">每月 允许的通配符[, - * /]</el-radio>
                  </el-row>
                  <el-row class="radioGroup">
                    <el-radio :label="2">不指定</el-radio>
                  </el-row>
                  <el-row class="radioGroup middleLine">
                    <el-radio :label="3">
                      <el-row class="line-row">
                        <el-col
                          :span='1'
                          class="line-label"
                        >从</el-col>
                        <el-col :span="2">
                          <el-input
                            v-model="monthForm.startAt"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='3'
                          class="line-label"
                        > &nbsp;月开始，到</el-col>
                        <el-col :span="2">
                          <el-input
                            v-model="monthForm.endAt"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='2'
                          class="line-label"
                        > &nbsp;月，每</el-col>
                        <el-col :span="3">
                          <el-input
                            v-model="monthForm.interval"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='4'
                          class="line-label"
                        > &nbsp;月执行一次</el-col>
                      </el-row>

                    </el-radio>
                  </el-row>
                  <el-row class="radioGroup">
                    <el-radio :label="4">指定</el-radio>
                    <el-checkbox-group
                      v-model="monthForm.checkVal"
                      class="checkGroup"
                    >
                      <el-checkbox
                        v-for="num in num12"
                        :label="num+1"
                        :key="num"
                      >{{add0(num+1)}}</el-checkbox>
                    </el-checkbox-group>
                  </el-row>
                </el-radio-group>
              </el-form-item>
            </el-form>
          </div>

        </el-tab-pane>
        <el-tab-pane label="周">
          <div class="tabWrap">
            <el-form
              label-width="10px"
              :model="weekForm"
            >
              <el-form-item>
                <el-radio-group v-model="weekForm.radio">
                  <el-row class="radioGroup">
                    <el-radio :label="1"> 每周 允许的通配符[, - * / L #]</el-radio>
                  </el-row>
                  <el-row class="radioGroup">
                    <el-radio :label="2"> 不指定</el-radio>
                  </el-row>
                  <el-row class="radioGroup middleLine">
                    <el-radio :label="3">
                      <el-row class="line-row">
                        <el-col
                          :span="1"
                          class="line-label"
                        >第
                        </el-col>
                        <el-col :span="4">
                          <el-input
                            v-model="weekForm.nthweek"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span="4"
                          class="line-label"
                        >&nbsp;周 的星期
                        </el-col>
                        <el-col :span="4">
                          <el-input
                            v-model="weekForm.weekday"
                            size="mini"
                          ></el-input>
                        </el-col>
                      </el-row>
                    </el-radio>
                  </el-row>
                  <el-row class="radioGroup middleLine">
                    <el-radio :label="4">
                      <el-row class="line-row">
                        <el-col
                          :span='10'
                          class="line-label"
                        >本月最后一个星期</el-col>
                        <el-col :span="6">
                          <el-input
                            v-model="weekForm.lastweekdayindex"
                            size="mini"
                          ></el-input>
                        </el-col>
                      </el-row>

                    </el-radio>
                  </el-row>
                  <el-row class="radioGroup">
                    <el-radio :label="5">指定</el-radio>
                    <el-checkbox-group
                      v-model="weekForm.checkVal"
                      class="checkGroup"
                    >
                      <el-checkbox
                        v-for="num in num7"
                        :label="num+1"
                        :key="num"
                      >{{add0(num+1)}}</el-checkbox>
                    </el-checkbox-group>
                  </el-row>
                </el-radio-group>
              </el-form-item>
            </el-form>
          </div>

        </el-tab-pane>
        <el-tab-pane label="年">
          <div class="tabWrap">
            <el-form
              label-width="10px"
              :model="yearForm"
            >
              <el-form-item>
                <el-radio-group v-model="yearForm.radio">
                  <el-row class="radioGroup">
                    <el-radio :label="2">不指定 允许的通配符[, - * /] 非必填</el-radio>
                  </el-row>
                  <el-row class="radioGroup">
                    <el-radio :label="1">每年</el-radio>

                  </el-row>
                  <el-row class="radioGroup middleLine">
                    <el-radio :label="3">
                      <el-row class="line-row">
                        <el-col
                          :span='2'
                          class="line-label"
                        >周期从</el-col>
                        <el-col :span="4">
                          <el-input
                            v-model="yearForm.startAt"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='4'
                          class="line-label"
                        > &nbsp;年开始，到</el-col>
                        <el-col :span="4">
                          <el-input
                            v-model="yearForm.endAt"
                            size="mini"
                          ></el-input>
                        </el-col>
                        <el-col
                          :span='2'
                          class="line-label"
                        > &nbsp;年。</el-col>

                      </el-row>

                    </el-radio>
                  </el-row>

                </el-radio-group>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
      <div class="transfer">
        <div class="transfer-border">
          <span class="transfer-label">表达式</span>
          <el-form label-width="150px">
            <br />
            <el-row>
              <el-col :span="8">
                <el-form-item label="Cron 表达式:">
                  <el-input
                    size="small"
                    placeholder="从上方选项勾选生成"
                    v-model="cronResult"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col
                :span="6"
                class="padding-top"
              >&nbsp;&nbsp;
                <el-button
                  type="primary"
                  size="small"
                  @click='generateCron'
                >生成quartz</el-button>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span='8'>
                <el-form-item label="crontab 表达式转换">
                  <el-input
                    placeholder="请输入crontab"
                    size="small"
                    v-model="crontab"
                  ></el-input>
                </el-form-item>

              </el-col>
              <el-col
                :span="4"
                class="padding-top"
              >&nbsp;&nbsp;
                <el-button
                  type="primary"
                  size="small"
                  @click="transferBatch"
                >解析为quartzCron</el-button>
              </el-col>
              <el-col
                :span="8"
                class="padding-top"
              >
                <pre>{{crontabs}}</pre>
              </el-col>
            </el-row>
            <br />
          </el-form>
        </div>
      </div>
    </el-row><br>

  </div>

</template>
<script>
import { successMsg, apiFn } from "@/assets/util";
import { cronTransferApi } from "@/api/tool";
const myMixin = {
  data: function() {
    return {
      cronTransferApi
    };
  }
};
export default {
  name: "CronTransfer",
  mixins: [myMixin],
  data() {
    return {
      cronResult: "",
      crontab: "",
      crontabs: "",
      threeKeys: ["allspec", "rangeFactor", "specifyFactor"],
      dayKey: [
        "allspec",
        "nospec",
        "rangeFactor",
        "nearestWeekday",
        "lastIndexDay",
        "specifyFactor"
      ],
      monthKey: ["allspec", "nospec", "rangeFactor", "specifyFactor"],
      weekKey: [
        "allspec",
        "nospec",
        "nthWeekday",
        "lastweekdayindex",
        "specifyFactor"
      ],
      yearKey: ["allspec", "nospec", "rangeFactor"],
      secondForm: { checkVal: [], radio: 1, startAt: 0, endAt: 1, interval: 1 },
      minForm: { checkVal: [], radio: 1, startAt: 0, endAt: 1, interval: 1 },
      hourForm: { checkVal: [], radio: 1, startAt: 0, endAt: 1, interval: 1 },
      dayForm: {
        checkVal: [],
        radio: 1,
        startAt: 1,
        endAt: 2,
        interval: 1,
        lastIndexDay: 1,
        nearestweekday: 1
      },
      monthForm: { checkVal: [], radio: 1, startAt: 0, endAt: 1, interval: 1 },
      weekForm: {
        checkVal: [],
        radio: 1,
        nthweek: 1,
        weekday: 1,
        lastweekdayindex: 1
      },
      yearForm: { radio: 1, startAt: 2017, endAt: 2018 },
      num60: this.createNum(60),
      num24: this.createNum(24),
      num31: this.createNum(31),
      num12: this.createNum(12),
      num7: this.createNum(7)
    };
  },
  components: {},
  created() {},
  watch: {},
  mounted() {},
  computed: {},
  methods: {
    createNum(len) {
      return Object.keys(Array.from({ length: len })).map(item => {
        return +item;
      });
    },
    add0(num) {
      return num >= 10 ? num : "0" + num;
    },
    setRangeFactor(form) {
      return {
        startAt: form.startAt,
        endAt: form.endAt,
        interval: form.interval
      };
    },
    setItemParam(form, keys) {
      let obj = {};
      let self = this;
      switch (keys[form.radio - 1]) {
        case "allspec":
          obj.allspec = true;
          break;
        case "rangeFactor":
          obj.rangeFactor = self.setRangeFactor(form);
          break;
        case "specifyFactor":
          obj.specifyFactor = { specify: form.checkVal };
          break;
        case "nospec":
          obj.nospec = false;
          break;
        case "nearestWeekday":
          obj.nearestWeekday = form.nearestweekday;
          break;
        case "lastIndexDay":
          obj.lastIndexDay = form.lastIndexDay;
          break;
        case "nthWeekday":
          obj.nthWeekday = { nthweek: form.nthweek, weekday: form.weekday };
          break;
        case "lastweekdayindex":
          obj.lastweekdayindex = form.lastweekdayindex;
          break;
      }
      return obj;
    },
    setParam() {
      let param = {
        second: JSON.stringify(
          this.setItemParam(this.secondForm, this.threeKeys)
        ),
        minute: JSON.stringify(this.setItemParam(this.minForm, this.threeKeys)),
        hour: JSON.stringify(this.setItemParam(this.hourForm, this.threeKeys)),
        dayOfMonth: JSON.stringify(
          this.setItemParam(this.dayForm, this.dayKey)
        ),
        month: JSON.stringify(this.setItemParam(this.monthForm, this.monthKey)),
        weekOfMonth: JSON.stringify(
          this.setItemParam(this.weekForm, this.weekKey)
        ),
        year: JSON.stringify(this.setItemParam(this.yearForm, this.yearKey))
      };
      return param;
    },
    transferBatch() {
      let param = { crontabs: JSON.stringify([this.crontab]) };
      apiFn()(cronTransferApi, "transferBatch", param, this).then(res => {
        if (Object.prototype.toString.call(res) == "[object Array]") {
          this.crontabs = res;
        }
      });
    },
    generateCron() {
      let param = this.setParam();
      apiFn()(cronTransferApi, "generateCron", param, this).then(res => {
        if (typeof res === "string") {
          this.cronResult = res;
        }
      });
    }
  }
};
</script>
<style scoped>
.tool-transfer-container {
  font-size: 12px;
  color: #333;
}

.tool-transfer-container .cron-transfer {
  width: 60%;
  margin: auto;
  border: 1px solid #e4e7ed;
}
.tool-transfer-container .transfer {
  padding: 10px;
}
.tool-transfer-container .transfer .transfer-border {
  position: relative;
  border: 1px solid #333;
  border-radius: 5px;
  padding: 5px;
}
.tool-transfer-container .transfer .transfer-border .transfer-label {
  position: absolute;
  background: white;
  top: -10px;
  left: 15px;
  border-radius: 5px;
  padding: 0px 5px;
  font-size: 10px;
}
.tool-transfer-container .transfer .padding-top {
  padding-top: 5px;
}
.tool-transfer-container .radioGroup {
  line-height: 34px;
  padding-right: 5px;
}
.tool-transfer-container .radioGroup.middleLine {
  height: 34px;
}
.tool-transfer-container .checkGroup {
  line-height: 24px;
}
.tool-transfer-container .checkGroup label {
  margin-left: 30px;
}
.tool-transfer-container .el-radio__label .line-label {
  line-height: 29px;
}
.tool-transfer-container .line-row {
  top: -21px;
  left: 26px;
}
.tool-transfer-container .tabWrap {
  min-height: 330px;
}
.tool-transfer-container pre {
  border: 1px solid #e4e7ed;
  width: 100%;
  padding: 5px;
  min-height: 24px;
}
</style>

