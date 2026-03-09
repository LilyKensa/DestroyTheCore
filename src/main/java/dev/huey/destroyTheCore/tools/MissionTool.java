package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.PosTool;
import dev.huey.destroyTheCore.records.Pos;
import dev.huey.destroyTheCore.utils.LocUtils;
import org.bukkit.Color;
import org.bukkit.Material;

public class MissionTool extends PosTool {
  
  public MissionTool() {
    super("mission", Material.BLAZE_ROD, Color.ORANGE);
  }
  
  @Override
  public Pos getPos() {
    return DestroyTheCore.game.map.mission;
  }
  
  @Override
  public void setPos(Pos pos) {
    DestroyTheCore.game.map.mission = LocUtils.toSpawnPoint(pos);
  }
}
