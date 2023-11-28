<template>
  <li>
    <a
      href="javascript:;"
      style="border-radius: 4px"
      class="clearfix full-opacity-hover"
      ><div>
        <span
          style="
            display: block;
            width: 20%;
            float: left;
            height: 12px;
            border-top-left-radius: 4px;
          "
          :style="{'background':color[0]}"
        ></span
        ><span
          style="
            display: block;
            width: 80%;
            float: left;
            height: 12px;
            border-top-right-radius: 4px;
          "
          :style="{'background':color[1]}"
        ></span>
      </div>
      <div>
        <span
          style="
            display: block;
            width: 20%;
            float: left;
            height: 34px;
            border-bottom-left-radius: 4px;
          "
          :style="{'background':color[2]}"
        ></span
        ><span
          style="
            display: block;
            width: 80%;
            float: left;
            height: 34px;
            border-bottom-right-radius: 4px;
          "
          :style="{'background':color[3]}"
        ></span></div
    ></a>
  </li>
</template>
<script>
import "./index.less";
export default {
  name: "p-demo",
  props: {
    color: Array,
    default: ['#2e3549','white','#2e3549','#f4f5f7']
  }
};
</script>