package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.missions.InstantMission;

public class NextDropAllMission extends InstantMission {
  public NextDropAllMission() {
    super("drop-all");
  }
  
  @Override
  public void run() {
    DestroyTheCore.game.nextPlayerDropAll = true;
  }
}
