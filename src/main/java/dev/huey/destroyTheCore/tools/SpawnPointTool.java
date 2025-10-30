package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.SingleLocationTool;
import dev.huey.destroyTheCore.utils.LocationUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

public class SpawnPointTool extends SingleLocationTool {
  public SpawnPointTool() {
    super("spawn-point", Material.NETHERITE_SWORD, Color.BLACK);
  }
  
  @Override
  public Location getLoc() {
    return DestroyTheCore.game.map.spawnPoint;
  }
  
  @Override
  public void setLoc(Location loc) {
    DestroyTheCore.game.map.spawnPoint = LocationUtils.toSpawnPoint(loc);
  }
}
