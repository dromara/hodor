import config from "@/configure/app.config.js";
import { getStorage, setStorage, delStorage } from "@/tools/storage";

const state = {

  /**
    * 布局方式（整体界面的排版方式）
    * layout-side -- 侧边布局
    * layout-head -- 顶部菜单
    * layout-comp -- 联动布局
    * */
  layout: getStorage('layout') != null ? getStorage('layout') : config.layout,

  /**
   * 系统主题（整体色调）
   * light -- 白色主题
   * dark -- 暗色主题
   * night -- 夜间主题
   */
  theme: getStorage('theme') != null ? getStorage('theme') : config.theme,

  /**
   * 主题颜色(主题颜色)
   */
  color: getStorage('color') != null ? getStorage('color') : config.color,

  /**
   * 侧边状态
   * true  --  隐藏
   * false --  展开
   * */
  collapsed: getStorage('collapsed') != null ? getStorage('collapsed') : config.collapsed,

  /**
   * 菜单头部
   * true  --  隐藏
   * false --  展开
   * */
  logo: getStorage('logo') != null ? getStorage('logo') : config.logo,

  /**
   * 是否开启多标签页
   * true  --  隐藏
   * false --  展开
   * */
  tab: getStorage('tab') != null ? getStorage('tab') : config.tab,

  /**
   * 保持状态
   * true -- 是
   * false -- 否
   */
  keepAlive: getStorage('keepAlive') != null ? getStorage('keepAlive') : config.keepAlive,
  /**
   * 多标签页样式
   * pear-card-tab
   * pear-dot-tab
   */
  tabType: getStorage('tabType') != null ? getStorage('tabType') : config.tabType,

  /**
   * 侧边菜单栏宽度
   * 单位:px
   * */
  sideWitch: config.sideWidth == null ? 220 : config.sideWidth,

  /**
   * 侧边菜单栏宽度(折叠)
   * 单位:px
   * */
  collapsedWidth: config.collapsedWidth == null ? 60 : config.collapsedWidth,

  /**
   * 固定头部
   * true
   * false
   */
  fixedHeader: getStorage('fixedHeader') != null ? getStorage('fixedHeader') : config.fixedHeader,

  /**
   * 固定侧边
   * true
   * false
   */
  fixedSide: getStorage('fixedSide') != null ? getStorage('fixedSide') : config.fixedHeader,

  /**
   * 路由动画
   * fadeRight
   * fadeTop
   */
  routerAnimate: getStorage('routerAnimate') != null ? getStorage('routerAnimate') : config.routerAnimate,

  /**
   * 配色列表
   * key
   * color
   * scopeName
   */
  colorList: config.colorList,

  /**
   * 主题面板状态
   * true
   * false
   */
  setting: { opened: false },

  /**
   * 菜单手风琴
   * true
   * false
   */
  muiltOpen: true,

  /**
   * 路由刷新辅助变量
   * true
   * false
   */
  routerActive: true,

  // 路由列表
  routes: [],

  /**
   * 移动端配置
   * true
   * false
   */
  isMobile: false,

  /**
   * 国际化配置
   * zh-CN 中文
   * en-US 英文
   */
  language: getStorage('language') != null ? getStorage('language') : config.defaultLanguage,

  /**
   * 请求辅助变量
   * true
   * false
   */
  cancelToken: []
}

const mutations = {
  PUSH_CANCEL_TOKEN(state, payload) {
    state.cancelToken.push(payload.cancelToken)
  },
  EXEC_CANCEL_TOKEN(state) {
    state.cancelToken.forEach(executor => {
      executor('路由跳转取消上个页面的请求')
    })
    state.cancelToken = []
  },
  SET_LANGUAGE(state, payload) {
    state.language = payload
    setStorage('language', payload)
  },
  TOGGLE_FIXEDSIDE(state) {
    state.fixedSide = !state.fixedSide;
  },
  TOGGLE_FIXEDHEADER(state) {
    state.fixedHeader = !state.fixedHeader;
  },
  TOGGLE_LANGUAGE(state, language) {
    state.language = language;
  },
  UPDATE_LAYOUT(state, layout) {
    state.layout = layout;
    setStorage('layout', layout)
  },
  UPDATE_TAB_TYPE(state, tabType) {
    state.tabType = tabType;
    setStorage('tabType', tabType);
  },
  UPDATE_THEME(state, theme) {
    state.theme = theme;
    setStorage('theme', theme);
  },
  UPDATE_ROUTER_ANIMATE(state, routerAnimate) {
    state.routerAnimate = routerAnimate;
    setStorage('routerAnimate',routerAnimate)
  },
  UPDATE_COLOR(state, color) {
    state.color = color;
    setStorage('color', color);
  },
  UPDATE_ROUTES(state, routes) {
    state.routes = routes;
  },
  UPDATE_COLLAPSED(state, collapsed) {
    state.collapsed = collapsed;
  },
  TOGGLE_SIDEBAR(state) {
    state.collapsed = !state.collapsed;
  },
  TOGGLE_SETTING(state) {
    state.setting.opened = !state.setting.opened;
  },
  TOGGLE_SIDEWITCH(state, width) {
    state.sideWitch = width;
  },
  TOGGLE_LOGO(state) {
    state.logo = !state.logo;
    setStorage('logo', state.logo)
  },
  UPDATE_TAB(state) {
    state.tab = !state.tab;
    setStorage('tab', state.tab)
  },
  UPDATE_ROUTER_ACTIVE(state) {
    state.routerActive = !state.routerActive;
  },
  UPDATE_ISMOBILE(state, isMobile) {
    state.isMobile = isMobile;
  },
  TOGGLE_KEEP_ALIVE(state) {
    state.keepAlive = !state.keepAlive;
    setStorage('keepAlive', state.keepAlive)
  }
}

const actions = {
  execCancelToken({ commit }) {
    return new Promise(resolve => {
      commit('EXEC_CANCEL_TOKEN');
      resolve()
    })
  },
  setLanguage({ commit }, payload) {
    commit('SET_LANGUAGE', payload)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
