package dev.huey.destroyTheCore.bases.missions;

import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.CoreUtils;

public abstract class InstantMission extends Mission {
  
  public InstantMission(String id) {
    super(id);
  }
  
  @Override
  public void start() {
    CoreUtils.setTickOut(this::end);
  }
  
  @Override
  public void tick() {
  }
  
  @Override
  public void finish() {
    run();
  }
  
  public abstract void run();
}
