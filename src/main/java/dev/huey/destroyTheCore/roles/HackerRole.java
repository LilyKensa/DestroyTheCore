package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.records.SideData;
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

public class HackerRole extends Role {
  public HackerRole() {
    super(RolesManager.RoleKey.ROYAL);
    addInfo(Material.MUSIC_DISC_5);
    addFeature();
    addExclusiveItem(Material.SPYGLASS, meta -> {
      meta.addEnchant(Enchantment.BANE_OF_ARTHROPODS, 5, true);
    });
    addSkill(200 * 20);
    addLevelReq(11);
  }


  @Override
  public void onTick(Player pl) {
    if (DestroyTheCore.ticksManager.isUpdateTick()) {
      pl.removePotionEffect(PotionEffectType.INVISIBILITY);
    }
  }

  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);

    PlayerData data = DestroyTheCore.game.getPlayerData(pl);

    boolean self = LocUtils.canAccess(data.side, pl.getLocation());

    SideData sd = DestroyTheCore.game.getSideData(data.side);

    if (self) {
      sd.addImmuneChance(
        SideData.ImmuneChance.Reason.ROLE_HACKER,
        pl,
        0.3333,
        30 * 20
      );
    }
    else {
      sd.addExtraDamage(
        SideData.ExtraDamage.Reason.ROLE_HACKER,
        pl,
        30 * 20
      );
    }

    PlayerUtils.auraBroadcast(
      pl.getLocation(),
      10,
      TextUtils.$(
        "roles.glutton.skill.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed("role", name),
          Placeholder.component(
            "type",
            TextUtils.$("roles.hacker.skill.types." + (self ? "self" : "enemy"))
          ),
          Placeholder.component(
            "side",
            (self ? data.side : data.side.opposite()).titleComp()
          )
        )
      )
    );
  }
}
