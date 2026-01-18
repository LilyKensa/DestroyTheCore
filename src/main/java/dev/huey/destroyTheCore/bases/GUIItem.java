package dev.huey.destroyTheCore.bases;

import dev.huey.destroyTheCore.utils.CoreUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public abstract class GUIItem extends AbstractItem {
  
  /** Close all windows for a player */
  protected void closeWindow(Player pl) {
    getWindows().stream().filter(window -> window.getViewer().equals(
      pl)).forEach(window -> CoreUtils.setTickOut(window::close));
  }
  
  @Override
  public abstract void handleClick(
                                   ClickType click, Player pl, InventoryClickEvent ev
  );
}
