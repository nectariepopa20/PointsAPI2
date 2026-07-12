package me.z609.pointsapi2.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.z609.pointsapi2.PointsAPI;
import me.z609.pointsapi2.currency.Currency;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Internal PlaceholderAPI expansion. It is bundled with PointsAPI and needs no eCloud download. */
public final class PointsPlaceholderExpansion extends PlaceholderExpansion {
    private final PointsAPI plugin;

    public PointsPlaceholderExpansion(PointsAPI plugin) { this.plugin = plugin; }

    @Override public @NotNull String getIdentifier() { return "pointsapi2"; }
    @Override public @NotNull String getAuthor() { return "Z609, nectariepopa20"; }
    @Override public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }

    @Override public boolean persist() { return true; }

    @Override public @Nullable String onRequest(@Nullable OfflinePlayer player, @NotNull String params) {
        if (player == null || !player.hasPlayedBefore() && !player.isOnline()) return null;
        Currency currency = plugin.getCurrencyManager().getCurrency(params);
        if (currency == null) return null;
        return Integer.toString(plugin.getPointsPlayerManager().getOfflinePlayer(player).get(currency));
    }
}
