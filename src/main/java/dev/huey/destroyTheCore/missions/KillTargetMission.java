package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.ParticleUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillTargetMission extends Mission {
  
  EnumMap<Game.Side, UUID> targets = new EnumMap<>(Game.Side.class);
  
  public KillTargetMission() {
    super("kill-target");
    addResult();
  }
  
  boolean draw = true;
  
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent ev) {
    Player pl = ev.getPlayer();
    PlayerData data = DTC.game.getPlayerData(pl);
    
    Game.Side otherSide = data.side.opposite();
    UUID id = targets.get(otherSide);
    if (id == null) return;
    
    if (pl.getUniqueId() == id) {
      draw = false;
      declareWinner(otherSide);
    }
  }
  
  @Override
  public void start() {
    for (Game.Side side : Game.bothSide) {
      Player pl = RandomUtils.pick(PlayerUtils.getEnemies(side));
      if (pl == null) continue;
      
      targets.put(side, pl.getUniqueId());
      
      for (Player p : PlayerUtils.getTeammates(side)) {
        send(
          p,
          TextUtils.$(
            "missions.kill-target.announce",
            List.of(
              Placeholder.component("player", PlayerUtils.getName(pl))
            )
          )
        );
      }
    }
  }
  
  @Override
  public void tick() {
    if (DTC.ticksManager.isParticleTick()) {
      for (Player p : PlayerUtils.allGaming()) {
        PlayerData d = DTC.game.getPlayerData(p);
        if (p.getUniqueId().equals(targets.get(d.side.opposite()))) {
          ParticleUtils.dust(
            PlayerUtils.getNonEnemies(p),
            p.getEyeLocation().add(0, 0.6, 0),
            Color.YELLOW
          );
        }
      }
    }
  }
  
  @Override
  public void finish() {
    if (draw) declareDraw();
  }
}
