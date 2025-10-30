package dev.huey.destroyTheCore.items.projectiles;

import dev.huey.destroyTheCore.bases.itemGens.ProjItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IceArrowGen extends ProjItemGen {
  public IceArrowGen() {
    super(
      ItemsManager.ItemKey.ICE_ARROW,
      Material.TIPPED_ARROW,
      "ice-arrow"
    );
  }
  
  @Override
  public void computeMeta(ItemMeta uncastedMeta) {
    PotionMeta meta = (PotionMeta) uncastedMeta;
    
    meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    meta.setColor(Color.fromRGB(100, 255, 255));
    meta.addCustomEffect(new PotionEffect(
      PotionEffectType.SLOWNESS,
      5 * 20,
      0,
      false,
      true
    ), true);
  }
  
  @Override
  public void onProjectileHit(ProjectileHitEvent ev) {
    Entity victim = ev.getHitEntity();
    if (victim == null) return;
    
    victim.setFreezeTicks(20 * 20);
  }
}
