package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.missions.InstantMission;
import dev.huey.destroyTheCore.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeleportCoreMission extends InstantMission {
  public TeleportCoreMission() {
    super("teleport-core");
  }
  
  @Override
  public void run() {
    for (Player p : Bukkit.getOnlinePlayers()) {
      p.teleport(LocationUtils.live(
        LocationUtils.toSpawnPoint(
          LocationUtils.selfSide(DestroyTheCore.game.map.core, p)
        ).add(0, -1, 0)
      ));
    }
  }
}
