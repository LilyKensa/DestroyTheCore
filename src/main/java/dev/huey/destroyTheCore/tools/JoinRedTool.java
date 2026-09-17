package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.editorTools.RegionTool;
import dev.huey.destroyTheCore.records.Region;
import org.bukkit.Color;
import org.bukkit.Material;

public class JoinRedTool extends RegionTool {
  
  public JoinRedTool() {
    super("join-red", Material.REDSTONE, Color.RED, Color.FUCHSIA);
  }
  
  @Override
  public Region getRegion() {
    return DTC.game.lobby.joinRed;
  }
  
  @Override
  public void setRegion(Region region) {
    DTC.game.lobby.joinRed = region;
  }
}
