package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.editorTools.RegionTool;
import dev.huey.destroyTheCore.records.Region;
import org.bukkit.Color;
import org.bukkit.Material;

public class JoinGreenTool extends RegionTool {
  
  public JoinGreenTool() {
    super("join-green", Material.EMERALD, Color.LIME, Color.GREEN);
  }
  
  @Override
  public Region getRegion() {
    return DTC.game.lobby.joinGreen;
  }
  
  @Override
  public void setRegion(Region region) {
    DTC.game.lobby.joinGreen = region;
  }
}
