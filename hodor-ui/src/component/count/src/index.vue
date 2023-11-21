<template>
  <span class="p-count">{{printVal}}</span>
</template>

<script>
import "./index.less";
import { computed, ref, onMounted, nextTick } from "vue";
export default{
  name: "p-count",
  props: {
    startVal: {
      type: [String, Number],
      default: "",
    },
    endVal: {
      type: [String, Number],
      default: "",
    },
    speed: {
      type: [String, Number],
      default: 20,
    },
    decimals: {
      type: [String, Number],
      default: 0,
    },
    isReverse: {
      type: [String, Boolean],
      default: false,
    },
  },
  setup(props) {
    
    const start = ref(+props.startVal);
    const end = ref(+props.endVal);
    const formatSpeed = ref(+props.speed || 5);

    const formatDecimals = computed(function () {
      let formatDecimals = props.decimals > 0 ? props.decimals : 0;
      return formatDecimals;
    });
    const decimalsLen = computed(function () {
      let decimalsLen = Math.pow(10, formatDecimals.value);
      return decimalsLen;
    });
    const printVal = computed(function () {
      let starts = (
        parseInt(start.value * decimalsLen.value) / decimalsLen.value
      ).toFixed(formatDecimals.value);
      return starts;
    });

    const accumulativeMachine = function () {
      setTimeout(() => {
        if (props.isReverse) {
          let decimalss =
            formatDecimals.value === 0 ? 0 : 1 / decimalsLen.value;
          let formatSpeeds = formatSpeed.value / decimalsLen.value + decimalss;
          start.value = start.value - formatSpeeds;
          if (printVal.value <= end.value) {
            start.value = end.value;
            return;
          }
        } else {
          let decimalss =
            formatDecimals.value === 0 ? 0 : 1 / decimalsLen.value;
          let formatSpeeds = formatSpeed.value / decimalsLen.value + decimalss;
          start.value = start.value + formatSpeeds;
          if (printVal.value >= end.value) {
            start.value = end.value;
            return;
          }
        }
        accumulativeMachine();
      }, 8);
    };

    onMounted(function () {
      nextTick(() => {
        accumulativeMachine();
      });
    });

    return {
      accumulativeMachine,
      formatDecimals,
      decimalsLen,
      formatSpeed,
      printVal,
      start,
      end,
    };
  },
};
</script>