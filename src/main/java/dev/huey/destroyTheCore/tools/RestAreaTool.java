package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.SingleLocationTool;
import dev.huey.destroyTheCore.utils.LocationUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

public class RestAreaTool extends SingleLocationTool {
  public RestAreaTool() {
    super("rest-area", Material.STONE_SWORD, Color.GRAY);
  }
  
  @Override
  public Location getLoc() {
    return DestroyTheCore.game.map.restArea;
  }
  
  @Override
  public void setLoc(Location loc) {
    DestroyTheCore.game.map.restArea = LocationUtils.toSpawnPoint(loc);
  }
}
