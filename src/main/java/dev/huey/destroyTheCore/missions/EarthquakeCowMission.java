package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class EarthquakeCowMission extends Mission implements Listener {
  static public Location getFloorBlock(Location originalLoc) {
    Location loc = LocationUtils.toBlockCenter(originalLoc);
    
    while (
      loc.getY() > loc.getWorld().getMinHeight() &&
      loc.getY() < loc.getWorld().getMaxHeight() &&
      loc.getBlock().isCollidable()
    ) {
      loc.add(0, 1, 0);
    }
    
    while (
      loc.getY() > loc.getWorld().getMinHeight() &&
      loc.getY() < loc.getWorld().getMaxHeight() &&
      !loc.getBlock().isCollidable()
    ) {
      loc.add(0, -1, 0);
    }
    
    return loc;
  }
  
  static public void flyBlock(Location loc, double strength) {
    Block block = loc.getBlock();
    if (block.getType().isAir() || !block.getType().isSolid()) return;
    if (block.getType().equals(Material.NETHERITE_BLOCK)) return;
    
    BlockData data = block.getBlockData();
    BlockState state = block.getState();
    block.setType(Material.AIR);
    
    FallingBlock fallingBlock = (FallingBlock) loc.getWorld().spawnEntity(
      loc.clone().add(0, 0.01, 0),
      EntityType.FALLING_BLOCK
    );
    fallingBlock.setBlockData(data);
    fallingBlock.setBlockState(state);
    
    fallingBlock.setVelocity(new Vector(0, strength, 0));
  }
  
  static public Set<Vector> getMidpointCirclePoints(int radius) {
    Set<Vector> points = new HashSet<>();
    if (radius <= 0) return points;
    
    int x = radius;
    int z = 0;
    int err = 0;
    
    BiConsumer<Integer, Integer> addPoints = (xi, zi) -> {
      points.add(new Vector(xi, 0, zi));
      points.add(new Vector(-xi, 0, zi));
      points.add(new Vector(xi, 0, -zi));
      points.add(new Vector(-xi, 0, -zi));
      points.add(new Vector(zi, 0, xi));
      points.add(new Vector(-zi, 0, xi));
      points.add(new Vector(zi, 0, -xi));
      points.add(new Vector(-zi, 0, -xi));
    };
    
    // Core Midpoint Circle Logic
    while (x >= z) {
      addPoints.accept(x, z);
      
      z++;
      err += 1 + 2 * z;
      if (2 * (err - x) + 1 > 0) {
        x--;
        err += 1 - 2 * x;
      }
    }
    
    return points;
  }
  
  public void pound(Location center) {
    final int maxRadius = 10;
    
    Set<Vector> history = new HashSet<>();
    
    new BukkitRunnable() {
      private int r = 0;
      
      @Override
      public void run() {
        r += 1;
        
        if (r > maxRadius) {
          this.cancel();
          return;
        }
        
        for (Vector offset : getMidpointCirclePoints(r)) {
          if (history.contains(offset)) continue;
          
          Location flyLoc = center.clone().add(offset.getX(), 0, offset.getZ());
          flyBlock(
            getFloorBlock(flyLoc),
            0.1 + 0.3 * r / maxRadius
          );
          history.add(offset);
          
          for (Player p : flyLoc.getNearbyPlayers(1)) {
            if (!PlayerUtils.shouldHandle(p)) continue;
            if (DestroyTheCore.game.getPlayerData(p).side == Game.Side.SPECTATOR) continue;
            
            p.damage(
              (maxRadius - r) * 0.5,
              DamageSource.builder(DamageType.MOB_ATTACK)
                .withDirectEntity(cow)
                .withCausingEntity(cow)
                .build()
            );
            p.setVelocity(
              p.getVelocity().add(
                p.getLocation()
                  .subtract(cow.getLocation())
                  .toVector()
                  .normalize()
                  .multiply(0.5)
                  .add(new Vector(0, 0.1, 0))
              )
            );
          }
        }
      }
    }.runTaskTimer(DestroyTheCore.instance, 0, 1);
  }
  
  BossBar healthBar;
  Cow cow;
  boolean killed = false;
  
  int earthquakeCooldown = 0;
  public void addCooldown() {
    earthquakeCooldown += RandomUtils.range(5 * 20, 20 * 20 + 1);
  }
  
  Map<Game.Side, Double> scores = new HashMap<>();
  public double getScore(Game.Side side) {
    return scores.getOrDefault(side, 0D);
  }
  void addScore(Game.Side side, double amount) {
    scores.put(
      side,
      getScore(side) + amount
    );
  }
  void addScore(Player pl, double amount) {
    addScore(DestroyTheCore.game.getPlayerData(pl).side, amount);
  }
  
  public EarthquakeCowMission() {
    super("earthquake-cow");
  }
  
  public void move() {
    cow.getPathfinder().moveTo(loc.clone().add(
      RandomUtils.aroundZero(30),
      0,
      RandomUtils.aroundZero(30)
    ));
  }
  
  @Override
  public void start() {
    healthBar = BossBar.bossBar(
      TextUtils.$("missions.earthquake-cow.boss-bar"),
      1F,
      BossBar.Color.YELLOW,
      BossBar.Overlay.PROGRESS
    );
    for (Player p : Bukkit.getOnlinePlayers())
      healthBar.addViewer(p);
    
    cow = (Cow) loc.getWorld().spawnEntity(
      loc,
      EntityType.COW
    );
    
    cow.customName(TextUtils.$("missions.earthquake-cow.cow"));
    cow.setCustomNameVisible(true);
    
    cow.setGlowing(true);
    cow.getAttribute(Attribute.SCALE).setBaseValue(2);
    cow.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.2);
    cow.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY).setBaseValue(1);
    cow.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1);
    cow.getAttribute(Attribute.MAX_HEALTH).setBaseValue(150);
    cow.setHealth(150);
    
    cow.getPathfinder().setCanFloat(true);
    
    move();
    addCooldown();
    
    DestroyTheCore.missionsManager.team.addEntity(cow);
  }
  
  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent ev) {
    if (!(ev.getDamager() instanceof Player pl)) return;
    if (ev.getEntity().getUniqueId() != cow.getUniqueId()) return;
    
    healthBar.progress((float) (
      cow.getHealth() / cow.getAttribute(Attribute.MAX_HEALTH).getValue()
    ));
    
    addScore(pl, ev.getFinalDamage());
    
    earthquakeCooldown -= 3 * 20;
  }
  
  @EventHandler
  public void onEntityDeath(EntityDeathEvent ev) {
    if (ev.getEntity().getUniqueId() != cow.getUniqueId()) return;
    
    ev.getDrops().clear();
    
    killed = true;
    end();
  }
  
  @Override
  public void tick() {
    if (DestroyTheCore.ticksManager.isSeconds()) {
      if (RandomUtils.hit(0.5)) {
        move();
      }
    }
    
    earthquakeCooldown--;
    
    if (earthquakeCooldown <= 0) {
      pound(cow.getLocation());
      
      addCooldown();
    }
  }
  
  @Override
  public void finish() {
    for (Player p : Bukkit.getOnlinePlayers())
      healthBar.removeViewer(p);
    
    double redScore = getScore(Game.Side.RED),
      greenScore = getScore(Game.Side.GREEN);
    
    Game.Side winner = Game.Side.SPECTATOR;
    
    if (!killed || redScore == greenScore) {
      cow.remove();
      
      declareDraw();
      return;
    }
    else if (redScore > greenScore) {
      winner = Game.Side.RED;
    }
    else if (greenScore > redScore) {
      winner = Game.Side.GREEN;
    }
    
    declareWinner(winner);
  }
}
