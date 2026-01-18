package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.SingleLocationTool;
import dev.huey.destroyTheCore.utils.LocationUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

public class MissionTool extends SingleLocationTool {
  
  public MissionTool() {
    super("mission", Material.BLAZE_ROD, Color.ORANGE);
  }
  
  @Override
  public Location getLoc() {
    return DestroyTheCore.game.map.mission;
  }
  
  @Override
  public void setLoc(Location loc) {
    DestroyTheCore.game.map.mission = LocationUtils.toSpawnPoint(loc);
  }
}
