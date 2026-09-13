package dev.huey.destroyTheCore.gui.control;

import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import org.bukkit.Material;
import xyz.xenondevs.invui.item.BoundItem;
import xyz.xenondevs.invui.item.ItemBuilder;

public class ScrollDownItem {
  
  static final int offset = 2;
  
  static public final BoundItem it = BoundItem.scrollBuilder()
    .setItemProvider(
      (pl, gui) -> new ItemBuilder(
        gui.getLine() < gui.getMaxLine() - offset
          ? Material.GLOWSTONE_DUST
          : Material.GUNPOWDER
      )
        .setCustomName(TextUtils.$("gui.buttons.scroll-down.title"))
        .addLoreLines(
          gui.getLine() < gui.getMaxLine() - offset
            ? List.of()
            : List.of(TextUtils.$("gui.buttons.scroll-down.desc-end"))
        )
    )
    .addClickHandler((item, gui, click) -> {
      gui.setLine(gui.getLine() + offset);
    })
    .build();
}
