package dev.huey.destroyTheCore.managers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class DamageManager {
  
  static public class DamageRecord {
    
    static public class Damage {
      
      double amount = 0;
      Date time = new Date(0);
      
      public Damage() {
        refresh();
      }
      
      public Damage(double amount) {
        this();
        this.amount = amount;
      }
      
      public void refresh() {
        this.time = new Date();
      }
      
      public boolean isWithin(long seconds) {
        return (new Date().getTime() - time.getTime() < seconds * 1000);
      }
      
      public void add(double amount) {
        refresh();
        this.amount += amount;
      }
    }
    
    Map<UUID, Damage> damageMap = new HashMap<>();
    
    public void addDamage(Player pl, double amount) {
      if (Math.floor(amount) <= 0) return;
      
      UUID id = pl.getUniqueId();
      
      Damage damage = damageMap.getOrDefault(id, new Damage(0));
      damage.add(amount);
      damageMap.put(id, damage);
    }
    
    public UUID getMostDamage() {
      UUID most = null;
      double maxDamage = 0;
      for (Map.Entry<UUID, Damage> entry : damageMap.entrySet()) {
        if (!entry.getValue().isWithin(10)) continue;
        
        double damage = entry.getValue().amount;
        if (damage > maxDamage) {
          maxDamage = damage;
          most = entry.getKey();
        }
      }
      
      return most;
    }
  }
  
  Map<UUID, DamageRecord> records = new HashMap<>();
  
  public DamageRecord getRecord(Player victim) {
    UUID id = victim.getUniqueId();
    if (!records.containsKey(id)) records.put(id, new DamageRecord());
    return records.get(id);
  }
  
  public void addDamage(Player attacker, Player victim, double amount) {
    getRecord(victim).addDamage(attacker, amount);
  }
  
  public UUID getMostDamage(Player victim) {
    return getRecord(victim).getMostDamage();
  }
}
