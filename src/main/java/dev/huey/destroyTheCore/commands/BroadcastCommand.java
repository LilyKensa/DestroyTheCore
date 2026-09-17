package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class BroadcastCommand extends Subcommand {
  
  public BroadcastCommand() {
    super("broadcast");
    addArgument("message", () -> List.of("<message>"));
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    TextComponent message = Component.text(String.join(" ", args));
    
    PlayerUtils.broadcast(
      Component.join(
        JoinConfiguration.noSeparators(),
        TextUtils.$("chat.broadcast.prefix"),
        TextUtils.$(
          "chat.format",
          List.of(
            Placeholder.component("player", TextUtils.$("chat.broadcast.name")),
            Placeholder.component("message", message.color(null))
          )
        )
      )
    );
  }
}
