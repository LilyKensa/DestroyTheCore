package dev.huey.destroyTheCore.missions;

import com.destroystokyo.paper.ParticleBuilder;
import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.bases.missions.TimedMission;
import dev.huey.destroyTheCore.managers.TicksManager;
import dev.huey.destroyTheCore.utils.PlayerUtils;
import dev.huey.destroyTheCore.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CursedItemMission extends TimedMission implements Listener {
  
  static public final NamespacedKey dataNamespace = new NamespacedKey(
    DestroyTheCore.instance,
    "cursed-item-mission-item"
  );
  
  static Item itemEntity;
  
  static public void setItemEntity(Item entity) {
    DestroyTheCore.missionsManager.team.addEntity(entity);
    entity.setGlowing(true);
    
    itemEntity = entity;
  }
  
  static public ItemStack getItem() {
    ItemStack item = new ItemStack(Material.RABBIT_FOOT);
    
    item.editMeta(meta -> {
      meta.displayName(TextUtils.$("missions.cursed-item.item"));
      
      meta.getPersistentDataContainer().set(
        dataNamespace,
        PersistentDataType.BOOLEAN,
        true
      );
    });
    
    return item;
  }
  
  static public void dropItem(Location there) {
    setItemEntity(there.getWorld().dropItem(there, getItem()));
  }
  
  static public boolean isItem(ItemStack item) {
    if (item == null || item.isEmpty()) return false;
    if (!item.hasItemMeta()) return false;
    
    return item.getItemMeta().getPersistentDataContainer().has(dataNamespace);
  }
  
  static public boolean hasItem(Player pl) {
    for (ItemStack item : pl.getInventory().getContents()) {
      if (isItem(item)) return true;
    }
    
    return false;
  }
  
  public CursedItemMission() {
    super("cursed-item");
  }
  
  @Override
  public void innerStart() {
    dropItem(centerLoc);
  }
  
  final int maxHolding = 10 * 20 / TicksManager.updateRate;
  int holding = 0;
  
  @EventHandler
  public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent ev) {
    if (isItem(ev.getItem().getItemStack())) {
      holding = 0;
    }
  }
  
  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent ev) {
    Item dropped = ev.getItemDrop();
    
    if (isItem(dropped.getItemStack())) {
      setItemEntity(dropped);
    }
  }
  
  @Override
  public void innerTick() {
    if (DestroyTheCore.ticksManager.isUpdateTick()) {
      for (Player p : PlayerUtils.allGaming()) {
        if (!hasItem(p)) continue;
        
        p.addPotionEffect(
          new PotionEffect(PotionEffectType.SPEED, 20, 2, true, false)
        );
        p.addPotionEffect(
          new PotionEffect(PotionEffectType.STRENGTH, 20, 0, true, false)
        );
        
        holding++;
        
        TextComponent.Builder comp = Component.text();
        comp.append(Component.text("["));
        for (int i = 0; i < maxHolding; ++i) comp.append(
          Component.text("■").color(
            i <= holding ? NamedTextColor.RED : NamedTextColor.DARK_GRAY
          )
        );
        comp.append(Component.text("]"));
        p.sendActionBar(comp.colorIfAbsent(NamedTextColor.GRAY));
        
        if (holding >= maxHolding) {
          p.getInventory().remove(Material.RABBIT_FOOT);
          p.damage(
            Double.MAX_VALUE,
            DamageSource.builder(DamageType.MAGIC).build()
          );
          
          holding = 0;
          dropItem(p.getLocation());
        }
      }
    }
  }
  
  @Override
  public void innerFinish() {
    if (itemEntity != null && !itemEntity.isDead()) {
      new ParticleBuilder(Particle.LARGE_SMOKE)
        .allPlayers()
        .location(itemEntity.getLocation())
        .extra(0)
        .spawn();
      
      itemEntity.remove();
    }
    
    for (Player p : PlayerUtils.allGaming()) {
      p.getInventory().remove(Material.RABBIT_FOOT);
    }
  }
}
