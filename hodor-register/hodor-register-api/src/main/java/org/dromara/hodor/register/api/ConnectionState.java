package org.dromara.hodor.register.api;

/**
 * Connection State
 *
 * @author tomgs
 * @since 2021/8/18
 */
public enum ConnectionState {
    CONNECTED {
        public boolean isConnected() {
            return true;
        }
    },
    SUSPENDED {
        public boolean isConnected() {
            return false;
        }
    },
    RECONNECTED {
        public boolean isConnected() {
            return true;
        }
    },
    LOST {
        public boolean isConnected() {
            return false;
        }
    },
    READ_ONLY {
        public boolean isConnected() {
            return true;
        }
    };

    private ConnectionState() {
    }

    public abstract boolean isConnected();
}
