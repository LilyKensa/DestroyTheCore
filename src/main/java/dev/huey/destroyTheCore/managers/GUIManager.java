package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.Constants;
import dev.huey.destroyTheCore.DestroyTheCore;
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
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.AnvilWindow;
import xyz.xenondevs.invui.window.Window;

public class GUIManager {
  
  Gui roleGui;
  
  public void init() {
    roleGui = PagedGui.items().setStructure(
      "# # # # # # # # #",
      "# x x x x x x x #",
      "# x x x x x x x #",
      "# x x x x x x x #",
      "# # < # R # > # #"
    ).addIngredient(
      '#',
      new SimpleItem(new ItemBuilder(CoreUtils.emptyGuiItem()))
    ).addIngredient('<', new PrevPageItem()).addIngredient('>',
      new NextPageItem()).addIngredient('R',
        new RandomRoleItem()).addIngredient('x',
          Markers.CONTENT_LIST_SLOT_HORIZONTAL).setContent(
            DestroyTheCore.rolesManager.roles.values().stream().map(
              r -> (Item) r).toList()
          ).build();
  }
  
  public void openRoleSelection(Player pl) {
    Window window = Window.single().setViewer(pl).setTitle(TextUtils.$r(
      "gui.titles.choose-role")).setGui(roleGui).build();
    
    window.open();
  }
  
  public void openTeleporter(Player pl) {
    Gui teleportGui = PagedGui.items().setStructure(
      "# # # # # # # # #",
      "# x x x x x x x #",
      "# x x x x x x x #",
      "# x x x x x x x #",
      "# # < # # # > # #"
    ).addIngredient(
      '#',
      new SimpleItem(new ItemBuilder(CoreUtils.emptyGuiItem()))
    ).addIngredient('<', new PrevPageItem()).addIngredient('>',
      new NextPageItem()).addIngredient('x',
        Markers.CONTENT_LIST_SLOT_HORIZONTAL).setContent(
          Bukkit.getOnlinePlayers().stream().filter(
            p -> DestroyTheCore.game.getPlayerData(
              p).side != Game.Side.SPECTATOR
          ).map(p -> (Item) new AbstractItem() {
            @Override
            public ItemProvider getItemProvider() {
              ItemStack item = new ItemStack(Material.PLAYER_HEAD);
              SkullMeta meta = (SkullMeta) item.getItemMeta();
              
              meta.displayName(
                PlayerUtils.getName(p).decoration(TextDecoration.ITALIC, false)
              );
              meta.setOwningPlayer(p);
              
              item.setItemMeta(meta);
              return new ItemBuilder(item);
            }
            
            @Override
            public void handleClick(
              ClickType click, Player pl, InventoryClickEvent ev
            ) {
              pl.teleport(p);
            }
          }
          ).toList()
        ).build();
    
    Window window = Window.single().setViewer(pl).setTitle(TextUtils.$r(
      "gui.titles.teleporter")).setGui(teleportGui).build();
    
    window.open();
  }
  
  public boolean postClick = false;
  
  public UUID shopEditor = null;
  
  public boolean isEditingShop() {
    return (shopEditor != null && Bukkit.getOfflinePlayer(
      shopEditor).isOnline());
  }
  
  public void onPlayerLeave(Player pl) {
    shopEditor = null;
  }
  
  public void openShopListEditor(Player pl) {
    shopEditor = pl.getUniqueId();
    
    Gui shopListGui = PagedGui.items().setStructure(
      "# # # # # # # # #",
      "# x x x x x x x #",
      "# x x x x x x x #",
      "# x x x x x x x #",
      "# # < # + # > # #"
    ).addIngredient(
      '#',
      new SimpleItem(new ItemBuilder(CoreUtils.emptyGuiItem()))
    ).addIngredient('<', new PrevPageItem()).addIngredient('>',
      new NextPageItem()).addIngredient('x',
        Markers.CONTENT_LIST_SLOT_HORIZONTAL).addIngredient('+',
          new NewShopItem()).setContent(
            DestroyTheCore.game.shops.stream().map(
              shop -> (Item) new AbstractItem() {
                @Override
                public ItemProvider getItemProvider() {
                  ItemStack item = new ItemStack(shop.blockType);
                  item.editMeta(meta -> {
                    meta.displayName(
                      Component.text(shop.name).color(
                        NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC,
                          false)
                    );
                  });
                  return new ItemBuilder(item);
                }
                
                @Override
                public void handleClick(
                  ClickType click, Player pl, InventoryClickEvent ev
                ) {
                  postClick = true;
                  DestroyTheCore.guiManager.openShopTradesEditor(pl, shop);
                }
              }
            ).toList()
          ).build();
    
    Window window = Window.single().setViewer(pl).setTitle(TextUtils.$r(
      "gui.titles.shop.list")).setGui(shopListGui).build();
    
    window.open();
    
    window.addCloseHandler(() -> {
      if (postClick) postClick = false;
      else shopEditor = null;
    });
  }
  
  public void openShopTradesEditor(Player pl, Game.Shop shop) {
    VirtualInventory tradesInv = new VirtualInventory(
      shop.items.stream().map(MaybeGen::get).toList().toArray(new ItemStack[0])
    );
    tradesInv.resize(3 * 7 * 2); // 3 pages
    
    VirtualInventory blockInv = new VirtualInventory(
      new ItemStack[]{new ItemStack(shop.blockType)}
    );
    
    Gui shopGui = ScrollGui.inventories().setStructure(
      "# # # # # # # # #",
      "# x x x x x x x #",
      "# # # # # # # # #",
      "# x x x x x x x #",
      "# # < # # # > # #",
      "# b # # # v r d #"
    ).addIngredient(
      '#',
      new SimpleItem(new ItemBuilder(CoreUtils.emptyGuiItem()))
    ).addIngredient('<', new ScrollUpItem()).addIngredient('>',
      new ScrollDownItem()).addIngredient('x',
        Markers.CONTENT_LIST_SLOT_VERTICAL).addIngredient('b',
          blockInv).addIngredient('v', new DetailShopItem(shop)).addIngredient(
            'r',
            new RenameShopItem(shop)).addIngredient('d',
              new DeleteShopItem(shop)).setContent(List.of(tradesInv)).build();
    
    Window window = Window.single().setViewer(pl).setTitle(TextUtils.$r(
      "gui.titles.shop.trades")).setGui(shopGui).build();
    
    window.open();
    
    window.addCloseHandler(() -> {
      ItemStack blockStack = blockInv.getItem(0);
      if (blockStack != null) shop.blockType = blockStack.getType();
      
      shop.items.clear();
      
      ItemStack[] stacks = tradesInv.getItems();
      for (int i = 0; i + 1 < stacks.length; i += 2) {
        ItemStack good = stacks[i], cost = stacks[i + 1];
        if (good == null && cost == null) continue;
        
        shop.items.add(MaybeGen.fromItem(good));
        shop.items.add(MaybeGen.fromItem(cost));
      }
      
      if (postClick) postClick = false;
      else CoreUtils.setTickOut(() -> openShopListEditor(pl)
      );
    });
  }
  
  public void openShopRenameEditor(Player pl, Game.Shop shop) {
    ItemStack item = new ItemStack(shop.blockType);
    item.editMeta(meta -> {
      meta.displayName(
        Component.text(shop.name).decoration(TextDecoration.ITALIC, false)
      );
    });
    
    Gui renameGui = Gui.normal().setStructure("x").addIngredient('x',
      item).build();
    
    AnvilWindow window = AnvilWindow.single().setViewer(pl).setTitle(
      TextUtils.$r("gui.titles.shop.rename")).setGui(renameGui).build();
    
    window.open();
    
    window.addCloseHandler(() -> {
      String name = window.getRenameText();
      if (name != null && !name.isEmpty()) shop.name = name;
      
      CoreUtils.setTickOut(() -> openShopTradesEditor(pl, shop));
    });
  }
  
  public void openShopDetailEditor(Player pl, Game.Shop shop) {
    List<Item> itemList = new ArrayList<>();
    
    itemList.addAll(
      Registry.VILLAGER_TYPE.stream().map(type -> (Item) new AbstractItem() {
        @Override
        public ItemProvider getItemProvider() {
          ItemStack item = new ItemStack(Constants.villagerIcons.get(type));
          item.editMeta(meta -> {
            meta.displayName(
              TextUtils.$("gui.villagers." + type.getKey().getKey()).color(
                NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
            );
            if (type == shop.biome) {
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
          ClickType click, Player pl, InventoryClickEvent ev
        ) {
          shop.biome = type;
          
          CoreUtils.setTickOut(() -> {
            postClick = true;
            openShopDetailEditor(pl, shop);
          });
        }
      }
      ).toList()
    );
    
    itemList.addAll(
      Registry.VILLAGER_PROFESSION.stream().map(
        prof -> (Item) new AbstractItem() {
          @Override
          public ItemProvider getItemProvider() {
            ItemStack item = new ItemStack(
              Constants.villagerJobSites.get(prof)
            );
            item.editMeta(meta -> {
              meta.displayName(
                Component.translatable(prof.translationKey()).color(
                  NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC,
                    false)
              );
              if (prof == shop.prof) {
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
            ClickType click, Player pl, InventoryClickEvent ev
          ) {
            shop.prof = prof;
            
            CoreUtils.setTickOut(() -> {
              postClick = true;
              openShopDetailEditor(pl, shop);
            });
          }
        }
      ).toList()
    );
    
    Gui shopGui = PagedGui.items().setStructure(
      "# # # # # # # # #",
      "# x x x x x x x #",
      "# # # # # # # # #",
      "# x x x x x x x #",
      "# x x x x x x x #",
      "# # # # # # # # #"
    ).addIngredient(
      '#',
      new SimpleItem(new ItemBuilder(CoreUtils.emptyGuiItem()))
    ).addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL).setContent(
      itemList).build();
    
    Window window = Window.single().setViewer(pl).setTitle(TextUtils.$r(
      "gui.titles.shop.villager")).setGui(shopGui).build();
    
    window.open();
    
    window.addCloseHandler(() -> {
      if (postClick) postClick = false;
      else CoreUtils.setTickOut(() -> openShopTradesEditor(pl, shop)
      );
    });
  }
}
