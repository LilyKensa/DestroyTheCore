package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReviveCommand extends Subcommand {
  
  public ReviveCommand() {
    super("revive");
    addArgument(
      "player",
      () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList()
    );
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    if (!DestroyTheCore.game.isPlaying) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.revive.no-game"));
      return;
    }
    
    Player target;
    
    if (!args.isEmpty()) {
      target = Bukkit.getPlayer(args.getFirst());
      if (target == null) {
        PlayerUtils.prefixedSend(
          pl,
          TextUtils.$("commands.revive.player-not-found")
        );
        return;
      }
      PlayerUtils.prefixedBroadcast(
        TextUtils.$(
          "commands.revive.made-other",
          List.of(
            Placeholder.component("player", PlayerUtils.getName(pl)),
            Placeholder.component("target", PlayerUtils.getName(target))
          )
        )
      );
    }
    else {
      target = pl;
      PlayerUtils.prefixedBroadcast(
        TextUtils.$(
          "commands.revive.made-self",
          List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
        )
      );
    }
    
    PlayerUtils.respawn(target);
  }
}
