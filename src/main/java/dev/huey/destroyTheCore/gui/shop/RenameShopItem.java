package dev.huey.destroyTheCore.gui.shop;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import xyz.xenondevs.invui.item.BoundItem;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemBuilder;

public class RenameShopItem {
  
  static public Item of(Game.Shop shop) {
    return BoundItem.builder()
      .setItemProvider(
        new ItemBuilder(Material.NAME_TAG)
          .setCustomName(TextUtils.$("gui.buttons.rename-shop.title"))
          .addLoreLines(
            TextUtils.$(
              "gui.buttons.rename-shop.desc",
              List.of(Placeholder.unparsed("name", shop.name))
            )
          )
          .build()
      )
      .addClickHandler((item, gui, click) -> {
        DTC.guiManager.openShopRenameEditor(click.player(), shop);
      })
      .build();
  }
}
