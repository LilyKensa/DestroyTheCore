package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.entity.Player;

public class GiveXpResult extends Mission.Result {
  public GiveXpResult() {
    super("give-xp");
  }
  
  @Override
  public void forWinner(Game.Side side) {
    announce(side);
    
    for (Player p : PlayerUtils.getTeammates(side)) {
      p.giveExp(500);
    }
  }
}
