package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.records.Stats;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class StatsCommand extends Subcommand {
  
  public StatsCommand() {
    super("stats");
    addArgument(
      "player",
      () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList()
    );
  }
  
  void sendWithPrefix(Player pl, Component comp) {
    PlayerUtils.send(
      pl,
      TextUtils.$("commands.stats.line-prefix").append(comp)
    );
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    OfflinePlayer target = pl;
    
    if (!args.isEmpty()) {
      target = Bukkit.getOfflinePlayer(args.get(0));
    }
    
    Stats stats = DestroyTheCore.game.getStats(target);
    
    if (stats == null) {
      PlayerUtils.prefixedSend(
        pl,
        TextUtils.$("commands.join.player-not-found")
      );
      return;
    }
    
    PlayerUtils.send(pl, Component.empty());
    
    PlayerUtils.prefixedSend(
      pl,
      TextUtils.$(
        "commands.stats.title",
        List.of(
          Placeholder.unparsed("player", target.getName())
        )
      )
    );
    
    PlayerUtils.send(pl, Component.empty());
    
    for (
      Component comp : List.of(
        TextUtils.$(
          "board.wins",
          List.of(
            Placeholder.component("wins", Component.text(stats.wins)),
            Placeholder.component("all", Component.text(stats.games)),
            Placeholder.unparsed(
              "ratio",
              stats.games == 0 ? "0.0" : CoreUtils.toFixed(
                100D * stats.wins / stats.games
              )
            )
          )
        ),
        TextUtils.$(
          "board.kd",
          List.of(
            Placeholder.component("kills", Component.text(stats.kills)),
            Placeholder.component("deaths", Component.text(stats.deaths))
          )
        ),
        TextUtils.$(
          "board.attacks",
          List.of(
            Placeholder.component("value", Component.text(stats.coreAttacks))
          )
        ),
        TextUtils.$(
          "board.skills",
          List.of(
            Placeholder.component(
              "value",
              Component.text(stats.skills)
            )
          )
        ),
        TextUtils.$(
          "board.ores",
          List.of(
            Placeholder.component(
              "value",
              Component.text(
                stats.ores.values().stream().reduce(0, Integer::sum)
              )
            )
          )
        )
      )
    ) {
      sendWithPrefix(pl, comp);
    }
    
    for (Map.Entry<Material, Integer> entry : stats.ores.entrySet()) {
      sendWithPrefix(
        pl,
        TextUtils.$(
          "board.ore-type",
          List.of(
            Placeholder.unparsed(
              "type",
              String.join(
                " ",
                Arrays.stream(
                  entry.getKey().name()
                    .split("_")
                )
                  .map(CoreUtils::capitalize)
                  .toList()
              )
            ),
            Placeholder.component("value", Component.text(entry.getValue()))
          )
        )
      );
    }
    
    PlayerUtils.send(pl, Component.empty());
  }
}
