package dev.huey.destroyTheCore.bases;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.missions.results.*;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.LocationUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public abstract class Mission implements Listener {
  public static Location loc;
  
  public static class Result {
    String id;
    
    public Result(String id) {
      this.id = id;
    }
    
    public void announce(Game.Side side, List<TagResolver> places) {
      List<TagResolver> placeholders = new ArrayList<>();
      placeholders.add(Placeholder.component("side", side.titleComp()));
      placeholders.addAll(places);
      
      broadcast(TextUtils.$("mission.results." + id, placeholders));
    }
    public void announce(Game.Side side) {
      announce(side, List.of());
    }
    
    public void run(Game.Side side) {
      forWinner(side);
      forLoser(side.opposite());
    }
    
    public void forWinner(Game.Side side) {
    
    }
    
    public void forLoser(Game.Side side) {
    
    }
  }
  
  public static List<Result> results;
  
  static public void broadcast(Component comp) {
    DestroyTheCore.missionsManager.broadcast(comp);
  }
  
  public boolean active = false;
  public BukkitTask clock;
  
  public String id;
  
  public Mission(String id) {
    this.id = id;
  }
  
  public void init() {
    loc = LocationUtils.live(DestroyTheCore.game.map.mission);
    
    results = List.of(
      new ClearXpResult(),
      new GiveXpResult(),
      new BadEffectResult(),
      new GoodEffectResult(),
      new AddRespawnTimeResult(),
      new RemoveRespawnTimeResult(),
      new BanOresResult(),
      new BanShopResult(),
      new AttackCoreResult(),
      new SummonPigResult(),
      new VoidResult(),
      new GiveEmeraldResult(),
      new LightningResult(),
      new TNTResult(),
      new SkillCooldownResult()
    );
    
    Bukkit.getServer().getPluginManager().registerEvents(this, DestroyTheCore.instance);
    
    start();
    active = true;
    
    broadcast(TextUtils.$("missions.%s.title".formatted(id)));
    broadcast(TextUtils.$("missions.%s.desc".formatted(id)));
    
    cancelClock();
    clock = Bukkit.getScheduler().runTaskLater(
      DestroyTheCore.instance,
      this::end,
      60 * 20
    );
  }
  
  public void onTick() {
    if (active)
      tick();
  }
  
  public void end() {
    active = false;
    cancelClock();
    finish();
    
    HandlerList.unregisterAll(this);
    
    CoreUtils.setTickOut(DestroyTheCore.missionsManager::next);
  }
  
  public void declareDraw() {
    broadcast(TextUtils.$("mission.draw"));
  }
  
  public void declareWinner(Game.Side side) {
    RandomUtils.pick(results).run(side);
  }
  public void declareWinner(Player pl) {
    declareWinner(DestroyTheCore.game.getPlayerData(pl).side);
  }
  
  public void cancelClock() {
    if (clock != null) clock.cancel();
  }
  
  public abstract void start();
  public abstract void tick();
  public abstract void finish();
}
