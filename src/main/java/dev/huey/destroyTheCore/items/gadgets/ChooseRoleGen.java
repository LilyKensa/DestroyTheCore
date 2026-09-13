package dev.huey.destroyTheCore.items.gadgets;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.itemGens.UsableItemGen;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;


public class ChooseRoleGen extends UsableItemGen {
  
  public ChooseRoleGen() {
    super(ItemsManager.ItemKey.CHOOSE_ROLE, Material.WRITABLE_BOOK);
  }
  
  @Override
  public void computeMeta(ItemMeta uncastedMeta) {
    BookMeta meta = (BookMeta) uncastedMeta;
    
    meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
  }
  
  @Override
  public void use(Player pl, Block block) {
    PlayerUtils.takeOneItemFromHand(pl);
    
    DTC.guiManager.openRoleSelection(pl);
  }
}
