package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpCommand extends Subcommand {
  public WarpCommand() {
    super("warp");
    addArgument(
      "place",
      () -> List.of("lobby", "spawn")
    );
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
        loc = DestroyTheCore.game.lobby.spawn;
      }
      case "spawn" -> {
        loc = LocationUtils.live(DestroyTheCore.game.map.spawnPoint);
      }
    }
    
    if (loc == null) {
      PlayerUtils.prefixedSend(pl, TextUtils.$("commands.warp.not-found"));
      return;
    }
    
    pl.teleport(loc);
  }
}
