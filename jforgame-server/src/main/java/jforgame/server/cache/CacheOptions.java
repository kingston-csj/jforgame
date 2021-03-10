package jforgame.server.cache;

import jforgame.common.utils.TimeUtil;

/**
 * 缓存相关配置
 * @author kinson
 */
public class CacheOptions {

    private final static int DEFAULT_INITIAL_CAPACITY = 1024; 
    private final static int DEFAULT_MAXIMUM_SIZE = 65536;
    private final static int DEFAULT_EXPIRE_AFTER_ACCESS_SECONDS = (int)(5*TimeUtil.ONE_HOUR/TimeUtil.ONE_MILLISECOND);
    private final static int DEFAULT_EXPIRE_AFTER_WRITE_SECONDS = (int)(5*TimeUtil.ONE_HOUR/TimeUtil.ONE_MILLISECOND);

    public final int initialCapacity;
    public final int maximumSize;
    public final int expireAfterAccessSeconds;
    public final int expireAfterWriteSeconds;

    private CacheOptions(int initialCapacity, int maximumSize, int expireAfterAccessSeconds, int expireAfterWriteSeconds) {
        this.initialCapacity = initialCapacity;
        this.maximumSize = maximumSize;
        this.expireAfterAccessSeconds = expireAfterAccessSeconds;
        this.expireAfterWriteSeconds = expireAfterWriteSeconds;
    }

    public static CacheOptions defaultCacheOptions() {
        return new Builder().build();
    }

    static class Builder {
        private int initialCapacity;
        private int maximumSize;
        private int expireAfterAccessSeconds;
        private int expireAfterWriteSeconds;

        private Builder() {

        }

        public Builder setInitialCapacity(int initialCapacity) {
            this.initialCapacity = initialCapacity;
            return this;
        }

        public Builder setMaximumSize(int maximumSize) {
            this.maximumSize = maximumSize;
            return this;
        }

        public Builder setExpireAfterAccessSeconds(int expireAfterAccessSeconds) {
            this.expireAfterAccessSeconds = expireAfterAccessSeconds;
            return this;
        }

        public Builder setExpireAfterWriteSeconds(int expireAfterWriteSeconds) {
            this.expireAfterWriteSeconds = expireAfterWriteSeconds;
            return this;
        }

        private CacheOptions build() {
            if (initialCapacity == 0) {
                setInitialCapacity(DEFAULT_INITIAL_CAPACITY);
            }
            if (maximumSize == 0) {
                setMaximumSize(DEFAULT_MAXIMUM_SIZE);
            }
            if(expireAfterAccessSeconds == 0) {
                setExpireAfterAccessSeconds(DEFAULT_EXPIRE_AFTER_ACCESS_SECONDS);
            }
            if(expireAfterWriteSeconds == 0) {
                setExpireAfterWriteSeconds(DEFAULT_EXPIRE_AFTER_WRITE_SECONDS);
            }
            return new CacheOptions(initialCapacity, maximumSize, expireAfterAccessSeconds, expireAfterWriteSeconds);
        }
    }

}
