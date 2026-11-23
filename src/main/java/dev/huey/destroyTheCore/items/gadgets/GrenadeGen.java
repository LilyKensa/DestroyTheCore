package dev.huey.destroyTheCore.items.gadgets;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GrenadeGen extends UsableItemGen {
  
  static public final int maxLife = 15;
  static public final float explosionPower = 1.6f;
  static public final double damageMultiplier = 0.84;
  static public final double teamDamageMultiplier = 0.68;
  static public final Particle particle = Particle.FLAME;
  
  static Projectile dummy = null;
  
  static public boolean checkExploding(Projectile proj) {
    return proj == dummy;
  }
  
  static class Grenade {
    
    Player owner;
    Location loc;
    
    int life = maxLife;
    
    public Grenade(Player owner, Location loc) {
      this.owner = owner;
      this.loc = loc;
    }
    
    public void explode() {
      dummy = loc.getWorld().spawn(
        loc.clone().add(0, -999, 0),
        SmallFireball.class,
        fireball -> {
          fireball.setShooter(owner);
          fireball.setYield(0);
        }
      );
      
      loc.getWorld().createExplosion(dummy, loc, explosionPower, false, false);
      
      dummy.remove();
      dummy = null;
      
      new ParticleBuilder(Particle.EXPLOSION)
        .allPlayers()
        .location(loc)
        .offset(1.4, 1.4, 1.4)
        .count(10)
        .extra(0.1)
        .spawn();
    }
    
    public void onTick() {
      life--;
      
      if (DTC.ticksManager.isParticleTick()) {
        new ParticleBuilder(Particle.LAVA)
          .allPlayers()
          .location(loc)
          .offset(0.2, 0.2, 0.2)
          .count(4)
          .extra(0)
          .spawn();
        new ParticleBuilder(Particle.FLAME)
          .allPlayers()
          .location(loc)
          .offset(0.8, 0.8, 0.8)
          .count(6)
          .extra(0)
          .spawn();
      }
    }
  }
  
  static Queue<Grenade> grenades = new ArrayDeque<>();
  
  static Set<Projectile> projs = new HashSet<>();
  
  static public boolean checkProj(Projectile proj) {
    return projs.contains(proj);
  }
  
  static public void onProjectileHit(ProjectileHitEvent ev) {
    Projectile proj = ev.getEntity();
    if (!checkProj(proj)) return;
    
    proj.remove();
    
    UUID ownerId = proj.getOwnerUniqueId();
    if (ownerId == null) return;
    Player pl = Bukkit.getPlayer(ownerId);
    if (pl == null || !pl.isOnline()) return;
    
    grenades.add(new Grenade(pl, proj.getLocation()));
  }
  
  static public void onTick() {
    for (Grenade grenade : grenades) {
      grenade.onTick();
    }
    
    while (!grenades.isEmpty() && grenades.peek().life <= 0) {
      Grenade grenade = grenades.poll();
      grenade.explode();
    }
    
    projs.removeIf((proj) -> {
      if (!proj.isValid()) return true;
      
      if (DTC.ticksManager.ticksCount % 2 == 0) {
        new ParticleBuilder(particle)
          .allPlayers()
          .location(proj.getLocation())
          .offset(0, 0, 0)
          .count(1)
          .extra(0)
          .spawn();
      }
      
      return false;
    });
  }
  
  public GrenadeGen() {
    super(ItemsManager.ItemKey.GRENADE, Material.POPPED_CHORUS_FRUIT);
  }
  
  @Override
  public void computeMeta(ItemMeta meta) {
    meta.setMaxStackSize(16);
  }
  
  @Override
  public void use(Player pl, Block block) {
    if (!PlayerUtils.checkHandCooldown(pl)) return;
    PlayerUtils.setHandCooldown(pl, 2 * 20);
    
    PlayerUtils.takeOneItemFromHand(pl);
    
    ThrowableProjectile proj = pl.launchProjectile(
      Snowball.class,
      pl.getLocation().getDirection()
    );
    proj.setItem(ItemStack.empty());
    projs.add(proj);
  }
}
