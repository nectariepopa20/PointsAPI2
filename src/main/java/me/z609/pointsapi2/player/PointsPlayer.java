package me.z609.pointsapi2.player;

import me.z609.pointsapi2.currency.Currency;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This code is by Z609, and is copyright (C) 2016 Z609. Don't share this
 * code with the public! Thanks!
 */
public class PointsPlayer extends OfflinePointsPlayer {
    private Player bukkitPlayer;
    private Map<Currency, Integer> currencyValues = new HashMap<Currency, Integer>();

    public PointsPlayer(PointsPlayerManager parent, Player bukkitPlayer) {
        super(parent, bukkitPlayer.getUniqueId());
        this.bukkitPlayer = bukkitPlayer;
        for(int i = 0; i < parent.getParent().getCurrencyManager().getCurrencies().size(); i++){
            Currency currency = parent.getParent().getCurrencyManager().getCurrencies().get(i);
            currencyValues.put(currency, parent.getParent().getPointStorage().get(getUniqueId(), currency.getId(), currency.getDefaultValue()));
        }
    }

    @Override
    public String getName(){
        return bukkitPlayer.getName();
    }

    @Override
    public void set(Currency currency, int value){
        if(currencyValues.containsKey(currency))
            currencyValues.remove(currency);
        currencyValues.put(currency, value);
        parent.getParent().getPointStorage().set(getUniqueId(), currency.getId(), value);
    }

    @Override
    public int get(Currency currency){
        if(!currencyValues.containsKey(currency))
            currencyValues.put(currency, currency.getDefaultValue());
        return currencyValues.get(currency);
    }

    public void save(){
        for (Map.Entry<Currency, Integer> entry : currencyValues.entrySet())
            parent.getParent().getPointStorage().set(getUniqueId(), entry.getKey().getId(), entry.getValue());
    }

    public Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    public Map<Currency, Integer> getCurrencyValues() {
        return currencyValues;
    }
}
