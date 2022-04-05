package org.dromara.hodor.actuator.bigdata.queue;

/**
 * @author tangzhongyuan
 * @create 2019-03-14 9:44
 **/
public interface ITask extends Comparable<ITask> {

    ITask run();

    void setPriority(Priority priority);

    Priority getPriority();

    void setSeq(int seq);

    int getSeq();
}
