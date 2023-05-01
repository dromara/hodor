package org.dromara.hodor.model.enums;

/**
 * default job command type
 *
 * @author tomgs
 * @since 1.0
 */
public enum CommandType {

    PHP("php"),

    SHELL("shell"),

    PYTHON("python"),

    JAR("jar"),

    JAVA("java"),

    COMMAND("command"),

    HTTP("http"),

    HADOOP_JAVA("hadoopJava"),

    HADOOP_SHELL("hadoopShell"),

    SPARK("spark"),

    FLINK("flink"),

    HIVE("hive"),

    SQOOP("sqoop"),

    SUPERVISOR("supervisor"),

    MQ("mq");

    private final String name;

    CommandType(String typeName) {
        this.name = typeName;
    }

    public static CommandType of(String type) {
        for (CommandType commandType : CommandType.values()) {
            if (commandType.getName().equals(type)) {
                return commandType;
            }
        }
        throw new IllegalArgumentException("not found command type by " + type);
    }

    public String getName() {
        return name;
    }
}
