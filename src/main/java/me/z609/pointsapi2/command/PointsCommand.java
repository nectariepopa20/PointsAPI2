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

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * This code is by Z609, and is copyright (C) 2016 Z609. Don't share this
 * code with the public! Thanks!
 */
public class PointsCommand implements CommandExecutor {
    private PointsAPI parent;

    public PointsCommand(PointsAPI parent) {
        this.parent = parent;
        parent.getCommand("points").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("points")){
            if(sender.hasPermission("points.*")||sender.hasPermission("points.points")) {
                if (args.length == 0) {
                    parent.getMessages().sendList(sender, "help");
                }
                else if (args[0].equalsIgnoreCase("balance")) {
                    if (args.length == 1) {
                        if(sender instanceof Player){
                            PointsPlayer player = parent.getPointsPlayerManager().getPlayer((Player)sender);
                            Iterator<Map.Entry<Currency, Integer>> balances = player.getCurrencyValues().entrySet().iterator();
                            while(balances.hasNext()){
                                Map.Entry<Currency, Integer> balance = balances.next();
                                sendBalance(sender, balance.getKey(), balance.getValue());
                            }
                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length < 4) {
                        parent.getMessages().send(sender, "missing-update-arguments");
                    }
                    else{
                        final String targetRaw = args[1];
                        final String currencyRaw = args[2];
                        final String iRaw = args[3];
                        update(sender, targetRaw, currencyRaw, CmdPointType.SET, iRaw);
                    }
                }
                else if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("give")) {
                    if (args.length < 4) {
                        parent.getMessages().send(sender, "missing-update-arguments");
                    }
                    else{
                        final String targetRaw = args[1];
                        final String currencyRaw = args[2];
                        final String iRaw = args[3];
                        update(sender, targetRaw, currencyRaw, CmdPointType.ADD, iRaw);
                    }
                }
                else if (args[0].equalsIgnoreCase("deduct") || args[0].equalsIgnoreCase("take")) {
                    if (args.length < 4) {
                        parent.getMessages().send(sender, "missing-update-arguments");
                    }
                    else{
                        final String targetRaw = args[1];
                        final String currencyRaw = args[2];
                        final String iRaw = args[3];
                        update(sender, targetRaw, currencyRaw, CmdPointType.DEDUCT, iRaw);
                    }
                }
                else if (args[0].equalsIgnoreCase("reset")) {
                    if (args.length < 3) {
                        parent.getMessages().send(sender, "missing-reset-arguments");
                    }
                    else{
                        final String targetRaw = args[1];
                        final String currencyRaw = args[2];
                        update(sender, targetRaw, currencyRaw, CmdPointType.RESET, "0");
                    }
                }
                else if (args[0].equalsIgnoreCase("reload")) {
                    parent.reloadMessages();
                    parent.getMessages().send(sender, "reloaded");
                }
            }
            else{
                parent.getMessages().send(sender, "insufficient-permissions");
            }
        }
        return true;
    }

    public void update(final CommandSender sender, final String name, final String strcurrency, final CmdPointType type, final String strint) {
        if(parent.getCurrencyManager().getCurrency(strcurrency)==null){
            parent.getMessages().send(sender, "invalid-currency", "{currency}", strcurrency);
            return;
        }
        Currency currency = parent.getCurrencyManager().getCurrency(strcurrency);
        if (StaticPointsAPI.isInteger(strint)) {
            final int i = Integer.parseInt(strint);
            final UUID uuid = Bukkit.getServer().getOfflinePlayer(name).getUniqueId();
            if (uuid != null) {
                if (Bukkit.getPlayerExact(name) != null) {
                    PointsPlayer pointsPlayer = parent.getPointsPlayerManager().getPlayer(Bukkit.getPlayerExact(name));
                    if (type == CmdPointType.ADD) {
                        pointsPlayer.set(currency, pointsPlayer.get(currency)+i);
                    }
                    else if (type == CmdPointType.DEDUCT) {
                        pointsPlayer.set(currency, pointsPlayer.get(currency)-i);
                    }
                    else if (type == CmdPointType.SET) {
                        pointsPlayer.set(currency, i);
                    }
                    else if (type == CmdPointType.RESET) {
                        pointsPlayer.set(currency, 0);
                    }
                    parent.getMessages().send(Bukkit.getPlayerExact(name), "target-balance-updated", "{currency_name}", currencyName(currency, pointsPlayer.get(currency)), "{amount}", Integer.toString(pointsPlayer.get(currency)));
                }
                else {
                    OfflinePointsPlayer pointsPlayer = parent.getPointsPlayerManager().getOfflinePlayer(uuid);
                    if (type == CmdPointType.ADD) {
                        pointsPlayer.set(currency, pointsPlayer.get(currency)+i);
                    }
                    else if (type == CmdPointType.DEDUCT) {
                        pointsPlayer.set(currency, pointsPlayer.get(currency)-i);
                    }
                    else if (type == CmdPointType.SET) {
                        pointsPlayer.set(currency, i);
                    }
                    else if (type == CmdPointType.RESET) {
                        pointsPlayer.set(currency, 0);
                    }
                    parent.getMessages().send(sender, "account-balance-updated", "{player}", name, "{currency_name}", currencyName(currency, pointsPlayer.get(currency)), "{amount}", Integer.toString(pointsPlayer.get(currency)));
                }
            }
            else {
                parent.getMessages().send(sender, "invalid-player");
            }
        }
        else {
            parent.getMessages().send(sender, "invalid-number");
        }
    }

    private void sendBalance(CommandSender recipient, Currency currency, int amount) {
        parent.getMessages().send(recipient, "balance", "{amount}", Integer.toString(amount), "{currency_name}", currencyName(currency, amount));
    }

    private String currencyName(Currency currency, int amount) {
        return amount == 1 ? currency.getNameSingular() : currency.getNamePlural();
    }

    public enum CmdPointType
    {
        SET,
        DEDUCT,
        RESET,
        ADD;
    }
}
