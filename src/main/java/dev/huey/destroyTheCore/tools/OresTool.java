package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.editorTools.PosListTool;
import dev.huey.destroyTheCore.records.Pos;
import java.util.Set;
import org.bukkit.Color;
import org.bukkit.Material;

public class OresTool extends PosListTool {
  
  public OresTool() {
    super("ores", Material.IRON_PICKAXE, Color.WHITE);
  }
  
  @Override
  public Set<Pos> getList() {
    return DTC.game.map.ores;
  }
  
  @Override
  public void setList(Set<Pos> list) {
    DTC.game.map.ores = list;
  }
}
