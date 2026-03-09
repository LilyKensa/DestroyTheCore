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

public class NewShopItem extends GUIItem {
  
  @Override
  public ItemProvider getItemProvider() {
    return new ItemBuilder(Material.EMERALD).setDisplayName(
      TextUtils.$r(
        "gui.buttons.new-shop.title"
      )
    );
  }
  
  @Override
  public void handleClick(ClickType click, Player pl, InventoryClickEvent ev) {
    DestroyTheCore.game.shops.add(new Game.Shop());
    
    DestroyTheCore.guiManager.postClick = true;
    DestroyTheCore.guiManager.openShopListEditor(pl);
  }
}
