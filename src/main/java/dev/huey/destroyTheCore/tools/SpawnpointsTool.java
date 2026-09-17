package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.editorTools.PosListTool;
import dev.huey.destroyTheCore.records.Pos;
import java.util.Set;
import org.bukkit.Color;
import org.bukkit.Material;

public class SpawnpointsTool extends PosListTool {
  public SpawnpointsTool() {
    super("spawnpoints", Material.NETHERITE_SWORD, Color.BLACK);
  }
  
  @Override
  public Set<Pos> getList() {
    return DTC.game.map.spawnpoints;
  }
  
  @Override
  public void setList(Set<Pos> list) {
    DTC.game.map.spawnpoints = list;
  }
}
