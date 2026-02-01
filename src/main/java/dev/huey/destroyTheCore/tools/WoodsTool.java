package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.editorTools.RegionTool;
import dev.huey.destroyTheCore.records.Pos;
import dev.huey.destroyTheCore.records.Region;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.*;

public class WoodsTool extends RegionTool {
  
  public WoodsTool() {
    super("woods", Material.WOODEN_SWORD, Color.ORANGE, Color.MAROON);
  }
  
  @Override
  public Region getRegion() {
    Set<Pos> set = DestroyTheCore.game.map.woods;
    if (set.isEmpty()) return null;
    
    double minX = 9999, minY = 9999, minZ = 9999;
    double maxX = -9999, maxY = -9999, maxZ = -9999;
    for (Pos pos : set) {
      double x = pos.getX();
      double y = pos.getY();
      double z = pos.getZ();
      
      if (x < minX) minX = x;
      if (y < minY) minY = y;
      if (z < minZ) minZ = z;
      if (x > maxX) maxX = x;
      if (y > maxY) maxY = y;
      if (z > maxZ) maxZ = z;
    }
    
    return new Region(
      new Pos(minX, minY, minZ),
      new Pos(maxX, maxY, maxZ)
    );
  }
  
  @Override
  public void setRegion(Region region) {
    if (region == null) {
      DestroyTheCore.game.map.woods = new HashSet<>();
      return;
    }
    
    Set<Pos> set = new HashSet<>();
    
    region.forEachBlock(DestroyTheCore.worldsManager.template, block -> {
      if (Tag.LOGS.isTagged(block.getType())) set.add(Pos.of(block));
    });
    DestroyTheCore.game.map.woods = set;
  }
}
