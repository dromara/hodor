import user from "@/store/modules/user.js"
import router from '@/route/index'

export default function permission(el, binding, VNode, prevNode) {
  const { currentRoute } = router
  const userInfo = user.state.userInfo
  const { permissions = [] } = userInfo
  let currentPageId = ''
  let currentPageActions = []
  for (let i = 0; i < permissions.length; i++) {
    const { pageId, actions } = permissions[i]
    if (currentRoute.value.name === pageId) {
      currentPageId = pageId
      currentPageActions = actions
      break
    }
  }
  const { arg } = binding
  if (currentPageId) {
    if (!currentPageActions.includes(arg)) {
      el.style.display = 'none'
    }
  }
}
