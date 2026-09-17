package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import java.util.List;
import org.bukkit.entity.Player;

public class SettingsCommand extends Subcommand {
  public SettingsCommand() {
    super("setting");
    addArgument("name", () -> List.of("<name>"));
    addArgument("operations", () -> List.of("<operations>"));
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
  }
}
