package dev.huey.destroyTheCore.items.weapons;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class TridentGen extends ItemGen {
  
  public TridentGen() {
    super(ItemsManager.ItemKey.TRIDENT, Material.TRIDENT);
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.LOYALTY, 3, true);
  }
}
