package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.missions.InstantMission;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeleportCoreMission extends InstantMission {
  
  public TeleportCoreMission() {
    super("teleport-core");
  }
  
  @Override
  public void run() {
    for (Player p : Bukkit.getOnlinePlayers()) {
      p.teleport(
        LocUtils.live(
          LocUtils.selfSide(
            LocUtils.toSpawnPoint(
              RandomUtils.pick(DestroyTheCore.game.map.spawnpoints)
            ),
            p
          )
        )
      );
    }
  }
}
