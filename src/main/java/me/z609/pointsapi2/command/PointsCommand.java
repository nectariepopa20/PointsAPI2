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
                    sender.sendMessage("§b§lPointsAPI §eby Z609");
                    sender.sendMessage("§b/points set <username> <currency> <amount> - Set points");
                    sender.sendMessage("§b/points add|give <username> <currency> <amount> - Add points");
                    sender.sendMessage("§b/points deduct|take <username> <currency> <amount> - Deduct points");
                    sender.sendMessage("§b/points reset <username> <currency> - Reset points");
                    sender.sendMessage("§b/points balance - See your points (admin version)");
                    sender.sendMessage("§b/balance|bal|money - User-friendly /balance");
                }
                else if (args[0].equalsIgnoreCase("balance")) {
                    if (args.length == 1) {
                        if(sender instanceof Player){
                            PointsPlayer player = parent.getPointsPlayerManager().getPlayer((Player)sender);
                            Iterator<Map.Entry<Currency, Integer>> balances = player.getCurrencyValues().entrySet().iterator();
                            while(balances.hasNext()){
                                Map.Entry<Currency, Integer> balance = balances.next();
                                sender.sendMessage("§bYou have " + balance.getValue() + " " + (balance.getValue() != 1 ? balance.getKey().getNamePlural() : balance.getKey().getNameSingular()) + "!");
                            }
                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length < 4) {
                        sender.sendMessage("§cPlease specify a username, currency, and an amount!");
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
                        sender.sendMessage("§cPlease specify a username, currency, and an amount!");
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
                        sender.sendMessage("§cPlease specify a username, currency, and an amount!");
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
                        sender.sendMessage("§cPlease specify a username and an amount!");
                    }
                    else{
                        final String targetRaw = args[1];
                        final String currencyRaw = args[2];
                        update(sender, targetRaw, currencyRaw, CmdPointType.RESET, "0");
                    }
                }
            }
            else{
                sender.sendMessage("Insufficient privileges.");
            }
        }
        return true;
    }

    public void update(final CommandSender sender, final String name, final String strcurrency, final CmdPointType type, final String strint) {
        if(parent.getCurrencyManager().getCurrency(strcurrency)==null){
            sender.sendMessage("§cInvalid currency!");
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
                    Bukkit.getPlayerExact(name).sendMessage("§bYour balance was updated.");
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
                    sender.sendMessage("§bYou have updated " + name + "'s account balance.");
                }
            }
            else {
                sender.sendMessage("§cThat player name is not a valid Minecraft username!");
            }
        }
        else {
            sender.sendMessage("§cThat is not a number!");
        }
    }

    public enum CmdPointType
    {
        SET,
        DEDUCT,
        RESET,
        ADD;
    }
}
