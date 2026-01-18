package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.SingleLocationTool;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

public class CoreBlockTool extends SingleLocationTool {
  
  public CoreBlockTool() {
    super("core", Material.GOLDEN_SWORD, Color.YELLOW);
  }
  
  @Override
  public Location getLoc() {
    return DestroyTheCore.game.map.core;
  }
  
  @Override
  public void setLoc(Location loc) {
    DestroyTheCore.game.map.core = loc;
  }
}
