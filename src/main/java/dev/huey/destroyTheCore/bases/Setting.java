package dev.huey.destroyTheCore.bases;

import org.bukkit.configuration.ConfigurationSection;

public abstract class Setting {
  
  public String id;
  
  public Setting(String id) {
    this.id = id;
  }
  
  public abstract void load(ConfigurationSection section);
  
  public abstract void save(ConfigurationSection section);
}
