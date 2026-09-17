package dev.huey.destroyTheCore.bases.editorTools;

import dev.huey.destroyTheCore.bases.EditorTool;
import dev.huey.destroyTheCore.records.Pos;
import dev.huey.destroyTheCore.utils.ParticleUtils;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PosTool extends EditorTool {
  
  Pos pos;
  Color col;
  
  public PosTool(String id, Material iconType, Color col) {
    super(id, iconType);
    this.col = col;
  }
  
  @Override
  public void refresh() {
    pos = getPos();
  }
  
  /** @implNote Required - To load the current location */
  public Pos getPos() {
    return null;
  }
  
  /** @implNote Required - To update the current location */
  public void setPos(Pos pos) {
  }
  
  @Override
  public void onParticleTick(Player pl) {
    if (pos == null) return;
    ParticleUtils.block(List.of(pl), pos.toLoc(pl.getWorld()), col);
  }
  
  void handleLoc(Pos pos) {
    this.pos = pos;
    setPos(pos);
  }
  
  @Override
  public void onRightClickAir(Player pl) {
    handleLoc(Pos.of(pl).floor());
  }
  
  @Override
  public void onRightClickBlock(Player pl, Block block) {
    handleLoc(
      Pos.of(block.getLocation().setRotation(pl.getYaw(), pl.getPitch()))
    );
  }
  
  @Override
  public void onBreakBlock(Player pl, Block block) {
    if (Pos.of(block).isSameBlockAs(pos)) {
      handleLoc(null);
    }
  }
}
