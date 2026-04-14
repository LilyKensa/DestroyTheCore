package dev.huey.destroyTheCore.bases.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.EnumMap;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class ProgressiveMission extends Mission implements Listener {
  
  EnumMap<Game.Side, BossBar> bars = new EnumMap<>(Game.Side.class);
  EnumMap<Game.Side, Float> values = new EnumMap<>(Game.Side.class);
  
  float displayRatio = 1;
  
  public ProgressiveMission(String id) {
    super(id);
  }
  
  @Override
  public void start() {
    bars.put(
      Game.Side.RED,
      BossBar.bossBar(
        TextUtils.$("mission.progressive.red-bar"),
        0F,
        BossBar.Color.RED,
        BossBar.Overlay.PROGRESS
      )
    );
    bars.put(
      Game.Side.GREEN,
      BossBar.bossBar(
        TextUtils.$("mission.progressive.green-bar"),
        0F,
        BossBar.Color.GREEN,
        BossBar.Overlay.PROGRESS
      )
    );
    
    for (Game.Side side : Game.bothSide) {
      values.put(side, 0f);
    }
    
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerData d = DestroyTheCore.game.getPlayerData(p);
      Game.Side firstSide = d.side.equals(
        Game.Side.SPECTATOR
      ) ? Game.Side.RED : d.side;
      bars.get(firstSide).addViewer(p);
      bars.get(firstSide.opposite()).addViewer(p);
    }
    
    innerStart();
  }
  
  public void refresh(Game.Side side) {
    bars.get(side).progress(values.get(side));
  }
  
  public void refresh() {
    for (Game.Side side : Game.bothSide) {
      refresh(side);
    }
  }
  
  public void progress(Game.Side side, float value) {
    if (!values.containsKey(side)) return;
    
    while (value * displayRatio > 1) {
      displayRatio /= 2;
    }
    
    values.put(side, value * displayRatio);
    
    refresh();
  }
  
  @Override
  public void finish() {
    for (Player p : Bukkit.getOnlinePlayers()) for (BossBar bar : bars.values())
      bar.removeViewer(
        p
      );
    
    innerFinish();
  }
  
  public abstract void innerStart();
  
  public abstract void tick();
  
  public abstract void innerFinish();
}
