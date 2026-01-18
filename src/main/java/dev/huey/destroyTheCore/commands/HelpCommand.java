package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCommand extends Subcommand {
  
  public HelpCommand() {
    super("help");
    addArgument(
      "command",
      () -> DestroyTheCore.commandsManager.subcommands.stream().map(
        c -> c.name).toList()
    );
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    PlayerUtils.prefixedSend(pl, ":3");
  }
}
