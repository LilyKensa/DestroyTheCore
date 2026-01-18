package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.DestroyTheCore;
import dev.huey.destroyTheCore.Game;
import dev.huey.destroyTheCore.records.MaybeGen;
import dev.huey.destroyTheCore.records.Region;
import dev.huey.destroyTheCore.records.Stats;
import dev.huey.destroyTheCore.utils.CoreUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConfigManager {
  static public File getFile(String path) {
    return new File(DestroyTheCore.instance.getDataFolder(), path);
  }
  
  static public abstract class Config {
    String path;
    File file;
    YamlConfiguration config;
    
    public Config(String path) {
      this.path = path;
    }
    
    public void load() {
      file = getFile(path);
      
      if (file.exists()) {
        config = YamlConfiguration.loadConfiguration(file);
      }
      else {
        CoreUtils.log("Creating %s!".formatted(path));
        
        config = new YamlConfiguration();
        save();
      }
      
      read();
    }
    
    public void save() {
      write();
      
      try {
        config.save(file);
      }
      catch (IOException e) {
        CoreUtils.error("Cannot save %s!".formatted(path));
      }
    }
    
    public abstract void read();
    public abstract void write();
  }
  
  public Config config, stats, lobby, shops, map;
  
  public void init() {
    ConfigurationSerialization.registerClass(Region.class);
    ConfigurationSerialization.registerClass(Stats.class);
    ConfigurationSerialization.registerClass(MaybeGen.class);
    ConfigurationSerialization.registerClass(Game.LobbyLocs.class);
    ConfigurationSerialization.registerClass(Game.MapLocs.class);
    ConfigurationSerialization.registerClass(Game.Shop.class);
    
    config = new Config("config.yml") {
      @Override
      public void read() {
        DestroyTheCore.translationsManager.currentLocale = Locale.forLanguageTag(
          CoreUtils.def(config.getString("lang"), "en-us")
        );
        
        DestroyTheCore.worldsManager.mapName = config.getString("map");
        DestroyTheCore.worldsManager.cloneLive();
      }
      
      @Override
      public void write() {
        config.set(
          "lang",
          DestroyTheCore.translationsManager.currentLocale
            .toLanguageTag().toLowerCase()
        );
        config.set(
          "map",
          DestroyTheCore.worldsManager.mapName
        );
      }
    };
    stats = new Config("stats.yml") {
      @Override
      public void read() {
        ConfigurationSection statsSection = config.getConfigurationSection("stats");
        if (statsSection == null) return;
        
        Map<String, Object> values = statsSection.getValues(true);
        for (String key : values.keySet()) {
          DestroyTheCore.game.stats.put(
            UUID.fromString(key),
            (Stats) values.get(key)
          );
        }
      }
      
      @Override
      public void write() {
        config.set(
          "stats",
          DestroyTheCore.game.stats.entrySet().stream()
            .collect(Collectors.toMap(
              entry -> entry.getKey().toString(),
              Map.Entry::getValue
            ))
        );
      }
    };
    shops = new Config("shops.yml") {
      @Override
      public void read() {
        DestroyTheCore.game.shops = CoreUtils.listLoader(Game.Shop.class)
          .apply(config.get("shops"));
      }
      
      @Override
      public void write() {
        config.set("shops", DestroyTheCore.game.shops);
      }
    };
    lobby = new Config("lobby.yml") {
      @Override
      public void read() {
        DestroyTheCore.game.lobby = (Game.LobbyLocs) config.get("locations");
      }
      
      @Override
      public void write() {
        config.set("locations", DestroyTheCore.game.lobby);
      }
    };
    
    load();
  }
  
  public void load() {
    config.load();
    
    // Recreate as map changes
    map = new Config("maps/%s.yml".formatted(DestroyTheCore.worldsManager.mapName)) {
      @Override
      public void read() {
        DestroyTheCore.game.map = (Game.MapLocs) config.get("locations");
      }
      
      @Override
      public void write() {
        config.set("locations", DestroyTheCore.game.map);
      }
    };
    
    stats.load();
    shops.load();
    lobby.load();
    map.load();
    
    DestroyTheCore.toolsManager.refresh();
  }
  
  public void save() {
    config.save();
    stats.save();
    shops.save();
    lobby.save();
    map.save();
  }
}
