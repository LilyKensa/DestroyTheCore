package dev.huey.destroyTheCore.tools;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.EditorTool;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CancelTool extends EditorTool {
  
  public CancelTool() {
    super("cancel", Material.BARRIER);
  }
  
  void leave(Player pl) {
    DTC.inventoriesManager.restoreHotbar(pl);
  }
  
  @Override
  public void onRightClickAir(Player pl) {
    leave(pl);
  }
  
  @Override
  public void onRightClickBlock(Player pl, Block block) {
    leave(pl);
  }
}
