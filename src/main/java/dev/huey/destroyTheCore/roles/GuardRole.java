package dev.huey.destroyTheCore.roles;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.AttributeUtils;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class GuardRole extends Role {
  static public void onEnchant(Player pl) {
    for (Player e : PlayerUtils.getEnemies(pl)) {
      if (DestroyTheCore.game.getPlayerData(e).role.id == RolesManager.RoleKey.GUARD)
        send(e, TextUtils.$("roles.guard.enchanting-alarm"));
    }
  }
  
  public GuardRole() {
    super(RolesManager.RoleKey.GUARD);
    addInfo(Material.SHIELD);
    addFeature();
    addExclusiveItem(
      Material.SHIELD,
      meta -> {
        meta.addAttributeModifier(
          Attribute.MAX_HEALTH,
          AttributeUtils.addition("max-health", EquipmentSlotGroup.OFFHAND, 10)
        );
      }
    );
    addSkill(300 * 20);
  }
  
  @Override
  public void onTick(Player pl) {
    if (DestroyTheCore.game.map.core == null) return;
    
    if (
      LocationUtils.near(pl.getLocation(), LocationUtils.live(
        LocationUtils.selfSide(DestroyTheCore.game.map.core, pl)
      ), 15)
    ) {
      if (DestroyTheCore.ticksManager.ticksCount % 25 == 0) // Regen 2 = every 25 ticks
        pl.addPotionEffect(new PotionEffect(
          PotionEffectType.REGENERATION,
          50,
          1,
          true,
          false
        ));
    }
    
    if (
      LocationUtils.near(pl.getLocation(), LocationUtils.live(
        LocationUtils.enemySide(DestroyTheCore.game.map.core, pl)
      ), 15)
    ) {
      pl.sendActionBar(TextUtils.$("roles.guard.near-enemy-core-warning", List.of(
        Placeholder.unparsed("role", name)
      )));
      if (DestroyTheCore.ticksManager.isUpdateTick()) // Wither 3 = every 10 ticks
        pl.addPotionEffect(new PotionEffect(
          PotionEffectType.WITHER,
          60,
          2,
          true,
          false
        ));
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    pl.addPotionEffect(new PotionEffect(
      PotionEffectType.STRENGTH,
      5 * 20,
      0,
      false,
      true
    ));
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$("roles.guard.skill.announce", List.of(
        Placeholder.component("player", PlayerUtils.getName(pl)),
        Placeholder.unparsed("role", name)
      ))
    );
    
    if (DestroyTheCore.game.map.core == null) return;
    
    boolean found = false;
    for (Player e : PlayerUtils.getEnemies(pl)) {
      if (
        LocationUtils.near(e.getLocation(), LocationUtils.live(
          LocationUtils.selfSide(DestroyTheCore.game.map.core, pl)
        ), 15)
      ) {
        found = true;
        
        send(pl, TextUtils.$("roles.guard.skill.found-enemy.self", List.of(
          Placeholder.component("enemy", PlayerUtils.getName(e))
        )));
        send(e, TextUtils.$("roles.guard.skill.found-enemy.enemy", List.of(
          Placeholder.component("player", PlayerUtils.getName(pl))
        )));
        
        PlayerUtils.delayAssign(pl, e, Particle.ENCHANTED_HIT, () -> {
          e.addPotionEffect(new PotionEffect(
            PotionEffectType.GLOWING,
            60 * 20,
            0,
            true,
            false
          ));
          e.addPotionEffect(new PotionEffect(
            PotionEffectType.SLOWNESS,
            5 * 20,
            4,
            true,
            false
          ));
          e.addPotionEffect(new PotionEffect(
            PotionEffectType.MINING_FATIGUE,
            5 * 20,
            4,
            true,
            false
          ));
          
          new ParticleBuilder(Particle.ELDER_GUARDIAN)
            .receivers(e)
            .location(e.getLocation())
            .spawn();
          e.playSound(
            e.getLocation(),
            Sound.ENTITY_ELDER_GUARDIAN_CURSE,
            1,
            1
          );
        });
      }
    }
    
    if (!found)
      send(pl, TextUtils.$("roles.guard.skill.found-enemy.none"));
  }
}
