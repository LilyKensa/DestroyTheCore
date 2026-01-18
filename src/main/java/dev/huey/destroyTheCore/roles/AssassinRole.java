package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AssassinRole extends Role {
  
  public static final int threshold = 2 * 20;
  
  static Map<UUID, Integer> standingTicks = new HashMap<>();
  
  public static void addStanding(Player pl) {
    standingTicks.put(
      pl.getUniqueId(),
      Math.min(standingTicks.getOrDefault(pl.getUniqueId(), 0) + 1, threshold)
    );
  }
  
  public static void resetStanding(Player pl) {
    standingTicks.put(pl.getUniqueId(), 0);
  }
  
  public static boolean isStanding(Player pl) {
    return (standingTicks.getOrDefault(pl.getUniqueId(), 0) >= threshold);
  }
  
  public static void onPlayerMove(Player pl) {
    resetStanding(pl);
  }
  
  public static void onPlayerShootBow(Player pl, EntityShootBowEvent ev) {
    if (!PlayerUtils.shouldHandle(pl)) return;
    
    if (
      DestroyTheCore.game.getPlayerData(
        pl).role.id == RolesManager.RoleKey.ASSASSIN
    ) {
      if (ev.getConsumable() != null) {
        Item itemEntity = pl.getWorld().dropItem(pl.getEyeLocation(),
          ev.getConsumable());
        itemEntity.setPickupDelay(20);
        itemEntity.setVelocity(ev.getProjectile().getVelocity());
      }
      
      pl.sendActionBar(TextUtils.$("roles.assassin.no-bow"));
      ev.setCancelled(true);
    }
  }
  
  public AssassinRole() {
    super(RolesManager.RoleKey.ASSASSIN);
    addInfo(Material.ENDER_PEARL);
    addFeature();
    addExclusiveItem(
      Material.STONE_SWORD,
      meta -> {
        meta.addEnchant(Enchantment.SMITE, 5, true);
      }
    );
    addSkill(180 * 20);
  }
  
  @Override
  public void onTick(Player pl) {
    if (!DestroyTheCore.game.getPlayerData(pl).alive) return;
    
    addStanding(pl);
    
    if (isStanding(pl)) {
      pl.addPotionEffect(
        new PotionEffect(PotionEffectType.INVISIBILITY, 5, 0, true, false)
      );
    }
    else if (
      pl.getHealth() >= pl.getAttribute(
        Attribute.MAX_HEALTH).getValue() && DestroyTheCore.game.phase != null && DestroyTheCore.game.phase.isAfter(
          Game.Phase.DoubleDamage)
    ) {
      pl.addPotionEffect(
        new PotionEffect(PotionEffectType.INVISIBILITY, 5, 0, true, true)
      );
    }
    
    if (pl.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
      pl.addPotionEffect(
        new PotionEffect(PotionEffectType.STRENGTH, 10, 0, true, false)
      );
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    if (!pl.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
      pl.setCooldown(Material.KNOWLEDGE_BOOK, 0);
      pl.sendActionBar(TextUtils.$("roles.assassin.skill.not-invis"));
      return;
    }
    
    Player nearest = Bukkit.getOnlinePlayers().stream().filter(p -> !p.equals(
      pl) && p.getWorld().equals(pl.getWorld()) && PlayerUtils.shouldHandle(
        p) && DestroyTheCore.game.getPlayerData(p).isGaming()
    ).min(
      Comparator.comparingDouble(p -> p.getLocation().distanceSquared(
        pl.getLocation())
      )
    ).orElse(null);
    
    if (nearest == null) {
      pl.setCooldown(Material.KNOWLEDGE_BOOK, 0);
      pl.sendActionBar(TextUtils.$("roles.assassin.skill.no-target"));
      return;
    }
    
    skillFeedback(pl);
    
    pl.teleport(nearest);
    
    pl.addPotionEffect(
      new PotionEffect(PotionEffectType.INVISIBILITY, 4 * 20, 0, true, false)
    );
    pl.addPotionEffect(
      new PotionEffect(PotionEffectType.STRENGTH, 4 * 20, 1, true, false)
    );
  }
}
