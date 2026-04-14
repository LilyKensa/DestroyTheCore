package dev.huey.destroyTheCore.missions;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.utils.LocUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;

public class ExplodeMission extends TimedMission {
  
  public ExplodeMission() {
    super("explode", 60 * 20);
  }
  
  List<Player> players = new ArrayList<>();
  
  @Override
  public void innerStart() {
    for (Game.Side side : Game.bothSide) {
      Player pl = RandomUtils.pick(PlayerUtils.getTeammates(side));
      if (pl == null) continue;
      
      players.add(pl);
    }
  }
  
  @Override
  public void innerTick() {
    if (DTC.ticksManager.isUpdateTick()) {
      for (Player pl : players) {
        if (pl == null) continue;
        
        pl.sendActionBar(
          TextUtils.$("missions.explode.warning")
        );
      }
    }
    
    if (DTC.ticksManager.isParticleTick()) {
      for (Player pl : players) {
        new ParticleBuilder(Particle.CAMPFIRE_COSY_SMOKE)
          .allPlayers()
          .location(LocUtils.hitboxCenter(pl))
          .count(RandomUtils.range(1, 4))
          .extra(0.1)
          .spawn();
      }
    }
  }
  
  @Override
  public void innerFinish() {
    for (Player pl : players) {
      pl.getWorld().createExplosion(
        pl,
        pl.getLocation(),
        5F, // Power
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
