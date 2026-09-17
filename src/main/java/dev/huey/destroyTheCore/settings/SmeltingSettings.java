package dev.huey.destroyTheCore.settings;

import dev.huey.destroyTheCore.bases.Setting;
import dev.huey.destroyTheCore.managers.RecipesManager;
import org.bukkit.configuration.ConfigurationSection;

public class SmeltingSettings extends Setting {
  
  public SmeltingSettings() {
    super("smelting");
  }
  
  @Override
  public void load(ConfigurationSection section) {
    RecipesManager.smeltSpeedUp = section.getInt("speed-up");
    RecipesManager.smeltXpUp = section.getInt("xp-up");
    RecipesManager.smeltFuelTimeUp = section.getInt("fuel-time-up");
  }
  
  @Override
  public void save(ConfigurationSection section) {
    section.set("speed-up", RecipesManager.smeltSpeedUp);
    section.set("xp-up", RecipesManager.smeltXpUp);
    section.set("fuel-time-up", RecipesManager.smeltFuelTimeUp);
  }
}
