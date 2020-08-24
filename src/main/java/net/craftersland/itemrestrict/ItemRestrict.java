package net.craftersland.itemrestrict;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.craftersland.itemrestrict.restrictions.BlockBreak;
import net.craftersland.itemrestrict.restrictions.Brewing;
import net.craftersland.itemrestrict.restrictions.Crafting;
import net.craftersland.itemrestrict.restrictions.Creative;
import net.craftersland.itemrestrict.restrictions.Drop;
import net.craftersland.itemrestrict.restrictions.OffHandSwap;
import net.craftersland.itemrestrict.restrictions.Ownership;
import net.craftersland.itemrestrict.restrictions.Pickup;
import net.craftersland.itemrestrict.restrictions.Placement;
import net.craftersland.itemrestrict.restrictions.Smelting;
import net.craftersland.itemrestrict.restrictions.Usage;
import net.craftersland.itemrestrict.utils.DisableRecipe;
import net.craftersland.itemrestrict.utils.MaterialCollection;
import net.craftersland.itemrestrict.utils.WearingScanner;
import net.craftersland.itemrestrict.utils.WorldScanner;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemRestrict extends JavaPlugin {
	
	public static Logger log;
	public static final String pluginName = "ItemRestrict";
	
	public static ArrayList<World> enforcementWorlds = new ArrayList<>();
	public static final MaterialCollection ownershipBanned = new MaterialCollection();
	public final MaterialCollection craftingBanned = new MaterialCollection();
	public final MaterialCollection smeltingBanned = new MaterialCollection();
	public final List<String> craftingDisabled = new ArrayList<>();
	public final List<String> smeltingDisabled = new ArrayList<>();
	public final List<Recipe> disabledRecipes = new ArrayList<>();
	public final MaterialCollection brewingBanned = new MaterialCollection();
	public final MaterialCollection wearingBanned = new MaterialCollection();
	public final MaterialCollection creativeBanned = new MaterialCollection();
	public final MaterialCollection usageBanned = new MaterialCollection();
	public final MaterialCollection placementBanned = new MaterialCollection();
	public final MaterialCollection blockBreakBanned = new MaterialCollection();
	public final MaterialCollection pickupBanned = new MaterialCollection();
	public final MaterialCollection dropBanned = new MaterialCollection();
	public final MaterialCollection worldBanned = new MaterialCollection();
	public final Map<Boolean, Integer> worldScanner = new HashMap<>();
	public final Map<Boolean, Integer> wearingScanner = new HashMap<>();
	
	private static ConfigHandler configHandler;
	private static RestrictedItemsHandler restrictedHandler;
	private static WorldScanner ws;
	private static WearingScanner es;
	private static DisableRecipe ds;
	private static SoundHandler sH;
	
	public void onEnable() {
		log = getLogger();
		worldScanner.put(false, 0);
		wearingScanner.put(false, 0);
		
		//Create ItemRestrict plugin folder
    	(new File("plugins"+System.getProperty("file.separator")+"ItemRestrict")).mkdir();
    	
    	//Load Configuration
        configHandler = new ConfigHandler(this);
        //Load Restricted Items
        restrictedHandler = new RestrictedItemsHandler(this);
        //Load Classes
        ws = new WorldScanner(this);
        es = new WearingScanner(this);
        ds = new DisableRecipe(this);
        sH = new SoundHandler(this);
        
        //Register Listeners
    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(new Ownership(this), this);
    	pm.registerEvents(new Crafting(this), this);
    	pm.registerEvents(new Smelting(this), this);
    	pm.registerEvents(new Brewing(this), this);
    	pm.registerEvents(new Creative(this), this);
    	pm.registerEvents(new Usage(this), this);
    	pm.registerEvents(new Placement(this), this);
    	pm.registerEvents(new BlockBreak(this), this);
    	pm.registerEvents(new Pickup(this), this);
    	pm.registerEvents(new Drop(this), this);
    	pm.registerEvents(new OffHandSwap(this), this);
    	CommandHandler cH = new CommandHandler(this);
    	getCommand("itemrestrict").setExecutor(cH);
    	
    	printConsoleStatus();
    	
    	if (configHandler.getBoolean("General.Restrictions.ArmorWearingBans")) {
    		//Start the wearing scanner task
    		es.wearingScanTask();
    	}
    	if (configHandler.getBoolean("General.WorldScannerON")) {
    		//Start the world scanner task
    		ws.worldScanTask();
		}
    	
    	log.info(pluginName + " loaded successfully!");
	}
	
	public void onReload() {
		log.info("Reloading config and RestrictedItems...");
		enforcementWorlds.clear();
		ownershipBanned.clear();
		craftingBanned.clear();
		smeltingBanned.clear();
		craftingDisabled.clear();
		brewingBanned.clear();
		wearingBanned.clear();
		creativeBanned.clear();
		usageBanned.clear();
		placementBanned.clear();
		blockBreakBanned.clear();
		pickupBanned.clear();
		dropBanned.clear();
		worldBanned.clear();
		
		//Load Configuration
        configHandler = new ConfigHandler(this);
        //Load Restricted Items
        restrictedHandler = new RestrictedItemsHandler(this);
        
        //Restore recipes
        ds.restoreRecipes();
        if (configHandler.getBoolean("General.WorldScannerON") && worldScanner.containsKey(false)) {
        	ws.worldScanTask();
        } else if (!configHandler.getBoolean("General.WorldScannerON") && worldScanner.containsKey(true)) {
        	Bukkit.getScheduler().cancelTask(worldScanner.get(true));
        	worldScanner.clear();
        	worldScanner.put(false, 0);
        }
        if (configHandler.getBoolean("General.Restrictions.ArmorWearingBans") && wearingScanner.containsKey(false)) {
        	es.wearingScanTask();
        } else if (!configHandler.getBoolean("General.Restrictions.ArmorWearingBans") && wearingScanner.containsKey(true)) {
        	Bukkit.getScheduler().cancelTask(wearingScanner.get(true));
        	wearingScanner.clear();
        	wearingScanner.put(false, 0);
        }
        
        //Disable Recipes Task
        ds.disableRecipesTask(1);
        
        printConsoleStatus();
        
        log.info("Reload complete!");
	}
	
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		log.info(pluginName + " is disabled!");
	}
	
	private void printConsoleStatus() {
		if (configHandler.getBoolean("General.Restrictions.EnableBrewingBans")) {
    		log.info("Brewing restrictions enabled!");
    	} else {
    		log.info("Brewing restrictions disabled!");
    	}
        if (configHandler.getBoolean("General.Restrictions.ArmorWearingBans")) {
    		log.info("Wearing restrictions enabled!");
    	} else {
    		log.info("Wearing restrictions disabled!");
    	}
        if (configHandler.getBoolean("General.Restrictions.CreativeBans")) {
    		log.info("Creative restrictions enabled!");
    	} else {
    		log.info("Creative restrictions disabled!");
    	}
        if (configHandler.getBoolean("General.Restrictions.PickupBans")) {
    		log.info("Pickup restrictions enabled!");
    	} else {
    		log.info("Pickup restrictions disabled!");
    	}
        if (configHandler.getBoolean("General.Restrictions.DropBans")) {
    		log.info("Drop restrictions enabled!");
    	} else {
    		log.info("Drop restrictions disabled!");
    	}
        if (configHandler.getBoolean("General.Restrictions.BreakBans")) {
    		log.info("Block break restrictions enabled!");
    	} else {
    		log.info("Block break restrictions disabled!");
    	}
        if (configHandler.getBoolean("General.WorldScannerON")) {
    		log.info("WorldScanner is enabled!");
		} else {
			log.info("WorldScanner is disabled!");
		}
	}
	
	//Getting other classes public
	public ConfigHandler getConfigHandler() {
		return configHandler;
	}
	public RestrictedItemsHandler getRestrictedItemsHandler() {
		return restrictedHandler;
	}
	public DisableRecipe getDisableRecipe() {
		return ds;
	}
	public SoundHandler getSoundHandler() {
		return sH;
	}

}
