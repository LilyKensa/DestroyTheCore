package dev.huey.destroyTheCore.gui.control;

import dev.huey.destroyTheCore.utils.TextUtils;
import org.bukkit.Material;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class ScrollDownItem extends FixedScrollItem {
  static final int offset = 2;
  
  public ScrollDownItem() {
    super(offset);
  }
  
  @Override
  public ItemProvider getItemProvider(ScrollGui<?> gui) {
    ItemBuilder builder = new ItemBuilder(
      gui.canScroll(offset)
        ? Material.GLOWSTONE_DUST
        :Material.GUNPOWDER
    );
    builder.setDisplayName(TextUtils.$r("gui.buttons.scroll-down.title"));
    if (!gui.canScroll(offset))
      builder.addLoreLines(TextUtils.$r("gui.buttons.scroll-down.desc-end"));
    
    return builder;
  }
  
}