package dev.huey.destroyTheCore.items.misc;

import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InvisPotionGen extends ItemGen {
  public InvisPotionGen() {
    super(
      ItemsManager.ItemKey.INVIS_POTION,
      Material.SPLASH_POTION
    );
  }
  
  @Override
  public void computeMeta(ItemMeta uncastedMeta) {
    PotionMeta meta = (PotionMeta) uncastedMeta;
    
    meta.setColor(Color.fromRGB(255, 255, 255));
    meta.addCustomEffect(new PotionEffect(
      PotionEffectType.INVISIBILITY,
      5 * 60 * 20,
      0,
      false,
      true
    ), true);
  }
}
