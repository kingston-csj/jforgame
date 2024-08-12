package jforgame.runtime.gc;

public class GcInfoVo {
    private String name;
    private long collectionCount;
    private long collectionTime;

    public GcInfoVo(String name, long collectionCount, long collectionTime) {
        this.name = name;
        this.collectionCount = collectionCount;
        this.collectionTime = collectionTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCollectionCount() {
        return collectionCount;
    }

    public void setCollectionCount(long collectionCount) {
        this.collectionCount = collectionCount;
    }

    public long getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(long collectionTime) {
        this.collectionTime = collectionTime;
    }
}

