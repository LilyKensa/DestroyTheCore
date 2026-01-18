package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.SingleLocationTool;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

public class StartButtonTool extends SingleLocationTool {
  
  public StartButtonTool() {
    super("start-button", Material.BLAZE_ROD, Color.YELLOW);
  }
  
  @Override
  public Location getLoc() {
    return DestroyTheCore.game.lobby.startButton;
  }
  
  @Override
  public void setLoc(Location loc) {
    DestroyTheCore.game.lobby.startButton = loc;
  }
}
