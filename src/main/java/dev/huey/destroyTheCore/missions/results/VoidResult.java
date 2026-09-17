package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

public class VoidResult extends Mission.Result {
  
  Player pl;
  
  public VoidResult() {
    super("void", false);
  }
  
  @Override
  public List<TagResolver> getExtraPlaceholers() {
    return List.of(getRandomPlayerPlaceholder(pl));
  }
  
  @Override
  public void forLoser(Game.Side side) {
    Player pl = RandomUtils.pick(PlayerUtils.getTeammates(side));
    if (pl == null) return;
    
    pl.teleport(
      LocUtils.live(
        LocUtils.selfSide(
          RandomUtils.pick(DTC.game.map.spawnpoints)
            .add(0, 200, 0),
          pl
        )
      )
    );
    
    outro(side);
  }
}
