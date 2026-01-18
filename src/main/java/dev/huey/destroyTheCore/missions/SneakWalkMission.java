package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.missions.ProgressiveMission;
import dev.huey.destroyTheCore.records.PlayerData;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class SneakWalkMission extends ProgressiveMission implements Listener {
  
  public SneakWalkMission() {
    super("sneak-walk");
  }
  
  Map<Game.Side, Double> dist = new HashMap<>();
  
  @Override
  public void innerStart() {
    dist.put(Game.Side.RED, 0D);
    dist.put(Game.Side.GREEN, 0D);
  }
  
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent ev) {
    if (!ev.hasChangedPosition()) return;
    
    Player pl = ev.getPlayer();
    if (!pl.isSneaking()) return;
    
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    if (!data.isGaming()) return;
    
    dist.put(
      data.side,
      dist.get(data.side) + ev.getTo().clone().subtract(
        ev.getFrom().clone()).length()
    );
    progress(data.side, (float) Math.min(dist.get(data.side) / 200D, 1));
  }
  
  @Override
  public void tick() {
  }
  
  @Override
  public void innerFinish() {
    for (Game.Side side : new Game.Side[]{Game.Side.RED, Game.Side.GREEN}) {
      if (dist.get(side) > dist.get(side.opposite())) {
        declareWinner(side);
        return;
      }
    }
    
    declareDraw();
  }
}
