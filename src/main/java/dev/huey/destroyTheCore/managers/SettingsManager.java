package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.Constants;
import dev.huey.destroyTheCore.bases.Setting;
import dev.huey.destroyTheCore.settings.OresSetting;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class SettingsManager {
  
  Map<String, Setting> settings = Stream.of(
    new OresSetting()
  ).collect(Collectors.toMap(s -> s.id, s -> s));
  
  public void load(ConfigurationSection section) {
    
  }
  
  public ConfigurationSection generateConfigSection() {
    ConfigurationSection section = new YamlConfiguration();
    
    ConfigurationSection oresSection = new YamlConfiguration();
    for (Material type : Constants.ores.keySet()) {
      Constants.OreData in = Constants.ores.get(type);
      ConfigurationSection out = new YamlConfiguration();
      
      out.set("min-xp", in.minXp);
      out.set("max-xp", in.maxXp);
      out.set("cooldown-seconds", in.cooldownSeconds);
      
      oresSection.set(type.name(), out);
    }
    section.set("ores", oresSection);
    
    return section;
  }
}
