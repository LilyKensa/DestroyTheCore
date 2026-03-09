package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.PosListTool;
import dev.huey.destroyTheCore.records.Pos;
import java.util.Set;
import org.bukkit.Color;
import org.bukkit.Material;

public class DiamondsTool extends PosListTool {
  
  public DiamondsTool() {
    super("diamonds", Material.DIAMOND_PICKAXE, Color.AQUA);
  }
  
  @Override
  public Set<Pos> getList() {
    return DestroyTheCore.game.map.diamonds;
  }
  
  @Override
  public void setList(Set<Pos> list) {
    DestroyTheCore.game.map.diamonds = list;
  }
}
