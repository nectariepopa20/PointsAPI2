package me.z609.pointsapi2.storage;

import me.z609.pointsapi2.PointsAPI;
import java.util.UUID;

/** Legacy local-file storage used when storage.type is YAML. */
public final class YamlPointStorage implements PointStorage {
    private final PointsAPI plugin;
    public YamlPointStorage(PointsAPI plugin) { this.plugin = plugin; }
    private String path(UUID playerId, String currencyId) { return "values." + playerId + "." + currencyId; }
    @Override public int get(UUID playerId, String currencyId, int defaultValue) {
        String path = path(playerId, currencyId);
        return plugin.getConfig().contains(path) ? plugin.getConfig().getInt(path) : defaultValue;
    }
    @Override public void set(UUID playerId, String currencyId, int value) {
        plugin.getConfig().set(path(playerId, currencyId), value);
        plugin.saveConfig();
    }
}
