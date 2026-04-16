package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.records.Stats;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerData data = DTC.game.getPlayerData(p);
      Stats stat = DTC.game.getStats(p);
      
      stat.addFromPlayerData(data);
    }
    
    DTC.game.stop();
    PlayerUtils.prefixedBroadcast(
      TextUtils.$(
        "commands.stop.announce",
        List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
      )
    );
  }
}
