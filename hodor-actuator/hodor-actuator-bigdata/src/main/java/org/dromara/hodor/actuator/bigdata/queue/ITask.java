package org.dromara.hodor.actuator.bigdata.queue;

/**
 * @author tomgs
 * @since 1.0
 **/
public interface ITask extends Comparable<ITask> {

    ITask run();

    void setPriority(Priority priority);

    Priority getPriority();

    void setSeq(int seq);

    int getSeq();
}
