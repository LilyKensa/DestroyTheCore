package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.gui.NextPageItem;
import dev.huey.destroyTheCore.gui.PickRandomItem;
import dev.huey.destroyTheCore.gui.PrevPageItem;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class GUIManager {
  Gui roleGui;
  
  public void init() {
    roleGui = PagedGui.items()
      .setStructure(
        "# # # # # # # # #",
        "# x x x x x x x #",
        "# x x x x x x x #",
        "# x x x x x x x #",
        "# # < # R # > # #")
      .addIngredient('#', new SimpleItem(
        new ItemBuilder(CoreUtils.emptyItem())
      ))
      .addIngredient('<', new PrevPageItem())
      .addIngredient('>', new NextPageItem())
      .addIngredient('R', new PickRandomItem())
      .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
      .setContent(
        DestroyTheCore.rolesManager.roles.values().stream()
          .map(r -> (Item) r).toList()
      )
      .build();
  }
  
  public void openRoleSelection(Player pl) {
    Window window = Window.single()
      .setViewer(pl)
      .setTitle(TextUtils.$r("gui.titles.choose-role"))
      .setGui(roleGui)
      .build();
    
    window.open();
  }
  
  public void openTeleporter(Player pl) {
    Gui teleportGui = PagedGui.items()
      .setStructure(
        "# # # # # # # # #",
        "# x x x x x x x #",
        "# x x x x x x x #",
        "# x x x x x x x #",
        "# # < # # # > # #")
      .addIngredient('#', new SimpleItem(
        new ItemBuilder(CoreUtils.emptyItem())
      ))
      .addIngredient('<', new PrevPageItem())
      .addIngredient('>', new NextPageItem())
      .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
      .setContent(
        Bukkit.getOnlinePlayers().stream()
          .filter(p ->
            DestroyTheCore.game.getPlayerData(p).side != Game.Side.SPECTATOR
          )
          .map(p -> (Item) new AbstractItem() {
            @Override
            public ItemProvider getItemProvider() {
              ItemStack item = new ItemStack(Material.PLAYER_HEAD);
              SkullMeta meta = (SkullMeta) item.getItemMeta();
              
              meta.displayName(PlayerUtils.getName(p)
                .decoration(TextDecoration.ITALIC, false));
              meta.setOwningPlayer(p);
              
              item.setItemMeta(meta);
              return new ItemBuilder(item);
            }
            
            @Override
            public void handleClick(ClickType click, Player pl, InventoryClickEvent ev) {
              pl.teleport(p);
            }
          })
          .toList()
      )
      .build();
    
    Window window = Window.single()
      .setViewer(pl)
      .setTitle(TextUtils.$r("gui.titles.teleporter"))
      .setGui(teleportGui)
      .build();
    
    window.open();
  }
}
