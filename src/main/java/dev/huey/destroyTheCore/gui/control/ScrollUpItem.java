package dev.huey.destroyTheCore.gui.control;

import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import org.bukkit.Material;
import xyz.xenondevs.invui.item.BoundItem;
import xyz.xenondevs.invui.item.ItemBuilder;

public class ScrollUpItem {
  
  static final int offset = -2;
  
  static public final BoundItem it = BoundItem.scrollBuilder()
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
      gui.setLine(gui.getLine() + offset);
    })
    .build();
}
