package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WarpCommand extends Subcommand {
  
  public WarpCommand() {
    super("warp");
    addArgument("place", () -> List.of("lobby", "spawn"));
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    if (args.isEmpty()) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.warp.unclear"));
      return;
    }
    
    Location loc = null;
    
    switch (args.getFirst()) {
      case "lobby" -> {
        loc = LocUtils.lobby(DestroyTheCore.game.lobby.spawn);
      }
      case "spawn" -> {
        loc = LocUtils.live(
          LocUtils.toSpawnPoint(
            RandomUtils.pick(DestroyTheCore.game.map.spawnpoints)
          )
        );
      }
    }
    
    if (loc == null) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.warp.not-found"));
      return;
    }
    
    pl.teleport(loc);
  }
}
