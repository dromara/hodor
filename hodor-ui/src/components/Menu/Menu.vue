<script setup>
import { ref } from 'vue';
import { useRouter, onBeforeRouteUpdate } from 'vue-router'
import { menu } from '@/components/Menu/menu'

const router = useRouter();

const selectedKeys = ref([router.currentRoute.value.path])

onBeforeRouteUpdate(async (to, from) => {
    // 每次路由更新之前更新selectedKeys，更换选中的menu item
    selectedKeys.value.length = 0;
    selectedKeys.value.push(to.path)
})
</script>

<template>
    <a-menu v-model:selectedKeys="selectedKeys" theme="dark" mode="inline" class="siderMenu">
        <template v-for="item in menu">
            <template v-if="item.children">
                <a-sub-menu :key=item.router>
                    <template #title>
                        <span>
                            <component :is="item.icon" />
                            <span>{{ item.text }}</span>
                        </span>
                    </template>
                    <a-menu-item v-for="subItem in item.children" :key="subItem.router">
                        <RouterLink :to=subItem.router >
                            {{ subItem.text }}
                        </RouterLink>
                    </a-menu-item>
                </a-sub-menu>
            </template>
            <template v-else>
                <a-menu-item :key=item.router>
                    <RouterLink :to=item.router>
                        <component :is="item.icon" />
                        <span>{{ item.text }}</span>
                    </RouterLink>
                </a-menu-item>
            </template>
        </template>
    </a-menu>
</template>
<style lang="scss" scoped>
.siderMenu {
    font-size: 1rem;
    :deep(.ant-menu-item){
        margin-bottom: 10px;
    }
}
</style>
