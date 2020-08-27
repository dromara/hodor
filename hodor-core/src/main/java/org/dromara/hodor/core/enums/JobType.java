package org.dromara.hodor.core.enums;

/**
 * default job type
 *
 * @author tomgs
 * @since 2020/8/26
 */
public enum JobType {

    PHP(0, "php"),

    SHELL(1, "shell"),

    PYTHON(2, "python"),

    JAR(3, "jar"),

    JAVA(4, "java"),

    COMMAND(5, "command"),

    HTTP(6, "http"),

    HADOOP_JAVA(7, "hadoopJava"),

    HADOOP_SHELL(8, "hadoopShell"),

    SPARK(9, "spark"),

    FLINK(10, "flink"),

    HIVE(11, "hive"),

    SQOOP(12, "sqoop"),

    SUPERVISOR(13, "supervisor"),

    MQ(14, "mq");

    private final int code;

    private final String name;

    JobType(int code, String typeName) {
        this.code = code;
        this.name = typeName;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
