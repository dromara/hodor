import Vue from 'vue'
import App from './views/App'
import router from "./router";
import ECharts from 'vue-echarts/components/ECharts.vue'
import VueQuillEditor from 'vue-quill-editor/dist/ssr'



// import ECharts modules manually to reduce bundle size
// import 'echarts/lib/chart/pie';
import 'echarts/lib/chart/line';
import 'echarts/lib/chart/bar';
import 'echarts/lib/chart/pie';
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';
import 'echarts/lib/component/legend';
import 'echarts/lib/component/dataZoom'
import 'echarts/lib/component/brush'
import 'echarts/lib/component/toolbox'
import 'echarts/lib/component/legendScroll'

import {
  Aside,
  Footer,
  Button,
  ButtonGroup,
  Card,
  Checkbox,
  CheckboxGroup,
  Col,
  Container,
  DatePicker,
  Dialog,
  Form,
  FormItem,
  Header,
  Input,
  InputNumber,
  Main,
  Menu,
  MenuItem,
  MenuItemGroup,
  MessageBox,
  Message,
  Option,
  Pagination,
  Row,
  Scrollbar,
  Select,
  Submenu,
  Table,
  TableColumn,
  Tooltip,
  Breadcrumb,
  BreadcrumbItem,
  RadioGroup,
  RadioButton,
  Radio,
  Dropdown,
  DropdownItem,
  DropdownMenu,
  CheckboxButton,
  Tree,
  Autocomplete,
  Tag,
  Badge,
  Steps,
  Step,
  Tabs,
  TabPane,
  Upload,
  Switch
} from "element-ui";

import VueRouter from 'vue-router';



Vue.config.productionTip = false
Vue.component('chart', ECharts);
Vue.use(Container)
Vue.use(Badge)
Vue.use(Footer)
Vue.use(Header)
Vue.use(Main)
Vue.use(Aside)
Vue.use(Menu)
Vue.use(MenuItem)
Vue.use(Submenu)
Vue.use(Select)
Vue.use(Option)
Vue.use(Button)
Vue.use(ButtonGroup)
Vue.use(Input)
Vue.use(InputNumber)
Vue.use(Table)
Vue.use(TableColumn)
Vue.use(Tooltip)
Vue.use(Pagination)
Vue.use(Scrollbar)
Vue.use(DatePicker)
Vue.use(Checkbox)
Vue.use(CheckboxGroup)
Vue.use(Card)
Vue.use(Row)
Vue.use(Col)
Vue.use(Form)
Vue.use(Dialog)
Vue.use(FormItem)
Vue.use(MenuItemGroup)
Vue.use(Breadcrumb)
Vue.use(BreadcrumbItem)
Vue.use(RadioGroup)
Vue.use(RadioButton)
Vue.use(Radio)
Vue.use(Dropdown)
Vue.use(DropdownItem)
Vue.use(DropdownMenu)
Vue.use(CheckboxButton)
Vue.use(Tree)
Vue.use(Autocomplete)
Vue.use(Tag)
Vue.use(Tabs)
Vue.use(TabPane)
Vue.use(Steps)
Vue.use(Step)
Vue.use(Upload)
Vue.use(Switch)
Vue.prototype.$alert = MessageBox.alert
Vue.prototype.$confirm = MessageBox.confirm
Vue.prototype.$prompt = MessageBox.prompt
Vue.prototype.$message = Message


Vue.use(VueQuillEditor)


/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: {
    App
  },
  template: '<App/>'
})
