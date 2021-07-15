package org.dromara.hodor.server.restservice.service;

import org.dromara.hodor.remoting.api.http.HodorHttpRequest;
import org.dromara.hodor.server.restservice.HodorRestService;

/**
 * register service
 *
 * @author tomgs
 * @since 2021/2/5
 */
@HodorRestService(value = "worker", desc = "work rest service")
public class WorkerService {

    public void workerRegistry(HodorHttpRequest request) {

    }

}
