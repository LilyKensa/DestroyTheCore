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

public class WeaknessArrowGen extends ProjItemGen {
  public WeaknessArrowGen() {
    super(
      ItemsManager.ItemKey.WEAKNESS_ARROW,
      Material.TIPPED_ARROW,
      "weakness-arrow"
    );
  }
  
  @Override
  public void computeMeta(ItemMeta uncastedMeta) {
    PotionMeta meta = (PotionMeta) uncastedMeta;
    
    meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    meta.setColor(Color.fromRGB(80, 25, 80));
    meta.addCustomEffect(new PotionEffect(
      PotionEffectType.WEAKNESS,
      8 * 20,
      0,
      false,
      true
    ), true);
  }
}
