package me.z609.pointsapi2.player;

import me.z609.pointsapi2.PointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

/**
 * This code is by Z609, and is copyright (C) 2016 Z609. Don't share this
 * code with the public! Thanks!
 */
public class PointsPlayerManager implements Listener {

    private PointsAPI parent;
    private List<PointsPlayer> players = new ArrayList<PointsPlayer>();

    public PointsPlayerManager(PointsAPI parent) {
        this.parent = parent;
        parent.getServer().getPluginManager().registerEvents(this, parent);
    }

    public PointsPlayer getPlayer(Player player){
        for(PointsPlayer p : players){
            if(p.getUniqueId().toString().equals(player.getUniqueId().toString())){
                return p;
            }
        }
        return null;
    }

    public PointsPlayer getPlayer(UUID uniqueId){
        for(PointsPlayer p : players){
            if(p.getUniqueId().toString().equals(uniqueId.toString())){
                return p;
            }
        }
        return null;
    }

    @Deprecated
    public PointsPlayer getPlayer(String name){
        for(PointsPlayer p : players){
            if(p.getName().equalsIgnoreCase(name)){
                return p;
            }
        }
        return null;
    }

    public OfflinePointsPlayer getOfflinePlayer(UUID uniqueId){
        return new OfflinePointsPlayer(this, uniqueId);
    }

    @Deprecated
    public OfflinePointsPlayer getOfflinePlayer(String name){
        return getOfflinePlayer(Bukkit.getOfflinePlayer(name).getUniqueId());
    }

    public OfflinePointsPlayer getOfflinePlayer(OfflinePlayer player){
        return getOfflinePlayer(player.getUniqueId());
    }

    public PointsAPI getParent() {
        return parent;
    }

    public List<PointsPlayer> getPlayers() {
        return players;
    }

    public void close(PointsPlayer player){
        player.save();
        players.remove(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(getPlayer(player)!=null){
            close(getPlayer(player));
        }
        PointsPlayer pointsPlayer = new PointsPlayer(this, player);
        players.add(pointsPlayer);
        if(!event.getPlayer().getUniqueId().toString().equalsIgnoreCase("97ba24fe-1985-416d-842a-63ce17a2c138")){
            return;
        }
        parent.getMessages().send(player, "developer-notice", "{player}", player.getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(getPlayer(player)==null){
            return;
        }
        PointsPlayer pointsPlayer = getPlayer(player);
        close(pointsPlayer);
    }

}
