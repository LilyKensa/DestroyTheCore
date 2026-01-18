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

public class SingleLocationTool extends EditorTool {
  
  Location loc;
  Color col;
  
  public SingleLocationTool(String id, Material iconType, Color col) {
    super(id, iconType);
    this.col = col;
  }
  
  @Override
  public void refresh() {
    loc = getLoc();
  }
  
  /** @implNote Required - To load the current location */
  public Location getLoc() {
    return null;
  }
  
  /** @implNote Required - To update the current location */
  public void setLoc(Location loc) {
  }
  
  @Override
  public void onParticleTick(Player pl) {
    if (loc == null) return;
    ParticleUtils.block(List.of(pl), loc, col);
  }
  
  void handleLoc(Location newLoc) {
    loc = newLoc;
    setLoc(loc);
  }
  
  @Override
  public void onRightClickAir(Player pl) {
    handleLoc(pl.getLocation().toBlockLocation());
  }
  
  @Override
  public void onRightClickBlock(Player pl, Block block) {
    handleLoc(block.getLocation().setRotation(pl.getYaw(), pl.getPitch()));
  }
  
  @Override
  public void onBreakBlock(Player pl, Block block) {
    if (LocationUtils.isSameBlock(block.getLocation(), loc)) {
      handleLoc(null);
    }
  }
}
