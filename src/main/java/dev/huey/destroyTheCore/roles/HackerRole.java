package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.records.SideData;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class HackerRole extends Role {
  public HackerRole() {
    super(RolesManager.RoleType.ASSISTANCE, RolesManager.RoleKey.HACKER);
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
    if (DTC.ticksManager.isUpdateTick()) {
      if (
        PlayerUtils.shouldHandle(pl)
          && pl.hasPotionEffect(PotionEffectType.INVISIBILITY)
      ) {
        pl.removePotionEffect(PotionEffectType.INVISIBILITY);
        
        pl.sendActionBar(TextUtils.$("roles.hacker.invis-warning"));
      }
    }
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    PlayerData data = DTC.game.getPlayerData(pl);
    
    boolean self = LocUtils.canAccess(data.side, pl.getLocation());
    
    SideData sd = DTC.game.getSideData(
      self ? data.side : data.side.opposite()
    );
    
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
        "roles.hacker.skill.announce",
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
