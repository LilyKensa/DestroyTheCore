package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.records.Stats;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import org.bukkit.entity.Player;

public class NightVisionCommand extends Subcommand {
  
  public NightVisionCommand() {
    super("night-vision");
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    Stats stats = DestroyTheCore.game.stats.get(pl.getUniqueId());
    
    stats.nightVision = !stats.nightVision;
    PlayerUtils.enforceNightVision(pl);
    
    PlayerUtils.prefixedSend(
      pl,
      TextUtils.$("commands.night-vision." + (stats.nightVision ? "on" : "off"))
    );
  }
}
