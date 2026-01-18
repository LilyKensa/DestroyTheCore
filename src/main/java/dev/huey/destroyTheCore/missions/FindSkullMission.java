package dev.huey.destroyTheCore.missions;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.managers.ItemsManager;
import dev.huey.destroyTheCore.utils.CoreUtils;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.RandomUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

public class FindSkullMission extends TimedMission implements Listener {
  
  public static final NamespacedKey dataNamespace = new NamespacedKey(
    DestroyTheCore.instance,
    "find-skull-mission-item"
  );
  
  public static ItemStack getSkullItem(Player pl) {
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    item.editMeta(uncastedMeta -> {
      SkullMeta meta = (SkullMeta) uncastedMeta;
      
      meta.setOwningPlayer(pl);
      
      meta.getPersistentDataContainer().set(
        dataNamespace,
        PersistentDataType.STRING,
        UUID.randomUUID().toString()
      );
    });
    return item;
  }
  
  public static boolean isSkullItem(ItemStack item) {
    return (item != null && !item.isEmpty() && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(
      dataNamespace));
  }
  
  public static Location randomLocation(Location center, double radius) {
    double angle = RandomUtils.nextDouble() * 2 * Math.PI;
    double randomRadius = Math.sqrt(RandomUtils.nextDouble()) * radius;
    
    double x = center.getX() + randomRadius * Math.cos(angle);
    double y = center.getY();
    double z = center.getZ() + randomRadius * Math.sin(angle);
    
    return new Location(center.getWorld(), x, y, z);
  }
  
  public static void giveTreasure(Player pl) {
    ItemStack item = RandomUtils.pick(
      new ItemStack(Material.EMERALD, 8),
      new ItemStack(Material.GOLD_INGOT, 64),
      DestroyTheCore.itemsManager.gens.get(
        ItemsManager.ItemKey.GRENADE).getItem(6),
      new ItemStack(Material.TNT, 1),
      new ItemStack(Material.GOLDEN_APPLE, 8)
    );
    
    pl.give(item);
    
    broadcast(
      TextUtils.$(
        "missions.find-skull.complete",
        List.of(
          Placeholder.component("player", PlayerUtils.getName(pl)),
          Placeholder.component(
            "item",
            item.effectiveName().colorIfAbsent(
              item.getItemMeta().hasRarity() ? item.getItemMeta().getRarity().color() : NamedTextColor.WHITE
            )
          ),
          Placeholder.component("amount", Component.text(item.getAmount()))
        )
      )
    );
  }
  
  public FindSkullMission() {
    super("find-skull");
  }
  
  public List<UUID> skullEntities = new ArrayList<>();
  
  @Override
  public void innerStart() {
    for (Player p : PlayerUtils.allGaming()) {
      Location skullLoc = randomLocation(
        loc.clone().add(0, RandomUtils.range(20, 30), 0),
        30
      );
      
      Item itemEntity = skullLoc.getWorld().dropItem(skullLoc, getSkullItem(p));
      itemEntity.setOwner(p.getUniqueId());
      
      skullEntities.add(itemEntity.getUniqueId());
    }
  }
  
  @Override
  public void innerTick() {
  }
  
  @EventHandler
  public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent ev) {
    if (!ev.getFlyAtPlayer()) return;
    
    Player pl = ev.getPlayer();
    
    Item itemEntity = ev.getItem();
    ItemStack item = itemEntity.getItemStack();
    
    if (!isSkullItem(item)) return;
    
    CoreUtils.setTickOut(() -> pl.getInventory().remove(item));
    
    giveTreasure(pl);
  }
  
  @Override
  public void innerFinish() {
    for (UUID id : skullEntities) {
      Entity e = Bukkit.getEntity(id);
      if (e != null) {
        new ParticleBuilder(Particle.LARGE_SMOKE).allPlayers().location(
          e.getLocation()).extra(0).spawn();
        
        e.remove();
      }
    }
  }
}
