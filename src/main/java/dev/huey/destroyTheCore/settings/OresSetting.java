package dev.huey.destroyTheCore.settings;

import dev.huey.destroyTheCore.Constants;
import dev.huey.destroyTheCore.bases.Setting;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class OresSetting extends Setting {
  
  public OresSetting() {
    super("ores");
  }
  
  @Override
  public void load(ConfigurationSection section) {
    for (String name : section.getKeys(false)) {
      ConfigurationSection in = section.getConfigurationSection(name);
      Constants.OreData out = Constants.ores.get(Material.valueOf(name));
      if (in == null) continue;
      
      out.minXp = in.getInt("min-xp");
      out.maxXp = in.getInt("max-xp");
      out.cooldownSeconds = in.getInt("cooldown-seconds");
    }
  }
  
  @Override
  public void save(ConfigurationSection section) {
    for (Material type : Constants.ores.keySet()) {
      Constants.OreData in = Constants.ores.get(type);
      ConfigurationSection out = new YamlConfiguration();
      
      out.set("min-xp", in.minXp);
      out.set("max-xp", in.maxXp);
      out.set("cooldown-seconds", in.cooldownSeconds);
      
      section.set(type.name(), out);
    }
  }
}
