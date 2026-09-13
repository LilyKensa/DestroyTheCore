package dev.huey.destroyTheCore.gui.shop;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.utils.TextUtils;
import org.bukkit.Material;
import xyz.xenondevs.invui.item.BoundItem;
import xyz.xenondevs.invui.item.ItemBuilder;

public class NewShopItem {
  
  static public final BoundItem it = BoundItem.builder()
    .setItemProvider(
      (pl, gui) -> new ItemBuilder(Material.EMERALD)
        .setCustomName(TextUtils.$("gui.buttons.new-shop.title"))
    )
    .addClickHandler((item, gui, click) -> {
      DTC.game.shops.add(new Game.Shop());
      DTC.guiManager.openShopListEditor(click.player());
    })
    .build();
}
