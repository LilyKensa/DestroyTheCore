package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class GluttonRole extends Role {
  public GluttonRole() {
    super(RolesManager.RoleKey.GLUTTON);
    addInfo(Material.COOKED_SALMON);
    addFeature();
    addExclusiveItem(Material.COOKED_SALMON, meta -> {
      meta.addEnchant(Enchantment.SHARPNESS, 2, true);
    });
    addSkill(60 * 20);
    addLevelReq(8);
  }
  
  @Override
  public void onTick(Player pl) {
    if (DestroyTheCore.ticksManager.isUpdateTick()) {
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
    skillFeedback(pl);
    
    if (pl.getFoodLevel() == 20) {
      pl.sendActionBar(TextUtils.$("roles.glutton.skill.not-hungry"));
      pl.setCooldown(Material.KNOWLEDGE_BOOK, 20);
      return;
    }
    
    int amount = 0;
    int resistance = 0, speed = 0;
    
    int totalFood = 0;
    float totalSaturation = 0;
    for (Player p : PlayerUtils.allGaming()) {
      if (p == pl) continue;
      if (!LocUtils.near(p, pl, 5)) continue;
      
      int food = Math.min(18, p.getFoodLevel());
      float saturation = Math.min(6, p.getSaturation());
      
      if (food <= 0) continue;
      
      if (PlayerUtils.isTeammate(p, pl))
        resistance++;
      else
        speed++;
      
      p.setFoodLevel(p.getFoodLevel() - food);
      p.setSaturation(p.getSaturation() - saturation);
      
      totalFood += food;
      totalSaturation += saturation;
      
      p.sendActionBar(
        TextUtils.$(
          "roles.glutton.skill.stolen",
          List.of(
            Placeholder.component("player", PlayerUtils.getName(pl))
          )
        )
      );
      
      amount++;
    }
    
    if (amount <= 0) {
      pl.sendActionBar(TextUtils.$("roles.glutton.skill.no-target"));
      pl.setCooldown(Material.KNOWLEDGE_BOOK, 20);
      return;
    }
    
    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.glutton.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name),
          Placeholder.component("amount", Component.text(amount))
        )
      )
    );
    
    pl.setFoodLevel(pl.getFoodLevel() + totalFood);
    pl.setSaturation(pl.getSaturation() + totalSaturation);
    
    PlayerUtils.addPassiveEffect(
      pl,
      PotionEffectType.REGENERATION,
      2 * 20,
      4
    );
    PlayerUtils.addPassiveEffect(
      pl,
      PotionEffectType.REGENERATION,
      10 * 20,
      2
    );
    
    PlayerUtils.addPassiveEffect(
      pl,
      PotionEffectType.RESISTANCE,
      (4 + amount) * 20,
      resistance
    );
    PlayerUtils.addPassiveEffect(
      pl,
      PotionEffectType.RESISTANCE,
      (4 + speed) * 20,
      speed
    );
  }
}
