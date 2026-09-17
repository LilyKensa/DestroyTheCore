package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class SuicideCommand extends Subcommand {
  
  public SuicideCommand() {
    super("suicide");
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!DTC.game.isPlaying || DTC.game.paused) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.suicide.bad-time"));
      return;
    }
    
    PlayerData data = DTC.game.getPlayerData(pl);
    if (!data.alive || data.side == Game.Side.SPECTATOR) {
      PlayerUtils.prefixedSend(
        pl,
        TextUtils.$("commands.suicide.already-dead")
      );
      return;
    }
    
    PlayerUtils.broadcast(
      TextUtils.$(
        "commands.suicide.announce",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.unparsed(
            "action",
            RandomUtils.pick(
              DTC.translationsManager.unparsed(
                "commands.suicide.actions"
              )
                .split("\\|")
            )
          )
        )
      )
    );
    
    CoreUtils.setTickOut(() -> {
      pl.damage(Double.MAX_VALUE);
    });
  }
}
