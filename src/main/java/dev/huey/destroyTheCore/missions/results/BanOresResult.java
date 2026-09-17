package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.DTC;
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
    
    SideData sd = DTC.game.getSideData(side);
    sd.banOres(2 * 60 * 20);
    
    DTC.game.noOresBars.show(side);
    DTC.game.banOres(side, sd.noOresTicks);
  }
}
