package dev.huey.destroyTheCore.items.gui;

import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PreviewMapEntryGen extends UsableItemGen {
  
  public PreviewMapEntryGen() {
    super(ItemsManager.ItemKey.PREVIEW_MAP_ENTRY, Material.ENDER_EYE);
  }
  
  @Override
  public void use(Player pl, Block block) {
    
  }
}
