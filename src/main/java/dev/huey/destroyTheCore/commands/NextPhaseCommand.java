package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class NextPhaseCommand extends Subcommand {
  
  public NextPhaseCommand() {
    super("skip");
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    if (!DestroyTheCore.game.isPlaying) return;
    
    DestroyTheCore.game.nextPhase();
    PlayerUtils.prefixedBroadcast(
      TextUtils.$(
        "commands.skip.announce",
        List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
      )
    );
    DestroyTheCore.boardsManager.refresh();
  }
}
