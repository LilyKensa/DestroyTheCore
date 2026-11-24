package dev.huey.destroyTheCore.items.gadgets;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.records.Pos;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.ParticleUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

public class SensorGen extends UsableItemGen {
  
  static public final int maxLife = 3 * 20;
  static public final double radius = 3.6;
  static public final int glowDuration = 5 * 20;
  static public final Particle particle = Particle.SCRAPE;
  
  static class Sensor {
    
    Game.Side side;
    Location loc;
    
    int life = maxLife;
    
    public Sensor(Game.Side side, Location loc) {
      this.side = side;
      this.loc = loc;
    }
    
    public void emitParticles() {
      ParticleUtils.spiralSphere(loc, radius, particle, 8, 16);
    }
    
    public void markEnemies() {
      for (Player p : PlayerUtils.getEnemies(side)) {
        if (
          !LocUtils.inLive(p) ||
            !LocUtils.near(
              Pos.of(LocUtils.hitboxCenter(p)),
              Pos.of(loc),
              radius
            )
        )
          continue;
        
        if (!p.hasPotionEffect(PotionEffectType.GLOWING)) {
          p.getWorld().playSound(
            p.getLocation(),
            Sound.BLOCK_NOTE_BLOCK_HAT,
            1,
            0.8f
          );
        }
        
        PlayerUtils.addPassiveEffect(
          p,
          PotionEffectType.GLOWING,
          glowDuration
        );
      }
    }
    
    public void onTick() {
      life--;
      
      if (DTC.ticksManager.ticksCount % 12 == 0) {
        loc.setRotation(loc.getYaw() + 1, 0);
      }
      
      if (DTC.ticksManager.isParticleTick()) {
        emitParticles();
      }
      
      if (DTC.ticksManager.isUpdateTick()) {
        markEnemies();
      }
    }
  }
  
  static Queue<Sensor> sensors = new ArrayDeque<>();
  
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
    
    PlayerData data = DTC.game.getPlayerData(pl);
    
    Sensor sensor = new Sensor(data.side, proj.getLocation());
    sensor.markEnemies();
    sensors.add(sensor);
  }
  
  static public void onTick() {
    for (Sensor sensor : sensors) {
      sensor.onTick();
    }
    
    while (!sensors.isEmpty() && sensors.peek().life <= 0) {
      sensors.poll();
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
  
  public SensorGen() {
    super(ItemsManager.ItemKey.SENSOR, Material.DIAMOND_NAUTILUS_ARMOR);
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
