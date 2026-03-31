package dev.huey.destroyTheCore.items.starter;

import dev.huey.destroyTheCore.bases.itemGens.StarterItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class StarterHelmetGen extends StarterItemGen {
  
  public StarterHelmetGen() {
    super(ItemsManager.ItemKey.STARTER_HELMET, Material.LEATHER_HELMET);
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.addEnchant(Enchantment.PROTECTION, getLevelsBasedOnPhase(), true);
  }
}
