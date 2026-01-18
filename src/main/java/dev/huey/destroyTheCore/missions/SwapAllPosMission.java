package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SwapAllPosMission extends TimedMission {
  
  public SwapAllPosMission() {
    super("swap-all-pos", 10 * 20);
  }
  
  @Override
  public void innerStart() {
  }
  
  @Override
  public void innerTick() {
  }
  
  @Override
  public void innerFinish() {
    List<Player> players = PlayerUtils.allGaming();
    List<Location> pos = new ArrayList<>(
      players.stream().map(Player::getLocation).toList()
    );
    Collections.shuffle(pos);
    
    for (int i = 0; i < players.size(); ++i) {
      players.get(i).teleport(pos.get(i));
    }
  }
}
