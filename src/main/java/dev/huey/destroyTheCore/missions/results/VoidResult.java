package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;

public class VoidResult extends Mission.Result {
  public VoidResult() {
    super("void");
  }
  
  @Override
  public void forLoser(Game.Side side) {
    Player p = RandomUtils.pick(PlayerUtils.getTeammates(side));
    if (p == null) return;
    
    p.teleport(p.getLocation().add(0, -1000, 0));
    
    announce(side, List.of(
      Placeholder.component("player", PlayerUtils.getName(p))
    ));
  }
}
