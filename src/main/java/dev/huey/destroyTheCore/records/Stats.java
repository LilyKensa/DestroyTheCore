package dev.huey.destroyTheCore.records;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Stats implements ConfigurationSerializable {
  
  public boolean nightVision = false;
  public int games = 0, wins = 0, kills = 0, deaths = 0, coreAttacks = 0,
    skills = 0, exp = 0, levels = 0;
  public Map<Material, Integer> ores = new HashMap<>();
  public char[] expBarAqua = new char[8];
  public char[] expBarGray = new char[8];
  public char[] expBar = "▇▇▇▇▇▇▇▇".toCharArray();

  public void addFromPlayerData(PlayerData data, boolean win) {
    games++;
    if (win) wins++;
    kills += data.kills;
    deaths += data.deaths;
    coreAttacks += data.coreAttacks;
    skills += data.skills;

    exp += data.exp + 5 * kills + 3 * coreAttacks;
    for (int value : ores.values()) {
      exp += value / 4;
    }
    
    if (exp >= 100 * levels + 500 && levels < 15) {
      exp -= 100 * levels + 500;
      levels++;
    }
    int barCount = exp / ((100 * levels + 500) / 8);
    expBarAqua = Arrays.copyOfRange(expBar, 0, barCount);
    expBarGray = Arrays.copyOfRange(expBar, 0, 8 - barCount);

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
    pusher.accept("skills", skills);
    pusher.accept("core-attacks", coreAttacks);
    pusher.accept("levels", levels);
    pusher.accept("exp", exp);
    
    Map<String, Integer> stringOres = new HashMap<>();
    for (Map.Entry<Material, Integer> entry : ores.entrySet()) {
      stringOres.put(entry.getKey().name(), entry.getValue());
    }
    pusher.accept("ores", stringOres);
    
    return map;
  }
  
  static public Stats deserialize(Map<String, Object> map) {
    Stats stats = new Stats();
    
    stats.nightVision = (boolean) map.getOrDefault("night-vision", false);
    stats.games = (int) map.getOrDefault("games", 0);
    stats.wins = (int) map.getOrDefault("wins", 0);
    stats.kills = (int) map.getOrDefault("kills", 0);
    stats.deaths = (int) map.getOrDefault("deaths", 0);
    stats.skills = (int) map.getOrDefault("skills", 0);
    stats.coreAttacks = (int) map.getOrDefault("core-attacks", 0);
    stats.levels = (int) map.getOrDefault("levels", 0);
    stats.exp = (int) map.getOrDefault("exp", 0);

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
