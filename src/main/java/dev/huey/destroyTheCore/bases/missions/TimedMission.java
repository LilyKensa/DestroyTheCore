package dev.huey.destroyTheCore.bases.missions;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class TimedMission extends Mission implements Listener {
  int ticks, maxTicks;
  BossBar timeBar;
  
  public TimedMission(String id, int ticks) {
    super(id);
    this.maxTicks = ticks;
    this.ticks = ticks;
  }
  public TimedMission(String id) {
    this(id, 60 * 20);
  }
  
  public Component getTitle() {
    return TextUtils.$("missions.%s.bar".formatted(id), List.of(
      Placeholder.component("time", CoreUtils.formatTimeComp(
        Math.ceilDiv(ticks, 20),
        NamedTextColor.GOLD
      ))
    ));
  }
  
  @Override
  public void start() {
    cancelClock();
    
    timeBar = BossBar.bossBar(
      getTitle(),
      1F,
      BossBar.Color.YELLOW,
      BossBar.Overlay.PROGRESS
    );
    for (Player p : Bukkit.getOnlinePlayers())
      timeBar.addViewer(p);

    innerStart();
  }
  
  @Override
  public void tick() {
    if (DestroyTheCore.ticksManager.isSeconds()) {
      timeBar.name(getTitle());
      timeBar.progress(((float) ticks) / maxTicks);
    }
    
    innerTick();
    
    ticks--;
    if (ticks <= 0) {
      end();
    }
  }
  
  @Override
  public void finish() {
    for (Player p : Bukkit.getOnlinePlayers())
      timeBar.removeViewer(p);
    
    innerFinish();
  }
  
  public abstract void innerStart();
  public abstract void innerTick();
  public abstract void innerFinish();
}
