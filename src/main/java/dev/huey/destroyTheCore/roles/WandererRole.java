package dev.huey.destroyTheCore.roles;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class WandererRole extends Role {
  
  public static class Elevator {
    
    final double radius = 2.5;
    final double height = 10;
    
    Location loc;
    public int duration = 20 * 20;
    
    public Elevator(Location loc) {
      this.loc = loc;
    }
    
    public boolean contains(Location thatLoc) {
      return (LocationUtils.isSameWorld(loc,
        thatLoc) && thatLoc.getY() >= loc.getY() && thatLoc.getY() <= loc.getY() + height && new Vector(
          thatLoc.getX() - loc.getX(),
          0,
          thatLoc.getZ() - loc.getZ()).lengthSquared() <= radius * radius);
    }
  }
  
  public static List<Elevator> elevators = new ArrayList<>();
  
  public static void onTick() {
    for (Elevator elevator : elevators) {
      if (DestroyTheCore.ticksManager.isUpdateTick()) {
        for (Player p : Bukkit.getOnlinePlayers()) {
          if (!elevator.contains(p.getLocation())) continue;
          
          p.addPotionEffect(
            new PotionEffect(PotionEffectType.LEVITATION, 20, 4, false, true)
          );
          
          if (!p.getLocation().add(0, -0.01, 0).getBlock().isCollidable()) {
            p.addPotionEffect(
              new PotionEffect(PotionEffectType.WEAKNESS, 20, 0, false, true)
            );
            p.addPotionEffect(
              new PotionEffect(PotionEffectType.JUMP_BOOST, 20, 0, false, true)
            );
          }
        }
      }
      
      elevator.duration--;
    }
    elevators.removeIf(e -> e.duration <= 0);
  }
  
  public static void onParticleTick() {
    for (Elevator elevator : elevators) {
      for (double y = 0; y < elevator.height; y += elevator.height / 10D) {
        double currentAngle = Math.toRadians(elevator.loc.getYaw()) + y * 5;
        double currentRadius = elevator.radius * (0.6 + (y / elevator.height) * 0.4);
        
        double x = currentRadius * Math.cos(currentAngle);
        double z = currentRadius * Math.sin(currentAngle);
        
        Location particleLoc = elevator.loc.clone().add(x, y, z);
        
        new ParticleBuilder(Particle.END_ROD).allPlayers().location(
          particleLoc).extra(0).spawn();
      }
      
      elevator.loc.addRotation(10, 0);
    }
  }
  
  public WandererRole() {
    super(RolesManager.RoleKey.WANDERER);
    addInfo(Material.IRON_SWORD);
    addFeature();
    addExclusiveItem(
      Material.IRON_SWORD,
      meta -> {
        meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
      }
    );
    addSkill(60 * 20);
  }
  
  @Override
  public void onTick(Player pl) {
    if (DestroyTheCore.game.map.core == null) return;
    
    if (DestroyTheCore.ticksManager.isUpdateTick()) {
      ItemStack offItem = pl.getInventory().getItemInOffHand();
      
      if (offItem.getType().name().endsWith("SWORD")) {
        pl.addPotionEffect(
          new PotionEffect(PotionEffectType.SPEED, 20, 0, true, false)
        );
        pl.addPotionEffect(
          new PotionEffect(PotionEffectType.REGENERATION, 20, 0, true, false)
        );
      }
      else {
        pl.addPotionEffect(
          new PotionEffect(PotionEffectType.SLOWNESS, 20, 0, true, false)
        );
      }
      
      if (offItem.getType().equals(Material.SHIELD)) {
        pl.getInventory().setItemInOffHand(ItemStack.empty());
        pl.getWorld().dropItemNaturally(LocationUtils.hitboxCenter(pl),
          offItem).setPickupDelay(20);
        
        pl.sendActionBar(TextUtils.$("roles.wanderer.no-shield"));
      }
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    Projectile proj = pl.launchProjectile(
      Snowball.class,
      pl.getLocation().getDirection()
    );
    proj.addScoreboardTag("wanderer-elevator");
  }
  
  public static void onProjectileHit(ProjectileHitEvent ev) {
    Projectile proj = ev.getEntity();
    if (!proj.getScoreboardTags().contains("wanderer-elevator")) return;
    
    elevators.add(new Elevator(proj.getLocation().add(0, -0.1, 0)));
  }
}
