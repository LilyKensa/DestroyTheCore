package dev.huey.destroyTheCore.items.starter;

import dev.huey.destroyTheCore.bases.itemGens.StarterItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class StarterBootsGen extends StarterItemGen {
  
  public StarterBootsGen() {
    super(ItemsManager.ItemKey.STARTER_BOOTS, Material.LEATHER_BOOTS);
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.PROTECTION, getLevelsBasedOnPhase(), true);
  }
}
