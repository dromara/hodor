import { module } from '@/tools/module'
import antLocal from "ant-design-vue/es/locale/zh_CN";

const modulesFiles = import.meta.globEager("./zh-CN/*.js")

const zhCN = module(modulesFiles)

export default {
  ...zhCN,
  antLocal
}
