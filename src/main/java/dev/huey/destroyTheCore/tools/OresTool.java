package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
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
    return DestroyTheCore.game.map.ores;
  }
  
  @Override
  public void setList(Set<Pos> list) {
    DestroyTheCore.game.map.ores = list;
  }
}
