package dev.huey.destroyTheCore.gui.control;

import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.BoundItem;
import xyz.xenondevs.invui.item.ItemBuilder;

public class ScrollUpItem {
  
  static final int offset = -1;
  
  static public BoundItem.Builder<ScrollGui<?>> get() {
    return BoundItem.scrollBuilder()
      .setItemProvider(
        (pl, gui) -> new ItemBuilder(
          gui.getLine() >= -offset
            ? Material.GLOWSTONE_DUST
            : Material.GUNPOWDER
        )
          .setCustomName(TextUtils.$("gui.buttons.scroll-up.title"))
          .addLoreLines(
            gui.getLine() >= -offset
              ? List.of()
              : List.of(TextUtils.$("gui.buttons.scroll-up.desc-end"))
          )
      )
      .addClickHandler((item, gui, click) -> {
        if (click.clickType() == ClickType.DOUBLE_CLICK) return;
        gui.setLine(gui.getLine() + offset);
      });
  }
}
