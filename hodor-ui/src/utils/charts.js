import {
  graphic
} from 'echarts/lib/export'
import {
  deepcopy,
  dateFormat
} from '@/assets/util'
import {
  crossOptions
} from './cross'
import {
  pieOptions
} from './pie'
import {
  barOptions
} from './bar'


var colors = [
  '#B5C334','#C1232B', '#FCCE10', '#E87C25', '#27727B',
  '#FE8463', '#9BCA63', '#FAD860', '#F3A43B', '#60C0DD',
  '#D7504B', '#C6E579', '#F4E001', '#F0805A', '#26C0C0'
];







let dataZoom = (param) => {
  let data = [{
      type: 'slider',
      show: true,
      xAxisIndex: [0],
      start: 0,
      end: 100,
      handleSize: 8
    },
    {
      type: 'inside',
      start: 0,
      end: 100
    },
    {
      type: 'slider',
      show: true,
      yAxisIndex: 0,
      filterMode: 'empty',
      width: 12,
      height: '70%',
      handleSize: 8,
      showDataShadow: false,
      left: '93%'
    }
  ]
  if (param > 0) {
    data[0].xAxisIndex = [0, 1]
  }
  return data
}


let createX = function () {
  let obj = {
    type: 'category',
    axisTick: {
      alignWithLabel: true
    },
    axisLine: {
      onZero: false,
      lineStyle: {
        color: colors[1]
      }
    },
    axisPointer: {
      label: {
        formatter(params) {
          return (
            '对比  ' +
            params.value +
            (params.seriesData.length ?
              '：' + params.seriesData[0].data :
              '')
          )
        }
      }
    },
  }
  return obj
}
let createY = function () {
  let obj = {
    type: 'category',
    axisLine: {
      onZero: false,
      lineStyle: {
        color: colors[1]
      }
    },

  }
  return obj
}
let creatSeriesObj = function (data, title, type, chartType,colorFul,barWidth) {
  return {
    name: title,
    type: chartType == 'barLine' ? 'bar' : chartType,
    xAxisIndex: type ? type : 0,
    smooth: true,
    data: data,
    symbolSize: 5,
    symbol: "roundRect",
    itemStyle:chartType=='bar'? colorFul?{
      normal: {
        color: function (params) {
          return colors[params.dataIndex]
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{b}\n{c}'
        }
      }
    }:{}:{},
    stack: chartType == 'line' ? null : setStack(),
    barWidth:barWidth?barWidth:''
  
  }
}

let createPieSeries = function (data, title) {
  return {
    name: title,
    type: 'pie',
    radius: '60%',
    center: ['50%', '60%'],
    data: data,
    itemStyle: {
      emphasis: {
        shadowBlur: 10,
        shadowOffsetX: 0,
        shadowColor: 'rgba(0, 0, 0, 0.5)'
      }
    }
  }
}

let setStack = function () {
  return 'total'
}





let toggleChartTyle = function (type, colors) {
  var opt = {}
  switch (type) {
    case 'line':
      opt = crossOptions(colors)
      break;
    case 'bar':
      opt = barOptions(colors)
      break;
    case 'pie':
      opt = pieOptions()
      break;
    default:
      opt = crossOptions(colors)
  }
  return opt;
}


export const initOptions = (data, chartType, title, legendData, chartsTitle,titlePosition,colorFul,barWidth) => {
  var baseOptions = toggleChartTyle(chartType, colors)
  let options = deepcopy(baseOptions);
  options.series = [];
  options.title = {
    text: chartsTitle.title,
    subtext: chartsTitle.subtext,
    x: titlePosition?titlePosition:'center'
  }
  if (chartType == 'pie') {
    options.legend.data = legendData
    options.legend.selected = legendData
    options.series.push(createPieSeries(data, title))
  } else if (chartType == 'bar') {
    options.legend.data = legendData
    options.xAxis[0].data = data.labels;
    options.series.push(creatSeriesObj(data.values, title, null, chartType,colorFul,barWidth))
  }else if(chartType == 'line'){
    options.legend.data = title
    options.xAxis[0].data = legendData;
    for(let key in data){
      options.series.push(creatSeriesObj(data[key].data, data[key].name, null, chartType))
    }
  }


  return options;
}
