package me.z609.pointsapi2.message;

import me.z609.pointsapi2.PointsAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

/** Loads editable player-facing strings from plugins/PointsAPI/messages.yml. */
public final class MessageManager {
    private final PointsAPI plugin;
    private FileConfiguration messages;

    public MessageManager(PointsAPI plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) plugin.saveResource("messages.yml", false);
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public void send(CommandSender recipient, String key, String... replacements) {
        String message = messages.getString(key, "&cMissing message: " + key);
        recipient.sendMessage(format(message, replacements));
    }

    public void sendList(CommandSender recipient, String key, String... replacements) {
        List<String> lines = messages.getStringList(key);
        for (String line : lines) recipient.sendMessage(format(line, replacements));
    }

    public String format(String message, String... replacements) {
        for (int i = 0; i + 1 < replacements.length; i += 2)
            message = message.replace(replacements[i], replacements[i + 1]);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
