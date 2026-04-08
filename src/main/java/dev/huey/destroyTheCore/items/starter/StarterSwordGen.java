package dev.huey.destroyTheCore.items.starter;

import dev.huey.destroyTheCore.bases.itemGens.StarterItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class StarterSwordGen extends StarterItemGen {
  
  public StarterSwordGen() {
    super(ItemsManager.ItemKey.STARTER_SWORD, Material.WOODEN_SWORD);
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.SHARPNESS, getLevelsBasedOnPhase(), true);
  }
}
