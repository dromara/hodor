<template>
  <div class="user-action-container">
    <el-row>
      <el-col :span='12'>
        <el-card
          class="box-card"
          shadow="hover"
        >
          <div
            slot="header"
            class="clearfix"
          >
            <span>请填写反馈或意见：</span>

            <el-button
              style="float: right; padding: 3px 0"
              type="text"
              v-if='userInfo.roleId==0'
              @click='checkList'
            >查看其他反馈意见</el-button>
          </div>
          <el-row>
            <Editor @content='changeContent' />
          </el-row>
          <el-row class="submitFeedback">
            <el-button
              @click="submitFeedback"
              type="primary"
              plain
            >提交反馈</el-button>
          </el-row>
        </el-card>
      </el-col>

    </el-row>

  </div>

</template>
<script>
import Editor from "./Editor.vue";
import { apiFn, session, successMsg } from "../../assets/util.js";
import { feedbackApi } from "@/api/feedback";
const myMixin = {
  data: function() {
    return {
      feedbackApi
    };
  }
};
export default {
  name: "Feedback",
  mixins: [myMixin],
  data() {
    return {
      content: null,
      userInfo: {}
    };
  },
  components: { Editor },
  created() {},
  mounted() {
    this.getUserInfo();
  },
  methods: {
    getUserInfo() {
      let userInfo = session("userInfo");
      if (userInfo) {
        this.userInfo = userInfo;
      }
    },
    checkList() {
      this.$router.push({
        name: "feedbackList"
      });
    },
    changeContent(val) {
      this.content = val;
    },
    submitFeedback() {
      if (this.content == null || this.content == "") {
        this.$message.warning("反馈内容为空，我们将不能明白您意思");
        return;
      } else {
        this.save();
      }
    },
    save() {
      let param = {
        // username: this.userInfo.username,
        username: "admin",
        content: this.content
      };
      apiFn()(feedbackApi, "save", param, this).then(res => {
        if (res && res.successful) {
          successMsg(res.msg, this);
        }
      });
    }
  }
};
</script>
<style scoped>
.submitFeedback {
  text-align: center;
  padding-top: 10px;
}
.submitFeedback button {
  width: 100%;
}
</style>

