package net.craftersland.itemrestrict.utils;

import net.craftersland.itemrestrict.ItemRestrict;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class WorldScanner {

    private final ItemRestrict ir;
    private int nextChunkPercentile = 0;

    public WorldScanner(ItemRestrict ir) {
        this.ir = ir;
    }

    private static String getFriendlyLocationString(Location location) {
        return location.getWorld().getName() + "(" + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + ")";
    }

    public void worldScanTask() {
        //start the repeating scan for banned items in loaded chunks
        //runs every minute and scans 5% of loaded chunks.
        int delay = ir.getConfigHandler().getInteger("General.WorldScannerDelay") * 60;

        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(ir, () -> {
            ItemRestrict.log.info("WorldScanner Task Started...");
            ArrayList<World> worlds;
            if (ir.worldBanned.size() > 0) {
                if (ItemRestrict.enforcementWorlds.size() == 0) {
                    worlds = (ArrayList<World>) Bukkit.getServer().getWorlds();
                } else {
                    worlds = ItemRestrict.enforcementWorlds;
                }

                for (World world : worlds) {
                    try {
                        Chunk[] chunks = world.getLoadedChunks();

//scan 5% of chunks each pass
                        int firstChunk = (int) (chunks.length * (nextChunkPercentile / 100f));
                        int lastChunk = (int) (chunks.length * ((nextChunkPercentile + 5) / 100f));

//for each chunk to be scanned
                        for (int j = firstChunk; j < lastChunk; j++) {
                            Chunk chunk = chunks[j];

//scan all its blocks for removable blocks
                            for (int x = 0; x < 16; x++) {
                                for (int y = 0; y < chunk.getWorld().getMaxHeight(); y++) {
                                    for (int z = 0; z < 16; z++) {
                                        final Block block = chunk.getBlock(x, y, z);
                                        removeBlock(block);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        ItemRestrict.log.warning("World Scanner Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                nextChunkPercentile += 5;
                if (nextChunkPercentile >= 100) nextChunkPercentile = 0;
                ItemRestrict.log.info("WorldScanner Task Ended.");
            }
        }, 20L * delay, 20L * delay);
        ir.worldScanner.clear();
        ir.worldScanner.put(true, task.getTaskId());
    }

    private void removeBlock(final Block block) {
        MaterialData materialInfo = new MaterialData(block.getType(), block.getData(), null, null);
        MaterialData bannedInfo = ir.worldBanned.Contains(materialInfo);
        boolean removeSkull = false;
        if (bannedInfo == null) {
            if (ir.getConfigHandler().getBoolean("General.RemoveSkulls")) {
                if (block.getType() == Material.LEGACY_SKULL) {
                    removeSkull = true;
                }
            }
        }
        if (bannedInfo != null || removeSkull) {
            Bukkit.getScheduler().runTask(ir, () -> block.setType(Material.AIR));
            String msg;
            if (bannedInfo != null) {
                msg = bannedInfo.toString();
            } else {
                msg = "skull";
            }
            ItemRestrict.log.info("Removed " + msg + " @ " + getFriendlyLocationString(block.getLocation()));
        }
    }

}
