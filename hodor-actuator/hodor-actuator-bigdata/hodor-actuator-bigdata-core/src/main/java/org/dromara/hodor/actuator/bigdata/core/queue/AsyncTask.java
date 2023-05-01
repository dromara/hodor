package org.dromara.hodor.actuator.bigdata.core.queue;

/**
 * 异步任务接口
 *
 * @author tomgs
 * @since 1.0
 **/
public interface AsyncTask extends Comparable<AsyncTask> {

    AsyncTask run();

    void setPriority(Priority priority);

    Priority getPriority();

    void setSeq(int seq);

    int getSeq();
}
