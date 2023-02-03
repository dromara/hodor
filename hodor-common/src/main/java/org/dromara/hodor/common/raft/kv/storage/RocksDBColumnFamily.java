package org.dromara.hodor.common.raft.kv.storage;

import org.dromara.hodor.common.utils.BytesUtil;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.RocksDB;

/**
 * default RocksDBColumnFamily
 *
 * @author tomgs
 * @since 1.0
 */
public enum RocksDBColumnFamily {

    Default {
        final String name = "default";

        @Override
        public String toString() {
            return name;
        }

        @Override
        public ColumnFamilyDescriptor getColumnFamilyDescriptor(ColumnFamilyOptions cfOptions) {
            return new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOptions);
        }
    },

    HodorRaft {
        final String name = "hodor_raft";

        @Override
        public String toString() {
            return name;
        }

        @Override
        public ColumnFamilyDescriptor getColumnFamilyDescriptor(ColumnFamilyOptions cfOptions) {
            return new ColumnFamilyDescriptor(BytesUtil.writeUtf8(name), cfOptions);
        }
    },

    HodorWatch {
        final String name = "hodor_watch";

        @Override
        public String toString() {
            return name;
        }

        @Override
        public ColumnFamilyDescriptor getColumnFamilyDescriptor(ColumnFamilyOptions cfOptions) {
            return new ColumnFamilyDescriptor(BytesUtil.writeUtf8(name), cfOptions);
        }
    },

    HodorLock {
        final String name = "hodor_lock";

        @Override
        public String toString() {
            return name;
        }

        @Override
        public ColumnFamilyDescriptor getColumnFamilyDescriptor(ColumnFamilyOptions cfOptions) {
            return new ColumnFamilyDescriptor(BytesUtil.writeUtf8(name), cfOptions);
        }
    },

    HodorSeq {
        final String name = "hodor_seq";

        @Override
        public String toString() {
            return name;
        }

        @Override
        public ColumnFamilyDescriptor getColumnFamilyDescriptor(ColumnFamilyOptions cfOptions) {
            return new ColumnFamilyDescriptor(BytesUtil.writeUtf8(name), cfOptions);
        }
    },

    HodorWrite {
        final String name = "hodor_write";

        @Override
        public String toString() {
            return name;
        }

        @Override
        public ColumnFamilyDescriptor getColumnFamilyDescriptor(ColumnFamilyOptions cfOptions) {
            return new ColumnFamilyDescriptor(BytesUtil.writeUtf8(name), cfOptions);
        }
    };

    public abstract ColumnFamilyDescriptor getColumnFamilyDescriptor(ColumnFamilyOptions cfOptions);

}
