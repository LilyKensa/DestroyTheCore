package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class GoOutsideMission extends TimedMission {
  
  public GoOutsideMission() {
    super("go-outside");
  }
  
  @Override
  public void innerStart() {
  }
  
  @Override
  public void innerTick() {
    if (DestroyTheCore.ticksManager.ticksCount % 25 == 0) { // Wither I = 25 ticks
      for (Player p : PlayerUtils.allGaming()) {
        if (PlayerUtils.isUnderSky(p)) continue;
        
        PlayerUtils.addPassiveEffect(
          p,
          PotionEffectType.SLOWNESS,
          40,
          1
        );
        PlayerUtils.addPassiveEffect(
          p,
          PotionEffectType.WITHER,
          40,
          1
        );
      }
    }
  }
  
  @Override
  public void innerFinish() {
  }
}
