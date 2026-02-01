package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.PosTool;
import dev.huey.destroyTheCore.records.Pos;
import dev.huey.destroyTheCore.utils.LocUtils;
import org.bukkit.Color;
import org.bukkit.Material;

public class RestAreaTool extends PosTool {
  
  public RestAreaTool() {
    super("rest-area", Material.STONE_SWORD, Color.GRAY);
  }
  
  @Override
  public Pos getPos() {
    return DestroyTheCore.game.map.restArea;
  }
  
  @Override
  public void setPos(Pos loc) {
    DestroyTheCore.game.map.restArea = LocUtils.toSpawnPoint(loc);
  }
}
