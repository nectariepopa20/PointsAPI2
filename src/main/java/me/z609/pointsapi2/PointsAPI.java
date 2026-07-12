package me.z609.pointsapi2;

import me.z609.pointsapi2.command.PointsCommand;
import me.z609.pointsapi2.currency.CurrencyManager;
import me.z609.pointsapi2.player.PointsPlayer;
import me.z609.pointsapi2.player.PointsPlayerManager;
import me.z609.pointsapi2.storage.MySqlPointStorage;
import me.z609.pointsapi2.storage.PointStorage;
import me.z609.pointsapi2.storage.YamlPointStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * This code is by Z609, and is copyright (C) 2016 Z609. Don't share this
 * code with the public! Thanks!
 */
public class PointsAPI extends JavaPlugin {

    private CurrencyManager currencyManager;
    private PointsPlayerManager pointsPlayerManager;
    private PointStorage pointStorage;
    private boolean fullInit = false;

    @Override
    public void onEnable(){
        getConfig().options().copyDefaults(true);
        saveConfig();
        currencyManager = new CurrencyManager(this);
        try {
            pointStorage = "MYSQL".equalsIgnoreCase(getConfig().getString("storage.type", "YAML"))
                    ? new MySqlPointStorage(this) : new YamlPointStorage(this);
        } catch (Exception exception) {
            getLogger().log(Level.SEVERE, "Could not initialize MySQL storage. Check storage.mysql in config.yml.", exception);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        pointsPlayerManager = new PointsPlayerManager(this);
        new PointsCommand(this);
        fullInit = true;
    }

    @Override
    public void onDisable(){
        if(!fullInit)return;
        for(PointsPlayer player : pointsPlayerManager.getPlayers()){
            player.save();
        }
        pointStorage.close();
    }

    public void reload(){
        reloadConfig();
    }

    public void save(){
        saveConfig();
        reload();
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    public PointsPlayerManager getPointsPlayerManager() {
        return pointsPlayerManager;
    }

    public PointStorage getPointStorage() { return pointStorage; }
}
