package me.z609.pointsapi2.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.z609.pointsapi2.PointsAPI;
import java.sql.*;
import java.util.UUID;

/** MySQL/MariaDB storage using an upsert so every balance is unique per player and currency. */
public final class MySqlPointStorage implements PointStorage {
    private final HikariDataSource dataSource;
    private final String table;

    public MySqlPointStorage(PointsAPI plugin) throws SQLException {
        String prefix = plugin.getConfig().getString("storage.mysql.table-prefix", "pointsapi_");
        if (!prefix.matches("[A-Za-z0-9_]+")) throw new SQLException("storage.mysql.table-prefix may contain only letters, numbers, and underscores");
        table = prefix + "balances";
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + plugin.getConfig().getString("storage.mysql.host", "127.0.0.1") + ":" + plugin.getConfig().getInt("storage.mysql.port", 3306) + "/" + plugin.getConfig().getString("storage.mysql.database", "minecraft") + "?useUnicode=true&characterEncoding=utf8&useSSL=" + plugin.getConfig().getBoolean("storage.mysql.use-ssl", false));
        config.setUsername(plugin.getConfig().getString("storage.mysql.username", "root"));
        config.setPassword(plugin.getConfig().getString("storage.mysql.password", ""));
        config.setMaximumPoolSize(Math.max(1, plugin.getConfig().getInt("storage.mysql.pool-size", 5)));
        config.setPoolName("PointsAPI-MySQL");
        dataSource = new HikariDataSource(config);
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " (player_uuid CHAR(36) NOT NULL, currency VARCHAR(64) NOT NULL, amount INT NOT NULL, PRIMARY KEY (player_uuid, currency))");
        }
    }

    @Override public int get(UUID playerId, String currencyId, int defaultValue) {
        String sql = "SELECT amount FROM " + table + " WHERE player_uuid = ? AND currency = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, playerId.toString()); s.setString(2, currencyId);
            try (ResultSet rs = s.executeQuery()) { return rs.next() ? rs.getInt(1) : defaultValue; }
        } catch (SQLException e) { throw new StorageException("Unable to read point balance", e); }
    }
    @Override public void set(UUID playerId, String currencyId, int value) {
        String sql = "INSERT INTO " + table + " (player_uuid, currency, amount) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE amount = VALUES(amount)";
        try (Connection c = dataSource.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, playerId.toString()); s.setString(2, currencyId); s.setInt(3, value); s.executeUpdate();
        } catch (SQLException e) { throw new StorageException("Unable to save point balance", e); }
    }
    @Override public void close() { dataSource.close(); }
}
