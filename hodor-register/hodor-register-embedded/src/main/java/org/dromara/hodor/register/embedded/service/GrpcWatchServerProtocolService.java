package org.dromara.hodor.register.embedded.service;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.thirdparty.io.grpc.Status;
import org.apache.ratis.thirdparty.io.grpc.stub.StreamObserver;
import org.dromara.hodor.common.proto.WatchCancelRequest;
import org.dromara.hodor.common.proto.WatchCreateRequest;
import org.dromara.hodor.common.proto.WatchRequest;
import org.dromara.hodor.common.proto.WatchResponse;
import org.dromara.hodor.common.proto.WatchServiceGrpc;
import org.dromara.hodor.register.embedded.core.AbstractConnection;
import org.dromara.hodor.register.embedded.core.WatchManager;
import org.dromara.hodor.register.embedded.core.WatchedStatus;

/**
 * GrpcWatchServerProtocolService
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class GrpcWatchServerProtocolService extends WatchServiceGrpc.WatchServiceImplBase {

    private final WatchManager watchManager = WatchManager.getInstance();

    @Override
    public StreamObserver<WatchRequest> watch(StreamObserver<WatchResponse> responseObserver) {
        // new connection
        WatchStreamConnection watchStreamConnection = new WatchStreamConnection(responseObserver);

        return new StreamObserver<WatchRequest>() {
            private  boolean initRequest = true;

            @Override
            public void onNext(WatchRequest request) {
                // init request
                if (initRequest) {
                    watchStreamConnection.setConnectionId(StrUtil.toString(request.getNodeId()));
                    watchManager.addWatchConnection(watchStreamConnection.getConnectionId(), watchStreamConnection);
                    initRequest = false;
                }

                if (request.hasCreateRequest()) {
                    final WatchCreateRequest createRequest = request.getCreateRequest();
                    watchManager.addWatchRequest(createRequest);
                }
                if (request.hasCancelRequest()) {
                    final WatchCancelRequest cancelRequest = request.getCancelRequest();
                    watchManager.cancelWatchRequest(cancelRequest);
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("error: {}", t.getMessage(), t);
                Status status = Status.fromThrowable(t);
                if (status != null && status.getCode() != Status.Code.CANCELLED) {
                    responseObserver.onCompleted();
                }
                clear();
            }

            @Override
            public void onCompleted() {
                log.info("watch stream: {} stream close.", watchStreamConnection.getConnectionId());
                responseObserver.onCompleted();
                clear();
            }

            private void clear() {
                watchManager.removeWatchConnection(watchStreamConnection.getConnectionId());
            }
        };
    }

    static class WatchStreamConnection extends AbstractConnection<WatchResponse> {

        public WatchStreamConnection(StreamObserver<WatchResponse> streamObserver) {
            super(streamObserver);
        }

        @Override
        public void push(WatchResponse response, WatchedStatus watchedStatus) {
            if (log.isDebugEnabled()) {
                log.debug("WatchResponse: {}", response.toString());
            }

            this.streamObserver.onNext(response);

            // Update watched status
            //watchedStatus.setLatestVersion(response.getSystemVersionInfo());
            //watchedStatus.setLatestNonce(response.getNonce());

            //log.info("watch: push, type: {}, connection-id {}, version {}, nonce {}, resource size {}.",
            //        watchedStatus.getType(),
            //        getConnectionId(),
            //        response.getSystemVersionInfo(),
            //        response.getNonce(),
            //        response.getResourcesCount());
        }

    }

}
