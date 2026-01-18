package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;

public class BanShopResult extends Mission.Result {
  
  public BanShopResult() {
    super("ban-shop");
  }
  
  @Override
  public void forLoser(Game.Side side) {
    announce(side);
    
    DestroyTheCore.game.getSideData(side).banShop(120 * 20);
    DestroyTheCore.game.noShopBars.show(side);
  }
}
