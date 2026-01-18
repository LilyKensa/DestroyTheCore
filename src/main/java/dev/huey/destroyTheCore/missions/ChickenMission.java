package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class ChickenMission extends Mission implements Listener {
  
  BossBar healthBar;
  Chicken chicken;
  boolean killed = false;
  
  Map<Game.Side, Double> scores = new HashMap<>();
  
  public double getScore(Game.Side side) {
    return scores.getOrDefault(side, 0D);
  }
  
  void addScore(Game.Side side, double amount) {
    scores.put(side, getScore(side) + amount);
  }
  
  void addScore(Player pl, double amount) {
    addScore(DestroyTheCore.game.getPlayerData(pl).side, amount);
  }
  
  public ChickenMission() {
    super("chicken");
  }
  
  public void move() {
    chicken.getPathfinder().moveTo(
      loc.clone().add(RandomUtils.aroundZero(30), 0, RandomUtils.aroundZero(30))
    );
  }
  
  @Override
  public void start() {
    healthBar = BossBar.bossBar(
      TextUtils.$("missions.chicken.boss-bar"),
      1F,
      BossBar.Color.YELLOW,
      BossBar.Overlay.PROGRESS
    );
    for (Player p : Bukkit.getOnlinePlayers()) healthBar.addViewer(p);
    
    chicken = (Chicken) loc.getWorld().spawnEntity(loc, EntityType.CHICKEN);
    chicken.customName(TextUtils.$("missions.chicken.chicken"));
    chicken.setCustomNameVisible(true);
    chicken.setGlowing(true);
    chicken.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(2);
    chicken.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY).setBaseValue(1);
    chicken.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1);
    chicken.getAttribute(Attribute.MAX_HEALTH).setBaseValue(50);
    chicken.setHealth(50);
    
    chicken.getPathfinder().setCanFloat(true);
    
    move();
    
    DestroyTheCore.missionsManager.team.addEntity(chicken);
  }
  
  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent ev) {
    if (!(ev.getDamager() instanceof Player pl)) return;
    if (ev.getEntity().getUniqueId() != chicken.getUniqueId()) return;
    
    healthBar.progress(
      (float) (chicken.getHealth() / chicken.getAttribute(
        Attribute.MAX_HEALTH).getValue())
    );
    
    addScore(pl, ev.getFinalDamage());
  }
  
  @EventHandler
  public void onEntityDeath(EntityDeathEvent ev) {
    if (ev.getEntity().getUniqueId() != chicken.getUniqueId()) return;
    
    ev.getDrops().clear();
    
    killed = true;
    end();
  }
  
  @Override
  public void tick() {
    if (DestroyTheCore.ticksManager.isSeconds()) {
      if (RandomUtils.hit(0.5)) {
        chicken.getPathfinder().moveTo(
          loc.clone().add(RandomUtils.aroundZero(30),
            0,
            RandomUtils.aroundZero(30))
        );
      }
    }
  }
  
  @Override
  public void finish() {
    for (Player p : Bukkit.getOnlinePlayers()) healthBar.removeViewer(p);
    
    double redScore = getScore(Game.Side.RED), greenScore = getScore(
      Game.Side.GREEN
    );
    
    Game.Side winner = Game.Side.SPECTATOR;
    
    if (!killed || redScore == greenScore) {
      chicken.remove();
      
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
