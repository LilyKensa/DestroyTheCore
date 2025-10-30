package dev.huey.destroyTheCore.bases.editorTools;

import dev.huey.destroyTheCore.bases.EditorTool;
import dev.huey.destroyTheCore.records.Region;
import dev.huey.destroyTheCore.utils.ParticleUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class RegionTool extends EditorTool {
  Location loc1, loc2;
  Region region;
  Color col1, col2;
  
  public RegionTool(String id, Material iconType, Color col1, Color col2) {
    super(id, iconType);
    this.col1 = col1;
    this.col2 = col2;
  }
  
  @Override
  public void refresh() {
    region = getRegion();
    if (region != null) {
      loc1 = region.loc1();
      loc2 = region.loc2();
    }
  }
  
  public Region getRegion() {
    return null;
  }
  
  public void setRegion(Region region) {
    
  }
  
  @Override
  public void onParticleTick(Player pl) {
    if (loc1 != null && loc2 != null) {
      ParticleUtils.region(List.of(pl), loc1, loc2, Color.GRAY, col1, col2);
    }
    else if (loc1 != null) {
      ParticleUtils.block(List.of(pl), loc1, col1);
    }
  }
  
  void handleLoc(Location loc) {
    if (loc1 == null) {
      loc1 = loc;
    }
    else if (loc2 == null) {
      loc2 = loc;
      
      region = new Region(loc1, loc2);
      setRegion(region);
    }
    else {
      loc1 = loc;
      loc2 = null;
      
      region = null;
      setRegion(null);
    }
  }
  
  @Override
  public void onRightClickAir(Player pl) {
    handleLoc(pl.getLocation().toBlockLocation());
  }
  
  @Override
  public void onRightClickBlock(Player pl, Block block) {
    handleLoc(block.getLocation());
  }
  
  @Override
  public void onBreakBlock(Player pl, Block block) {
    if (region != null && region.contains(block.getLocation())) {
      loc1 = null;
      loc2 = null;
      region = null;
      setRegion(null);
    }
  }
}
