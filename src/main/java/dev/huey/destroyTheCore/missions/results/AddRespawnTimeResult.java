package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.entity.Player;

public class AddRespawnTimeResult extends Mission.Result {
  public AddRespawnTimeResult() {
    super("add-respawn-time");
  }
  
  @Override
  public void forLoser(Game.Side side) {
    announce(side);
    
    for (Player p : PlayerUtils.getTeammates(side)) {
      DestroyTheCore.game.getPlayerData(p).addRespawnTime(20);
    }
  }
}
