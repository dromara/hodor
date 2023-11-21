<template>
  <div id="p-table">
    <!-- 表格工具栏 -->
    <div class="p-table-tool">
      <!-- 自定义工具栏 -->
      <div class="p-table-prev">
        <template :key="index" v-for="(item, index) in toolbar">
          <!-- 更多按钮 -->
          <p-authority
            :value="item.code ? item.code : false"
            v-if="item.children && item.children.length > 0"
          >
            <a-dropdown>
              <a-button @click="item.event(selectedRowKeys)">
                {{ item.label }}
              </a-button>
              <template #overlay>
                <a-menu>
                  <!-- 遍历子集 -->
                  <p-authority
                    v-for="(child, i) in item.children"
                    :key="i"
                    :value="child.code ? item.code : false"
                  >
                    <a-menu-item>
                      <a @click="child.event(selectedRowKeys)">
                        {{ child.label }}
                      </a>
                    </a-menu-item>
                  </p-authority>
                </a-menu>
              </template>
            </a-dropdown>
          </p-authority>
          <p-authority :value="item.code ? item.code : false" v-else>
            <a-button
              :type="index == 0 ? 'primary' : 'default'"
              @click="item.event(selectedRowKeys)"
            >
              {{ item.label }}
            </a-button>
          </p-authority>
        </template>
      </div>
      <!-- 默认工具栏 -->
      <div class="p-table-next" v-if="defaultToolbar">
        <!-- 刷新工具栏 -->
        <a-button @click="reload">
          <template #icon><SyncOutlined /></template>
        </a-button>
        <!-- 过滤工具栏 -->
        <a-dropdown>
          <a-button>
            <template #icon><AppstoreOutlined /></template>
          </a-button>
          <template #overlay>
            <a-menu class="filtration">
              <a-checkbox-group
                v-model:value="filtrationColumnKeys"
                @change="filtration"
              >
                <a-row>
                  <!-- 遍历字段 -->
                  <a-col
                    :span="24"
                    :key="index"
                    v-for="(filtrationColumn, index) in filtrationColumns"
                  >
                    <a-checkbox :value="filtrationColumn.value">
                      {{ filtrationColumn.label }}
                    </a-checkbox>
                  </a-col>
                </a-row>
              </a-checkbox-group>
            </a-menu>
          </template>
        </a-dropdown>
        <!-- 过滤工具栏 -->
        <a-dropdown>
          <a-button>
            <template #icon><ColumnHeightOutlined /></template>
          </a-button>
          <template #overlay>
            <a-menu :selectedKeys="[size]">
              <a-menu-item @click="changeSize('default')" key="default"
                >默认尺寸</a-menu-item
              >
              <a-menu-item @click="changeSize('middle')" key="middle"
                >中等尺寸</a-menu-item
              >
              <a-menu-item @click="changeSize('small')" key="small"
                >最小尺寸</a-menu-item
              >
            </a-menu>
          </template>
        </a-dropdown>
        <a-button @click="print">
          <template #icon><ExportOutlined /></template>
        </a-button>
      </div>
    </div>
    <!-- 表格组件 -->
    <a-table
      rowKey="id"
      @change="fetch"
      :columns="columns"
      :loading="loading"
      :pagination="pagination"
      :dataSource="datasource"
      :row-selection="rowSelection"
      :size="size"
    >
      <!-- 列转换 -->
      <template
        :key="index"
        v-for="(column, index) in columns"
        #[column.dataIndex]="{ record }"
      >
        <!-- 行操作 -->
        <span v-if="column.dataIndex == 'operate'">
          <template :key="i" v-for="(item, i) in operate">
            <!-- 下拉操作 -->
            <a-dropdown v-if="item.children && item.children.length > 0">
              <a> {{ item.label }} </a>
              <template #overlay>
                <a-menu>
                  <!-- 遍历子集 -->
                  <p-authority
                    v-for="(child, i) in item.children"
                    :key="i"
                    :value="child.code ? item.code : false"
                  >
                    <a-menu-item>
                      <a @click="child.event(selectedRowKeys)">
                        {{ child.label }}
                      </a>
                    </a-menu-item>
                  </p-authority>
                </a-menu>
              </template>
            </a-dropdown>
            <!-- 单个操作 -->
            <p-authority :value="item.code ? item.code : false" v-else>
              <a @click="item.event(record)"> {{ item.label }} </a>
              <a-divider type="vertical" v-if="i != operate.length - 1" />
            </p-authority>
          </template>
        </span>

        <!-- 开关转换 -->
        <span v-else-if="column.switch">
          <a-switch
            @change="column.switch.event($event, record)"
            :checked="record[column.dataIndex] === column.switch.yes"
          />
        </span>

        <!-- 文本转换 -->
        <span v-else-if="column.conver">
          <template v-for="(data, index) in column.conver">
            <span :key="index" v-if="data.value === record[column.dataIndex]">
              {{ data.label }}
            </span>
          </template>
        </span>

        <!-- 头像 -->
        <span v-else-if="column.avatar">
          <!-- 空头像 -->
          <a-avatar
            v-if="record[column.dataIndex] == null"
            :size="column.avatar.size"
            :shape="column.avatar.shape"
          >
            <template #icon><UserOutlined /></template>
          </a-avatar>

          <!-- 非头像 -->
          <a-avatar
            v-else
            :src="record[column.dataIndex]"
            :size="column.avatar.size"
            :shape="column.avatar.shape"
          />
        </span>

        <!-- 预览 -->
        <span v-else-if="column.image">
          <a-image
            :width="column.image.width"
            :src="record[column.dataIndex]"
          />
        </span>

        <!-- 使用format -->
        <span v-else-if="column.format">
          {{column.format(record)}}
        </span>

        <!-- 原样输出 -->
        <span v-else-if="record">
          {{ record[column.dataIndex] }}
        </span>
      </template>

      <template v-for="name in slotsData" #[name]="{ record }">
        <slot :name="name" :record="record"></slot>
      </template>

    </a-table>
  </div>
</template>
<script>
import "./index.less";
import T from "ant-design-vue/es/table/Table";
import { defineComponent, onMounted, reactive, toRefs, watch, ref } from "vue";
import {
  AppstoreOutlined,
  ExportOutlined,
  SyncOutlined,
  UserOutlined,
  ColumnHeightOutlined,
} from "@ant-design/icons-vue";

const TProps = T.props;
export default defineComponent({
  name: "p-table",
  components: {
    ColumnHeightOutlined,
    AppstoreOutlined,
    ExportOutlined,
    SyncOutlined,
    UserOutlined,
  },
  /// 数据来源
  props: Object.assign({}, TProps, {
    /// 扩展参数
    param: {
      type: Object,
    },
    /// 数据来源
    fetch: {
      type: Function,
      required: false,
    },
    /// 数据解析
    columns: {
      type: Array,
      required: true,
    },
    /// 头工具栏
    toolbar: {
      type: Array,
    },
    defaultToolbar: {
      type: Boolean,
      default: true
    },
    /// 行工具栏
    operate: {
      type: Array || Boolean,
      default: false,
    },
    /// 分页参数
    pagination: {
      type: [Object, Boolean],
      default: false,
    },
    rowSelection: {
      type: Object,
    },
  }),
  setup(props) {

    const slotsData = ref([]);

    props.columns.map((value) => {
      if (value.slots) {
        slotsData.value.push(value.slots.customRender);
      }
    });

    /// 状态共享
    const state = reactive({
      pagination: props.pagination == false ? false : props.pagination, // 分页
      datasource: [], // 数据源
      loading: true, // 加载
      columns: props.columns, // 字段
      filtrationColumnKeys: [], // 过滤
      selectedRowKeys: [], // 选中项
      size: props.size, // 表格大小
    });

    /// 默认操作
    if (props.operate != false) {
      state.columns.push({
        dataIndex: "operate",
        key: "operate",
        title: "操作",
        fixed: "right",
      });
    }

    /// 为所有 column 新增默认 customRender 属性
    state.columns.forEach((column) => {
      if (!column?.slots?.customRender) {
        column.slots = { customRender: column.dataIndex };
      }
    });

    /// 过滤字段
    const filtrationColumns = [];
    props.columns.forEach(function (item) {
      filtrationColumns.push({ label: item.title, value: item.key });
      state.filtrationColumnKeys.push(item.key);
    });

    /// 过滤字段
    const filtration = function (value) {
      state.columns = props.columns.filter((item) => value.includes(item.key));
      state.filtrationColumnKeys = value;
    };

    /// 选中回调
    const onSelectChange = (selectedRowKeys) => {
      state.selectedRowKeys = selectedRowKeys;
    };

    /// 数据请求
    const fetchData = async (pagination) => {
      /// 分页处理
      if (pagination != undefined) {
        state.pagination.pageNum = pagination.current;
      }
      /// 开启加载
      state.loading = true;
      /// 请求数据
      const { total, data } = await props.fetch(
        Object.assign({}, state.pagination, props.param)
      );
      /// 状态重置
      if (state.pagination != false) {
        state.pagination.total = total;
      }
      state.datasource = data;
      state.loading = false;
    };

    /// 刷新方法
    const reload = function () {
      fetchData();
    };

    /// 初始数据
    onMounted(async () => {
      await fetchData();
    });

    /// 监听扩展参数, 触发表格刷新
    watch(
      () => props.param,
      () => {
        fetchData();
      },
      { deep: true }
    );

    /// 改变按钮尺寸
    const changeSize = (target) => {
      state.size = target;
    };

    /// 表格打印
    const print = function () {
      let subOutputRankPrint = document.getElementById("p-table");
      let newContent = subOutputRankPrint.innerHTML;
      let oldContent = document.body.innerHTML;
      document.body.innerHTML = newContent;
      window.print();
      window.location.reload();
      document.body.innerHTML = oldContent;
    };

    return {
      // 数据信息
      ...toRefs(state),
      // 数据加载
      fetch: fetchData,
      // 刷新方法
      reload,
      // 过滤字段
      filtrationColumns,
      filtration,
      // 选中字段
      onSelectChange,
      // 改变大小
      changeSize,
      // 打印
      print,
      // 插槽信息
      slotsData,
    };
  },
});
</script>