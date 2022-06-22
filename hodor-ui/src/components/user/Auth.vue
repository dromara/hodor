<template>
  <div class="user-action-container">
    <el-row v-if='activeTab==0'>
      <el-col :span="2">
        <div class="nameLabel">用户名：</div>
      </el-col>
      <el-col :span="4">
        <el-select
          v-model="username"
          filterable
          placeholder="请选择用户"
          size="small"
          class="width100"
          @change="changeName"
        >
          <el-option
            v-for="item in userList"
            :key="item"
            :label="item"
            :value="item"
          >
          </el-option>
        </el-select>
      </el-col>
      <el-col :span="2">
        <div class="nameLabel">权限：</div>
      </el-col>
      <el-col :span="6">
        <el-checkbox-group
          v-model="authtype"
          size="small"
          @change="changecheckBox"
        >
          <el-checkbox-button
            :label="item.val"
            v-for='item in authTitle'
            :key='item.val'
          >{{item.label}}</el-checkbox-button>
        </el-checkbox-group>
      </el-col>
    </el-row>
    <el-row v-if='activeTab==1'>
      <el-col :span="2">
        <div class="nameLabel">角色：</div>
      </el-col>
      <el-col :span="8">
        <el-select
          v-model="roleId"
          filterable
          placeholder="请选择角色"
          size="small"
          class="width100"
          @change='changeRole'
        >
          <el-option
            v-for="item in roleList"
            :key="item.id"
            :label="item.roleName"
            :value="item.id"
          >
          </el-option>
        </el-select>
      </el-col :span='2'>
      &nbsp;
      <el-button
        type="primary"
        size="small"
        @click="authSumbit('ztree-menu')"
      >给角色授权</el-button>
      <el-col>

      </el-col>
    </el-row>

    <br />
    <el-tabs
      type="border-card"
      @tab-click='tabClickHandle'
    >
      <el-tab-pane label="组授权">
        <el-row>
          <el-col :span='10'>
            <el-input
              placeholder="输入关键字进行过滤"
              v-model="filterGroup"
              size="small"
              v-if='false'
            >
            </el-input>
            <div
              id="ztree-group"
              class="ztree"
            ></div>
          </el-col>
        </el-row>

      </el-tab-pane>
      <el-tab-pane label="操作授权">
        <el-row>
          <el-col :span='10'>
            <el-input
              placeholder="输入关键字进行过滤"
              v-model="filterText"
              size="small"
              v-if='false'
            >
            </el-input>
            <div
              id="ztree-menu"
              class="ztree"
            ></div>
          </el-col>
        </el-row>
        <el-row>

        </el-row>

      </el-tab-pane>
    </el-tabs>

  </div>

</template>
<script>
import { successMsg, apiFn } from "@/assets/util";
import { userAuthApi } from "@/api/userAuth";
import "@/assets/css/plugins/ztree/metroStyle/zTreeStyle.css";
import "@/lib/jquery.ztree.all.min.js";
import { userRoleApi } from "@/api/userRole";

const myMixin = {
  data: function() {
    return {
      userAuthApi,
      userRoleApi
    };
  }
};
export default {
  name: "Auth",
  mixins: [myMixin],
  data() {
    let title = [
      { val: "reader", label: "读权限" },
      { val: "owner", label: "所有者权限" }
    ];
    return {
      username: "",
      authtype: [],
      roleId: "",
      permititem: "",
      filterText: "",
      filterGroup: "",
      activeTab: 0,
      userList: [],
      roleList: [],
      groupList: [],
      optionsList: [],
      defaultProps: {
        children: "children",
        label: "desciption"
      },
      groupAuthTitle: title,
      authTitle: title,
      jobAuthTitle: [{ val: "reader", label: "权限" }],
      groupTree: [],
      optionsTree: [],
      infoAuth: [],
      options: {}
    };
  },
  components: {},
  created() {
    this.init("getUserList", "userList");
    this.init("getJobgroup", "groupList");
    this.init("getPermitList", "optionsList");
    this.getRolesList();
  },
  watch: {
    filterGroup(val) {
      console.log(val);
    },
    filterText(val) {
      console.log(val);
    },
    activeTab: {
      handler(val) {
        this.authTitle = val == 0 ? this.groupAuthTitle : this.jobAuthTitle;
        this.authtype = [];
      }
    }
  },
  mounted() {},
  computed: {},
  methods: {
    getSetting(typeId, checkType) {
      let that = this;
      return {
        data: {
          simpleData: { enable: true, idKey: "id", rootPId: 0 },
          key: {
            children: "children",
            name: "desciption"
          }
        },
        view: {
          showLine: true,
          txtSelectedEnable: false
        },
        check: {
          autoCheckTrigger: false,
          check: { Y: "", N: "" },
          chkStyle: checkType,
          enable: true,
          nocheckInherit: true,
          chkDisabledInherit: true
        },
        callback: {
          //节点点击事件
          onClick: function(e, treeId, treeNode) {
            e.preventDefault();
            that.clickCallback(typeId, treeNode);
          },
          onCheck: function(e, treeId, treeNode) {
            that.checkCallback(treeId, treeNode);
          }
        }
      };
    },
    clickCallback(typeId, treeNode) {
      let ztreeObj = $.fn.zTree.getZTreeObj(typeId);
      treeNode.checked = true;
      ztreeObj.updateNode(treeNode);
      if (typeId == "ztree-group") {
        this.changeName();
      }
    },
    checkCallback(typeId, treeNode) {
      if (typeId == "ztree-group") {
        this.changeName();
      }
    },
    init(type, key) {
      apiFn()(userAuthApi, type, null, this).then(res => {
        if (Object.prototype.toString.call(res) == "[object Array]") {
          if (key == "groupList") {
            this[key] = this.reSetGroupList(res);
            this.initNodes("job组列表", this[key], "ztree-group", "radio");
          } else {
            this[key] = res;
            if (key == "optionsList") {
              this.initNodes("操作列表", this[key], "ztree-menu", "checkbox");
            }
          }
        }
      });
    },
    getRolesList() {
      apiFn()(userRoleApi, "init", null, this).then(res => {
        if (res && res.successful) {
          this.roleList = res.rows;
        }
      });
    },
    changeRole(val) {
      apiFn()(userRoleApi, "getRolePermitInfo", { roleId: val }, this).then(
        res => {
          if (res && res.successful) {
            this.setSelectNodes(res.data);
          }
        }
      );
    },
    setSelectNodes(extArr) {
      var treeObj = $.fn.zTree.getZTreeObj("ztree-menu");
      var nodes = treeObj.getNodes();
      let list = nodes[0].children;
      list.forEach(ele => {
        ele.checked = extArr.indexOf(ele.id) > -1 ? true : false;
        treeObj.updateNode(ele);
      });
    },
    initNodes(title, data, typeId, checkType) {
      let nodes = [{ desciption: title, children: [], id: 0, open: true }];
      nodes[0].children = data;
      let setting = this.getSetting(typeId, checkType);
      $.fn.zTree.init($("#" + typeId), setting, nodes);
    },
    getSelectedMenu(typeId) {
      let ztreeObj = $.fn.zTree.getZTreeObj(typeId);
      let nodes = ztreeObj.getCheckedNodes(true);
      return nodes;
    },
    reSetGroupList(data) {
      return data.map((ele, i) => {
        return { desciption: ele, id: i };
      });
    },

    getExtAuth(param) {
      let type = this.activeTab == 0 ? "getAuthInfos" : "getPermitInfo";
      apiFn()(userAuthApi, type, param, this).then(res => {
        if (Object.prototype.toString.call(res) == "[object Array]") {
          this.authtype = [];
          res[0] ? this.authtype.push("reader") : "";
          res[1] ? this.authtype.push("owner") : "";
        } else if (typeof res == "boolean") {
          res ? this.authtype.push("reader") : (this.authtype = []);
        }
        this.infoAuth = [].concat(this.authtype);
      });
    },
    setExtAuthParam() {
      if (this.activeTab == 0) {
        let selectNodeName = this.getSelectedMenu("ztree-group");
        if (this.username && selectNodeName && selectNodeName.length > 0) {
          return {
            jobgroup: selectNodeName[0].desciption,
            username: this.username
          };
        }
      }
    },
    changeName(val) {
      let param = this.setExtAuthParam();
      if (param) {
        this.getExtAuth(param);
      }
    },
    setAuthApi(val, flag) {
      let param = { username: this.username };
      let type = "";
      let msg = "";

      if (this.activeTab == 0) {
        let selectNodeName = this.getSelectedMenu("ztree-group");
        if (selectNodeName && selectNodeName.length > 0) {
          param.jobgroup = selectNodeName[0].desciption;
          param.authtype = val;
          type = flag ? "groupUnauth" : "saveGroupAuth";
          msg = flag ? "权限解除成功" : "授权成功";
          this.infoAuth = [].concat(this.authtype);
          this.sendAuthApi(type, param, msg);
        }
      }
    },
    sendAuthApi(type, param, msg) {
      apiFn()(userAuthApi, type, param, this).then(res => {
        if (res && res.successful) {
          successMsg(msg, this);
        }
      });
    },
    changecheckBox(val) {
      let authType = null;
      if (val.length > this.infoAuth.length) {
        authType = val.length > 0 ? val[val.length - 1] : null;
        this.setAuthApi(authType);
      } else {
        if (this.activeTab == 0) {
          let i = this.infoAuth.indexOf(val[0]);
          authType = val.length>0?this.infoAuth[i > 0 ? 0 : 1]:this.infoAuth[0];
          this.setAuthApi(authType, true);
        }
      }
    },

    tabClickHandle(e) {
      this.activeTab = e.paneName;
    },
    // 给角色授权
    authSumbit(typeId) {
      let selectNodes = this.getSelectedMenu(typeId);
      if (this.roleId!==0&&(this.roleId == null || this.roleId == "")) {
        this.$message.warning("请选择角色");
        return;
      }
      let param = {
        roleId: this.roleId,
        items: JSON.stringify(this.setSubmitIds(selectNodes))
      };
      apiFn()(userAuthApi, "updateOrSaveCompetence", param, this).then(res => {
        if (res && res.successful) {
          successMsg(res.msg, this);
        }
      });
    },
    setSubmitIds(nodes) {
      if (nodes[0].id === 0) {
        nodes.shift();
      }

      if (nodes.length > 0) {
        return nodes.map(ele => {
          return ele.id;
        });
      } else {
        return [];
      }
    }
  }
};
</script>
<style scoped>
.user-action-container .addBtn {
  text-align: center;
}
.user-action-container .padding-left {
  padding-left: 20px;
}
.nameLabel {
  text-align: center;
  line-height: 31px;
}
</style>

