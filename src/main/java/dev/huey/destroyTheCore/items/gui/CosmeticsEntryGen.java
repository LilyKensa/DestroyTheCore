package dev.huey.destroyTheCore.items.gui;

import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CosmeticsEntryGen extends UsableItemGen {
  
  public CosmeticsEntryGen() {
    super(ItemsManager.ItemKey.COSMETICS_ENTRY, Material.NAUTILUS_SHELL);
    setBound();
  }
  
  @Override
  public void use(Player pl, Block block) {
    
  }
}
