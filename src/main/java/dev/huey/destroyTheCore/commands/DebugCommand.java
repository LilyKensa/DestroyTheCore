package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class DebugCommand extends Subcommand {
  
  public DebugCommand() {
    super("debug");
    addArgument("command", () -> List.of("respawn-shop"));
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    switch (args.getFirst()) {
      case "respawn-shop" -> {
        if (!DTC.game.isPlaying) {
          PlayerUtils.prefixedSend(pl, TextUtils.$("commands.debug.no-game"));
          return;
        }
        
        for (Game.VillagerData vd : DTC.game.villagers) {
          Villager v = vd.villager();
          v.setInvulnerable(false);
          v.kill();
        }
        DTC.game.villagers.clear();
        
        DTC.game.summonShopVillagers();
        
        PlayerUtils.prefixedSend(pl, TextUtils.$("commands.debug.good"));
      }
    }
  }
}
