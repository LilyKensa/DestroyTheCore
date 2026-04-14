package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import org.bukkit.entity.Player;

public class EditCommand extends Subcommand {
  
  public EditCommand() {
    super("edit");
    addArgument(
      "tool-group",
      () -> DTC.toolsManager.kits.keySet().stream().toList()
    );
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    if (args.isEmpty()) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.edit.unclear"));
      return;
    }
    
    String kitName = args.getFirst();
    
    if (!DTC.toolsManager.kits.containsKey(kitName)) return;
    
    DTC.toolsManager.loadKit(pl, kitName);
    PlayerUtils.prefixedSend(pl, TextUtils.$("commands.edit.loaded"));
  }
}
