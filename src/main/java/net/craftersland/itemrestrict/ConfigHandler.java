package net.craftersland.itemrestrict;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {

    private final ItemRestrict ir;

    public ConfigHandler(final ItemRestrict ir) {
        this.ir = ir;
        loadConfig();
    }

    public void loadConfig() {
        File pluginFolder = new File("plugins" + System.getProperty("file.separator") + ItemRestrict.pluginName);
        if (!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }
        File configFile = new File("plugins" + System.getProperty("file.separator") + ItemRestrict.pluginName + System.getProperty("file.separator") + "config.yml");
        if (!configFile.exists()) {
            ItemRestrict.log.info("No config file found! Creating new one...");
            ir.saveDefaultConfig();
        }
        try {
            ItemRestrict.log.info("Loading the config file...");
            ir.getConfig().load(configFile);
        } catch (Exception e) {
            ItemRestrict.log.severe("Could not load the config file! You need to regenerate the config! Error: " + e.getMessage());
            e.printStackTrace();
        }
        if (getBoolean("General.EnableOnAllWorlds")) {
            ItemRestrict.log.info("Restrictions enabled on all worlds.");
        } else {
            getWorldsTask();
        }
    }

    public String getString(String key) {
        if (!ir.getConfig().contains(key)) {
            ir.getLogger().severe("Could not locate " + key + " in the config.yml inside of the " + ItemRestrict.pluginName + " folder! (Try generating a new one by deleting the current)");
            return "errorCouldNotLocateInConfigYml:" + key;
        } else {
            return ir.getConfig().getString(key);
        }
    }

    public Integer getInteger(String key) {
        if (!ir.getConfig().contains(key)) {
            ir.getLogger().severe("Could not locate " + key + " in the config.yml inside of the " + ItemRestrict.pluginName + " folder! (Try generating a new one by deleting the current)");
            return null;
        } else {
            return ir.getConfig().getInt(key);
        }
    }

    public Boolean getBoolean(String key) {
        if (!ir.getConfig().contains(key)) {
            ir.getLogger().severe("Could not locate " + key + " in the config.yml inside of the " + ItemRestrict.pluginName + " folder! (Try generating a new one by deleting the current)");
            return null;
        } else {
            return ir.getConfig().getBoolean(key);
        }
    }

    //Send chat messages from config
    public void printMessage(Player p, String messageKey, String reason) {
        if (ir.getConfig().contains(messageKey)) {
            List<String> message = new ArrayList<>();
            message.add(ir.getConfig().getString(messageKey));

            if (reason != null) {
                message.set(0, message.get(0).replaceAll("%reason", "" + reason));
            }

            if (p != null) {
                //Message format
                p.sendMessage(getString("chatMessages.prefix").replaceAll("&", "§") + message.get(0).replaceAll("&", "§"));
            }
        } else {
            ir.getLogger().severe("Could not locate '" + messageKey + "' in the config.yml inside of the ItemRestrict folder!");
            p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + ">> " + ChatColor.RED + "Could not locate '" + messageKey + "' in the config.yml inside of the ItemRestrict folder!");
        }

    }

    //Get worlds
    private void getWorldsTask() {
        //Get worlds to enable restrictions from config
        final List<String> enabledWorlds = ir.getConfig().getStringList("General.Worlds");
        ItemRestrict.log.info("Scanning for loaded worlds in 10 seconds...");

        Bukkit.getScheduler().runTaskLaterAsynchronously(ir, () -> {
            //validate that list
            ItemRestrict.enforcementWorlds = new ArrayList<>();
            ItemRestrict.log.info("Scanning for loaded worlds...");
            for (String worldName : enabledWorlds) {
                World world = ir.getServer().getWorld(worldName);
                if (world == null) {
                    ItemRestrict.log.warning("Error: There's no world named " + worldName + ".  Please update your config.yml.");
                } else {
                    ItemRestrict.enforcementWorlds.add(world);
                }
            }
            if (enabledWorlds.size() == 0) {
                ItemRestrict.log.warning("No worlds found listed in config! Restrictions will not take place!");
            }
            //List the world names found.
            ArrayList<String> worldNames = new ArrayList<>();
            for (World x : ItemRestrict.enforcementWorlds) {
                worldNames.add(x.getName());
            }
            ItemRestrict.log.info("Plugin enabled on worlds: " + worldNames.toString());
        }, 200L);
    }

}
