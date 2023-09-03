<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router'
import { menu } from '@/components/Menu/menu'

const router = useRouter()

// tabs
const panes = ref([
    {
        title: '主页',
        key: '/home',
        closable: false,
    }
]);
const activeKey = ref(router.currentRoute.value.path);
const add = () => {
    activeKey.value = router.currentRoute.value.path;
    const menuItem = menu.find(item => {
        if (item.children) {
            const res = item.children.find(subItem => {
                return subItem.router === activeKey.value
            })
            return res;
        }
        else {
            return item.router == activeKey.value;
        }
    });
    panes.value.push({
        title: (menuItem.children && menuItem.children.find(item => item.router === activeKey.value).text) || menuItem.text,
        key: activeKey.value,
    });
};

router.afterEach(async (to, from) => {
    const toPage = to.path;
    // 在tabPanes中寻找，如果已有标签页则显示相应标签页，如果没有则新建标签页
    const res = panes.value.find(item => item.key === toPage)
    if (res) {
        activeKey.value = toPage;
    }
    else {
        add();
    }
})
const remove = targetKey => {
    let lastIndex = 0;
    panes.value.forEach((pane, i) => {
        if (pane.key === targetKey) {
            lastIndex = i - 1;
        }
    });
    panes.value = panes.value.filter(pane => pane.key !== targetKey);
    if (panes.value.length && activeKey.value === targetKey) {
        if (lastIndex >= 0) {
            activeKey.value = panes.value[lastIndex].key;
        } else {
            activeKey.value = panes.value[0].key;
        }
    }
    router.push(activeKey.value)
};
const onEdit = targetKey => {
    remove(targetKey);
};
const switchTabs = () => {
    // 切换标签页时切换路由
    router.push(activeKey.value)
}
</script>

<template>
    <a-tabs v-model:activeKey="activeKey" hide-add type="editable-card"
        @edit="onEdit" @change="switchTabs" @tabClick="switchTabs">
        <a-tab-pane v-for="(pane, index) in panes" :key="pane.key" :tab="pane.title" :closable="pane.closable">
        </a-tab-pane>
    </a-tabs>
</template>
