package dev.huey.destroyTheCore.missions;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class ColdMission extends TimedMission {
  public ColdMission() {
    super("cold");
  }
  
  @Override
  public void innerStart() {
  
  }
  
  Set<Player> isClose = new HashSet<>();
  
  @Override
  public void innerTick() {
    if (DestroyTheCore.ticksManager.ticksCount % 5 == 0) {
      for (Player p : PlayerUtils.allGaming()) {
        int freeze = p.getFreezeTicks();
        if (isClose.contains(p)) {
          p.setFreezeTicks(freeze / 2);
        }
        else {
          if (freeze < 20 * 20)
            p.setFreezeTicks(freeze + 20);
        }
      }
    }
    
    if (DestroyTheCore.ticksManager.ticksCount % 25 == 0) {
      isClose.clear();
      
      for (Player p : PlayerUtils.allGaming()) {
        if (
          PlayerUtils.allGaming()
            .stream().anyMatch(e -> !e.equals(p) && LocationUtils.near(e, p, 5))
        ) {
          isClose.add(p);
        }
      }
      
      for (Player p : PlayerUtils.allGaming()) {
        LocationUtils.ring(LocationUtils.hitboxCenter(p), 5, loc -> {
          new ParticleBuilder(isClose.contains(p) ? Particle.COMPOSTER : Particle.WAX_ON)
            .receivers(p)
            .location(loc)
            .extra(0)
            .spawn();
        });
      }
    }
  }
  
  @Override
  public void innerFinish() {
  
  }
}
