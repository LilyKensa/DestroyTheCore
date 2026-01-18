package dev.huey.destroyTheCore.missions;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.missions.ProgressiveMission;
import dev.huey.destroyTheCore.records.PlayerData;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NoJumpMission extends ProgressiveMission implements Listener {
  
  public NoJumpMission() {
    super("no-jump");
  }
  
  Map<Game.Side, Integer> quota = new HashMap<>();
  
  @Override
  public void innerStart() {
    for (Game.Side side : new Game.Side[]{Game.Side.RED, Game.Side.GREEN}) {
      quota.put(side, 100);
      progress(side, 1F);
    }
  }
  
  @EventHandler
  public void onPlayerJump(PlayerJumpEvent ev) {
    Player pl = ev.getPlayer();
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    if (data.side.equals(Game.Side.SPECTATOR)) return;
    
    quota.put(data.side, Math.max(quota.get(data.side) - 1, 0));
    progress(data.side, (float) Math.max(quota.get(data.side) / 100D, 0));
  }
  
  @Override
  public void tick() {
  }
  
  @Override
  public void innerFinish() {
    for (Game.Side side : new Game.Side[]{Game.Side.RED, Game.Side.GREEN}) {
      if (quota.get(side) > quota.get(side.opposite())) {
        declareWinner(side);
        return;
      }
    }
    
    declareDraw();
  }
}
