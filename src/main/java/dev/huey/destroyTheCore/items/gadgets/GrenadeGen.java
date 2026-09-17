package dev.huey.destroyTheCore.items.gadgets;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;

public class GrenadeGen extends UsableItemGen {
  
  public GrenadeGen() {
    super(ItemsManager.ItemKey.GRENADE, Material.POPPED_CHORUS_FRUIT);
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (!PlayerUtils.checkHandCooldown(pl)) return;
    PlayerUtils.setHandCooldown(pl, 2 * 20);
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    Projectile proj = pl.launchProjectile(
      Snowball.class,
      pl.getLocation().getDirection()
    );
    proj.addScoreboardTag("grenade");
  }
  
  static public void onProjectileHit(ProjectileHitEvent ev) {
    Projectile proj = ev.getEntity();
    if (!proj.getScoreboardTags().contains("grenade")) return;
    
    proj.getWorld().createExplosion(proj, 1.6F, false, false);
    proj.remove();
    
    new ParticleBuilder(Particle.EXPLOSION)
      .allPlayers()
      .location(proj.getLocation().add(0, 0.2, 0))
      .offset(1.4, 1.4, 1.4)
      .count(10)
      .extra(0.1).spawn();
  }
}
