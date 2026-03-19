package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.records.Stats;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/** for debug purpose will delete after */
public class LevelCommand extends Subcommand {
  
  public LevelCommand() {
    super("level");
    addArgument("command", () -> List.of("add", "minus", "clear"));
    addArgument("amount", () -> List.of("<amount>"));
    addArgument(
      "player",
      () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList()
    );
  }
  
  public void execute(Player pl, List<String> args) {
    // 1. Permission Check
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    String action = args.isEmpty() ? null : args.get(0).toLowerCase();
    
    int amount = args.size() > 2 ? Integer.parseInt(args.get(1)) : 1;
    
    Player target = pl;
    if (args.size() > 3) {
      Player p = Bukkit.getPlayer(args.get(2));
      if (p != null) target = p;
    }
    
    Stats stat = DestroyTheCore.game.getStats(target);
    
    switch (action) {
      case "add" -> stat.addLevels(amount);
      case "minus" -> stat.minusLevels(amount);
      case "clear" -> stat.clearLevels();
      case null, default -> {
      }
    }
    
    DestroyTheCore.game.enforceLevelScore(target);
    
    pl.sendMessage(
      TextUtils.$(
        "commands.level.changed",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(target)),
          Placeholder.component("value", Component.text(stat.levels))
        )
      )
    );
  }
}
