package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.bases.missions.InstantMission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FreeMoneyMission extends InstantMission {
  
  public FreeMoneyMission() {
    super("free-money");
  }
  
  @Override
  public void run() {
    for (Player p : PlayerUtils.allGaming()) {
      PlayerUtils.give(p, Material.EMERALD, 2);
      PlayerUtils.give(p, Material.GOLD_INGOT, 8);
      PlayerUtils.give(p, Material.IRON_INGOT, 16);
    }
  }
}
