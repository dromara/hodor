export const service = [{
    label: '服务器名称',
    val: 'hostName',
  },
  {
    label: 'IP',
    val: 'ip',
  },
  {
    label: '权重',
    val: 'weight',
  },
  {
    label: '有效权重',
    val: 'effectiveWeight',
  },
  {
    label: '当前权重',
    val: 'currentWeight',
  },
  {
    label: '是否master',
    val: 'master',
  },
  {
    label: '是否在线',
    val: 'active',
  },
]
export const jobGroup = [{
    label: '应用组名程',
    val: 'groupName',
  },
  {
    label: '备注',
    val: 'remark',
  },
]
export const jobStatus = [{
    label: '任务组',
    val: 'groupName',
  },
  {
    label: '阀值（%）',
    val: 'threshold',
  },
  {
    label: '是否开启报警',
    val: 'enabled',
  },
  {
    label: '备注',
    val: 'comment',
  },
]
export const jobMessage = [{
    label: '任务名称',
    val: 'jobName',
  },
  {
    label: '文件路径',
    val: 'filePath',
  },
  {
    label: 'corn时间表达式',
    val: 'cronExpression',
  },
  {
    label: '任务参数',
    val: 'parameters',
  },
  {
    label: '超时时间（秒）',
    val: 'timeOut',
  },
  {
    label: '任务类型',
    val: 'bin',
  },
  {
    label: '任务状态',
    val: 'jobStatus',
  },

  {
    label: '上次执行时间',
    val: 'lastFireTime',
  },
  {
    label: '任务结束时间',
    val: 'endTime',
  },
  {
    label: '创建时间',
    val: 'createTime',
  },
  {
    label: '故障转义',
    val: 'failover',
  },
  {
    label: '错过马上执行',
    val: 'misfire',
  },
  {
    label: '马上调度',
    val: 'fireNow',
  },
  {
    label: '并行执行',
    val: 'ifParallel',
  },
  {
    label: '依赖任务',
    val: 'isDependence',
  },
  {
    label: '任务描述',
    val: 'desc',
  },
  {
    label: '子节点Ip',
    val: 'slaveIp',
  },
]
export const zone = [{
    label: '序号',
    val: 'id',
  },
  {
    label: '名称',
    val: 'name',
  },
  {
    label: 'masterid',
    val: 'zoneManagerId',
  },
]
export const jobAlarm = [{
    label: '任务组',
    val: 'groupName',
  },
  {
    label: '创建时间',
    val: 'createdAt',
  },
  {
    label: '最近修改时间',
    val: 'updatedAt',
  },
  {
    label: '是否开启报警',
    val: 'onOff',
  },
  {
    label: 'rtx报警接收人列表',
    val: 'alarmRtx',
  },
]
export const jobRunDetails = [{
    label: 'uuid',
    val: 'uuid',
  },
  {
    label: '执行开始时间',
    val: 'executeStart',
  },
  {
    label: '执行结束时间',
    val: 'executeEnd',
  },
  {
    label: '耗时（秒）',
    val: 'timeConsuming',
  },
  {
    label: '状态',
    val: 'currentStatus',
  },
  {
    label: '是否强制终止',
    val: 'killprocess',
  },
  {
    label: '实例Id',
    val: 'instanceId',
  },
  {
    label: '调度服务器IP',
    val: 'scheduleSip',
  },
  {
    label: '业务服务器Ip',
    val: 'businessSip',
  },
  {
    label: '调度开始时间',
    val: 'scheduleStart',
  },
  {
    label: '调度结束时间',
    val: 'scheduleEnd',
  },
  {
    label: '创建时间',
    val: 'createdAt',
  },
  {
    label: '更新时间',
    val: 'updatedAt',
  },
  {
    label: '异常位置',
    val: 'errorLocation',
  },
  {
    label: '异常类型',
    val: 'errorType',
  },
  {
    label: '异常原因',
    val: 'errorReason',
  },
]
export const jobAlarmSearch = [{
    label: 'id',
    val: 'id',
  },
  {
    label: '服务器名',
    val: 'serverName',
  },
  {
    label: '服务器IP',
    val: 'serverIp',
  },
  {
    label: '监控码',
    val: 'pointCode',
  },
  {
    label: '错误码',
    val: 'errorCode',
  },
  {
    label: '告警级别',
    val: 'level',
  },
  {
    label: '告警内容',
    val: 'content',
  },
  {
    label: '告警时间',
    val: 'noticeTime',
  },

]

export const jobAlarmFilter = [{
    label: '组名',
    val: 'groupName',
  },
  {
    label: '错误编码',
    val: 'code',
  },
  {
    label: '错误片段',
    val: 'exception',
  },
  {
    label: '使用范围',
    val: 'scope',
  },
]

export const jobBind = [{
    label: '名称',
    val: 'groupName',
  },
  {
    label: '是否已绑定',
    val: 'binded',
  },
  {
    label: '已绑定的会话',
    val: 'bindSession',
  },
  {
    label: '备注',
    val: 'remark',
  },
]

export const onceSearch = [{
    label: '任务名称',
    val: 'jobName',
  },
  {
    label: '任务组',
    val: 'groupName',
  },
  {
    label: 'corn时间表达式',
    val: 'cronExpression',
  },
  {
    label: 'job执行时间',
    val: 'executeTime',
  },
  {
    label: 'job创建时间',
    val: 'createTime',
  },
  {
    label: 'slaveIp',
    val: 'slaveIp',
  },
  {
    label: '任务参数',
    val: 'parameters',
  },
  {
    label: '任务描述',
    val: 'desc',
  },
  {
    label: '上次任务执行时间',
    val: 'lastFireTime',
  },
]
export const nodeInfo = [{
    label: '机器',
    val: 'nodeName',
  },
  {
    label: '权重',
    val: 'weight',
  },
  {
    label: 'session名称',
    val: 'sessionName',
  },
  {
    label: '状态',
    val: 'statusDesc',
  },

]
export const userAction = [{
    label: 'id',
    val: 'id',
  },
  {
    label: '用户名',
    val: 'username',
  },
  {
    label: '操作',
    val: 'permitItem',
  },
  {
    label: '操作日期',
    val: 'actionDate',
  },
  {
    label: '操作参数',
    val: 'actionParam',
  },
  {
    label: '操作主机',
    val: 'host',
  },
  {
    label: '操作IP',
    val: 'addr',
  },
]
export const userRole = [{
    label: 'id',
    val: 'id',
  },
  {
    label: '角色名称',
    val: 'roleName',
  },
  {
    label: '描述',
    val: 'description',
  },

]
export const userManage = [{
    label: '用户',
    val: 'username',
  },
  {
    label: '角色',
    val: 'roleName',
  },
  {
    label: '创建时间',
    val: 'createdAt',
  },
  {
    label: '更改时间',
    val: 'updatedAt',
  },
]
export const toolMonitor = [{
    label: 'id',
    val: 'id',
  },
  {
    label: '错误编码',
    val: 'errorCode',
  },
  {
    label: '项目编码描述',
    val: 'description',
  },
  {
    label: '监控点编码',
    val: 'pointCode',
  },
  {
    label: '是否自检',
    val: 'isTest',
  },
  {
    label: '自检项关联ID',
    val: 'testProjectCode',
  },
  {
    label: '触发报警的条件',
    val: 'sendCondition',
  },
  {
    label: '是否可用',
    val: 'available',
  }
]
export const rmsConfig = [{
    label: '任务组',
    val: 'jobGroup',
  },
  {
    label: '项目编码',
    val: 'projectCode',
  },
  {
    label: '监控点编码',
    val: 'pointCode',
  },
  {
    label: '监控点编码描述',
    val: 'description',
  },
  {
    label: '错误码',
    val: 'errorCode',
  },
  {
    label: 'token值',
    val: 'token',
  },
  {
    label: '触发报警的条件',
    val: 'sendCondition',
  },
  {
    label: '是否可用',
    val: 'available',
  }
]
