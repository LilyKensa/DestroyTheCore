package dev.huey.destroyTheCore.bases;

import dev.huey.destroyTheCore.DTC;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.managers.MissionsManager;
import dev.huey.destroyTheCore.missions.results.*;
import dev.huey.destroyTheCore.utils.*;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

public abstract class Mission implements Listener {
  /** Mission central location, assigned in {@link #init} */
  static public Location centerLoc;
  
  static public class Result {
    
    String id;
    boolean isForWinner;
    
    /** @param id Used for translation, all lowercase */
    public Result(String id, boolean isForWinner) {
      this.id = id;
      this.isForWinner = isForWinner;
    }
    
    /** Broadcast when mission is started */
    public void intro() {
      List<TagResolver> placeholders = new ArrayList<>();
      placeholders.add(
        Placeholder.component(
          "action",
          TextUtils.$("mission.result." + (isForWinner ? "win" : "lose"))
        )
      );
      placeholders.addAll(getExtraPlaceholers());
      
      broadcast(
        Component.join(
          JoinConfiguration.noSeparators(),
          TextUtils.$("mission.result.intro", placeholders),
          TextUtils.$("mission.results." + id, placeholders)
        )
      );
    }
    
    /** Broadcast when mission is ended */
    public void outro(Game.Side side) {
      List<TagResolver> placeholders = new ArrayList<>();
      placeholders.add(Placeholder.component("side", side.titleComp()));
      placeholders.add(
        Placeholder.component(
          "action",
          TextUtils.$("mission.result." + (isForWinner ? "win" : "lose"))
        )
      );
      placeholders.addAll(getExtraPlaceholers());
      
      broadcast(
        Component.join(
          JoinConfiguration.noSeparators(),
          TextUtils.$("mission.result.outro", placeholders),
          TextUtils.$("mission.results." + id, placeholders)
        )
      );
    }
    
    public void run(Game.Side side) {
      forWinner(side);
      forLoser(side.opposite());
    }
    
    public TagResolver getRandomPlayerPlaceholder(Player pl) {
      return Placeholder.component(
        "player",
        pl == null ? TextUtils.$("mission.result.random-player") : PlayerUtils
          .getName(pl)
      );
    }
    
    /** @implNote Optional */
    public List<TagResolver> getExtraPlaceholers() {
      return List.of();
    }
    
    /** @implNote Optional */
    public void forWinner(Game.Side side) {
    }
    
    /** @implNote Optional */
    public void forLoser(Game.Side side) {
    }
  }
  
  /** How long it waits until the mission is automatically ended */
  static public final int clockDuration = 60 * 20;
  
  /** Prefixed broadcast */
  static public void broadcast(Component comp) {
    DTC.missionsManager.broadcast(comp);
  }
  
  public boolean active = false;
  public BukkitTask clock;
  
  public String id;
  Result result;
  
  /** @param id Used for translation, all lowercase */
  public Mission(String id, boolean hasResult) {
    this.id = id;
  }
  
  /** Call this in constructor to add result */
  protected void addResult() {
    result = RandomUtils.pick(
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
  }
  
  public Mission(String id) {
    this(id, false);
  }
  
  public void init() {
    Bukkit.getServer().getPluginManager().registerEvents(
      this,
      DTC.instance
    );
    
    start();
    active = true;
    
    PlayerUtils.broadcast(Component.empty());
    broadcast(TextUtils.$("missions.%s.title".formatted(id)));
    broadcast(TextUtils.$("missions.%s.desc".formatted(id)));
    if (result != null) result.intro();
    PlayerUtils.broadcast(Component.empty());
    
    cancelClock();
    clock = Bukkit.getScheduler().runTaskLater(
      DTC.instance,
      this::end,
      clockDuration
    );
  }
  
  /** Used in {@link MissionsManager#onTick} */
  public void onTick() {
    if (active) tick();
  }
  
  /** Call this to end the mission */
  public void end() {
    active = false;
    cancelClock();
    finish();
    
    HandlerList.unregisterAll(this);
    
    CoreUtils.setTickOut(DTC.missionsManager::next);
  }
  
  /** Call this to announce draw */
  public void declareDraw() {
    broadcast(TextUtils.$("mission.draw"));
  }
  
  /** Call this to announce the winner */
  public void declareWinner(Game.Side side) {
    DTC.game.getSideData(side).missionsCompleted++;
    
    for (Player p : PlayerUtils.getTeammates(side)) {
      DTC.game.getPlayerData(p).addExtraExp(25);
    }
    
    if (result != null) result.run(side);
  }
  
  public void declareWinner(Player pl) {
    declareWinner(DTC.game.getPlayerData(pl).side);
  }
  
  /** Call this if you don't want the background clock */
  public void cancelClock() {
    if (clock != null) clock.cancel();
  }
  
  public abstract void start();
  
  public abstract void tick();
  
  public abstract void finish();
}
