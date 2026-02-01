package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.ItemGen;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import org.bukkit.entity.Player;

public class GiveCommand extends Subcommand {
  public GiveCommand() {
    super("give");
    addArgument(
      "item",
      () -> DestroyTheCore.itemsManager.gens.keySet().stream().map(
        key -> key.name().toLowerCase()
      ).toList()
    );
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    if (args.isEmpty()) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.give.unclear"));
      return;
    }
    
    int amount = 1;
    if (args.size() >= 2) {
      try {
        amount = Integer.parseInt(args.get(1));
      }
      catch (NumberFormatException ignored) {
      }
    }
    
    ItemsManager.ItemKey key;
    try {
      key = ItemsManager.ItemKey.valueOf(args.getFirst().toUpperCase());
    }
    catch (IllegalArgumentException ignored) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.give.not-found"));
      return;
    }
    
    ItemGen ig = DestroyTheCore.itemsManager.gens.get(key);
    pl.give(ig.getItem(amount));
  }
}
