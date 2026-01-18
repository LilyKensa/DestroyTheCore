package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.Mission;
import dev.huey.destroyTheCore.missions.*;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class MissionsManager {
  
  public static Component prefix;
  public static final int waitingTicks = 60 * 20;
  
  public boolean active = false;
  public Mission mission;
  
  BossBar waitingBar;
  public Team team;
  
  public void broadcast(Component comp) {
    PlayerUtils.broadcast(prefix.append(comp));
  }
  
  public void start() {
    prefix = TextUtils.$("mission.prefix");
    
    Scoreboard board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
    team = board.registerNewTeam("mission");
    team.color(NamedTextColor.YELLOW);
    team.prefix(
      Component.text(PlainTextComponentSerializer.plainText().serialize(prefix))
    );
    
    restart();
    active = true;
  }
  
  public void restart() {
    if (waitingBar != null) {
      for (Player p : Bukkit.getOnlinePlayers()) waitingBar.removeViewer(p);
    }
    
    mission = RandomUtils.pick(
      new ChickenMission(),
      new RollCallMission(),
      new CollectStarsMission(),
      new OccupyCenterMission(),
      new OresMission(),
      new NoJumpMission(),
      new TeleportCoreMission(),
      new HungryMission(),
      new EarthquakeCowMission(),
      new NextDropAllMission(),
      new JumpMission(),
      new EatCakeMission(),
      new DiscountTraderMission(),
      new FreeMoneyMission(),
      new DropOresMission(),
      new AntiGravityMission(),
      new XpFountainMission(),
      new AngryBeesMission(),
      new SwapPosMission(),
      new SwapAllPosMission(),
      new CovidMission(),
      new RandomRoleMission(),
      new FreeSoupMission(),
      new HeroMission(),
      new SneakWalkMission(),
      new InvisMission(),
      new CargoMission(),
      new ExplodeMission(),
      new InfiniteOresMission(),
      new RocketMission(),
      new ColdMission(),
      new CursedItemMission(),
      new GoOutsideMission(),
      new FindSkullMission()
    );
    mission.init();
  }
  
  /** Check if missions should continue, then restart */
  public void next() {
    if (!active) return;
    if (!DestroyTheCore.game.isPlaying) return;
    if (DestroyTheCore.game.phaseTimer <= waitingTicks) return;
    
    waitingBar = BossBar.bossBar(
      TextUtils.$("mission.waiting-title"),
      1F,
      BossBar.Color.WHITE,
      BossBar.Overlay.PROGRESS
    );
    for (Player p : Bukkit.getOnlinePlayers()) waitingBar.addViewer(p);
    
    final int step = 20;
    new BukkitRunnable() {
      final int maxDuration = waitingTicks / step;
      int duration = maxDuration;
      
      @Override
      public void run() {
        waitingBar.progress(1F * duration / maxDuration);
        
        duration--;
        if (duration <= 0) {
          restart();
          
          cancel();
          return;
        }
      }
    }.runTaskTimer(DestroyTheCore.instance, 0, step);
  }
  
  public void forceStop() {
    if (mission != null && mission.active) {
      mission.end();
    }
    stop();
  }
  
  public void stop() {
    active = false;
  }
  
  public void onTick() {
    if (mission != null && mission.active) {
      mission.onTick();
    }
  }
}
