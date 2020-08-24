package net.craftersland.itemrestrict.utils;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import net.craftersland.itemrestrict.ItemRestrict;

public class DisableRecipe {
	
	private final ItemRestrict ir;
	
	public DisableRecipe(ItemRestrict ir) {
		this.ir = ir;
		
		disableRecipesTask(5);
	}
	
	private void removeRecipe(ItemStack is) {
        Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
        Recipe recipe;
        while (it.hasNext()) {
            recipe = it.next();
            if (recipe != null && recipe.getResult().isSimilar(is)) {
            	ir.disabledRecipes.add(recipe);
                it.remove();
            }
        }
    }
	
	public void disableRecipesTask(int delay) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(ir, () -> {
            if (!ir.craftingDisabled.isEmpty()) {
                for (String s : ir.craftingDisabled) {
                    String[] s1 = s.split(":");
                    try {
                        String name = s1[0];
                        Material m = Material.getMaterial(name);
ItemStack is = new ItemStack(m);
                        if (!s1[1].contains("*")) {
short b = Short.parseShort(s1[1]);
is.setDurability(b);
                        }

                        removeRecipe(is);
                    } catch (Exception e) {
                        ItemRestrict.log.warning("Failed to disable crafting for item: " + s1[0] + ":" + s1[1] + " . Error: " + e.getMessage());
                    }
                }
            }
        }, delay * 20L);
	}
	
	public void restoreRecipes() {
		Bukkit.getScheduler().runTaskAsynchronously(ir, () -> {
            if (!ir.disabledRecipes.isEmpty()) {
                for (Recipe r : ir.disabledRecipes) {
                    try {
                        Bukkit.addRecipe(r);
                    } catch (Exception e) {
                        ItemRestrict.log.warning("Failed to restore disabled recipe for: " + r.getResult().getType().toString() + " .Error: " + e.getMessage());
                    }
                }
                ir.disabledRecipes.clear();
            }
        });
	}

}
