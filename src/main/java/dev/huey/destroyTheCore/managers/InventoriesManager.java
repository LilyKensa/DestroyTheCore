package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.Constants;
import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.utils.RandomUtils;
import java.util.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class InventoriesManager {
  
  final Map<UUID, ItemStack[]> savedInventories = new HashMap<>();
  
  /** Store the player's inventory, use {@link #restore} to restore */
  public void store(Player pl) {
    if (savedInventories.containsKey(pl.getUniqueId())) return;
    
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
    
    ItemStack placeholder = DTC.itemsManager.gens.get(
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
      if (DTC.rolesManager.isExclusiveItem(item)) continue;
      if (
        DTC.itemsManager.isGen(
          item
        ) && DTC.itemsManager.getGen(item).willNeverDrop()
      ) continue;
      
      if (
        DTC.itemsManager.isGen(
          item
        ) && DTC.itemsManager.getGen(item).willVanish()
      ) {
        contents[i] = placeholder;
      }
      else if (
        item.getType() == Material.ENCHANTING_TABLE ||
          item
            .getType() == Material.ENDER_CHEST ||
          Constants.oreItems.contains(
            item.getType()
          ) ||
          RandomUtils.hit(chance)
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
  
  public void dropXp(Player pl) {
    int currentLevel = pl.getLevel();
    if (currentLevel <= 3) return;
    
    int xpBefore = pl.calculateTotalExperiencePoints();
    
    pl.setLevel(currentLevel - 3);
    
    int toDrop = xpBefore - pl.calculateTotalExperiencePoints();
    
    while (toDrop > 0) {
      int value = Math.min(RandomUtils.range(2, 72), toDrop);
      
      ExperienceOrb orb = pl.getWorld().spawn(
        pl.getLocation(),
        ExperienceOrb.class
      );
      orb.setExperience(value);
      
      double angle = RandomUtils.nextDouble() * 2 * Math.PI;
      double radius = Math.sqrt(RandomUtils.nextDouble()) * 0.5;
      orb.setVelocity(
        new Vector(
          Math.cos(angle) * radius,
          0.2 + RandomUtils.nextDouble() * 0.3,
          Math.sin(angle) * radius
        )
      );
      
      toDrop -= value;
    }
  }
}
