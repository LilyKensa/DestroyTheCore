package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SwapPosMission extends TimedMission {
  
  public SwapPosMission() {
    super("swap-pos", 10 * 20);
  }
  
  @Override
  public void innerStart() {
  }
  
  @Override
  public void innerTick() {
  }
  
  public Player randomPlayer(Game.Side side) {
    return RandomUtils.pick(PlayerUtils.getTeammates(side));
  }
  
  @Override
  public void innerFinish() {
    Player a = randomPlayer(Game.Side.RED), b = randomPlayer(Game.Side.GREEN);
    if (a == null || b == null) return;
    
    Location al = a.getLocation(), bl = b.getLocation();
    
    a.teleport(bl);
    b.teleport(al);
  }
}
