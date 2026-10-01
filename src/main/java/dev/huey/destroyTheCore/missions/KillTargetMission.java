package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import java.util.EnumMap;
import java.util.UUID;
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
    }
  }
  
  @Override
  public void tick() {
    
  }
  
  @Override
  public void finish() {
    if (draw) declareDraw();
  }
}
