package dev.huey.destroyTheCore.settings;

import dev.huey.destroyTheCore.Constants;
import dev.huey.destroyTheCore.bases.Setting;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class OresSetting extends Setting {
  
  public OresSetting() {
    super("ores");
  }
  
  @Override
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
  
  @Override
  public ConfigurationSection generate() {
    return null;
  }
}
