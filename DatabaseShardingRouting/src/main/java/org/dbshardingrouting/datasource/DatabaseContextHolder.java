package org.dbshardingrouting.datasource;


public class DatabaseContextHolder {
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setShardKey(String shardKey) {
        CONTEXT.set(shardKey);
    }

    public static String getShardKey() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}