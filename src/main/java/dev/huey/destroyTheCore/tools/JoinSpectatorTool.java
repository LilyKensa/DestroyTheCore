package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.editorTools.RegionTool;
import dev.huey.destroyTheCore.records.Region;
import org.bukkit.Color;
import org.bukkit.Material;

public class JoinSpectatorTool extends RegionTool {
  
  public JoinSpectatorTool() {
    super("join-spectator", Material.FIREWORK_STAR, Color.WHITE, Color.SILVER);
  }
  
  @Override
  public Region getRegion() {
    return DTC.game.lobby.joinSpectator;
  }
  
  @Override
  public void setRegion(Region region) {
    DTC.game.lobby.joinSpectator = region;
  }
}
