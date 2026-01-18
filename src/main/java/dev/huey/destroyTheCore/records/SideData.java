package dev.huey.destroyTheCore.records;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class SideData {
  
  /** Constants */
  public static final int maxCoreHealth = 75;
  
  public int coreHealth = maxCoreHealth;
  public int invulnTicks = 0, clearInvCooldown = 0, extraDamageTicks = 0,
    noOresTicks = 0, maxNoOresTicks = 0,
    noShopTicks = 0, maxNoShopTicks = 0;
  public boolean usedTruce = false;
  
  public Inventory enderChest = Bukkit.createInventory(
    null,
    6 * 9,
    TextUtils.$("game.side.ender-chest").color(null)
  );
  public Map<Location, Set<UUID>> enderChestViewers = new HashMap<>();
  
  public void addEnderChestViewer(Location loc, Player pl) {
    if (!enderChestViewers.containsKey(loc)) enderChestViewers.put(
      loc,
      new HashSet<>()
    );
    
    enderChestViewers.get(loc).add(pl.getUniqueId());
  }
  
  public void removeEnderChestViewer(Location loc, Player pl) {
    if (!enderChestViewers.containsKey(loc)) return;
    
    enderChestViewers.get(loc).remove(pl.getUniqueId());
  }
  
  public Location getEnderChestViewer(Player pl) {
    for (Location loc : enderChestViewers.keySet()) {
      if (enderChestViewers.get(loc).contains(pl.getUniqueId())) return loc;
    }
    
    return null;
  }
  
  /** Remove specific amount of health, without modifiers */
  public void directAttackCore(int amount) {
    coreHealth -= amount;
    if (coreHealth < 0) coreHealth = 0;
  }
  
  public void directAttackCore() {
    directAttackCore(1);
  }
  
  /** Remove 1 core health, with modifiers */
  public void attackCore() {
    directAttackCore();
    if (DestroyTheCore.game.phase.isAfter(Game.Phase.DoubleDamage)) {
      directAttackCore();
    }
    if (extraDamageTicks > 0) {
      directAttackCore();
    }
  }
  
  public boolean isInvuln() {
    return invulnTicks > 0;
  }
  
  public void banOres(int ticks) {
    noOresTicks += ticks;
    maxNoOresTicks = Math.max(noOresTicks, maxNoOresTicks);
  }
  
  public void banShop(int ticks) {
    noShopTicks += ticks;
    maxNoShopTicks = Math.max(noShopTicks, maxNoShopTicks);
  }
}
