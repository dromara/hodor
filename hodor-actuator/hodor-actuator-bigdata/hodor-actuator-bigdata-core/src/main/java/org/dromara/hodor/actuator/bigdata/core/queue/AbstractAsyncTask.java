package org.dromara.hodor.actuator.bigdata.core.queue;

/**
 * 异步任务抽象
 *
 * @author tomgs
 * @since 1.0
 **/
public abstract class AbstractAsyncTask implements AsyncTask {

    private Priority priority = Priority.DEFAULT;

    private int seq;

    @Override
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public Priority getPriority() {
        return this.priority;
    }

    @Override
    public void setSeq(int seq) {
        this.seq = seq;
    }

    @Override
    public int getSeq() {
        return this.seq;
    }

    @Override
    public AsyncTask run() {
        seq++;
        return runTask();
    }

    protected abstract AsyncTask runTask();

    @Override
    public int compareTo(AsyncTask task) {
        Priority otherPriority = task.getPriority();
        Priority thisPriority = this.getPriority();

        return thisPriority == otherPriority ? this.getSeq() - task.getSeq()
            : otherPriority.ordinal() - thisPriority.ordinal();
    }
}
