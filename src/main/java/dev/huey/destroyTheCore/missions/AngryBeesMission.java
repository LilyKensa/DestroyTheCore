package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class AngryBeesMission extends Mission implements Listener {
  
  BossBar healthBar;
  Bee queenBee;
  List<Bee> bees = new ArrayList<>();
  
  public AngryBeesMission() {
    super("angry-bees");
  }
  
  public void madAt(Player target) {
    queenBee.getPathfinder().moveTo(target.getEyeLocation());
    for (Bee bee : bees) {
      bee.getPathfinder().moveTo(target.getEyeLocation());
      bee.setAnger(10 * 20);
      bee.setTarget(target);
    }
  }
  
  public void idle() {
    Location targetLoc = loc.clone().add(RandomUtils.aroundZero(30),
      0,
      RandomUtils.aroundZero(30));
    
    queenBee.getPathfinder().moveTo(targetLoc);
    for (Bee bee : bees) {
      bee.getPathfinder().moveTo(targetLoc);
    }
  }
  
  @Override
  public void start() {
    healthBar = BossBar.bossBar(
      TextUtils.$("missions.angry-bees.boss-bar"),
      1F,
      BossBar.Color.YELLOW,
      BossBar.Overlay.PROGRESS
    );
    for (Player p : Bukkit.getOnlinePlayers()) healthBar.addViewer(p);
    
    queenBee = (Bee) loc.getWorld().spawnEntity(loc, EntityType.BEE);
    
    queenBee.customName(TextUtils.$("missions.angry-bees.queen-bee"));
    queenBee.setCustomNameVisible(true);
    
    queenBee.setGlowing(true);
    queenBee.getAttribute(Attribute.SCALE).setBaseValue(2);
    queenBee.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.2);
    queenBee.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY).setBaseValue(1);
    queenBee.getAttribute(Attribute.MAX_HEALTH).setBaseValue(100);
    queenBee.setHealth(100);
    
    for (int i = 0; i < 4; ++i) {
      Bee bee = (Bee) loc.getWorld().spawnEntity(loc, EntityType.BEE);
      bee.customName(TextUtils.$("missions.angry-bees.normal-bee"));
      bee.setCustomNameVisible(true);
      bee.setGlowing(true);
      bee.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.2);
      bee.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY).setBaseValue(1);
      bee.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
      bee.setHealth(20);
      
      DestroyTheCore.missionsManager.team.addEntity(bee);
      bees.add(bee);
    }
    
    idle();
    
    DestroyTheCore.missionsManager.team.addEntity(queenBee);
  }
  
  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent ev) {
    if (!(ev.getDamager() instanceof Player pl)) return;
    if (ev.getEntity().getUniqueId() != queenBee.getUniqueId()) return;
    
    healthBar.progress(
      (float) (queenBee.getHealth() / queenBee.getAttribute(
        Attribute.MAX_HEALTH).getValue())
    );
    
    madAt(pl);
  }
  
  @EventHandler
  public void onEntityDeath(EntityDeathEvent ev) {
    if (!(ev.getEntity() instanceof Bee bee)) return;
    
    List<ItemStack> drops = ev.getDrops();
    
    if (bee.equals(queenBee)) {
      drops.clear();
      drops.add(new ItemStack(Material.EMERALD, 16));
      
      end();
    }
    else if (bees.contains(bee)) {
      drops.clear();
    }
  }
  
  @Override
  public void tick() {
    if (DestroyTheCore.ticksManager.isSeconds()) {
      if (RandomUtils.hit(0.05)) {
        idle();
      }
      else if (RandomUtils.hit(0.8)) {
        Player target = Bukkit.getOnlinePlayers().stream().filter(
          p -> LocationUtils.near(p, queenBee, 30)).min(
            Comparator.comparingDouble(p -> p.getLocation().distanceSquared(
              queenBee.getLocation())
            )
          ).orElse(null);
        
        if (target != null) madAt(target);
      }
    }
  }
  
  @Override
  public void finish() {
    for (Player p : Bukkit.getOnlinePlayers()) healthBar.removeViewer(p);
    
    if (!queenBee.isDead()) queenBee.remove();
    
    for (Bee bee : bees) {
      if (!bee.isDead()) bee.remove();
    }
  }
}
