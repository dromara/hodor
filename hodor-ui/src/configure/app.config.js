/*
 * 项目默认配置信息
 *
 * Store 初始化时决定使用 localstore 本地 / pear.js 默认配置
 */
export default {

  /**
   * 参数 : 网站图标
   */
  image: "@/assets/image/logo.png",

  /**
   * 参数 : 网站名称
   * */
  title: "Hodor Admin",

  /**
   * 参数 : 默认使用的布局
   * layout-side 侧边模式
   * layout-head 顶部模式
   * layout-comp 综合模式
   * */
  layout: "layout-side",

  /**
   * 参数 : 默认的主题
   * theme-default
   * theme-light
   * theme-night
   * */
  theme: "theme-dark",

  /**
   * 参数 : 当前菜单的状态
   * true
   * false
   * */
  collapsed: false,

  /**
   * 参数：是否显示菜单头
   * true
   * false
   */
  logo: true,

  /**
   * 参数: 是否开启多标签页
   * true
   * false
   * */
  tab: true,

  /**
   * 参数: 侧边菜单栏宽度
   * 单位: px
   * */
  sideWidth: 240,
  /**
   * 参数: 侧边菜单栏宽度(折叠)
   * 单位: px
   * */
  collapsedWidth: 70,

  /**
   * 参数: 是否固定侧边
   * true
   * false
   * */
  fixedSide: true,

  /**
   * 参数：是否开启全局 keep - Alive 状态缓存
   * true
   * false
   * */
  keepAlive: true,

  /**
   * 参数: 是否固定顶部
   * true
   * false
   */
  fixedHeader: true,

  /**
   * 参数: 选项卡样式
   * pear-card-tab
   * pear-dot-tab
   */
  tabType: "pear-tab-1",

  /**
   * 参数: 主题颜色
   * color - list
   */
  color: { scopeName: "theme-blue", color: '#2d8cf0' },

  /**
   * 参数: 可选的主题颜色列表
   * color - key
   * color - scopeName
   * color - color
   */
  colorList: [
    { scopeName: "theme-blue", color: '#2d8cf0' },
    { scopeName: "theme-green", color: '#36b368' },
    { scopeName: "theme-yellow", color: '#f6ad55' },
    { scopeName: "theme-red", color: '#f56c6c' },
    { scopeName: "theme-purple", color: '#3963bc' }],

  /**
   * 参数: 路由动画
   * fade-right
   * fade-top
   */
  routerAnimate: "fade-top",

  /**
   * 参数：国际化默认语言
   *
   * zh-CN 中文
   * en-US 英文
   */
  defaultLanguage: 'zh-CN'

};
