package dev.huey.destroyTheCore.items.weapons;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class KbStickGen extends ItemGen {
  
  public KbStickGen() {
    super(ItemsManager.ItemKey.KB_STICK, Material.STICK);
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
  }
}
