<template>
    <a-row>
        <a-table :columns="jobAlarmColumns" :data-source="jobAlarm" bordered style="width: 100%;">
        </a-table>
    </a-row>
</template>

<script>
import { defineComponent, reactive, ref } from 'vue';
export default defineComponent({
    setup() {
        // 表格
        const jobAlarmColumns = reactive([{
            title: '任务组',
            dataIndex: 'groupName',
            key: 'groupName',
        },
        {
            title: '创建时间',
            dataIndex: 'createdAt',
            key: 'createdAt',
        },
        {
            title: '最近修改时间',
            dataIndex: 'updatedAt',
            key: 'updatedAt',
        },
        {
            title: '是否开启报警',
            dataIndex: 'onOff',
            key: 'onOff',
        },
        {
            title: '报警接收人列表',
            dataIndex: 'alarmRtx',
            key: 'alarmRtx',
        },
        ]);
        const jobAlarm = ref();

        return {
            // 表格
            jobAlarmColumns,
            jobAlarm,
        };
    },
});
</script>