package me.z609.pointsapi2.currency;

import me.z609.pointsapi2.PointsAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * This code is by Z609, and is copyright (C) 2016 Z609. Don't share this
 * code with the public! Thanks!
 */
public class CurrencyManager {

    private PointsAPI parent;
    private List<Currency> currencies = new ArrayList<Currency>();

    public CurrencyManager(PointsAPI parent) {
        this.parent = parent;
        FileConfiguration configuration = parent.getConfig();
        ConfigurationSection currencySection = configuration.getConfigurationSection("currencies");
        if (currencySection == null) {
            parent.getLogger().warning("No currencies section was found in config.yml.");
            return;
        }
        for(String s : currencySection.getKeys(false)){
            String key = "currencies." + s + ".";
            String singular = configuration.getString(key + "singular");
            String plural = configuration.getString(key + "plural");
            int defaultValue = configuration.getInt(key + "defaultValue");
            Currency currency = new Currency(this, s, singular, plural, defaultValue);
            currencies.add(currency);
        }
    }

    public Currency getCurrency(String name){
        for(Currency currency : currencies){
            if(currency.getId().equalsIgnoreCase(name)){
                return currency;
            }
        }
        return null;
    }

    public PointsAPI getParent() {
        return parent;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }
}
