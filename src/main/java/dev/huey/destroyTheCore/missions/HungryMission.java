package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.bases.missions.InstantMission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HungryMission extends InstantMission {
  
  public HungryMission() {
    super("hungry");
  }
  
  @Override
  public void run() {
    for (Player p : Bukkit.getOnlinePlayers()) {
      p.setFoodLevel(2);
      p.setSaturation(0);
    }
  }
}
