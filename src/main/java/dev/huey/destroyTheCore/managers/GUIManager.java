package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.Constants;
import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.gui.control.NextPageItem;
import dev.huey.destroyTheCore.gui.control.PrevPageItem;
import dev.huey.destroyTheCore.gui.control.ScrollDownItem;
import dev.huey.destroyTheCore.gui.control.ScrollUpItem;
import dev.huey.destroyTheCore.gui.role.RandomRoleItem;
import dev.huey.destroyTheCore.gui.shop.DeleteShopItem;
import dev.huey.destroyTheCore.gui.shop.DetailShopItem;
import dev.huey.destroyTheCore.gui.shop.NewShopItem;
import dev.huey.destroyTheCore.gui.shop.RenameShopItem;
import dev.huey.destroyTheCore.records.MaybeGen;
import dev.huey.destroyTheCore.utils.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.gui.*;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.*;
import xyz.xenondevs.invui.window.AnvilWindow;
import xyz.xenondevs.invui.window.Window;

public class GUIManager {
  
  public void init() {
    Structure.addGlobalIngredient(
      '#',
      Item.simple(
        new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
          .hideTooltip(true)
      )
    );
    Structure.addGlobalIngredient(
      '-',
      Markers.CONTENT_LIST_SLOT_HORIZONTAL
    );
    Structure.addGlobalIngredient(
      '|',
      Markers.CONTENT_LIST_SLOT_VERTICAL
    );
    Structure.addGlobalIngredient('<', PrevPageItem.it);
    Structure.addGlobalIngredient('>', NextPageItem.it);
    Structure.addGlobalIngredient('[', ScrollUpItem.it);
    Structure.addGlobalIngredient(']', ScrollDownItem.it);
  }
  
  public void openRoleSelection(Player pl) {
    Gui roleGui = PagedGui.itemsBuilder()
      .setStructure(
        "# # # # # # # # #",
        "# - - - - - - - #",
        "# - - - - - - - #",
        "# - - - - - - - #",
        "# # < # R # > # #"
      )
      .addIngredient('R', RandomRoleItem.it)
      .setContent(
        DTC.rolesManager.roles.values().stream()
          .map(
            role -> BoundItem.pagedBuilder()
              .setItemProvider(role::getGuiItem)
              .addClickHandler(role::handleClick)
              .build()
          )
          .toList()
      )
      .build();
    
    Window window = Window.builder()
      .setViewer(pl)
      .setTitle(TextUtils.$("gui.titles.choose-role"))
      .setUpperGui(roleGui)
      .build();
    
    if (LocUtils.inLobby(pl)) {
      pl.playSound(
        pl,
        Sound.BLOCK_ENDER_CHEST_OPEN,
        1, // Volume
        1 // Pitch
      );
      
      window.addCloseHandler((reason) -> {
        pl.setCooldown(Material.ENDER_CHEST, 5 * 20);
        pl.playSound(
          pl,
          Sound.BLOCK_ENDER_CHEST_CLOSE,
          1, // Volume
          1 // Pitch
        );
      });
    }
    
    window.open();
    
    AdvUtils.grant(pl, DTC.advancementsManager.chooseRoleAdv);
  }
  
  public void openTeleporter(Player pl) {
    Gui teleportGui = PagedGui.itemsBuilder()
      .setStructure(
        "# # # # # # # # #",
        "# - - - - - - - #",
        "# - - - - - - - #",
        "# - - - - - - - #",
        "# # < # # # > # #"
      )
      .setContent(
        Bukkit.getOnlinePlayers().stream()
          .filter(
            p -> DTC.game.getPlayerData(p).side != Game.Side.SPECTATOR
          )
          .map(p -> (Item) new AbstractItem() {
            
            @Override
            public ItemProvider getItemProvider(Player pl) {
              ItemStack item = new ItemStack(Material.PLAYER_HEAD);
              SkullMeta meta = (SkullMeta) item.getItemMeta();
              
              meta.displayName(
                PlayerUtils.getName(p)
                  .decoration(TextDecoration.ITALIC, false)
              );
              meta.setOwningPlayer(p);
              
              item.setItemMeta(meta);
              return new ItemBuilder(item);
            }
            
            @Override
            public void handleClick(ClickType type, Player pl, Click click) {
              pl.teleport(p);
            }
          }).toList()
      )
      .build();
    
    Window window = Window.builder()
      .setViewer(pl)
      .setTitle(TextUtils.$("gui.titles.teleporter"))
      .setUpperGui(teleportGui)
      .build();
    
    window.open();
  }
  
  public UUID shopEditor = null;
  
  public boolean isEditingShop() {
    return shopEditor != null && Bukkit.getOfflinePlayer(shopEditor).isOnline();
  }
  
  public void onPlayerLeave(Player pl) {
    shopEditor = null;
  }
  
  public void openShopListEditor(Player pl) {
    shopEditor = pl.getUniqueId();
    
    Gui shopListGui = PagedGui.itemsBuilder()
      .setStructure(
        "# # # # # # # # #",
        "# - - - - - - - #",
        "# - - - - - - - #",
        "# - - - - - - - #",
        "# # < # + # > # #"
      )
      .addIngredient('+', NewShopItem.it)
      .setContent(
        DTC.game.shops.stream().map(
          shop -> (Item) new AbstractItem() {
            
            @Override
            public ItemProvider getItemProvider(Player pl) {
              ItemStack item = new ItemStack(shop.blockType);
              item.editMeta(meta -> {
                meta.displayName(
                  Component.text(shop.name).color(
                    NamedTextColor.YELLOW
                  ).decoration(
                    TextDecoration.ITALIC,
                    false
                  )
                );
              });
              return new ItemBuilder(item);
            }
            
            @Override
            public void handleClick(
              ClickType type, Player pl, Click click
            ) {
              DTC.guiManager.openShopTradesEditor(pl, shop);
            }
          }
        ).toList()
      )
      .build();
    
    Window window = Window.builder()
      .setViewer(pl)
      .setTitle(TextUtils.$("gui.titles.shop.list"))
      .setUpperGui(shopListGui)
      .addCloseHandler((reason) -> {
        if (reason != InventoryCloseEvent.Reason.OPEN_NEW)
          shopEditor = null;
      })
      .build();
    
    window.open();
  }
  
  public void openShopTradesEditor(Player pl, Game.Shop shop) {
    List<ItemStack> displayItems = new ArrayList<>();
    for (int i = 0; i + 1 < shop.items.size(); i += 2) {
      ItemStack good = shop.items.get(i).get(), cost = shop.items.get(i + 1)
        .get();
      
      displayItems.add(good);
      displayItems.add(ItemStack.empty());
      displayItems.add(cost);
    }
    
    VirtualInventory tradesInv = new VirtualInventory(
      displayItems.toArray(new ItemStack[0])
    );
    
    VirtualInventory blockInv = new VirtualInventory(
      new ItemStack[]{
        new ItemStack(shop.blockType)
      }
    );
      
    Gui shopGui = ScrollGui.inventoriesBuilder()
      .setStructure(
        "# # # # # # # # #",
        "# | | | | | | | #",
        "# # # # # # # # #",
        "# | | | | | | | #",
        "# # [ # # # ] # #",
        "# b # # # v r d #"
      )
      .addIngredient('b', blockInv)
      .addIngredient('v', DetailShopItem.of(shop))
      .addIngredient('r', RenameShopItem.of(shop))
      .addIngredient('d', DeleteShopItem.of(shop))
      .setContent(List.of(tradesInv))
      .build();
    
    Window window = Window.builder()
      .setViewer(pl)
      .setTitle(TextUtils.$("gui.titles.shop.trades"))
      .setUpperGui(shopGui)
      .addCloseHandler((reason) -> {
        ItemStack blockStack = blockInv.getItem(0);
        if (blockStack != null) shop.blockType = blockStack.getType();
        
        shop.items.clear();
        
        ItemStack[] stacks = tradesInv.getItems();
        for (int i = 0; i + 1 < stacks.length; i += 3) {
          ItemStack good = stacks[i], cost = stacks[i + 2];
          if (good == null && cost == null) continue;
          
          shop.items.add(MaybeGen.fromItem(good));
          shop.items.add(MaybeGen.fromItem(cost));
        }
        
        if (reason != InventoryCloseEvent.Reason.OPEN_NEW)
          CoreUtils.setTickOut(
            () -> openShopListEditor(pl)
          );
      })
      .build();
    
    window.open();
  }
  
  public void openShopRenameEditor(Player pl, Game.Shop shop) {
    ItemStack item = new ItemStack(shop.blockType);
    item.editMeta(meta -> {
      meta.displayName(
        Component.text(shop.name)
          .decoration(TextDecoration.ITALIC, false)
      );
    });
    
    Gui renameGui = Gui.builder()
      .setStructure("x")
      .addIngredient('x', item)
      .build();
    
    AnvilWindow window = AnvilWindow.builder()
      .setViewer(pl)
      .setTitle(TextUtils.$("gui.titles.shop.rename"))
      .setUpperGui(renameGui)
      .build();
    
    window.addCloseHandler((reason) -> {
      String name = window.getRenameText();
      if (!name.isEmpty()) shop.name = name;
      
      if (reason != InventoryCloseEvent.Reason.OPEN_NEW)
        CoreUtils.setTickOut(
          () -> openShopTradesEditor(pl, shop)
        );
    });
    
    window.open();
  }
  
  public void openShopDetailEditor(Player pl, Game.Shop shop) {
    List<Item> itemList = new ArrayList<>();
    
    itemList.addAll(
      Registry.VILLAGER_TYPE.stream()
        .map(biome -> (Item) new AbstractItem() {
          
          @Override
          public ItemProvider getItemProvider(Player pl) {
            ItemStack item = new ItemStack(Constants.villagerIcons.get(biome));
            item.editMeta(meta -> {
              meta.displayName(
                TextUtils.$("gui.villagers." + biome.getKey().getKey())
                  .color(NamedTextColor.YELLOW)
                  .decoration(TextDecoration.ITALIC, false)
              );
              if (biome == shop.biome) {
                meta.setEnchantmentGlintOverride(true);
                meta.lore(
                  List.of(TextUtils.$("gui.buttons.detail-shop.selected"))
                );
              }
            });
            return new ItemBuilder(item);
          }
          
          @Override
          public void handleClick(
            ClickType type, Player pl, Click click
          ) {
            shop.biome = biome;
            
            CoreUtils.setTickOut(() -> {
              openShopDetailEditor(pl, shop);
            });
          }
        })
        .toList()
    );
    
    itemList.addAll(
      Registry.VILLAGER_PROFESSION.stream().map(
        prof -> (Item) new AbstractItem() {
          
          @Override
          public ItemProvider getItemProvider(Player pl) {
            ItemStack item = new ItemStack(
              Constants.villagerJobSites.get(prof)
            );
            item.editMeta(meta -> {
              meta.displayName(
                Component.translatable(prof.translationKey())
                  .color(NamedTextColor.YELLOW)
                  .decoration(TextDecoration.ITALIC, false)
              );
              if (prof == shop.prof) {
                meta.setEnchantmentGlintOverride(true);
                meta.lore(
                  List.of(
                    TextUtils.$("gui.buttons.detail-shop.selected")
                  )
                );
              }
            });
            return new ItemBuilder(item);
          }
          
          @Override
          public void handleClick(
            ClickType type, Player pl, Click click
          ) {
            shop.prof = prof;
            
            CoreUtils.setTickOut(() -> {
              openShopDetailEditor(pl, shop);
            });
          }
        }
      ).toList()
    );
    
    Gui detailsGui = PagedGui.itemsBuilder()
      .setStructure(
        "# # # # # # # # #",
        "# - - - - - - - #",
        "# # # # # # # # #",
        "# - - - - - - - #",
        "# - - - - - - - #",
        "# # # # # # # # #"
      )
      .setContent(itemList)
      .build();
    
    Window window = Window.builder()
      .setViewer(pl)
      .setTitle(TextUtils.$("gui.titles.shop.villager"))
      .setUpperGui(detailsGui)
      .addCloseHandler((reason) -> {
        if (reason != InventoryCloseEvent.Reason.OPEN_NEW)
          CoreUtils.setTickOut(
            () -> openShopTradesEditor(pl, shop)
          );
      })
      .build();
    
    window.open();
  }
}
