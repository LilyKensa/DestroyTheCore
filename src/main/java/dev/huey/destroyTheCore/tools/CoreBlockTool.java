package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.PosTool;
import dev.huey.destroyTheCore.records.Pos;
import org.bukkit.Color;
import org.bukkit.Material;

public class CoreBlockTool extends PosTool {
  
  public CoreBlockTool() {
    super("core", Material.GOLDEN_SWORD, Color.YELLOW);
  }
  
  @Override
  public Pos getPos() {
    return DestroyTheCore.game.map.core;
  }
  
  @Override
  public void setPos(Pos pos) {
    DestroyTheCore.game.map.core = pos;
  }
}
