package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.missions.ProgressiveMission;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.LocationUtils;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RollCallMission extends ProgressiveMission {
  
  Map<Game.Side, Float> progress = new HashMap<>();
  Game.Side winner;
  
  public RollCallMission() {
    super("roll-call");
  }
  
  @Override
  public void innerStart() {
    progress.put(Game.Side.RED, 0F);
    progress.put(Game.Side.GREEN, 0F);
  }
  
  @Override
  public void tick() {
    if (DestroyTheCore.ticksManager.isUpdateTick()) {
      Map<Game.Side, Integer> all = new HashMap<>();
      Map<Game.Side, Integer> attended = new HashMap<>();
      
      for (Player p : Bukkit.getOnlinePlayers()) {
        PlayerData d = DestroyTheCore.game.getPlayerData(p);
        
        all.put(d.side, all.getOrDefault(d.side, 0) + 1);
        
        if (
          LocationUtils.near(
            p.getLocation(),
            LocationUtils.live(
              LocationUtils.selfSide(DestroyTheCore.game.map.core, p)
            ),
            6
          )
        ) {
          attended.put(d.side, attended.getOrDefault(d.side, 0) + 1);
        }
      }
      
      for (Game.Side side : new Game.Side[]{Game.Side.RED, Game.Side.GREEN,
      }) {
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
