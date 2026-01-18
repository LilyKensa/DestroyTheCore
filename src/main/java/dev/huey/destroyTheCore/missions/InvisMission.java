package dev.huey.destroyTheCore.missions;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.bases.missions.InstantMission;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InvisMission extends InstantMission {
  
  public InvisMission() {
    super("invis");
  }
  
  @Override
  public void run() {
    for (Player p : PlayerUtils.allGaming()) {
      new ParticleBuilder(Particle.CLOUD).allPlayers().location(
        LocationUtils.hitboxCenter(p)).offset(0.2, 0.4, 0.2).count(10).extra(
          0.02).spawn();
      
      p.addPotionEffect(
        new PotionEffect(
          PotionEffectType.INVISIBILITY,
          PotionEffect.INFINITE_DURATION,
          0,
          true,
          false
        )
      );
    }
  }
}
