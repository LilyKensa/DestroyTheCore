package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class ShoutCommand extends Subcommand {
  
  public ShoutCommand() {
    super("shout");
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    
    if (data.shoutCooldown > 0 && !PlayerUtils.isAdmin(pl)) {
      PlayerUtils.send(
        pl,
        TextUtils.$(
          "chat.shout.cooldown",
          List.of(
            Placeholder.component(
              "value",
              Component.text(Math.ceilDiv(data.shoutCooldown, 20))
            )
          )
        )
      );
      return;
    }
    
    data.shoutCooldown = PlayerData.shoutCooldownDuration;
    
    Component message = args.isEmpty() ? TextUtils.$(
      "chat.shout.empty"
    ) : Component.text(String.join(" ", args));
    
    PlayerUtils.broadcast(
      Component.join(
        JoinConfiguration.noSeparators(),
        TextUtils.$("chat.shout.prefix"),
        TextUtils.$(
          "chat.format",
          List.of(
            Placeholder.component("player", PlayerUtils.getName(pl)),
            Placeholder.component("message", message.color(null))
          )
        )
      )
    );
  }
}
