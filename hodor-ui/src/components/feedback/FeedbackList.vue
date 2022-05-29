<template>
  <div class="user-action-container">
    <el-row>
      <el-col>
        <el-card
          class="box-card"
          shadow="hover"
        >
          <div
            slot="header"
            class="clearfix"
          >
            <span>反馈列表：</span>
            <el-button
              style="float: right; padding: 3px 0"
              type="text"
              @click='editFeedback'
            >我要反馈</el-button>
          </div>
          <el-row class="feedbackList">
            <ul>
              <li v-for='(item,index) in feedbackList'>
                <el-tooltip
                  class="item closeIcon"
                  effect="dark"
                  content="删除"
                  placement="top-center"
                >
                  <i
                    class="el-icon-circle-close"
                    @click="remove(item)"
                  ></i>
                </el-tooltip>

                <p class="title"><label>{{item.userName}}</label><span>{{item.createTime}}</span></p>
                <div class="content">
                  <pre v-html="item.content"> {{item.content}} </pre>
                </div>

              </li>
            </ul>

          </el-row>
          <el-row>
            <Pagination
              :pageSize.sync='pageSize'
              :currentPage.sync='currentPage'
              :total='total'
              @init='init'
            />
          </el-row>

        </el-card>
      </el-col>

    </el-row>

  </div>

</template>
<script>
import { apiFn, session, successMsg } from "../../assets/util.js";
import Pagination from "../common/Pagination";
import { feedbackApi } from "@/api/feedback";
const myMixin = {
  data: function() {
    return {
      feedbackApi
    };
  }
};
export default {
  name: "FeedbackList",
  mixins: [myMixin],
  data() {
    return {
      total: 0,
      pageSize: 10,
      currentPage: 1,
      feedbackList: [],
      userInfo:{}
    };
  },
  components: { Pagination },
  created() {},
  mounted() {
    this.getUserInfo();
    this.init();
  },
  methods: {
    getUserInfo() {
      let userInfo = session("userInfo");
      if (userInfo) {
        this.userInfo = userInfo;
      }
    },
    init() {
      let param = {
        username: this.userInfo.username ? this.userInfo.username : "",
        limit: this.pageSize,
        offset: (this.currentPage - 1) * this.pageSize
      };
      apiFn()(feedbackApi, "init", param, this).then(res => {
        if (res && res.successful) {
          this.feedbackList = res.rows;
          this.total = res.total;
        } else {
          this.feedbackList = [];
          this.total = 0;
        }
      });
    },
    remove(item) {
      this.$confirm("此操作将删除此条反馈, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          apiFn()(
            feedbackApi,
            "remove",
            {
              id: item.id,
              username: item.userName
            },
            this
          ).then(res => {
            if (res && res.successful) {
              successMsg(res.msg, this);
              this.init();
            }
          });
        })
        .catch(() => {});
    },
    editFeedback() {
      this.$router.push({
        name: "feedback"
      });
    }
  }
};
</script>
<style scoped>
.feedbackList li {
  background-color: #e8ecef;
  padding: 20px;
  border-radius: 5px;
  margin-top: 10px;
  color: #34495e;
  font-size: 12px;
  position: relative;
}
.feedbackList .content {
  padding-top: 10px;
}
.feedbackList li .feedbackList li .title {
  overflow: hidden;
  font-size: 14px;
  line-height: 30px;
}
.feedbackList li .title label {
  font-weight: 700;
}
.closeIcon {
  color: #f56c6c;
  font-size: 16px;
  position: absolute;
  right: 2px;
  top: 3px;
}
.feedbackList li span {
  float: right;
}
</style>

