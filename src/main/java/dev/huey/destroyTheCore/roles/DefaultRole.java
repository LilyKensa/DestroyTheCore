package dev.huey.destroyTheCore.roles;

import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class DefaultRole extends Role {
  
  public DefaultRole() {
    super(RolesManager.RoleKey.DEFAULT);
    addInfo(Material.VILLAGER_SPAWN_EGG);
    addSkill(60);
  }
  
  @Override
  public void useSkill(Player pl) {
    skillFeedback(pl);
    
    PlayerUtils.broadcast(
      TextUtils.$(
        "roles.default.skill.announce",
        List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
      )
    );
  }
}
