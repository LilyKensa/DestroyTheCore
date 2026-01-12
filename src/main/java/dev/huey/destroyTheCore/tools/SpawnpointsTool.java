package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.MultipleLocationsTool;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Set;

public class SpawnpointsTool extends MultipleLocationsTool {
  public SpawnpointsTool() {
    super("spawnpoints", Material.NETHERITE_SWORD, Color.BLACK);
  }
  
  @Override
  public Set<Location> getLocs() {
    return DestroyTheCore.game.map.spawnpoints;
  }
  
  @Override
  public void setLocs(Set<Location> locs) {
    DestroyTheCore.game.map.spawnpoints = locs;
  }
}
