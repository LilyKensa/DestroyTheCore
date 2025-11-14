package dev.huey.destroyTheCore.gui.shop;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.GUIItem;
import dev.huey.destroyTheCore.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class DeleteShopItem extends GUIItem {
  Game.Shop shop;
  boolean confirming = false;
  
  public DeleteShopItem(Game.Shop shop) {
    this.shop = shop;
  }
  
  @Override
  public ItemProvider getItemProvider() {
    return new ItemBuilder(Material.REDSTONE)
      .setDisplayName(TextUtils.$r(
        "gui.buttons.delete-shop.title" + (confirming ? "-confirm" : "")
      ));
  }
  
  @Override
  public void handleClick(ClickType click, Player pl, InventoryClickEvent ev) {
    if (!confirming) {
      confirming = true;
      notifyWindows();
      return;
    }
    
    DestroyTheCore.game.shops.remove(shop);
    
    DestroyTheCore.guiManager.postClick = true;
    DestroyTheCore.guiManager.openShopListEditor(pl);
  }
}
