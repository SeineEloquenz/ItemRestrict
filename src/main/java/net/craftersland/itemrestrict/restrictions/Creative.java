package net.craftersland.itemrestrict.restrictions;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Creative implements Listener {

    private final ItemRestrict ir;

    public Creative(ItemRestrict ir) {
        this.ir = ir;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onCreativeEvents(InventoryCreativeEvent event) {
        if (ir.getConfigHandler().getBoolean("General.Restrictions.CreativeBans")) {
            ItemStack cursorItem = event.getCursor();

            Player p = (Player) event.getWhoClicked();
            MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, cursorItem.getType(), cursorItem.getDurability(), p.getLocation());

            if (bannedInfo == null) {
                MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Creative, p, cursorItem.getType(), cursorItem.getDurability(), p.getLocation());

                if (bannedInfo2 != null) {
                    event.setCancelled(true);
                    event.setCursor(null);

                    ir.getSoundHandler().sendItemBreakSound(p);
                    ir.getConfigHandler().printMessage(p, "chatMessages.creativeRestricted", bannedInfo2.reason);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemClicked(InventoryClickEvent event) {
        if (ir.getConfigHandler().getBoolean("General.Restrictions.CreativeBans")) {
            event.getSlotType();
            if (event.getCurrentItem() != null) {
                Player p = (Player) event.getWhoClicked();
                if (p.getGameMode() == GameMode.CREATIVE) {
                    ItemStack currentItem = event.getCurrentItem();

                    MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, currentItem.getType(), currentItem.getDurability(), p.getLocation());

                    if (bannedInfo == null) {
                        MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Creative, p, currentItem.getType(), currentItem.getDurability(), p.getLocation());

                        if (bannedInfo2 != null) {
                            event.setCancelled(true);

                            ir.getSoundHandler().sendItemBreakSound(p);
                            ir.getConfigHandler().printMessage(p, "chatMessages.creativeRestricted", bannedInfo2.reason);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();

        if (p.getGameMode() == GameMode.CREATIVE) {
            ItemStack item = p.getItemInHand();

            MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getType(), item.getDurability(), p.getLocation());

            if (bannedInfo == null) {
                MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Creative, p, item.getType(), item.getDurability(), p.getLocation());

                if (bannedInfo2 != null) {
                    event.setCancelled(true);

                    ir.getSoundHandler().sendItemBreakSound(p);
                    ir.getConfigHandler().printMessage(p, "chatMessages.creativeRestricted", bannedInfo2.reason);
                }
            }
        }
    }

}
