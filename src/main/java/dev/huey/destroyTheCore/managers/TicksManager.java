package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.roles.KekkaiMasterRole;
import dev.huey.destroyTheCore.roles.RangerRole;
import dev.huey.destroyTheCore.roles.WandererRole;
import org.bukkit.scheduler.BukkitRunnable;

public class TicksManager {
  
  public static final int particleRate = 4, updateRate = 10;
  
  /** Ticks elapsed from last game start */
  public int ticksCount = 0;
  
  /** Particle tick is every {@value #particleRate} ticks */
  public boolean isParticleTick() {
    return ticksCount % particleRate == 0;
  }
  
  /** Update tick is every {@value #updateRate} ticks */
  public boolean isUpdateTick() {
    return ticksCount % updateRate == 0;
  }
  
  /** Second is every 20 ticks */
  public boolean isSeconds() {
    return ticksCount % 20 == 0;
  }
  
  /** The {@link BukkitRunnable} that runs every tick */
  public class TicksRunnable extends BukkitRunnable {
    
    @Override
    public void run() {
      ticksCount++;
      
      DestroyTheCore.game.onTick();
      
      DestroyTheCore.missionsManager.onTick();
      KekkaiMasterRole.onTick();
      WandererRole.onTick();
      
      if (isParticleTick()) {
        DestroyTheCore.toolsManager.onParticleTick();
        DestroyTheCore.glowManager.onParticleTick();
        
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
    }
  }
  
  /** Instance of {@link TicksRunnable} */
  TicksRunnable task;
  
  public void init() {
    (task = new TicksRunnable()).runTaskTimer(DestroyTheCore.instance, 0, 1);
  }
}
