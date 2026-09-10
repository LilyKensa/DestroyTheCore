package dev.huey.destroyTheCore.gui.control;

import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import xyz.xenondevs.invui.item.BoundItem;
import xyz.xenondevs.invui.item.ItemBuilder;

public class PrevPageItem {
  
  static public final BoundItem it = BoundItem.pagedBuilder()
    .setItemProvider(
      (pl, gui) -> new ItemBuilder(
        gui.getPage() > 0
          ? Material.GLOWSTONE_DUST
          : Material.GUNPOWDER
      )
        .setCustomName(
          TextUtils.$(
            "gui.buttons.prev-page.title"
          )
        )
        .addLoreLines(
          gui.getPage() > 0
            ? TextUtils.$(
              "gui.buttons.prev-page.desc",
              List.of(
                Placeholder.component(
                  "prev",
                  Component.text(gui.getPage())
                ),
                Placeholder.component("max", Component.text(gui.getPageCount()))
              )
            )
            : TextUtils.$("gui.buttons.prev-page.desc-end")
        )
    )
    .addClickHandler((item, gui, click) -> {
      gui.setPage(gui.getPage() - 1);
    })
    .build();
}
