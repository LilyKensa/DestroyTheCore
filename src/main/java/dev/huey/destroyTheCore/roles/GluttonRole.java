package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.*;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class GluttonRole extends Role {
  
  static public final int maxDrain = 75;
  
  public GluttonRole() {
    super(RolesManager.RoleType.ASSISTANCE, RolesManager.RoleKey.GLUTTON);
    addInfo(Material.COOKED_SALMON);
    addFeature();
    addExclusiveItem(Material.COOKED_SALMON, meta -> {
      meta.addEnchant(Enchantment.SHARPNESS, 2, true);
    });
    addSkill(60 * 20);
    addLevelReq(6);
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
  }
  
  @Override
  public void useSkill(Player pl) {
    PlayerData data = DTC.game.getPlayerData(pl);
    
    // if (pl.getFoodLevel() == 20) {
    //   PlayerUtils.setSkillCooldown(pl, 10);
    //
    //   data.skillReloadedMessage = true;
    //   pl.sendActionBar(TextUtils.$("roles.glutton.skill.not-hungry"));
    //   return;
    // }
    
    skillFeedback(pl);
    
    List<Player> targets = PlayerUtils.allGaming().stream()
      .filter(p -> p != pl)
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
        else if (currentFood > 0) {
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
      PlayerUtils.setSkillCooldown(pl, 5 * 20);
      pl.sendActionBar(TextUtils.$("roles.glutton.skill.no-target"));
      return;
    }
    
    for (Player p : targets) {
      p.setFoodLevel(virtualFood.get(p));
      p.setSaturation(virtualSatu.get(p));
    }
    
    int food = (int) Math.min(drained, 20 - pl.getFoodLevel());
    drained -= food;
    float satu = Math.min(drained, 20 - pl.getSaturation());
    
    pl.setFoodLevel(pl.getFoodLevel() + food);
    pl.setSaturation(pl.getSaturation() + satu);
    
    PlayerUtils.addPassiveEffect(
      pl,
      PotionEffectType.RESISTANCE,
      (4 + resistance) * 20,
      resistance
    );
    PlayerUtils.addPassiveEffect(
      pl,
      PotionEffectType.SPEED,
      (4 + speed) * 20,
      speed
    );
    
    PlayerUtils.addPassiveEffect(
      pl,
      PotionEffectType.WEAKNESS,
      10 * 20,
      1
    );
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.glutton.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name),
          Placeholder.component("amount", Component.text(targets.size()))
        )
      )
    );
  }
}
