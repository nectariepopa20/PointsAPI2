package me.z609.pointsapi2.storage;

import java.util.UUID;

/** Persistent point balances. Implementations must be safe for plugin lifecycle use. */
public interface PointStorage extends AutoCloseable {
    int get(UUID playerId, String currencyId, int defaultValue);
    void set(UUID playerId, String currencyId, int value);
    @Override default void close() { }
}
