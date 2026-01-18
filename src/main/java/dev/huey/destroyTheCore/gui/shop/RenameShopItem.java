package dev.huey.destroyTheCore.gui.shop;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.GUIItem;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

import java.util.List;

public class RenameShopItem extends GUIItem {
  Game.Shop shop;
  
  public RenameShopItem(Game.Shop shop) {
    this.shop = shop;
  }
  
  @Override
  public ItemProvider getItemProvider() {
    return new ItemBuilder(Material.NAME_TAG)
      .setDisplayName(TextUtils.$r("gui.buttons.rename-shop.title"))
      .addLoreLines(TextUtils.$r("gui.buttons.rename-shop.desc", List.of(
        Placeholder.unparsed("name", shop.name)
      )));
  }
  
  @Override
  public void handleClick(ClickType click, Player pl, InventoryClickEvent ev) {
    DestroyTheCore.guiManager.postClick = true;
    DestroyTheCore.guiManager.openShopRenameEditor(pl, shop);
  }
}
