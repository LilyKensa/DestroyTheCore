package dev.huey.destroyTheCore.gui.shop;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.GUIItem;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.List;

public class DetailShopItem extends GUIItem {
  
  Game.Shop shop;
  
  public DetailShopItem(Game.Shop shop) {
    this.shop = shop;
  }
  
  @Override
  public ItemProvider getItemProvider() {
    return new ItemBuilder(Material.VILLAGER_SPAWN_EGG).setDisplayName(
      TextUtils.$r("gui.buttons.detail-shop.title")).addLoreLines(
        TextUtils.$r(
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
                DestroyTheCore.translationsManager.currentLocale
              )
            )
          )
        )
      );
  }
  
  @Override
  public void handleClick(ClickType click, Player pl, InventoryClickEvent ev) {
    DestroyTheCore.guiManager.postClick = true;
    DestroyTheCore.guiManager.openShopDetailEditor(pl, shop);
  }
}
