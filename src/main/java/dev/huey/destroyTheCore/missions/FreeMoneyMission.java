package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.bases.missions.InstantMission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FreeMoneyMission extends InstantMission {
  public FreeMoneyMission() {
    super("free-money");
  }
  
  @Override
  public void run() {
    for (Player p : PlayerUtils.allGaming()) {
      p.give(new ItemStack(Material.EMERALD, 2));
      p.give(new ItemStack(Material.GOLD_INGOT, 8));
      p.give(new ItemStack(Material.IRON_INGOT, 16));
    }
  }
}
