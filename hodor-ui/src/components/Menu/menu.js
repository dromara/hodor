import {
    HomeOutlined,
    BlockOutlined,
    BuildOutlined,
    ProfileOutlined,
    ApartmentOutlined,
} from '@ant-design/icons-vue';

export const menu = [
    {
        router: "/home",
        text: "HodorScheduler",
        icon: HomeOutlined,
    },
    {
        router: "/scheduler",
        text: "调度节点管理",
        icon: BlockOutlined,
    },
    {
        router: "/actuator",
        text: "执行节点管理",
        icon: BuildOutlined,
    },
    {
        router: "/job-group",
        text: "任务组管理",
        icon: ProfileOutlined,
    },
    {
        text: "任务管理",
        icon: ProfileOutlined,
        children: [
            {
                router: "/job-info",
                text: "任务信息管理",
            },
            {
                router: "/job-status",
                text: "任务执行状态",
            },
            // {
            //     router: "/job-alarm-search",
            //     text: "任务报警查询",
            // },
            // {
            //     router: "/job-alarm-filter",
            //     text: "任务报警过滤管理",
            // },
            // {
            //     router: "/one-time-job",
            //     text: "一次性任务查询",
            // },
        ]
    },
    {
        router: "/workflow",
        text: "DAG任务管理",
        icon: ApartmentOutlined,
    },
]
