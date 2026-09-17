package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

public class TNTResult extends Mission.Result {
  
  Player pl;
  
  public TNTResult() {
    super("tnt", false);
  }
  
  @Override
  public List<TagResolver> getExtraPlaceholers() {
    return List.of(getRandomPlayerPlaceholder(pl));
  }
  
  @Override
  public void forLoser(Game.Side side) {
    pl = RandomUtils.pick(PlayerUtils.getTeammates(side));
    if (pl == null) return;
    
    TNTPrimed tnt = (TNTPrimed) pl.getWorld().spawnEntity(
      pl.getLocation(),
      EntityType.TNT
    );
    tnt.setFuseTicks(30);
    
    outro(side);
  }
}
