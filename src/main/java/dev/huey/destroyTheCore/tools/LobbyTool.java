package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.editorTools.PosTool;
import dev.huey.destroyTheCore.records.Pos;
import dev.huey.destroyTheCore.utils.LocUtils;
import org.bukkit.Color;
import org.bukkit.Material;

public class LobbyTool extends PosTool {
  
  public LobbyTool() {
    super("lobby", Material.BREEZE_ROD, Color.AQUA);
  }
  
  @Override
  public Pos getPos() {
    return DTC.game.lobby.spawn;
  }
  
  @Override
  public void setPos(Pos pos) {
    DTC.game.lobby.spawn = LocUtils.toSpawnPoint(pos);
  }
}
