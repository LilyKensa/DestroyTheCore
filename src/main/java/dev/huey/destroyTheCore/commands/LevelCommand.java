package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import java.util.List;
import java.util.Objects;
import org.bukkit.entity.Player;

public class LevelCommand extends Subcommand {
  
  public LevelCommand() {
    super("level");
    addArgument("command", () -> List.of("add", "subtract", "zero"));
  }
  
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    pl.sendMessage(args.get(0));
    if (Objects.equals(args.get(0), "add")) {
      DestroyTheCore.game.stats.get(
        pl.getUniqueId()).levels += 1;
    }
    if (Objects.equals(args.get(0), "subtract")) DestroyTheCore.game.stats.get(
      pl.getUniqueId()).levels -= 1;
    if (Objects.equals(args.get(0), "zero")) DestroyTheCore.game.stats.get(
      pl.getUniqueId()).levels = 0;
    
    
    pl.sendMessage("Your level is now" + DestroyTheCore.game.stats.get(
      pl.getUniqueId()).levels);
  }
}
