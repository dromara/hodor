<script setup>
import { onMounted } from 'vue';
import { Graph } from '@antv/x6';
import { register, getTeleport } from '@antv/x6-vue-shape'
import Node from './Node.vue';

const data = {
    // 节点
    nodes: [
        {
            id: 'node1', // String，可选，节点的唯一标识
            x: 200,       // Number，必选，节点位置的 x 值
            y: 100,       // Number，必选，节点位置的 y 值
            // width: 80,   // Number，可选，节点大小的 width 值
            // height: 40,  // Number，可选，节点大小的 height 值
            label: 'hello', // String，节点标签
            data:{
                name:'node1',
                status:'running',
            }
        },
        {
            id: 'node2', // String，节点的唯一标识
            x: 50,      // Number，必选，节点位置的 x 值
            y: 300,      // Number，必选，节点位置的 y 值
            // width: 80,   // Number，可选，节点大小的 width 值
            // height: 40,  // Number，可选，节点大小的 height 值
            label: 'world', // String，节点标签
        },
        {
            id: 'node3', // String，节点的唯一标识
            x: 400,      // Number，必选，节点位置的 x 值
            y: 300,      // Number，必选，节点位置的 y 值
            // width: 80,   // Number，可选，节点大小的 width 值
            // height: 40,  // Number，可选，节点大小的 height 值
            label: 'world', // String，节点标签
        },
    ],
    // 边
    edges: [
        {
            source: 'node1', // String，必须，起始节点 id
            target: 'node2', // String，必须，目标节点 id
        },
        {
            source: 'node1', // String，必须，起始节点 id
            target: 'node3', // String，必须，目标节点 id
        },
    ],
};

register({
    shape: 'custom-vue-node',
    width: 300,
    height: 150,
    component: Node,
})
const TeleportContainer = getTeleport()



onMounted(() => {
    const container = document.getElementById('container');
    const graph = new Graph({
        container: container,
        width: '100%',
        // height: 600,
        grid: {
            size: 10,      // 网格大小 10px
            visible: true, // 渲染网格背景
        },
        panning: true,//拖拽画布
        mousewheel: {   //画布缩放
            enabled: true,
            modifiers: ['ctrl', 'meta'],
        },
        // connecting: {   //设置连接线锚点
        //     sourceAnchor: { //起始节点
        //         name: 'bottom', // 锚点会在节点右侧中心往上偏移 10px
        //         // args: {
        //         //     dy: -10,
        //         // },
        //     },
        // }
    });
    // graph.fromJSON(data)
    data.nodes.map((node) => {
        graph.addNode(
            Object.assign(node, {
                shape: 'custom-vue-node',
            })
        )
    })
    data.edges.map((e) => {
        graph.addEdge(e)
    })
})
</script>

<template>
    <div id="container"></div>
    <TeleportContainer />
</template>

<style scoped>
#container{
    height: 100vh;
}
</style>
