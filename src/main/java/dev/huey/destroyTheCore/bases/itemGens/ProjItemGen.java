package dev.huey.destroyTheCore.bases.itemGens;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.EventsManager;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjItemGen extends ItemGen {
  
  String tag;
  
  /** @param tag Scoreboard tag used for arrow entities */
  public ProjItemGen(ItemsManager.ItemKey id, Material iconType, String tag) {
    super(id, iconType);
    this.tag = tag;
  }
  
  /** Used in {@link EventsManager#onEntityShootBow} */
  public void outerOnEntityShootBow(EntityShootBowEvent ev) {
    if (!checkItem(ev.getConsumable())) return;
    
    ev.getProjectile().addScoreboardTag(tag);
    onEntityShootBow(ev);
  }
  
  /** @implNote Optional - When this custom arrow is shot */
  public void onEntityShootBow(EntityShootBowEvent ev) {
  }
  
  /** Used in {@link EventsManager#onProjectileHit} */
  public void outerOnProjectileHit(ProjectileHitEvent ev) {
    Projectile proj = ev.getEntity();
    if (!proj.getScoreboardTags().contains(tag)) return;
    
    Entity victim = ev.getHitEntity();
    
    onProjectileHit(ev);
    
    if (victim == null) {
      new ParticleBuilder(Particle.WHITE_SMOKE).allPlayers().location(
        ev.getEntity().getLocation()).count(5).extra(0).spawn();
      
      proj.remove();
    }
  }
  
  /** @implNote Optional - When this custom arrow hit the ground */
  public void onProjectileHit(ProjectileHitEvent ev) {
  }
}
