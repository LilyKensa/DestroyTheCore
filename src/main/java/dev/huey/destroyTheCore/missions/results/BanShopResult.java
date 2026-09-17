package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;

public class BanShopResult extends Mission.Result {
  
  public BanShopResult() {
    super("ban-shop", false);
  }
  
  @Override
  public void forLoser(Game.Side side) {
    outro(side);
    
    DTC.game.getSideData(side).banShop(120 * 20);
    DTC.game.noShopBars.show(side);
  }
}
