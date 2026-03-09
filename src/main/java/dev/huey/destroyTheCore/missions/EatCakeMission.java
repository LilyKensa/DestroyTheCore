package dev.huey.destroyTheCore.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.missions.ProgressiveMission;
import dev.huey.destroyTheCore.records.PlayerData;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class EatCakeMission extends ProgressiveMission implements Listener {
  
  public EatCakeMission() {
    super("eat-cake");
  }
  
  Map<Game.Side, Integer> count = new HashMap<>();
  
  @Override
  public void innerStart() {
    count.put(Game.Side.RED, 0);
    count.put(Game.Side.GREEN, 0);
  }
  
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent ev) {
    Player pl = ev.getPlayer();
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    if (data.side.equals(Game.Side.SPECTATOR)) return;
    
    if (ev.getHand() != EquipmentSlot.HAND) return;
    if (ev.getAction() != Action.RIGHT_CLICK_BLOCK) return;
    if (ev.getClickedBlock().getType() != Material.CAKE) return;
    if (pl.getFoodLevel() >= 20) return;
    
    count.put(data.side, count.get(data.side) + 1);
    progress(data.side, (float) Math.min(count.get(data.side) / 30D, 1));
  }
  
  @Override
  public void tick() {
  }
  
  @Override
  public void innerFinish() {
    for (Game.Side side : new Game.Side[]{
      Game.Side.RED, Game.Side.GREEN
    }) {
      if (count.get(side) > count.get(side.opposite())) {
        declareWinner(side);
        return;
      }
    }
    
    declareDraw();
  }
}
