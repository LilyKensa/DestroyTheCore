package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.entity.Player;

public class ClearXpResult extends Mission.Result {
  
  public ClearXpResult() {
    super("clear-xp");
  }
  
  @Override
  public void forLoser(Game.Side side) {
    announce(side);
    
    for (Player p : PlayerUtils.getTeammates(side)) {
      p.setExperienceLevelAndProgress(0);
    }
  }
}
