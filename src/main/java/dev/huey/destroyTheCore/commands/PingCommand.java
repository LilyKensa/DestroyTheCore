package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class PingCommand extends Subcommand {
  static final float greenBound = 20f;
  static final float redBound = 200f;
  
  TextColor getColor(int ping) {
    float ratio = (Math.max(
      greenBound,
      Math.min(redBound, (float) ping)
    ) - greenBound) / (redBound - greenBound);
    
    return TextColor.lerp(ratio, NamedTextColor.GREEN, NamedTextColor.RED);
  }
  
  public PingCommand() {
    super("ping");
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    int ping = pl.getPing();
    PlayerUtils.prefixedSend(
      pl,
      TextUtils.$(
        "commands.ping.result",
        List.of(
          Placeholder.component(
            "value",
            Component.text(ping).color(getColor(ping))
          )
        )
      )
    );
  }
}
