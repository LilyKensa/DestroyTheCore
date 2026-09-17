package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.bases.Setting;
import dev.huey.destroyTheCore.settings.OresSetting;
import dev.huey.destroyTheCore.settings.SmeltingSettings;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class SettingsManager {
  
  Map<String, Setting> settings = Stream.of(
    new OresSetting(),
    new SmeltingSettings()
  ).collect(Collectors.toMap(s -> s.id, s -> s));
  
  public void load(ConfigurationSection section) {
    for (Map.Entry<String, Setting> entry : settings.entrySet()) {
      String key = entry.getKey();
      Setting setting = entry.getValue();
      
      ConfigurationSection inner = section.getConfigurationSection(key);
      if (inner != null) {
        setting.load(inner);
      }
    }
  }
  
  public void save(ConfigurationSection section) {
    for (Map.Entry<String, Setting> entry : settings.entrySet()) {
      String key = entry.getKey();
      Setting setting = entry.getValue();
      
      ConfigurationSection inner = new YamlConfiguration();
      setting.save(inner);
      section.set(key, inner);
    }
  }
}
