package dev.huey.destroyTheCore.missions;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;

public class ExplodeMission extends TimedMission {
  
  public ExplodeMission() {
    super("explode", 60 * 20);
  }
  
  public Player randomPlayer(Game.Side side) {
    return RandomUtils.pick(PlayerUtils.getTeammates(side));
  }
  
  Player a, b;
  
  @Override
  public void innerStart() {
    a = randomPlayer(Game.Side.RED);
    b = randomPlayer(Game.Side.GREEN);
  }
  
  @Override
  public void innerTick() {
    if (DestroyTheCore.ticksManager.isUpdateTick()) {
      for (Player pl : new Player[]{a, b}) pl.sendActionBar(
        TextUtils.$("missions.explode.warning")
      );
    }
    
    if (DestroyTheCore.ticksManager.isParticleTick()) {
      for (Player pl : new Player[]{a, b}) new ParticleBuilder(
        Particle.CAMPFIRE_COSY_SMOKE
      ).allPlayers().location(LocationUtils.hitboxCenter(pl)).count(
        RandomUtils.range(1, 4)).extra(0.1).spawn();
    }
  }
  
  @Override
  public void innerFinish() {
    if (a == null || b == null) return;
    
    for (Player pl : new Player[]{a, b}) {
      pl.getWorld().createExplosion(
        pl,
        pl.getLocation(),
        8F, // Power
        false, // Fire
        true, // Break
        true // Exclude self
      );
      pl.damage(
        Double.MAX_VALUE,
        DamageSource.builder(DamageType.EXPLOSION).build()
      );
    }
  }
}
