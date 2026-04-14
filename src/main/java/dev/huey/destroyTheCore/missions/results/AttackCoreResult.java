package dev.huey.destroyTheCore.missions.results;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;

public class AttackCoreResult extends Mission.Result {
  
  public AttackCoreResult() {
    super("attack-core", false);
  }
  
  @Override
  public void forLoser(Game.Side side) {
    outro(side);
    
    DTC.game.getSideData(side).directAttackCore(10);
    DTC.game.checkWinner();
  }
}
