package me.z609.pointsapi2.command;

import me.z609.pointsapi2.PointsAPI;
import me.z609.pointsapi2.StaticPointsAPI;
import me.z609.pointsapi2.currency.Currency;
import me.z609.pointsapi2.player.OfflinePointsPlayer;
import me.z609.pointsapi2.player.PointsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/** The only command exposed by PointsAPI. */
public final class PointsCommand implements CommandExecutor {
    private final PointsAPI plugin;

    public PointsCommand(PointsAPI plugin) {
        this.plugin = plugin;
        plugin.getCommand("points").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /points is intentionally permission-free: it only displays the caller's own balance.
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                plugin.getMessages().send(sender, "players-only");
                return true;
            }
            PointsPlayer pointsPlayer = plugin.getPointsPlayerManager().getPlayer(player);
            if (pointsPlayer != null) for (Map.Entry<Currency, Integer> balance : pointsPlayer.getCurrencyValues().entrySet())
                sendBalance(sender, balance.getKey(), balance.getValue());
            return true;
        }

        String subcommand = args[0].toLowerCase();
        if (subcommand.equals("help")) {
            if (allowed(sender, "help")) plugin.getMessages().sendList(sender, "help");
        } else if (subcommand.equals("set")) {
            if (allowed(sender, "set")) updateFromArguments(sender, args, CmdPointType.SET);
        } else if (subcommand.equals("add") || subcommand.equals("give")) {
            if (allowed(sender, "add")) updateFromArguments(sender, args, CmdPointType.ADD);
        } else if (subcommand.equals("deduct") || subcommand.equals("take")) {
            if (allowed(sender, "deduct")) updateFromArguments(sender, args, CmdPointType.DEDUCT);
        } else if (subcommand.equals("reset")) {
            if (allowed(sender, "reset")) {
                if (args.length < 3) plugin.getMessages().send(sender, "missing-reset-arguments");
                else update(sender, args[1], args[2], CmdPointType.RESET, "0");
            }
        } else if (subcommand.equals("reload")) {
            if (allowed(sender, "reload")) {
                plugin.reloadMessages();
                plugin.getMessages().send(sender, "reloaded");
            }
        } else {
            if (allowed(sender, "help")) plugin.getMessages().send(sender, "unknown-subcommand");
        }
        return true;
    }

    private boolean allowed(CommandSender sender, String permission) {
        if (sender.hasPermission("points.*") || sender.hasPermission("points." + permission)) return true;
        plugin.getMessages().send(sender, "insufficient-permissions");
        return false;
    }

    private void updateFromArguments(CommandSender sender, String[] args, CmdPointType type) {
        if (args.length < 4) plugin.getMessages().send(sender, "missing-update-arguments");
        else update(sender, args[1], args[2], type, args[3]);
    }

    private void update(CommandSender sender, String name, String currencyId, CmdPointType type, String rawAmount) {
        Currency currency = plugin.getCurrencyManager().getCurrency(currencyId);
        if (currency == null) {
            plugin.getMessages().send(sender, "invalid-currency", "{currency}", currencyId);
            return;
        }
        if (!StaticPointsAPI.isInteger(rawAmount)) {
            plugin.getMessages().send(sender, "invalid-number");
            return;
        }
        UUID uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
        if (uuid == null) {
            plugin.getMessages().send(sender, "invalid-player");
            return;
        }
        int amount = Integer.parseInt(rawAmount);
        Player onlinePlayer = Bukkit.getPlayerExact(name);
        if (onlinePlayer != null) {
            PointsPlayer pointsPlayer = plugin.getPointsPlayerManager().getPlayer(onlinePlayer);
            setBalance(pointsPlayer, currency, type, amount);
            int updatedAmount = pointsPlayer.get(currency);
            plugin.getMessages().send(onlinePlayer, "target-balance-updated", "{currency_name}", currencyName(currency, updatedAmount), "{amount}", Integer.toString(updatedAmount));
        } else {
            OfflinePointsPlayer pointsPlayer = plugin.getPointsPlayerManager().getOfflinePlayer(uuid);
            setBalance(pointsPlayer, currency, type, amount);
            int updatedAmount = pointsPlayer.get(currency);
            plugin.getMessages().send(sender, "account-balance-updated", "{player}", name, "{currency_name}", currencyName(currency, updatedAmount), "{amount}", Integer.toString(updatedAmount));
        }
    }

    private void setBalance(OfflinePointsPlayer player, Currency currency, CmdPointType type, int amount) {
        if (type == CmdPointType.SET) player.set(currency, amount);
        else if (type == CmdPointType.RESET) player.set(currency, 0);
        else if (type == CmdPointType.ADD) player.set(currency, player.get(currency) + amount);
        else player.set(currency, player.get(currency) - amount);
    }

    private void sendBalance(CommandSender recipient, Currency currency, int amount) {
        plugin.getMessages().send(recipient, "balance", "{amount}", Integer.toString(amount), "{currency_name}", currencyName(currency, amount));
    }

    private String currencyName(Currency currency, int amount) {
        return amount == 1 ? currency.getNameSingular() : currency.getNamePlural();
    }

    private enum CmdPointType { SET, DEDUCT, RESET, ADD }
}
