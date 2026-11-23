package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.records.PlayerData;

public class FastRRTMission extends TimedMission {
  
  public FastRRTMission() {
    super("fast-rrt");
  }
  
  @Override
  public void innerStart() {
    DTC.game.rrtDuration = 20;
  }
  
  @Override
  public void innerTick() {
  }
  
  @Override
  public void innerFinish() {
    DTC.game.rrtDuration = PlayerData.rrtDuration;
  }
}
