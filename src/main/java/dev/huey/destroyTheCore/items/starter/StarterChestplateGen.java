package dev.huey.destroyTheCore.items.starter;

import dev.huey.destroyTheCore.bases.itemGens.StarterItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class StarterChestplateGen extends StarterItemGen {
  
  public StarterChestplateGen() {
    super(ItemsManager.ItemKey.STARTER_CHESTPLATE, Material.LEATHER_CHESTPLATE);
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.PROTECTION, getLevelsBasedOnPhase(), true);
  }
}
