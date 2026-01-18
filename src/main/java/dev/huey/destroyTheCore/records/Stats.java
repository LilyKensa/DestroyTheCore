package dev.huey.destroyTheCore.records;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Stats implements ConfigurationSerializable {
  
  public boolean nightVision = false;
  public int games = 0, wins = 0, kills = 0, deaths = 0, coreAttacks = 0;
  public Map<Material, Integer> ores = new HashMap<>();
  
  public void addFromPlayerData(PlayerData data, boolean win) {
    games++;
    if (win) wins++;
    kills += data.kills;
    deaths += data.deaths;
    coreAttacks += data.coreAttacks;
    
    for (Material type : data.ores.keySet()) ores.put(
      type,
      ores.getOrDefault(type, 0) + data.ores.getOrDefault(type, 0)
    );
  }
  
  public void addFromPlayerData(PlayerData data) {
    addFromPlayerData(data, false);
  }
  
  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> map = new HashMap<>();
    
    BiConsumer<String, Object> pusher = (key, value) -> {
      if (value != null) map.put(key, value);
    };
    
    pusher.accept("night-vision", nightVision);
    pusher.accept("games", games);
    pusher.accept("wins", wins);
    pusher.accept("kills", kills);
    pusher.accept("deaths", deaths);
    pusher.accept("core-attacks", coreAttacks);
    
    Map<String, Integer> stringOres = new HashMap<>();
    for (Map.Entry<Material, Integer> entry : ores.entrySet()) {
      stringOres.put(entry.getKey().name(), entry.getValue());
    }
    pusher.accept("ores", stringOres);
    
    return map;
  }
  
  public static Stats deserialize(Map<String, Object> map) {
    Stats stats = new Stats();
    
    stats.nightVision = (boolean) map.getOrDefault("night-vision", false);
    stats.games = (int) map.getOrDefault("games", 0);
    stats.wins = (int) map.getOrDefault("wins", 0);
    stats.kills = (int) map.getOrDefault("kills", 0);
    stats.deaths = (int) map.getOrDefault("deaths", 0);
    stats.coreAttacks = (int) map.getOrDefault("core-attacks", 0);
    
    Map<String, Integer> stringOres = (Map<String, Integer>) map.getOrDefault(
      "ores",
      new HashMap<>()
    );
    stats.ores = new HashMap<>();
    for (Map.Entry<String, Integer> entry : stringOres.entrySet()) {
      stats.ores.put(Material.valueOf(entry.getKey()), entry.getValue());
    }
    
    return stats;
  }
}
