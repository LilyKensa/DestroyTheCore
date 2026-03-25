package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class RoyalRole extends Role {
  public RoyalRole() {
    super(RolesManager.RoleKey.ROYAL);
    addInfo(Material.GOLDEN_HELMET);
    addFeature();
    addExclusiveItem(Material.GOLDEN_SWORD, meta -> {
      meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
    });
    addSkill(180 * 20);
    addLevelReq(6);
  }

  @Override
  public void onTick(Player pl) {
    if (DestroyTheCore.ticksManager.isUpdateTick()) {
      for (Player p : PlayerUtils.getTeammates(pl)) {
        if (DestroyTheCore.game.getPlayerData(p).role.id == this.id) continue;

        if (LocUtils.near(p, pl, 10)) {
          PlayerUtils.addPassiveEffect(
            p,
            PotionEffectType.RESISTANCE,
            20,
            1
          );
        }
      }
    }
  }

  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);

    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.royal.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name)
        )
      )
    );

    for (Player p : PlayerUtils.getTeammates(pl)) {
      if (DestroyTheCore.game.getPlayerData(p).role.id != this.id) {
        PlayerUtils.addPassiveEffect(
          p,
          PotionEffectType.ABSORPTION,
          60 * 20,
          3
        );
      }
    }
  }
}
