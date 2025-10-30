package dev.huey.destroyTheCore.bases.itemGens;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjItemGen extends ItemGen {
  String tag;
  
  public ProjItemGen(ItemsManager.ItemKey id, Material iconType, String tag) {
    super(id, iconType);
    this.tag = tag;
  }
  
  public void outerOnEntityShootBow(EntityShootBowEvent ev) {
    if (!checkItem(ev.getConsumable())) return;
    
    ev.getProjectile().addScoreboardTag(tag);
    onEntityShootBow(ev);
  }
  
  public void onEntityShootBow(EntityShootBowEvent ev) {
  
  }
  
  public void outerOnProjectileHit(ProjectileHitEvent ev) {
    Projectile proj = ev.getEntity();
    if (!proj.getScoreboardTags().contains(tag)) return;
    
    Entity victim = ev.getHitEntity();
    
    onProjectileHit(ev);
    
    if (victim == null) {
      new ParticleBuilder(Particle.WHITE_SMOKE)
        .allPlayers()
        .location(ev.getEntity().getLocation())
        .count(5)
        .extra(0)
        .spawn();
      
      proj.remove();
    }
  }
  
  public void onProjectileHit(ProjectileHitEvent ev) {
  
  }
}
