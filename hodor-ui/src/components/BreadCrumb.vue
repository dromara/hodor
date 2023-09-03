<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { menu } from '@/components/Menu/menu'

const router = useRouter()

const breadRoutes = ref([{
    path: '/home',
    breadcrumbName: '主页',
}])
function findElementRecursive(array, target) {
    for (let item of array) {
        if (item.router === target) {
            return item.text;
        } else if (Array.isArray(item.children)) {
            return findElementRecursive(item.children, target)
        }
    }
    return undefined;
}
router.beforeEach((to, from, next) => {
    const toPath = to.path;
    if (toPath === menu[0].router) {
        if (breadRoutes.value.length > 1) breadRoutes.value.pop();
    }
    else {
        const hasCurPath = breadRoutes.value.find(item => item.path === toPath)
        if (!hasCurPath) {
            const toMenuItem = findElementRecursive(menu, toPath)
            if (toMenuItem) {
                if (breadRoutes.value.length > 1) breadRoutes.value.pop();
                breadRoutes.value.push({
                    path: toPath,
                    breadcrumbName: toMenuItem,
                })
            }
        }
    }
    next();
})
</script>

<template>
    <a-breadcrumb :routes="breadRoutes" class="bread">
        <template #itemRender="{ route, params, routes, paths }">
            <span v-if="routes.indexOf(route) === routes.length - 1">
                {{ route.breadcrumbName }}
            </span>
            <RouterLink v-else :to=route.path>
                <span class="title">{{ route.breadcrumbName }}</span>
            </RouterLink>
        </template>
    </a-breadcrumb>
</template>

<style scoped lang="less">
.bread {
    padding: 10px;
    :deep(.ant-breadcrumb-separator){
        color: #cdcdcd;
    }
    span {
        color: #cdcdcdb9;
    }
    .title{
        color: white;
    }
}
</style>
