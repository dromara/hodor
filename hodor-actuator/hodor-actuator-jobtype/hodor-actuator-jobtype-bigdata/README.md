# 大数据执行端数据目录设计
大数据执行端基于Azkaban二次开发扩展实现而来。

```text
${dataPath}/plugins/jobtypes/
- hadoop/
- spark/
- java/

${dataPath}/resources/${jobKey}/${version}/
- xxx.jar
- xxx.properties

${dataPath}/executions/${requestId}/
- _job.${jobKey}.${requestId}.log
- createFlattenedPropsFile -> ${requestId}_props_._tmp
- createOutputPropsFile -> ${requestId}_output_._tmp
- 创建一个硬链接，连接到对应version的resources（这样就可以避免每次都复制资源文件）
```

