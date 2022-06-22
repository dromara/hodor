export const pieOptions = function () {
  return {
    color: [
      '#42b983', '#C1232B', '#3c8dbc', '#E87C25'
    ],
    tooltip: {
      trigger: 'item',
      formatter: "{a} <br/>{b} : {c} ({d}%)"
    },
    toolbox: {
      show: true,
      feature: {
        mark: {
          show: true
        },
        dataView: {
          show: true,
          readOnly: false
        },
        magicType: {
          show: true,
          type: ['pie', 'funnel'],
          option: {
            funnel: {
              x: '25%',
              width: '50%',
              funnelAlign: 'left',
              max: 1548
            }
          }
        },
        restore: {
          show: true
        },
        saveAsImage: {
          show: true
        }
      }
    },

    legend: {
      type: 'scroll',
      orient: 'vertical',
      left: 10,
      top: 20,
      bottom: 20,
      data: [],
      selected: []
    },


    series: [],
    animationDuration: 1000
  }

}
