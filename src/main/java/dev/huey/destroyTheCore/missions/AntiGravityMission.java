package dev.huey.destroyTheCore.missions;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.records.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AntiGravityMission extends TimedMission {
  
  public AntiGravityMission() {
    super("anti-gravity");
  }
  
  @Override
  public void innerStart() {
  }
  
  @Override
  public void innerTick() {
    if (DestroyTheCore.ticksManager.isUpdateTick()) {
      for (Player p : Bukkit.getOnlinePlayers()) {
        PlayerData d = DestroyTheCore.game.getPlayerData(p);
        if (!d.alive) continue;
        if (d.side == Game.Side.SPECTATOR) continue;
        
        p.addPotionEffect(
          new PotionEffect(PotionEffectType.SLOW_FALLING, 30, 0, true, false)
        );
        
        if (p.isSneaking()) {
          p.addPotionEffect(
            new PotionEffect(PotionEffectType.LEVITATION, 15, 5, true, false)
          );
          
          new ParticleBuilder(Particle.CLOUD).allPlayers().location(
            p.getLocation()).offset(0.1, 0, 0.1).count(2).extra(0.05).spawn();
        }
      }
    }
  }
  
  @Override
  public void innerFinish() {
  }
}
