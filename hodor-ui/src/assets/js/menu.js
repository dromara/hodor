// 菜单配置
export const menu = [{
    "name": "服务器管理",
    "index": 1,
    "router": "/service",
    "icon": "el-icon-ump-fenfayuan",
    "authUniqueMark": 'edts_source'
  },
  {
    "name": "job管理",
    "index": 2,
    "icon": "el-icon-ump-xiangmu1",
    "authUniqueMark": 'edts_pipe',
    "router": "/job",
    "children": [{
        "name": "job组管理",
        "router": "/jobgroup",
        "id": 2,
        "authUniqueMark": 'job_group',
      },
      /*{
        "name": "job报警管理",
        "id": 1,
        "router": "/jobalarm",
        "authUniqueMark": 'job_alarm',
      },*/
      {
        "name": "job执行状态监控管理",
        "router": "/jobstatus",
        "authUniqueMark": 'job_status',
      },
      {
        "name": "job信息管理",
        "router": "/jobmessage",
        "id": 3,
        "authUniqueMark": 'job_message',
      },
      {
        "name": "job报警查询",
        "router": "/job_alarm_search",
        "authUniqueMark": 'job_alarm_search',
      },
      {
        "name": "job报警过滤管理",
        "router": "/job_alarm_filter",
        "authUniqueMark": 'job_alarm_filter',
      },
      {
        "name": "job绑定",
        "router": "/job_bind",
        "authUniqueMark": 'job_bind',
      },
      {
        "name": "一次性job查询",
        "router": "/once_search",
        "authUniqueMark": 'job_once_search',
      }
    ]
  },
  {
    "name": "workFlow管理",
    "index": 3,
    "icon": "el-icon-ump-channel",
    "authUniqueMark": 'edts_zone',
    "router": "/work_flow"
  },
  {
    "name": "图表统计",
    "index": 4,
    "icon": "el-icon-ump-linode",
    "authUniqueMark": 'edts_node',
    "router": "/chart",
    "children": [
      {
        "name": "job统计",
        "router": "/chart_statistics",
        "authUniqueMark": 'job_chart_statistics',
      }]
  },
  {
    "name": "自助服务",
    "index": 5,
    "icon": "el-icon-ump-xiangmu1",
    "authUniqueMark": 'edts_monitor',
    "router": "/tool",
    "children": [{
        "name": "表达式转换",
        "router": "/tool_cron_transfer",
        "authUniqueMark": 'job_tool_cron_transfer',
      },
      {
        "name": "一键配置报警",
        "router": "/tool_config_monitor",
        "authUniqueMark": 'job_tool_config_monitor',
      },
      {
        "name": "操作记录",
        "router": "/user_action",
        "authUniqueMark": 'job_user_action',
      }
    ]
  },
  {
    "name": "权限管理",
    "index": 6,
    "icon": "el-icon-ump-user",
    "authUniqueMark": 'edts_fn',
    "roleId": 0,
    "router": "/user",
    "children": [{
        "name": "授权",
        "router": "/user_auth",
        "authUniqueMark": 'job_user_auth',
      },
      {
        "name": "用户行为",
        "router": "/user_action",
        "authUniqueMark": 'job_user_action',
      },
      {
        "name": "角色管理",
        "router": "/user_role",
        "authUniqueMark": 'job_user_role',
      },
      {
        "name": "用户管理",
        "router": "/user_manage",
        "authUniqueMark": 'job_user_manage',
      },
      {
        "name": "监控项管理",
        "router": "/tool_monitor",
        "authUniqueMark": 'job_tool_monitor',
      }
    ]
  },
  {
    "name": "反馈和帮助",
    "index": 7,
    "icon": "el-icon-ump-xiangmu1",
    "authUniqueMark": 'feedback_help',
    "children": [{
        "name": "使用说明",
        "router": "/help",
        "authUniqueMark": 'job_help',
      },
      {
        "name": "意见反馈",
        "router": "/feedback",
        "authUniqueMark": 'job_feedback',
      },
    ]
  }]
