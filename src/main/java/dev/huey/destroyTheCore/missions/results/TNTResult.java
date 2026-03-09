package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

public class TNTResult extends Mission.Result {
  
  public TNTResult() {
    super("tnt");
  }
  
  @Override
  public void forLoser(Game.Side side) {
    Player p = RandomUtils.pick(PlayerUtils.getTeammates(side));
    if (p == null) return;
    
    TNTPrimed tnt = (TNTPrimed) p.getWorld().spawnEntity(
      p.getLocation(),
      EntityType.TNT
    );
    tnt.setFuseTicks(30);
    
    announce(
      side,
      List.of(Placeholder.component("player", PlayerUtils.getName(p)))
    );
  }
}
