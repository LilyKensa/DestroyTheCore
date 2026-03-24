package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.roles.KekkaiMasterRole;
import dev.huey.destroyTheCore.roles.MoleRole;
import dev.huey.destroyTheCore.roles.RangerRole;
import dev.huey.destroyTheCore.roles.WandererRole;
import org.bukkit.scheduler.BukkitRunnable;

public class TicksManager {
  
  static public final int particleRate = 4, updateRate = 10,
    tipRate = 5 * 60 * 20;
  
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
  
  /** Tip tick is every {@value #tipRate} ticks */
  public boolean isTipTick() {
    return ticksCount % tipRate == 0;
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
        MoleRole.onParticleTick();
        
        DestroyTheCore.game.onParticleTick();
      }
      
      if (isUpdateTick()) {
        DestroyTheCore.itemsManager.onUpdateTick();
        
        RangerRole.onUpdateTick();
        MoleRole.onUpdateTick();
      }
      
      if (isTipTick()) {
        DestroyTheCore.tipsManager.onTipTick();
      }
      
      if (isSeconds()) {
        DestroyTheCore.boardsManager.onUITick();
      }
    }
  }
  
  /** Instance of {@link TicksRunnable} */
  TicksRunnable task;
  
  public void init() {
    task = new TicksRunnable();
    task.runTaskTimer(DestroyTheCore.instance, 0, 1);
  }
}
