package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.editorTools.PosTool;
import dev.huey.destroyTheCore.records.Pos;
import org.bukkit.Color;
import org.bukkit.Material;

public class StartButtonTool extends PosTool {
  
  public StartButtonTool() {
    super("start-button", Material.BLAZE_ROD, Color.YELLOW);
  }
  
  @Override
  public Pos getPos() {
    return DTC.game.lobby.startButton;
  }
  
  @Override
  public void setPos(Pos pos) {
    DTC.game.lobby.startButton = pos;
  }
}
