package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import io.papermc.paper.entity.TeleportFlag;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

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
    List<Location> pos = players.stream()
      .map(Player::getLocation)
      .collect(Collectors.toCollection(ArrayList::new));
    
    for (int i = pos.size() - 1; i > 0; --i) {
      int j = RandomUtils.range(i);
      Collections.swap(pos, i, j);
    }
    
    for (int i = 0; i < players.size(); ++i) {
      players.get(i).teleport(
        pos.get(i),
        PlayerTeleportEvent.TeleportCause.PLUGIN,
        TeleportFlag.EntityState.RETAIN_VEHICLE,
        TeleportFlag.EntityState.RETAIN_OPEN_INVENTORY
      );
    }
  }
}
