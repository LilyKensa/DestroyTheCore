package dev.huey.destroyTheCore.bases.editorTools;

import dev.huey.destroyTheCore.bases.EditorTool;
import dev.huey.destroyTheCore.records.Pos;
import dev.huey.destroyTheCore.utils.ParticleUtils;
import java.util.List;
import java.util.Set;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PosListTool extends EditorTool {
  
  Set<Pos> list;
  Color col;
  
  public PosListTool(String id, Material iconType, Color col) {
    super(id, iconType);
    this.col = col;
  }
  
  @Override
  public void refresh() {
    list = getList();
  }
  
  /** @implNote Required - To load the current pos list */
  public Set<Pos> getList() {
    return null;
  }
  
  /** @implNote Required - To update the current pos list */
  public void setList(Set<Pos> list) {
  }
  
  @Override
  public void onParticleTick(Player pl) {
    for (Pos pos : list) {
      ParticleUtils.block(List.of(pl), pos.toLoc(pl.getWorld()), col);
    }
  }
  
  void handleAddPos(Pos pos) {
    list.add(pos);
    setList(list);
  }
  
  void handleRemovePos(Pos pos) {
    list.removeIf(p -> p.isSameBlockAs(pos));
    setList(list);
  }
  
  @Override
  public void onRightClickAir(Player pl) {
    handleAddPos(Pos.of(pl).floor());
  }
  
  @Override
  public void onRightClickBlock(Player pl, Block block) {
    handleAddPos(
      Pos.of(
        block.getLocation()
          .setRotation(pl.getYaw(), pl.getPitch())
      )
    );
  }
  
  @Override
  public void onBreakBlock(Player pl, Block block) {
    handleRemovePos(Pos.of(block));
  }
}
