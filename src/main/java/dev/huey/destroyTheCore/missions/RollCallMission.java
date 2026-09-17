package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.missions.ProgressiveMission;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.records.Pos;
import dev.huey.destroyTheCore.utils.LocUtils;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public class RollCallMission extends ProgressiveMission {
  
  Map<Game.Side, Float> progress = new HashMap<>();
  Game.Side winner;
  
  public RollCallMission() {
    super("roll-call");
    addResult();
  }
  
  @Override
  public void innerStart() {
    progress.put(Game.Side.RED, 0F);
    progress.put(Game.Side.GREEN, 0F);
  }
  
  @Override
  public void tick() {
    if (DTC.ticksManager.isUpdateTick()) {
      Map<Game.Side, Integer> all = new HashMap<>();
      Map<Game.Side, Integer> attended = new HashMap<>();
      
      for (Player p : DTC.worldsManager.live.getPlayers()) {
        PlayerData d = DTC.game.getPlayerData(p);
        
        all.put(d.side, all.getOrDefault(d.side, 0) + 1);
        
        if (
          LocUtils.near(
            Pos.of(p),
            LocUtils.selfSide(DTC.game.map.core, p),
            6
          )
        ) {
          attended.put(d.side, attended.getOrDefault(d.side, 0) + 1);
        }
      }
      
      for (Game.Side side : Game.bothSide) {
        int allCount = all.getOrDefault(side, 0);
        if (allCount == 0) allCount = 1;
        
        progress.put(side, 1F * attended.getOrDefault(side, 0) / allCount);
        progress(side, progress.get(side));
        
        if (progress.get(side) >= 1F) {
          winner = side;
          end();
          return;
        }
      }
    }
  }
  
  @Override
  public void innerFinish() {
    if (winner == null) {
      declareDraw();
    }
    else {
      declareWinner(winner);
    }
  }
}
