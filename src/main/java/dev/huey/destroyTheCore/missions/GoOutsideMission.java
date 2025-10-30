package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GoOutsideMission extends TimedMission {
  static public boolean isUnderSky(Player pl) {
    return pl.getY() > pl.getWorld()
      .getHighestBlockAt(pl.getLocation())
      .getLocation().getY();
  }
  
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
        if (isUnderSky(p)) continue;
        
        p.addPotionEffect(new PotionEffect(
          PotionEffectType.SLOWNESS,
          40,
          0,
          true,
          false
        ));
        p.addPotionEffect(new PotionEffect(
          PotionEffectType.WITHER,
          40,
          0,
          true,
          false
        ));
      }
    }
  }
  
  @Override
  public void innerFinish() {
  
  }
}
