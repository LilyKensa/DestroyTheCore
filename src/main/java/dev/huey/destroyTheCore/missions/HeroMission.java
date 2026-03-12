package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.missions.InstantMission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class HeroMission extends InstantMission {
  
  public HeroMission() {
    super("hero");
  }
  
  public Player randomPlayer(Game.Side side) {
    return RandomUtils.pick(PlayerUtils.getTeammates(side));
  }
  
  @Override
  public void run() {
    Player a = randomPlayer(Game.Side.RED), b = randomPlayer(Game.Side.GREEN);
    
    for (Player pl : new Player[]{
      a, b
    }) {
      if (pl == null) continue;
      
      PlayerUtils.glow(pl, 60 * 20);
      PlayerUtils.addEffect(
        pl,
        PotionEffectType.STRENGTH,
        60 * 20,
        1
      );
      PlayerUtils.addEffect(
        pl,
        PotionEffectType.RESISTANCE,
        60 * 20,
        3
      );
      PlayerUtils.addEffect(
        pl,
        PotionEffectType.SPEED,
        60 * 20,
        2
      );
    }
  }
}
