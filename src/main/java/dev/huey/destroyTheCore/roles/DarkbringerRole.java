package dev.huey.destroyTheCore.roles;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class DarkbringerRole extends Role {
  
  static final int maxCombo = 5;
  
  static boolean inDarkness(Player pl) {
    Block block = pl.getLocation().getBlock();
    return block.getLightFromSky() < 10 && block.getLightFromBlocks() < 7;
  }
  
  static class Curse {
    public UUID playerId;
    public int time;
    public int combo = 0;
    
    public Curse(Player target) {
      playerId = target.getUniqueId();
      resetTime();
    }
    
    public Player getPlayer() {
      return Bukkit.getPlayer(playerId);
    }
    
    public boolean inTime() {
      return DTC.ticksManager.ticksCount - time < 15 * 20;
    }
    
    public void resetTime() {
      time = DTC.ticksManager.ticksCount;
    }
    
    public void increase() {
      combo++;
      resetTime();
    }
  }
  
  static Map<UUID, Curse> map = new HashMap<>();
  
  static Curse getCurse(Player pl) {
    return map.get(pl.getUniqueId());
  }
  
  static Curse addCurse(Player pl, Player target) {
    Curse curse = new Curse(target);
    map.put(pl.getUniqueId(), curse);
    return curse;
  }
  
  static void resetCurse(Player pl) {
    map.remove(pl.getUniqueId());
  }
  
  public DarkbringerRole() {
    super(RolesManager.RoleType.ASSISTANCE, RolesManager.RoleKey.DARKBRINGER);
    addInfo(Material.WARDEN_SPAWN_EGG);
    addFeature();
    addExclusiveItem(
      Material.ECHO_SHARD,
      meta -> {
        meta.addEnchant(Enchantment.PROTECTION, 2, true);
      }
    );
    addSkill(10 * 20, 10);
  }
  
  @Override
  public ItemsManager.ItemKey defBoots() {
    return ItemsManager.ItemKey.DARKBRINGER_BOOTS;
  }
  
  @Override
  public void onTick(Player pl) {
    if (DTC.ticksManager.isUpdateTick()) {
      if (inDarkness(pl)) {
        PlayerUtils.addPassiveEffect(
          pl,
          PotionEffectType.RESISTANCE,
          30,
          1
        );
      }
    }
    
    if (DTC.ticksManager.isParticleTick()) {
      if (inDarkness(pl)) {
        new ParticleBuilder(Particle.SQUID_INK)
          .allPlayers()
          .location(pl.getLocation().add(0, 0.1, 0))
          .offset(0.2, 0.4, 0.2)
          .count(2)
          .extra(0)
          .spawn();
      }
      
      Curse curse = getCurse(pl);
      if (curse != null && curse.inTime()) {
        Player target = curse.getPlayer();
        if (target != null) {
          new ParticleBuilder(Particle.SQUID_INK)
            .receivers(pl)
            .location(target.getLocation().add(0, 0.1, 0))
            .offset(0.2, 0.4, 0.2)
            .count(curse.combo * 2)
            .extra(0)
            .spawn();
        }
      }
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    PlayerData data = DTC.game.getPlayerData(pl);
    
    Player target = null;
    
    Curse curse = getCurse(pl);
    if (curse != null && !curse.inTime()) curse = null;
    
    if (curse != null) {
      Player lastTarget = curse.getPlayer();
      
      if (lastTarget != null && LocUtils.near(lastTarget, pl, skillRadius)) {
        target = lastTarget;
      }
    }
    
    if (target == null) {
      for (Player e : PlayerUtils.getEnemies(pl)) {
        if (LocUtils.near(e, pl, skillRadius)) {
          target = e;
          break;
        }
      }
    }
    
    if (target == null) {
      PlayerUtils.setSkillCooldown(pl, 10);
      data.skillReloadedMessage = true;
      
      pl.sendActionBar(TextUtils.$("roles.darkbringer.skill.no-target"));
      return;
    }
    
    if (curse == null) {
      curse = addCurse(pl, target);
    }
    
    curse.increase();
    
    Player finalTarget = target;
    PlayerUtils.delayAssign(
      pl,
      target,
      Particle.SQUID_INK,
      () -> {
        PlayerUtils.addPassiveEffect(
          finalTarget,
          PotionEffectType.DARKNESS,
          (inDarkness(pl) ? 10 : 3) * 20,
          1
        );
      }
    );
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.darkbringer.skill.announce" + (curse.combo >= maxCombo ? "-boom"
          : ""),
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name),
          Placeholder.component("target", PlayerUtils.getName(target)),
          Placeholder.component("count", Component.text(curse.combo)),
          Placeholder.component("max", Component.text(maxCombo))
        )
      )
    );
    
    if (curse.combo >= 5) {
      resetCurse(pl);
      
      Location locA = pl.getEyeLocation();
      Location locB = LocUtils.hitboxCenter(target);
      
      double dist = locA.distance(locB);
      double speed = 0.8;
      
      int totalSteps = (int) (dist / speed);
      Vector dir = locB.toVector().subtract(locA.toVector()).normalize();
      Vector step = dir.multiply(speed);
      
      Location loc = locA.clone();
      
      for (int i = 0; i < totalSteps + 5; ++i) {
        new ParticleBuilder(Particle.SONIC_BOOM)
          .allPlayers()
          .location(loc)
          .count(1)
          .extra(0)
          .spawn();
        
        loc.add(step);
      }
      
      target.damage(
        16,
        DamageSource.builder(DamageType.SONIC_BOOM)
          .withCausingEntity(pl)
          .withDirectEntity(pl)
          .build()
      );
      
      target.setVelocity(
        target.getVelocity()
          .add(dir.multiply(0.05))
      );
      
      PlayerUtils.setSkillCooldown(pl, 100 * 20);
    }
  }
}
