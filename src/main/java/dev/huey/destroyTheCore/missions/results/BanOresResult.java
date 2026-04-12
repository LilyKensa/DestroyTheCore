package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;

public class BanOresResult extends Mission.Result {
  
  public BanOresResult() {
    super("ban-ores", false);
  }
  
  @Override
  public void forLoser(Game.Side side) {
    outro(side);
    
    DestroyTheCore.game.banOres(side);
    DestroyTheCore.game.getSideData(side).banOres(120 * 20);
    DestroyTheCore.game.noOresBars.show(side);
  }
}
