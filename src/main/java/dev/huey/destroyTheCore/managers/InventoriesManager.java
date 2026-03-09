package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.Constants;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.utils.RandomUtils;
import java.util.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoriesManager {
  
  final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();
  
  /** Store the player's inventory, use {@link #restore} to restore */
  public void store(Player pl) {
    savedInventories.put(pl.getUniqueId(), pl.getInventory().getContents());
    pl.getInventory().clear();
  }
  
  /** Restore the inventory stored by {@link #store} */
  public void restore(Player pl) {
    UUID id = pl.getUniqueId();
    if (!savedInventories.containsKey(id)) return;
    
    pl.getInventory().setContents(savedInventories.get(id));
    savedInventories.remove(id);
  }
  
  final Map<UUID, List<ItemStack>> savedHotbars = new HashMap<>();
  
  /** Save the hotbar, use {@link #restoreHotbar} to restore */
  public void saveHotbar(Player pl) {
    List<ItemStack> hotbar = new ArrayList<>();
    for (int i = 0; i < 9; ++i) {
      hotbar.add(pl.getInventory().getItem(i));
    }
    savedHotbars.put(pl.getUniqueId(), hotbar);
  }
  
  /** Restore the inventory stored by {@link #saveHotbar} */
  public void restoreHotbar(Player pl) {
    List<ItemStack> hotbar = savedHotbars.get(pl.getUniqueId());
    
    for (int i = 0; i < 9; ++i) {
      pl.getInventory().setItem(
        i,
        hotbar == null ? ItemStack.empty() : hotbar.get(i)
      );
    }
    
    savedHotbars.remove(pl.getUniqueId());
  }
  
  /** Remove all the saved inventories */
  public void reset() {
    savedInventories.clear();
  }
  
  /** Remove all items with {@link Enchantment#VANISHING_CURSE} from a player */
  public void applyVanishingCurse(Player pl) {
    PlayerInventory inv = pl.getInventory();
    
    ItemStack[] contents = inv.getContents();
    for (int i = 0; i < contents.length; i++) {
      ItemStack item = contents[i];
      if (
        item != null && item.containsEnchantment(Enchantment.VANISHING_CURSE)
      ) {
        contents[i] = null;
      }
    }
    inv.setContents(contents);
  }
  
  /** @param chance Every item will have this chance to drop */
  public void dropSome(Player pl, double chance) {
    PlayerInventory inv = pl.getInventory();
    
    ItemStack placeholder = DestroyTheCore.itemsManager.gens.get(
      ItemsManager.ItemKey.PLACEHOLDER
    ).getItem();
    
    ItemStack[] contents = inv.getContents();
    for (int i = 0; i < contents.length; i++) {
      ItemStack item = contents[i];
      if (item == null) continue;
      
      if (
        List.of(Material.SHIELD, Material.KNOWLEDGE_BOOK).contains(
          item.getType()
        )
      ) continue;
      if (DestroyTheCore.rolesManager.isExclusiveItem(item)) continue;
      if (
        DestroyTheCore.itemsManager.isGen(
          item
        ) && DestroyTheCore.itemsManager.getGen(item).willNeverDrop()
      ) continue;
      
      if (
        DestroyTheCore.itemsManager.isGen(
          item
        ) && DestroyTheCore.itemsManager.getGen(item).willVanish()
      ) {
        contents[i] = placeholder;
      }
      else if (
        item.getType() == Material.ENCHANTING_TABLE
          || item.getType() == Material.ENDER_CHEST
          || Constants.oreItems.contains(
            item.getType()
          )
          || RandomUtils.hit(chance)
      ) {
        pl.getWorld().dropItemNaturally(pl.getLocation(), item).setPickupDelay(
          20
        );
        contents[i] = placeholder;
      }
    }
    
    inv.setContents(contents);
  }
  
  public void dropOres(Player pl) {
    PlayerInventory inv = pl.getInventory();
    
    ItemStack[] contents = inv.getContents();
    for (int i = 0; i < contents.length; i++) {
      ItemStack item = contents[i];
      if (item == null) continue;
      
      if (Constants.oreItems.contains(item.getType())) {
        pl.getWorld().dropItemNaturally(pl.getLocation(), item).setPickupDelay(
          20
        );
        contents[i] = null;
      }
    }
    inv.setContents(contents);
  }
}
