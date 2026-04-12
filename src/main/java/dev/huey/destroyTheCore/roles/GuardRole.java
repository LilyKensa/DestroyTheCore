package dev.huey.destroyTheCore.roles;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.Pos;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class GuardRole extends Role {
  
  static public void onEnchant(Player pl) {
    for (Player e : PlayerUtils.getEnemies(pl)) {
      if (
        DestroyTheCore.game.getPlayerData(
          e
        ).role.id == RolesManager.RoleKey.GUARD
      ) {
        send(e, TextUtils.$("roles.guard.enchanting-alarm"));
      }
    }
  }
  
  public GuardRole() {
    super(RolesManager.RoleType.DEFENSE, RolesManager.RoleKey.GUARD);
    addInfo(Material.SHIELD);
    addFeature();
    addExclusiveItem(
      Material.SHIELD,
      meta -> {
        // meta.addAttributeModifier(
        //   Attribute.MAX_HEALTH,
        //   AttributeUtils.addition("max-health", EquipmentSlotGroup.OFFHAND, 10)
        // );
      }
    );
    addSkill(300 * 20);
  }
  
  @Override
  public void onTick(Player pl) {
    if (DestroyTheCore.game.map.core == null) return;
    if (!LocUtils.inLive(pl)) return;
    
    if (
      LocUtils.near(
        Pos.of(pl),
        LocUtils.selfSide(DestroyTheCore.game.map.core, pl),
        15
      )
    ) {
      if (DestroyTheCore.ticksManager.ticksCount % 25 == 0) {
        
        PlayerUtils.addPassiveEffect(
          pl,
          PotionEffectType.REGENERATION,
          50,
          2
        ); // Regen 2 = every 25 ticks
      }
    }
    
    if (
      LocUtils.near(
        Pos.of(pl),
        LocUtils.enemySide(DestroyTheCore.game.map.core, pl),
        15
      )
    ) {
      pl.sendActionBar(
        TextUtils.$(
          "roles.guard.near-enemy-core-warning",
          List.of(Placeholder.unparsed("role", name))
        )
      );
      if (DestroyTheCore.ticksManager.isUpdateTick()) {
        PlayerUtils.addEffect(
          pl,
          PotionEffectType.WITHER,
          60,
          3
        ); // Wither 3 = every 10 ticks
      }
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    PlayerUtils.addEffect(
      pl,
      PotionEffectType.STRENGTH,
      5 * 20,
      1
    );
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.guard.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name)
        )
      )
    );
    
    if (DestroyTheCore.game.map.core == null) return;
    
    boolean found = false;
    for (Player e : PlayerUtils.getEnemies(pl)) {
      if (!LocUtils.inLive(e)) continue;
      
      if (
        LocUtils.near(
          Pos.of(e),
          LocUtils.selfSide(DestroyTheCore.game.map.core, pl),
          15
        )
      ) {
        found = true;
        
        send(
          pl,
          TextUtils.$(
            "roles.guard.skill.found-enemy.self",
            List.of(Placeholder.component("enemy", PlayerUtils.getName(e)))
          )
        );
        send(
          e,
          TextUtils.$(
            "roles.guard.skill.found-enemy.enemy",
            List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
          )
        );
        
        PlayerUtils.delayAssign(
          pl,
          e,
          Particle.ENCHANTED_HIT,
          () -> {
            PlayerUtils.glow(e, 60 * 20);
            PlayerUtils.addPassiveEffect(
              e,
              PotionEffectType.SLOWNESS,
              5 * 20,
              5
            );
            PlayerUtils.addPassiveEffect(
              e,
              PotionEffectType.MINING_FATIGUE,
              5 * 20,
              5
            );
            
            new ParticleBuilder(Particle.ELDER_GUARDIAN)
              .receivers(e)
              .location(e.getLocation())
              .spawn();
            e.playSound(
              e.getLocation(),
              Sound.ENTITY_ELDER_GUARDIAN_CURSE,
              1, // Volume
              1 // Pitch
            );
          }
        );
      }
    }
    
    if (!found) {
      PlayerUtils.setHandCooldown(pl, skillCooldown / 2);
      send(pl, TextUtils.$("roles.guard.skill.found-enemy.none"));
    }
  }
}
