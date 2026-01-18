package dev.huey.destroyTheCore.items.projectiles;

import dev.huey.destroyTheCore.bases.itemGens.ProjItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonArrowGen extends ProjItemGen {
  
  public PoisonArrowGen() {
    super(
      ItemsManager.ItemKey.POISON_ARROW,
      Material.TIPPED_ARROW,
      "poison-arrow"
    );
  }
  
  @Override
  public void computeMeta(ItemMeta uncastedMeta) {
    PotionMeta meta = (PotionMeta) uncastedMeta;
    
    meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    meta.setColor(Color.fromRGB(75, 125, 0));
    meta.addCustomEffect(
      new PotionEffect(PotionEffectType.POISON, 8 * 20, 0, false, true),
      true
    );
  }
}
