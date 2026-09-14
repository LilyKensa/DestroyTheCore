package dev.huey.destroyTheCore.managers;

import dev.huey.destroyTheCore.Constants;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class SettingsManager {
  
  public void load(ConfigurationSection section) {
    ConfigurationSection oresSection = section.getConfigurationSection("ores");
    if (oresSection != null) {
      for (String name : oresSection.getKeys(false)) {
        ConfigurationSection in = oresSection.getConfigurationSection(name);
        Constants.OreData out = Constants.ores.get(Material.valueOf(name));
        if (in == null) continue;
        
        out.minXp = in.getInt("min-xp");
        out.maxXp = in.getInt("max-xp");
        out.cooldownSeconds = in.getInt("cooldown-seconds");
      }
    }
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
