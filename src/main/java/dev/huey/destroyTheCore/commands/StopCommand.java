package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;

public class StopCommand extends Subcommand {
  public StopCommand() {
    super("stop");
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    DestroyTheCore.game.stop();
    PlayerUtils.prefixedBroadcast(TextUtils.$("commands.stop.announce", List.of(
      Placeholder.component("player", PlayerUtils.getName(pl))
    )));
  }
}
