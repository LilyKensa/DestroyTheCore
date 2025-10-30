package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.roles.KekkaiMasterRole;
import dev.huey.destroyTheCore.roles.RangerRole;
import dev.huey.destroyTheCore.roles.WandererRole;
import org.bukkit.Bukkit;

public class TicksManager {
  static public final int particleRate = 4, updateRate = 10;
  
  public int ticksCount = 0;
  
  public boolean isParticleTick() {
    return ticksCount % particleRate == 0;
  }
  
  public boolean isUpdateTick() {
    return ticksCount % updateRate == 0;
  }
  
  public boolean isSeconds() {
    return ticksCount % 20 == 0;
  }
  
  public void init() {
    Bukkit.getScheduler().scheduleSyncRepeatingTask(DestroyTheCore.instance, () -> {
      ticksCount++;
      
      DestroyTheCore.game.onTick();
      
      DestroyTheCore.missionsManager.onTick();
      KekkaiMasterRole.onTick();
      WandererRole.onTick();
      
      if (isParticleTick()) {
        DestroyTheCore.toolsManager.onParticleTick();
        KekkaiMasterRole.onParticleTick();
        RangerRole.onParticleTick();
        WandererRole.onParticleTick();
        DestroyTheCore.game.onParticleTick();
      }
      
      if (isUpdateTick()) {
        DestroyTheCore.itemsManager.onUpdateTick();
      }
      
      if (isSeconds()) {
        DestroyTheCore.boardsManager.onUITick();
      }
    }, 0, 1);
  }
}
