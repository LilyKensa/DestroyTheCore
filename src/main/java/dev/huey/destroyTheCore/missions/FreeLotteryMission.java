package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.bases.missions.InstantMission;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.entity.Player;

public class FreeLotteryMission extends InstantMission {
  
  public FreeLotteryMission() {
    super("free-lottery");
  }
  
  @Override
  public void run() {
    for (Player p : PlayerUtils.allGaming()) {
      PlayerUtils.give(p, ItemsManager.ItemKey.LOTTERY, 8);
    }
  }
}
