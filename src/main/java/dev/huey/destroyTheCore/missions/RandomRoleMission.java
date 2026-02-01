package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Role;
import dev.huey.destroyTheCore.bases.missions.InstantMission;
import dev.huey.destroyTheCore.managers.RolesManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import org.bukkit.entity.Player;

public class RandomRoleMission extends InstantMission {
  
  public RandomRoleMission() {
    super("random-role");
  }
  
  @Override
  public void run() {
    Player pl = RandomUtils.pick(PlayerUtils.allGaming());
    if (pl == null) return;
    
    Role role = RandomUtils.pick(
      DestroyTheCore.rolesManager.roles.values().stream().filter(
        r -> r.id != RolesManager.RoleKey.DEFAULT
      ).toList()
    );
    
    DestroyTheCore.rolesManager.setRole(pl, role);
    DestroyTheCore.game.enforceTeam(pl);
    DestroyTheCore.boardsManager.refresh(pl);
  }
}
