package dev.huey.destroyTheCore.gui.shop;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Material;
import xyz.xenondevs.invui.item.BoundItem;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemBuilder;
import xyz.xenondevs.invui.item.ItemProvider;

public class DeleteShopItem {
  
  static public Item of(Game.Shop shop) {
    AtomicBoolean confirming = new AtomicBoolean(false);
    
    return BoundItem.builder()
      .setItemProvider(
        (pl) -> (ItemProvider) new ItemBuilder(Material.REDSTONE)
          .setCustomName(
            TextUtils.$(
              "gui.buttons.delete-shop.title" + (confirming.get() ? "-confirm"
                : "")
            )
          )
          .build()
      )
      .addClickHandler((item, gui, click) -> {
        if (!confirming.get()) {
          confirming.set(true);
          gui.notifyWindows();
          return;
        }
        
        DTC.game.shops.remove(shop);
        
        DTC.guiManager.openShopListEditor(click.player());
      })
      .build();
  }
}
