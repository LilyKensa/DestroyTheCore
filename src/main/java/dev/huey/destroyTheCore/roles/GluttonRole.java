package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class GluttonRole extends Role {
  public GluttonRole() {
    super(RolesManager.RoleKey.ROYAL);
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

    if (pl.getFoodLevel() >= 18) {
      pl.sendActionBar(TextUtils.$("roles.glutton.skill.not-hungry"));
      pl.setCooldown(Material.KNOWLEDGE_BOOK, 20);
      return;
    }

    int amount = 0;

    int food = 0;
    float saturation = 0;
    for (Player p : PlayerUtils.allGaming()) {
      if (!LocUtils.near(p, pl, 5)) continue;

      int foodStolen = Math.min(10, p.getFoodLevel());
      float saturationStolen = Math.min(4, p.getSaturation());

      p.setFoodLevel(p.getFoodLevel() - foodStolen);
      p.setSaturation(p.getSaturation() - saturationStolen);

      amount++;
    }

    pl.setFoodLevel(pl.getFoodLevel() + food);
    pl.setSaturation(pl.getSaturation() + saturation);

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
  }
}
