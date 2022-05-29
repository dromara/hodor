export const saveBreadcrumb = (data) => {
    localStorage.setItem('$routeParams', JSON.stringify(data))   
}


const spliceItem = (data,routeItem) => {
  let index = [];
  data.breadcrumbItem.forEach(function (ele, i) {
    if (routeItem.indexOf(ele.path) > -1) {
      index.push(i)
    }
  })
  if (index.length > 0) {
    index.forEach(function (el) {
      data.breadcrumbItem.splice(el, 1)
    })
  }
  return data
}
export const getBreadcrumb = (routeItem) => {
  let $routeParamsDetails = localStorage.getItem('$routeParams')
  if ($routeParamsDetails) {
    return spliceItem(JSON.parse($routeParamsDetails),routeItem)
  }
}
