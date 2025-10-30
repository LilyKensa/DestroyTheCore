package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.missions.InstantMission;
import dev.huey.destroyTheCore.records.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DropOresMission extends InstantMission {
  public DropOresMission() {
    super("drop-ores");
  }
  
  @Override
  public void run() {
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerData d = DestroyTheCore.game.getPlayerData(p);
      if (!d.alive) continue;
      if (d.side == Game.Side.SPECTATOR) continue;
      
      DestroyTheCore.inventoriesManager.dropOres(p);
    }
  }
}
