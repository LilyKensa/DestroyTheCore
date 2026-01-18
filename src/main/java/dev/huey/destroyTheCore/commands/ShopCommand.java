package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import org.bukkit.entity.Player;

public class ShopCommand extends Subcommand {
  
  public ShopCommand() {
    super("shop");
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    if (DestroyTheCore.guiManager.isEditingShop()) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("gui.in-use"));
      return;
    }
    
    DestroyTheCore.guiManager.openShopListEditor(pl);
  }
}
