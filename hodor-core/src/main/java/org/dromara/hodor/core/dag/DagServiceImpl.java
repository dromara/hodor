package org.dromara.hodor.core.dag;

import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.DagService;
import org.dromara.hodor.common.dag.Node;
import org.springframework.stereotype.Service;

/**
 * dag service implement
 *
 * @author tomgs
 * @since 2021/8/30
 */
@Service
public class DagServiceImpl implements DagService {

    @Override
    public void startDag(Dag dag) {

    }

    @Override
    public void markNodeRunning(Node node) {

    }

    @Override
    public void markNodeSuccess(Node node) {

    }

    @Override
    public void markNodeKilled(Node node) {

    }

    @Override
    public void markNodeFailed(Node node) {

    }

    @Override
    public void killDag(Dag dag) {

    }

    @Override
    public void shutdownAndAwaitTermination() throws InterruptedException {

    }

}
