package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.MultipleLocationsTool;
import java.util.Set;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

public class DiamondsTool extends MultipleLocationsTool {
  
  public DiamondsTool() {
    super("diamonds", Material.DIAMOND_PICKAXE, Color.AQUA);
  }
  
  @Override
  public Set<Location> getLocs() {
    return DestroyTheCore.game.map.diamonds;
  }
  
  @Override
  public void setLocs(Set<Location> locs) {
    DestroyTheCore.game.map.diamonds = locs;
  }
}
