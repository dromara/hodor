/**
 * 菜单结构
 * 
 * @param list 菜单集合 
 */
export const toTree = list => {
    list.sort((a, b) => {
        return a.sort - b.sort
    })
    let node
    const map = {}
    const tree = []
    for (let i = 0; i < list.length; i++) {
        map[list[i].id] = i;
    }
    for (let i = 0; i < list.length; i++) {
        node = list[i];
        if (node.parent != "0") {
            const children = list[map[node.parent]].children || []
            list[map[node.parent]].children = [...children, node]
        } else {
            tree.push(node)
        }
    }
    return tree
}

/**
 * 路由是否注册 
 * 
 * @param routes 路由集合
 * @param currentPath 当前路由
 */
export const hasRoute = (routes, currentPath) => {
    let boolean = false
    for (let i = 0; i < routes.length; i++) {
        const { path, children = [] } = routes[i]
        if (path === currentPath) {
            boolean = true
        } else if (children.length > 0) {
            boolean = hasRoute(children, currentPath)
        }
        if (boolean) {
            break
        }
    }
    return boolean;
}