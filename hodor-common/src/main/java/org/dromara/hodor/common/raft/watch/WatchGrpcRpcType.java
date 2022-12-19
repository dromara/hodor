package org.dromara.hodor.common.raft.watch;

import org.apache.ratis.conf.Parameters;
import org.apache.ratis.rpc.RpcFactory;
import org.apache.ratis.rpc.RpcType;
import org.apache.ratis.util.ReflectionUtils;

/**
 * WatchGrpcRpcType
 *
 * @author tomgs
 * @since 1.0
 */
public class WatchGrpcRpcType implements RpcType {

    public static final WatchGrpcRpcType INSTANCE = new WatchGrpcRpcType();

    private WatchGrpcRpcType() {}

    private final String factoryClassName = WatchGrpcFactory.class.getName();

    private static final Class<?>[] ARG_CLASSES = {Parameters.class};

    @Override
    public String name() {
        return this.getClass().getName();
    }

    @Override
    public RpcFactory newFactory(Parameters parameters) {
        final Class<? extends RpcFactory> clazz = ReflectionUtils.getClass(
                factoryClassName, RpcFactory.class);
        return ReflectionUtils.newInstance(clazz, ARG_CLASSES, parameters);
    }

}
