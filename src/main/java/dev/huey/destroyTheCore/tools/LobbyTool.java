package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.SingleLocationTool;
import dev.huey.destroyTheCore.utils.LocationUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

public class LobbyTool extends SingleLocationTool {
  
  public LobbyTool() {
    super("lobby", Material.BREEZE_ROD, Color.AQUA);
  }
  
  @Override
  public Location getLoc() {
    return DestroyTheCore.game.lobby.spawn;
  }
  
  @Override
  public void setLoc(Location loc) {
    DestroyTheCore.game.lobby.spawn = LocationUtils.toSpawnPoint(loc);
  }
}
