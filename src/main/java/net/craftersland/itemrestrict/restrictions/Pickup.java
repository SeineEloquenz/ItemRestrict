package net.craftersland.itemrestrict.restrictions;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Pickup implements Listener {

    private final ItemRestrict ir;

    public Pickup(ItemRestrict ir) {
        this.ir = ir;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemPickup(PlayerPickupItemEvent event) {
        if (ir.getConfigHandler().getBoolean("General.Restrictions.PickupBans")) {
            Player p = event.getPlayer();
            ItemStack item = event.getItem().getItemStack();

            MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getType(), item.getDurability(), p.getLocation());

            if (bannedInfo == null) {
                MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Pickup, p, item.getType(), item.getDurability(), p.getLocation());

                if (bannedInfo2 != null) {
                    event.setCancelled(true);

                    Location loc = event.getItem().getLocation();
                    event.getItem().teleport(new Location(loc.getWorld(), loc.getX() + getRandomInt(), loc.getY() + getRandomInt(), loc.getZ() + getRandomInt()));

                    ir.getSoundHandler().sendPlingSound(p);
                    ir.getConfigHandler().printMessage(p, "chatMessages.pickupRestricted", bannedInfo2.reason);
                }
            }
        }
    }

    private int getRandomInt() {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(5);
    }

}
