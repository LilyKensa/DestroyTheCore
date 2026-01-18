package dev.huey.destroyTheCore.gui.control;

import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

import java.util.List;

public class NextPageItem extends PageItem {
  public NextPageItem() {
    super(true);
  }
  
  @Override
  public ItemProvider getItemProvider(PagedGui<?> gui) {
    ItemBuilder builder = new ItemBuilder(
      gui.hasNextPage()
        ? Material.GLOWSTONE_DUST
        :Material.GUNPOWDER
    );
    builder.setDisplayName(TextUtils.$r("gui.buttons.next-page.title"))
      .addLoreLines(
        gui.hasNextPage()
          ? TextUtils.$r("gui.buttons.next-page.desc", List.of(
            Placeholder.component("next", Component.text(gui.getCurrentPage() + 2)),
            Placeholder.component("max", Component.text(gui.getPageAmount()))
          ))
          : TextUtils.$r("gui.buttons.next-page.desc-end")
      );
    
    return builder;
  }
}
