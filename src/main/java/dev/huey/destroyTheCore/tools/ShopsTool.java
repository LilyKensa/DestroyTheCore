package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.editorTools.PosListTool;
import dev.huey.destroyTheCore.records.Pos;
import java.util.Set;
import org.bukkit.Color;
import org.bukkit.Material;

public class ShopsTool extends PosListTool {
  
  public ShopsTool() {
    super("shops", Material.VILLAGER_SPAWN_EGG, Color.MAROON);
  }
  
  @Override
  public Set<Pos> getList() {
    return DTC.game.map.shops;
  }
  
  @Override
  public void setList(Set<Pos> list) {
    DTC.game.map.shops = list;
  }
}
