package dev.huey.destroyTheCore.missions;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.bases.missions.ProgressiveMission;
import dev.huey.destroyTheCore.records.PlayerData;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.*;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class CollectStarsMission extends ProgressiveMission implements Listener {
  
  static public final int totalCount = 120;
  
  static public final NamespacedKey dataNamespace = new NamespacedKey(
    DestroyTheCore.instance,
    "collect-stars-mission-item"
  );
  
  static public ItemStack getStarItem() {
    ItemStack item = new ItemStack(Material.NETHER_STAR);
    item.editMeta(meta -> {
      meta.displayName(TextUtils.$("missions.collect-stars.item"));
      
      meta.getPersistentDataContainer().set(
        dataNamespace,
        PersistentDataType.STRING,
        UUID.randomUUID().toString()
      );
    });
    return item;
  }
  
  static public boolean isStarItem(ItemStack item) {
    return (item != null
      && !item.isEmpty()
      && item.hasItemMeta()
      && item.getItemMeta().getPersistentDataContainer().has(
        dataNamespace
      ));
  }
  
  int currentCount = 0;
  
  public CollectStarsMission() {
    super("collect-stars");
  }
  
  static public Location randomLocation(Location center, double radius) {
    double angle = RandomUtils.nextDouble() * 2 * Math.PI;
    double randomRadius = Math.sqrt(RandomUtils.nextDouble()) * radius;
    
    double x = center.getX() + randomRadius * Math.cos(angle);
    double y = center.getY();
    double z = center.getZ() + randomRadius * Math.sin(angle);
    
    return new Location(center.getWorld(), x, y, z);
  }
  
  List<UUID> starEntities = new ArrayList<>();
  Map<Game.Side, Integer> counts = new HashMap<>();
  
  @Override
  public void innerStart() {
    counts.put(Game.Side.RED, 0);
    counts.put(Game.Side.GREEN, 0);
  }
  
  @Override
  public void tick() {
    if (DestroyTheCore.ticksManager.isUpdateTick()) {
      if (currentCount < totalCount) {
        Location starLoc = randomLocation(
          centerLoc.clone().add(0, RandomUtils.range(20, 30), 0),
          30
        );
        Item itemEntity = starLoc.getWorld().dropItem(starLoc, getStarItem());
        starEntities.add(itemEntity.getUniqueId());
        
        currentCount++;
      }
    }
  }
  
  @EventHandler
  public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent ev) {
    Player pl = ev.getPlayer();
    PlayerData data = DestroyTheCore.game.getPlayerData(pl);
    
    Item itemEntity = ev.getItem();
    ItemStack item = itemEntity.getItemStack();
    
    if (!isStarItem(item)) return;
    
    CoreUtils.setTickOut(() -> pl.getInventory().remove(item));
    
    counts.put(data.side, counts.getOrDefault(data.side, 0) + 1);
    progress(
      data.side,
      (float) Math.min(2D * counts.get(data.side) / totalCount, 1)
    );
    
    broadcast(
      TextUtils.$(
        "missions.collect-stars.score",
        List.of(Placeholder.component("player", PlayerUtils.getName(pl)))
      )
    );
  }
  
  @Override
  public void innerFinish() {
    for (UUID id : starEntities) {
      Entity e = Bukkit.getEntity(id);
      if (e != null) {
        new ParticleBuilder(Particle.LARGE_SMOKE)
          .allPlayers()
          .location(e.getLocation())
          .extra(0)
          .spawn();
        
        e.remove();
      }
    }
    
    for (Game.Side side : new Game.Side[]{
      Game.Side.RED, Game.Side.GREEN
    }) {
      if (
        counts.getOrDefault(side, 0) > counts.getOrDefault(side.opposite(), 0)
      ) {
        declareWinner(side);
        return;
      }
    }
    
    declareDraw();
  }
}
