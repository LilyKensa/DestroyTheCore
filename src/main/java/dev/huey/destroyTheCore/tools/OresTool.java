package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.MultipleLocationsTool;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Set;

public class OresTool extends MultipleLocationsTool {
  public OresTool() {
    super("ores", Material.IRON_PICKAXE, Color.WHITE);
  }
  
  @Override
  public Set<Location> getLocs() {
    return DestroyTheCore.game.map.ores;
  }
  
  @Override
  public void setLocs(Set<Location> locs) {
    DestroyTheCore.game.map.ores = locs;
  }
}
