package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class SaveCommand extends Subcommand {
  public SaveCommand() {
    super("save");
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    DestroyTheCore.configManager.save();
    PlayerUtils.prefixedSend(pl, TextUtils.$("commands.save.success"));
  }
}
