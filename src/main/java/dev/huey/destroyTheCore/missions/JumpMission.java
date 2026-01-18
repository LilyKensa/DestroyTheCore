package dev.huey.destroyTheCore.missions;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.missions.ProgressiveMission;
import dev.huey.destroyTheCore.records.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class JumpMission extends ProgressiveMission implements Listener {
  
  public JumpMission() {
    super("jump");
  }
  
  Map<Game.Side, Integer> count = new HashMap<>();
  
  @Override
  public void innerStart() {
    count.put(Game.Side.RED, 0);
    count.put(Game.Side.GREEN, 0);
  }
  
  @EventHandler
  public void onPlayerJump(PlayerJumpEvent ev) {
    Player pl = ev.getPlayer();
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    if (data.side.equals(Game.Side.SPECTATOR)) return;
    
    count.put(data.side, count.get(data.side) + 1);
    progress(data.side, (float) Math.min(count.get(data.side) / 100D, 1));
  }
  
  @Override
  public void tick() {
  }
  
  @Override
  public void innerFinish() {
    for (Game.Side side : new Game.Side[]{Game.Side.RED, Game.Side.GREEN}) {
      if (count.get(side) > count.get(side.opposite())) {
        declareWinner(side);
        return;
      }
    }
    
    declareDraw();
  }
}
