package dev.huey.destroyTheCore.gui.shop;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Material;
import xyz.xenondevs.invui.item.BoundItem;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemBuilder;

public class DetailShopItem {
  
  static public Item of(Game.Shop shop) {
    return BoundItem.builder()
      .setItemProvider(
        new ItemBuilder(Material.VILLAGER_SPAWN_EGG)
          .setCustomName(TextUtils.$("gui.buttons.detail-shop.title"))
          .addLoreLines(
            TextUtils.$(
              "gui.buttons.detail-shop.desc",
              List.of(
                Placeholder.component(
                  "type",
                  TextUtils.$("gui.villagers." + shop.biome.getKey().getKey())
                ),
                Placeholder.component(
                  "profession",
                  GlobalTranslator.render(
                    Component.translatable(shop.prof.translationKey()),
                    DTC.translationsManager.currentLocale
                  )
                )
              )
            )
          )
          .build()
      )
      .addClickHandler((item, gui, click) -> {
        DTC.guiManager.openShopDetailEditor(click.player(), shop);
      })
      .build();
  }
}
