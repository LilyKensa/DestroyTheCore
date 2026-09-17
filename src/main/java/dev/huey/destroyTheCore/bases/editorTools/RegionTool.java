package dev.huey.destroyTheCore.bases.editorTools;

import dev.huey.destroyTheCore.bases.EditorTool;
import dev.huey.destroyTheCore.records.Pos;
import dev.huey.destroyTheCore.records.Region;
import dev.huey.destroyTheCore.utils.ParticleUtils;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class RegionTool extends EditorTool {
  Pos first, second;
  Region region;
  Color firstColor, secondColor;
  
  public RegionTool(
    String id, Material iconType, Color firstColor, Color secondColor
  ) {
    super(id, iconType);
    this.firstColor = firstColor;
    this.secondColor = secondColor;
  }
  
  @Override
  public void refresh() {
    region = getRegion();
    if (region != null) {
      first = region.getFirst();
      second = region.getSecond();
    }
  }
  
  /** @implNote Required - To load the current region */
  public Region getRegion() {
    return null;
  }
  
  /** @implNote Required - To update the current region */
  public void setRegion(Region region) {
  }
  
  @Override
  public void onParticleTick(Player pl) {
    World world = pl.getWorld();
    
    if (first != null && second != null) {
      ParticleUtils.region(
        List.of(pl),
        first.toLoc(world),
        second.toLoc(world),
        Color.GRAY,
        firstColor,
        secondColor
      );
    }
    else if (first != null) {
      ParticleUtils.block(List.of(pl), first.toLoc(world), firstColor);
    }
  }
  
  void handlePos(Pos pos) {
    if (first == null) {
      first = pos;
    }
    else if (second == null) {
      second = pos;
      
      region = new Region(first, second);
      setRegion(region);
    }
    else {
      first = pos;
      second = null;
      
      region = null;
      setRegion(null);
    }
  }
  
  @Override
  public void onRightClickAir(Player pl) {
    handlePos(Pos.of(pl).floor());
  }
  
  @Override
  public void onRightClickBlock(Player pl, Block block) {
    handlePos(Pos.of(block));
  }
  
  @Override
  public void onBreakBlock(Player pl, Block block) {
    if (region != null && region.contains(Pos.of(block))) {
      first = null;
      second = null;
      region = null;
      setRegion(null);
    }
  }
}
