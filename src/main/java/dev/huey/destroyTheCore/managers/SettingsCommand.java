package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import java.util.List;
import org.bukkit.entity.Player;

public class SettingsCommand extends Subcommand {
  public SettingsCommand() {
    super("settings");
    addArgument("name", () -> List.of("<name>"));
    addArgument("value", () -> List.of("<value>"));
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
  }
}
