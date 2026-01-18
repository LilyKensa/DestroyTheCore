package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;

public class ResetCommand extends Subcommand {
  
  public ResetCommand() {
    super("reset");
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    DestroyTheCore.game.reset();
    PlayerUtils.prefixedBroadcast(
      TextUtils.$(
        "commands.reset.announce",
        List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
      )
    );
  }
}
