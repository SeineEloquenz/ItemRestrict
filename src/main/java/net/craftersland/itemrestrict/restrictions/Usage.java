package net.craftersland.itemrestrict.restrictions;

import net.craftersland.itemrestrict.ItemRestrict;
import net.craftersland.itemrestrict.RestrictedItemsHandler.ActionType;
import net.craftersland.itemrestrict.utils.MaterialData;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class Usage implements Listener {

    private final ItemRestrict ir;
    private final Set<String> safety = new HashSet<>();

    public Usage(ItemRestrict ir) {
        this.ir = ir;
    }

    private boolean isEventSafe(final String pN) {
        if (safety.contains(pN)) {
            return false;
        }
        safety.add(pN);
        Bukkit.getScheduler().runTaskLaterAsynchronously(ir, () -> safety.remove(pN), 1L);
        return true;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        ItemStack item;
        ItemStack item2 = null;
        if (!isEventSafe(event.getPlayer().getName())) {
            return;
        }
        item = p.getInventory().getItemInMainHand();
        item2 = p.getInventory().getItemInOffHand();
        Block interactigBlock = event.getClickedBlock();
        MaterialData bannedInfoInteractingBlock = null;
        if (interactigBlock != null) {
            bannedInfoInteractingBlock = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, interactigBlock.getType(), interactigBlock.getData(), p.getLocation());
        }

        if (bannedInfoInteractingBlock != null) {
            event.setCancelled(true);
            ir.getSoundHandler().sendPlingSound(p);
            ir.getConfigHandler().printMessage(p, "chatMessages.ussageRestricted", bannedInfoInteractingBlock.reason);
        } else {
            if (!event.isBlockInHand()) {
                if (ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getType(), item.getDurability(), p.getLocation()) == null) {
                    MaterialData bannedInfoMainHand = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, item.getType(), item.getDurability(), p.getLocation());
                    if (bannedInfoMainHand != null) {
                        event.setCancelled(true);
                        Bukkit.getScheduler().runTask(ir, () -> {
                            ItemStack handItem = p.getInventory().getItemInMainHand();
                            p.getWorld().dropItem(p.getLocation(), handItem);
                            p.getInventory().setItemInMainHand(null);
                            p.closeInventory();
                            p.updateInventory();
                        });

                        ir.getSoundHandler().sendPlingSound(p);
                        ir.getConfigHandler().printMessage(p, "chatMessages.ussageRestricted", bannedInfoMainHand.reason);
                    }
                }
                if (ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getType(), item.getDurability(), p.getLocation()) == null) {
                    MaterialData bannedInfoOffHand = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, item2.getType(), item2.getDurability(), p.getLocation());
                    if (bannedInfoOffHand != null) {
                        event.setCancelled(true);
                        Bukkit.getScheduler().runTask(ir, () -> {
                            ItemStack handItem = p.getInventory().getItemInOffHand();
                            p.getWorld().dropItem(p.getLocation(), handItem);
                            p.getInventory().setItemInOffHand(null);
                            p.closeInventory();
                            p.updateInventory();
                        });

                        ir.getSoundHandler().sendPlingSound(p);
                        ir.getConfigHandler().printMessage(p, "chatMessages.ussageRestricted", bannedInfoOffHand.reason);
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemHeldSwitch(PlayerItemHeldEvent event) {
        int newSlot = event.getNewSlot();
        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItem(newSlot);

        if (item != null) {
            if (!item.getType().isBlock()) {
                MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getType(), item.getDurability(), p.getLocation());

                if (bannedInfo == null) {
                    MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, item.getType(), item.getDurability(), p.getLocation());

                    if (bannedInfo2 != null) {
                        p.getWorld().dropItem(p.getLocation(), item);
                        p.getInventory().setItem(newSlot, null);
                        p.updateInventory();

                        ir.getSoundHandler().sendPlingSound(p);
                        ir.getConfigHandler().printMessage(p, "chatMessages.ussageRestricted", bannedInfo2.reason);
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player p = (Player) event.getDamager();
            ItemStack item = p.getItemInHand();
            MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, item.getType(), item.getDurability(), p.getLocation());

            if (bannedInfo != null) {
                event.setCancelled(true);
            }
        } else {
            if (event.getDamager() instanceof Projectile) {
                Projectile pr = (Projectile) event.getDamager();
                if (pr.getShooter() instanceof Player) {
                    Player p = (Player) pr.getShooter();
                    ItemStack item = p.getItemInHand();
                    MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Usage, p, item.getType(), item.getDurability(), p.getLocation());

                    if (bannedInfo != null) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
