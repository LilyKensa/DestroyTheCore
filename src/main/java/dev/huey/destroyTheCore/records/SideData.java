package dev.huey.destroyTheCore.records;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.utils.TextUtils;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SideData {
  
  static public class ExtraDamage {
    public enum Reason {
      PHASE,
      LAST_DITCH,
      ROLE_HACKER
    }
    
    public Reason reason;
    public Player origin;
    public int ticks;
    
    public ExtraDamage(Reason reason, Player origin, int ticks) {
      this.reason = reason;
      this.origin = origin;
      this.ticks = ticks;
    }
  }
  
  static public class ImmuneChance {
    public enum Reason {
      ROLE_HACKER
    }
    
    public Reason reason;
    public Player origin;
    public double chance;
    public int ticks;
    
    public ImmuneChance(
      Reason reason, Player origin, double chance, int ticks
    ) {
      this.reason = reason;
      this.origin = origin;
      this.chance = chance;
      this.ticks = ticks;
    }
  }
  
  /** Constants */
  static public final int maxCoreHealth = 75;
  
  public int coreHealth = maxCoreHealth;
  public int invulnTicks = 0;
  public List<ExtraDamage> extraDamages = new ArrayList<>();
  public List<ImmuneChance> immuneChances = new ArrayList<>();
  
  public int noOresTicks = 0, maxNoOresTicks = 0;
  public int noShopTicks = 0, maxNoShopTicks = 0;
  
  public int clearInvCooldown = 0;
  public boolean usedTruce = false;
  
  public int missionsCompleted = 0;
  
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
    
    for (ExtraDamage ed : extraDamages) {
      directAttackCore();
      
      if (ed.origin == null || !ed.origin.isOnline()) continue;
      
      PlayerData d = DestroyTheCore.game.getPlayerData(ed.origin);
      if (!d.alive) continue;
      
      d.addRespawnTime(5);
    }
  }
  
  public boolean isInvuln() {
    return invulnTicks > 0;
  }
  
  public void addExtraDamage(
    ExtraDamage.Reason reason, Player origin, int ticks
  ) {
    extraDamages.add(new ExtraDamage(reason, origin, ticks));
  }
  
  public void addImmuneChance(
    ImmuneChance.Reason reason, Player origin, double chance, int ticks
  ) {
    immuneChances.add(new ImmuneChance(reason, origin, chance, ticks));
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
