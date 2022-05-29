export const crossOptions = function (colors) {
  return {
    color: colors,
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },


    legend: {
      data: []
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [{
      type: 'category',
      min: 'dataMin',
      max: 'dataMax',
      axisTick: {
        alignWithLabel: true
      },
      splitLine: {
        show: true
      },
      axisLine: {
        onZero: false,
        lineStyle: {
          color: colors[0]
        }
      },
    }],
    yAxis: [{
      type: 'value'
    }],
    series: [],
    animationDuration: 1000
  }

}
