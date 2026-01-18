package dev.huey.destroyTheCore.commands;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Subcommand;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ShuffleTeamCommand extends Subcommand {
  
  public ShuffleTeamCommand() {
    super("shuffle-team");
  }
  
  @Override
  public void execute(Player pl, List<String> args) {
    if (!PlayerUtils.isAdmin(pl)) {
      PlayerUtils.reportNoPerm(pl);
      return;
    }
    
    Collection<Player> players = (Collection<Player>) Bukkit.getOnlinePlayers();
    List<Game.Side> randomSides = new ArrayList<>();
    
    int size = players.size(), half = players.size() / 2;
    if (size % 2 == 1 && Math.random() < 0.5) {
      half++;
    }
    
    for (int i = 0; i < half; i++) {
      randomSides.add(Game.Side.RED);
    }
    for (int i = 0; i < players.size() - half; i++) {
      randomSides.add(Game.Side.GREEN);
    }
    Collections.shuffle(randomSides);
    
    int i = 0;
    for (Player p : players) {
      DestroyTheCore.game.getPlayerData(p).join(randomSides.get(i));
      DestroyTheCore.game.enforceTeam(p);
      i++;
    }
    DestroyTheCore.boardsManager.refresh();
    
    PlayerUtils.prefixedBroadcast(
      TextUtils.$(
        "commands.shuffle-team.announce",
        List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
      )
    );
  }
}
