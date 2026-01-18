package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.missions.ProgressiveMission;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OccupyCenterMission extends ProgressiveMission {
  
  public OccupyCenterMission() {
    super("occupy-center");
  }
  
  Map<Game.Side, Integer> seconds = new HashMap<>();
  
  @Override
  public void innerStart() {
    seconds.put(Game.Side.RED, 0);
    seconds.put(Game.Side.GREEN, 0);
  }
  
  @Override
  public void tick() {
    if (DestroyTheCore.ticksManager.isSeconds()) {
      Set<Game.Side> occupied = new HashSet<>();
      
      for (Player p : Bukkit.getOnlinePlayers()) {
        PlayerData d = DestroyTheCore.game.getPlayerData(p);
        if (d.side.equals(Game.Side.SPECTATOR)) continue;
        if (occupied.contains(d.side)) continue;
        
        if (LocationUtils.near(p.getLocation(), loc, 30)) {
          seconds.put(d.side, seconds.getOrDefault(d.side, 0) + 1);
          occupied.add(d.side);
        }
      }
      
      for (Game.Side side : new Game.Side[]{Game.Side.RED, Game.Side.GREEN,
      }) {
        if (!occupied.contains(side)) {
          seconds.put(side, Math.max(seconds.getOrDefault(side, 0) - 1, 0));
        }
        
        float ratio = seconds.get(side) / 30F;
        progress(side, ratio);
        
        if (ratio == 1) {
          end();
          return;
        }
      }
    }
  }
  
  @Override
  public void innerFinish() {
    for (Game.Side side : new Game.Side[]{Game.Side.RED, Game.Side.GREEN}) {
      if (seconds.get(side).equals(seconds.get(side.opposite()))) {
        declareDraw();
        return;
      }
      if (seconds.get(side) >= 30) {
        declareWinner(side);
        return;
      }
    }
    
    declareDraw();
  }
}
