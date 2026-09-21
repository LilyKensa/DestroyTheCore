package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import java.util.*;
import java.util.stream.Collectors;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class GluttonRole extends Role {
  
  static public final int duration = 10 * 20;
  static public final int maxDrain = 3;
  static public final int keepFood = 5;
  
  Map<UUID, Integer> activeTicks = new HashMap<>();
  Map<UUID, BossBar> bossBars = new HashMap<>();
  
  public GluttonRole() {
    super(RolesManager.RoleType.ASSISTANCE, RolesManager.RoleKey.GLUTTON);
    addInfo(Material.COOKED_SALMON);
    addFeature();
    addExclusiveItem(Material.BREEZE_ROD, meta -> {
      meta.addEnchant(Enchantment.RESPIRATION, 1, true);
    }, item -> {
      Consumable consumable = Consumable.consumable()
        .consumeSeconds(0.5f)
        .animation(ItemUseAnimation.DRINK)
        .hasConsumeParticles(false)
        .build();
      item.setData(DataComponentTypes.CONSUMABLE, consumable);
    });
    addSkill(60 * 20, 10);
    addLevelReq(6);
  }
  
  public boolean isAbsorbing(Player pl) {
    return activeTicks.containsKey(pl.getUniqueId());
  }
  
  public void startAbsorbing(Player pl) {
    stopAbsorbing(pl);
    activeTicks.put(pl.getUniqueId(), duration);
    
    BossBar bar = BossBar.bossBar(
      TextUtils.$(
        "roles.glutton.skill.bossbar",
        List.of(
          Placeholder.parsed("time", CoreUtils.toFixed(duration / 20d, 1))
        )
      ),
      1.0f,
      BossBar.Color.PURPLE,
      BossBar.Overlay.PROGRESS
    );
    bossBars.put(pl.getUniqueId(), bar);
    pl.showBossBar(bar);
  }
  
  public void stopAbsorbing(Player pl) {
    activeTicks.remove(pl.getUniqueId());
    BossBar bar = bossBars.remove(pl.getUniqueId());
    if (bar != null) {
      pl.hideBossBar(bar);
    }
  }
  
  public void absorbFor(Player pl) {
    List<Player> targets = PlayerUtils.allGaming().stream()
      .filter(p -> p != pl)
      .filter(p -> LocUtils.near(p, pl, skillRadius))
      .collect(Collectors.toList());
    Collections.shuffle(targets);
    
    Map<Player, Integer> virtualFood = new HashMap<>();
    Map<Player, Float> virtualSatu = new HashMap<>();
    
    int resistance = 0, speed = 0;
    
    for (Player p : targets) {
      virtualFood.put(p, p.getFoodLevel());
      virtualSatu.put(p, p.getSaturation());
      
      if (PlayerUtils.isTeammate(p, pl))
        resistance++;
      else
        speed++;
    }
    
    boolean next = true;
    float drained = 0;
    
    distributionLoop: while (next) {
      next = false;
      
      for (Player p : targets) {
        if (drained >= maxDrain) break distributionLoop;
        
        int currentFood = virtualFood.get(p);
        float currentSatu = virtualSatu.get(p);
        
        if (currentSatu > 0.01) {
          float drain = Math.min(2, currentSatu);
          virtualSatu.put(p, currentSatu - drain);
          drained += drain;
        }
        else if (currentFood > keepFood) {
          virtualFood.put(p, currentFood - 1);
          drained++;
        }
        else {
          continue;
        }
        
        next = true;
      }
    }
    
    if (drained <= 0) {
      pl.sendActionBar(TextUtils.$("roles.glutton.skill.no-target"));
      return;
    }
    
    for (Player p : targets) {
      p.setFoodLevel(virtualFood.get(p));
      p.setSaturation(virtualSatu.get(p));
      
      p.sendActionBar(
        TextUtils.$(
          "roles.glutton.skill.stolen",
          List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
        )
      );
    }
    
    int food = (int) Math.min(drained, 20 - pl.getFoodLevel());
    drained -= food;
    float satu = Math.min(drained, 20 - pl.getSaturation());
    
    pl.setFoodLevel(pl.getFoodLevel() + food);
    pl.setSaturation(pl.getSaturation() + satu);
    
    PlayerUtils.addEffect(
      pl,
      PotionEffectType.RESISTANCE,
      (1 + resistance) * 20,
      resistance
    );
    PlayerUtils.addEffect(
      pl,
      PotionEffectType.SPEED,
      (1 + speed) * 20,
      speed
    );
    
    PlayerUtils.addEffect(
      pl,
      PotionEffectType.WEAKNESS,
      10 * 20,
      2
    );
  }
  
  @Override
  public void onTick(Player pl) {
    if (DTC.ticksManager.isUpdateTick()) {
      if (pl.hasPotionEffect(PotionEffectType.POISON)) {
        PlayerUtils.addPassiveEffect(
          pl,
          PotionEffectType.SLOWNESS,
          15,
          3
        );
      }
    }
    
    UUID uuid = pl.getUniqueId();
    if (!activeTicks.containsKey(uuid)) return;
    
    int remaining = activeTicks.get(uuid) - 1;
    if (remaining <= 0 || !pl.isOnline()) {
      stopAbsorbing(pl);
      return;
    }
    activeTicks.put(uuid, remaining);
    
    BossBar bar = bossBars.get(uuid);
    if (bar != null) {
      float progress = Math.max(
        0.0f,
        Math.min(1.0f, (float) remaining / duration)
      );
      bar.progress(progress);
      bar.name(
        TextUtils.$(
          "roles.glutton.skill.bossbar",
          List.of(
            Placeholder.unparsed("time", CoreUtils.toFixed(remaining / 20d, 1))
          )
        )
      );
    }
    
    if (isAbsorbing(pl) && DTC.ticksManager.isParticleTick()) {
      pl.getWorld().spawnParticle(
        Particle.PORTAL,
        LocUtils.hitboxCenter(pl),
        15,
        6,
        0.5,
        6,
        1
      );
    }
  }
  
  public void onConsume(Player pl, ItemStack item, PlayerItemConsumeEvent ev) {
    if (
      item.getType().isEdible() &&
        item.getType() != Material.POTION
    ) {
      pl.sendActionBar(TextUtils.$("roles.glutton.eat-warning"));
      ev.setCancelled(true);
      return;
    }
    
    if (DTC.rolesManager.checkExclusiveItem(item, id)) {
      if (isAbsorbing(pl)) absorbFor(pl);
      ev.setCancelled(true);
      return;
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.glutton.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component("role", name)
        )
      )
    );
    
    PlayerUtils.addPassiveEffect(
      pl,
      PotionEffectType.RESISTANCE,
      2 * 20,
      1
    );
    
    startAbsorbing(pl);
  }
}
