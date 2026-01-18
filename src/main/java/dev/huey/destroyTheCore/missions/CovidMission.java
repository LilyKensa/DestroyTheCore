package dev.huey.destroyTheCore.missions;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class CovidMission extends TimedMission {
  
  public CovidMission() {
    super("covid");
  }
  
  @Override
  public void innerStart() {
  }
  
  Set<Player> isClose = new HashSet<>();
  
  @Override
  public void innerTick() {
    if (DestroyTheCore.ticksManager.ticksCount % 25 == 0) { // Poison 0 = every 25 ticks
      isClose.clear();
      
      for (Player p : PlayerUtils.allGaming()) {
        if (
          PlayerUtils.allGaming().stream().anyMatch(e -> !e.equals(
            p) && LocationUtils.near(e, p, 5))
        ) {
          isClose.add(p);
        }
      }
      
      for (Player p : isClose) {
        p.addPotionEffect(
          new PotionEffect(PotionEffectType.POISON, 40, 0, true, false)
        );
      }
      
      for (Player p : PlayerUtils.allGaming()) {
        LocationUtils.ring(
          LocationUtils.hitboxCenter(p),
          5,
          loc -> {
            new ParticleBuilder(
              isClose.contains(p) ? Particle.WAX_ON : Particle.COMPOSTER
            ).receivers(p).location(loc).extra(0).spawn();
          }
        );
      }
    }
  }
  
  @Override
  public void innerFinish() {
  }
}
