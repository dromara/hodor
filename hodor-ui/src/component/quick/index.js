import Component from './src/index.vue'

Component.install = function (Vue) { 
  Vue.component(Component.name, Component)
}

export default Component;