package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class StartCommand extends Subcommand {

  public StartCommand() {
    super("start");
  }

  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }

    if (DTC.game.isPlaying) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.start.bad-time"));
      return;
    }

    DTC.game.start();
  }
}
