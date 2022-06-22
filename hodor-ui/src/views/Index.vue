<template>
  <el-container class="home-container ">
    <el-aside class="home-aside-content">
      <el-scrollbar class="menu-scrollbar">
        <el-menu
          :default-active="defaultActive"
          :default-openeds="open_list"
          router
          ref="navMenu"
          unique-opened
          :collapse="isCollapse"
          class="el-menu-vertical-in-home"
        >
          <el-menu-item
            index='0_0'
            route='/'
            class="gohome"
          >
            <i class="el-icon-ump-home home"></i>
            <span
              slot="title"
              class="logo-section"
            >
              <span>HodorScheduler</span>
            </span>
          </el-menu-item>
          <template v-for="(route, index) in asideMenus">
            <template v-if="route.children">
              <el-submenu
                v-if='route.roleId==0?userInfo.roleId==0?true:false:true'
                :key="route.index"
                :index="route.index+''"
              >
                <template slot="title">
                  <i :class="route.icon"></i>
                  <span slot="title">{{route.name}}</span>
                </template>
                <el-menu-item
                  @click="clickMenu"
                  v-for="(cRoute, cIndex) in route.children"
                  :key="cIndex"
                  :index="route.index+'_'+cIndex"
                  :route="cRoute.router"
                >
                <a class='helpA' v-if='cRoute.router=="/help"' :href="helpAddress" target="_blank">
                  {{ cRoute.name}}
                  </a>
                  <span v-else> {{ cRoute.name}}</span>
                </el-menu-item>

              </el-submenu>
            </template>
            <template v-else>
              <el-menu-item
                @click="clickMenu"
                :route="route.router"
                :index="route.index+''"
              >
                <i :class="route.icon"></i>
                <span slot="title">{{route.name}} </span>
              </el-menu-item>
            </template>
          </template>
        </el-menu>
      </el-scrollbar>
    </el-aside>
    <el-container
      class="flex-1"
      id="content-container"
    >
      <el-header
        class="home-header flex-container"
        height="60px"
      >
        <div class="menu-controller">
          <i
            class="el-icon-ump-alignjustify cursor-p"
            @click="toggleMenu"
          ></i>
        </div>
        <div class="user-name">
          <i class="el-icon-ump-user"></i>
          {{userInfo.username}}&nbsp;&nbsp;|&nbsp;&nbsp;
          <span
            class="esearch-link"
            @click="logOut"
          >
            <i class="el-icon-ump-tuichu"></i>退出
          </span>
        </div>
      </el-header>
      <el-main id="loading-area">
        <router-view></router-view>
      </el-main>
      <el-footer>HodorScheduler v1.0.0</el-footer>
    </el-container>
  </el-container>
</template>

<script>
import "@/assets/fonts/icon/style.css";
import "@/assets/css/reset.css";
import "@/assets/sass/common.css";
import "@/assets/sass/index.css";
import * as asideMenu from "../assets/js/menu.js";
import { apiFn, session } from "../assets/util.js";
import instance from "../api";
import * as loginApi from "../api/login.js";

const myMixin = {
  data: function() {
    return {
      loginApi
    };
  }
};
export default {
  mixins: [myMixin],
  components: {
    asideMenu
  },

  data() {
    let activeIndex =
      window.location.hash == "#/home"
        ? null
        : this.$route.query.activeIndex
        ? this.$route.query.activeIndex
        : session("activeIndex");
    return {
      menus: [],
      helpAddress:'https://github.com/dromara/hodor',
      isCollapse: false,
      defaultActive: activeIndex ? activeIndex : "0_0",
      activeIndex: "",
      userInfo: {},
      userName: "",
      userButtons: [],
      asideMenus: asideMenu.menu,
      project: "",
      environment: "",
      version: "",
      userAuthList: [],
      projectList: [],
      open_list: ["4"]
    };
  },
  created() {
    this.getUserInfo();
  },
  mounted() {
    this.setMenuCollapse();
    window.addEventListener("resize", () => {
      this.setMenuCollapse();
    });
  },
  watch: {

  },
  updated() {
    if (this.$refs.navMenu) {
      session("activeIndex", this.$refs.navMenu.activeIndex);
    }
  },
  methods: {
    getUserInfo() {
      let userInfo = session("userInfo");
      if (userInfo) {
        this.userInfo = userInfo;
      }
    },
    logOut() {
      apiFn()(loginApi, "logOut", null, this).then(res => {
        this.$router.replace("/login");
        session("userInfo", "");
      });
    },
    hasSubMenu(menu) {
      return menu.subMenu && menu.subMenu.length > 0;
    },

    changeMainContent(moduleName, btns) {
      this.userButtons = btns;
      this.currentTabComponent = moduleName;
    },
    clickMenu(e) {
      localStorage.removeItem("$routeParams");
    },
    toggleMenu() {
      this.defaultActive = this.$refs.navMenu.activeIndex;
      this.isCollapse = !this.isCollapse;
    },

    setMenuCollapse() {
      let bodyWidth = document.body.getBoundingClientRect().width;
      if (this.$refs.navMenu) {
        this.defaultActive = this.$refs.navMenu.activeIndex;
      }

      this.isCollapse = bodyWidth < 960 ? true : false;
    }
  }
};
</script>





