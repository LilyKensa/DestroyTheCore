package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;

public class RejoinCommand extends Subcommand {
  
  public RejoinCommand() {
    super("rejoin");
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.inLobby(pl)) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.rejoin.wrong-world"));
      return;
    }
    
    if (!DestroyTheCore.game.isPlaying) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.rejoin.no-game"));
      return;
    }
    
    PlayerUtils.broadcast(
      TextUtils.$(
        "commands.rejoin.announce",
        List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
      )
    );
    
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    data.kill();
    
    if (PlayerUtils.shouldHandle(pl)) pl.getInventory().clear();
    PlayerUtils.teleportToRestArea(pl);
    PlayerUtils.scheduleRespawn(pl);
  }
}
