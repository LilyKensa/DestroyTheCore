package dev.huey.destroyTheCore.bases.editorTools;

import dev.huey.destroyTheCore.bases.EditorTool;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.ParticleUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class MultipleLocationsTool extends EditorTool {
  Set<Location> locs;
  Color col;
  
  public MultipleLocationsTool(String id, Material iconType, Color col) {
    super(id, iconType);
    this.col = col;
  }
  
  @Override
  public void refresh() {
    locs = getLocs();
  }
  
  /** @implNote Required - To load the current locations list */
  public Set<Location> getLocs() {
    return null;
  }
  
  /** @implNote Required - To update the current locations list */
  public void setLocs(Set<Location> locs) {
  
  }
  
  @Override
  public void onParticleTick(Player pl) {
    for (Location loc : locs)
      ParticleUtils.block(List.of(pl), loc, col);
  }
  
  void handleAddLoc(Location loc) {
    locs.add(loc);
    setLocs(locs);
  }
  
  void handleRemoveLoc(Location loc) {
    locs.removeIf(l -> LocationUtils.isSameBlock(l, loc));
    setLocs(locs);
  }
  
  @Override
  public void onRightClickAir(Player pl) {
    handleAddLoc(pl.getLocation().toBlockLocation());
  }
  
  @Override
  public void onRightClickBlock(Player pl, Block block) {
    handleAddLoc(
      block.getLocation()
    );
  }
  
  @Override
  public void onBreakBlock(Player pl, Block block) {
    handleRemoveLoc(block.getLocation());
  }
}
