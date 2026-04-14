package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.records.SideData;

public class BanOresResult extends Mission.Result {
  
  public BanOresResult() {
    super("ban-ores", false);
  }
  
  @Override
  public void forLoser(Game.Side side) {
    outro(side);
    
    SideData sd = DestroyTheCore.game.getSideData(side);
    sd.banOres(2 * 60 * 20);
    
    DestroyTheCore.game.noOresBars.show(side);
    DestroyTheCore.game.banOres(side, sd.noOresTicks);
  }
}
