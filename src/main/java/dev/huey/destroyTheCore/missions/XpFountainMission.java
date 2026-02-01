package dev.huey.destroyTheCore.missions;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.RandomUtils;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.util.Vector;

public class XpFountainMission extends Mission {
  
  int count = 0;
  
  public XpFountainMission() {
    super("xp-fountain");
  }
  
  @Override
  public void start() {
  }
  
  @Override
  public void tick() {
    if (!DestroyTheCore.ticksManager.isParticleTick()) return;
    
    for (int i = 0; i < RandomUtils.range(1, 4); ++i) {
      ExperienceOrb orb = (ExperienceOrb) centerLoc.getWorld().spawnEntity(
        centerLoc,
        EntityType.EXPERIENCE_ORB
      );
      
      new ParticleBuilder(Particle.CLOUD)
        .allPlayers()
        .location(centerLoc)
        .offset(0.1, 0, 0.1)
        .extra(0.05)
        .spawn();
      
      orb.setExperience(RandomUtils.range(5, 11));
      
      double angle = RandomUtils.nextDouble() * 2 * Math.PI;
      double radius = Math.sqrt(RandomUtils.nextDouble()) * 0.1;
      orb.setVelocity(
        new Vector(
          Math.cos(angle) * radius,
          0.5 + RandomUtils.nextDouble() * 0.5,
          Math.sin(angle) * radius
        )
      );
      
      count++;
    }
    
    if (count >= 500) end();
  }
  
  @Override
  public void finish() {
  }
}
