package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.bases.missions.InstantMission;

public class NextDropAllMission extends InstantMission {
  
  public NextDropAllMission() {
    super("drop-all");
  }
  
  @Override
  public void run() {
    DTC.game.nextPlayerDropAll = true;
  }
}
