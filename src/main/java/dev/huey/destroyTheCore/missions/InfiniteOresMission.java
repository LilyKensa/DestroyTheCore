package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.Constants;
import dev.huey.destroyTheCore.DestroyTheCore;
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
    if (DestroyTheCore.ticksManager.ticksCount % 2 == 0) {
      set(RandomUtils.pick(Constants.ores.keySet().toArray(new Material[0])));
    }
  }
  
  @Override
  public void innerFinish() {
    set(Material.NETHERITE_BLOCK);
  }
}
