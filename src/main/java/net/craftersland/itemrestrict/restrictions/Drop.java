package net.craftersland.itemrestrict.restrictions;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class Drop implements Listener {

    private final ItemRestrict ir;

    public Drop(ItemRestrict ir) {
        this.ir = ir;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemDrop(PlayerDropItemEvent event) {
        if (ir.getConfigHandler().getBoolean("General.Restrictions.DropBans")) {
            Player p = event.getPlayer();
            ItemStack item = event.getItemDrop().getItemStack();

            MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getType(), item.getDurability(), p.getLocation());

            if (bannedInfo == null) {
                MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Drop, p, item.getType(), item.getDurability(), p.getLocation());

                if (bannedInfo2 != null) {
                    event.setCancelled(true);

                    ir.getSoundHandler().sendPlingSound(p);
                    ir.getConfigHandler().printMessage(p, "chatMessages.dropingRestricted", bannedInfo2.reason);
                }
            }
        }
    }

}
