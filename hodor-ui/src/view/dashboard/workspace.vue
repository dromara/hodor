<template>
  <div>
    <a-card style="border-radius: 0px !important">
      <a-avatar
        :size="64"
        style="float: left"
        src="https://portrait.gitee.com/uploads/avatars/user/2813/8441097_shaynas_1610801433.png!avatar200"
      >
      </a-avatar>
      <div style="float: left; margin-left: 20px">
        <div style="font-size: 18px; margin-top: 2px">就 眠 仪 式</div>
        <div style="margin-top: 9px">
          全 栈 工 程 师 - 蚂 蚁 金 服 - 事 业 群 - 前 端 平 台
        </div>
      </div>
      <div style="float: right; width: 380px">
        <a-row :gutter="16">
          <a-col :span="8">
            <a-statistic title="项目" :value="240"> </a-statistic>
          </a-col>
          <a-col :span="8">
            <a-statistic title="排名" :value="93"></a-statistic>
          </a-col>
          <a-col :span="8">
            <a-statistic title="团队" :value="28"> </a-statistic>
          </a-col>
        </a-row>
      </div>
    </a-card>
    <page-layout>
      <a-row :gutter="[10, 10]">
        <a-col :xs="24" :lg="18" :xxl="18">
          <a-card title="项目">
            <a-card-grid class="card" :hoverable="false">
              <a-avatar
                src="https://gw.alipayobjects.com/zos/rmsportal/WdGqmHpayyMjiEhcKoVE.png"
              ></a-avatar>
              <span class="card-title">Alipay</span>
              <div class="card-describe">
                那是一种内在的东西， 他们到达不了，也无法触及的
              </div>
              <div class="card-bottom">
                <div class="group">科学搬砖组</div>
                <div class="date">10月12日</div>
              </div>
            </a-card-grid>
            <a-card-grid class="card" :hoverable="false">
              <a-avatar
                src="https://gw.alipayobjects.com/zos/rmsportal/zOsKZmFRdUtvpqCImOVY.png"
              ></a-avatar>
              <span class="card-title">Angular</span>
              <div class="card-describe">
                希望是一个好东西，也许是最好的，好东西是不会消亡的
              </div>
              <div class="card-bottom">
                <div class="group">科学搬砖组</div>
                <div class="date">10月12日</div>
              </div>
            </a-card-grid>
            <a-card-grid class="card" :hoverable="false">
              <a-avatar
                src="https://gw.alipayobjects.com/zos/rmsportal/siCrBXXhmvTQGWPNLBow.png"
              ></a-avatar>
              <span class="card-title">Bootstrap</span>
              <div class="card-describe">
                城镇中有那么多的酒馆，她却偏偏走进了我的酒馆
              </div>
              <div class="card-bottom">
                <div class="group">科学搬砖组</div>
                <div class="date">10月12日</div>
              </div>
            </a-card-grid>
            <a-card-grid class="card" :hoverable="false">
              <a-avatar
                src="https://gw.alipayobjects.com/zos/rmsportal/dURIMkkrRFpPgTuzkwnB.png"
              ></a-avatar>
              <span class="card-title">Ant Design</span>
              <div class="card-describe">
                那时候我只会想自己想要什么，从不想自己拥有什么
              </div>
              <div class="card-bottom">
                <div class="group">科学搬砖组</div>
                <div class="date">10月12日</div>
              </div>
            </a-card-grid>
            <a-card-grid class="card" :hoverable="false">
              <a-avatar src="https://www.runoob.com/wp-content/uploads/2016/02/react.png"
              ></a-avatar>
              <span class="card-title">React</span>
              <div class="card-describe">
                没有锚的船当然也可以航行，只是紧张充满你的一生。
              </div>
              <div class="card-bottom">
                <div class="group">科学搬砖组</div>
                <div class="date">10月12日</div>
              </div>
            </a-card-grid>
            <a-card-grid class="card" :hoverable="false">
              <a-avatar
                src="https://gw.alipayobjects.com/zos/rmsportal/ComBAopevLwENQdKWiIn.png"
              ></a-avatar>
              <span class="card-title">Vue</span>
              <div class="card-describe">
                生命就像一盒巧克力，结果往往出人意料
              </div>
              <div class="card-bottom">
                <div class="group">科学搬砖组</div>
                <div class="date">10月12日</div>
              </div>
            </a-card-grid>
          </a-card>
          <a-card title="动态" style="margin-top: 10px">
            <a-list item-layout="horizontal" :data-source="data">
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-list-item-meta :description="item.info">
                    <template #title>
                      <a href="https://www.antdv.com/">{{ item.title }}</a>
                    </template>
                    <template #avatar>
                      <a-avatar :src="item.avatar" />
                    </template>
                  </a-list-item-meta>
                </a-list-item>
              </template>
            </a-list>
          </a-card>
        </a-col>
        <a-col :xs="24" :lg="6" :xxl="6">
          <a-card title="快捷">
            <a-row :gutter="[10,10]">
              <a-col :span="8">
                <quick
                  color="rgb(255, 214, 102)"
                  icon="NotificationFilled"
                  title="消息"
                >
                </quick>
              </a-col>
              <a-col :span="8">
                <quick
                  color="rgb(179, 127, 235)"
                  icon="MailFilled"
                  title="邮件"
                >
                </quick>
              </a-col>
              <a-col :span="8">
                <quick
                  color="rgb(255, 156, 110)"
                  icon="CustomerServiceFilled"
                  title="访问"
                >
                </quick>
              </a-col>
              <a-col :span="8">
                <quick
                  color="rgb(105, 192, 255)"
                  icon="BuildFilled"
                  title="用户"
                >
                </quick>
              </a-col>
              <a-col :span="8">
                <quick
                  color="rgb(255, 133, 192)"
                  icon="TrophyFilled"
                  title="排名"
                >
                </quick>
              </a-col>
              <a-col :span="8">
                <quick
                  color="rgb(149, 222, 100)"
                  icon="CloudFilled"
                  title="首页"
                >
                </quick>
              </a-col>
            </a-row>
          </a-card>
          <a-card title="欢迎" style="margin-top: 10px">
            <a-result title="Hello Word!">
              <template #icon>
                <smile-twoTone />
              </template>
              <template #extra>
                <a-button type="primary">Next</a-button>
              </template>
            </a-result>
          </a-card>
        </a-col>
      </a-row>
    </page-layout>
    <page-footer></page-footer>
  </div>
</template>
<script>
import { SmileTwoTone } from "@ant-design/icons-vue";
export default {
  components: {
    SmileTwoTone,
  },
  setup() {
    const data = [
      {
        avatar:
          "https://portrait.gitee.com/uploads/avatars/user/2813/8441097_shaynas_1610801433.png!avatar200",
        title: "就眠仪式",
        info: "城镇中有那么多的酒馆，她却偏偏走进了我的酒馆。",
      },
      {
        avatar:
          "https://portrait.gitee.com/uploads/avatars/user/2813/8441097_shaynas_1610801433.png!avatar200",
        title: "夏娜",
        info:
          "我跟自己和解，终究是做不会冷漠绝情的人类。本质就像感性动物的我，所以注定是要心碎不止一万次的。",
      },
      {
        avatar:
          "https://portrait.gitee.com/uploads/avatars/user/2813/8441097_shaynas_1610801433.png!avatar200",
        title: "风筝",
        info: "生命就像一盒巧克力，结果往往出人意料。",
      },
      {
        avatar:
          "https://portrait.gitee.com/uploads/avatars/user/2813/8441097_shaynas_1610801433.png!avatar200",
        title: "马克思",
        info: "没有锚的船当然也可以航行，只是紧张充满你的一生。",
      },
    ];

    return {
      data,
    };
  },
};
</script>
<style lang="less" scoped>
.ant-list-item-meta-description {
  line-height: 28px;
}
.card {
  width: 33.33%;
  height: 170px;
  line-height: 28px;
  border-top: none;
  border-left: none;
}
.card-title {
  margin-left: 15px;
  font-weight: 600;
}
.card-describe {
  margin-top: 10px;
  height: 60px;
}
.card-bottom {
  margin-top: 5px;
  .group {
    float: left;
    font-size: 13.5px;
  }
  .date {
    float: right;
    font-size: 13.5px;
  }
}
</style>
