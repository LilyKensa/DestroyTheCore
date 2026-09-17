package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DTC;
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
      DTC.rolesManager.roles.values().stream()
        .filter(
          r -> r.id != RolesManager.RoleKey.DEFAULT
//            && r.levelReq <= DestroyTheCore.game.stats.get(
//              pl.getUniqueId()
//            ).levels
        )
        .toList()
    );
    
    DTC.rolesManager.setRole(pl, role);
    DTC.game.enforceDisplay(pl);
    DTC.boardsManager.refresh(pl);
  }
}
