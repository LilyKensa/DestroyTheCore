package dev.huey.destroyTheCore.missions;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RocketMission extends TimedMission {
  
  public RocketMission() {
    super("rocket", 5 * 20);
  }
  
  @Override
  public void innerStart() {
  }
  
  @Override
  public void innerTick() {
    if (DestroyTheCore.ticksManager.isUpdateTick()) {
      for (Player pl : PlayerUtils.allGaming()) pl.sendActionBar(
        TextUtils.$("missions.rocket.warning")
      );
    }
    
    if (DestroyTheCore.ticksManager.isParticleTick()) {
      for (Player pl : PlayerUtils.allGaming()) new ParticleBuilder(
        Particle.CLOUD
      ).allPlayers().location(pl.getLocation()).count(RandomUtils.range(1,
        4)).extra(0.05).spawn();
    }
  }
  
  @Override
  public void innerFinish() {
    for (Player pl : PlayerUtils.allGaming()) {
      pl.addPotionEffect(
        new PotionEffect(PotionEffectType.LEVITATION, 20, 79, true, false)
      );
      
      new ParticleBuilder(Particle.CLOUD).allPlayers().location(
        pl.getLocation()).count(20).extra(0.1).spawn();
      
      pl.playSound(
        pl.getLocation(),
        Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,
        1, // Volume
        1 // Pitch
      );
    }
  }
}
