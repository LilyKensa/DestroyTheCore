package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class SnapCommand extends Subcommand {
  
  public SnapCommand() {
    super("snap");
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    pl.teleport(LocationUtils.toSpawnPoint(pl.getLocation()));
  }
}
