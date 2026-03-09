package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GiveEmeraldResult extends Mission.Result {
  
  public GiveEmeraldResult() {
    super("give-emerald");
  }
  
  @Override
  public void forWinner(Game.Side side) {
    announce(side);
    
    for (Player p : PlayerUtils.getTeammates(side)) {
      PlayerUtils.give(p, Material.EMERALD);
    }
  }
}
