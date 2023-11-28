import { module } from '@/tools/module'
import antLocal from "ant-design-vue/es/locale/en_US";

const modulesFiles = import.meta.globEager("./en-US/*.js")

const enUS = module(modulesFiles)

export default {
  ...enUS,
  antLocal
}
