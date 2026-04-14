package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;

public class InfiniteOresMission extends TimedMission implements Listener {
  
  static public void set(Material type) {
    centerLoc.clone().add(0, -1, 0).getBlock().setType(type);
  }
  
  static public Material getRandomOre() {
    int rand = RandomUtils.range(100);
    
    if ((rand -= 45) < 0) {
      return Material.IRON_ORE;
    }
    else if ((rand -= 15) < 0) {
      return Material.REDSTONE_ORE;
    }
    else if ((rand -= 15) < 0) {
      return Material.GOLD_ORE;
    }
    else if ((rand -= 12) < 0) {
      return Material.EMERALD_ORE;
    }
    else if ((rand -= 12) < 0) {
      return Material.LAPIS_ORE;
    }
    else if ((rand -= 1) < 0) {
      return Material.DIAMOND_ORE;
    }
    
    return Material.STONE;
  }
  
  static public boolean check(Location toCheck) {
    return LocUtils.isSameBlock(toCheck, centerLoc.clone().add(0, -1, 0));
  }
  
  public InfiniteOresMission() {
    super("infinite-ores", 30 * 20);
  }
  
  @Override
  public void innerStart() {
  }
  
  @Override
  public void innerTick() {
    if (DTC.ticksManager.ticksCount % 2 == 0) {
      set(getRandomOre());
    }
  }
  
  @Override
  public void innerFinish() {
    set(Material.NETHERITE_BLOCK);
  }
}
