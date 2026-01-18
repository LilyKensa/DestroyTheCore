package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
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
    return DestroyTheCore.game.lobby.joinGreen;
  }
  
  @Override
  public void setRegion(Region region) {
    DestroyTheCore.game.lobby.joinGreen = region;
  }
}
