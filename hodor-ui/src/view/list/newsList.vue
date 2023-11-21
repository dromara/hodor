<template>
  <div>
    <page-header
      title="图文列表"
      describe="以卡片的形式展现商品或图片信息"
    ></page-header>
    <page-layout>
      <a-card>
        <a-list
          item-layout="vertical"
          size="large"
          :pagination="pagination"
          :data-source="listData"
        >
          <template v-slot:footer>
            <div><b>ant design vue</b> footer part</div>
          </template>
          <template v-slot:renderItem="{ item }">
            <a-list-item key="item.title">
              <template v-slot:actions>
                <span v-for="{ type, text } in actions" :key="type">
                  <component v-bind:is="type" style="margin-right: 8px" />
                  {{ text }}
                </span>
              </template>
              <template v-slot:extra>
                <img
                  width="272"
                  alt="logo"
                  src="https://gw.alipayobjects.com/zos/rmsportal/mqaQswcyDLcXyDKnZfES.png"
                />
              </template>
              <a-list-item-meta :description="item.description">
                <template v-slot:title>
                  <a :href="item.href">{{ item.title }}</a>
                </template>
                <template v-slot:avatar
                  ><a-avatar :src="item.avatar"
                /></template>
              </a-list-item-meta>
              {{ item.content }}
            </a-list-item>
          </template>
        </a-list>
      </a-card>
    </page-layout>
    <page-footer></page-footer>
  </div>
</template>
<script>
import {
  StarOutlined,
  LikeOutlined,
  MessageOutlined,
} from "@ant-design/icons-vue";
const listData = [];
for (let i = 0; i < 23; i++) {
  listData.push({
    href: "https://www.antdv.com/",
    title: `ant design vue part ${i}`,
    avatar: "https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png",
    description:
      "Ant Design, a design language for background applications, is refined by Ant UED Team.",
    content:
      "We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.",
  });
}

export default {
  components: {
    StarOutlined,
    LikeOutlined,
    MessageOutlined,
  },
  data() {
    return {
      listData,
      pagination: {
        onChange: (page) => {
          console.log(page);
        },
        pageSize: 3,
      },
      actions: [
        { type: "StarOutlined", text: "156" },
        { type: "LikeOutlined", text: "156" },
        { type: "MessageOutlined", text: "2" },
      ],
    };
  },
};
</script>
<style></style>