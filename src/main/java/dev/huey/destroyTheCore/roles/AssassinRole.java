package dev.huey.destroyTheCore.roles;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;

public class AssassinRole extends Role {
  
  static public final int threshold = 2 * 20;
  
  static Map<UUID, Integer> standingTicks = new HashMap<>();
  
  static public void addStanding(Player pl) {
    standingTicks.put(
      pl.getUniqueId(),
      Math.min(standingTicks.getOrDefault(pl.getUniqueId(), 0) + 1, threshold)
    );
  }
  
  static public void resetStanding(Player pl) {
    standingTicks.put(pl.getUniqueId(), 0);
  }
  
  static public boolean isStanding(Player pl) {
    return (standingTicks.getOrDefault(pl.getUniqueId(), 0) >= threshold);
  }
  
  static public void onPlayerMove(Player pl) {
    resetStanding(pl);
  }
  
  static public void onPlayerShootBow(Player pl, EntityShootBowEvent ev) {
    if (!PlayerUtils.shouldHandle(pl)) return;
    
    if (
      DestroyTheCore.game.getPlayerData(
        pl
      ).role.id != RolesManager.RoleKey.ASSASSIN
    ) return;
    
    if (ev.getConsumable() != null) {
      Item itemEntity = pl.getWorld().dropItem(
        pl.getEyeLocation(),
        ev.getConsumable()
      );
      
      itemEntity.setPickupDelay(20);
      itemEntity.setVelocity(ev.getProjectile().getVelocity());
      
      if (
        ev.getProjectile() instanceof Arrow arrow
          && arrow.getPickupStatus() != AbstractArrow.PickupStatus.ALLOWED
      ) {
        itemEntity.setCanPlayerPickup(false);
        itemEntity.setCanMobPickup(false);
      }
    }
    
    pl.sendActionBar(TextUtils.$("roles.assassin.no-bow"));
    ev.setCancelled(true);
  }
  
  public AssassinRole() {
    super(RolesManager.RoleType.ATTACKING, RolesManager.RoleKey.ASSASSIN);
    addInfo(Material.ENDER_PEARL);
    addFeature();
    addExclusiveItem(
      Material.STONE_SWORD,
      meta -> {
        meta.addEnchant(Enchantment.SMITE, 5, true);
      }
    );
    addSkill(180 * 20);
    addLevelReq(7);
  }
  
  @Override
  public void onTick(Player pl) {
    if (!DestroyTheCore.game.getPlayerData(pl).alive) return;
    
    addStanding(pl);
    
    if (isStanding(pl)) {
      if (!pl.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
        ParticleUtils.cloud(PlayerUtils.all(), LocUtils.hitboxCenter(pl));
      }
      
      PlayerUtils.addPassiveEffect(
        pl,
        PotionEffectType.INVISIBILITY,
        5,
        1
      );
    }
    else if (
      pl.getHealth() >= AttributeUtils.get(
        pl,
        Attribute.MAX_HEALTH
      )
        && DestroyTheCore.game.phase != null
        && DestroyTheCore.game.phase.isAfter(
          Game.Phase.DoubleDamage
        )
    ) {
      PlayerUtils.addEffect(
        pl,
        PotionEffectType.INVISIBILITY,
        5,
        1,
        true,
        true
      );
    }
    
    if (pl.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
      PlayerUtils.addPassiveEffect(
        pl,
        PotionEffectType.STRENGTH,
        10,
        1
      );
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    
    if (!pl.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
      pl.setCooldown(Material.KNOWLEDGE_BOOK, 10);
      data.skillReloadedMessage = true;
      
      pl.sendActionBar(TextUtils.$("roles.assassin.skill.not-invis"));
      return;
    }
    
    Player nearest = Bukkit.getOnlinePlayers().stream().filter(
      p -> !p.equals(
        pl
      )
        && p.getWorld().equals(pl.getWorld())
        && PlayerUtils.shouldHandle(
          p
        )
        && DestroyTheCore.game.getPlayerData(p).isGaming()
    ).min(
      Comparator.comparingDouble(
        p -> p.getLocation().distanceSquared(
          pl.getLocation()
        )
      )
    ).orElse(null);
    
    if (nearest == null) {
      pl.setCooldown(Material.KNOWLEDGE_BOOK, 10);
      data.skillReloadedMessage = true;
      
      pl.sendActionBar(TextUtils.$("roles.assassin.skill.no-target"));
      return;
    }
    
    skillFeedback(pl);
    
    new ParticleBuilder(Particle.REVERSE_PORTAL)
      .allPlayers()
      .location(pl.getLocation())
      .offset(0.2, 0.3, 0.2)
      .extra(5)
      .count(20)
      .spawn();
    
    pl.teleport(nearest);
    
    PlayerUtils.addEffect(
      pl,
      PotionEffectType.INVISIBILITY,
      4 * 20,
      1
    );
    PlayerUtils.addEffect(
      pl,
      PotionEffectType.STRENGTH,
      4 * 20,
      2
    );
  }
}
