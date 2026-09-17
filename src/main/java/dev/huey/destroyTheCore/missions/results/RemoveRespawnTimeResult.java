package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.entity.Player;

public class RemoveRespawnTimeResult extends Mission.Result {
  
  public RemoveRespawnTimeResult() {
    super("remove-respawn-time", true);
  }
  
  @Override
  public void forWinner(Game.Side side) {
    outro(side);
    
    for (Player p : PlayerUtils.getTeammates(side)) {
      DTC.game.getPlayerData(p).addRespawnTime(-20);
    }
  }
}
