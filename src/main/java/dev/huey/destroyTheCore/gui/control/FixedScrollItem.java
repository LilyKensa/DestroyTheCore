package dev.huey.destroyTheCore.gui.control;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.impl.controlitem.ControlItem;
import xyz.xenondevs.invui.item.impl.controlitem.ScrollItem;

import java.util.HashMap;

/** A fix for {@link ScrollItem} */
public abstract class FixedScrollItem extends ControlItem<ScrollGui<?>> {
  private final HashMap<ClickType, Integer> scroll;
  
  public FixedScrollItem(int linesOnLeftClick) {
    this.scroll = new HashMap<>();
    this.scroll.put(ClickType.LEFT, linesOnLeftClick);
  }
  
  public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
    if (this.scroll.containsKey(clickType)) {
      ScrollGui<?> gui = this.getGui();
      int offset = this.scroll.get(clickType);
      
      if (gui.canScroll(offset)) // Added this check
        gui.scroll(offset);
    }
  }
}
