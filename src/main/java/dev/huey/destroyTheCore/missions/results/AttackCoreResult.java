package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;

public class AttackCoreResult extends Mission.Result {
  public AttackCoreResult() {
    super("attack-core");
  }
  
  @Override
  public void forLoser(Game.Side side) {
    announce(side);
    
    DestroyTheCore.game.getSideData(side).directAttackCore(10);
    DestroyTheCore.game.checkWinner();
  }
}
