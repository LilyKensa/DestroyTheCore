package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.MultipleLocationsTool;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Set;

public class ShopsTool extends MultipleLocationsTool {
  public ShopsTool() {
    super("shops", Material.VILLAGER_SPAWN_EGG, Color.MAROON);
  }
  
  @Override
  public Set<Location> getLocs() {
    return DestroyTheCore.game.map.shops;
  }
  
  @Override
  public void setLocs(Set<Location> locs) {
    DestroyTheCore.game.map.shops = locs;
  }
}
